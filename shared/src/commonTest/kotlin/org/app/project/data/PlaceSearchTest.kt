package org.app.project.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PlaceSearchTest {

    private val citiesFile = """
        #CH
        Zürich	Zurich	Zuerich
        Geneva
        #IN
        Mumbai
        Delhi
        Bengaluru
        New Delhi
        #US
        New York City
    """.trimIndent() + "\n"

    private val countries = parseCountries("IN\tIndia\nIO\tBritish Indian Ocean Territory\nID\tIndonesia\nFI\tFinland\n")

    // ---- reading the bundled files ----

    @Test
    fun parsesCountryLines() {
        assertEquals(Country("IN", "India"), countries.first())
        assertEquals(4, countries.size)
    }

    @Test
    fun parsesOnlyTheRequestedCountrysCities() {
        assertEquals(listOf("Mumbai", "Delhi", "Bengaluru", "New Delhi"), parseCitiesOf(citiesFile, "IN").map { it.name })
        assertEquals(listOf("Zürich", "Geneva"), parseCitiesOf(citiesFile, "CH").map { it.name })   // first block
        assertEquals(listOf("New York City"), parseCitiesOf(citiesFile, "US").map { it.name })       // last block
        assertTrue(parseCitiesOf(citiesFile, "FR").isEmpty())
    }

    @Test
    fun keepsOtherSpellingsAsSearchKeys() {
        val zurich = parseCitiesOf(citiesFile, "CH").first()
        assertEquals(listOf("zürich", "zurich", "zuerich"), zurich.searchKeys)
    }

    // ---- country suggestions ----

    @Test
    fun nothingTypedOffersEveryCountry() {
        assertEquals(countries, suggestCountries("  ", countries))
    }

    @Test
    fun countriesStartingWithTheTextComeBeforeOnesContainingIt() {
        assertEquals(
            listOf("India", "Indonesia", "British Indian Ocean Territory", "Finland"),
            suggestCountries("in", countries).map { it.name }
        )
    }

    @Test
    fun countryWithNoMatchGivesNothing() {
        assertTrue(suggestCountries("zzz", countries).isEmpty())
    }

    // ---- city suggestions ----

    private val india = CityChoices(
        history = listOf("Hampi", "Delhi"),
        catalog = parseCitiesOf(citiesFile, "IN")
    )

    @Test
    fun nothingTypedOffersHistoryThenBiggestCities() {
        assertEquals(
            listOf("Hampi", "Delhi", "Mumbai", "Bengaluru", "New Delhi"),
            suggestCities("", india).map { it.name }
        )
    }

    @Test
    fun loggedCitiesComeFirstAndAreNotRepeated() {
        val suggestions = suggestCities("del", india)
        assertEquals(listOf("Delhi", "New Delhi"), suggestions.map { it.name })
        assertEquals(listOf(true, false), suggestions.map { it.fromHistory })
    }

    @Test
    fun findsCitiesByAnySpelling() {
        val swiss = CityChoices(history = emptyList(), catalog = parseCitiesOf(citiesFile, "CH"))
        assertEquals(listOf("Zürich"), suggestCities("zuri", swiss).map { it.name })
        assertEquals(listOf("Zürich"), suggestCities("Zür", swiss).map { it.name })
    }

    @Test
    fun respectsTheLimit() {
        assertEquals(2, suggestCities("", india, limit = 2).size)
    }
}
