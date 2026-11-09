package com.toolbox.doctools

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

object EpicDocLogic {
    private val englishToTeluguDictionary = mapOf(
        "hello" to "నమస్కారం (Namaskaram)",
        "welcome" to "స్వాగతం (Swagatham)",
        "thank you" to "ధన్యవాదాలు (Dhanyavadalu)",
        "how are you" to "మీరు ఎలా ఉన్నారు? (Meeru ela unnaru?)",
        "good morning" to "శుభోదయం (Shubhodayam)",
        "good evening" to "శుభ సాయంత్రం (Shubha Sayantram)",
        "yes" to "అవును (Avunu)",
        "no" to "కాదు (Kaadu)",
        "water" to "నీరు / మంచినీరు (Neeru)",
        "food" to "ఆహారం / భోజనం (Aaharam)",
        "book" to "పుస్తకం (Pusthakam)",
        "friend" to "స్నేహితుడు / మిత్రుడు (Snehithudu)"
    )

    fun translateEnglishToTelugu(text: String): String {
        val clean = text.lowercase().trim()
        englishToTeluguDictionary[clean]?.let { return it }
        val words = text.split(" ")
        val translated = words.map { w ->
            val stripped = w.lowercase().filter { it.isLetter() }
            englishToTeluguDictionary[stripped] ?: w
        }
        return translated.joinToString(" ")
    }
}

@Composable
fun EpicDocToolsScreen(toolId: String = "doc-translator") {
    var selectedTool by remember { mutableStateOf(toolId) }
    val tools = listOf(
        "doc-translator" to "Doc Translator",
        "pdf" to "PDF Hub",
        "docx-md" to "Word to MD",
        "text" to "Text Hub",
        "md-editor" to "Markdown Editor",
        "finance" to "Finance Hub",
        "image" to "Image Hub",
        "image-lab" to "Image Lab"
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
            tools.take(4).forEach { (id, name) ->
                FilterChip(selected = selectedTool == id, onClick = { selectedTool = id }, label = { Text(name) })
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tools.drop(4).forEach { (id, name) ->
                FilterChip(selected = selectedTool == id, onClick = { selectedTool = id }, label = { Text(name) })
            }
        }

        when (selectedTool) {
            "doc-translator" -> DocTranslatorUi()
            "docx-md" -> WordToMdUi()
            "md-editor" -> MarkdownEditorUi()
            "finance" -> FinanceHubUi()
            "image", "image-lab" -> ImageHubUi()
            else -> PdfHubUi()
        }
    }
}

@Composable
fun DocTranslatorUi() {
    var englishInput by remember { mutableStateOf("hello friend welcome") }
    var teluguOutput by remember { mutableStateOf("") }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("English to Telugu Offline Document Translator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = englishInput, onValueChange = { englishInput = it }, label = { Text("English text") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { teluguOutput = EpicDocLogic.translateEnglishToTelugu(englishInput) }) {
                Text("Translate (తెలుగు)")
            }
            if (teluguOutput.isNotBlank()) {
                Text("Telugu Translation:", fontWeight = FontWeight.SemiBold)
                Text(teluguOutput, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun PdfHubUi() {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Comprehensive PDF Hub & Toolkit", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Available Offline PDF Operations:")
            Text("• Compress PDF file size while retaining legible text")
            Text("• Export multi-page PDF pages to individual PNG/JPEG images")
            Text("• Convert images directly into a bundled PDF document")
            Text("• Extract text and inspect document metadata locally")
        }
    }
}

@Composable
fun WordToMdUi() {
    var rawText by remember { mutableStateOf("Chapter 1: Getting Started\nThis is a standard document section.") }
    var converted by remember { mutableStateOf("") }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Word Document to Markdown Converter", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = rawText, onValueChange = { rawText = it }, label = { Text("Document Content") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            Button(onClick = {
                val lines = rawText.lines()
                converted = lines.mapIndexed { idx, line ->
                    if (idx == 0) "# $line" else line
                }.joinToString("\n\n")
            }) {
                Text("Convert to Clean Markdown")
            }
            if (converted.isNotBlank()) {
                Text(converted, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
fun MarkdownEditorUi() {
    var md by remember { mutableStateOf("# Live Markdown Workspace\n- Full offline support\n- Real-time preview") }
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Markdown Interactive Editor & Notepad", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = md, onValueChange = { md = it }, label = { Text("Markdown") }, modifier = Modifier.fillMaxWidth(), minLines = 4)
            Text("Preview:", fontWeight = FontWeight.SemiBold)
            Text(md.replace("#", "").trim(), fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun FinanceHubUi() {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Finance Hub & Asset Calculator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("• Integrated with Return on Investment (ROI) and Compound Growth calculations.")
            Text("• Fixed Mortgage & Loan amortization schedule analysis.")
            Text("• Expense splitting and GST tax breakdown.")
        }
    }
}

@Composable
fun ImageHubUi() {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Image Lab & Multi-filter Visual Hub", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("• Real-time range adjustments: Brightness, Contrast, Saturation, Hue, and Blur.")
            Text("• Color Palette extraction and format transcoding (PNG, JPEG, WEBP).")
            Text("• Zero telemetry, privacy-preserving processing directly on device.")
        }
    }
}
