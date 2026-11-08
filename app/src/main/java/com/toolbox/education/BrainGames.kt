package com.toolbox.education

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toolbox.core.ui.components.TileSelector
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun BrainGamesScreen(
    modifier: Modifier = Modifier,
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Speed Math", "IQ Sequences", "Memory Matrix", "Trivia Hub")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TileSelector(
            options = tabs,
            selectedIndex = selectedIndex,
            onSelectIndex = { selectedIndex = it },
        )

        when (selectedIndex) {
            0 -> SpeedMathGame()
            1 -> IqSequenceGame()
            2 -> MemoryMatrixGame()
            3 -> GeneralTriviaGame()
        }
    }
}

data class MathQuestion(
    val prompt: String,
    val answer: Int,
    val options: List<Int>,
)

object BrainEngine {
    fun generateMathQuestion(): MathQuestion {
        val op = listOf("+", "-", "*").random()
        val a: Int
        val b: Int
        val ans: Int
        when (op) {
            "+" -> {
                a = Random.nextInt(10, 99)
                b = Random.nextInt(10, 99)
                ans = a + b
            }
            "-" -> {
                a = Random.nextInt(20, 99)
                b = Random.nextInt(5, a)
                ans = a - b
            }
            else -> {
                a = Random.nextInt(3, 15)
                b = Random.nextInt(3, 12)
                ans = a * b
            }
        }
        val options = mutableSetOf(ans)
        while (options.size < 4) {
            val delta = Random.nextInt(-10, 11)
            if (delta != 0) options.add(ans + delta)
        }
        return MathQuestion("$a $op $b = ?", ans, options.toList().shuffled())
    }

    data class SequenceProblem(
        val sequence: List<Int>,
        val answer: Int,
        val explanation: String,
        val options: List<Int>,
    )

    fun generateSequenceProblem(): SequenceProblem {
        val type = Random.nextInt(4)
        return when (type) {
            0 -> { // Arithmetic
                val start = Random.nextInt(2, 20)
                val diff = Random.nextInt(3, 9)
                val seq = (0..4).map { start + it * diff }
                val next = start + 5 * diff
                val opts = listOf(next, next - diff, next + diff, next + 2).shuffled()
                SequenceProblem(seq, next, "Adding $diff each step", opts)
            }
            1 -> { // Geometric / Doubling
                val start = Random.nextInt(1, 5)
                val seq = (0..4).map { start * (1 shl it) }
                val next = start * (1 shl 5)
                val opts = listOf(next, next - 4, next + 8, next * 2).shuffled()
                SequenceProblem(seq, next, "Multiplying by 2 each step", opts)
            }
            2 -> { // Squares
                val offset = Random.nextInt(1, 4)
                val seq = (1..5).map { (it + offset) * (it + offset) }
                val next = (6 + offset) * (6 + offset)
                val opts = listOf(next, next - 10, next + 11, next + 5).shuffled()
                SequenceProblem(seq, next, "Sequential squares: n²", opts)
            }
            else -> { // Fibonacci-style
                val a = Random.nextInt(1, 5)
                val b = Random.nextInt(1, 5)
                val list = mutableListOf(a, b)
                for (i in 2..4) list.add(list[i - 1] + list[i - 2])
                val next = list[4] + list[3]
                val opts = listOf(next, next - 2, next + 3, next + 1).shuffled()
                SequenceProblem(list, next, "Each number is the sum of previous two", opts)
            }
        }
    }
}

