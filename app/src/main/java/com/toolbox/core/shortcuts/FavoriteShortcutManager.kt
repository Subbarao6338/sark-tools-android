package com.toolbox.core.shortcuts

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.toolbox.MainActivity
import com.toolbox.dashboard.ToolDefinition
import com.toolbox.dashboard.allTools

data class PredefinedShortcut(
    val id: String,
    val toolId: String,
    val title: String,
    val subtitle: String,
    val category: String,
)

object FavoriteShortcutManager {

    val predefinedShortcuts: List<PredefinedShortcut> = listOf(
        PredefinedShortcut("sc_flashlight", "flashlight", "Flashlight", "Instant torch toggle", "Utilities"),
        PredefinedShortcut("sc_level", "level", "Bubble Level", "Surface alignment", "Sensors"),
        PredefinedShortcut("sc_soundmeter", "sound_meter", "Sound Meter", "Measure decibel noise", "Sensors"),
        PredefinedShortcut("sc_compass", "compass", "Compass", "True & magnetic north", "Sensors"),
        PredefinedShortcut("sc_ruler", "ruler", "Ruler", "Screen measurement", "Sensors"),
        PredefinedShortcut("sc_qr_scanner", "qr_scanner", "QR Scanner", "Scan codes & barcodes", "Everyday"),
        PredefinedShortcut("sc_stopwatch", "stopwatch_timer", "Stopwatch & Timer", "Precision timing", "DateTime"),
        PredefinedShortcut("sc_counter", "counter", "Tally Counter", "Count anything easily", "Everyday"),
        PredefinedShortcut("sc_unit_converter", "unit_converter", "Unit Converter", "Universal conversion", "Unit Converters"),
        PredefinedShortcut("sc_calc", "scientific_calculator", "Calculator", "Quick calculations", "Calculators"),
        PredefinedShortcut("sc_battery", "battery_info", "Battery Health", "Monitor battery & temp", "Device"),
        PredefinedShortcut("sc_speedtest", "network_speed_test", "Speed Test", "Check ping & jitter", "Utilities"),
        PredefinedShortcut("sc_mirror", "mirror", "Mirror", "Front camera pocket mirror", "Everyday"),
        PredefinedShortcut("sc_magnifier", "magnifier", "Magnifier", "Zoom inspection glass", "Everyday"),
        PredefinedShortcut("sc_voicerecorder", "voice_recorder", "Voice Recorder", "Offline audio recorder", "Everyday"),
        PredefinedShortcut("sc_notepad", "notepad", "Quick Notes", "Scratchpad memo notes", "Everyday"),
        PredefinedShortcut("sc_pedometer", "pedometer", "Pedometer", "Step tracker & fitness", "Sensors"),
        PredefinedShortcut("sc_worldclock", "world_clock", "World Clock", "Global time zones", "DateTime"),
        PredefinedShortcut("sc_discount", "discount_calculator", "Discount Calc", "Sale & tax calculations", "Calculators"),
        PredefinedShortcut("sc_fuel", "fuel_calculator", "Fuel Economy", "Trip & mileage cost", "Calculators"),
        PredefinedShortcut("sc_bmi", "bmr_calculator", "BMI & Health", "Calorie & body metrics", "Calculators"),
        PredefinedShortcut("sc_colorpicker", "color_picker", "Color Picker", "Palette & HEX codes", "Everyday"),
        PredefinedShortcut("sc_vibration", "vibration_patterns", "Vibration", "Custom haptic pulses", "Everyday"),
        PredefinedShortcut("sc_screendimmer", "screen_dimmer", "Screen Dimmer", "Night eye protection", "Utilities"),
        PredefinedShortcut("sc_whitenoise", "white_noise", "White Noise", "Relaxing ambient sounds", "Everyday"),
    )

    fun updateShortcuts(context: Context, favoriteIds: Set<String>) {
        updateDynamicShortcuts(context, favoriteIds)
    }

    fun updateDynamicShortcuts(context: Context, favoriteIds: Set<String>) {
        val shortcutManager = context.getSystemService(ShortcutManager::class.java) ?: return
        val maxShortcuts = shortcutManager.maxShortcutCountPerActivity.coerceAtMost(4)

        val shortcuts = allTools
            .filter { it.id in favoriteIds }
            .take(maxShortcuts)
            .mapIndexed { index, tool ->
                val intent = Intent(context, MainActivity::class.java).apply {
                    action = Intent.ACTION_VIEW
                    putExtra("tool_id", tool.id)
                }
                ShortcutInfo.Builder(context, "fav_${tool.id}")
                    .setShortLabel(tool.name)
                    .setLongLabel(tool.name)
                    .setIcon(android.graphics.drawable.Icon.createWithResource(context, iconResForTool(tool.id)))
                    .setIntent(intent)
                    .setRank(index)
                    .build()
            }

        try {
            shortcutManager.dynamicShortcuts = shortcuts
        } catch (_: Exception) {}
    }

    fun pinShortcut(context: Context, toolId: String, customTitle: String? = null, customBgColor: Int? = null): Boolean {
        if (!ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {
            return false
        }
        val tool = allTools.firstOrNull { it.id == toolId }
        val title = customTitle?.takeIf { it.isNotBlank() } ?: tool?.name ?: "Tool"

        val intent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra("tool_id", toolId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val bitmap = generateShortcutIconBitmap(title, customBgColor ?: 0xFF325DA8.toInt())
        val iconCompat = IconCompat.createWithBitmap(bitmap)

        val pinShortcutInfo = ShortcutInfoCompat.Builder(context, "pinned_${toolId}_${System.currentTimeMillis()}")
            .setShortLabel(title)
            .setLongLabel(tool?.description?.take(25) ?: title)
            .setIcon(iconCompat)
            .setIntent(intent)
            .build()

        return ShortcutManagerCompat.requestPinShortcut(context, pinShortcutInfo, null)
    }

    private fun generateShortcutIconBitmap(label: String, bgColor: Int): Bitmap {
        val size = 108
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = bgColor
        }
        val rect = RectF(6f, 6f, (size - 6).toFloat(), (size - 6).toFloat())
        canvas.drawRoundRect(rect, 24f, 24f, bgPaint)

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            textSize = 34f
            textAlign = Paint.Align.CENTER
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        val letter = label.take(2).uppercase()
        val yPos = (canvas.height / 2 - (textPaint.descent() + textPaint.ascent()) / 2)
        canvas.drawText(letter, canvas.width / 2f, yPos, textPaint)

        return bitmap
    }

    private fun iconResForTool(toolId: String): Int = when (toolId) {
        "flashlight" -> com.toolbox.R.drawable.ic_flashlight_tile
        "sound_meter" -> com.toolbox.R.drawable.ic_soundmeter_tile
        "level" -> com.toolbox.R.drawable.ic_level_tile
        "stopwatch_timer" -> com.toolbox.R.drawable.ic_timer_tile
        "compass" -> com.toolbox.R.drawable.ic_qs_compass
        "ruler" -> com.toolbox.R.drawable.ic_qs_ruler
        "qr_scanner" -> com.toolbox.R.drawable.ic_qs_qr
        "notepad" -> com.toolbox.R.drawable.ic_qs_notes
        "voice_recorder" -> com.toolbox.R.drawable.ic_qs_mic
        "battery_info" -> com.toolbox.R.drawable.ic_qs_battery
        else -> com.toolbox.R.drawable.ic_qs_calc
    }
}
