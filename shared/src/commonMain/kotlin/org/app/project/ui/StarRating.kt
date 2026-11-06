package org.app.project.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

const val MAX_RATING = 5

// Each star leans a little differently so the row looks hand-stamped.
private val starTilts = listOf(-8f, 6f, -5f, 9f, -6f)

// Star outline on a 26 x 26 grid (the extra room on the right/bottom holds the offset shadow).
private const val STAR_GRID = 26f
private val starPoints = listOf(
    12f to 2f, 15f to 8.6f, 22f to 9.4f, 16.8f to 14.2f, 18.3f to 21.4f,
    12f to 17.6f, 5.7f to 21.4f, 7.2f to 14.2f, 2f to 9.4f, 9f to 8.6f
)
private const val STAR_STROKE = 2.2f
private const val STAR_SHADOW_OFFSET = 2.2f

/**
 * A row of [MAX_RATING] tappable stars with a thick black outline.
 * Tapping star N sets the rating to N; tapping the current rating again clears it.
 */
@Composable
fun StarRating(
    rating: Int?,
    onRatingChange: (Int?) -> Unit,
    fillColor: Color,
    modifier: Modifier = Modifier,
    starSize: Dp = 40.dp
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        for (star in 1..MAX_RATING) {
            val filled = rating != null && star <= rating
            Canvas(
                modifier = Modifier
                    .size(starSize)
                    .rotate(starTilts[(star - 1) % starTilts.size])
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        role = Role.Button
                    ) { onRatingChange(if (rating == star) null else star) }
                    .semantics {
                        contentDescription = "$star of $MAX_RATING stars" + if (filled) ", selected" else ""
                    }
            ) {
                val unit = size.minDimension / STAR_GRID
                val path = starPath(size)
                val outline = Stroke(width = STAR_STROKE * unit, join = StrokeJoin.Round)

                // hard black shadow, nudged down and to the right
                translate(left = STAR_SHADOW_OFFSET * unit, top = STAR_SHADOW_OFFSET * unit) {
                    drawPath(path, Color.Black)
                    drawPath(path, Color.Black, style = outline)
                }
                drawPath(path, if (filled) fillColor else Color.White)
                drawPath(path, Color.Black, style = outline)
            }
        }
    }
}

private fun starPath(size: Size): Path {
    val unit = size.minDimension / STAR_GRID
    return Path().apply {
        starPoints.forEachIndexed { index, (x, y) ->
            if (index == 0) moveTo(x * unit, y * unit) else lineTo(x * unit, y * unit)
        }
        close()
    }
}
