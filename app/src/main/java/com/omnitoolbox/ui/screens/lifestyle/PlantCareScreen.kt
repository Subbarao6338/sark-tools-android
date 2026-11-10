package omni.toolbox.ui.screens.lifestyle

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

data class Plant(
    val id: String,
    val name: String,
    val location: String,
    val intervalDays: Int,
    val lastWatered: Long,
    val notes: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantCareScreen(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("plant_care_prefs", Context.MODE_PRIVATE) }

    // State list for plants loaded persistently
    var plantsList by remember { mutableStateOf(loadPlants(context)) }

    // Prepopulate on first run
    LaunchedEffect(Unit) {
        if (!sharedPrefs.contains("plants_list_json")) {
            val initial = listOf(
                Plant("p1", "Monstera Deliciosa", "Indoor", 7, System.currentTimeMillis() - TimeUnit.DAYS.toMillis(2), "Keep in bright indirect sunlight. Wipe leaves monthly."),
                Plant("p2", "Snake Plant", "Indoor", 14, System.currentTimeMillis() - TimeUnit.DAYS.toMillis(15), "Extremely drought tolerant. Water only when soil is completely dry."),
                Plant("p3", "Aloe Vera", "Balcony", 10, System.currentTimeMillis() - TimeUnit.DAYS.toMillis(3), "Thrives in dry, warm conditions. Direct sunlight is great.")
            )
            savePlants(context, initial)
            plantsList = initial
        }
    }

    // Filter and search query states
    var searchQuery by remember { mutableStateOf("") }
    var selectedLocationFilter by remember { mutableStateOf("All") }

    // Dialog state management
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Plant?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf<Plant?>(null) }

    // Available categories
    val locations = listOf("All", "Indoor", "Outdoor", "Office", "Balcony")

    // Filter plants list
    val filteredPlants = remember(plantsList, searchQuery, selectedLocationFilter) {
        plantsList.filter { plant ->
            val matchesSearch = plant.name.contains(searchQuery, ignoreCase = true) ||
                    plant.notes.contains(searchQuery, ignoreCase = true)
            val matchesFilter = selectedLocationFilter == "All" || plant.location == selectedLocationFilter
            matchesSearch && matchesFilter
        }
    }

    // Status Summary calculations
    val totalPlants = plantsList.size
    val needsWaterCount = plantsList.count { isOverdue(it) }
    val healthyCount = totalPlants - needsWaterCount

    ToolScreen(
        title = "Plant Care",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Summary Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Eco,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                "Foliage Status Summary",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SummaryStatItem("Total", totalPlants.toString(), MaterialTheme.colorScheme.primary)
                            SummaryStatItem("Overdue", needsWaterCount.toString(), if (needsWaterCount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline)
                            SummaryStatItem("Healthy", healthyCount.toString(), MaterialTheme.colorScheme.secondary)
                        }
                    }
                }

                // Search & Filter Row
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search your plants...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear search")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    // Scrollable Horizontal Filter Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        locations.forEach { loc ->
                            val isSelected = selectedLocationFilter == loc
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedLocationFilter = loc },
                                label = { Text(loc, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                shape = RoundedCornerShape(8.dp),
                                border = null
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Plant List
                if (filteredPlants.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Eco,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                modifier = Modifier.size(72.dp)
                            )
                            Text(
                                if (searchQuery.isNotEmpty() || selectedLocationFilter != "All") "No matching plants found" else "No plants tracked yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                if (searchQuery.isNotEmpty() || selectedLocationFilter != "All") "Try modifying your search or filters" else "Tap the '+' floating button to add your first plant!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f),
                                modifier = Modifier.padding(horizontal = 32.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredPlants, key = { it.id }) { plant ->
                            PlantCard(
                                plant = plant,
                                onWaterNow = {
                                    val updated = plantsList.map { p ->
                                        if (p.id == plant.id) p.copy(lastWatered = System.currentTimeMillis()) else p
                                    }
                                    plantsList = updated
                                    savePlants(context, updated)
                                    Toast.makeText(context, "${plant.name} successfully watered!", Toast.LENGTH_SHORT).show()
                                },
                                onEdit = { showEditDialog = plant },
                                onDelete = { showDeleteConfirmation = plant }
                            )
                        }
                    }
                }
            }

            // Floating Action Button
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Plant")
            }
        }
    }

    // Dialogs
    if (showAddDialog) {
        PlantFormDialog(
            title = "Add New Plant",
            onDismiss = { showAddDialog = false },
            onSave = { name, loc, days, notes ->
                val newPlant = Plant(
                    id = "plant_" + System.currentTimeMillis(),
                    name = name,
                    location = loc,
                    intervalDays = days,
                    lastWatered = System.currentTimeMillis(),
                    notes = notes
                )
                val updated = plantsList + newPlant
                plantsList = updated
                savePlants(context, updated)
                showAddDialog = false
                Toast.makeText(context, "$name successfully added!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    showEditDialog?.let { plantToEdit ->
        PlantFormDialog(
            title = "Edit Plant Details",
            initialPlant = plantToEdit,
            onDismiss = { showEditDialog = null },
            onSave = { name, loc, days, notes ->
                val updated = plantsList.map { p ->
                    if (p.id == plantToEdit.id) {
                        p.copy(name = name, location = loc, intervalDays = days, notes = notes)
                    } else p
                }
                plantsList = updated
                savePlants(context, updated)
                showEditDialog = null
                Toast.makeText(context, "Plant details updated!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    showDeleteConfirmation?.let { plantToDelete ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = null },
            title = { Text("Delete Plant?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you absolutely sure you want to stop tracking ${plantToDelete.name}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val updated = plantsList.filter { it.id != plantToDelete.id }
                        plantsList = updated
                        savePlants(context, updated)
                        showDeleteConfirmation = null
                        Toast.makeText(context, "${plantToDelete.name} deleted.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SummaryStatItem(label: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
    }
}

@Composable
fun PlantCard(
    plant: Plant,
    onWaterNow: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isWaterNeeded = isOverdue(plant)
    val remainingDaysText = getRemainingTimeText(plant)

    val cardBorderModifier = if (isWaterNeeded) {
        Modifier.border(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
    } else {
        Modifier.border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
    }

    Card(
        modifier = Modifier.fillMaxWidth().then(cardBorderModifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isWaterNeeded) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.08f)
                             else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            plant.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                plant.location,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "Water every ${plant.intervalDays} days",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Edit & Delete icons
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Plant",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Plant",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Last Watered Details
            val format = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault())
            val lastWateredStr = format.format(Date(plant.lastWatered))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    "Last Watered: $lastWateredStr",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            // Note section if present
            if (plant.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        plant.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action / Countdown Status Footer Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Countdown Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isWaterNeeded) MaterialTheme.colorScheme.errorContainer
                            else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            if (isWaterNeeded) Icons.Default.Warning else Icons.Default.Eco,
                            contentDescription = null,
                            tint = if (isWaterNeeded) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            remainingDaysText,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isWaterNeeded) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Interactive Water Button
                Button(
                    onClick = onWaterNow,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isWaterNeeded) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        Icons.Default.Opacity,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Water Now", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun PlantFormDialog(
    title: String,
    initialPlant: Plant? = null,
    onDismiss: () -> Unit,
    onSave: (name: String, location: String, intervalDays: Int, notes: String) -> Unit
) {
    var name by remember { mutableStateOf(initialPlant?.name ?: "") }
    var location by remember { mutableStateOf(initialPlant?.location ?: "Indoor") }
    var intervalStr by remember { mutableStateOf(initialPlant?.intervalDays?.toString() ?: "7") }
    var notes by remember { mutableStateOf(initialPlant?.notes ?: "") }

    val locations = listOf("Indoor", "Outdoor", "Office", "Balcony")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Plant Name") },
                    placeholder = { Text("e.g. Fiddle Leaf Fig") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                // Location selector dropdown or chips
                Column {
                    Text("Location Category", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        locations.forEach { loc ->
                            val isSelected = location == loc
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .clickable { location = loc }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    loc,
                                    fontSize = 12.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = intervalStr,
                    onValueChange = { intervalStr = it },
                    label = { Text("Watering Interval (Days)") },
                    placeholder = { Text("e.g. 7") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Care Instructions / Notes") },
                    placeholder = { Text("e.g. Keep in shade, water once a week") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val days = intervalStr.toIntOrNull() ?: 7
                    if (name.trim().isNotEmpty()) {
                        onSave(name.trim(), location, days, notes.trim())
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Utility logic functions
private fun isOverdue(plant: Plant): Boolean {
    val elapsedMillis = System.currentTimeMillis() - plant.lastWatered
    val intervalMillis = plant.intervalDays * 24L * 60L * 60L * 1000L
    return elapsedMillis >= intervalMillis
}

private fun getRemainingTimeText(plant: Plant): String {
    val elapsedMillis = System.currentTimeMillis() - plant.lastWatered
    val intervalMillis = plant.intervalDays * 24L * 60L * 60L * 1000L
    val remainingMillis = intervalMillis - elapsedMillis

    return if (remainingMillis <= 0) {
        val overdueDays = TimeUnit.MILLISECONDS.toDays(-remainingMillis).toInt()
        if (overdueDays <= 0) "Water Needed Today!" else "Overdue by $overdueDays days"
    } else {
        val daysLeft = TimeUnit.MILLISECONDS.toDays(remainingMillis).toInt()
        if (daysLeft <= 0) {
            val hoursLeft = TimeUnit.MILLISECONDS.toHours(remainingMillis).toInt()
            if (hoursLeft <= 0) "Water in < 1 hour" else "Water in $hoursLeft hours"
        } else {
            "Water in $daysLeft days"
        }
    }
}

private fun loadPlants(context: Context): List<Plant> {
    val prefs = context.getSharedPreferences("plant_care_prefs", Context.MODE_PRIVATE)
    val jsonStr = prefs.getString("plants_list_json", null) ?: return emptyList()
    val list = mutableListOf<Plant>()
    try {
        val array = JSONArray(jsonStr)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(
                Plant(
                    id = obj.optString("id", System.currentTimeMillis().toString() + "_" + i),
                    name = obj.getString("name"),
                    location = obj.optString("location", "Indoor"),
                    intervalDays = obj.optInt("intervalDays", 7),
                    lastWatered = obj.optLong("lastWatered", System.currentTimeMillis()),
                    notes = obj.optString("notes", "")
                )
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return list
}

private fun savePlants(context: Context, list: List<Plant>) {
    val prefs = context.getSharedPreferences("plant_care_prefs", Context.MODE_PRIVATE)
    try {
        val array = JSONArray()
        for (plant in list) {
            val obj = JSONObject()
            obj.put("id", plant.id)
            obj.put("name", plant.name)
            obj.put("location", plant.location)
            obj.put("intervalDays", plant.intervalDays)
            obj.put("lastWatered", plant.lastWatered)
            obj.put("notes", plant.notes)
            array.put(obj)
        }
        prefs.edit { putString("plants_list_json", array.toString()) }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
