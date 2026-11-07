package com.toolbox.pokedex

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toolbox.core.ui.components.TileSelector

enum class PokemonSortOption(val displayName: String) {
    NumberAsc("Number (Low to High)"),
    NumberDesc("Number (High to Low)"),
    NameAsc("Name (A to Z)"),
    TotalStatsDesc("Highest Base Stats"),
    HpDesc("Highest HP"),
    AttackDesc("Highest Attack"),
    SpeedDesc("Highest Speed"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokedexScreen(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var selectedTopTab by remember { mutableIntStateOf(0) }
    val topTabs = listOf("National Dex", "Guess The Pokémon")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        TileSelector(
            options = topTabs,
            selectedIndex = selectedTopTab,
            onSelectIndex = { selectedTopTab = it },
        )

        when (selectedTopTab) {
            0 -> NationalPokedexBrowser()
            1 -> PokemonGuessingGame()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NationalPokedexBrowser() {
    val context = LocalContext.current
    val allPokemon = remember { PokedexRepository.getAllPokemon(context) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<String?>(null) }
    var sortOption by remember { mutableStateOf(PokemonSortOption.NumberAsc) }
    var showSortMenu by remember { mutableStateOf(false) }
    var inspectedPokemon by remember { mutableStateOf<Pokemon?>(null) }

    val filteredList = remember(allPokemon, searchQuery, selectedType, sortOption) {
        val query = searchQuery.trim().lowercase()
        val filtered = allPokemon.filter { p ->
            val matchesSearch = if (query.isEmpty()) true else {
                p.name.lowercase().contains(query) ||
                p.id.toString() == query ||
                p.species.lowercase().contains(query)
            }
            val matchesType = selectedType == null || p.types.any { it.equals(selectedType, ignoreCase = true) }
            matchesSearch && matchesType
        }

        when (sortOption) {
            PokemonSortOption.NumberAsc -> filtered.sortedBy { it.id }
            PokemonSortOption.NumberDesc -> filtered.sortedByDescending { it.id }
            PokemonSortOption.NameAsc -> filtered.sortedBy { it.name }
            PokemonSortOption.TotalStatsDesc -> filtered.sortedByDescending { it.totalStats }
            PokemonSortOption.HpDesc -> filtered.sortedByDescending { it.hp }
            PokemonSortOption.AttackDesc -> filtered.sortedByDescending { it.atk }
            PokemonSortOption.SpeedDesc -> filtered.sortedByDescending { it.spd }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Search & Sort Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Search #ID or Pokémon name...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
            )

            Box {
                IconButton(onClick = { showSortMenu = true }) {
                    Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort")
                }
                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false },
                ) {
                    PokemonSortOption.entries.forEach { opt ->
                        DropdownMenuItem(
                            text = { Text(opt.displayName) },
                            onClick = {
                                sortOption = opt
                                showSortMenu = false
                            },
                        )
                    }
                }
            }
        }

        // Elemental Type Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FilterChip(
                selected = selectedType == null,
                onClick = { selectedType = null },
                label = { Text("All (${filteredList.size})") },
            )

            PokemonType.entries.forEach { pType ->
                FilterChip(
                    selected = selectedType.equals(pType.typeName, ignoreCase = true),
                    onClick = {
                        selectedType = if (selectedType.equals(pType.typeName, ignoreCase = true)) null else pType.typeName
                    },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(pType.color, CircleShape)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(pType.typeName)
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = pType.color.copy(alpha = 0.25f),
                    ),
                )
            }
        }

        // Pokémon List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(filteredList, key = { it.id }) { pokemon ->
                PokemonListItem(
                    pokemon = pokemon,
                    onClick = { inspectedPokemon = pokemon },
                )
            }
        }
    }

    // Detail Bottom Sheet
    inspectedPokemon?.let { p ->
        PokemonDetailSheet(
            pokemon = p,
            onDismiss = { inspectedPokemon = null },
        )
    }
}

