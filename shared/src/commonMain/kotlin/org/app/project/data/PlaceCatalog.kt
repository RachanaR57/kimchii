package org.app.project.data

import kimchii.shared.generated.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

data class Country(val code: String, val name: String)

/**
 * A city from the bundled list.
 * [searchKeys] are all the lowercase spellings it can be found by, e.g. "zürich", "zurich", "zuerich".
 */
data class CityOption(val name: String, val searchKeys: List<String>)

/**
 * The countries and cities shipped inside the app (see tools/generate_places.py).
 * Everything is read from two text files, so it works with no network.
 */
class PlaceCatalog(
    private val readFile: suspend (path: String) -> String = { Res.readBytes(it).decodeToString() }
) {
    // The files are read once, the first time they are needed, and kept in memory.
    private val mutex = Mutex()
    private var countries: List<Country>? = null
    private var citiesText: String? = null

    suspend fun countries(): List<Country> = mutex.withLock {
        countries ?: withContext(Dispatchers.Default) {
            parseCountries(readFile("files/countries.txt"))
        }.also { countries = it }
    }

    /** Cities of one country, biggest first. Empty if the list has none for it. */
    suspend fun cities(countryCode: String): List<CityOption> {
        val text = mutex.withLock {
            citiesText ?: readFile("files/cities.txt").also { citiesText = it }
        }
        return withContext(Dispatchers.Default) { parseCitiesOf(text, countryCode) }
    }
}

/** Each line is `CODE<tab>Name`. */
internal fun parseCountries(text: String): List<Country> =
    text.lineSequence()
        .mapNotNull { line ->
            val parts = line.split('\t')
            if (parts.size == 2 && parts[0].isNotBlank()) Country(parts[0], parts[1].trim()) else null
        }
        .toList()

/**
 * The cities file is one long list: a `#CODE` line starts a country and the lines after it are
 * that country's cities (`Name`, optionally followed by tab-separated other spellings).
 * Only the block for [countryCode] is turned into objects, so picking a country stays cheap.
 */
internal fun parseCitiesOf(text: String, countryCode: String): List<CityOption> {
    val header = "#$countryCode\n"
    val start = when {
        text.startsWith(header) -> header.length
        else -> text.indexOf("\n$header").takeIf { it >= 0 }?.plus(header.length + 1) ?: return emptyList()
    }
    val end = text.indexOf("\n#", start).takeIf { it >= 0 } ?: text.length

    return text.substring(start, end)
        .lineSequence()
        .filter { it.isNotBlank() }
        .map { line ->
            val spellings = line.split('\t')
            CityOption(name = spellings.first(), searchKeys = spellings.map { it.lowercase() })
        }
        .toList()
}