@Composable
fun SpeedMathGame() {
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var question by remember { mutableStateOf(BrainEngine.generateMathQuestion()) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }

    fun next() {
        question = BrainEngine.generateMathQuestion()
        selectedOption = null
        isSubmitted = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Text("Score: $score", fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp))
            }
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Text("Streak: $streak 🔥", fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp))
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            shape = RoundedCornerShape(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = question.prompt,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        // 4 options in 2x2 grid
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                question.options.take(2).forEach { opt ->
                    val isSelected = selectedOption == opt
                    val isCorrect = opt == question.answer
                    val bgColor = when {
                        !isSubmitted && isSelected -> MaterialTheme.colorScheme.primaryContainer
                        isSubmitted && isCorrect -> Color(0xFF81C784)
                        isSubmitted && isSelected && !isCorrect -> Color(0xFFE57373)
                        else -> MaterialTheme.colorScheme.surfaceContainerHigh
                    }
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(enabled = !isSubmitted) { selectedOption = opt },
                        colors = CardDefaults.cardColors(containerColor = bgColor),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                            Text("$opt", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                question.options.drop(2).forEach { opt ->
                    val isSelected = selectedOption == opt
                    val isCorrect = opt == question.answer
                    val bgColor = when {
                        !isSubmitted && isSelected -> MaterialTheme.colorScheme.primaryContainer
                        isSubmitted && isCorrect -> Color(0xFF81C784)
                        isSubmitted && isSelected && !isCorrect -> Color(0xFFE57373)
                        else -> MaterialTheme.colorScheme.surfaceContainerHigh
                    }
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(enabled = !isSubmitted) { selectedOption = opt },
                        colors = CardDefaults.cardColors(containerColor = bgColor),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                            Text("$opt", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (!isSubmitted) {
            Button(
                onClick = {
                    isSubmitted = true
                    if (selectedOption == question.answer) {
                        score += 10
                        streak++
                    } else {
                        streak = 0
                    }
                },
                enabled = selectedOption != null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Confirm Answer", fontWeight = FontWeight.Bold)
            }
        } else {
            Button(
                onClick = { next() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Next Equation ➔", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun IqSequenceGame() {
    var problem by remember { mutableStateOf(BrainEngine.generateSequenceProblem()) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }

    fun next() {
        problem = BrainEngine.generateSequenceProblem()
        selectedOption = null
        isSubmitted = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("IQ Score: $score", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text("Identify the Next Number in the Sequence:", style = MaterialTheme.typography.labelMedium)
                Text(
                    text = problem.sequence.joinToString(", ") + ",  ?",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        // 4 Options
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            problem.options.forEach { opt ->
                val isSelected = selectedOption == opt
                val isCorrect = opt == problem.answer
                val bgColor = when {
                    !isSubmitted && isSelected -> MaterialTheme.colorScheme.primaryContainer
                    isSubmitted && isCorrect -> Color(0xFF81C784)
                    isSubmitted && isSelected && !isCorrect -> Color(0xFFE57373)
                    else -> MaterialTheme.colorScheme.surfaceContainerHigh
                }
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(enabled = !isSubmitted) { selectedOption = opt },
                    colors = CardDefaults.cardColors(containerColor = bgColor),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Box(modifier = Modifier.padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                        Text("$opt", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }
        }

        if (isSubmitted) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text(
                    text = "Rule: ${problem.explanation}",
                    modifier = Modifier.padding(12.dp),
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        if (!isSubmitted) {
            Button(
                onClick = {
                    isSubmitted = true
                    if (selectedOption == problem.answer) score += 20
                },
                enabled = selectedOption != null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Confirm Guess", fontWeight = FontWeight.Bold)
            }
        } else {
            Button(
                onClick = { next() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Next Sequence ➔", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MemoryMatrixGame() {
    var level by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var targetCount by remember { mutableIntStateOf(3) }
    var targetIndices by remember { mutableStateOf(setOf<Int>()) }
    var isMemorizing by remember { mutableStateOf(false) }
    val selectedIndices = remember { mutableStateListOf<Int>() }
    var roundOver by remember { mutableStateOf(false) }
    var roundSuccess by remember { mutableStateOf(false) }

    fun startRound() {
        selectedIndices.clear()
        roundOver = false
        roundSuccess = false
        targetCount = (level + 2).coerceAtMost(8)
        val list = (0..15).shuffled().take(targetCount).toSet()
        targetIndices = list
        isMemorizing = true
    }

    LaunchedEffect(isMemorizing) {
        if (isMemorizing) {
            delay(1500)
            isMemorizing = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Memory Level: $level", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Score: $score", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        Text(
            text = if (isMemorizing) "Memorize the highlighted tiles!" else if (!roundOver) "Recall all $targetCount tiles!" else if (roundSuccess) "Perfect Recall! ✨" else "Missed some tiles!",
            fontWeight = FontWeight.Bold,
            color = if (isMemorizing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        )

        // 4x4 Matrix
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            (0..3).forEach { r ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    (0..3).forEach { c ->
                        val index = r * 4 + c
                        val isTarget = index in targetIndices
                        val isSelected = index in selectedIndices

                        val cellColor = when {
                            isMemorizing && isTarget -> MaterialTheme.colorScheme.primary
                            roundOver && isTarget -> Color(0xFF81C784)
                            roundOver && isSelected && !isTarget -> Color(0xFFE57373)
                            isSelected -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.surfaceContainerHigh
                        }

                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(cellColor)
                                .clickable(enabled = !isMemorizing && !roundOver) {
                                    if (index !in selectedIndices) {
                                        selectedIndices.add(index)
                                        if (selectedIndices.size == targetCount) {
                                            roundOver = true
                                            if (selectedIndices.toSet() == targetIndices) {
                                                roundSuccess = true
                                                score += level * 10
                                                level++
                                            } else {
                                                roundSuccess = false
                                                level = (level - 1).coerceAtLeast(1)
                                            }
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            if (roundOver && isTarget) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                            }
                        }
                    }
                }
            }
        }

        if (roundOver || targetIndices.isEmpty()) {
            Button(
                onClick = { startRound() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(if (targetIndices.isEmpty()) "Start Memory Game" else "Next Round ➔", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun GeneralTriviaGame() {
    val triviaList = remember {
        listOf(
            Triple("What is the powerhouse organelle of eukaryotic cells?", listOf("Mitochondria", "Ribosome", "Nucleus", "Endoplasmic Reticulum"), 0),
            Triple("Which planet in our solar system has the most extensive ring system?", listOf("Saturn", "Jupiter", "Uranus", "Neptune"), 0),
            Triple("Who discovered penicillin in 1928?", listOf("Alexander Fleming", "Louis Pasteur", "Robert Koch", "Edward Jenner"), 0),
            Triple("What is the SI unit of electric capacitance?", listOf("Farad", "Henry", "Tesla", "Weber"), 0),
            Triple("Which gas makes up approximately 78% of Earth's atmosphere?", listOf("Nitrogen", "Oxygen", "Argon", "Carbon Dioxide"), 0),
            Triple("What is the hardest known natural mineral on Earth?", listOf("Diamond", "Corundum", "Quartz", "Topaz"), 0),
            Triple("How many bones are in the adult human body?", listOf("206", "195", "215", "220"), 0),
        )
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }

    val current = triviaList[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Trivia Question ${currentIndex + 1} of ${triviaList.size} · Score: $score", fontWeight = FontWeight.Bold)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                text = current.first,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(20.dp),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            current.second.forEachIndexed { i, opt ->
                val isSelected = selectedOption == i
                val isCorrect = i == current.third
                val bgColor = when {
                    !isSubmitted && isSelected -> MaterialTheme.colorScheme.primaryContainer
                    isSubmitted && isCorrect -> Color(0xFF81C784)
                    isSubmitted && isSelected && !isCorrect -> Color(0xFFE57373)
                    else -> MaterialTheme.colorScheme.surfaceContainerHigh
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !isSubmitted) { selectedOption = i },
                    colors = CardDefaults.cardColors(containerColor = bgColor),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        text = opt,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }

        if (!isSubmitted) {
            Button(
                onClick = {
                    isSubmitted = true
                    if (selectedOption == current.third) score += 15
                },
                enabled = selectedOption != null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Confirm Answer", fontWeight = FontWeight.Bold)
            }
        } else {
            Button(
                onClick = {
                    currentIndex = (currentIndex + 1) % triviaList.size
                    selectedOption = null
                    isSubmitted = false
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Next Question ➔", fontWeight = FontWeight.Bold)
            }
        }
    }
}
