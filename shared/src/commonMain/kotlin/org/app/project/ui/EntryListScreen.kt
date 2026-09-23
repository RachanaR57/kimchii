package org.app.project.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import org.app.project.presentation.EntryListViewModel
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.app.project.model.Entry
import org.app.project.model.EntryStatus
import org.app.project.model.EntryType
import kotlin.time.Clock
import kotlin.uuid.Uuid

@Composable
fun EntryListScreen(
    viewModel: EntryListViewModel,
    onEntryClick: (String) -> Unit
) {
    val entries by viewModel.entries.collectAsState()
    var showAddForm by remember { mutableStateOf(false) }

    Spacer(modifier = Modifier.height(200.dp))

    if (showAddForm) {
        AddEntryForm(
            onSave = { entry ->
                viewModel.addEntry(entry)
                showAddForm = false
            },
            onCancel = { showAddForm = false }
        )
    } else {
        val query by viewModel.query.collectAsState()

        Column(modifier = Modifier.fillMaxSize().padding(100.dp)) {
            Button(onClick = { showAddForm = true }) {
                Text("+ Log a memory (${entries.size})")
            }

            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.onQueryChange(it) },
                label = { Text("Search a dish, city, anything…") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )
            LazyColumn {
                items(entries) { entry ->
                    Text(
                        text = "${entry.city} · ${entry.notes ?: ""}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEntryClick(entry.id) }
                            .padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}
