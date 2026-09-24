package org.app.project

import androidx.compose.ui.window.ComposeUIViewController
import org.app.project.data.DatabaseBuilderFactory
import org.app.project.data.createEntryRepository
import org.app.project.presentation.EntryListViewModel
import org.app.project.ui.AppNavigation

fun MainViewController() = ComposeUIViewController {
    val repository = createEntryRepository(DatabaseBuilderFactory())
    val viewModel = EntryListViewModel(repository)
    AppNavigation(viewModel)
}