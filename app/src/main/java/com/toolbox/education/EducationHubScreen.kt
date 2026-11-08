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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toolbox.core.ui.components.TileSelector

data class FormulaItem(val topic: String, val formula: String, val explanation: String)

@Composable
fun EducationHubScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Periodic Table", "Chemistry Games", "Science Labs", "Brain Games", "Formulas & Laws", "Constants")

    val formulas = remember {
        listOf(
            FormulaItem("Physics: Newton's Second Law", "F = m · a", "Force equals mass times acceleration"),
            FormulaItem("Physics: Kinetic Energy", "KE = 1/2 · m · v²", "Energy possessed by an object due to motion"),
            FormulaItem("Physics: Ohm's Law", "V = I · R", "Voltage equals current multiplied by resistance"),
            FormulaItem("Physics: Mass-Energy Equivalence", "E = m · c²", "Mass and energy are interchangeable"),
            FormulaItem("Math: Quadratic Formula", "x = (-b ± √(b² - 4ac)) / (2a)", "Roots of quadratic polynomial ax² + bx + c = 0"),
            FormulaItem("Math: Pythagorean Theorem", "a² + b² = c²", "Relationship between sides of a right triangle"),
            FormulaItem("Chemistry: Ideal Gas Law", "P · V = n · R · T", "State of a hypothetical ideal gas"),
            FormulaItem("Chemistry: Molarity", "M = n / V", "Moles of solute divided by liters of solution"),
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
            "Boltzmann Constant (k_B)" to "1.380649 × 10⁻²³ J/K",
            "Standard Acceleration of Gravity (g)" to "9.80665 m/s²",
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TileSelector(
            options = tabs,
            selectedIndex = selectedTab,
            onSelectIndex = { selectedTab = it },
        )

        when (selectedTab) {
            0 -> {
                // Fully detailed 118-element interactive periodic table
                PeriodicTableScreen(modifier = Modifier.fillMaxSize().padding(top = 8.dp))
            }
            1 -> {
                // Interactive Chemistry Games
                ChemistryGamesScreen(modifier = Modifier.fillMaxSize())
            }
            2 -> {
                // STEM Science Labs (Physics & Biology Genetics)
                ScienceLabsScreen(modifier = Modifier.fillMaxSize())
            }
            3 -> {
                // Brain & IQ Games (Math, Sequences, Memory Matrix, Trivia)
                BrainGamesScreen(modifier = Modifier.fillMaxSize())
            }
            4 -> {
                // Formulas & Laws
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
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
            }
            5 -> {
                // Constants
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
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
            }
        }
    }
}
