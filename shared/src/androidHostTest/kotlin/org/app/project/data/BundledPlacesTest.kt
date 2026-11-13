package org.app.project.data

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** Checks the real files that ship in the app, not hand-written samples. */
class BundledPlacesTest {

    // Gradle runs unit tests from the module folder (shared/).
    private val filesDir = File("src/commonMain/composeResources/files")
    private val countries = parseCountries(File(filesDir, "countries.txt").readText())
    private val citiesText = File(filesDir, "cities.txt").readText()

    @Test
    fun countryListLooksRight() {
        assertTrue(countries.size in 240..260, "unexpected country count: ${countries.size}")
        assertEquals(countries.size, countries.map { it.code }.toSet().size, "duplicate country codes")
        assertEquals(countries.size, countries.map { it.name.lowercase() }.toSet().size, "duplicate country names")
        assertNotNull(countries.find { it.code == "IN" && it.name == "India" })
        assertNotNull(countries.find { it.code == "NL" && it.name == "Netherlands" })
    }

    @Test
    fun everyCityBlockBelongsToAListedCountry() {
        val codes = countries.map { it.code }.toSet()
        val blockCodes = citiesText.lineSequence().filter { it.startsWith("#") }.map { it.drop(1) }.toList()
        assertTrue(blockCodes.isNotEmpty())
        assertTrue(codes.containsAll(blockCodes), "city blocks without a country: ${blockCodes - codes}")
    }

    @Test
    fun biggestCitiesComeFirst() {
        assertEquals("Mumbai", parseCitiesOf(citiesText, "IN").first().name)
        assertEquals("Tokyo", parseCitiesOf(citiesText, "JP").first().name)
    }

    @Test
    fun accentedCitiesAreFoundByPlainSpelling() {
        val swiss = CityChoices(history = emptyList(), catalog = parseCitiesOf(citiesText, "CH"))
        assertEquals("Zürich", suggestCities("zurich", swiss).first().name)
    }
}
