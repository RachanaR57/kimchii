package org.app.project.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.app.project.model.Entry
import org.app.project.model.EntryStatus
import org.app.project.model.EntryType
import kotlin.time.Clock
import kotlin.uuid.Uuid

@Composable
fun AddEntryForm(
    onSave: (Entry) -> Unit,
    onCancel: () -> Unit
) {
    var type by remember { mutableStateOf(EntryType.FOOD) }
    var city by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val canSave = city.isNotBlank() && country.isNotBlank() && date.isNotBlank()

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {

        Text("New memory")
        Spacer(Modifier.height(12.dp))

        Row {
            Button(onClick = { type = EntryType.FOOD }) { Text("Food") }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(onClick = { type = EntryType.TRAVEL }) { Text("Travel") }
        }
        Text("Selected: ${type.name}")
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("City *") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = country,
            onValueChange = { country = it },
            label = { Text("Country *") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Date (YYYY-MM-DD) *") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Row {
            Button(
                onClick = {
                    val now = Clock.System.now().toEpochMilliseconds()
                    onSave(
                        Entry(
                            id = Uuid.random().toString(),
                            type = type,
                            city = city.trim(),
                            date = date.trim(),
                            country = country.trim(),
                            status = EntryStatus.BEEN,
                            notes = notes.trim().ifBlank { null },
                            createdBy = "Rachana",
                            createdAt = now,
                            updatedAt = now
                        )
                    )
                },
                enabled = canSave
            ) {
                Text("Save")
            }

            Spacer(Modifier.width(8.dp))

            TextButton(onClick = onCancel) { Text("Cancel") }
        }
    }
}