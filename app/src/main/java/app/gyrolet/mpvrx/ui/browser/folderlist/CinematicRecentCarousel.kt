/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * Cinematic Recent Carousel
 * يقبل List<Video> ويعرض بطاقة بارزة + بطاقات جانبية + شريط تقدم نيوني
 */

package app.gyrolet.mpvrx.ui.browser.folderlist

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.gyrolet.mpvrx.domain.media.model.Video

private val NeonCyan = Color(0xFF00F0FF)
private val NeonMagenta = Color(0xFFFF007A)
private val NeonPurple = Color(0xFF8A00FF)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CinematicRecentCarousel(
    videos: List<Video>,
    onVideoClick: (Video) -> Unit,
    modifier: Modifier = Modifier,
    containerTitle: String = "شاهدت مؤخراً",
) {
    if (videos.isEmpty()) return

    val listState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        NeonPurple.copy(alpha = 0.12f),
                        NeonCyan.copy(alpha = 0.06f),
                        Color.Black.copy(alpha = 0.5f),
                    ),
                ),
            )
            .border(
                1.dp,
                Brush.linearGradient(
                    listOf(
                        NeonCyan.copy(alpha = 0.4f),
                        NeonMagenta.copy(alpha = 0.3f),
                        Color.Transparent,
                    ),
                ),
                RoundedCornerShape(24.dp),
            )
            .padding(vertical = 12.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = containerTitle,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "${videos.size} عنصر",
                    color = NeonCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                state = listState,
                flingBehavior = flingBehavior,
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                itemsIndexed(videos, key = { _, v -> v.path }) { index, video ->
                    CinematicVideoCard(
                        video = video,
                        isFeatured = index == 0,
                        onClick = { onVideoClick(video) },
                    )
                }
            }
        }
    }
}

@Composable
private fun CinematicVideoCard(
    video: Video,
    isFeatured: Boolean,
    onClick: () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "card_pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            tween(1500, easing = FastOutSlowInEasing),
            RepeatMode.Reverse,
        ),
        label = "glow_alpha",
    )
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isFeatured) 1.02f else 1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "card_scale",
    )

    val cardWidth = if (isFeatured) 220.dp else 160.dp
    val cardHeight = if (isFeatured) 140.dp else 110.dp

    Box(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight)
            .scale(scale)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.Black.copy(alpha = 0.6f))
            .border(
                if (isFeatured) 2.dp else 1.dp,
                if (isFeatured) {
                    Brush.linearGradient(
                        listOf(
                            NeonCyan.copy(alpha = glowAlpha),
                            NeonMagenta.copy(alpha = glowAlpha),
                        ),
                    )
                } else {
                    Brush.linearGradient(
                        listOf(Color.White.copy(alpha = 0.2f), Color.Transparent),
                    )
                },
                RoundedCornerShape(18.dp),
            )
            .clickable(onClick = onClick),
    ) {
        // تدرج داكن أسفل
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(cardHeight)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f)),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(10.dp),
        ) {
            Text(
                text = video.displayName.ifBlank { video.name },
                color = Color.White,
                fontSize = if (isFeatured) 13.sp else 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = video.path.substringAfterLast("/").take(30),
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 9.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        // زر التشغيل للبطاقة البارزة
        if (isFeatured) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(NeonCyan.copy(alpha = 0.3f))
                    .border(1.5.dp, NeonCyan, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}
