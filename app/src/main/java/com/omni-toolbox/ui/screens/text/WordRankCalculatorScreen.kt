package omni.toolbox.ui.screens.text

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import omni.toolbox.utils.WordRankCalculator
import java.math.BigInteger

@Composable
fun WordRankCalculatorScreen(navController: NavHostController) {
    var wordInput by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<BigInteger?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val clipboardManager = LocalClipboardManager.current

    ToolScreen(
        title = "Word Rank Calculator",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        "The lexicographical rank is the position of a word among all its possible permutations sorted alphabetically.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            OutlinedTextField(
                value = wordInput,
                onValueChange = {
                    wordInput = it
                    error = null
                },
                label = { Text("Enter Word") },
                placeholder = { Text("e.g. BABA") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = error != null
            )

            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Button(
                onClick = {
                    if (wordInput.isBlank()) {
                        error = "Please enter a word."
                        result = null
                    } else {
                        val trimmed = wordInput.filter { it.isLetterOrDigit() }
                        if (trimmed.isEmpty()) {
                            error = "Word must contain letters or digits."
                            result = null
                        } else {
                            result = WordRankCalculator.calculateRank(trimmed)
                            error = null
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Calculate, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Calculate Rank")
            }

            result?.let { res ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Rank of the word:",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            res.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(res.toString()))
                        }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                                Spacer(Modifier.width(4.dp))
                                Text("Copy Result", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }

                // Example explanation for small words
                if (wordInput.filter { it.isLetterOrDigit() }.length in 2..5) {
                    val word = wordInput.filter { it.isLetterOrDigit() }.uppercase()
                    Text(
                        "Position of \"$word\" in alphabetically sorted permutations.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}
