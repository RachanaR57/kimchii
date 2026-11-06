package org.app.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import org.app.project.presentation.AuthViewModel
import org.app.project.presentation.EntryListViewModel
import org.app.project.ui.AppNavigation

class MainActivity : ComponentActivity() {

    // `by viewModels` keeps the same instances across rotation and other
    // configuration changes instead of building new ones in every onCreate.
    private val entryViewModel: EntryListViewModel by viewModels {
        viewModelFactory {
            initializer { EntryListViewModel((application as KimchiiApp).repository) }
        }
    }

    private val authViewModel: AuthViewModel by viewModels {
        viewModelFactory {
            initializer { AuthViewModel((application as KimchiiApp).authService) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation(authViewModel, entryViewModel)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
