package org.app.project.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.project.model.Entry
import org.app.project.model.EntryType
import org.app.project.presentation.EntryListViewModel
import org.app.project.theme.Ink
import org.app.project.theme.InkSoft
import org.app.project.theme.Lime
import org.app.project.theme.LimeDeep
import org.app.project.theme.Paper
import org.app.project.theme.RosePale

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
                Text(
                    "Memories",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )
                Text(
                    "${entries.size} kept",
                    fontSize = 12.sp,
                    color = InkSoft,
                    modifier = Modifier
                        .border(1.5.dp, Ink, RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                )
            }

            Spacer(Modifier.height(14.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.onQueryChange(it) },
                label = { Text("a dish · a city · anything…") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(4.dp)
            )

            Spacer(Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                items(entries) { entry ->
                    EntryCard(entry = entry, onClick = { onEntryClick(entry.id) })
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Button(
                onClick = { showAddForm = true },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("+ Log")
            }
        }
    }
}

@Composable
private fun EntryCard(entry: Entry, onClick: () -> Unit) {
    val isFood = entry.type == EntryType.FOOD
    val tagBg = if (isFood) RosePale else Lime
    val tagText = if (isFood) Color(0xFF4B1528) else LimeDeep
    val tagLabel = if (isFood) "FOOD" else "TRAVEL"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Ink, RoundedCornerShape(4.dp))
            .background(Color.White, RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                tagLabel,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = tagText,
                modifier = Modifier
                    .border(1.5.dp, Ink, RoundedCornerShape(3.dp))
                    .background(tagBg, RoundedCornerShape(3.dp))
                    .padding(horizontal = 7.dp, vertical = 3.dp)
            )
            entry.rating?.let {
                Text(
                    it.toString(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Ink,
                    modifier = Modifier
                        .size(26.dp)
                        .border(1.5.dp, Ink, CircleShape),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Text(
            entry.restaurantName ?: entry.title ?: entry.city,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Ink
        )
        Spacer(Modifier.height(3.dp))
        Text(
            "${entry.city}${entry.notes?.let { " · $it" } ?: ""}",
            fontSize = 12.sp,
            color = InkSoft,
            maxLines = 1
        )
    }
}