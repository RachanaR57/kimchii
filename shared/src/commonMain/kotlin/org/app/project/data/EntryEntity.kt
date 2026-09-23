package org.app.project.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entries")
data class EntryEntity (
    @PrimaryKey
    val id: String,

    val type: String,
    val city: String,
    val date: String,
    val country: String,

    val status: String,
    val rating: Int?,
    val notes: String?,
    val title: String?,
    val restaurantName: String?,
    val dishes: String?,

    val createdBy: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isDeleted: Boolean
)