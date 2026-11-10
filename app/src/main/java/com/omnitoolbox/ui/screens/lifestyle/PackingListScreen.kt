package omni.toolbox.ui.screens.lifestyle

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import org.json.JSONArray
import org.json.JSONObject

data class PackingItem(
    val name: String,
    val packed: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackingListScreen(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("packing_list_prefs", Context.MODE_PRIVATE) }

    // State list for packing items loaded persistently
    var itemsList by remember { mutableStateOf(loadPackingItems(context)) }

    // Prepopulate default items if empty and never run before
    LaunchedEffect(Unit) {
        if (!sharedPrefs.contains("packing_items_json")) {
            val initial = listOf(
                PackingItem("Passport & Tickets", true),
                PackingItem("Phone & Charger", false),
                PackingItem("Toothbrush & Toothpaste", false),
                PackingItem("Fresh Clothes", false),
                PackingItem("First Aid Kit", false)
            )
            savePackingItems(context, initial)
            itemsList = initial
        }
    }

    var newItemName by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") } // All, Packed, Remaining

    // Filter items list
    val filteredItems = remember(itemsList, selectedFilter) {
        itemsList.filter { item ->
            when (selectedFilter) {
                "Packed" -> item.packed
                "Remaining" -> !item.packed
                else -> true
            }
        }
    }

    // Calculations
    val totalCount = itemsList.size
    val packedCount = itemsList.count { it.packed }
    val progressFraction = if (totalCount > 0) packedCount.toFloat() / totalCount.toFloat() else 0f

    ToolScreen(
        title = "Packing List",
        onBack = { navController.popBackStack() },
        actions = {
            if (itemsList.isNotEmpty()) {
                IconButton(onClick = {
                    itemsList = emptyList()
                    savePackingItems(context, emptyList())
                    Toast.makeText(context, "List cleared.", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(Icons.Default.ClearAll, contentDescription = "Clear All")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary Panel & Progress Indicator
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.TravelExplore, null, tint = MaterialTheme.colorScheme.primary)
                            Text("Travel Preparation", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        }
                        Text(
                            "$packedCount of $totalCount packed",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )

                    val progressPercent = (progressFraction * 100).toInt()
                    Text(
                        text = if (totalCount == 0) "No items on your list yet."
                               else if (packedCount == totalCount) "🎉 Outstanding! You are fully packed and ready to go!"
                               else "You have completed $progressPercent% of your preparations.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Input fields
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = newItemName,
                    onValueChange = { newItemName = it },
                    placeholder = { Text("e.g., Swimwear, Sunglasses...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Button(
                    onClick = {
                        val trimmed = newItemName.trim()
                        if (trimmed.isNotEmpty()) {
                            if (itemsList.any { it.name.equals(trimmed, ignoreCase = true) }) {
                                Toast.makeText(context, "$trimmed is already on your list!", Toast.LENGTH_SHORT).show()
                            } else {
                                val updated = itemsList + PackingItem(trimmed, false)
                                itemsList = updated
                                savePackingItems(context, updated)
                                newItemName = ""
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Item")
                }
            }

            // Tabs/Filters for list
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Packed", "Remaining").forEach { filter ->
                    val isSelected = selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp),
                        border = null,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Item List Column
            if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (selectedFilter) {
                            "Packed" -> "No packed items yet."
                            "Remaining" -> "No remaining items! Ready to travel!"
                            else -> "Your packing list is empty."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredItems, key = { it.name }) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    if (item.packed) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                                    RoundedCornerShape(12.dp)
                                ),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (item.packed) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.05f)
                                                 else MaterialTheme.colorScheme.surface
                            ),
                            onClick = {
                                val updated = itemsList.map {
                                    if (it.name == item.name) it.copy(packed = !it.packed) else it
                                }
                                itemsList = updated
                                savePackingItems(context, updated)
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Checkbox(
                                        checked = item.packed,
                                        onCheckedChange = { checked ->
                                            val updated = itemsList.map {
                                                if (it.name == item.name) it.copy(packed = checked) else it
                                            }
                                            itemsList = updated
                                            savePackingItems(context, updated)
                                        }
                                    )
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = if (item.packed) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                                else MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        val updated = itemsList.filter { it.name != item.name }
                                        itemsList = updated
                                        savePackingItems(context, updated)
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete item",
                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Utility storage helpers
private fun loadPackingItems(context: Context): List<PackingItem> {
    val prefs = context.getSharedPreferences("packing_list_prefs", Context.MODE_PRIVATE)
    val jsonStr = prefs.getString("packing_items_json", null) ?: return emptyList()
    val list = mutableListOf<PackingItem>()
    try {
        val array = JSONArray(jsonStr)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(
                PackingItem(
                    name = obj.getString("name"),
                    packed = obj.optBoolean("packed", false)
                )
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return list
}

private fun savePackingItems(context: Context, list: List<PackingItem>) {
    val prefs = context.getSharedPreferences("packing_list_prefs", Context.MODE_PRIVATE)
    try {
        val array = JSONArray()
        for (item in list) {
            val obj = JSONObject()
            obj.put("name", item.name)
            obj.put("packed", item.packed)
            array.put(obj)
        }
        prefs.edit { putString("packing_items_json", array.toString()) }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
