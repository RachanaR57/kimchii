package org.app.project.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import org.app.project.model.Entry
import org.app.project.model.EntryType
import org.app.project.presentation.EntryListViewModel
import org.app.project.theme.Ink
import org.app.project.theme.InkSoft
import org.app.project.theme.Lime
import org.app.project.theme.LimeDeep
import org.app.project.theme.Paper
import org.app.project.theme.RosePale
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper)
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .border(2.dp, Ink, RoundedCornerShape(20.dp))
                .background(Color.White, RoundedCornerShape(20.dp))
                .clickable { onBack() }
                .padding(start = 10.dp, end = 14.dp, top = 6.dp, bottom = 6.dp)
        ) {
            Text("‹", fontSize = 20.sp, color = Ink, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(6.dp))
            Text("Back", fontSize = 14.sp, color = Ink, fontWeight = FontWeight.Medium)
        }

        val e = entry
        if (e == null) {
            Spacer(Modifier.height(24.dp))
            Text("Loading…", color = InkSoft)
        } else if (editing) {
            Spacer(Modifier.height(16.dp))
            Text("Editing: ${e.city}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Ink)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = ratingText,
                onValueChange = { ratingText = it },
                label = { Text("Rating (1-5)") },
                singleLine = true,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = notesText,
                onValueChange = { notesText = it },
                label = { Text("Notes") },
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        val now = Clock.System.now().toEpochMilliseconds()
                        val updated = e.copy(
                            rating = ratingText.toIntOrNull(),
                            notes = notesText.trim().ifBlank { null },
                            updatedAt = now
                        )
                        viewModel.updateEntry(updated)
                        entry = updated
                        editing = false
                    },
                    shape = RoundedCornerShape(4.dp)
                ) { Text("Save") }
                OutlinedButton(
                    onClick = { editing = false },
                    shape = RoundedCornerShape(4.dp)
                ) { Text("Cancel", color = Ink) }
            }
        } else {
            val isFood = e.type == EntryType.FOOD
            val tagBg = if (isFood) RosePale else Lime
            val tagText = if (isFood) Color(0xFF4B1528) else LimeDeep

            Spacer(Modifier.height(16.dp))
            Text(
                if (isFood) "FOOD" else "TRAVEL",
                fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = tagText,
                modifier = Modifier
                    .border(1.5.dp, Ink, RoundedCornerShape(3.dp))
                    .background(tagBg, RoundedCornerShape(3.dp))
                    .padding(horizontal = 7.dp, vertical = 3.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(e.restaurantName ?: e.title ?: e.city, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Ink)
            Spacer(Modifier.height(4.dp))
            Text("${e.city}, ${e.country} · ${e.date}", fontSize = 13.sp, color = InkSoft)

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Ink, RoundedCornerShape(4.dp))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                e.rating?.let { DetailStat("Rating", "$it / 5") }
                e.dishes?.let { DetailStat("Dishes", it) }
                DetailStat("Added by", e.createdBy)
            }

            e.notes?.let {
                Spacer(Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, Ink, RoundedCornerShape(4.dp))
                        .background(Color.White, RoundedCornerShape(4.dp))
                        .padding(14.dp)
                ) {
                    Text("NOTES", fontSize = 10.sp, letterSpacing = 1.sp, color = InkSoft, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(5.dp))
                    Text(it, fontSize = 14.sp, color = Ink)
                }
            }

            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = {
                        notesText = e.notes ?: ""
                        ratingText = e.rating?.toString() ?: ""
                        editing = true
                    },
                    shape = RoundedCornerShape(4.dp)
                ) { Text("Edit", color = Ink) }
                Button(
                    onClick = { viewModel.deleteEntry(e.id); onBack() },
                    shape = RoundedCornerShape(4.dp)
                ) { Text("Delete") }
            }
        }
    }
}

@Composable
private fun DetailStat(label: String, value: String) {
    Column {
        Text(label, fontSize = 10.sp, color = InkSoft, letterSpacing = 0.5.sp)
        Spacer(Modifier.height(2.dp))
        Text(value, fontSize = 14.sp, color = Ink, fontWeight = FontWeight.Medium)
    }
}