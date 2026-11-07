package com.toolbox.social.quotemaker

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val quoteGradients = listOf(
    listOf(Color(0xFF1E3C72), Color(0xFF2A5298)), // Deep Royal
    listOf(Color(0xFF11998E), Color(0xFF38EF7D)), // Emerald Glow
    listOf(Color(0xFFEE0979), Color(0xFFFF6A00)), // Sunset Punch
    listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0)), // Purple Dream
    listOf(Color(0xFF232526), Color(0xFF414345)), // Sleek Noir
    listOf(Color(0xFFFF512F), Color(0xFFDD2476)), // Crimson Bloom
)

@Composable
fun QuoteMakerScreen() {
    var quoteText by remember { mutableStateOf("The best way to predict the future is to create it.") }
    var author by remember { mutableStateOf("Peter Drucker") }
    var selectedGradientIndex by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    val currentGradient = quoteGradients[selectedGradientIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Aesthetic Quote Banner Preview
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(currentGradient))
                    .padding(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        Icons.Default.FormatQuote,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp),
                        tint = Color.White.copy(alpha = 0.8f),
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "“${quoteText.ifBlank { "Your inspiring thought here..." }}”",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            fontStyle = FontStyle.Italic,
                            lineHeight = 28.sp,
                        ),
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "— ${author.ifBlank { "Anonymous" }}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.9f),
                        letterSpacing = 1.sp,
                    )
                }
            }
        }

        // Palette Selector
        Text("Background Themes", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            quoteGradients.forEachIndexed { index, gradient ->
                val isSelected = index == selectedGradientIndex
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(gradient))
                        .clickable { selectedGradientIndex = index }
                        .padding(4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                        )
                    }
                }
            }
        }

        // Share Button
        Button(
            onClick = {
                val fullText = "“$quoteText”\n\n— $author"
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, fullText)
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, "Share Quote"))
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Share Quote Text")
        }

        // Customization Inputs
        OutlinedTextField(
            value = quoteText,
            onValueChange = { quoteText = it },
            label = { Text("Quote / Thought") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
        )

        OutlinedTextField(
            value = author,
            onValueChange = { author = it },
            label = { Text("Author / Speaker") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
