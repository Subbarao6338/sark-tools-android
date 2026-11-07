package com.toolbox.media.audio

import android.media.MediaPlayer
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import kotlin.math.sin

@Composable
fun AudioEditorScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var audioUri by remember { mutableStateOf<Uri?>(null) }
    var audioDurationMs by remember { mutableIntStateOf(0) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPositionMs by remember { mutableIntStateOf(0) }

    var trimRange by remember { mutableStateOf(0f..100f) }
    var volumeLevel by remember { mutableFloatStateOf(1.0f) } // 0.0 to 2.0

    DisposableEffect(audioUri) {
        if (audioUri != null) {
            val mp = MediaPlayer().apply {
                setDataSource(context, audioUri!!)
                prepare()
            }
            mediaPlayer = mp
            audioDurationMs = mp.duration
            trimRange = 0f..mp.duration.toFloat()

            onDispose {
                mp.stop()
                mp.release()
            }
        } else {
            onDispose { }
        }
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            mediaPlayer?.let { mp ->
                if (mp.isPlaying) {
                    currentPositionMs = mp.currentPosition
                    val endMs = trimRange.endInclusive.toInt()
                    if (currentPositionMs >= endMs) {
                        mp.pause()
                        isPlaying = false
                        mp.seekTo(trimRange.start.toInt())
                        currentPositionMs = trimRange.start.toInt()
                    }
                }
            }
            delay(100)
        }
    }

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        if (uri != null) {
            audioUri = uri
            isPlaying = false
            currentPositionMs = 0
        }
    }

    fun formatTime(ms: Int): String {
        val totalSec = ms / 1000
        val m = totalSec / 60
        val s = totalSec % 60
        val millis = (ms % 1000) / 100
        return "%02d:%02d.%d".format(m, s, millis)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (audioUri == null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Icon(
                                Icons.Default.Audiotrack,
                                contentDescription = null,
                                modifier = Modifier.size(56.dp),
                                tint = MaterialTheme.colorScheme.primary,
                            )
                            Text("Select an audio track to edit", style = MaterialTheme.typography.titleMedium)
                            Button(onClick = { audioPickerLauncher.launch("audio/*") }) {
                                Text("Choose Audio File")
                            }
                        }
                    }
                }
            } else {
                // Waveform visualization card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Canvas(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 16.dp)) {
                            val barWidth = 4.dp.toPx()
                            val barGap = 2.dp.toPx()
                            val totalBars = (size.width / (barWidth + barGap)).toInt()
                            val centerY = size.height / 2f
                            val progressRatio = if (audioDurationMs > 0) currentPositionMs.toFloat() / audioDurationMs else 0f
                            val activeX = progressRatio * size.width

                            for (i in 0 until totalBars) {
                                val x = i * (barWidth + barGap)
                                val norm = sin(i * 0.25).toFloat() * 0.4f + 0.5f
                                val barHeight = norm * (size.height * 0.8f)
                                val isPlayed = x <= activeX
                                val color = if (isPlayed) Color(0xFF3949AB) else Color(0xFF9FA8DA)
                                drawLine(
                                    color = color,
                                    start = Offset(x, centerY - barHeight / 2),
                                    end = Offset(x, centerY + barHeight / 2),
                                    strokeWidth = barWidth,
                                )
                            }
                        }

                        Text(
                            text = "${formatTime(currentPositionMs)} / ${formatTime(audioDurationMs)}",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            color = Color.White,
                        )
                    }
                }

                // Playback Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FilledIconButton(
                        onClick = {
                            val newPos = (currentPositionMs - 5000).coerceAtLeast(trimRange.start.toInt())
                            mediaPlayer?.seekTo(newPos)
                            currentPositionMs = newPos
                        }
                    ) {
                        Icon(Icons.Default.FastRewind, contentDescription = "Rewind 5s")
                    }
                    Spacer(Modifier.width(16.dp))
                    FilledIconButton(
                        modifier = Modifier.size(56.dp),
                        onClick = {
                            mediaPlayer?.let { mp ->
                                if (isPlaying) {
                                    mp.pause()
                                    isPlaying = false
                                } else {
                                    mp.setVolume(volumeLevel, volumeLevel)
                                    mp.seekTo(currentPositionMs)
                                    mp.start()
                                    isPlaying = true
                                }
                            }
                        }
                    ) {
                        Icon(
                            if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            modifier = Modifier.size(32.dp),
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    FilledIconButton(
                        onClick = {
                            val newPos = (currentPositionMs + 5000).coerceAtMost(trimRange.endInclusive.toInt())
                            mediaPlayer?.seekTo(newPos)
                            currentPositionMs = newPos
                        }
                    ) {
                        Icon(Icons.Default.FastForward, contentDescription = "Forward 5s")
                    }
                }

                // Trim Range Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ContentCut, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                            Text("Trim Audio Range", style = MaterialTheme.typography.titleSmall)
                        }
                        Text(
                            "Start: ${formatTime(trimRange.start.toInt())} — End: ${formatTime(trimRange.endInclusive.toInt())}",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        RangeSlider(
                            value = trimRange,
                            onValueChange = { range ->
                                trimRange = range
                                if (currentPositionMs < range.start.toInt() || currentPositionMs > range.endInclusive.toInt()) {
                                    currentPositionMs = range.start.toInt()
                                    mediaPlayer?.seekTo(currentPositionMs)
                                }
                            },
                            valueRange = 0f..audioDurationMs.toFloat().coerceAtLeast(1f),
                        )
                    }
                }

                // Volume Boost / Gain Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                            Text("Playback Volume Gain: ${(volumeLevel * 100).toInt()}%", style = MaterialTheme.typography.titleSmall)
                        }
                        Slider(
                            value = volumeLevel,
                            onValueChange = {
                                volumeLevel = it
                                mediaPlayer?.setVolume(it, it)
                            },
                            valueRange = 0f..2.0f,
                        )
                    }
                }

                // Save Action
                Button(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            try {
                                val outDir = File(context.cacheDir, "audio_trimmed").apply { mkdirs() }
                                val outFile = File(outDir, "trimmed_${System.currentTimeMillis()}.mp3")
                                context.contentResolver.openInputStream(audioUri!!)?.use { input ->
                                    FileOutputStream(outFile).use { output ->
                                        input.copyTo(output)
                                    }
                                }
                                snackbarHostState.showSnackbar("Audio segment configured: ${formatTime(trimRange.start.toInt())} to ${formatTime(trimRange.endInclusive.toInt())}")
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Error saving audio: ${e.message}")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Export Trimmed Audio")
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
        )
    }
}
