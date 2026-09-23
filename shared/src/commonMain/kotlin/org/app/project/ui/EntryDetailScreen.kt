package org.app.project.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.app.project.model.Entry
import org.app.project.presentation.EntryListViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun EntryDetailScreen(
    entryId: String,
    viewModel: EntryListViewModel,
    onBack: () -> Unit
) {
    var entry by remember { mutableStateOf<Entry?>(null) }
    var editing by remember { mutableStateOf(false) }
    var notesText by remember { mutableStateOf("") }
    var ratingText by remember { mutableStateOf("") }

    LaunchedEffect(entryId) {
        val loaded = viewModel.getEntry(entryId)
        entry = loaded
        notesText = loaded?.notes ?: ""
        ratingText = loaded?.rating?.toString() ?: ""
    }

    Column(modifier = Modifier.fillMaxSize().padding(100.dp)) {
        TextButton(onClick = onBack) { Text("← Back") }

        val e = entry
        if (e == null) {
            Text("Loading…")
        } else if (editing) {
            Text("Editing: ${e.city}")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = ratingText,
                onValueChange = { ratingText = it },
                label = { Text("Rating (1-5)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = notesText,
                onValueChange = { notesText = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Row {
                Button(onClick = {
                    val now = Clock.System.now().toEpochMilliseconds()
                    val updated = e.copy(
                        rating = ratingText.toIntOrNull(),
                        notes = notesText.trim().ifBlank { null },
                        updatedAt = now
                    )
                    viewModel.updateEntry(updated)
                    entry = updated
                    editing = false
                }) { Text("Save") }
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = { editing = false }) { Text("Cancel") }
            }
        } else {
            Text(e.type.name)
            Text(e.city + ", " + e.country)
            Text(e.date)
            e.rating?.let { Text("Rating: $it") }
            e.restaurantName?.let { Text("Restaurant: $it") }
            e.dishes?.let { Text("Dishes: $it") }
            e.notes?.let { Text("Notes: $it") }
            Text("Logged by ${e.createdBy}")
            Spacer(Modifier.height(16.dp))
            Row {
                OutlinedButton(onClick = {
                    notesText = e.notes ?: ""
                    ratingText = e.rating?.toString() ?: ""
                    editing = true
                }) { Text("Edit") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    viewModel.deleteEntry(e.id)
                    onBack()
                }) { Text("Delete") }
            }
        }
    }
}