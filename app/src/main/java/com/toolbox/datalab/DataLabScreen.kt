package com.toolbox.datalab

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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.pow
import kotlin.math.sqrt

object DataLabLogic {
    fun calculateStats(numbers: List<Double>): Map<String, Double> {
        if (numbers.isEmpty()) return emptyMap()
        val sorted = numbers.sorted()
        val count = sorted.size.toDouble()
        val sum = sorted.sum()
        val mean = sum / count
        val median = if (sorted.size % 2 == 1) {
            sorted[sorted.size / 2]
        } else {
            (sorted[sorted.size / 2 - 1] + sorted[sorted.size / 2]) / 2.0
        }
        val variance = sorted.map { (it - mean).pow(2) }.sum() / count
        val stdDev = sqrt(variance)
        val min = sorted.first()
        val max = sorted.last()
        return mapOf(
            "Count" to count,
            "Sum" to sum,
            "Mean" to mean,
            "Median" to median,
            "Variance" to variance,
            "StdDev" to stdDev,
            "Min" to min,
            "Max" to max
        )
    }

    fun detectAnomalies(numbers: List<Double>, zThreshold: Double = 2.0): List<Double> {
        val stats = calculateStats(numbers)
        val mean = stats["Mean"] ?: return emptyList()
        val stdDev = stats["StdDev"] ?: return emptyList()
        if (stdDev == 0.0) return emptyList()
        return numbers.filter { kotlin.math.abs(it - mean) / stdDev > zThreshold }
    }

    fun generateMockUsers(count: Int): List<String> {
        val names = listOf("Alex", "Sam", "Jordan", "Taylor", "Morgan", "Casey", "Subba", "Priya", "Rahul", "Aarav")
        val domains = listOf("gmail.com", "example.org", "outlook.com", "techcorp.io")
        val roles = listOf("Engineering", "Product", "Design", "Marketing", "Security")
        return (1..count).map { idx ->
            val name = names[idx % names.size] + "$idx"
            val email = "${name.lowercase()}@${domains[idx % domains.size]}"
            val role = roles[idx % roles.size]
            "{\"id\": $idx, \"name\": \"$name\", \"email\": \"$email\", \"role\": \"$role\"}"
        }
    }

    fun reconcileLists(listA: List<String>, listB: List<String>): Map<String, List<String>> {
        val setA = listA.toSet()
        val setB = listB.toSet()
        val matched = setA.intersect(setB).toList()
        val onlyInA = setA.subtract(setB).toList()
        val onlyInB = setB.subtract(setA).toList()
        return mapOf("Matched" to matched, "Only in A" to onlyInA, "Only in B" to onlyInB)
    }
}

@Composable
fun DataLabScreen(toolId: String = "science") {
    var selectedTool by remember { mutableStateOf(toolId) }
    val tools = listOf(
        "science" to "Data Science Hub",
        "viewer" to "Data Viewer",
        "adv-data" to "Advanced Analytics",
        "reconcile" to "Reconciliation",
        "mock" to "Mock Data Generator",
        "synthetic" to "Synthetic Data"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tools.take(3).forEach { (id, name) ->
                FilterChip(selected = selectedTool == id, onClick = { selectedTool = id }, label = { Text(name) })
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tools.drop(3).forEach { (id, name) ->
                FilterChip(selected = selectedTool == id, onClick = { selectedTool = id }, label = { Text(name) })
            }
        }

        when (selectedTool) {
            "science", "adv-data" -> DataScienceHubUi()
            "viewer" -> DataViewerUi()
            "reconcile" -> ReconciliationUi()
            "mock", "synthetic" -> MockDataGeneratorUi()
            else -> DataScienceHubUi()
        }
    }
}

@Composable
fun DataScienceHubUi() {
    var rawInput by remember { mutableStateOf("12, 15, 18, 20, 22, 19, 14, 85, 16, 21") }
    var stats by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }
    var anomalies by remember { mutableStateOf<List<Double>>(emptyList()) }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Data Science & Anomaly Detection Hub", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = rawInput,
                onValueChange = { rawInput = it },
                label = { Text("Numbers (comma or space separated)") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = {
                val numbers = rawInput.split(Regex("[,\\s]+")).mapNotNull { it.trim().toDoubleOrNull() }
                stats = DataLabLogic.calculateStats(numbers)
                anomalies = DataLabLogic.detectAnomalies(numbers)
            }) {
                Text("Analyze Dataset")
            }
            if (stats.isNotEmpty()) {
                Text("Statistical Summary:", fontWeight = FontWeight.SemiBold)
                stats.forEach { (k, v) ->
                    Text("$k: ${String.format("%.3f", v)}")
                }
                if (anomalies.isNotEmpty()) {
                    Text("Outliers Detected (Z > 2.0): ${anomalies.joinToString(", ")}", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                } else {
                    Text("No significant outliers detected.", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun DataViewerUi() {
    var csvText by remember { mutableStateOf("Name,Age,Role\nAlice,29,Engineer\nBob,34,Designer\nCharlie,42,Manager") }
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Tabular Data Viewer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = csvText, onValueChange = { csvText = it }, label = { Text("CSV Data") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            val rows = csvText.lines().filter { it.isNotBlank() }.map { it.split(",") }
            if (rows.isNotEmpty()) {
                Text("Table Preview:", fontWeight = FontWeight.SemiBold)
                rows.forEachIndexed { idx, row ->
                    Text(row.joinToString("  |  ") { it.trim() }, fontWeight = if (idx == 0) FontWeight.Bold else FontWeight.Normal, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

@Composable
fun ReconciliationUi() {
    var listA by remember { mutableStateOf("Apple\nBanana\nCherry\nDate") }
    var listB by remember { mutableStateOf("Banana\nDate\nElderberry\nFig") }
    var result by remember { mutableStateOf<Map<String, List<String>>>(emptyMap()) }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Dataset Reconciliation & Audit", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = listA, onValueChange = { listA = it }, label = { Text("Dataset A (line-separated)") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            OutlinedTextField(value = listB, onValueChange = { listB = it }, label = { Text("Dataset B (line-separated)") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            Button(onClick = {
                val a = listA.lines().map { it.trim() }.filter { it.isNotEmpty() }
                val b = listB.lines().map { it.trim() }.filter { it.isNotEmpty() }
                result = DataLabLogic.reconcileLists(a, b)
            }) {
                Text("Reconcile Datasets")
            }
            if (result.isNotEmpty()) {
                Text("Matched Records: ${result["Matched"]?.joinToString(", ")}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Text("Only in A: ${result["Only in A"]?.joinToString(", ")}")
                Text("Only in B: ${result["Only in B"]?.joinToString(", ")}")
            }
        }
    }
}

@Composable
fun MockDataGeneratorUi() {
    var count by remember { mutableStateOf("5") }
    var output by remember { mutableStateOf("") }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Mock & Synthetic Data Generator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = count, onValueChange = { count = it }, label = { Text("Number of Records") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = {
                val n = count.toIntOrNull() ?: 5
                output = DataLabLogic.generateMockUsers(n.coerceIn(1, 50)).joinToString(",\n")
            }) {
                Text("Generate Mock JSON Records")
            }
            if (output.isNotBlank()) {
                Text("[\n$output\n]", fontFamily = FontFamily.Monospace)
            }
        }
    }
}
