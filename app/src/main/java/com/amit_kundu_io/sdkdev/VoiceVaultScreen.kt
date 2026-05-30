/**
 * VoiceVaultScreen.kt
 *
 * Author      : Amit Kundu
 * Created On  : 31/05/2026
 *
 * Description :
 * Part of the project codebase. This file contributes to the overall
 * functionality and follows standard coding practices and architecture.
 *
 * Notes :
 * Ensure changes are consistent with project guidelines and maintain
 * code readability and quality.
 */

package com.amit_kundu_io.sdkdev

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.amit_kundu_io.voicevault_sdk.audio.PlaybackState
import com.amit_kundu_io.voicevault_sdk.voice.VoiceVault
import com.amit_kundu_io.voicevault_sdk.voice.VoiceVault.stopPlayback
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun VoiceVaultDemoUI() {

    val scope =
        rememberCoroutineScope()

    var file by remember {

        mutableStateOf<File?>(
            null
        )
    }

    val isRecording by
    VoiceVault
        .isRecording
        .collectAsStateWithLifecycle()

    val playbackState by
    VoiceVault
        .playbackState
        .collectAsStateWithLifecycle()

    val recordingTime by
    VoiceVault
        .recordingTime
        .collectAsStateWithLifecycle()

    val infinite =
        rememberInfiniteTransition(
            label = ""
        )

    val pulse by
    infinite.animateFloat(

        initialValue = 1f,

        targetValue = 1.25f,

        animationSpec =
            infiniteRepeatable(

                tween(
                    700
                ),

                RepeatMode.Reverse
            ),

        label = ""
    )

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {

        AnimatedVisibility(

            visible =
                isRecording
        ) {

            Icon(painter = painterResource(R.drawable.baseline_mic_24),
                null,
                tint = Color.Red,
                modifier = Modifier.size(80.dp * pulse)
            )
        }

        Spacer(
            Modifier.height(20.dp)
        )

        Text(

            text =
                formatTime(
                    recordingTime
                ),

            style =
                MaterialTheme
                    .typography
                    .displaySmall
        )

        Spacer(
            Modifier.height(16.dp)
        )

        when (
            playbackState
        ) {

            PlaybackState.Playing -> {

                Text(
                    "🔊 Playing"
                )
            }

            PlaybackState.Paused -> {

                Text(
                    "⏸ Playback Paused"
                )
            }

            PlaybackState.Completed -> {

                Text(
                    "✅ Playback Completed"
                )
            }

            else -> {}
        }

        Spacer(
            Modifier.height(32.dp)
        )

        Row(
            horizontalArrangement =
                Arrangement.spacedBy(
                    12.dp
                )
        ) {

            Button(

                onClick = {

                    scope.launch {

                        VoiceVault
                            .startRecording()
                    }
                }
            ) {

                Text("Start")
            }

            Button(

                onClick = {

                    scope.launch {

                        VoiceVault
                            .pauseRecording()
                    }
                }
            ) {

                Text("Pause Rec")
            }

            Button(

                onClick = {

                    scope.launch {

                        VoiceVault
                            .resumeRecording()
                    }
                }
            ) {

                Text("Resume Rec")
            }
        }

        Spacer(
            Modifier.height(12.dp)
        )

        Row(
            horizontalArrangement =
                Arrangement.spacedBy(
                    12.dp
                )
        ) {

            Button(

                onClick = {

                    scope.launch {

                        file =
                            VoiceVault
                                .stopRecording()
                    }
                }
            ) {

                Text("Stop Rec")
            }

            Button(

                onClick = {

                    scope.launch {

                        file?.let {

                            VoiceVault
                                .play(it)
                        }
                    }
                }
            ) {

                Text("Play")
            }
        }

        Spacer(
            Modifier.height(12.dp)
        )

        Row(
            horizontalArrangement =
                Arrangement.spacedBy(
                    12.dp
                )
        ) {

            Button(

                onClick = {

                    scope.launch {

                        VoiceVault
                            .pausePlayback()
                    }
                }
            ) {

                Text("Pause Play")
            }

            Button(

                onClick = {

                    scope.launch {

                        VoiceVault
                            .resumePlayback()
                    }
                }
            ) {

                Text("Resume Play")
            }

            Button(

                onClick = {

                    scope.launch {

                        stopPlayback()
                    }
                }
            ) {

                Text("Stop Play")
            }
        }
    }
}
fun formatTime(
    millis: Long
): String {

    val totalSeconds =
        millis / 1000

    val minutes =
        totalSeconds / 60

    val seconds =
        totalSeconds % 60

    return String.format(
        "%02d:%02d",
        minutes,
        seconds
    )
}