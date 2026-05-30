/**
 * Audiorecorderscreen.kt
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


import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.amit_kundu_io.voicevault_sdk.audio.PlaybackState
import java.io.File

// ─────────────────────────────────────────────────────────────────────────────
// Colour tokens
// ─────────────────────────────────────────────────────────────────────────────
private val BgDeep      = Color(0xFF0B0E14)
private val BgCard      = Color(0xFF141820)
private val BgElevated  = Color(0xFF1C2230)
private val AccentRed   = Color(0xFFFF3B55)
private val AccentTeal  = Color(0xFF00D4AA)
private val AccentAmber = Color(0xFFFFB830)
private val TextPrimary = Color(0xFFF0F4FF)
private val TextMuted   = Color(0xFF6B7A99)
private val Divider     = Color(0xFF232B3E)

// ─────────────────────────────────────────────────────────────────────────────
// Waveform painter (static decorative bars)
// ─────────────────────────────────────────────────────────────────────────────
private val fakeWaveHeights = listOf(
    0.30f,0.55f,0.40f,0.70f,0.85f,0.60f,0.45f,0.90f,0.65f,0.50f,
    0.75f,0.35f,0.80f,0.55f,0.40f,0.95f,0.60f,0.45f,0.70f,0.55f,
    0.30f,0.65f,0.80f,0.50f,0.40f,0.75f,0.60f,0.85f,0.35f,0.55f,
    0.70f,0.45f,0.90f,0.60f,0.50f,0.80f,0.65f,0.40f,0.75f,0.55f,
    0.30f,0.60f,0.85f,0.50f,0.70f,0.45f,0.90f,0.35f,0.65f,0.80f
)

@Composable
private fun WaveformBar(
    heights: List<Float>,
    playedFraction: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val barWidth = (size.width / heights.size) * 0.55f
        val gap      = (size.width / heights.size) * 0.45f
        val playedX  = size.width * playedFraction

        heights.forEachIndexed { i, h ->
            val x      = i * (barWidth + gap)
            val barH   = size.height * h
            val top    = (size.height - barH) / 2f
            val color  = if (x + barWidth < playedX) AccentTeal else Color(0xFF2A3347)
            drawRoundRect(
                color       = color,
                topLeft     = Offset(x, top),
                size        = Size(barWidth, barH),
                cornerRadius= CornerRadius(barWidth / 2)
            )
        }
        // playhead
        if (playedFraction > 0f && playedFraction < 1f) {
            drawLine(
                color       = AccentTeal,
                start       = Offset(playedX, 0f),
                end         = Offset(playedX, size.height),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Animated recording ring
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun PulsingRing(active: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue  = if (active) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue  = if (active) 0.0f else 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(96.dp)
    ) {
        if (active) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .scale(scale)
                    .alpha(alpha)
                    .background(AccentRed.copy(alpha = 0.25f), CircleShape)
            )
        }
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(
                    if (active) AccentRed else BgElevated,
                    CircleShape
                )
                .border(2.dp, if (active) AccentRed else Divider, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Mic icon (drawn with canvas)
            Canvas(Modifier.size(28.dp)) {
                val w = size.width; val h = size.height
                // Body
                drawRoundRect(
                    color = TextPrimary,
                    topLeft = Offset(w * 0.32f, 0f),
                    size = Size(w * 0.36f, h * 0.58f),
                    cornerRadius = CornerRadius(w * 0.18f)
                )
                // Arc
                drawArc(
                    color = TextPrimary,
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(w * 0.12f, h * 0.30f),
                    size = Size(w * 0.76f, h * 0.50f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5.dp.toPx())
                )
                // Stand line
                drawLine(
                    color = TextPrimary,
                    start = Offset(w * 0.50f, h * 0.76f),
                    end = Offset(w * 0.50f, h),
                    strokeWidth = 2.5.dp.toPx()
                )
                drawLine(
                    color = TextPrimary,
                    start = Offset(w * 0.28f, h),
                    end = Offset(w * 0.72f, h),
                    strokeWidth = 2.5.dp.toPx()
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Icon button helper
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun CircleIconBtn(
    size: Dp = 48.dp,
    bg: Color = BgElevated,
    border: Color = Divider,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size)
            .background(bg, CircleShape)
            .border(1.dp, border, CircleShape)
            .clickable(onClick = onClick),
        content = content
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Timer formatter
// ─────────────────────────────────────────────────────────────────────────────
private fun Long.toTimestamp(): String {
    val s = this / 1000
    return "%02d:%02d".format(s / 60, s % 60)
}

// ─────────────────────────────────────────────────────────────────────────────
// Main Screen
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun AudioRecorderScreen(
    // ── State ──────────────────────────────────────────────
    playbackState  : PlaybackState,   // your StateFlow value collected by caller
    isRecording    : Boolean,         // graph.recordingRepository.isRecording
    recordingTimeMs: Long,            // graph.recordingRepository.recordingTime
    recordedFile   : File?,           // file produced after stopRecording()
    playedFraction : Float = 0f,      // 0..1 from your playback progress source

    // ── Actions ─────────────────────────────────────────────
    onStartRecording  : () -> Unit,
    onPauseRecording  : () -> Unit,
    onResumeRecording : () -> Unit,
    onStopRecording   : () -> Unit,
    onPlay            : (File) -> Unit,
    onPausePlayback   : () -> Unit,
    onResumePlayback  : () -> Unit,
    onStopPlayback    : () -> Unit,
) {
    // Recording FSM
    var recordingPaused by remember { mutableStateOf(false) }

    // Playback helpers
    val isPlaying = playbackState == PlaybackState.Playing
    val isPausedPb = playbackState == PlaybackState.Paused

    Surface(
        modifier = Modifier.fillMaxSize(),
        color    = BgDeep
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(36.dp))

            // ── Header ──────────────────────────────────────────
            Text(
                text  = "RECORDER",
                color = TextMuted,
                fontSize   = 11.sp,
                fontWeight = FontWeight.W600,
                letterSpacing = 4.sp
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text  = "Audio Studio",
                color = TextPrimary,
                fontSize   = 28.sp,
                fontWeight = FontWeight.W700,
                letterSpacing = (-0.5).sp
            )

            Spacer(Modifier.height(40.dp))

            // ── Recording timer ──────────────────────────────────
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(BgCard, RoundedCornerShape(20.dp))
                    .border(1.dp, Divider, RoundedCornerShape(20.dp))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isRecording) {
                        // blinking dot
                        val blink by rememberInfiniteTransition(label = "blink")
                            .animateFloat(
                                initialValue = 1f, targetValue = 0f,
                                animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
                                label = "blink"
                            )
                        Box(
                            Modifier
                                .size(8.dp)
                                .alpha(if (recordingPaused) 0.3f else blink)
                                .background(AccentRed, CircleShape)
                        )
                        Spacer(Modifier.width(10.dp))
                    }
                    Text(
                        text  = recordingTimeMs.toTimestamp(),
                        color = TextPrimary,
                        fontSize   = 48.sp,
                        fontWeight = FontWeight.W300,
                        fontFamily = FontFamily.Monospace
                    )
                }
                if (!isRecording && recordingTimeMs == 0L) {
                    Text(
                        text  = "00:00",
                        color = TextMuted.copy(alpha = 0.4f),
                        fontSize   = 48.sp,
                        fontWeight = FontWeight.W300,
                        fontFamily = FontFamily.Monospace
                    )
                }
                // Status label
                Text(
                    text = when {
                        recordingPaused -> "PAUSED"
                        isRecording     -> "REC"
                        else            -> "READY"
                    },
                    color = when {
                        recordingPaused -> AccentAmber
                        isRecording     -> AccentRed
                        else            -> TextMuted
                    },
                    fontSize   = 10.sp,
                    fontWeight = FontWeight.W700,
                    letterSpacing = 2.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                )
            }

            Spacer(Modifier.height(32.dp))

            // ── Mic + recording controls ─────────────────────────
            PulsingRing(active = isRecording && !recordingPaused)

            Spacer(Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when {
                    // Not started yet
                    !isRecording && !recordingPaused -> {
                        CircleIconBtn(
                            size   = 56.dp,
                            bg     = AccentRed,
                            border = AccentRed,
                            onClick = {
                                recordingPaused = false
                                onStartRecording()
                            }
                        ) {
                            // Play triangle
                            Canvas(Modifier.size(20.dp)) {
                                drawCircle(Color.White, radius = size.minDimension * 0.38f)
                            }
                            Text("●", color = Color.White, fontSize = 22.sp)
                        }
                    }

                    // Recording active
                    isRecording && !recordingPaused -> {
                        // Pause
                        CircleIconBtn(onClick = {
                            recordingPaused = true
                            onPauseRecording()
                        }) {
                            Text("⏸", color = TextPrimary, fontSize = 18.sp)
                        }
                        // Stop
                        CircleIconBtn(
                            size   = 56.dp,
                            bg     = AccentRed,
                            border = AccentRed,
                            onClick = {
                                recordingPaused = false
                                onStopRecording()
                            }
                        ) {
                            Box(
                                Modifier
                                    .size(18.dp)
                                    .background(Color.White, RoundedCornerShape(4.dp))
                            )
                        }
                    }

                    // Recording paused
                    recordingPaused -> {
                        // Resume
                        CircleIconBtn(onClick = {
                            recordingPaused = false
                            onResumeRecording()
                        }) {
                            Text("▶", color = AccentTeal, fontSize = 18.sp)
                        }
                        // Stop
                        CircleIconBtn(
                            size   = 56.dp,
                            bg     = AccentRed,
                            border = AccentRed,
                            onClick = {
                                recordingPaused = false
                                onStopRecording()
                            }
                        ) {
                            Box(
                                Modifier
                                    .size(18.dp)
                                    .background(Color.White, RoundedCornerShape(4.dp))
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(40.dp))

            // ── Player section (only shown when a file exists) ───
            recordedFile?.let { file ->
                HorizontalDivider(color = Divider)
                Spacer(Modifier.height(28.dp))

                Text(
                    text  = "PLAYBACK",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.W600,
                    letterSpacing = 4.sp
                )
                Spacer(Modifier.height(16.dp))

                // File info chip
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BgCard, RoundedCornerShape(12.dp))
                        .border(1.dp, Divider, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Box(
                        Modifier
                            .size(36.dp)
                            .background(AccentTeal.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("♪", color = AccentTeal, fontSize = 16.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = file.name,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.W600,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "%.1f KB".format(file.length() / 1024f),
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                val animatedPlayedFraction by animateFloatAsState(
                    targetValue = playedFraction,
                    animationSpec = tween(
                        durationMillis = 1009,
                        easing = LinearEasing
                    ),
                    label = "wave_progress"
                )

                WaveformBar(
                    heights = fakeWaveHeights,
                    playedFraction = animatedPlayedFraction,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                )


                Spacer(Modifier.height(24.dp))

                // Playback controls
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Stop playback
                    CircleIconBtn(onClick = onStopPlayback) {
                        Box(
                            Modifier
                                .size(16.dp)
                                .background(TextMuted, RoundedCornerShape(3.dp))
                        )
                    }

                    when {
                        isPlaying -> {
                            CircleIconBtn(
                                size   = 64.dp,
                                bg     = AccentTeal,
                                border = AccentTeal,
                                onClick = onPausePlayback
                            ) {
                                Text("⏸", color = BgDeep, fontSize = 22.sp)
                            }
                        }
                        isPausedPb -> {
                            CircleIconBtn(
                                size   = 64.dp,
                                bg     = AccentTeal,
                                border = AccentTeal,
                                onClick = onResumePlayback
                            ) {
                                Text("▶", color = BgDeep, fontSize = 22.sp)
                            }
                        }
                        else -> {
                            CircleIconBtn(
                                size   = 64.dp,
                                bg     = AccentTeal,
                                border = AccentTeal,
                                onClick = { onPlay(file) }
                            ) {
                                Text("▶", color = BgDeep, fontSize = 22.sp)
                            }
                        }
                    }

                    // Placeholder for future seek-fwd
                    CircleIconBtn(onClick = {}) {
                        Text("↺", color = TextMuted, fontSize = 20.sp)
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = when {
                        isPlaying  -> "Playing…"
                        isPausedPb -> "Paused"
                        else       -> "Tap ▶ to play"
                    },
                    color    = TextMuted,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}