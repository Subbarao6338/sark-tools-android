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
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.toolbox.MainActivity

class BmiCalculatorWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(ColorProvider(Color(0xFFFFFFFF)))
                        .cornerRadius(16.dp)
                        .padding(12.dp)
                        .clickable(
                            actionStartActivity<MainActivity>(
                                actionParametersOf(ActionParameters.Key<String>("tool_id") to "bmi_calculator")
                            )
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "BMI Index",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = ColorProvider(Color(0xFF888888)),
                        ),
                        maxLines = 1,
                    )
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Text(
                        text = "22.5",
                        style = TextStyle(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorProvider(Color(0xFF2E7D32)),
                            textAlign = TextAlign.Center,
                        ),
                    )
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Text(
                        text = "Normal Weight",
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorProvider(Color(0xFF2E7D32)),
                        ),
                    )
                }
            }
        }
    }
}

class BmiCalculatorWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = BmiCalculatorWidget()
}
