package com.toolbox.education

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class FormulaItem(val topic: String, val formula: String, val explanation: String)
data class PeriodicElement(val symbol: String, val name: String, val atomicNumber: Int, val atomicMass: Double)

@Composable
fun EducationHubScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Formulas & Laws", "Periodic Table", "Constants", "Quiz")

    val formulas = remember {
        listOf(
            FormulaItem("Physics: Newton's Second Law", "F = m · a", "Force equals mass times acceleration"),
            FormulaItem("Physics: Kinetic Energy", "KE = 1/2 · m · v²", "Energy possessed by an object due to motion"),
            FormulaItem("Physics: Ohm's Law", "V = I · R", "Voltage equals current multiplied by resistance"),
            FormulaItem("Math: Quadratic Formula", "x = (-b ± √(b² - 4ac)) / (2a)", "Roots of quadratic polynomial ax² + bx + c = 0"),
            FormulaItem("Math: Pythagorean Theorem", "a² + b² = c²", "Relationship between sides of a right triangle"),
            FormulaItem("Chemistry: Ideal Gas Law", "P · V = n · R · T", "State of a hypothetical ideal gas"),
        )
    }

    val periodicElements = remember {
        listOf(
            PeriodicElement("H", "Hydrogen", 1, 1.008),
            PeriodicElement("He", "Helium", 2, 4.0026),
            PeriodicElement("Li", "Lithium", 3, 6.94),
            PeriodicElement("C", "Carbon", 6, 12.011),
            PeriodicElement("N", "Nitrogen", 7, 14.007),
            PeriodicElement("O", "Oxygen", 8, 15.999),
            PeriodicElement("Na", "Sodium", 11, 22.990),
            PeriodicElement("Fe", "Iron", 26, 55.845),
            PeriodicElement("Cu", "Copper", 29, 63.546),
            PeriodicElement("Au", "Gold", 79, 196.97),
        )
    }

    val physicalConstants = remember {
        listOf(
            "Speed of Light (c)" to "299,792,458 m/s",
            "Planck's Constant (h)" to "6.62607015 × 10⁻³⁴ J·s",
            "Gravitational Constant (G)" to "6.67430 × 10⁻¹¹ m³/(kg·s²)",
            "Elementary Charge (e)" to "1.602176634 × 10⁻¹⁹ C",
            "Avogadro's Number (N_A)" to "6.02214076 × 10²³ mol⁻¹",
            "Gas Constant (R)" to "8.314462618 J/(mol·K)",
        )
    }

    var quizQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableIntStateOf(-1) }
    var quizScore by remember { mutableIntStateOf(0) }
    val questions = remember {
        listOf(
            Triple("What is the chemical symbol for Gold?", listOf("Au", "Ag", "Fe", "Cu"), 0),
            Triple("Which equation represents Ohm's Law?", listOf("E=mc²", "V=IR", "F=ma", "P=IV"), 1),
            Triple("What is the approximate speed of light in vacuum?", listOf("300,000 km/s", "150,000 km/s", "3,000 km/s", "30,000 km/s"), 0),
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) },
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            when (selectedTab) {
                0 -> { // Formulas & Laws
                    formulas.forEach { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(item.topic, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(
                                    item.formula,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                Text(item.explanation, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
                1 -> { // Periodic Table
                    periodicElements.forEach { el ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
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
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                        shape = RoundedCornerShape(8.dp),
                                    ) {
                                        Box(modifier = Modifier.padding(10.dp), contentAlignment = Alignment.Center) {
                                            Text(el.symbol, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(el.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Text("Atomic #: ${el.atomicNumber}", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                                Text("Mass: ${el.atomicMass}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
                2 -> { // Constants
                    physicalConstants.forEach { (name, value) ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(name, fontWeight = FontWeight.Medium)
                                Text(value, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
                3 -> { // Educational Quiz
                    val q = questions[quizQuestionIndex]
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Question ${quizQuestionIndex + 1} of ${questions.size}", style = MaterialTheme.typography.labelMedium)
                            Text(q.first, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                            q.second.forEachIndexed { index, option ->
                                FilterChip(
                                    selected = selectedOption == index,
                                    onClick = { selectedOption = index },
                                    label = { Text(option) },
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }

                            Button(
                                onClick = {
                                    if (selectedOption == q.third) {
                                        quizScore++
                                    }
                                    if (quizQuestionIndex < questions.size - 1) {
                                        quizQuestionIndex++
                                        selectedOption = -1
                                    } else {
                                        // Finished
                                        quizQuestionIndex = 0
                                        selectedOption = -1
                                    }
                                },
                                enabled = selectedOption != -1,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(if (quizQuestionIndex < questions.size - 1) "Next Question" else "Finish & Restart")
                            }

                            Text("Score: $quizScore", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
