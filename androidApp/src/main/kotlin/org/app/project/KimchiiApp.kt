package org.app.project

import android.app.Application
import org.app.project.data.DatabaseBuilderFactory
import org.app.project.data.EntryRepository
import org.app.project.data.createEntryRepository

class KimchiiApp : Application() {

    lateinit var repository: EntryRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val factory = DatabaseBuilderFactory(this)
        repository = createEntryRepository(factory)
    }
}