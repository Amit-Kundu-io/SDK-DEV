package com.amit_kundu_io.sdkdev

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.amit_kundu_io.sdkdev.ui.theme.SDKDevTheme
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

                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        AudioRecorderScreen(
                            playbackState = playbackState,
                            isRecording = isRecording,
                            recordingTimeMs = time,
                            recordedFile = file,

                            playedFraction = playedFraction,

                            onStartRecording = {
                                cc.launch {
                                    VoiceVault.recorder.startRecording()
                                }

                            },
                            onPauseRecording = {
                                cc.launch {
                                    VoiceVault.recorder.pauseRecording()
                                }

                            },
                            onResumeRecording = {
                                cc.launch {
                                    VoiceVault.recorder.resumeRecording()
                                }

                            },
                            onStopRecording = {
                                cc.launch {
                                    file = VoiceVault.recorder.stopRecording()
                                }

                            },
                            onPlay = {
                                cc.launch {
                                    file?.let { it1 -> VoiceVault.player.play(it1) }
                                }

                            },
                            onPausePlayback = {
                                cc.launch {
                                    VoiceVault.player.pause()
                                }

                            },
                            onResumePlayback = {
                                cc.launch {
                                    VoiceVault.player.resume()
                                }

                            },
                            onStopPlayback = {
                                cc.launch {
                                    VoiceVault.player.stop()
                                }
                            }
                        )

                        FloatingActionButton(
                            modifier = Modifier
                                .align(
                                    Alignment.BottomEnd
                                )
                                .padding(bottom = 46.dp, end = 16.dp),
                            onClick = {
                                Log.d("FILE_UPLOADING", "onCreate: Click ${file?.absolutePath}")
                            file?.absolutePath?.let { VoiceVault.uploadFile("lkjfk44asfa4fs54fsa6f46a5",it,) }
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_cloud_upload_24),
                                null
                            )
                        }
                    }


                }
            }
        }
    }
}

