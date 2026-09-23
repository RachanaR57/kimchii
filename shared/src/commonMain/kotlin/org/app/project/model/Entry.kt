package org.app.project.model

import kotlin.uuid.Uuid

data class Entry(
    val id: String = Uuid.random().toString(),

    val type: EntryType,
    val city: String,
    val date: String,
    val country: String,

    val status: EntryStatus = EntryStatus.BEEN,
    val rating: Int? = null,
    val notes: String? = null,
    val title: String? = null,
    val restaurantName: String? = null,
    val dishes: String? = null,

    val createdBy: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isDeleted: Boolean = false
)