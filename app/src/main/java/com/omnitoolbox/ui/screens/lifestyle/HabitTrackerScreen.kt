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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

data class Habit(
    val id: String,
    val name: String,
    val category: String, // Fitness, Mind, Work, Health, Other
    val isCompleted: Boolean,
    val streak: Int,
    val lastCompleted: Long,
    val notes: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitTrackerScreen(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("habit_tracker_prefs", Context.MODE_PRIVATE) }

    // State list for habits loaded persistently
    var habitsList by remember { mutableStateOf(loadHabits(context)) }

    // Prepopulate on first run
    LaunchedEffect(Unit) {
        if (!sharedPrefs.contains("habits_list_json")) {
            val initial = listOf(
                Habit("h1", "Morning Yoga", "Fitness", false, 3, System.currentTimeMillis() - 86400000L, "15 minutes of dynamic stretching"),
                Habit("h2", "Read a Book", "Mind", false, 5, System.currentTimeMillis() - 86400000L, "At least 10 pages daily"),
                Habit("h3", "Drink 8 Glass of Water", "Health", false, 0, 0L, "Stay hydrated throughout the day")
            )
            saveHabits(context, initial)
            habitsList = initial
        }
    }

    // Filter and search query states
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }

    // Dialog state management
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Habit?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf<Habit?>(null) }

    // Available categories
    val categories = listOf("All", "Fitness", "Mind", "Work", "Health", "Other")

    // Filter habits list
    val filteredHabits = remember(habitsList, searchQuery, selectedCategoryFilter) {
        habitsList.filter { habit ->
            val matchesSearch = habit.name.contains(searchQuery, ignoreCase = true) ||
                    habit.notes.contains(searchQuery, ignoreCase = true)
            val matchesFilter = selectedCategoryFilter == "All" || habit.category == selectedCategoryFilter
            matchesSearch && matchesFilter
        }
    }

    // Status Summary calculations
    val totalHabits = habitsList.size
    val completedCount = habitsList.count { habit ->
        // Auto-reset completed state if it's a new day
        habit.isCompleted && isCompletedToday(habit.lastCompleted)
    }
    val maxStreak = if (habitsList.isNotEmpty()) habitsList.maxOf { it.streak } else 0

    ToolScreen(
        title = "Habit Tracker",
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
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                "Daily Habit Progress",
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
                            SummaryStatItem("Tracked", totalHabits.toString(), MaterialTheme.colorScheme.primary)
                            SummaryStatItem("Done Today", "$completedCount/$totalHabits", if (completedCount == totalHabits && totalHabits > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)
                            SummaryStatItem("Best Streak", "$maxStreak 🔥", MaterialTheme.colorScheme.error)
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
                        placeholder = { Text("Search habits...") },
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
                        categories.forEach { cat ->
                            val isSelected = selectedCategoryFilter == cat
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategoryFilter = cat },
                                label = { Text(cat, fontSize = 12.sp) },
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

                // Scrollable Habit List
                if (filteredHabits.isEmpty()) {
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
                                Icons.Default.DateRange,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                modifier = Modifier.size(72.dp)
                            )
                            Text(
                                if (searchQuery.isNotEmpty() || selectedCategoryFilter != "All") "No matching habits found" else "No habits configured",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                if (searchQuery.isNotEmpty() || selectedCategoryFilter != "All") "Try modifying your search or filters" else "Tap the '+' floating button to create your first habit tracker!",
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
                        items(filteredHabits, key = { it.id }) { habit ->
                            HabitCard(
                                habit = habit,
                                onToggleComplete = {
                                    val isCurrentlyDone = habit.isCompleted && isCompletedToday(habit.lastCompleted)
                                    val nextDone = !isCurrentlyDone
                                    val nextLastCompleted = if (nextDone) System.currentTimeMillis() else habit.lastCompleted
                                    val nextStreak = if (nextDone) {
                                        calculateNextStreak(habit.lastCompleted, habit.streak)
                                    } else {
                                        habit.streak // keep streak as is, or adjust as preferred
                                    }

                                    val updated = habitsList.map { h ->
                                        if (h.id == habit.id) {
                                            h.copy(
                                                isCompleted = nextDone,
                                                lastCompleted = nextLastCompleted,
                                                streak = nextStreak
                                            )
                                        } else h
                                    }
                                    habitsList = updated
                                    saveHabits(context, updated)

                                    val completionMsg = if (nextDone) {
                                        "${habit.name} marked complete! Streak: $nextStreak 🔥"
                                    } else {
                                        "${habit.name} unmarked."
                                    }
                                    Toast.makeText(context, completionMsg, Toast.LENGTH_SHORT).show()
                                },
                                onEdit = { showEditDialog = habit },
                                onDelete = { showDeleteConfirmation = habit }
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
                Icon(Icons.Default.Add, contentDescription = "Add Habit")
            }
        }
    }

    // Dialogs
    if (showAddDialog) {
        HabitFormDialog(
            title = "Add New Habit",
            onDismiss = { showAddDialog = false },
            onSave = { name, cat, notes ->
                val newHabit = Habit(
                    id = "habit_" + System.currentTimeMillis(),
                    name = name,
                    category = cat,
                    isCompleted = false,
                    streak = 0,
                    lastCompleted = 0L,
                    notes = notes
                )
                val updated = habitsList + newHabit
                habitsList = updated
                saveHabits(context, updated)
                showAddDialog = false
                Toast.makeText(context, "$name successfully created!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    showEditDialog?.let { habitToEdit ->
        HabitFormDialog(
            title = "Edit Habit Details",
            initialHabit = habitToEdit,
            onDismiss = { showEditDialog = null },
            onSave = { name, cat, notes ->
                val updated = habitsList.map { h ->
                    if (h.id == habitToEdit.id) {
                        h.copy(name = name, category = cat, notes = notes)
                    } else h
                }
                habitsList = updated
                saveHabits(context, updated)
                showEditDialog = null
                Toast.makeText(context, "Habit details updated!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    showDeleteConfirmation?.let { habitToDelete ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = null },
            title = { Text("Delete Habit?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you absolutely sure you want to delete ${habitToDelete.name}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val updated = habitsList.filter { it.id != habitToDelete.id }
                        habitsList = updated
                        saveHabits(context, updated)
                        showDeleteConfirmation = null
                        Toast.makeText(context, "${habitToDelete.name} deleted.", Toast.LENGTH_SHORT).show()
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
fun HabitCard(
    habit: Habit,
    onToggleComplete: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isDone = habit.isCompleted && isCompletedToday(habit.lastCompleted)

    val cardBorderModifier = if (isDone) {
        Modifier.border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
    } else {
        Modifier.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(cardBorderModifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDone) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.08f)
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Checkbox
                    Checkbox(
                        checked = isDone,
                        onCheckedChange = { onToggleComplete() }
                    )

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                habit.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isDone) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    habit.category,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }

                // Actions & Streak
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (habit.streak > 0) {
                        Text(
                            "${habit.streak} 🔥",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp
                        )
                    }

                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Habit",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Habit",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Note section if present
            if (habit.notes.isNotBlank()) {
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
                        habit.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            // Last Completed Details
            if (habit.lastCompleted > 0L) {
                Spacer(modifier = Modifier.height(8.dp))
                val format = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault())
                val lastCompletedStr = format.format(Date(habit.lastCompleted))
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
                        "Last Done: $lastCompletedStr",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@Composable
fun HabitFormDialog(
    title: String,
    initialHabit: Habit? = null,
    onDismiss: () -> Unit,
    onSave: (name: String, category: String, notes: String) -> Unit
) {
    var name by remember { mutableStateOf(initialHabit?.name ?: "") }
    var category by remember { mutableStateOf(initialHabit?.category ?: "Fitness") }
    var notes by remember { mutableStateOf(initialHabit?.notes ?: "") }

    val categories = listOf("Fitness", "Mind", "Work", "Health", "Other")

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
                    label = { Text("Habit Name") },
                    placeholder = { Text("e.g. Meditate") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                // Category selection
                Column {
                    Text("Category", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSelected = category == cat
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .clickable { category = cat }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    cat,
                                    fontSize = 11.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Reminders / Notes") },
                    placeholder = { Text("e.g. In the living room before coffee") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.trim().isNotEmpty()) {
                        onSave(name.trim(), category, notes.trim())
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

// Helper utilities for habits
fun calculateNextStreak(lastCompleted: Long, currentStreak: Int): Int {
    if (lastCompleted == 0L) return 1

    val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    val lastDayStr = sdf.format(Date(lastCompleted))
    val todayStr = sdf.format(Date())

    if (lastDayStr == todayStr) {
        return currentStreak
    }

    // Check if lastCompleted was yesterday
    val calLast = Calendar.getInstance().apply { timeInMillis = lastCompleted }
    val calToday = Calendar.getInstance()

    // Subtract 1 day from today
    calToday.add(Calendar.DAY_OF_YEAR, -1)
    val yesterdayStr = sdf.format(calToday.time)

    return if (lastDayStr == yesterdayStr) {
        currentStreak + 1
    } else {
        1
    }
}

fun isCompletedToday(lastCompleted: Long): Boolean {
    if (lastCompleted == 0L) return false
    val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    return sdf.format(Date(lastCompleted)) == sdf.format(Date())
}

private fun loadHabits(context: Context): List<Habit> {
    val prefs = context.getSharedPreferences("habit_tracker_prefs", Context.MODE_PRIVATE)
    val jsonStr = prefs.getString("habits_list_json", null) ?: return emptyList()
    val list = mutableListOf<Habit>()
    try {
        val array = JSONArray(jsonStr)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val lastCompleted = obj.optLong("lastCompleted", 0L)
            var streak = obj.optInt("streak", 0)
            val isCompleted = obj.optBoolean("isCompleted", false)

            // Dynamic check for broken streak (missed more than 48 hours since last completed)
            if (lastCompleted > 0L && !isCompletedToday(lastCompleted)) {
                val diffMillis = System.currentTimeMillis() - lastCompleted
                if (diffMillis > 48 * 60 * 60 * 1000L) {
                    streak = 0
                }
            }

            list.add(
                Habit(
                    id = obj.optString("id", System.currentTimeMillis().toString() + "_" + i),
                    name = obj.getString("name"),
                    category = obj.optString("category", "Other"),
                    isCompleted = isCompleted,
                    streak = streak,
                    lastCompleted = lastCompleted,
                    notes = obj.optString("notes", "")
                )
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return list
}

private fun saveHabits(context: Context, list: List<Habit>) {
    val prefs = context.getSharedPreferences("habit_tracker_prefs", Context.MODE_PRIVATE)
    try {
        val array = JSONArray()
        for (habit in list) {
            val obj = JSONObject()
            obj.put("id", habit.id)
            obj.put("name", habit.name)
            obj.put("category", habit.category)
            obj.put("isCompleted", habit.isCompleted)
            obj.put("streak", habit.streak)
            obj.put("lastCompleted", habit.lastCompleted)
            obj.put("notes", habit.notes)
            array.put(obj)
        }
        prefs.edit { putString("habits_list_json", array.toString()) }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
