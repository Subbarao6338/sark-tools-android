package com.toolbox.audiotools

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.concurrent.thread
import kotlin.math.sin

@Composable
fun MorseAudioSynthScreen() {
    var textInput by remember { mutableStateOf("SOS") }
    var frequencyHz by remember { mutableFloatStateOf(700f) }
    var wpm by remember { mutableFloatStateOf(15f) }
    var isPlaying by remember { mutableStateOf(false) }

    fun playBeep(durationMs: Int, freq: Float) {
        val sampleRate = 44100
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val angle = 2.0 * Math.PI * i * freq / sampleRate
            buffer[i] = (sin(angle) * Short.MAX_VALUE * 0.7).toInt().toShort()
        }
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_MEDIA).build())
            .setAudioFormat(AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT).setSampleRate(sampleRate).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build())
            .setBufferSizeInBytes(buffer.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()
        audioTrack.write(buffer, 0, buffer.size)
        audioTrack.play()
        Thread.sleep(durationMs.toLong())
        audioTrack.release()
    }

    val morseMap = mapOf(
        'S' to "...", 'O' to "---", 'A' to ".-", 'B' to "-...", 'C' to "-.-.",
        'D' to "-..", 'E' to ".", 'F' to "..-.", 'G' to "--.", 'H' to "....",
        'I' to "..", 'J' to ".---", 'K' to "-.-", 'L' to ".-..", 'M' to "--",
        'N' to "-.", 'P' to ".--.", 'Q' to "--.-", 'R' to ".-.", 'T' to "-",
        'U' to "..-", 'V' to "...-", 'W' to ".--", 'X' to "-..-", 'Y' to "-.--",
        'Z' to "--..", '1' to ".----", '2' to "..---", '3' to "...--", '4' to "....-",
        '5' to ".....", '6' to "-....", '7' to "--...", '8' to "---..", '9' to "----.",
        '0' to "-----"
    )

    val morseRepresentation = textInput.uppercase().map { morseMap[it] ?: " " }.joinToString(" ")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Morse Code Audio Synthesizer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Synthesize crisp sinusoidal audio beeps with customizable audio frequency and transmission speed.")
            }
        }

        OutlinedTextField(
            value = textInput,
            onValueChange = { textInput = it },
            label = { Text("Text to Transmit") },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Morse Sequence: $morseRepresentation", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

        Text("Tone Frequency: ${frequencyHz.toInt()} Hz", fontWeight = FontWeight.SemiBold)
        Slider(value = frequencyHz, onValueChange = { frequencyHz = it }, valueRange = 400f..1200f)

        Text("Speed: ${wpm.toInt()} WPM", fontWeight = FontWeight.SemiBold)
        Slider(value = wpm, onValueChange = { wpm = it }, valueRange = 5f..35f)

        Button(
            onClick = {
                if (isPlaying) return@Button
                isPlaying = true
                thread {
                    try {
                        val dotMs = (1200 / wpm).toInt()
                        val dashMs = dotMs * 3
                        for (ch in morseRepresentation) {
                            when (ch) {
                                '.' -> {
                                    playBeep(dotMs, frequencyHz)
                                    Thread.sleep(dotMs.toLong())
                                }
                                '-' -> {
                                    playBeep(dashMs, frequencyHz)
                                    Thread.sleep(dotMs.toLong())
                                }
                                ' ' -> Thread.sleep((dotMs * 3).toLong())
                            }
                        }
                    } catch (_: Exception) {}
                    isPlaying = false
                }
            },
            enabled = !isPlaying,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isPlaying) "Playing Transmission..." else "Play Morse Tone Audio")
        }
    }
}
