package org.app.project.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Editorial palette
val Ink = Color(0xFF26215C)        // deep indigo — text, borders
val InkSoft = Color(0xFF534AB7)    // lighter indigo — secondary text
val Paper = Color(0xFFF1EFE8)      // warm cream — background
val Rose = Color(0xFFD4537E)       // accent — food, highlights
val RosePale = Color(0xFFF4C0D1)   // rose tint — food tag bg
val Lime = Color(0xFFC0DD97)       // accent — travel, actions
val LimeDeep = Color(0xFF27500A)   // dark lime — text on lime

private val KimchiiColors = lightColorScheme(
    primary = Ink,
    onPrimary = Paper,
    secondary = Rose,
    background = Paper,
    onBackground = Ink,
    surface = Color(0xFFFFFFFF),
    onSurface = Ink,
)

@Composable
fun KimchiiTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KimchiiColors,
        content = content
    )
}