@Composable
fun PokemonListItem(
    pokemon: Pokemon,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Number badge
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(pokemon.primaryTypeColor.copy(alpha = 0.35f))
                        .border(1.5.dp, pokemon.primaryTypeColor, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "#${pokemon.id}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }

                Column {
                    Text(
                        text = pokemon.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        pokemon.types.forEach { t ->
                            val typeColor = PokemonType.fromString(t)?.color ?: Color.Gray
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(typeColor.copy(alpha = 0.25f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                            ) {
                                Text(
                                    text = t,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "BST: ${pokemon.totalStats}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace,
                )
                Text(
                    text = "SPD: ${pokemon.spd} · ATK: ${pokemon.atk}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailSheet(
    pokemon: Pokemon,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(pokemon.primaryTypeColor.copy(alpha = 0.35f))
                            .border(2.dp, pokemon.primaryTypeColor, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "#${pokemon.id}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Column {
                        Text(
                            text = pokemon.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = pokemon.species.ifEmpty { "Pokémon" },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            // Types Row
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pokemon.types.forEach { t ->
                    val typeColor = PokemonType.fromString(t)?.color ?: Color.Gray
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(typeColor)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text = t.uppercase(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                    }
                }
            }

            HorizontalDivider()

            // Lore / Description
            Text("Pokedex Lore", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    text = pokemon.description.ifEmpty { "Data regarding this Pokémon is currently being researched by Pokémon professors." },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(14.dp),
                    lineHeight = 22.sp,
                )
            }

            // Physical Profile
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Height", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(pokemon.height, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Weight", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(pokemon.weight, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Base Stats Radar / Bar Chart
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Base Stats", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Total: ${pokemon.totalStats}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                shape = RoundedCornerShape(12.dp),
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatBar("HP", pokemon.hp, 255, Color(0xFFFF5959))
                    StatBar("Attack", pokemon.atk, 190, Color(0xFFF5AC78))
                    StatBar("Defense", pokemon.def, 230, Color(0xFFFAE078))
                    StatBar("Sp. Atk", pokemon.spAtk, 194, Color(0xFF9DB7F5))
                    StatBar("Sp. Def", pokemon.spDef, 230, Color(0xFFA7DB8D))
                    StatBar("Speed", pokemon.spd, 200, Color(0xFFFA92B2))
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun StatBar(
    label: String,
    value: Int,
    maxVal: Int,
    color: Color,
) {
    val progress = (value.toFloat() / maxVal).coerceIn(0f, 1f)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = label,
            modifier = Modifier.width(60.dp),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "$value",
            modifier = Modifier.width(36.dp),
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        )
    }
}

@Composable
fun PokemonGuessingGame() {
    val context = LocalContext.current
    val allPokemon = remember { PokedexRepository.getAllPokemon(context) }
    var streak by remember { mutableIntStateOf(0) }
    var bestStreak by remember { mutableIntStateOf(0) }

    var targetPokemon by remember { mutableStateOf(allPokemon.random()) }
    var options by remember(targetPokemon) {
        val pool = allPokemon.filter { it.id != targetPokemon.id }.shuffled().take(3)
        mutableStateOf((pool + targetPokemon).shuffled())
    }
    var selectedId by remember { mutableStateOf<Int?>(null) }
    var isAnswerSubmitted by remember { mutableStateOf(false) }

    fun next() {
        targetPokemon = allPokemon.random()
        selectedId = null
        isAnswerSubmitted = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Streaks
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = "Streak: $streak 🔥",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                )
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = "Best: $bestStreak 🏆",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                )
            }
        }

        // Silhouette / Mystery Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(if (isAnswerSubmitted) targetPokemon.primaryTypeColor else Color.Black),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (isAnswerSubmitted) "#${targetPokemon.id}" else "?",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }

                Text(
                    text = if (isAnswerSubmitted) targetPokemon.name else "Who's That Pokémon?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = "Clue: ${targetPokemon.species} · Type: ${targetPokemon.types.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // 4 Options
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            options.forEach { opt ->
                val isSelected = selectedId == opt.id
                val isCorrect = opt.id == targetPokemon.id
                val btnColor = when {
                    !isAnswerSubmitted && isSelected -> MaterialTheme.colorScheme.primaryContainer
                    isAnswerSubmitted && isCorrect -> Color(0xFF81C784)
                    isAnswerSubmitted && isSelected && !isCorrect -> Color(0xFFE57373)
                    else -> MaterialTheme.colorScheme.surfaceContainerHigh
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !isAnswerSubmitted) { selectedId = opt.id },
                    colors = CardDefaults.cardColors(containerColor = btnColor),
                    shape = RoundedCornerShape(12.dp),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                ) {
                    Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = opt.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }

        // Action Button
        if (!isAnswerSubmitted) {
            Button(
                onClick = {
                    isAnswerSubmitted = true
                    if (selectedId == targetPokemon.id) {
                        streak++
                        if (streak > bestStreak) bestStreak = streak
                    } else {
                        streak = 0
                    }
                },
                enabled = selectedId != null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Confirm Guess", fontWeight = FontWeight.Bold)
            }
        } else {
            Button(
                onClick = { next() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Next Pokémon ➔", fontWeight = FontWeight.Bold)
            }
        }
    }
}
