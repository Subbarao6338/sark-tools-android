package com.toolbox.everyday.morse

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MorseTrainerScreen() {
    val morseMap = mapOf(
        'A' to ".-", 'B' to "-...", 'C' to "-.-.", 'D' to "-..", 'E' to ".",
        'F' to "..-.", 'G' to "--.", 'H' to "....", 'I' to "..", 'J' to ".---",
        'K' to "-.-", 'L' to ".-..", 'M' to "--", 'N' to "-.", 'O' to "---",
        'P' to ".--.", 'Q' to "--.-", 'R' to ".-.", 'S' to "...", 'T' to "-",
        'U' to "..-", 'V' to "...-", 'W' to ".--", 'X' to "-..-", 'Y' to "-.--",
        'Z' to "--..", '1' to ".----", '2' to "..---", '3' to "...--", '4' to "....-",
        '5' to ".....", '6' to "-....", '7' to "--...", '8' to "---..", '9' to "----.",
        '0' to "-----"
    )

    val charList = morseMap.keys.toList()

    var score by remember { mutableStateOf(0) }
    var totalAttempts by remember { mutableStateOf(0) }
    var targetChar by remember { mutableStateOf('S') }
    var targetMorse by remember { mutableStateOf("...") }
    var options by remember { mutableStateOf(listOf('S', 'O', 'H', 'E')) }
    var feedback by remember { mutableStateOf<String?>(null) }

    fun nextQuestion() {
        feedback = null
        val correct = charList.random()
        targetChar = correct
        targetMorse = morseMap[correct] ?: ""
        val wrongOptions = charList.filter { it != correct }.shuffled().take(3)
        options = (wrongOptions + correct).shuffled()
    }

    fun submitAnswer(chosen: Char) {
        totalAttempts++
        if (chosen == targetChar) {
            score++
            feedback = "Correct! $chosen is '$targetMorse'"
        } else {
            feedback = "Incorrect! $chosen is '${morseMap[chosen]}', answer was $targetChar"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = targetMorse,
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Score: $score / $totalAttempts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        feedback?.let {
            Text(
                it,
                fontWeight = FontWeight.Bold,
                color = if (it.startsWith("Correct")) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Text("Select the corresponding character:", fontWeight = FontWeight.SemiBold)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.take(2).forEach { ch ->
                Button(
                    onClick = { submitAnswer(ch) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    Text("$ch", style = MaterialTheme.typography.titleLarge)
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.drop(2).take(2).forEach { ch ->
                Button(
                    onClick = { submitAnswer(ch) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    Text("$ch", style = MaterialTheme.typography.titleLarge)
                }
            }
        }

        Button(
            onClick = { nextQuestion() },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Next Flashcard")
        }
    }
}
