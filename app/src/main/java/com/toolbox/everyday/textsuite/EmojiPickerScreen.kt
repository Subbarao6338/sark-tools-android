package com.toolbox.everyday.textsuite

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toolbox.core.ui.copyToClipboard

data class EmojiCategory(val name: String, val items: List<String>)

@Composable
fun EmojiPickerScreen() {
    val context = LocalContext.current
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCatIndex by rememberSaveable { mutableIntStateOf(0) }
    val recents = rememberSaveable { mutableStateListOf<String>() }

    val categories = listOf(
        EmojiCategory("Smileys", listOf("😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "🥲", "😊", "😇", "🙂", "🙃", "😉", "😌", "😍", "🥰", "😘", "😗", "😙", "😚", "😋", "😛", "😝", "😜", "🤪", "🤨", "🧐", "🤓", "😎", "🥸", "🤩", "🥳", "😏", "😒", "😞", "😔", "😟", "😕", "🙁", "😣", "😖", "😫", "😩", "🥺", "😢", "😭", "😮‍💨", "😤", "😠", "😡", "🤬", "🤯", "😳", "🥵", "🥶", "😱", "😨", "😰", "😥", "😓")),
        EmojiCategory("Gestures", listOf("👋", "🤚", "🖐️", "✋", "🖖", "👌", "🤌", "🤏", "✌️", "🤞", "🫰", "🤟", "🤘", "🤙", "👈", "👉", "👆", "🖕", "👇", "☝️", "👍", "👎", "✊", "👊", "🤛", "🤜", "👏", "🙌", "👐", "🤲", "🤝", "🙏", "✍️", "💅", "🤳", "💪")),
        EmojiCategory("Hearts", listOf("❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔", "❤️‍🔥", "❤️‍🩹", "❣️", "💕", "💞", "💓", "💗", "💖", "💘", "💝", "💟")),
        EmojiCategory("Animals", listOf("🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯", "🦁", "🐮", "🐷", "🐸", "🐵", "🐔", "🐧", "🐦", "🐤", "🦆", "🦅", "🦉", "🦇", "🐺", "🐗", "🐴", "🦄", "🐝", "🪱", "🐛", "🦋", "🐌", "🐞", "🐜", "🪰", "🪲")),
        EmojiCategory("Food", listOf("🍏", "🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🫐", "🍈", "🍒", "🍑", "🥭", "🍍", "🥥", "🥝", "🍅", "🥑", "🥦", "🌽", "🥕", "🧄", "🧅", "🥔", "🥐", "🍞", "🥖", "🥨", "🥯", "🧀", "🥚", "🍳", "🥞", "🧇", "🥓", "🥩", "🍗", "🍖", "🌭", "🍔", "🍟", "🍕")),
        EmojiCategory("Objects", listOf("💡", "🔦", "🕯️", "🧯", "🛢️", "💸", "💵", "💴", "💶", "💷", "💰", "💳", "💎", "⚖️", "🪜", "🧰", "🪛", "🔧", "🔨", "⚒️", "🛠️", "⛏️", "🪚", "🔩", "⚙️", "🪤", "🧱", "⛓️", "🧲", "🔫", "💣", "🧨", "🪓", "🔪", "🗡️", "⚔️")),
        EmojiCategory("Symbols", listOf("✨", "⭐", "🌟", "💫", "⚡", "☄️", "💥", "🔥", "🌪️", "🌈", "☀️", "🌤️", "⛅", "🌥️", "☁️", "🌧️", "⛈️", "🌩️", "❄️", "⛄", "🌊", "💧", "🫧", "🎵", "🎶", "💤", "💬", "💭", "📢", "📣", "🔔", "🔕", "🃏", "🀄", "🎴", "♠️", "♥️", "♦️", "♣️")),
        EmojiCategory("Math", listOf("➕", "➖", "➗", "✖️", "🟰", "≠", "≈", "≤", "≥", "±", "×", "÷", "√", "∑", "∏", "π", "∞", "∫", "∆", "∂", "µ", "Ω", "°", "‰", "℅", "№", "§", "¶", "©", "®", "™")),
    )

    val currentCategory = categories[selectedCatIndex]
    val displayedEmojis = if (searchQuery.isBlank()) {
        currentCategory.items
    } else {
        categories.flatMap { it.items }.filter { it.contains(searchQuery) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Symbols") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            categories.forEachIndexed { idx, cat ->
                FilterChip(
                    selected = selectedCatIndex == idx && searchQuery.isBlank(),
                    onClick = {
                        selectedCatIndex = idx
                        searchQuery = ""
                    },
                    label = { Text(cat.name) },
                )
            }
        }

        if (recents.isNotEmpty() && searchQuery.isBlank()) {
            Text("Recently Copied", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                recents.take(10).forEach { emoji ->
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clickable { copyToClipboard(context, "Emoji", emoji) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(emoji, fontSize = 24.sp)
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 48.dp),
                modifier = Modifier.fillMaxSize().padding(8.dp),
            ) {
                items(displayedEmojis) { emoji ->
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable {
                                copyToClipboard(context, "Emoji", emoji)
                                if (emoji !in recents) recents.add(0, emoji)
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = emoji, fontSize = 26.sp)
                    }
                }
            }
        }
    }
}
