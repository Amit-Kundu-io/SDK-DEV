package com.amit_kundu_io.voicevault_sdk.audio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File

class AudioRecorderImpl(
    private val context: Context,
    private val fileManager: AudioFileManager,
    private val maxDurationMs: Int = 300_000,
    private val maxFileSizeBytes: Long = 50L * 1024 * 1024
) : AudioRecorder {

    private val mutex = Mutex()
    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null

    //  Scope lives for SDK lifetime, not per recording
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var timerJob: Job? = null

    private val _isRecording = MutableStateFlow(false)
    override val isRecordingFlow: StateFlow<Boolean> get() = _isRecording

    private val _recordingTime = MutableStateFlow(0L)
    override val recordingTime: StateFlow<Long> get() = _recordingTime

    override suspend fun startRecording() {
        mutex.withLock {
            if (recorder != null) return

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED
            ) {
                throw SecurityException("RECORD_AUDIO permission not granted")
            }

            outputFile = fileManager.createRecordingFile()
                ?: throw IllegalStateException("Failed to create output file")

            _recordingTime.value = 0L

            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128_000)
                setAudioSamplingRate(44_100)
                setOutputFile(outputFile!!.absolutePath)

                setMaxDuration(maxDurationMs)
                setMaxFileSize(maxFileSizeBytes)

                setOnInfoListener { _, what, _ ->
                    when (what) {
                        MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED,
                        MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED -> {
                            scope.launch {
                                mutex.withLock { stopInternal() }
                            }
                        }
                    }
                }

                try {
                    prepare()
                    start()
                } catch (e: Exception) {
                    release()
                    recorder = null
                    throw IllegalStateException("Recorder start failed", e)
                }
            }

            _isRecording.value = true
            startTimer()
        }
    }

    override suspend fun pauseRecording() {
        mutex.withLock {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                recorder?.pause()
                stopTimer()
            }
        }
    }

    override suspend fun resumeRecording() {
        mutex.withLock {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                recorder?.resume()
                startTimer()
            }
        }
    }

    override suspend fun stopRecording(): File {
        return mutex.withLock {
            val file = outputFile ?: error("No active recording.")
            stopInternal()
            file
        }
    }

    override fun isRecording(): Boolean = _isRecording.value

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = scope.launch {
            while (isActive && _isRecording.value) {
                delay(1000)
                _recordingTime.value += 1000
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private suspend fun stopInternal() {
        val current = recorder
        recorder = null
        stopTimer()
        _isRecording.value = false

        runCatching {
            current?.apply {
                try {
                    stop()
                } catch (e: RuntimeException) {
                    println("Recorder stop failed: ${e.message}")
                } finally {
                    reset()
                    release()
                }
            }
        }

        // Clear file reference after stop
        outputFile = null
    }

    //  Only call this at SDK shutdown, not per recording
    fun shutdown() {
        scope.cancel()
        recorder?.release()
        recorder = null
        outputFile = null
    }
}
