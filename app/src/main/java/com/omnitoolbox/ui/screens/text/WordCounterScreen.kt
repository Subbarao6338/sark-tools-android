package omni.toolbox.ui.screens.text

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import java.util.Locale

@Composable
fun WordCounterScreen(navController: NavHostController, title: String = "Word Counter", initialText: String = "") {
    var text by remember { mutableStateOf(initialText) }

    val stats = remember(text) {
        TextAnalysisUtils.calculateTextStats(text)
    }

    ToolScreen(title = title, onBack = { navController.popBackStack() }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                label = { Text("Input Text") },
                placeholder = { Text("Paste or type your text here...") }
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Text Metrics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    CounterRow("Words", stats.wordCount.toString())
                    CounterRow("Characters", stats.charCount.toString())
                    CounterRow("Characters (no whitespace)", stats.charCountNoSpaces.toString())
                    CounterRow("Sentences", stats.sentenceCount.toString())
                    CounterRow("Paragraphs", stats.paragraphCount.toString())

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    Text("Time Estimations", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    CounterRow("Reading Time (~200 wpm)", formatDuration(stats.readingTimeMinutes))
                    CounterRow("Speaking Time (~130 wpm)", formatDuration(stats.speakingTimeMinutes))
                }
            }
        }
    }
}

private fun formatDuration(minutes: Double): String {
    val totalSeconds = (minutes * 60).toInt()
    if (totalSeconds < 60) {
        return "$totalSeconds sec"
    }
    val min = totalSeconds / 60
    val sec = totalSeconds % 60
    return if (sec == 0) "$min min" else String.format(Locale.getDefault(), "%d min %d sec", min, sec)
}

@Composable
private fun CounterRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}
