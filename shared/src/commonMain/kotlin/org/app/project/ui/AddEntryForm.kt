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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.project.data.CityChoices
import org.app.project.data.Country
import org.app.project.data.suggestCities
import org.app.project.data.suggestCountries
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
    loadCountries: suspend () -> List<Country>,
    loadCityChoices: suspend (Country) -> CityChoices,
    onSave: (Entry) -> Unit,
    onCancel: () -> Unit
) {
    var type by remember { mutableStateOf(EntryType.FOOD) }
    var city by remember { mutableStateOf("") }
    var countryText by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }   // stored as YYYY-MM-DD
    var rating by remember { mutableStateOf<Int?>(null) }
    var notes by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    // The country list is loaded once when the form opens.
    val countries by produceState(initialValue = emptyList<Country>()) {
        value = loadCountries()
    }
    // Country is a strict pick: the text only counts once it is exactly a country from the list.
    val selectedCountry = remember(countryText, countries) {
        countries.firstOrNull { it.name.equals(countryText.trim(), ignoreCase = true) }
    }
    // City suggestions are reloaded whenever a different country is chosen.
    val cityChoices by produceState(initialValue = CityChoices.Empty, selectedCountry) {
        value = CityChoices.Empty
        selectedCountry?.let { value = loadCityChoices(it) }
    }

    val countrySuggestions = remember(countryText, countries) {
        suggestCountries(countryText, countries).map { Suggestion(it.name) }
    }
    val citySuggestions = remember(city, cityChoices) {
        suggestCities(city, cityChoices).map {
            Suggestion(it.name, hint = if (it.fromHistory) "been here" else null)
        }
    }

    val canSave = city.isNotBlank() && selectedCountry != null && isValidIsoDate(date)
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

        // Country comes first so the city suggestions can be narrowed to it.
        SuggestionField(
            value = countryText,
            onValueChange = { countryText = it },
            label = "Country",
            suggestions = countrySuggestions,
            error = when {
                countryText.isBlank() || selectedCountry != null -> null
                countrySuggestions.isEmpty() -> "No country matches that"
                else -> "Pick a country from the list"
            }
        )
        Spacer(Modifier.height(10.dp))
        SuggestionField(
            value = city,
            onValueChange = { city = it },
            label = "City",
            suggestions = citySuggestions
        )
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
                        // the list's own spelling, even if it was typed in lowercase
                        country = selectedCountry?.name ?: countryText.trim(),
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

/** One row in a suggestion menu: the text to fill in, and an optional small note beside it. */
private data class Suggestion(val text: String, val hint: String? = null)

/**
 * A text field with a dropdown of suggestions under it. Typing filters the list (the caller
 * passes the already-filtered [suggestions]); tapping a row fills the field with it.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SuggestionField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    suggestions: List<Suggestion>,
    error: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    // No point opening a menu whose only row is exactly what is already in the field.
    val onlyRowIsTheValue = suggestions.size == 1 && suggestions[0].text.equals(value.trim(), ignoreCase = true)
    val showMenu = expanded && suggestions.isNotEmpty() && !onlyRowIsTheValue

    ExposedDropdownMenuBox(
        expanded = showMenu,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
                expanded = true
            },
            label = { Text(label) },
            singleLine = true,
            // Only flag a problem once the menu is closed; while it is open the list is the prompt.
            isError = error != null && !showMenu,
            supportingText = error?.takeIf { !showMenu }?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier
                // PrimaryEditable = the field stays typable and keeps the keyboard while the menu is open
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = showMenu,
            onDismissRequest = { expanded = false }
        ) {
            suggestions.forEach { suggestion ->
                DropdownMenuItem(
                    text = { Text(suggestion.text, color = Ink) },
                    trailingIcon = suggestion.hint?.let { { Text(it, fontSize = 11.sp, color = InkSoft) } },
                    onClick = {
                        onValueChange(suggestion.text)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
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
