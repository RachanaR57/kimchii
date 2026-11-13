package org.app.project.data

/** Everything the city field can suggest once a country is chosen. */
data class CityChoices(
    /** Cities already logged in that country, most recent first. */
    val history: List<String>,
    /** The bundled cities of that country, biggest first. */
    val catalog: List<CityOption>
) {
    companion object {
        val Empty = CityChoices(history = emptyList(), catalog = emptyList())
    }
}

data class CitySuggestion(val name: String, val fromHistory: Boolean)

/**
 * How well [key] answers what was typed. Lower is better, null means no match.
 *   0  starts with it            "ban"  -> "Bangalore"
 *   1  a later word starts with it   "york" -> "New York City"
 *   2  it appears somewhere      "lor"  -> "Bangalore"
 */
private fun matchRank(key: String, typed: String): Int? = when {
    key.startsWith(typed) -> 0
    key.contains(" $typed") || key.contains("-$typed") -> 1
    key.contains(typed) -> 2
    else -> null
}

/** Countries matching what was typed, best match first. Nothing typed returns every country. */
fun suggestCountries(typed: String, countries: List<Country>): List<Country> {
    val query = typed.trim().lowercase()
    if (query.isEmpty()) return countries
    return countries
        .mapNotNull { country -> matchRank(country.name.lowercase(), query)?.let { rank -> rank to country } }
        .sortedBy { (rank, _) -> rank }   // stable sort: alphabetical order is kept within a rank
        .map { (_, country) -> country }
}

/**
 * Cities to offer for what was typed: places already logged come first, then the bundled list.
 * With nothing typed it offers the history followed by the country's biggest cities.
 */
fun suggestCities(typed: String, choices: CityChoices, limit: Int = 8): List<CitySuggestion> {
    val query = typed.trim().lowercase()

    val fromHistory = choices.history
        .mapNotNull { name -> matchRank(name.lowercase(), query)?.let { rank -> rank to name } }
        .sortedBy { (rank, _) -> rank }
        .map { (_, name) -> CitySuggestion(name, fromHistory = true) }

    val fromCatalog = choices.catalog
        .mapNotNull { city ->
            // a city matches if any of its spellings does; keep its best rank
            city.searchKeys.mapNotNull { key -> matchRank(key, query) }.minOrNull()?.let { rank -> rank to city }
        }
        .sortedBy { (rank, _) -> rank }   // stable sort: biggest cities stay first within a rank
        .map { (_, city) -> CitySuggestion(city.name, fromHistory = false) }

    return (fromHistory + fromCatalog)
        .distinctBy { it.name.lowercase() }   // a logged city that is also in the list shows once
        .take(limit)
}
