package com.toolbox.widgets

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.toolbox.MainActivity
import com.toolbox.everyday.notepad.Note
import com.toolbox.everyday.notepad.NotesRepository
import kotlinx.coroutines.flow.first

class NotesWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val notes: List<Note> = try {
            NotesRepository(context).notes.first()
        } catch (_: Exception) {
            emptyList()
        }
        val latestNote = notes.firstOrNull()

        provideContent {
            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(ColorProvider(Color(0xFFFFFDE7)))
                        .cornerRadius(16.dp)
                        .padding(12.dp)
                        .clickable(
                            actionStartActivity<MainActivity>(
                                actionParametersOf(ActionParameters.Key<String>("tool_id") to "notepad")
                            )
                        ),
                    verticalAlignment = Alignment.Top,
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = "Quick Notes 📝",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorProvider(Color(0xFFF57F17)),
                        ),
                    )
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Text(
                        text = latestNote?.title?.ifEmpty { "Note" } ?: "Tap to create note",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorProvider(Color(0xFF212121)),
                        ),
                        maxLines = 1,
                    )
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    Text(
                        text = latestNote?.body ?: "Your scratchpad is ready for thoughts, tasks, and ideas.",
                        style = TextStyle(
                            fontSize = 11.sp,
                            color = ColorProvider(Color(0xFF616161)),
                        ),
                        maxLines = 3,
                    )
                }
            }
        }
    }
}

class NotesWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = NotesWidget()
}
