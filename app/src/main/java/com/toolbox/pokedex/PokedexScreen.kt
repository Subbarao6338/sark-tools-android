package com.toolbox.pokedex

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Male
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

fun getColorForName(colorName: String): Color {
    return when (colorName.lowercase()) {
        "black" -> Color(0xFF37474F)
        "blue" -> Color(0xFF1E88E5)
        "brown" -> Color(0xFF8D6E63)
        "gray" -> Color(0xFF78909C)
        "green" -> Color(0xFF43A047)
        "pink" -> Color(0xFFEC407A)
        "purple" -> Color(0xFF8E24AA)
        "red" -> Color(0xFFE53935)
        "white" -> Color(0xFFB0BEC5)
        "yellow" -> Color(0xFFFDD835)
        else -> Color(0xFF90A4AE)
    }
}

fun getShinyPalette(baseColor: Color, id: Int): Color {
    // Generate an aesthetically striking alternate shiny coloration
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(
        android.graphics.Color.rgb(
            (baseColor.red * 255).toInt(),
            (baseColor.green * 255).toInt(),
            (baseColor.blue * 255).toInt()
        ),
        hsv
    )
    val shinyHue = (hsv[0] + 180f + (id % 45)) % 360f
    val shinySat = (hsv[1] * 1.15f).coerceIn(0.4f, 1f)
    val shinyVal = (hsv[2] * 0.95f).coerceIn(0.4f, 0.95f)
    val shinyInt = android.graphics.Color.HSVToColor(floatArrayOf(shinyHue, shinySat, shinyVal))
    return Color(shinyInt)
}

