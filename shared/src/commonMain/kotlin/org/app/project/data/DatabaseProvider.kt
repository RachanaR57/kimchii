package org.app.project.data

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

fun createDatabase(builderFactory: DatabaseBuilderFactory): AppDatabase {
    return builderFactory.create()
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

fun createEntryRepository(builderFactory: DatabaseBuilderFactory): EntryRepository {
    val database = createDatabase(builderFactory)
    return EntryRepositoryImpl(database.entryDao())
}