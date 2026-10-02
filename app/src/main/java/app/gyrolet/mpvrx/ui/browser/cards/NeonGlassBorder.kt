/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package app.gyrolet.mpvrx.ui.browser.cards

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawOutline
import androidx.compose.ui.unit.dp

/** Lightweight neon edging for media cards. Uses three lightweight gradient passes rather than expensive blur layers. */
@Composable
internal fun Modifier.neonGlassBorder(
  shape: Shape,
  enabled: Boolean = true,
): Modifier {
  if (!enabled) return this
  val primary = MaterialTheme.colorScheme.primary
  val secondary = MaterialTheme.colorScheme.secondary
  val tertiary = MaterialTheme.colorScheme.tertiary
  val edge = Brush.linearGradient(
    colors = listOf(secondary.copy(alpha = 0.92f), primary.copy(alpha = 0.96f), tertiary.copy(alpha = 0.78f), secondary.copy(alpha = 0.72f)),
  )
  val outerGlass = Brush.linearGradient(
    colors = listOf(secondary.copy(alpha = 0.12f), primary.copy(alpha = 0.16f), tertiary.copy(alpha = 0.12f)),
  )
  val innerGlass = Brush.linearGradient(
    colors = listOf(Color.White.copy(alpha = 0.24f), secondary.copy(alpha = 0.42f), primary.copy(alpha = 0.34f)),
  )
  return drawBehind {
    val outline = shape.createOutline(size, layoutDirection, this)
    // Three restrained passes create a layered glass edge without per-card blur surfaces.
    drawOutline(outline = outline, brush = outerGlass, style = Stroke(width = 8.dp.toPx()))
    drawOutline(outline = outline, brush = edge, style = Stroke(width = 3.dp.toPx()))
    drawOutline(outline = outline, brush = innerGlass, style = Stroke(width = 1.dp.toPx()))
  }
}
