package app.gyrolet.mpvrx.ui.browser.folderlist

import android.graphics.Bitmap
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CinematicFolderVideoInfo(
    val id: String,
    val title: String,
    val episodeNumber: Int = 0,
    val duration: String = "",
    val progress: Float = 0f,
    val thumbnail: Bitmap? = null,
    val isLastWatched: Boolean = false,
)

private val NeonCyan = Color(0xFF00F0FF)
private val NeonMagenta = Color(0xFFFF007A)
private val NeonPurple = Color(0xFF8A00FF)
private val NeonLime = Color(0xFF39FF14)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CinematicFolderCarousel(
    folderName: String,
    videos: List<CinematicFolderVideoInfo>,
    onVideoClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (videos.isEmpty()) return
    val listState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val lastWatchedIndex = videos.indexOfFirst { it.isLastWatched }.coerceAtLeast(0)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        NeonPurple.copy(alpha = 0.15f),
                        NeonMagenta.copy(alpha = 0.08f),
                        Color.Black.copy(alpha = 0.5f),
                    ),
                ),
            )
            .border(
                1.5.dp,
                Brush.linearGradient(
                    listOf(
                        NeonLime.copy(alpha = 0.5f),
                        NeonCyan.copy(alpha = 0.4f),
                        Color.Transparent,
                    ),
                ),
                RoundedCornerShape(24.dp),
            )
            .padding(vertical = 12.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        PulsingDot(NeonLime)
                        Text("شوهد مؤخراً", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(folderName, color = NeonCyan.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(NeonLime.copy(alpha = 0.15f))
                        .border(1.dp, NeonLime.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Text("${videos.size} حلقة", color = NeonLime, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(10.dp))
            LazyRow(
                state = listState,
                flingBehavior = flingBehavior,
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                itemsIndexed(videos, key = { _, v -> v.id }) { index, video ->
                    CinematicFolderVideoCard(video, index == lastWatchedIndex, video.isLastWatched) { onVideoClick(video.id) }
                }
            }
        }
    }
}

@Composable
private fun PulsingDot(color: Color) {
    val t = rememberInfiniteTransition(label = "pulsing_dot")
    val alpha by t.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "dot_alpha",
    )
    Box(Modifier.size(8.dp).clip(CircleShape).background(color.copy(alpha = alpha)))
}

@Composable
private fun CinematicFolderVideoCard(
    video: CinematicFolderVideoInfo,
    isFeatured: Boolean,
    isLastWatched: Boolean,
    onClick: () -> Unit,
) {
    val t = rememberInfiniteTransition(label = "folder_card_pulse")
    val glowAlpha by t.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow",
    )
    val scale by t.animateFloat(
        initialValue = 1f,
        targetValue = if (isFeatured) 1.03f else 1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "scale",
    )
    val cardWidth = if (isFeatured) 200.dp else 140.dp
    val cardHeight = if (isFeatured) 130.dp else 100.dp
    val borderBrush = when {
        isLastWatched -> Brush.linearGradient(listOf(NeonLime.copy(alpha = glowAlpha), NeonCyan.copy(alpha = glowAlpha)))
        isFeatured -> Brush.linearGradient(listOf(NeonCyan.copy(alpha = glowAlpha), NeonMagenta.copy(alpha = glowAlpha)))
        else -> Brush.linearGradient(listOf(Color.White.copy(alpha = 0.2f), Color.Transparent))
    }

    Box(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight)
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black.copy(alpha = 0.6f))
            .border(if (isLastWatched || isFeatured) 2.dp else 1.dp, borderBrush, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
    ) {
        Box(Modifier.fillMaxWidth().height(cardHeight)) {
            video.thumbnail?.let {
                Image(it.asImageBitmap(), null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth().height(cardHeight))
            }
            Box(Modifier.fillMaxWidth().height(cardHeight).background(
                Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.92f))),
            ))
            if (isLastWatched) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(NeonLime.copy(alpha = 0.3f))
                        .border(1.dp, NeonLime, RoundedCornerShape(8.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    Text("آخر مشاهدة", color = NeonLime, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                }
            }
            if (video.episodeNumber > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.7f))
                        .border(1.dp, NeonCyan.copy(alpha = 0.6f), CircleShape)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    Text("ح${video.episodeNumber}", color = NeonCyan, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Column(Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(8.dp)) {
            Text(video.title, color = Color.White, fontSize = if (isFeatured) 12.sp else 10.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (video.duration.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, null, tint = NeonCyan.copy(alpha = 0.8f), modifier = Modifier.size(9.dp))
                    Text(video.duration, color = NeonCyan.copy(alpha = 0.9f), fontSize = 8.sp, fontWeight = FontWeight.Medium)
                }
            }
            if (video.progress > 0f) {
                Spacer(Modifier.height(4.dp))
                Box(Modifier.fillMaxWidth().height(3.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.15f))) {
                    Box(
                        Modifier.fillMaxWidth(video.progress.coerceIn(0f, 1f)).height(3.dp).clip(CircleShape).background(
                            Brush.horizontalGradient(if (isLastWatched) listOf(NeonLime, NeonCyan) else listOf(NeonCyan, NeonMagenta)),
                        ),
                    )
                }
            }
        }
        if (isFeatured) {
            Box(
                modifier = Modifier.align(Alignment.Center).size(36.dp).clip(CircleShape)
                    .background(NeonLime.copy(alpha = 0.25f)).border(1.5.dp, NeonLime, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.PlayArrow, null, tint = NeonLime, modifier = Modifier.size(20.dp))
            }
        }
    }
}
