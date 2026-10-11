package org.app.project.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.project.model.Entry
import org.app.project.model.EntryStatus
import org.app.project.model.EntryType
import org.app.project.theme.Ink
import org.app.project.theme.Lime
import org.app.project.theme.Paper
import org.app.project.theme.Rose
import org.app.project.theme.RosePale
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.Uuid

@OptIn(ExperimentalTime::class)
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper)
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("New memory", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Ink)
            Text("✕", fontSize = 20.sp, color = Ink, modifier = Modifier.clickable { onCancel() })
        }

        Spacer(Modifier.height(16.dp))

        // type toggle
        Row(
            modifier = Modifier
                .border(2.dp, Ink, RoundedCornerShape(4.dp))
                .padding(3.dp)
        ) {
            TypeChip("Food", type == EntryType.FOOD, RosePale) { type = EntryType.FOOD }
            TypeChip("Travel", type == EntryType.TRAVEL, Lime) { type = EntryType.TRAVEL }
        }

        Spacer(Modifier.height(20.dp))
        Text("THE THREE ESSENTIALS", fontSize = 10.sp, letterSpacing = 1.sp, color = Rose, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))

        EditorialField(city, { city = it }, "City")
        Spacer(Modifier.height(10.dp))
        EditorialField(country, { country = it }, "Country")
        Spacer(Modifier.height(10.dp))
        EditorialField(date, { date = it }, "Date (YYYY-MM-DD)")

        Spacer(Modifier.height(16.dp))
        EditorialField(notes, { notes = it }, "Notes (optional)")

        Spacer(Modifier.height(22.dp))

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
            enabled = canSave,
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save this memory", fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun TypeChip(label: String, selected: Boolean, selectedBg: Color, onClick: () -> Unit) {
    Text(
        label,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = Ink,
        modifier = Modifier
            .clickable { onClick() }
            .background(if (selected) selectedBg else Color.Transparent, RoundedCornerShape(2.dp))
            .padding(horizontal = 18.dp, vertical = 7.dp)
    )
}

@Composable
private fun EditorialField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.fillMaxWidth()
    )
}