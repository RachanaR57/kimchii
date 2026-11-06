package org.app.project.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EntryRow(
    val id: String,
    @SerialName("space_id") val spaceId: String,
    val type: String,
    val city: String,
    val country: String,
    val date: String,
    val status: String,
    val rating: Int? = null,
    val notes: String? = null,
    val title: String? = null,
    @SerialName("restaurant_name") val restaurantName: String? = null,
    val dishes: String? = null,
    @SerialName("created_by") val createdBy: String,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("updated_at") val updatedAt: Long,
    @SerialName("is_deleted") val isDeleted: Boolean
)