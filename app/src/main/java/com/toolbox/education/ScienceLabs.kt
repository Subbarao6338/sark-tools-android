package com.toolbox.education

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toolbox.core.ui.components.TileSelector
import kotlin.math.PI
import kotlin.math.sin
import kotlin.math.sqrt

object ScienceEngine {
    // Standard Genetic Code codon table
    private val codonTable = mapOf(
        "UUU" to "Phe (Phenylalanine)", "UUC" to "Phe (Phenylalanine)",
        "UUA" to "Leu (Leucine)", "UUG" to "Leu (Leucine)",
        "CUU" to "Leu (Leucine)", "CUC" to "Leu (Leucine)", "CUA" to "Leu (Leucine)", "CUG" to "Leu (Leucine)",
        "AUU" to "Ile (Isoleucine)", "AUC" to "Ile (Isoleucine)", "AUA" to "Ile (Isoleucine)",
        "AUG" to "Met (Start)",
        "GUU" to "Val (Valine)", "GUC" to "Val (Valine)", "GUA" to "Val (Valine)", "GUG" to "Val (Valine)",
        "UCU" to "Ser (Serine)", "UCC" to "Ser (Serine)", "UCA" to "Ser (Serine)", "UCG" to "Ser (Serine)",
        "CCU" to "Pro (Proline)", "CCC" to "Pro (Proline)", "CCA" to "Pro (Proline)", "CCG" to "Pro (Proline)",
        "ACU" to "Thr (Threonine)", "ACC" to "Thr (Threonine)", "ACA" to "Thr (Threonine)", "ACG" to "Thr (Threonine)",
        "GCU" to "Ala (Alanine)", "GCC" to "Ala (Alanine)", "GCA" to "Ala (Alanine)", "GCG" to "Ala (Alanine)",
        "UAU" to "Tyr (Tyrosine)", "UAC" to "Tyr (Tyrosine)",
        "UAA" to "STOP (Ochre)", "UAG" to "STOP (Amber)", "UGA" to "STOP (Opal)",
        "CAU" to "His (Histidine)", "CAC" to "His (Histidine)",
        "CAA" to "Gln (Glutamine)", "CAG" to "Gln (Glutamine)",
        "AAU" to "Asn (Asparagine)", "AAC" to "Asn (Asparagine)",
        "AAA" to "Lys (Lysine)", "AAG" to "Lys (Lysine)",
        "GAU" to "Asp (Aspartic acid)", "GAC" to "Asp (Aspartic acid)",
        "GAA" to "Glu (Glutamic acid)", "GAG" to "Glu (Glutamic acid)",
        "UGU" to "Cys (Cysteine)", "UGC" to "Cys (Cysteine)",
        "UGG" to "Trp (Tryptophan)",
        "CGU" to "Arg (Arginine)", "CGC" to "Arg (Arginine)", "CGA" to "Arg (Arginine)", "CGG" to "Arg (Arginine)",
        "AGU" to "Ser (Serine)", "AGC" to "Ser (Serine)",
        "AGA" to "Arg (Arginine)", "AGG" to "Arg (Arginine)",
        "GGU" to "Gly (Glycine)", "GGC" to "Gly (Glycine)", "GGA" to "Gly (Glycine)", "GGG" to "Gly (Glycine)"
    )

    fun translateRnaToAminoAcids(rnaSeq: String): List<Pair<String, String>> {
        val clean = rnaSeq.uppercase().replace("T", "U").filter { it in "ACGU" }
        val result = mutableListOf<Pair<String, String>>()
        for (i in 0 until (clean.length - clean.length % 3) step 3) {
            val codon = clean.substring(i, i + 3)
            val aa = codonTable[codon] ?: "Unknown"
            result.add(codon to aa)
        }
        return result
    }

    fun computePunnettSquare(parent1: String, parent2: String): List<String> {
        val p1 = parent1.trim().take(2).padEnd(2, 'a')
        val p2 = parent2.trim().take(2).padEnd(2, 'a')
        val list = mutableListOf<String>()
        for (a1 in p1) {
            for (a2 in p2) {
                val pair = listOf(a1, a2).sortedWith(compareBy({ !it.isUpperCase() }, { it })).joinToString("")
                list.add(pair)
            }
        }
        return list
    }

    fun computePendulumPeriod(lengthMeters: Double, gravity: Double = 9.80665): Double {
        if (lengthMeters <= 0.0 || gravity <= 0.0) return 0.0
        return 2.0 * PI * sqrt(lengthMeters / gravity)
    }

    fun computeProjectileMotion(velocity: Double, angleDeg: Double, gravity: Double = 9.80665): Triple<Double, Double, Double> {
        val angleRad = angleDeg * PI / 180.0
        val vx = velocity * kotlin.math.cos(angleRad)
        val vy = velocity * sin(angleRad)
        val timeOfFlight = (2.0 * vy) / gravity
        val maxRange = vx * timeOfFlight
        val maxHeight = (vy * vy) / (2.0 * gravity)
        return Triple(timeOfFlight.coerceAtLeast(0.0), maxRange.coerceAtLeast(0.0), maxHeight.coerceAtLeast(0.0))
    }
}

