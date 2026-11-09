package com.toolbox.everyday.randomsuite

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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toolbox.core.ui.copyToClipboard
import java.security.SecureRandom

@Composable
fun PassphraseGeneratorScreen() {
    val context = LocalContext.current
    var wordCount by rememberSaveable { mutableFloatStateOf(4f) }
    var separator by rememberSaveable { mutableStateOf("-") }
    var capitalize by rememberSaveable { mutableStateOf(true) }
    var includeNumber by rememberSaveable { mutableStateOf(true) }
    var generatedPassphrase by rememberSaveable { mutableStateOf("") }

    val wordList = listOf(
        "correct", "horse", "battery", "staple", "apple", "river", "mountain", "cloud",
        "forest", "winter", "summer", "ocean", "silver", "golden", "shadow", "planet",
        "galaxy", "nebula", "guitar", "crystal", "dragon", "falcon", "tiger", "beacon",
        "harbor", "anchor", "meadow", "canyon", "velvet", "whisper", "thunder", "aurora",
        "breeze", "castle", "desert", "echo", "feather", "glacier", "horizon", "island",
        "jungle", "lantern", "miracle", "oasis", "puzzle", "quartz", "riddle", "safari",
        "timber", "umbrella", "valley", "voyage", "walnut", "zenith", "amber", "bronze"
    )

    fun generate(): String {
        val rng = SecureRandom()
        val count = wordCount.toInt()
        val words = (1..count).map {
            var w = wordList[rng.nextInt(wordList.size)]
            if (capitalize) w = w.replaceFirstChar { it.uppercase() }
            w
        }.toMutableList()

        if (includeNumber) {
            val num = rng.nextInt(10, 99)
            words[words.lastIndex] = words.last() + num
        }
        return words.joinToString(separator)
    }

    if (generatedPassphrase.isEmpty()) {
        generatedPassphrase = generate()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Passphrase Options", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text("Word Count: ${wordCount.toInt()}")
                Slider(
                    value = wordCount,
                    onValueChange = { wordCount = it },
                    valueRange = 3f..8f,
                    steps = 4,
                )

                Text("Separator:")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    listOf("-" to "Hyphen (-)", "." to "Dot (.)", " " to "Space ( )", "_" to "Underscore (_)").forEach { (sep, label) ->
                        FilterChip(
                            selected = separator == sep,
                            onClick = { separator = sep },
                            label = { Text(label) },
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = capitalize, onCheckedChange = { capitalize = it })
                        Text("Capitalize")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = includeNumber, onCheckedChange = { includeNumber = it })
                        Text("Append Number")
                    }
                }

                Button(
                    onClick = { generatedPassphrase = generate() },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Generate New Passphrase")
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        ) {
            Column(
                modifier = Modifier.padding(20.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text("Generated Passphrase", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text(
                    generatedPassphrase,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )

                val bits = (wordCount.toInt() * 12.9 + if (includeNumber) 6.6 else 0.0).toInt()
                Text("Estimated Entropy: ~$bits bits (Very Strong)", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)

                OutlinedButton(onClick = { copyToClipboard(context, "Passphrase", generatedPassphrase) }) {
                    Text("Copy Passphrase")
                }
            }
        }
    }
}
