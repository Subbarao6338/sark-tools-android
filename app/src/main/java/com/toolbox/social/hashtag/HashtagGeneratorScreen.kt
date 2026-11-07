package com.toolbox.social.hashtag

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

val hashtagCategories = mapOf(
    "Photography" to listOf("#photography", "#photooftheday", "#naturephotography", "#streetphotography", "#portrait", "#visualsoflife", "#artofvisuals", "#canon", "#mobilephotography", "#landscape"),
    "Fitness" to listOf("#fitness", "#workout", "#gym", "#fitlife", "#bodybuilding", "#healthylifestyle", "#motivation", "#cardio", "#training", "#wellness"),
    "Travel" to listOf("#travel", "#wanderlust", "#travelgram", "#explore", "#vacation", "#adventure", "#instatravel", "#tourism", "#passportready", "#traveler"),
    "Tech & Coding" to listOf("#tech", "#coding", "#developer", "#softwareengineer", "#androiddev", "#kotlin", "#programming", "#innovation", "#ai", "#webdev"),
    "Food & Cooking" to listOf("#foodie", "#instafood", "#delicious", "#homemade", "#chef", "#yummy", "#cooking", "#dinner", "#foodlover", "#healthyfood"),
    "Motivation" to listOf("#motivation", "#mindset", "#success", "#inspiration", "#goals", "#positivevibes", "#dailygrind", "#focus", "#growth", "#hustle"),
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HashtagGeneratorScreen() {
    var selectedCategory by remember { mutableStateOf("Photography") }
    val selectedTags = remember { mutableStateListOf<String>() }
    val clipboard = LocalClipboardManager.current
    var customTag by remember { mutableStateOf("") }

    val currentCategoryTags = hashtagCategories[selectedCategory] ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Category Selector
        Text("Categories", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            hashtagCategories.keys.forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat) },
                )
            }
        }

        // Available Tags Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Tap to Add to Collection", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Button(
                        onClick = {
                            currentCategoryTags.forEach { if (it !in selectedTags) selectedTags.add(it) }
                        },
                    ) {
                        Text("Add All")
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    currentCategoryTags.forEach { tag ->
                        val isAdded = tag in selectedTags
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isAdded) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    if (isAdded) selectedTags.remove(tag) else selectedTags.add(tag)
                                },
                        ) {
                            Text(
                                text = tag,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isAdded) FontWeight.Bold else FontWeight.Normal,
                                color = if (isAdded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }
        }

        // Custom Tag Input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = customTag,
                onValueChange = { customTag = it },
                label = { Text("Add custom tag") },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    val formatted = if (customTag.startsWith("#")) customTag.trim() else "#${customTag.trim()}"
                    if (formatted.length > 1 && formatted !in selectedTags) {
                        selectedTags.add(formatted)
                        customTag = ""
                    }
                },
            ) {
                Text("Add")
            }
        }

        // Selected Collection Summary
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Selected Tags (${selectedTags.size}/30)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    if (selectedTags.isNotEmpty()) {
                        Button(
                            onClick = {
                                val allJoined = selectedTags.joinToString(" ")
                                clipboard.setText(AnnotatedString(allJoined))
                            },
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy All")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                if (selectedTags.isEmpty()) {
                    Text(
                        text = "No hashtags selected yet. Pick from categories above or add custom ones.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    )
                } else {
                    Text(
                        text = selectedTags.joinToString(" "),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }
    }
}
