package com.toolbox.ai

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

object EpicAiLogic {
    fun analyzeSentiment(text: String): Map<String, String> {
        val positive = listOf("great", "good", "excellent", "fast", "love", "awesome", "perfect", "clean", "beautiful", "happy")
        val negative = listOf("bad", "terrible", "slow", "hate", "ugly", "error", "bug", "fail", "poor", "broken")
        val words = text.lowercase().split(Regex("\\W+"))
        val posCount = words.count { it in positive }
        val negCount = words.count { it in negative }
        val sentiment = when {
            posCount > negCount -> "Positive ($posCount positive words)"
            negCount > posCount -> "Negative ($negCount negative words)"
            else -> "Neutral / Balanced"
        }
        return mapOf(
            "Overall Sentiment" to sentiment,
            "Positive Word Count" to posCount.toString(),
            "Negative Word Count" to negCount.toString()
        )
    }

    fun craftImagePrompt(subject: String, style: String): String {
        val styleKeywords = when (style) {
            "Cinematic" -> "35mm anamorphic, depth of field, dramatic atmospheric lighting, 8k render, photorealistic"
            "Anime" -> "Makoto Shinkai aesthetic, vibrant celestial palette, cel shaded, highly detailed line art"
            "Cyberpunk" -> "Neon noir, rainy asphalt reflections, holographic billboards, volumetric mist"
            else -> "Award winning photography, crisp focus, studio lighting"
        }
        return "$subject, $styleKeywords"
    }
}

@Composable
fun EpicAiScreen(toolId: String = "local") {
    var selectedTool by remember { mutableStateOf(toolId) }
    val tools = listOf(
        "image-gen" to "AI Image Gen",
        "chat" to "AI Chat Assistant",
        "local" to "Local AI Utilities"
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
            tools.forEach { (id, name) ->
                FilterChip(selected = selectedTool == id, onClick = { selectedTool = id }, label = { Text(name) })
            }
        }

        when (selectedTool) {
            "image-gen" -> AiImagePromptUi()
            "chat" -> AiChatUi()
            "local" -> LocalAiUi()
            else -> LocalAiUi()
        }
    }
}

@Composable
fun AiImagePromptUi() {
    var subject by remember { mutableStateOf("Ancient temple hidden in a bioluminescent rainforest") }
    var style by remember { mutableStateOf("Cinematic") }
    var prompt by remember { mutableStateOf("") }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("AI Image Prompt Studio", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Cinematic", "Anime", "Cyberpunk", "Realistic").forEach { s ->
                    FilterChip(selected = style == s, onClick = { style = s }, label = { Text(s) })
                }
            }
            OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject Description") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { prompt = EpicAiLogic.craftImagePrompt(subject, style) }) {
                Text("Generate Optimized Prompt")
            }
            if (prompt.isNotBlank()) {
                Text("Generated Prompt:", fontWeight = FontWeight.SemiBold)
                Text(prompt, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun AiChatUi() {
    var message by remember { mutableStateOf("How do coroutines manage threading in Kotlin?") }
    var response by remember { mutableStateOf("") }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("AI Chat Assistant (Offline Knowledge Engine)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = message, onValueChange = { message = it }, label = { Text("Ask a question") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = {
                response = "Coroutines suspend execution without blocking threads using continuations and dispatchers (Dispatchers.Main, Dispatchers.IO, Dispatchers.Default)."
            }) {
                Text("Ask Assistant")
            }
            if (response.isNotBlank()) {
                Text(response, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun LocalAiUi() {
    var text by remember { mutableStateOf("This offline toolkit is awesome, fast, and completely privacy-respecting!") }
    var sentiment by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Local AI & Sentiment Analysis (100% On-Device)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = text, onValueChange = { text = it }, label = { Text("Input Text") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            Button(onClick = { sentiment = EpicAiLogic.analyzeSentiment(text) }) {
                Text("Run Local Analysis")
            }
            sentiment.forEach { (k, v) ->
                Text("$k: $v", fontWeight = FontWeight.Medium)
            }
        }
    }
}
