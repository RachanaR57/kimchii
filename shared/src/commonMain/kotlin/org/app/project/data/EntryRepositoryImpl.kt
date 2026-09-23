package org.app.project.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.app.project.model.Entry
import org.app.project.model.EntryStatus
import org.app.project.model.EntryType
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
private fun nowMillis(): Long = Clock.System.now().toEpochMilliseconds()

class EntryRepositoryImpl(
    private val dao: EntryDao
) : EntryRepository {

    override fun observeAll(): Flow<List<Entry>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun search(query: String): Flow<List<Entry>> =
        dao.search(query).map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: String): Entry? =
        dao.getById(id)?.toDomain()

    override suspend fun add(entry: Entry) =
        dao.insert(entry.toEntity())

    override suspend fun update(entry: Entry) =
        dao.update(entry.toEntity())

    override suspend fun softDelete(id: String) =
        dao.softDelete(id = id, updatedAt = nowMillis())
}

private fun EntryEntity.toDomain(): Entry = Entry(
    id = id,
    type = EntryType.valueOf(type),
    city = city,
    date = date,
    country = country,
    status = EntryStatus.valueOf(status),
    rating = rating,
    notes = notes,
    title = title,
    restaurantName = restaurantName,
    dishes = dishes,
    createdBy = createdBy,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isDeleted = isDeleted
)

private fun Entry.toEntity(): EntryEntity = EntryEntity(
    id = id,
    type = type.name,
    city = city,
    date = date,
    country = country,
    status = status.name,
    rating = rating,
    notes = notes,
    title = title,
    restaurantName = restaurantName,
    dishes = dishes,
    createdBy = createdBy,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isDeleted = isDeleted
)