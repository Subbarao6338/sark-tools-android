package com.toolbox.core.persistence

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.toolbox.core.export.MeasurementLog
import com.toolbox.everyday.counter.CounterData
import com.toolbox.everyday.notepad.Note
import com.toolbox.everyday.notepad.NotesRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import androidx.datastore.preferences.core.edit

private val Context.counterBackupStore by preferencesDataStore(name = "counters")
private val COUNTERS_BACKUP_KEY = stringPreferencesKey("counters_json")

@Serializable
data class AppBackupData(
    val version: Int = 1,
    val exportedAt: Long = System.currentTimeMillis(),
    val themeMode: String,
    val natureTheme: String,
    val themeMatchedIcons: Boolean,
    val categorySortOrder: String,
    val toolSortOrder: String,
    val favoriteToolIds: List<String>,
    val pinnedToolIds: List<String>,
    val counters: List<BackupCounterItem> = emptyList(),
    val notes: List<BackupNoteItem> = emptyList(),
    val measurementLogCsv: String? = null,
)

@Serializable
data class BackupCounterItem(
    val name: String,
    val value: Int,
)

@Serializable
data class BackupNoteItem(
    val id: String,
    val body: String,
    val updatedAt: Long,
)

object BackupRestoreManager {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    suspend fun createBackupJson(context: Context): String {
        val repo = UserPreferencesRepository(context)

        val theme = repo.themeMode.first().name
        val nature = repo.natureTheme.first().name
        val matchedIcons = repo.themeMatchedIcons.first()
        val catSort = repo.categorySortOrder.first().name
        val toolSort = repo.toolSortOrder.first().name
        val favorites = repo.favoriteToolIds.first().toList()
        val pinned = repo.pinnedToolIds.first().toList()

        // Read counters
        val counters: List<BackupCounterItem> = try {
            val raw = context.counterBackupStore.data.first()[COUNTERS_BACKUP_KEY]
            if (!raw.isNullOrBlank()) {
                val list = Json.decodeFromString<List<CounterData>>(raw)
                list.map { BackupCounterItem(it.name, it.value) }
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }

        // Read notes
        val notes: List<BackupNoteItem> = try {
            val noteList = NotesRepository(context).notes.first()
            noteList.map { BackupNoteItem(it.id, it.body, it.updatedAt) }
        } catch (_: Exception) {
            emptyList()
        }

        // Read measurement log
        val logCsv = MeasurementLog.toCsv()

        val backup = AppBackupData(
            themeMode = theme,
            natureTheme = nature,
            themeMatchedIcons = matchedIcons,
            categorySortOrder = catSort,
            toolSortOrder = toolSort,
            favoriteToolIds = favorites,
            pinnedToolIds = pinned,
            counters = counters,
            notes = notes,
            measurementLogCsv = logCsv,
        )

        return json.encodeToString(backup)
    }

    suspend fun restoreFromJson(context: Context, jsonString: String): Result<Unit> {
        return runCatching {
            val data = json.decodeFromString<AppBackupData>(jsonString)
            val repo = UserPreferencesRepository(context)

            // Restore preferences
            ThemeMode.entries.find { it.name == data.themeMode }?.let {
                repo.setThemeMode(it)
            }
            NatureTheme.entries.find { it.name == data.natureTheme }?.let {
                repo.setNatureTheme(it)
            }
            repo.setThemeMatchedIcons(data.themeMatchedIcons)
            CategorySortOrder.entries.find { it.name == data.categorySortOrder }?.let {
                repo.setCategorySortOrder(it)
            }
            ToolSortOrder.entries.find { it.name == data.toolSortOrder }?.let {
                repo.setToolSortOrder(it)
            }

            // Restore favorites
            val currentFavs = repo.favoriteToolIds.first()
            currentFavs.forEach { id ->
                if (id !in data.favoriteToolIds) {
                    repo.toggleFavorite(id)
                }
            }
            data.favoriteToolIds.forEach { id ->
                if (id !in currentFavs) {
                    repo.toggleFavorite(id)
                }
            }

            // Restore pinned tools
            val currentPinned = repo.pinnedToolIds.first()
            currentPinned.forEach { id ->
                if (id !in data.pinnedToolIds) {
                    repo.togglePin(id)
                }
            }
            data.pinnedToolIds.forEach { id ->
                if (id !in currentPinned) {
                    repo.togglePin(id)
                }
            }

            // Restore counters
            if (data.counters.isNotEmpty()) {
                val counterList = data.counters.map { CounterData(it.name, it.value) }
                val encoded = Json.encodeToString(counterList)
                context.counterBackupStore.edit { prefs ->
                    prefs[COUNTERS_BACKUP_KEY] = encoded
                }
            }

            // Restore notes
            if (data.notes.isNotEmpty()) {
                val notesRepo = NotesRepository(context)
                data.notes.forEach { noteItem ->
                    notesRepo.upsert(Note(noteItem.id, noteItem.body, noteItem.updatedAt))
                }
            }
        }
    }
}
