package org.app.project.data

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

// v2 only adds the sync_state table, so existing entries are kept on upgrade.
private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `sync_state` " +
                "(`id` INTEGER NOT NULL, `lastSyncedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))"
        )
    }
}

fun createDatabase(builderFactory: DatabaseBuilderFactory): AppDatabase {
    return builderFactory.create()
        .setDriver(BundledSQLiteDriver())
        .addMigrations(MIGRATION_1_2)
        .setQueryCoroutineContext(Dispatchers.IO)
        .fallbackToDestructiveMigration(true)
        .build()
}

fun createEntryRepository(builderFactory: DatabaseBuilderFactory): EntryRepository {
    val database = createDatabase(builderFactory)
    return EntryRepositoryImpl(database.entryDao())
}

class AppGraph(
    val repository: EntryRepository,
    val authService: AuthService,
    val syncService: SyncService
)

fun createAppGraph(builderFactory: DatabaseBuilderFactory): AppGraph {
    val database = createDatabase(builderFactory)
    val dao = database.entryDao()
    return AppGraph(
        repository = EntryRepositoryImpl(dao),
        authService = AuthService(),
        syncService = SyncService(dao, SpaceService())
    )
}