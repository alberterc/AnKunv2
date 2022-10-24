package com.radx.ankunv2.screens.videoplayer

import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.MimeTypes
import com.radx.ankunv2.R
import com.radx.ankunv2.anime.AnimeVideoStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AnimeVideoScreen(episodeID: String) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        var videoStreamUrl by remember { mutableStateOf("") }
        LaunchedEffect(Unit) {
            getEpisodeDetailsMap(episodeID = episodeID)
            videoStreamUrl = episodeDetailsMap["Direct-directhls"]!!
        }

        // video player
        if (videoStreamUrl != "") {
            VideoPlayer(videoUri = videoStreamUrl)
        }
    }
}

@Composable
fun VideoPlayer(modifier: Modifier = Modifier, videoUri: String) {
    val context = LocalContext.current

Log.e("URL", videoUri)
    // create video player
    val trackSelector = DefaultTrackSelector(context.applicationContext).apply {
        setParameters(buildUponParameters().setMaxVideoSizeSd())
    }

    val exoPlayer =
        ExoPlayer.Builder(context)
            .setTrackSelector(trackSelector)
            .setSeekBackIncrementMs(5000)
            .setSeekForwardIncrementMs(10000)
            .build()
            .apply {
                setMediaItem(MediaItem.fromUri(videoUri))
                prepare()
                playWhenReady = true
    }

    Box(modifier = Modifier) {
        DisposableEffect(
            AndroidView(
                factory = {
                    StyledPlayerView(context).apply {
                        player = exoPlayer
                        layoutParams =
                            FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                    }
                }
            )
        ) {
            onDispose { exoPlayer.release() }
        }
    }
}

//@Composable
//fun VideoPlayerControls(
//    modifier: Modifier = Modifier,
//    isPlaying: () -> Boolean,
//    onReplayClick: () -> Unit,
//    onPauseToggle: () -> Unit,
//    onForwardClick: () -> Unit
//) {
//    val isVideoPlaying = remember(isPlaying()) { isPlaying() }
//
//    // black overlay for video player
//    Box(modifier = Modifier.background(Color.Black.copy(alpha = 0.6f))) {
//        Row(
//            modifier = Modifier
//                .align(Alignment.Center)
//                .fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceEvenly
//        ) {
//            // replay button
//            IconButton(
//                modifier = Modifier.size(40.dp),
//                onClick = { onReplayClick }
//            ) {
//                Image(
//                    modifier = Modifier.fillMaxSize(),
//                    contentScale = ContentScale.Crop,
//                    painter = painterResource(id = R.drawable.ic_baseline_replay_5_24),
//                    contentDescription = "Replay 5 seconds"
//                )
//            }
//
//            // forward button
//            IconButton(
//                modifier = Modifier.size(40.dp),
//                onClick = { onForwardClick }
//            ) {
//                Image(
//                    modifier = Modifier.fillMaxSize(),
//                    contentScale = ContentScale.Crop,
//                    painter = painterResource(id = R.drawable.ic_baseline_forward_10_24),
//                    contentDescription = "Forward 10 seconds"
//                )
//            }
//
//            // play/pause button
//            IconButton(
//                modifier = Modifier.size(40.dp),
//                onClick = { onPauseToggle }
//            ) {
//                Image(
//                    modifier = Modifier.fillMaxSize(),
//                    contentScale = ContentScale.Crop,
//                    painter =
//                        if(isVideoPlaying) { painterResource(id = R.drawable.ic_baseline_play_arrow_24) }
//                        else { painterResource(id = R.drawable.ic_baseline_pause_24) },
//                    contentDescription = "Play/Pause"
//                )
//            }
//        }
//    }
//}

var episodeDetailsMap = mapOf<String, String>()
suspend fun getEpisodeDetailsMap(episodeID: String) = withContext(Dispatchers.IO) {
    fillEpisodeDetailsMap(episodeID = episodeID)
}
fun fillEpisodeDetailsMap(episodeID: String) {
    episodeDetailsMap = AnimeVideoStream.getEpisodeUrlsMap(episodeID = episodeID)
}