package app.gyrolet.mpvrx.ui.browser.cards

import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

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
        colors = listOf(
            secondary.copy(alpha = 0.92f),
            primary.copy(alpha = 0.96f),
            tertiary.copy(alpha = 0.78f),
            secondary.copy(alpha = 0.72f),
        ),
    )
    return this.border(width = 1.5.dp, brush = edge, shape = shape)
}
