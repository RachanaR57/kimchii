package org.app.project.data

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSHomeDirectory

actual class DatabaseBuilderFactory {
    @OptIn(ExperimentalForeignApi::class)
    actual fun create(): RoomDatabase.Builder<AppDatabase> {
        val dbFile = NSHomeDirectory() + "/kimchii.db"
        return Room.databaseBuilder<AppDatabase>(
            name = dbFile
        )
    }
}