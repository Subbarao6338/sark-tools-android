package com.toolbox.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.Color as AndroidColor
import android.widget.RemoteViews
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toolbox.MainActivity
import com.toolbox.R

object DigitalClockPrefs {
    private const val PREFS_NAME = "digital_clock_widget_prefs"
    const val KEY_COLOR = "clock_color"
    const val KEY_FONT = "clock_font"
    const val KEY_BG = "clock_bg"
    const val KEY_OPACITY = "clock_opacity"
    const val KEY_24HR = "clock_24hr"

    fun save(context: Context, color: String, font: String, bg: String, opacity: Float, is24Hr: Boolean) {
        val sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sp.edit()
            .putString(KEY_COLOR, color)
            .putString(KEY_FONT, font)
            .putString(KEY_BG, bg)
            .putFloat(KEY_OPACITY, opacity)
            .putBoolean(KEY_24HR, is24Hr)
            .apply()

        val mgr = AppWidgetManager.getInstance(context)
        val ids = mgr.getAppWidgetIds(ComponentName(context, DigitalClockWidgetProvider::class.java))
        if (ids.isNotEmpty()) {
            val intent = Intent(context, DigitalClockWidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            }
            context.sendBroadcast(intent)
        }
    }

    fun getColor(context: Context): String = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_COLOR, "Cyan") ?: "Cyan"
    fun getFont(context: Context): String = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_FONT, "Monospace") ?: "Monospace"
    fun getBg(context: Context): String = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_BG, "Dark Slate") ?: "Dark Slate"
    fun getOpacity(context: Context): Float = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getFloat(KEY_OPACITY, 0.9f)
    fun is24Hr(context: Context): Boolean = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(KEY_24HR, false)
}

class DigitalClockWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) {
            updateWidget(context, appWidgetManager, id)
        }
    }

    companion object {
        fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_digital_clock)

            val colorName = DigitalClockPrefs.getColor(context)
            val bgName = DigitalClockPrefs.getBg(context)
            val is24Hr = DigitalClockPrefs.is24Hr(context)
            val opacity = DigitalClockPrefs.getOpacity(context)

            val dateColor = when (colorName) {
                "Emerald" -> AndroidColor.parseColor("#81C784")
                "Amber" -> AndroidColor.parseColor("#FFD54F")
                "Rose" -> AndroidColor.parseColor("#F48FB1")
                else -> AndroidColor.parseColor("#80DEEA")
            }

            val alpha = (opacity * 255).toInt().coerceIn(0, 255)
            val bgColor = when (bgName) {
                "OLED Black" -> AndroidColor.argb(alpha, 0, 0, 0)
                "Translucent" -> AndroidColor.argb(alpha, 33, 33, 33)
                else -> AndroidColor.argb(alpha, 30, 30, 46)
            }

            views.setInt(R.id.clock_root, "setBackgroundColor", bgColor)
            views.setTextColor(R.id.clock_date, dateColor)
            views.setTextColor(R.id.clock_time, AndroidColor.WHITE)

            if (is24Hr) {
                views.setString(R.id.clock_time, "setFormat12Hour", "HH:mm:ss")
                views.setString(R.id.clock_time, "setFormat24Hour", "HH:mm:ss")
            } else {
                views.setString(R.id.clock_time, "setFormat12Hour", "hh:mm:ss a")
                views.setString(R.id.clock_time, "setFormat24Hour", "hh:mm:ss a")
            }

            val intent = Intent(context, MainActivity::class.java).apply {
                putExtra("tool_id", "digital_clock_widget")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.clock_root, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

@Composable
fun DigitalClockConfigScreen() {
    val context = LocalContext.current
    var selectedColor by remember { mutableStateOf(DigitalClockPrefs.getColor(context)) }
    var selectedFont by remember { mutableStateOf(DigitalClockPrefs.getFont(context)) }
    var selectedBg by remember { mutableStateOf(DigitalClockPrefs.getBg(context)) }
    var is24Hr by remember { mutableStateOf(DigitalClockPrefs.is24Hr(context)) }
    var transparency by remember { mutableFloatStateOf(DigitalClockPrefs.getOpacity(context)) }
    var saved by remember { mutableStateOf(false) }

    val colorMap = mapOf(
        "Cyan" to Color(0xFF80DEEA),
        "Emerald" to Color(0xFF81C784),
        "Amber" to Color(0xFFFFD54F),
        "Rose" to Color(0xFFF48FB1)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Digital Clock Live Preview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                val font = when (selectedFont) {
                    "Monospace" -> FontFamily.Monospace
                    "Serif" -> FontFamily.Serif
                    else -> FontFamily.Default
                }
                val bgColor = when (selectedBg) {
                    "OLED Black" -> Color.Black.copy(alpha = transparency)
                    "Translucent" -> Color(0xFF212121).copy(alpha = transparency)
                    else -> Color(0xFF1E1E2E).copy(alpha = transparency)
                }
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = bgColor),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Mon, Nov 9, 2026",
                            color = colorMap[selectedColor] ?: Color(0xFF80DEEA),
                            fontFamily = font,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (is24Hr) "14:32:45" else "02:32:45 PM",
                            color = Color.White,
                            fontFamily = font,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        )
                    }
                }
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Clock Customization Options", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                Text("Date & Accent Color:", fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Cyan", "Emerald", "Amber", "Rose").forEach { col ->
                        FilterChip(selected = selectedColor == col, onClick = { selectedColor = col }, label = { Text(col) })
                    }
                }

                Text("Time Format:", fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = !is24Hr, onClick = { is24Hr = false }, label = { Text("12-Hour (with seconds)") })
                    FilterChip(selected = is24Hr, onClick = { is24Hr = true }, label = { Text("24-Hour (with seconds)") })
                }

                Text("Font Family:", fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Monospace", "Sans-Serif", "Serif").forEach { f ->
                        FilterChip(selected = selectedFont == f, onClick = { selectedFont = f }, label = { Text(f) })
                    }
                }

                Text("Background Style:", fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Dark Slate", "OLED Black", "Translucent").forEach { bg ->
                        FilterChip(selected = selectedBg == bg, onClick = { selectedBg = bg }, label = { Text(bg) })
                    }
                }

                Text("Background Opacity: ${(transparency * 100).toInt()}%", fontWeight = FontWeight.SemiBold)
                Slider(
                    value = transparency,
                    onValueChange = { transparency = it },
                    valueRange = 0.2f..1.0f
                )

                Button(
                    onClick = {
                        DigitalClockPrefs.save(context, selectedColor, selectedFont, selectedBg, transparency, is24Hr)
                        saved = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Apply & Save Preferences to Widget")
                }
                if (saved) {
                    Text("Preferences saved and home screen widget updated!", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
