package com.toolbox.lifestyle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toolbox.core.ui.copyToClipboard

@Composable
fun QuoteGeneratorScreen() {
    val context = LocalContext.current
    val quotes = listOf(
        Pair("The only way to do great work is to love what you do.", "Steve Jobs"),
        Pair("Simplicity is prerequisite for reliability.", "Edsger W. Dijkstra"),
        Pair("Code is like humor. When you have to explain it, it’s bad.", "Cory House"),
        Pair("First, solve the problem. Then, write the code.", "John Johnson"),
        Pair("In the middle of difficulty lies opportunity.", "Albert Einstein"),
        Pair("It always seems impossible until it's done.", "Nelson Mandela"),
        Pair("Act as if what you do makes a difference. It does.", "William James"),
        Pair("Success is not final, failure is not fatal: it is the courage to continue that counts.", "Winston Churchill")
    )

    var currentQuote by remember { mutableStateOf(quotes.first()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Inspirational & Wisdom Quotes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Curated offline thoughts on engineering, perseverance, philosophy, and creativity.")
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "“${currentQuote.first}”",
                    style = MaterialTheme.typography.titleLarge,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "— ${currentQuote.second}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Button(
            onClick = {
                val next = quotes.filter { it != currentQuote }.random()
                currentQuote = next
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate Next Quote")
        }

        OutlinedButton(
            onClick = {
                copyToClipboard(context, "Quote", "“${currentQuote.first}” — ${currentQuote.second}")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Copy Quote")
        }
    }
}
