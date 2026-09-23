package org.app.project.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {

    @Query("SELECT * FROM entries WHERE isDeleted = 0 ORDER BY date DESC")
    fun observeAll(): Flow<List<EntryEntity>>

    @Query(
        """
        SELECT * FROM entries
        WHERE isDeleted = 0
          AND (
            city LIKE '%' || :query || '%'
            OR notes LIKE '%' || :query || '%'
            OR dishes LIKE '%' || :query || '%'
            OR restaurantName LIKE '%' || :query || '%'
            OR title LIKE '%' || :query || '%'
          )
        ORDER BY date DESC
        """
    )
    fun search(query: String): Flow<List<EntryEntity>>

    @Query("SELECT * FROM entries WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): EntryEntity?

    @Insert
    suspend fun insert(entity: EntryEntity)

    @Update
    suspend fun update(entity: EntryEntity)

    @Query("UPDATE entries SET isDeleted = 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun softDelete(id: String, updatedAt: Long)
}