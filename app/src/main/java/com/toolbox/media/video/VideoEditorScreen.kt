package com.toolbox.media.video

import android.net.Uri
import android.widget.VideoView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.toolbox.core.media.MediaStoreWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun VideoEditorScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var videoUri by remember { mutableStateOf<Uri?>(null) }
    var videoDurationMs by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }

    var trimRange by remember { mutableStateOf(0f..100f) }
    var isMuted by remember { mutableStateOf(false) }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        if (uri != null) {
            videoUri = uri
            isPlaying = false
        }
    }

    fun formatDuration(ms: Int): String {
        val s = (ms / 1000) % 60
        val m = (ms / (1000 * 60)) % 60
        return "%02d:%02d".format(m, s)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (videoUri == null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Icon(
                                Icons.Default.Movie,
                                contentDescription = null,
                                modifier = Modifier.size(56.dp),
                                tint = MaterialTheme.colorScheme.primary,
                            )
                            Text("Select a video to edit", style = MaterialTheme.typography.titleMedium)
                            Button(onClick = { videoPickerLauncher.launch("video/*") }) {
                                Text("Choose Video")
                            }
                        }
                    }
                }
            } else {
                // Video Player Viewport
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.Black),
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AndroidView(
                            factory = { ctx ->
                                VideoView(ctx).apply {
                                    setVideoURI(videoUri)
                                    setOnPreparedListener { mp ->
                                        videoDurationMs = mp.duration
                                        trimRange = 0f..mp.duration.toFloat()
                                        if (isMuted) mp.setVolume(0f, 0f) else mp.setVolume(1f, 1f)
                                    }
                                    setOnCompletionListener {
                                        isPlaying = false
                                        seekTo(trimRange.start.toInt())
                                    }
                                    videoViewRef = this
                                }
                            },
                            update = { view ->
                                videoViewRef = view
                            },
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }

                // Playback and change video
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Button(
                        onClick = {
                            videoViewRef?.let { vv ->
                                if (isPlaying) {
                                    vv.pause()
                                    isPlaying = false
                                } else {
                                    vv.seekTo(trimRange.start.toInt())
                                    vv.start()
                                    isPlaying = true
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text(if (isPlaying) "Pause" else "Play Range")
                    }
                    OutlinedButton(
                        onClick = { videoPickerLauncher.launch("video/*") },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Change Video")
                    }
                }

                // Trimming controls
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ContentCut, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                            Text("Trim Video Clip", style = MaterialTheme.typography.titleSmall)
                        }
                        Text(
                            "Selected Range: ${formatDuration(trimRange.start.toInt())} — ${formatDuration(trimRange.endInclusive.toInt())} (Total: ${formatDuration(videoDurationMs)})",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        RangeSlider(
                            value = trimRange,
                            onValueChange = { range ->
                                trimRange = range
                                videoViewRef?.seekTo(range.start.toInt())
                            },
                            valueRange = 0f..videoDurationMs.toFloat().coerceAtLeast(1f),
                        )
                    }
                }

                // Audio configuration: Mute or keep audio
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                            Text("Audio Track", style = MaterialTheme.typography.bodyMedium)
                        }
                        FilterChip(
                            selected = isMuted,
                            onClick = { isMuted = !isMuted },
                            label = { Text(if (isMuted) "Muted" else "Original Audio") },
                        )
                    }
                }

                // Export and Save
                Button(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            try {
                                val res = MediaStoreWriter.saveVideo(context, videoUri!!, "Toolbox_Video", "TRIMMED_VID", "video/mp4")
                                snackbarHostState.showSnackbar("Video saved to ${res.displayPath}")
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Error saving video: ${e.message}")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Save / Export Video")
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
        )
    }
}
