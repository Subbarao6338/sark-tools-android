package com.toolbox.education

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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toolbox.core.ui.components.TileSelector

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PeriodicTableScreen(
    modifier: Modifier = Modifier,
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ElementCategory?>(null) }
    var selectedState by remember { mutableStateOf<ElementState?>(null) }
    var viewMode by remember { mutableStateOf(0) } // 0: Grid (18 columns), 1: List
    var inspectedElement by remember { mutableStateOf<ElementDetails?>(null) }

    val filteredElements = remember(searchQuery, selectedCategory, selectedState) {
        PeriodicTableData.elements.filter { element ->
            val matchesSearch = if (searchQuery.isBlank()) true else {
                element.name.contains(searchQuery, ignoreCase = true) ||
                element.symbol.contains(searchQuery, ignoreCase = true) ||
                element.number.toString() == searchQuery.trim()
            }
            val matchesCategory = selectedCategory == null || element.category == selectedCategory
            val matchesState = selectedState == null || element.standardState == selectedState
            matchesSearch && matchesCategory && matchesState
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search 118 elements by name, symbol, or number...") },
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

        // View Mode Selector & Category quick chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TileSelector(
                options = listOf("Table Grid", "Element List"),
                selectedIndex = viewMode,
                onSelectIndex = { viewMode = it },
                modifier = Modifier.weight(1f),
            )
        }

        // Filters: Category and State Filter Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FilterChip(
                selected = selectedCategory == null && selectedState == null,
                onClick = {
                    selectedCategory = null
                    selectedState = null
                },
                label = { Text("All (${filteredElements.size})") },
            )

            ElementState.entries.forEach { state ->
                FilterChip(
                    selected = selectedState == state,
                    onClick = {
                        selectedState = if (selectedState == state) null else state
                    },
                    label = { Text(state.displayName) },
                )
            }

            ElementCategory.entries.forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = {
                        selectedCategory = if (selectedCategory == cat) null else cat
                    },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(cat.color, CircleShape)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(cat.displayName)
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = cat.color.copy(alpha = 0.25f),
                        selectedLabelColor = MaterialTheme.colorScheme.onSurface,
                    ),
                )
            }
        }

        // View Content
        if (viewMode == 0) {
            // Periodic Table 18-Column Interactive Grid
            PeriodicTableGridView(
                filteredElements = filteredElements,
                onElementClick = { inspectedElement = it },
                modifier = Modifier.weight(1f),
            )
        } else {
            // Element List View
            ElementListView(
                elements = filteredElements,
                onElementClick = { inspectedElement = it },
                modifier = Modifier.weight(1f),
            )
        }
    }

    // Element Detail Bottom Sheet
    inspectedElement?.let { element ->
        ElementDetailSheet(
            element = element,
            onDismiss = { inspectedElement = null },
        )
    }
}

