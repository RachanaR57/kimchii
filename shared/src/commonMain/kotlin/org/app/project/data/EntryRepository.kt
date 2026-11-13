package org.app.project.data

import kotlinx.coroutines.flow.Flow
import org.app.project.model.Entry

interface EntryRepository {
    fun observeAll(): Flow<List<Entry>>

    fun search(query: String): Flow<List<Entry>>

    suspend fun getById(id: String): Entry?

    /** Cities already logged in [country], most recently used first. */
    suspend fun citiesLoggedIn(country: String): List<String>

    suspend fun add(entry: Entry)

    suspend fun update(entry: Entry)

    suspend fun softDelete(id: String)
}