@Composable
fun PokemonProceduralBadge(
    id: Int,
    name: String,
    primaryColor: Color,
    isShiny: Boolean,
    isFemale: Boolean,
    modifier: Modifier = Modifier,
    hasGenderDiff: Boolean = false,
    types: List<String> = emptyList(),
    isMega: Boolean = false,
    isGigantamax: Boolean = false,
    isLegendsZa: Boolean = false,
    isSilhouette: Boolean = false,
) {
    val displayColor = if (isSilhouette) Color(0xFF263238) else if (isShiny) getShinyPalette(primaryColor, id) else primaryColor
    val secondaryColor = if (isSilhouette) Color(0xFF37474F) else {
        val secType = types.getOrNull(1)
        if (secType != null) {
            PokemonType.fromString(secType)?.color ?: displayColor.copy(alpha = 0.7f)
        } else {
            displayColor.copy(alpha = 0.5f)
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.linearGradient(
                    colors = if (isSilhouette) {
                        listOf(Color(0xFF1E272C), Color(0xFF101417))
                    } else {
                        listOf(
                            displayColor.copy(alpha = 0.90f),
                            secondaryColor.copy(alpha = 0.45f)
                        )
                    }
                )
            )
            .border(
                1.5.dp,
                if (isSilhouette) Color(0xFF455A64) else displayColor,
                RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Background subtle radial energy glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        (if (isSilhouette) Color.Black else Color.White).copy(alpha = 0.35f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.5f, h * 0.5f),
                    radius = w * 0.55f
                ),
                radius = w * 0.55f,
                center = Offset(w * 0.5f, h * 0.5f)
            )

            // Stylized Pokeball background watermark
            drawCircle(
                color = Color.White.copy(alpha = if (isSilhouette) 0.08f else 0.18f),
                radius = w * 0.40f,
                center = Offset(w * 0.5f, h * 0.5f),
                style = Stroke(width = 2.5.dp.toPx())
            )
            drawLine(
                color = Color.White.copy(alpha = if (isSilhouette) 0.08f else 0.18f),
                start = Offset(w * 0.10f, h * 0.5f),
                end = Offset(w * 0.90f, h * 0.5f),
                strokeWidth = 2.dp.toPx()
            )
            drawCircle(
                color = Color.White.copy(alpha = if (isSilhouette) 0.12f else 0.28f),
                radius = w * 0.12f,
                center = Offset(w * 0.5f, h * 0.5f)
            )

            // Procedural Pokémon Artwork Silhouette / Creature Geometry
            val artPath = Path().apply {
                val cx = w * 0.5f
                val cy = h * 0.52f
                val archetype = (id + name.length) % 6

                when (archetype) {
                    0 -> { // Quadruped (e.g. Bulbasaur, Eevee, Arcanine)
                        moveTo(cx - w * 0.25f, cy + h * 0.22f) // back leg
                        lineTo(cx - w * 0.28f, cy - h * 0.02f) // back
                        cubicTo(cx - w * 0.20f, cy - h * 0.22f, cx - w * 0.05f, cy - h * 0.28f, cx + w * 0.12f, cy - h * 0.22f) // torso & neck
                        lineTo(cx + w * 0.24f, cy - h * 0.28f) // ear/crest
                        lineTo(cx + w * 0.28f, cy - h * 0.12f) // snout
                        lineTo(cx + w * 0.20f, cy - h * 0.02f) // chin
                        lineTo(cx + w * 0.22f, cy + h * 0.22f) // front leg
                        lineTo(cx + w * 0.12f, cy + h * 0.22f)
                        lineTo(cx + w * 0.08f, cy + h * 0.06f) // belly
                        lineTo(cx - w * 0.12f, cy + h * 0.06f)
                        lineTo(cx - w * 0.16f, cy + h * 0.22f)
                        close()
                    }
                    1 -> { // Winged / Avian (e.g. Charizard, Pidgeot, Zapdos)
                        moveTo(cx, cy + h * 0.22f) // tail
                        lineTo(cx - w * 0.35f, cy - h * 0.10f) // left wing tip
                        cubicTo(cx - w * 0.25f, cy - h * 0.24f, cx - w * 0.12f, cy - h * 0.12f, cx, cy - h * 0.18f) // neck
                        lineTo(cx + w * 0.05f, cy - h * 0.30f) // crest/head
                        lineTo(cx + w * 0.12f, cy - h * 0.18f)
                        cubicTo(cx + w * 0.22f, cy - h * 0.12f, cx + w * 0.35f, cy - h * 0.24f, cx + w * 0.35f, cy - h * 0.10f) // right wing
                        lineTo(cx + w * 0.08f, cy + h * 0.14f) // body
                        close()
                    }
                    2 -> { // Bipedal Warrior / Dragon (e.g. Mewtwo, Lucario, Gengar)
                        moveTo(cx - w * 0.18f, cy + h * 0.25f) // left foot
                        lineTo(cx - w * 0.15f, cy + h * 0.02f) // hip
                        lineTo(cx - w * 0.28f, cy - h * 0.08f) // left arm
                        lineTo(cx - w * 0.14f, cy - h * 0.12f) // shoulder
                        lineTo(cx - w * 0.12f, cy - h * 0.28f) // left ear/horn
                        lineTo(cx, cy - h * 0.22f) // crown
                        lineTo(cx + w * 0.12f, cy - h * 0.28f) // right ear/horn
                        lineTo(cx + w * 0.14f, cy - h * 0.12f)
                        lineTo(cx + w * 0.28f, cy - h * 0.08f) // right arm
                        lineTo(cx + w * 0.15f, cy + h * 0.02f)
                        lineTo(cx + w * 0.18f, cy + h * 0.25f) // right foot
                        lineTo(cx, cy + h * 0.12f) // groin
                        close()
                    }
                    3 -> { // Serpentine / Draconic (e.g. Rayquaza, Onix, Milotic, Gyarados)
                        moveTo(cx - w * 0.30f, cy + h * 0.18f)
                        cubicTo(cx - w * 0.28f, cy - h * 0.08f, cx - w * 0.10f, cy + h * 0.22f, cx, cy - h * 0.02f)
                        cubicTo(cx + w * 0.10f, cy - h * 0.22f, cx + w * 0.28f, cy - h * 0.28f, cx + w * 0.28f, cy - h * 0.15f)
                        cubicTo(cx + w * 0.20f, cy, cx - w * 0.05f, cy - h * 0.12f, cx - w * 0.12f, cy + h * 0.08f)
                        close()
                    }
                    4 -> { // Aquatic / Fish / Cetacean (e.g. Squirtle, Kyogre, Vaporeon, Lapras)
                        moveTo(cx - w * 0.32f, cy) // tail fin
                        lineTo(cx - w * 0.18f, cy - h * 0.15f) // dorsal ridge
                        cubicTo(cx - w * 0.05f, cy - h * 0.24f, cx + w * 0.18f, cy - h * 0.20f, cx + w * 0.28f, cy - h * 0.05f) // head
                        lineTo(cx + w * 0.15f, cy + h * 0.12f) // flipper
                        cubicTo(cx, cy + h * 0.22f, cx - w * 0.18f, cy + h * 0.12f, cx - w * 0.32f, cy)
                        close()
                    }
                    else -> { // Spherical / Mineral / Ghost / Fairy (e.g. Gengar, Jigglypuff, Gastly, Solosis)
                        val r = w * 0.25f
                        moveTo(cx, cy - r)
                        cubicTo(cx + r * 1.2f, cy - r, cx + r * 1.2f, cy + r, cx, cy + r)
                        cubicTo(cx - r * 1.2f, cy + r, cx - r * 1.2f, cy - r, cx, cy - r)
                        // ears / spikes
                        moveTo(cx - r * 0.8f, cy - r * 0.4f)
                        lineTo(cx - r * 1.1f, cy - r * 1.1f)
                        lineTo(cx - r * 0.3f, cy - r * 0.9f)
                        moveTo(cx + r * 0.8f, cy - r * 0.4f)
                        lineTo(cx + r * 1.1f, cy - r * 1.1f)
                        lineTo(cx + r * 0.3f, cy - r * 0.9f)
                    }
                }
            }

            // Draw creature silhouette / body
            val creatureFill = if (isSilhouette) {
                Color(0xFF1A1A1A)
            } else {
                Color.White.copy(alpha = 0.92f)
            }
            drawPath(artPath, creatureFill)

            // Stylized inner accents / eye / glowing feature
            if (!isSilhouette) {
                val eyeColor = displayColor
                val cx = w * 0.5f
                val cy = h * 0.52f
                drawCircle(
                    color = eyeColor,
                    radius = w * 0.035f,
                    center = Offset(cx + w * 0.05f, cy - h * 0.08f)
                )
                drawCircle(
                    color = Color.White,
                    radius = w * 0.015f,
                    center = Offset(cx + w * 0.06f, cy - h * 0.09f)
                )
            }

            // Mega / Z-A / Gigantamax crest
            if (isMega || isLegendsZa) {
                val megaColor = if (isLegendsZa) Color(0xFF7E57C2) else Color(0xFFFF4081)
                drawCircle(
                    color = megaColor.copy(alpha = 0.9f),
                    radius = w * 0.10f,
                    center = Offset(w * 0.82f, h * 0.20f)
                )
                drawCircle(
                    color = Color.White,
                    radius = w * 0.04f,
                    center = Offset(w * 0.82f, h * 0.20f)
                )
            } else if (isGigantamax) {
                val gmaxColor = Color(0xFFE53935)
                drawCircle(
                    color = gmaxColor.copy(alpha = 0.85f),
                    radius = w * 0.09f,
                    center = Offset(w * 0.82f, h * 0.20f)
                )
            }

            // Female heart marking
            if (isFemale && hasGenderDiff && !isSilhouette) {
                val path = Path().apply {
                    val cx = w * 0.78f
                    val cy = h * 0.25f
                    val r = w * 0.12f
                    moveTo(cx, cy + r * 0.7f)
                    cubicTo(cx - r, cy, cx - r * 0.8f, cy - r, cx, cy - r * 0.2f)
                    cubicTo(cx + r * 0.8f, cy - r, cx + r, cy, cx, cy + r * 0.7f)
                }
                drawPath(path, Color(0xFFE91E63))
            }

            // Shiny stars sparkles
            if (isShiny && !isSilhouette) {
                drawCircle(
                    color = Color(0xFFFFD700),
                    radius = 3.5.dp.toPx(),
                    center = Offset(w * 0.24f, h * 0.24f)
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.dp.toPx(),
                    center = Offset(w * 0.76f, h * 0.76f)
                )
            }
        }

        // Pokémon Number Badge
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(4.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Black.copy(alpha = 0.45f))
                .padding(horizontal = 4.dp, vertical = 1.dp)
        ) {
            Text(
                text = if (isSilhouette) "???" else "#$id",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                fontSize = 9.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokedexScreen(
    modifier: Modifier = Modifier,
) {
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
    var selectedGen by remember { mutableStateOf<Int?>(null) }
    var selectedColor by remember { mutableStateOf<String?>(null) }
    var filterOnlyForms by remember { mutableStateOf(false) }
    var filterOnlyLegendsZa by remember { mutableStateOf(false) }
    var filterOnlyGenderDiff by remember { mutableStateOf(false) }
    var globalShinyMode by remember { mutableStateOf(false) }

    var sortOption by remember { mutableStateOf(PokemonSortOption.NumberAsc) }
    var showSortMenu by remember { mutableStateOf(false) }
    var inspectedPokemon by remember { mutableStateOf<Pokemon?>(null) }

    val allColors = remember {
        listOf("Black", "Blue", "Brown", "Gray", "Green", "Pink", "Purple", "Red", "White", "Yellow")
    }

    val filteredList = remember(
        allPokemon,
        searchQuery,
        selectedType,
        selectedGen,
        selectedColor,
        filterOnlyForms,
        filterOnlyLegendsZa,
        filterOnlyGenderDiff,
        sortOption
    ) {
        val query = searchQuery.trim().lowercase()
        val filtered = allPokemon.filter { p ->
            val matchesSearch = if (query.isEmpty()) true else {
                p.name.lowercase().contains(query) ||
                p.id.toString() == query ||
                p.species.lowercase().contains(query) ||
                p.forms.any { it.name.lowercase().contains(query) }
            }
            val matchesType = selectedType == null || p.types.any { it.equals(selectedType, ignoreCase = true) }
            val matchesGen = selectedGen == null || p.generation == selectedGen
            val matchesColor = selectedColor == null || p.color.equals(selectedColor, ignoreCase = true)
            val matchesForms = !filterOnlyForms || p.forms.isNotEmpty()
            val matchesZa = !filterOnlyLegendsZa || (p.isLegendsZa || p.forms.any { it.isLegendsZa })
            val matchesGender = !filterOnlyGenderDiff || p.hasGenderDiff

            matchesSearch && matchesType && matchesGen && matchesColor && matchesForms && matchesZa && matchesGender
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
        // Search & Quick Action Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Search #1..1025, name, form...") },
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

            // Global Shiny toggle
            IconButton(
                onClick = { globalShinyMode = !globalShinyMode },
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (globalShinyMode) Color(0xFFFFD54F).copy(alpha = 0.35f)
                        else MaterialTheme.colorScheme.surfaceContainerHigh
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Shiny Mode",
                    tint = if (globalShinyMode) Color(0xFFF57F17) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Sort Menu
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

        // Generation & Form Special Filter Chips Row
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            item {
                FilterChip(
                    selected = selectedGen == null && !filterOnlyLegendsZa && !filterOnlyForms && !filterOnlyGenderDiff && selectedColor == null,
                    onClick = {
                        selectedGen = null
                        filterOnlyLegendsZa = false
                        filterOnlyForms = false
                        filterOnlyGenderDiff = false
                        selectedColor = null
                        selectedType = null
                    },
                    label = { Text("All (${filteredList.size})") },
                )
            }

            item {
                FilterChip(
                    selected = filterOnlyLegendsZa,
                    onClick = { filterOnlyLegendsZa = !filterOnlyLegendsZa },
                    label = { Text("Legends: Z-A (Megas)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF7E57C2).copy(alpha = 0.3f),
                    )
                )
            }

            item {
                FilterChip(
                    selected = filterOnlyForms,
                    onClick = { filterOnlyForms = !filterOnlyForms },
                    label = { Text("Forms / Megas") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF26A69A).copy(alpha = 0.3f),
                    )
                )
            }

            item {
                FilterChip(
                    selected = filterOnlyGenderDiff,
                    onClick = { filterOnlyGenderDiff = !filterOnlyGenderDiff },
                    label = { Text("Gender Diff (102)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFEC407A).copy(alpha = 0.3f),
                    )
                )
            }

            // Gen 1 to Gen 9 chips
            items(9) { idx ->
                val genNum = idx + 1
                FilterChip(
                    selected = selectedGen == genNum,
                    onClick = { selectedGen = if (selectedGen == genNum) null else genNum },
                    label = { Text("Gen $genNum") },
                )
            }
        }

        // Elemental Type & Color Filter Row
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items(PokemonType.entries.toTypedArray()) { pType ->
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

            // Colors
            items(allColors) { colName ->
                val cVal = getColorForName(colName)
                FilterChip(
                    selected = selectedColor.equals(colName, ignoreCase = true),
                    onClick = {
                        selectedColor = if (selectedColor.equals(colName, ignoreCase = true)) null else colName
                    },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(cVal, CircleShape)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(colName)
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = cVal.copy(alpha = 0.25f),
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
                    isGlobalShiny = globalShinyMode,
                    onClick = { inspectedPokemon = pokemon },
                )
            }
        }
    }

    // Detail Bottom Sheet
    inspectedPokemon?.let { p ->
        PokemonDetailSheet(
            pokemon = p,
            initialShiny = globalShinyMode,
            onDismiss = { inspectedPokemon = null },
        )
    }
}

@Composable
fun PokemonListItem(
    pokemon: Pokemon,
    isGlobalShiny: Boolean,
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
                // Procedural Badge with Sprite emblem & Shiny rendering
                PokemonProceduralBadge(
                    id = pokemon.id,
                    name = pokemon.name,
                    primaryColor = pokemon.primaryTypeColor,
                    isShiny = isGlobalShiny,
                    isFemale = false,
                    hasGenderDiff = pokemon.hasGenderDiff,
                    types = pokemon.types,
                    isMega = pokemon.forms.any { it.isMega },
                    isGigantamax = pokemon.forms.any { it.isGigantamax },
                    isLegendsZa = pokemon.isLegendsZa || pokemon.forms.any { it.isLegendsZa },
                    modifier = Modifier.size(58.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = pokemon.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        if (pokemon.forms.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF7E57C2).copy(alpha = 0.2f))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "${pokemon.forms.size} Forms",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF7E57C2)
                                )
                            }
                        }
                        val genderType = pokemon.genderClassification
                        when {
                            genderType.isGenderless -> {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF90A4AE).copy(alpha = 0.2f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "⚲ Genderless",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF78909C)
                                    )
                                }
                            }
                            genderType == PokemonGenderClassification.FemaleOnly -> {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFE91E63).copy(alpha = 0.15f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "100% ♀",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE91E63)
                                    )
                                }
                            }
                            genderType == PokemonGenderClassification.MaleOnly -> {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF1976D2).copy(alpha = 0.15f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "100% ♂",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1976D2)
                                    )
                                }
                            }
                            pokemon.hasGenderDiff -> {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFAB47BC).copy(alpha = 0.15f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "♂/♀ Diff",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFAB47BC)
                                    )
                                }
                            }
                            else -> {
                                Text(
                                    text = "♂/♀",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

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

                        if (pokemon.color.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(getColorForName(pokemon.color).copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = pokemon.color,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    text = "Gen ${pokemon.generation} · SPD ${pokemon.spd}",
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
    initialShiny: Boolean = false,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var isShiny by remember { mutableStateOf(initialShiny) }
    var selectedFormIndex by remember { mutableIntStateOf(-1) } // -1 means Base Form
    var selectedGenderFemale by remember(pokemon, selectedFormIndex) { mutableStateOf(pokemon.genderClassification == PokemonGenderClassification.FemaleOnly) }

    val activeName = if (selectedFormIndex >= 0 && selectedFormIndex < pokemon.forms.size) {
        pokemon.forms[selectedFormIndex].name
    } else {
        pokemon.name
    }

    val activeTypes = if (selectedFormIndex >= 0 && selectedFormIndex < pokemon.forms.size) {
        pokemon.forms[selectedFormIndex].types
    } else {
        pokemon.types
    }

    val activeHp = if (selectedFormIndex >= 0 && selectedFormIndex < pokemon.forms.size) {
        pokemon.forms[selectedFormIndex].hp
    } else {
        pokemon.hp
    }

    val activeAtk = if (selectedFormIndex >= 0 && selectedFormIndex < pokemon.forms.size) {
        pokemon.forms[selectedFormIndex].atk
    } else {
        pokemon.atk
    }

    val activeDef = if (selectedFormIndex >= 0 && selectedFormIndex < pokemon.forms.size) {
        pokemon.forms[selectedFormIndex].def
    } else {
        pokemon.def
    }

    val activeSpAtk = if (selectedFormIndex >= 0 && selectedFormIndex < pokemon.forms.size) {
        pokemon.forms[selectedFormIndex].spAtk
    } else {
        pokemon.spAtk
    }

    val activeSpDef = if (selectedFormIndex >= 0 && selectedFormIndex < pokemon.forms.size) {
        pokemon.forms[selectedFormIndex].spDef
    } else {
        pokemon.spDef
    }

    val activeSpd = if (selectedFormIndex >= 0 && selectedFormIndex < pokemon.forms.size) {
        pokemon.forms[selectedFormIndex].spd
    } else {
        pokemon.spd
    }

    val activeTotalStats = activeHp + activeAtk + activeDef + activeSpAtk + activeSpDef + activeSpd
    val activePrimaryColor = PokemonType.fromString(activeTypes.firstOrNull() ?: "")?.color ?: Color(0xFF90A4AE)
    val activeForm = if (selectedFormIndex >= 0 && selectedFormIndex < pokemon.forms.size) pokemon.forms[selectedFormIndex] else null
    val activeGenderClassification = activeForm?.genderClassification ?: pokemon.genderClassification

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
            // Header with badge and title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    PokemonProceduralBadge(
                        id = pokemon.id,
                        name = activeName,
                        primaryColor = activePrimaryColor,
                        isShiny = isShiny,
                        isFemale = selectedGenderFemale,
                        hasGenderDiff = pokemon.hasGenderDiff,
                        types = activeTypes,
                        isMega = selectedFormIndex >= 0 && pokemon.forms.getOrNull(selectedFormIndex)?.isMega == true,
                        isGigantamax = selectedFormIndex >= 0 && pokemon.forms.getOrNull(selectedFormIndex)?.isGigantamax == true,
                        isLegendsZa = (selectedFormIndex >= 0 && pokemon.forms.getOrNull(selectedFormIndex)?.isLegendsZa == true) || pokemon.isLegendsZa,
                        modifier = Modifier.size(90.dp)
                    )

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = activeName,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            if (isShiny) {
                                Text(
                                    text = "✨",
                                    fontSize = 20.sp,
                                )
                            }
                        }
                        Text(
                            text = "${pokemon.species.ifEmpty { "Pokémon" }} · Gen ${pokemon.generation} · ${pokemon.color}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            // Interactive Toggles Row: Shiny & Gender Differences
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                // Shiny Toggle Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { isShiny = !isShiny },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isShiny) Color(0xFFFFD54F).copy(alpha = 0.35f) else MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = if (isShiny) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFF57F17)) else null,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = "Shiny",
                            tint = if (isShiny) Color(0xFFF57F17) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = if (isShiny) "Shiny Form ✨" else "Normal Form",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Gender Card & Toggle for All Pokémon & Forms
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(enabled = activeGenderClassification.canBeMale && activeGenderClassification.canBeFemale) {
                            selectedGenderFemale = !selectedGenderFemale
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            activeGenderClassification.isGenderless -> MaterialTheme.colorScheme.surfaceContainerHigh
                            activeGenderClassification == PokemonGenderClassification.FemaleOnly || (selectedGenderFemale && activeGenderClassification.canBeFemale) -> Color(0xFFF48FB1).copy(alpha = 0.35f)
                            else -> Color(0xFF90CAF9).copy(alpha = 0.35f)
                        }
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = when {
                        activeGenderClassification.isGenderless -> null
                        activeGenderClassification == PokemonGenderClassification.FemaleOnly || (selectedGenderFemale && activeGenderClassification.canBeFemale) -> androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFE91E63))
                        else -> androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF1976D2))
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        when {
                            activeGenderClassification.isGenderless -> {
                                Text(
                                    text = "⚲ Genderless",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            activeGenderClassification == PokemonGenderClassification.FemaleOnly -> {
                                Icon(
                                    imageVector = Icons.Default.Female,
                                    contentDescription = "Female Only",
                                    tint = Color(0xFFE91E63)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "Female (100% ♀)",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            activeGenderClassification == PokemonGenderClassification.MaleOnly -> {
                                Icon(
                                    imageVector = Icons.Default.Male,
                                    contentDescription = "Male Only",
                                    tint = Color(0xFF1976D2)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "Male (100% ♂)",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            else -> {
                                Icon(
                                    imageVector = if (selectedGenderFemale) Icons.Default.Female else Icons.Default.Male,
                                    contentDescription = "Gender",
                                    tint = if (selectedGenderFemale) Color(0xFFE91E63) else Color(0xFF1976D2)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = if (selectedGenderFemale) "Female ♀ (Tap)" else "Male ♂ (Tap)",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            // Gender Distribution & Differences Information Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                shape = RoundedCornerShape(10.dp),
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Gender Ratio:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = activeGenderClassification.ratioDescription,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (pokemon.hasGenderDiff && pokemon.genderDiffDesc.isNotEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Female,
                                contentDescription = "Gender Differences",
                                tint = Color(0xFFE91E63),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Form Difference: ${pokemon.genderDiffDesc}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Forms & Variations Selector (Mega Evolutions, Regional, Legends: Z-A)
            if (pokemon.forms.isNotEmpty()) {
                Text(
                    text = "Alternative Forms & Megas (${pokemon.forms.size + 1})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedFormIndex == -1,
                            onClick = { selectedFormIndex = -1 },
                            label = { Text("Base Form") },
                        )
                    }

                    items(pokemon.forms.size) { idx ->
                        val f = pokemon.forms[idx]
                        FilterChip(
                            selected = selectedFormIndex == idx,
                            onClick = { selectedFormIndex = idx },
                            label = {
                                Text(
                                    when {
                                        f.isLegendsZa -> "⚡ ${f.name} (Z-A)"
                                        f.isMega -> "✨ ${f.name}"
                                        f.isGigantamax -> "🌟 ${f.name}"
                                        f.isRegional -> "🌍 ${f.name}"
                                        else -> f.name
                                    }
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (f.isLegendsZa) Color(0xFF7E57C2).copy(alpha = 0.35f)
                                else MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }

            // Types Row
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                activeTypes.forEach { t ->
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
                Text("Total: $activeTotalStats", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                shape = RoundedCornerShape(12.dp),
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatBar("HP", activeHp, 255, Color(0xFFFF5959))
                    StatBar("Attack", activeAtk, 190, Color(0xFFF5AC78))
                    StatBar("Defense", activeDef, 230, Color(0xFFFAE078))
                    StatBar("Sp. Atk", activeSpAtk, 194, Color(0xFF9DB7F5))
                    StatBar("Sp. Def", activeSpDef, 230, Color(0xFFA7DB8D))
                    StatBar("Speed", activeSpd, 200, Color(0xFFFA92B2))
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

    var isShinyChance by remember { mutableStateOf(false) }
    var targetPokemon by remember { mutableStateOf(allPokemon.random()) }
    var options by remember(targetPokemon) {
        val pool = allPokemon.filter { it.id != targetPokemon.id }.shuffled().take(3)
        mutableStateOf((pool + targetPokemon).shuffled())
    }
    var selectedId by remember { mutableStateOf<Int?>(null) }
    var isAnswerSubmitted by remember { mutableStateOf(false) }

    fun next() {
        targetPokemon = allPokemon.random()
        isShinyChance = (1..5).random() == 1 // 20% shiny appearance in quiz!
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
                PokemonProceduralBadge(
                    id = targetPokemon.id,
                    name = targetPokemon.name,
                    primaryColor = if (isAnswerSubmitted) targetPokemon.primaryTypeColor else Color.Black,
                    isShiny = isAnswerSubmitted && isShinyChance,
                    isFemale = false,
                    hasGenderDiff = targetPokemon.hasGenderDiff,
                    types = targetPokemon.types,
                    isSilhouette = !isAnswerSubmitted,
                    modifier = Modifier.size(110.dp)
                )

                Text(
                    text = if (isAnswerSubmitted) {
                        if (isShinyChance) "Shiny ${targetPokemon.name} ✨" else targetPokemon.name
                    } else "Who's That Pokémon?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = "Clue: ${targetPokemon.species} · Gen ${targetPokemon.generation} · Color: ${targetPokemon.color} · Type: ${targetPokemon.types.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
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
