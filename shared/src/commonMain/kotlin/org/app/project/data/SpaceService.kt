package org.app.project.data

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class SpaceMemberRow(
    @SerialName("space_id") val spaceId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("display_name") val displayName: String? = null
)

@Serializable
data class SpaceRow(
    val id: String? = null,
    val name: String
)

class SpaceService {

    suspend fun ensureSpace(displayName: String): String {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: error("Not logged in")

        val existing = supabase.postgrest["space_members"]
            .select { filter { eq("user_id", userId) } }
            .decodeList<SpaceMemberRow>()
        if (existing.isNotEmpty()) return existing.first().spaceId

        val spaceId = Uuid.random().toString()
        supabase.postgrest["spaces"].insert(SpaceRow(id = spaceId, name = "$displayName's space"))
        supabase.postgrest["space_members"].insert(
            SpaceMemberRow(spaceId = spaceId, userId = userId, displayName = displayName)
        )
        return spaceId
    }}