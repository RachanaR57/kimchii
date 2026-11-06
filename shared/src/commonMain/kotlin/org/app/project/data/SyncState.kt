package org.app.project.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_state")
data class SyncState(
    @PrimaryKey val id: Int = 1,
    val lastSyncedAt: Long
)