package com.toolbox.widgets

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

data class WidgetInfoItem(
    val id: String,
    val name: String,
    val size: String,
    val description: String,
    val receiverClass: Class<*>,
    val category: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetsManagementScreen(
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val widgetList = remember {
        listOf(
            WidgetInfoItem("counter", "Counter Widget", "2x2", "Quick tap counter with plus and minus buttons directly on home screen", CounterWidgetReceiver::class.java, "Everyday"),
            WidgetInfoItem("flashlight", "Flashlight Toggle", "1x1", "One-touch toggle to switch camera torch light on or off", FlashlightWidgetReceiver::class.java, "Utilities"),
            WidgetInfoItem("quick_launch", "Quick Launch Grid", "4x2", "Launch favorite tools immediately without opening the main app", QuickLaunchWidgetReceiver::class.java, "Utilities"),
            WidgetInfoItem("stopwatch", "Stopwatch & Timer", "2x2", "Lap and elapsed stopwatch control from home screen", StopwatchWidgetReceiver::class.java, "DateTime"),
            WidgetInfoItem("unit_converter", "Unit Converter", "3x2", "Quick length, mass, and temperature unit conversion tile", UnitConverterWidgetReceiver::class.java, "Converters"),
            WidgetInfoItem("date_countdown", "Date Countdown", "2x2", "Countdown remaining days and hours to important events", DateCountdownWidgetReceiver::class.java, "DateTime"),
            WidgetInfoItem("pedometer", "Pedometer Tracker", "2x2", "Real-time daily walking steps and activity progress", PedometerWidgetReceiver::class.java, "Sensors"),
            WidgetInfoItem("favorites_slider", "Favorite Tools Slider", "4x2", "Interactive carousel of all your marked favorite tools", FavoritesSliderWidgetReceiver::class.java, "Utilities"),
            WidgetInfoItem("battery", "Battery Health Widget", "2x2", "Monitor battery level, temperature, and charging status", BatteryWidgetReceiver::class.java, "Device"),
            WidgetInfoItem("bmi", "BMI Index Widget", "2x2", "Keep track of your Body Mass Index and health category", BmiCalculatorWidgetReceiver::class.java, "Calculators"),
            WidgetInfoItem("notes", "Quick Notes Scratchpad", "3x2", "View your latest thoughts and notes right on your home screen", NotesWidgetReceiver::class.java, "Everyday"),
            WidgetInfoItem("world_clock", "World Clock Widget", "3x2", "Dual local time and UTC global time synchronization", WorldClockWidgetReceiver::class.java, "DateTime"),
        )
    }

    var customWidgetTitle by remember { mutableStateOf("") }
    var selectedWidgetType by remember { mutableStateOf("counter") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Widgets Management") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Header Creator Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Widgets, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Widget Creator & Manager",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Add and pin precision widgets to your Android Home Screen. ${widgetList.size} predefined widgets available.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                        )
                    }
                }
            }

            // Custom Widget Creation Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    ),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    ) {
                        Text(
                            text = "CREATE CUSTOM WIDGET",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = customWidgetTitle,
                            onValueChange = { customWidgetTitle = it },
                            label = { Text("Custom Widget Label") },
                            placeholder = { Text("e.g. My Workout Counter") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                val target = widgetList.firstOrNull { it.id == selectedWidgetType } ?: widgetList[0]
                                pinWidgetToHomeScreen(context, target.receiverClass) { success ->
                                    scope.launch {
                                        if (success) {
                                            snackbarHostState.showSnackbar("Pin request submitted to launcher")
                                        } else {
                                            snackbarHostState.showSnackbar("Please add widget from your launcher's widget picker")
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pin Widget to Home Screen")
                        }
                    }
                }
            }

            item {
                Text(
                    text = "AVAILABLE WIDGETS (${widgetList.size})",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            items(widgetList, key = { it.id }) { widget ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    ),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = widget.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = "Category: ${widget.category}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.secondaryContainer,
                                        RoundedCornerShape(8.dp),
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                            ) {
                                Text(
                                    text = widget.size,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = widget.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            OutlinedButton(
                                onClick = {
                                    pinWidgetToHomeScreen(context, widget.receiverClass) { success ->
                                        scope.launch {
                                            if (success) {
                                                snackbarHostState.showSnackbar("Pin request submitted for ${widget.name}")
                                            } else {
                                                snackbarHostState.showSnackbar("Long press home screen to place ${widget.name}")
                                            }
                                        }
                                    }
                                },
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add to Home")
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun pinWidgetToHomeScreen(
    context: Context,
    receiverClass: Class<*>,
    onResult: (Boolean) -> Unit,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val appWidgetManager = context.getSystemService(AppWidgetManager::class.java)
        val myProvider = ComponentName(context, receiverClass)
        if (appWidgetManager != null && appWidgetManager.isRequestPinAppWidgetSupported) {
            val success = appWidgetManager.requestPinAppWidget(myProvider, null, null)
            onResult(success)
            return
        }
    }
    onResult(false)
}
