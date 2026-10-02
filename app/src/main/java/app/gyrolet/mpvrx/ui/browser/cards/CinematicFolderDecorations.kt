/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * Cinematic Folder Decorations
 * حدود نيونية متوهجة (Bloom) بدون Pulse — أداء ممتاز
 */

package app.gyrolet.mpvrx.ui.browser.cards

import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.NativePaint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import app.gyrolet.mpvrx.ui.icons.AppIcon as ImageVector
import app.gyrolet.mpvrx.ui.icons.Icons
import androidx.compose.ui.unit.dp

data class FolderColors(
    val primary: Color,
    val secondary: Color,
    val icon: ImageVector,
)

fun cinematicFolderColors(name: String): FolderColors {
    val lower = name.lowercase()
    return when {
        listOf("فيلم", "أفلام", "افلام", "movie", "film", "cinema").any { lower.contains(it) } ->
            FolderColors(Color(0xFF8A00FF), Color(0xFFFF007A), Icons.RoundedFilled.Movie)
        listOf("مسلسل", "مسلسلات", "series", "show", "tv", "season").any { lower.contains(it) } ->
            FolderColors(Color(0xFF0066FF), Color(0xFF00F0FF), Icons.RoundedFilled.Tv)
        listOf("موسيق", "اغاني", "أغاني", "music", "audio", "song", "mp3").any { lower.contains(it) } ->
            FolderColors(Color(0xFFFF007A), Color(0xFF8A00FF), Icons.RoundedFilled.MusicNote)
        listOf("صور", "photo", "picture", "gallery").any { lower.contains(it) } ->
            FolderColors(Color(0xFFFF7A00), Color(0xFFFFE600), Icons.RoundedFilled.Image)
        listOf("فيديو", "فيديوهات", "video", "vid", "clip").any { lower.contains(it) } ->
            FolderColors(Color(0xFF00F0FF), Color(0xFF8A00FF), Icons.RoundedFilled.VideoLibrary)
        listOf("image", "img", "screenshot").any { lower.contains(it) } ->
            FolderColors(Color(0xFFFF1E8A), Color(0xFFFFE600), Icons.RoundedFilled.Image)
        listOf("download", "تحميل", "تحميلات").any { lower.contains(it) } ->
            FolderColors(Color(0xFF39FF14), Color(0xFF00F0FF), Icons.RoundedFilled.Download)
        listOf("وثائق", "document", "doc", "pdf").any { lower.contains(it) } ->
            FolderColors(Color(0xFFFFD500), Color(0xFFFF007A), Icons.RoundedFilled.Article)
        else -> FolderColors(Color(0xFF8A00FF), Color(0xFF00F0FF), Icons.RoundedFilled.Folder)
    }
}

/**
 * Modifier — حدود نيونية + توهج Bloom (بدون Pulse)
 */
@Composable
fun Modifier.cinematicNeonBorder(
    folderName: String,
    isActive: Boolean = false,
    isSelected: Boolean = false,
): Modifier {
    val colors = cinematicFolderColors(folderName)
    val primaryAlpha = if (isActive || isSelected) 0.95f else 0.55f
    val secondaryAlpha = if (isActive || isSelected) 0.75f else 0.35f
    val glowAlpha = if (isActive || isSelected) 0.7f else 0.3f
    val glowColor = colors.primary

    return this
        .drawBehind {
            // توهج Bloom خلف البطاقة (بدون Pulse — يحسب مرة واحدة)
            drawIntoCanvas { canvas ->
                val paint = NativePaint().apply {
                    color = glowColor.copy(alpha = glowAlpha).toArgb()
                    isAntiAlias = true
                    maskFilter = android.graphics.BlurMaskFilter(
                        20f,
                        android.graphics.BlurMaskFilter.Blur.NORMAL,
                    )
                }
                canvas.nativeCanvas.drawRoundRect(
                    0f,
                    0f,
                    size.width,
                    size.height,
                    20.dp.toPx(),
                    20.dp.toPx(),
                    paint,
                )
            }
        }
        .border(
            width = if (isActive || isSelected) 2.dp else 1.2.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    colors.primary.copy(alpha = primaryAlpha),
                    colors.secondary.copy(alpha = secondaryAlpha),
                    colors.primary.copy(alpha = primaryAlpha * 0.5f),
                ),
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        )
}
