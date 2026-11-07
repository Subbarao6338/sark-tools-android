package com.toolbox.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.toolbox.core.persistence.UserPreferencesRepository
import com.toolbox.core.sensor.SensorAvailability
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardUiState(
    val query: String = "",
    val selectedCategory: ToolCategory? = null,
    val tools: List<ToolDefinition> = allTools,
    val totalCount: Int = allTools.size,
    val disabledToolIds: Set<String> = emptySet(),
    val favoriteToolIds: Set<String> = emptySet(),
    val collapsedCategories: Set<ToolCategory> = emptySet(),
    val themeMatchedIcons: Boolean = true,
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val sensorAvailability = SensorAvailability(application)
    private val preferencesRepository = UserPreferencesRepository(application)

    private val _uiState = MutableStateFlow(
        DashboardUiState(
            disabledToolIds = computeDisabledTools(),
        )
    )
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepository.favoriteToolIds.collect { favorites ->
                _uiState.update { it.copy(favoriteToolIds = favorites) }
            }
        }
        viewModelScope.launch {
            preferencesRepository.themeMatchedIcons.collect { matched ->
                _uiState.update { it.copy(themeMatchedIcons = matched) }
            }
        }
    }

    fun onQueryChanged(query: String) {
        _uiState.update { state ->
            state.copy(
                query = query,
                tools = filterTools(query, state.selectedCategory),
                // When actively searching, expand categories so user sees matching tools immediately
                collapsedCategories = if (query.isNotBlank()) emptySet() else state.collapsedCategories,
            )
        }
    }

    fun toggleCategoryCollapsed(category: ToolCategory) {
        _uiState.update { state ->
            val updated = if (category in state.collapsedCategories) {
                state.collapsedCategories - category
            } else {
                state.collapsedCategories + category
            }
            state.copy(collapsedCategories = updated)
        }
    }

    fun expandAllCategories() {
        _uiState.update { it.copy(collapsedCategories = emptySet()) }
    }

    fun collapseAllCategories() {
        _uiState.update { it.copy(collapsedCategories = ToolCategory.entries.toSet()) }
    }

    fun onCategorySelected(category: ToolCategory?) {
        _uiState.update { state ->
            val newCategory = if (state.selectedCategory == category) null else category
            state.copy(
                selectedCategory = newCategory,
                tools = filterTools(state.query, newCategory),
            )
        }
    }

    fun toggleFavorite(toolId: String) {
        viewModelScope.launch {
            preferencesRepository.toggleFavorite(toolId)
        }
    }

    private fun filterTools(query: String, category: ToolCategory?): List<ToolDefinition> {
        var list = allTools
        if (category != null) {
            list = list.filter { it.category == category }
        }
        if (query.isBlank()) return list
        val lower = query.lowercase().trim()
        return list.filter { tool ->
            tool.name.lowercase().contains(lower) ||
                tool.category.label.lowercase().contains(lower) ||
                tool.searchKeywords.any { it.contains(lower) }
        }
    }

    private fun computeDisabledTools(): Set<String> {
        val context = getApplication<Application>()
        return allTools.filter { tool ->
            val sensorMissing = tool.requiredSensorType != null &&
                !sensorAvailability.isSensorAvailable(tool.requiredSensorType)
            val cameraMissing = tool.requiresCamera &&
                !sensorAvailability.hasCamera(context)
            sensorMissing || cameraMissing
        }.map { it.id }.toSet()
    }
}
