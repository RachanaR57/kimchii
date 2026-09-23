package org.app.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.app.project.presentation.EntryListViewModel
import org.app.project.ui.AppNavigation
import org.app.project.ui.EntryListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
       // enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val app = application as KimchiiApp
        val viewModel = EntryListViewModel(app.repository)
        setContent {
            AppNavigation(viewModel)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}