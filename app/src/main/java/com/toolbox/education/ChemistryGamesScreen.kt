package com.toolbox.education

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import kotlin.random.Random

@Composable
fun ChemistryGamesScreen(
    modifier: Modifier = Modifier,
) {
    var selectedGameTab by remember { mutableIntStateOf(0) }
    val gameTabs = listOf("Symbol Quiz", "Atomic # Guess", "Compound Lab")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TileSelector(
            options = gameTabs,
            selectedIndex = selectedGameTab,
            onSelectIndex = { selectedGameTab = it },
        )

        when (selectedGameTab) {
            0 -> ElementSymbolQuiz()
            1 -> AtomicNumberGuessGame()
            2 -> CompoundLabGame()
        }
    }
}

@Composable
fun ElementSymbolQuiz() {
    var streak by remember { mutableIntStateOf(0) }
    var bestStreak by remember { mutableIntStateOf(0) }
    var currentElement by remember { mutableStateOf(PeriodicTableData.elements.random()) }
    var options by remember(currentElement) {
        val pool = PeriodicTableData.elements.filter { it.number != currentElement.number }.shuffled().take(3)
        mutableStateOf((pool + currentElement).shuffled())
    }
    var selectedSymbol by remember { mutableStateOf<String?>(null) }
    var isAnswerSubmitted by remember { mutableStateOf(false) }

    fun nextQuestion() {
        currentElement = PeriodicTableData.elements.random()
        selectedSymbol = null
        isAnswerSubmitted = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Score Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = "Current Streak: $streak 🔥",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                )
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = "Best Streak: $bestStreak 🏆",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                )
            }
        }

        // Question Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "What is the chemical symbol for:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = currentElement.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "Atomic #${currentElement.number} · ${currentElement.category.displayName}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        // 4 Option Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            options.forEach { opt ->
                val isSelected = selectedSymbol == opt.symbol
                val isCorrect = opt.symbol == currentElement.symbol
                val btnColor = when {
                    !isAnswerSubmitted && isSelected -> MaterialTheme.colorScheme.primaryContainer
                    isAnswerSubmitted && isCorrect -> Color(0xFF81C784)
                    isAnswerSubmitted && isSelected && !isCorrect -> Color(0xFFE57373)
                    else -> MaterialTheme.colorScheme.surfaceContainerHigh
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !isAnswerSubmitted) {
                            selectedSymbol = opt.symbol
                        },
                    colors = CardDefaults.cardColors(containerColor = btnColor),
                    shape = RoundedCornerShape(12.dp),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = opt.symbol,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                        )
                        if (isAnswerSubmitted && isCorrect) {
                            Icon(Icons.Default.Check, contentDescription = "Correct", tint = Color(0xFF1B5E20))
                        } else if (isAnswerSubmitted && isSelected && !isCorrect) {
                            Icon(Icons.Default.Close, contentDescription = "Wrong", tint = Color(0xFFB71C1C))
                        }
                    }
                }
            }
        }

        // Action Button
        if (!isAnswerSubmitted) {
            Button(
                onClick = {
                    isAnswerSubmitted = true
                    if (selectedSymbol == currentElement.symbol) {
                        streak++
                        if (streak > bestStreak) bestStreak = streak
                    } else {
                        streak = 0
                    }
                },
                enabled = selectedSymbol != null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Confirm Answer", fontWeight = FontWeight.Bold)
            }
        } else {
            Button(
                onClick = { nextQuestion() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Next Element ➔", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AtomicNumberGuessGame() {
    var targetElement by remember { mutableStateOf(PeriodicTableData.elements.random()) }
    var options by remember(targetElement) {
        val diff1 = targetElement.number + Random.nextInt(1, 6)
        val diff2 = (targetElement.number - Random.nextInt(1, 6)).coerceAtLeast(1)
        val diff3 = (targetElement.number + Random.nextInt(7, 15)).coerceAtMost(118)
        val pool = setOf(targetElement.number, diff1.coerceIn(1, 118), diff2.coerceIn(1, 118), diff3.coerceIn(1, 118))
        mutableStateOf(pool.toList().shuffled())
    }
    var selectedNumber by remember { mutableStateOf<Int?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }

    fun next() {
        targetElement = PeriodicTableData.elements.random()
        selectedNumber = null
        isSubmitted = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Score: $score", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(targetElement.category.color.copy(alpha = 0.35f))
                        .border(2.dp, targetElement.category.color, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = targetElement.symbol,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Text(
                    text = targetElement.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Guess the atomic number of ${targetElement.name}:",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        // Options
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            options.forEach { opt ->
                val isSelected = selectedNumber == opt
                val isCorrect = opt == targetElement.number
                val bgColor = when {
                    !isSubmitted && isSelected -> MaterialTheme.colorScheme.primaryContainer
                    isSubmitted && isCorrect -> Color(0xFF81C784)
                    isSubmitted && isSelected && !isCorrect -> Color(0xFFE57373)
                    else -> MaterialTheme.colorScheme.surfaceContainerHigh
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(enabled = !isSubmitted) { selectedNumber = opt },
                    colors = CardDefaults.cardColors(containerColor = bgColor),
                    shape = RoundedCornerShape(12.dp),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                ) {
                    Box(modifier = Modifier.padding(vertical = 18.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "$opt",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }

        if (!isSubmitted) {
            Button(
                onClick = {
                    isSubmitted = true
                    if (selectedNumber == targetElement.number) score++
                },
                enabled = selectedNumber != null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Submit Guess", fontWeight = FontWeight.Bold)
            }
        } else {
            Button(
                onClick = { next() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Next ➔", fontWeight = FontWeight.Bold)
            }
        }
    }
}

data class CompoundRecipe(
    val name: String,
    val formula: String,
    val neededElements: Map<String, Int>, // Symbol to count
    val description: String,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CompoundLabGame() {
    val recipes = remember {
        listOf(
            CompoundRecipe("Water", "H₂O", mapOf("H" to 2, "O" to 1), "Universal solvent essential for all known forms of life."),
            CompoundRecipe("Carbon Dioxide", "CO₂", mapOf("C" to 1, "O" to 2), "Important greenhouse gas utilized in photosynthesis."),
            CompoundRecipe("Table Salt", "NaCl", mapOf("Na" to 1, "Cl" to 1), "Ionic salt mineral essential for animal metabolism."),
            CompoundRecipe("Methane", "CH₄", mapOf("C" to 1, "H" to 4), "Primary component of natural gas, a potent hydrocarbon."),
            CompoundRecipe("Ammonia", "NH₃", mapOf("N" to 1, "H" to 3), "Key compound used in fertilizers and industrial cleaning."),
            CompoundRecipe("Rust (Iron Oxide)", "Fe₂O₃", mapOf("Fe" to 2, "O" to 3), "Common oxide formed when iron reacts with oxygen."),
        )
    }

    var currentRecipeIndex by remember { mutableIntStateOf(0) }
    val currentRecipe = recipes[currentRecipeIndex]
    val workbench = remember { mutableStateListOf<String>() }
    var message by remember { mutableStateOf<String?>(null) }
    var synthesizedCount by remember { mutableIntStateOf(0) }

    val availableElements = remember(currentRecipe) {
        (currentRecipe.neededElements.keys + listOf("He", "N", "K", "Ca", "S")).distinct().shuffled()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Target Compound Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Synthesize Target Compound:", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = currentRecipe.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = currentRecipe.formula,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Text(
                    text = currentRecipe.description,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        // Workbench
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            shape = RoundedCornerShape(14.dp),
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Workbench Atoms (${workbench.size}):", fontWeight = FontWeight.Bold)
                    if (workbench.isNotEmpty()) {
                        OutlinedButton(
                            onClick = { workbench.clear(); message = null },
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        ) {
                            Text("Clear")
                        }
                    }
                }

                if (workbench.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "Tap elements below to place them into the reactor.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        workbench.forEachIndexed { index, symbol ->
                            Card(
                                modifier = Modifier.clickable {
                                    workbench.removeAt(index)
                                    message = null
                                },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                                shape = RoundedCornerShape(8.dp),
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Text(symbol, fontWeight = FontWeight.Bold)
                                    Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Element Dispensers
        Text("Elemental Stockpile:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            availableElements.forEach { sym ->
                val el = PeriodicTableData.getBySymbol(sym)
                Card(
                    modifier = Modifier.clickable {
                        workbench.add(sym)
                        message = null
                    },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(sym, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        Text(el?.name ?: "", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // React Button
        Button(
            onClick = {
                // Check composition
                val counts = workbench.groupingBy { it }.eachCount()
                if (counts == currentRecipe.neededElements) {
                    message = "✨ Success! You synthesized ${currentRecipe.name} (${currentRecipe.formula})!"
                    synthesizedCount++
                } else {
                    message = "❌ Incorrect composition. Check chemical stoichiometry!"
                }
            },
            enabled = workbench.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
        ) {
            Icon(Icons.Default.Science, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Initiate Chemical Reaction", fontWeight = FontWeight.Bold)
        }

        message?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (it.startsWith("✨")) Color(0xFFC8E6C9) else Color(0xFFFFCDD2)
                ),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = it,
                    modifier = Modifier.padding(12.dp),
                    fontWeight = FontWeight.Bold,
                    color = if (it.startsWith("✨")) Color(0xFF1B5E20) else Color(0xFFB71C1C),
                )
            }
        }

        if (message?.startsWith("✨") == true) {
            OutlinedButton(
                onClick = {
                    currentRecipeIndex = (currentRecipeIndex + 1) % recipes.size
                    workbench.clear()
                    message = null
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Next Compound Recipe ➔")
            }
        }
    }
}
