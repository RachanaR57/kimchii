package org.app.project

import androidx.compose.ui.window.ComposeUIViewController
import org.app.project.data.AuthService
import org.app.project.data.DatabaseBuilderFactory
import org.app.project.data.createEntryRepository
import org.app.project.presentation.AuthViewModel
import org.app.project.presentation.EntryListViewModel
import org.app.project.ui.AppNavigation
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    // Built once per view controller, outside the composable, so a recomposition
    // can't open a second database or throw away the view models' state.
    val repository = createEntryRepository(DatabaseBuilderFactory())
    val entryViewModel = EntryListViewModel(repository)
    val authViewModel = AuthViewModel(AuthService())
    return ComposeUIViewController {
        AppNavigation(authViewModel, entryViewModel)
    }
}
