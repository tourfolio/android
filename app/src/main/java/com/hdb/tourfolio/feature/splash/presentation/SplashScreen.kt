@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.splash.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.hdb.tourfolio.R
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Primary70

@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val currentOnFinished by rememberUpdatedState(onFinished)

    val player =
        remember {
            ExoPlayer.Builder(context).build().apply {
                setMediaItem(
                    MediaItem.fromUri(
                        "android.resource://${context.packageName}/${R.raw.splash_video}",
                    ),
                )
                volume = 0f
                repeatMode = Player.REPEAT_MODE_OFF
                prepare()
                playWhenReady = true
            }
        }

    DisposableEffect(player) {
        val listener =
            object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        currentOnFinished()
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    currentOnFinished()
                }
            }
        player.addListener(listener)

        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    Box(
        modifier = modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center,
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { viewContext ->
                PlayerView(viewContext).apply {
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    this.player = player
                }
            },
        )

        Text(
            text = "Tourfolio",
            style =
                LocalAppTypography.current.headlineLarge.heavy.copy(
                    color = Primary70,
                ),
        )
    }
}
