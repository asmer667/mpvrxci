/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package app.gyrolet.mpvrx.ui.browser.folderlist

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.gyrolet.mpvrx.domain.media.model.Video
import app.gyrolet.mpvrx.domain.thumbnail.ThumbnailRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import kotlin.math.abs

/** Local-only, database-backed recently played carousel. Items are loaded lazily as they enter the row. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun CinematicRecentCarousel(
  videos: List<Video>,
  onVideoClick: (Video) -> Unit,
  modifier: Modifier = Modifier,
) {
  if (videos.isEmpty()) return
  val repository = koinInject<ThumbnailRepository>()
  val listState = rememberLazyListState()
  val snapBehavior = rememberSnapFlingBehavior(lazyListState = listState)
  val centeredIndex by remember {
    derivedStateOf {
      val visible = listState.layoutInfo.visibleItemsInfo
      if (visible.isEmpty()) 0 else {
        val center = (listState.layoutInfo.viewportStartOffset + listState.layoutInfo.viewportEndOffset) / 2
        visible.minByOrNull { abs((it.offset + it.size / 2) - center) }?.index ?: 0
      }
    }
  }

  Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Text(stringResource(app.gyrolet.mpvrx.R.string.ui_cinematic_recently_played), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
      Text(stringResource(app.gyrolet.mpvrx.R.string.ui_swipe_to_browse), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
    }
    LazyRow(
      state = listState,
      flingBehavior = snapBehavior,
      contentPadding = PaddingValues(horizontal = 42.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier.fillMaxWidth().height(224.dp),
    ) {
      itemsIndexed(videos, key = { _, video -> "recent-${video.id}-${video.path}" }) { index, video ->
        var bitmap by remember(video.path, video.dateModified, video.size) {
          mutableStateOf<Bitmap?>(repository.peekThumbnailFromMemory(video, 520, 292))
        }
        LaunchedEffect(video.path, video.dateModified, video.size) {
          if (bitmap == null) {
            bitmap = withContext(Dispatchers.IO) { repository.getThumbnail(video, 520, 292) }
          }
        }
        val distance = abs(index - centeredIndex).coerceAtMost(3)
        val cardShape = RoundedCornerShape(22.dp)
        Box(
          modifier = Modifier
            .width(244.dp)
            .height(210.dp)
            .graphicsLayer {
              val emphasis = 1f - distance * 0.075f
              scaleX = emphasis
              scaleY = emphasis
              rotationY = (index - centeredIndex).coerceIn(-2, 2) * -7f
              cameraDistance = 18f * density
              alpha = 1f - distance * 0.08f
            }
            .background(
              Brush.linearGradient(
                listOf(
                  MaterialTheme.colorScheme.secondary.copy(alpha = 0.58f),
                  MaterialTheme.colorScheme.primary.copy(alpha = 0.50f),
                  MaterialTheme.colorScheme.tertiary.copy(alpha = 0.42f),
                ),
              ),
              RoundedCornerShape(25.dp),
            )
            .padding(2.dp)
            .background(Color(0xFF111322), RoundedCornerShape(23.dp))
            .padding(3.dp),
        ) {
          Surface(
            onClick = { onVideoClick(video) },
            shape = cardShape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shadowElevation = if (index == centeredIndex) 16.dp else 5.dp,
            modifier = Modifier.fillMaxWidth().height(204.dp)
              .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.55f), cardShape),
          ) {
            Box {
              if (bitmap != null) {
                Image(
                  bitmap = bitmap!!.asImageBitmap(),
                  contentDescription = video.displayName,
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.matchParentSize(),
                )
              } else {
                Box(
                  Modifier.matchParentSize().background(
                    Brush.linearGradient(listOf(Color(0xFF292A49), Color(0xFF111322))),
                  ),
                )
              }
              Box(
                Modifier.matchParentSize().background(
                  Brush.verticalGradient(
                    0f to Color.Transparent,
                    0.42f to Color.Transparent,
                    1f to Color(0xE6000618),
                  ),
                ),
              )
              Column(
                modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(13.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
              ) {
                Text(
                  video.displayName,
                  color = Color.White,
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  maxLines = 2,
                  overflow = TextOverflow.Ellipsis,
                )
                Text(
                  video.durationFormatted,
                  color = Color.White.copy(alpha = 0.78f),
                  style = MaterialTheme.typography.labelSmall,
                )
              }
            }
          }
        }
      }
    }
  }
}