@Composable
private fun PeriodicTableGridView(
    filteredElements: List<ElementDetails>,
    onElementClick: (ElementDetails) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hScroll = rememberScrollState()
    val vScroll = rememberScrollState()
    val filteredNumbers = remember(filteredElements) { filteredElements.map { it.number }.toSet() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
            .padding(6.dp)
            .horizontalScroll(hScroll)
            .verticalScroll(vScroll),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        // Group column headers 1 to 18
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                Text("P\\G", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            (1..18).forEach { g ->
                Box(
                    modifier = Modifier.size(width = 54.dp, height = 24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "$g",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }

        // Main Periods 1 to 7
        (1..7).forEach { period ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Period number label
                Box(
                    modifier = Modifier.size(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "$period",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                (1..18).forEach { group ->
                    val element = PeriodicTableData.elements.find {
                        // Main table excludes Lanthanides (57-71 in period 6) and Actinides (89-103 in period 7) except placeholder/group 3
                        it.period == period && it.group == group && !(period == 6 && it.number in 58..71) && !(period == 7 && it.number in 90..103)
                    }

                    if (period == 6 && group == 3) {
                        // Lanthanide marker
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(ElementCategory.Lanthanide.color.copy(alpha = 0.2f))
                                .border(1.dp, ElementCategory.Lanthanide.color, RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("57-71\nLa-Lu", fontSize = 9.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                        }
                    } else if (period == 7 && group == 3) {
                        // Actinide marker
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(ElementCategory.Actinide.color.copy(alpha = 0.2f))
                                .border(1.dp, ElementCategory.Actinide.color, RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("89-103\nAc-Lr", fontSize = 9.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                        }
                    } else if (element != null) {
                        val isHighlighted = element.number in filteredNumbers
                        ElementGridCell(
                            element = element,
                            isHighlighted = isHighlighted,
                            onClick = { onElementClick(element) },
                        )
                    } else {
                        // Empty slot
                        Spacer(modifier = Modifier.size(54.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Lanthanides row (57 to 71)
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.size(width = 112.dp, height = 54.dp)) // Spacer for groups 1-2
            (57..71).forEach { num ->
                val el = PeriodicTableData.getByNumber(num)
                if (el != null) {
                    val isHighlighted = el.number in filteredNumbers
                    ElementGridCell(
                        element = el,
                        isHighlighted = isHighlighted,
                        onClick = { onElementClick(el) },
                    )
                }
            }
        }

        // Actinides row (89 to 103)
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.size(width = 112.dp, height = 54.dp)) // Spacer for groups 1-2
            (89..103).forEach { num ->
                val el = PeriodicTableData.getByNumber(num)
                if (el != null) {
                    val isHighlighted = el.number in filteredNumbers
                    ElementGridCell(
                        element = el,
                        isHighlighted = isHighlighted,
                        onClick = { onElementClick(el) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ElementGridCell(
    element: ElementDetails,
    isHighlighted: Boolean,
    onClick: () -> Unit,
) {
    val alpha = if (isHighlighted) 1f else 0.25f
    val bgColor = element.category.color.copy(alpha = if (isHighlighted) 0.35f else 0.1f)
    val borderColor = if (isHighlighted) element.category.color else Color.Transparent

    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(2.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "${element.number}",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
                )
                Text(
                    text = "%.1f".format(element.atomicMass),
                    fontSize = 7.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha),
                )
            }
            Text(
                text = element.symbol,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
            )
            Text(
                text = element.name,
                fontSize = 7.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha),
            )
        }
    }
}

@Composable
private fun ElementListView(
    elements: List<ElementDetails>,
    onElementClick: (ElementDetails) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(elements, key = { it.number }) { el ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onElementClick(el) },
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
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(el.category.color.copy(alpha = 0.35f))
                                .border(1.5.dp, el.category.color, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${el.number}",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = el.symbol,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                )
                            }
                        }

                        Column {
                            Text(
                                text = el.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = el.category.displayName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                Text("•", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    text = "${el.standardState.displayName} (${el.block}-block)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "%.3f u".format(el.atomicMass),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace,
                        )
                        Text(
                            text = "P: ${el.period} · G: ${el.group}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElementDetailSheet(
    element: ElementDetails,
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
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(element.category.color.copy(alpha = 0.35f))
                            .border(2.dp, element.category.color, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${element.number}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(element.symbol, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    Column {
                        Text(
                            text = element.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = element.category.displayName,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            HorizontalDivider()

            // Key Properties Grid
            Text(
                text = "Physical & Chemical Properties",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                shape = RoundedCornerShape(12.dp),
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    DetailRow("Atomic Number", "${element.number}")
                    DetailRow("Standard Atomic Weight", "%.4f u".format(element.atomicMass))
                    DetailRow("Standard State", element.standardState.displayName)
                    DetailRow("Period / Group / Block", "Period ${element.period}, Group ${element.group}, ${element.block}-block")
                    DetailRow("Electron Configuration", element.electronConfiguration.ifEmpty { "N/A" })
                    DetailRow("Melting Point", element.meltingPointK?.let { "%.2f K (%.1f °C)".format(it, it - 273.15) } ?: "Unknown")
                    DetailRow("Boiling Point", element.boilingPointK?.let { "%.2f K (%.1f °C)".format(it, it - 273.15) } ?: "Unknown")
                    DetailRow("Density", element.density?.let { "$it g/cm³" } ?: "Unknown")
                    DetailRow("Discovered By", element.discoveredBy)
                }
            }

            // Summary / Lore
            Text(
                text = "Overview & Characteristics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    text = element.summary.ifEmpty { "No summary available." },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(14.dp),
                    lineHeight = 22.sp,
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End,
        )
    }
}
