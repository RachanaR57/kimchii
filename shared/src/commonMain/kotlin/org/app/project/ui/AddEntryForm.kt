package org.app.project.ui

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.project.model.Entry
import org.app.project.model.EntryStatus
import org.app.project.model.EntryType
import org.app.project.theme.Ink
import org.app.project.theme.InkSoft
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
    var date by remember { mutableStateOf("") }   // stored as YYYY-MM-DD
    var rating by remember { mutableStateOf<Int?>(null) }
    var notes by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    val canSave = city.isNotBlank() && country.isNotBlank() && isValidIsoDate(date)
    val typeColor = if (type == EntryType.FOOD) RosePale else Lime

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper)
            .statusBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
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
        SectionLabel("THE THREE ESSENTIALS")
        Spacer(Modifier.height(10.dp))

        EditorialField(city, { city = it }, "City")
        Spacer(Modifier.height(10.dp))
        EditorialField(country, { country = it }, "Country")
        Spacer(Modifier.height(10.dp))
        DateField(date = date, onClick = { showDatePicker = true })

        Spacer(Modifier.height(20.dp))
        SectionLabel("HOW WAS IT")
        Spacer(Modifier.height(10.dp))
        StarRating(
            rating = rating,
            onRatingChange = { rating = it },
            fillColor = typeColor
        )

        Spacer(Modifier.height(20.dp))
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
                        rating = rating,
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

    if (showDatePicker) {
        EntryDatePickerDialog(
            initialDate = date,
            onDateSelected = { date = it; showDatePicker = false },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, fontSize = 10.sp, letterSpacing = 1.sp, color = Rose, fontWeight = FontWeight.Bold)
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

/** Looks like the other fields, but tapping it opens the calendar instead of the keyboard. */
@Composable
private fun DateField(date: String, onClick: () -> Unit) {
    val shape = RoundedCornerShape(4.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clip(shape)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape)
            .clickable(role = Role.Button, onClickLabel = "Pick a date", onClick = onClick)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (date.isBlank()) {
            Text("Date", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            Column {
                Text("Date", fontSize = 12.sp, color = InkSoft)
                Text(formatDisplayDate(date), fontSize = 16.sp, color = Ink)
            }
        }
        CalendarIcon(color = Ink)
    }
}

@Composable
private fun CalendarIcon(color: Color) {
    Canvas(modifier = Modifier.size(20.dp)) {
        val stroke = 1.6.dp.toPx()
        val bodyTop = size.height * 0.16f
        // page outline
        drawRoundRect(
            color = color,
            topLeft = Offset(stroke / 2, bodyTop),
            size = Size(size.width - stroke, size.height - bodyTop - stroke / 2),
            cornerRadius = CornerRadius(2.5.dp.toPx()),
            style = Stroke(stroke)
        )
        // header line
        val headerY = size.height * 0.42f
        drawLine(color, Offset(stroke / 2, headerY), Offset(size.width - stroke / 2, headerY), stroke)
        // the two binder rings
        for (x in listOf(0.3f, 0.7f)) {
            drawLine(
                color,
                Offset(size.width * x, stroke / 2),
                Offset(size.width * x, size.height * 0.28f),
                stroke,
                cap = StrokeCap.Round
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EntryDatePickerDialog(
    initialDate: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val state = rememberDatePickerState(
        initialSelectedDateMillis = isoDateToEpochMillis(initialDate)
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { state.selectedDateMillis?.let { onDateSelected(epochMillisToIsoDate(it)) } },
                enabled = state.selectedDateMillis != null
            ) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    ) {
        DatePicker(state = state)
    }
}