@Composable
fun ScienceLabsScreen(
    modifier: Modifier = Modifier,
) {
    var selectedLabIndex by remember { mutableIntStateOf(0) }
    val labTabs = listOf("Physics Lab", "Biology Lab")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TileSelector(
            options = labTabs,
            selectedIndex = selectedLabIndex,
            onSelectIndex = { selectedLabIndex = it },
        )

        when (selectedLabIndex) {
            0 -> PhysicsSimulationLab()
            1 -> BiologyGeneticsLab()
        }
    }
}

@Composable
fun PhysicsSimulationLab() {
    var length by remember { mutableDoubleStateOf(1.0) } // meters
    var gravityPlanet by remember { mutableStateOf("Earth (9.81 m/s²)" to 9.80665) }

    var projVelocity by remember { mutableDoubleStateOf(25.0) }
    var projAngle by remember { mutableDoubleStateOf(45.0) }

    val period = remember(length, gravityPlanet) {
        ScienceEngine.computePendulumPeriod(length, gravityPlanet.second)
    }

    val (flightTime, range, maxHeight) = remember(projVelocity, projAngle, gravityPlanet) {
        ScienceEngine.computeProjectileMotion(projVelocity, projAngle, gravityPlanet.second)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Simple Harmonic Pendulum
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Simple Pendulum Harmonic Oscillator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = "Formula: T = 2π · √(L / g)",
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.primary,
                )

                Text("Pendulum String Length: ${"%.2f".format(length)} m")
                Slider(
                    value = length.toFloat(),
                    onValueChange = { length = it.toDouble() },
                    valueRange = 0.1f..10.0f,
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Oscillation Period (T):", style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = "${"%.3f".format(period)} seconds",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                        )
                        Text("Frequency: ${"%.3f".format(if (period > 0) 1.0 / period else 0.0)} Hz")
                    }
                }
            }
        }

        // Projectile Motion
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("2D Projectile Motion Trajectory", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                Text("Launch Velocity: ${projVelocity.toInt()} m/s")
                Slider(
                    value = projVelocity.toFloat(),
                    onValueChange = { projVelocity = it.toDouble() },
                    valueRange = 1f..100f,
                )

                Text("Launch Angle: ${projAngle.toInt()}°")
                Slider(
                    value = projAngle.toFloat(),
                    onValueChange = { projAngle = it.toDouble() },
                    valueRange = 5f..85f,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Max Range", fontSize = 11.sp)
                            Text("${"%.1f".format(range)} m", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                    Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Max Height", fontSize = 11.sp)
                            Text("${"%.1f".format(maxHeight)} m", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                    Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Flight Time", fontSize = 11.sp)
                            Text("${"%.2f".format(flightTime)} s", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BiologyGeneticsLab() {
    var rnaInput by remember { mutableStateOf("AUGUUUCCUAAAUGA") }
    val aminoAcids = remember(rnaInput) { ScienceEngine.translateRnaToAminoAcids(rnaInput) }

    var p1Alleles by remember { mutableStateOf("Aa") }
    var p2Alleles by remember { mutableStateOf("Aa") }
    val punnettSquare = remember(p1Alleles, p2Alleles) { ScienceEngine.computePunnettSquare(p1Alleles, p2Alleles) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // DNA/RNA Codon Translator
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Molecular Biology: DNA/RNA Codon Translator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = rnaInput,
                    onValueChange = { rnaInput = it },
                    label = { Text("Enter mRNA sequence (A, U/T, C, G)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Text("Synthesized Polypeptide Chain (${aminoAcids.size} codons):", fontWeight = FontWeight.SemiBold)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    aminoAcids.forEachIndexed { i, (codon, aa) ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text("Codon ${i + 1}: $codon", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                Text(aa, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }

        // Mendelian Genetics Punnett Square
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Mendelian Genetics: 2x2 Punnett Square", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = p1Alleles,
                        onValueChange = { if (it.length <= 2) p1Alleles = it },
                        label = { Text("Parent 1") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = p2Alleles,
                        onValueChange = { if (it.length <= 2) p2Alleles = it },
                        label = { Text("Parent 2") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                }

                Text("Offspring Genotypes:", fontWeight = FontWeight.SemiBold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    punnettSquare.forEach { g ->
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        ) {
                            Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                                Text(g, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            }
                        }
                    }
                }

                val genotypeRatio = punnettSquare.groupingBy { it }.eachCount()
                Text(
                    text = "Genotypic Ratio: " + genotypeRatio.entries.joinToString(", ") { "${it.key}: ${it.value}/4" },
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
