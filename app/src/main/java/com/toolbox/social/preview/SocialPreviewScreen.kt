package com.toolbox.social.preview

import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toolbox.core.ui.components.TileSelector

@Composable
fun SocialPreviewScreen() {
    var platformIndex by remember { mutableStateOf(0) }
    val platforms = listOf("Twitter / X", "LinkedIn", "Facebook", "Instagram")

    var authorName by remember { mutableStateOf("Jane Developer") }
    var handle by remember { mutableStateOf("@janedev") }
    var postContent by remember { mutableStateOf("Excited to launch our new offline tool suite! 🚀 Over 60 high-precision utilities with zero tracking and full privacy. Check it out!") }
    var linkTitle by remember { mutableStateOf("Sark Tools — Precision Utility Workbench") }
    var linkUrl by remember { mutableStateOf("https://sark.tools") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TileSelector(
            options = platforms,
            selectedIndex = platformIndex,
            onSelectIndex = { platformIndex = it },
        )

        // Platform Feed Preview Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = when (platformIndex) {
                    0 -> Color(0xFF15202B) // X Dark mode
                    1 -> MaterialTheme.colorScheme.surfaceContainerHigh // LinkedIn
                    2 -> MaterialTheme.colorScheme.surfaceContainerHigh // Facebook
                    else -> MaterialTheme.colorScheme.surfaceContainerHigh // Instagram
                },
            ),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Author row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = authorName.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = authorName.ifBlank { "Author Name" },
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (platformIndex == 0) Color.White else MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = if (platformIndex == 0) handle else "1h ago • Public",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (platformIndex == 0) Color.Gray else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Post Content Body
                Text(
                    text = postContent.ifBlank { "Post content will appear here..." },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (platformIndex == 0) Color(0xFFE1E8ED) else MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp,
                )

                Spacer(modifier = Modifier.height(12.dp))

                // OpenGraph Link Preview Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (platformIndex == 0) Color(0xFF192734) else MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = linkUrl.ifBlank { "example.com" },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (platformIndex == 0) Color.Gray else MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = linkTitle.ifBlank { "Link Preview Headline" },
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (platformIndex == 0) Color.White else MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }

        // Character counts and platform limits
        val charLimit = when (platformIndex) {
            0 -> 280
            1 -> 3000
            2 -> 63206
            else -> 2200
        }
        val remaining = charLimit - postContent.length
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "${platforms[platformIndex]} preview",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "${postContent.length} / $charLimit characters ($remaining left)",
                style = MaterialTheme.typography.labelMedium,
                color = if (remaining >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
            )
        }

        // Post Customization Fields
        OutlinedTextField(
            value = postContent,
            onValueChange = { postContent = it },
            label = { Text("Post Message") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = authorName,
                onValueChange = { authorName = it },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            OutlinedTextField(
                value = handle,
                onValueChange = { handle = it },
                label = { Text("Handle (@tag)") },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
        }

        OutlinedTextField(
            value = linkTitle,
            onValueChange = { linkTitle = it },
            label = { Text("Link Preview Title") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = linkUrl,
            onValueChange = { linkUrl = it },
            label = { Text("Target URL") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
