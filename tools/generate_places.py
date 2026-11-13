#!/usr/bin/env python3
"""Builds the country and city lists that the app bundles for its dropdowns.

Input (from https://download.geonames.org/export/dump/, licensed CC BY 4.0):
  cities15000.txt   every city with more than 15,000 people (unzip cities15000.zip)
  countryInfo.txt   one row per country

Output (into shared/src/commonMain/composeResources/files/):
  countries.txt     one line per country:  CODE<tab>Name          sorted by name
  cities.txt        "#CODE" starts a country, then one city per line, biggest first.
                    A line is  Name  optionally followed by tab-separated other
                    spellings, so that typing "Zurich" still finds "Zürich".

Usage:
  python3 tools/generate_places.py path/to/cities15000.txt path/to/countryInfo.txt
"""

import sys
import unicodedata
from collections import defaultdict
from pathlib import Path

OUT_DIR = Path(__file__).resolve().parent.parent / "shared/src/commonMain/composeResources/files"

# GeoNames column positions (tab separated).
CITY_NAME, CITY_ASCII, CITY_COUNTRY, CITY_POPULATION = 1, 2, 8, 14
COUNTRY_CODE, COUNTRY_NAME = 0, 4

# Nicer display names than the ones GeoNames ships.
COUNTRY_RENAMES = {"NL": "Netherlands"}

# Countries GeoNames still lists although they no longer exist.
COUNTRY_SKIP = {"AN", "CS"}  # Netherlands Antilles, Serbia and Montenegro

# Letters used by scholarly transliteration (e.g. "Zamānia"). Nobody types or reads
# city names that way, so those names fall back to their plain-ASCII spelling.
# Ordinary accents (é, ü, ã, ø, ł ...) are kept.
TRANSLITERATION_MARKS = set("āēīōūĀĒĪŌŪḍḌḥḤḷḶṃṁṀṂṅṄṇṆṛṚṣṢṭṬẕẔʻʼ‘’")


def display_name(name: str, ascii_name: str) -> str:
    if ascii_name and any(ch in TRANSLITERATION_MARKS for ch in name):
        return ascii_name
    return name


def strip_accents(text: str) -> str:
    """ "Zürich" -> "Zurich", "São Paulo" -> "Sao Paulo". """
    decomposed = unicodedata.normalize("NFKD", text)
    return "".join(ch for ch in decomposed if not unicodedata.combining(ch))


def main() -> None:
    if len(sys.argv) != 3:
        sys.exit(__doc__)
    cities_path, countries_path = Path(sys.argv[1]), Path(sys.argv[2])

    countries = {}
    for line in countries_path.read_text(encoding="utf-8").splitlines():
        if not line.strip() or line.startswith("#"):
            continue
        cols = line.split("\t")
        code = cols[COUNTRY_CODE].strip()
        if code in COUNTRY_SKIP:
            continue
        countries[code] = COUNTRY_RENAMES.get(code, cols[COUNTRY_NAME].strip())

    cities_by_country = defaultdict(list)
    for line in cities_path.read_text(encoding="utf-8").splitlines():
        cols = line.split("\t")
        code = cols[CITY_COUNTRY]
        if code not in countries:
            continue
        ascii_name = cols[CITY_ASCII].strip()
        name = display_name(cols[CITY_NAME].strip(), ascii_name)
        if not name or "\t" in name or name.startswith("#"):
            continue
        cities_by_country[code].append((int(cols[CITY_POPULATION] or 0), name, ascii_name))

    OUT_DIR.mkdir(parents=True, exist_ok=True)

    country_lines = [f"{code}\t{name}" for code, name in sorted(countries.items(), key=lambda c: c[1].lower())]
    (OUT_DIR / "countries.txt").write_text("\n".join(country_lines) + "\n", encoding="utf-8")

    city_lines, city_count = [], 0
    for code in sorted(cities_by_country):
        city_lines.append(f"#{code}")
        seen = set()
        # biggest first, so the best-known place wins when two share a name
        for _, name, ascii_name in sorted(cities_by_country[code], key=lambda c: (-c[0], c[1])):
            if name.lower() in seen:
                continue
            seen.add(name.lower())
            # other spellings someone might type: accents stripped ("Zurich"), and
            # GeoNames' own ASCII form when that is different again ("Zuerich")
            spellings = []
            for alt in (strip_accents(name), ascii_name):
                if alt and alt != name and alt not in spellings:
                    spellings.append(alt)
            city_lines.append("\t".join([name] + spellings))
            city_count += 1
    (OUT_DIR / "cities.txt").write_text("\n".join(city_lines) + "\n", encoding="utf-8")

    print(f"{len(countries)} countries, {city_count} cities in {len(cities_by_country)} countries -> {OUT_DIR}")


if __name__ == "__main__":
    main()
