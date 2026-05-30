package com.amit_kundu_io.sdkdev

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.amit_kundu_io.sdkdev.ui.theme.SDKDevTheme
import com.amit_kundu_io.voicevault_sdk.audio.PlaybackState
import com.amit_kundu_io.voicevault_sdk.voice.VoiceVault
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val instance = VoiceVault
        setContent {
            SDKDevTheme {
                var name by remember { mutableStateOf("Ai Stop") }

                var file by remember { mutableStateOf<File?>(null) }

                var isPaused by remember { mutableStateOf(false) }

                val playbackState by VoiceVault
                    .playbackState
                    .collectAsStateWithLifecycle()

                val playbackTime by VoiceVault
                    .playbackTime
                    .collectAsStateWithLifecycle()


                val isRecording by VoiceVault
                    .isRecording
                    .collectAsStateWithLifecycle()

                val time by
                VoiceVault
                    .recordingTime
                    .collectAsStateWithLifecycle()

                val cc = rememberCoroutineScope()


                val playedFraction =
                    (playbackTime.time.toFloat() /
                            playbackTime.totalTime
                                .coerceAtLeast(1L)
                                .toFloat())
                        .coerceIn(0f, 1f)

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    innerPadding
                    //VoiceVaultDemoUI()

                    AudioRecorderScreen(
                        playbackState = playbackState,
                        isRecording = isRecording,
                        recordingTimeMs = time,
                        recordedFile = file,

                         playedFraction =playedFraction,

                        onStartRecording = {
                            cc.launch {
                                VoiceVault.startRecording()
                            }

                        },
                        onPauseRecording = {
                            cc.launch {
                                VoiceVault.pauseRecording()
                            }

                        },
                        onResumeRecording = {
                            cc.launch {
                                VoiceVault.resumeRecording()
                            }

                        },
                        onStopRecording = {
                            cc.launch {
                                file = VoiceVault.stopRecording()
                            }

                        },
                        onPlay = {
                            cc.launch {
                                file?.let { it1 -> VoiceVault.play(it1) }
                            }

                        },
                        onPausePlayback = {
                            cc.launch {
                                VoiceVault.pausePlayback()
                            }

                        },
                        onResumePlayback = {
                            cc.launch {
                                VoiceVault.resumePlayback()
                            }

                        },
                        onStopPlayback = {
                            cc.launch {
                                VoiceVault.stopRecording()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(
    name: String, modifier: Modifier = Modifier,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onPlayClick: () -> Unit,
    onPausedClick: () -> Unit,
    playbackState: PlaybackState,
    time: Long,
    isRecording: Boolean,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {


        if (isRecording) {
            Text(
                formatTime(time)
            )
        } else {
            when (playbackState) {

                PlaybackState.Playing -> {
                    Text("🔊 Playing")
                }

                PlaybackState.Paused -> {
                    Text("⏸ Paused")
                }

                PlaybackState.Completed -> {
                    Text("✅ Completed")
                }

                else -> {}
            }
        }

        Button(
            onClick = onStartClick
        ) {
            Text("Start Record")
        }

        Button(
            onClick = onPausedClick
        ) {
            Text("Paused Record")
        }

        Button(
            onClick = onStopClick
        ) {
            Text("Stop Record")
        }

        Button(
            onClick = onPlayClick
        ) {
            Text("Play Record")
        }

    }
}

