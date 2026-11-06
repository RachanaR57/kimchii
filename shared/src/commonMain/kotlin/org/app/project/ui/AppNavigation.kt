package org.app.project.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.savedstate.read
import org.app.project.data.AuthState
import org.app.project.presentation.AuthViewModel
import org.app.project.presentation.EntryListViewModel
import org.app.project.theme.KimchiiTheme
import org.app.project.theme.Paper

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    entryViewModel: EntryListViewModel
) {
    KimchiiTheme {
        val authState by authViewModel.authState.collectAsState()

        when (authState) {
            // Session is still being restored — show a blank page instead of flashing the login screen.
            AuthState.Loading -> Box(Modifier.fillMaxSize().background(Paper))
            AuthState.SignedOut -> AuthScreen(authViewModel)
            AuthState.SignedIn -> MainNavHost(entryViewModel)
        }
    }
}

@Composable
private fun MainNavHost(entryViewModel: EntryListViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            EntryListScreen(
                viewModel = entryViewModel,
                onEntryClick = { id -> navController.navigate("detail/$id") }
            )
        }
        composable(
            route = "detail/{entryId}",
            arguments = listOf(navArgument("entryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.read { getStringOrNull("entryId") } ?: ""
            EntryDetailScreen(
                entryId = entryId,
                viewModel = entryViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
