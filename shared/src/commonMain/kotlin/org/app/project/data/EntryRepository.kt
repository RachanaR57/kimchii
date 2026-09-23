package org.app.project.data

import kotlinx.coroutines.flow.Flow
import org.app.project.model.Entry

interface EntryRepository {
    fun observeAll(): Flow<List<Entry>>

    fun search(query: String): Flow<List<Entry>>

    suspend fun getById(id: String): Entry?

    suspend fun add(entry: Entry)

    suspend fun update(entry: Entry)

    suspend fun softDelete(id: String)
}