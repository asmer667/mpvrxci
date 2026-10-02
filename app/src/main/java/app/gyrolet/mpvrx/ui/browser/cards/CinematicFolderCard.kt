package app.gyrolet.mpvrx.ui.browser.cards

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CinematicFolderInfo(
    val name: String,
    val path: String,
    val videoCount: Int,
    val totalSizeBytes: Long = 0L,
)

private enum class FolderType(
    val icon: ImageVector,
    val primaryColor: Color,
    val secondaryColor: Color,
) {
    MOVIE(Icons.Default.Movie, Color(0xFF8A00FF), Color(0xFFFF007A)),
    SERIES(Icons.Default.Tv, Color(0xFF0066FF), Color(0xFF00F0FF)),
    MUSIC(Icons.Default.MusicNote, Color(0xFFFF007A), Color(0xFF8A00FF)),
    PHOTOS(Icons.Default.PhotoCamera, Color(0xFFFF7A00), Color(0xFFFFE600)),
    VIDEO(Icons.Default.VideoLibrary, Color(0xFF00F0FF), Color(0xFF8A00FF)),
    IMAGES(Icons.Default.Image, Color(0xFFFF1E8A), Color(0xFFFFE600)),
    DOWNLOADS(Icons.Default.Download, Color(0xFF39FF14), Color(0xFF00F0FF)),
    DOCS(Icons.Default.Article, Color(0xFFFFD500), Color(0xFFFF007A)),
    DEFAULT(Icons.Default.Folder, Color(0xFF8A00FF), Color(0xFF00F0FF)),
}

private fun detectFolderType(name: String): FolderType {
    val lower = name.lowercase()
    return when {
        listOf("فيلم", "أفلام", "افلام", "movie", "film", "cinema").any { lower.contains(it) } -> FolderType.MOVIE
        listOf("مسلسل", "مسلسلات", "series", "show", "tv", "season").any { lower.contains(it) } -> FolderType.SERIES
        listOf("موسيق", "اغاني", "أغاني", "music", "audio", "song", "mp3").any { lower.contains(it) } -> FolderType.MUSIC
        listOf("صور", "photo", "picture", "gallery").any { lower.contains(it) } -> FolderType.PHOTOS
        listOf("فيديو", "فيديوهات", "video", "vid", "clip").any { lower.contains(it) } -> FolderType.VIDEO
        listOf("image", "img", "screenshot").any { lower.contains(it) } -> FolderType.IMAGES
        listOf("download", "تحميل", "تحميلات").any { lower.contains(it) } -> FolderType.DOWNLOADS
        listOf("وثائق", "document", "doc", "pdf").any { lower.contains(it) } -> FolderType.DOCS
        else -> FolderType.DEFAULT
    }
}

private fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return ""
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0
    return when {
        gb >= 1.0 -> String.format(java.util.Locale.US, "%.1f GB", gb)
        mb >= 1.0 -> String.format(java.util.Locale.US, "%.0f MB", mb)
        kb >= 1.0 -> String.format(java.util.Locale.US, "%.0f KB", kb)
        else -> "$bytes B"
    }
}

@Composable
fun CinematicFolderCard(
    folder: CinematicFolderInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isNew: Boolean = false,
) {
    val folderType = detectFolderType(folder.name)
    val infiniteTransition = rememberInfiniteTransition(label = "folder_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse_scale",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        folderType.primaryColor.copy(alpha = 0.15f),
                        folderType.secondaryColor.copy(alpha = 0.08f),
                        Color.Black.copy(alpha = 0.4f),
                    ),
                ),
            )
            .border(
                1.5.dp,
                Brush.linearGradient(
                    listOf(
                        folderType.primaryColor.copy(alpha = 0.8f),
                        folderType.secondaryColor.copy(alpha = 0.5f),
                        Color.Transparent,
                    ),
                ),
                RoundedCornerShape(20.dp),
            )
            .clickable(onClick = onClick)
            .padding(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                folderType.primaryColor.copy(alpha = 0.4f),
                                folderType.primaryColor.copy(alpha = 0.1f),
                                Color.Transparent,
                            ),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = folderType.icon,
                    contentDescription = null,
                    tint = folderType.primaryColor,
                    modifier = Modifier.size(32.dp),
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = folder.name,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = folder.path,
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FolderChip("${folder.videoCount} فيديو", folderType.primaryColor)
                    if (folder.totalSizeBytes > 0) {
                        FolderChip(formatBytes(folder.totalSizeBytes), folderType.secondaryColor)
                    }
                    if (isNew) {
                        FolderChip("جديد", Color(0xFF39FF14))
                    }
                }
            }
        }
    }
}

@Composable
private fun FolderChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(0.5.dp, color.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(text, color = color, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
    }
}
