package org.app.project.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.savedstate.read
import org.app.project.presentation.EntryListViewModel

@Composable
fun AppNavigation(viewModel: EntryListViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "list"
    ) {
        composable("list") {
            EntryListScreen(
                viewModel = viewModel,
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
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}