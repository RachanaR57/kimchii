package org.app.project.data

import io.github.jan.supabase.postgrest.postgrest
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class SyncService(
    private val dao: EntryDao,
    private val spaceService: SpaceService
) {
    suspend fun sync(displayName: String) {
        val spaceId = spaceService.ensureSpace(displayName)
        val lastSynced = dao.getLastSynced() ?: 0L
        val now = Clock.System.now().toEpochMilliseconds()

        // Read both sides before writing either, so the newest version of a row wins
        // no matter which device syncs first.
        val localChanges = dao.getChangedSince(lastSynced)
        val cloudRows = supabase.postgrest["entries"].select {
            filter {
                eq("space_id", spaceId)
                gt("updated_at", lastSynced)
            }
        }.decodeList<EntryRow>()
        val cloudById = cloudRows.associateBy { it.id }

        // PULL — cloud rows that are newer than what we have come down
        for (row in cloudRows) {
            val local = dao.getById(row.id)
            if (local == null || row.updatedAt > local.updatedAt) {
                dao.upsert(row.toEntity())
            }
        }

        // PUSH — local changes go up, unless the cloud already has a newer version
        val toPush = localChanges.filter { local ->
            val cloud = cloudById[local.id]
            cloud == null || local.updatedAt > cloud.updatedAt
        }
        if (toPush.isNotEmpty()) {
            supabase.postgrest["entries"].upsert(toPush.map { it.toRow(spaceId) })
        }

        dao.setSyncState(SyncState(lastSyncedAt = now))
    }
}

private fun EntryEntity.toRow(spaceId: String) = EntryRow(
    id = id, spaceId = spaceId, type = type, city = city, country = country,
    date = date, status = status, rating = rating, notes = notes, title = title,
    restaurantName = restaurantName, dishes = dishes, createdBy = createdBy,
    createdAt = createdAt, updatedAt = updatedAt, isDeleted = isDeleted
)

private fun EntryRow.toEntity() = EntryEntity(
    id = id, type = type, city = city, country = country, date = date,
    status = status, rating = rating, notes = notes, title = title,
    restaurantName = restaurantName, dishes = dishes, createdBy = createdBy,
    createdAt = createdAt, updatedAt = updatedAt, isDeleted = isDeleted
)
