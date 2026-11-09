package com.toolbox.tiles

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toolbox.MainActivity
import kotlinx.coroutines.launch

data class QuickTileItem(
    val id: String,
    val name: String,
    val toolId: String,
    val description: String,
    val serviceClass: Class<*>,
    val category: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickTilesManagementScreen(
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val tileList = remember {
        listOf(
            QuickTileItem("flashlight", "Flashlight", "flashlight", "Direct camera torch toggle", FlashlightTileService::class.java, "Utilities"),
            QuickTileItem("sound_meter", "Sound Meter", "sound_meter", "Launch acoustic decibel meter", SoundMeterTileService::class.java, "Sensors"),
            QuickTileItem("level", "Bubble Level", "level", "Launch 2D surface spirit level", LevelTileService::class.java, "Sensors"),
            QuickTileItem("timer", "Timer", "stopwatch_timer", "Launch countdown timer & stopwatch", TimerTileService::class.java, "DateTime"),
            QuickTileItem("compass", "Compass", "compass", "Launch magnetic compass", CompassTileService::class.java, "Sensors"),
            QuickTileItem("ruler", "Ruler", "ruler", "Launch screen ruler measurement", RulerTileService::class.java, "Sensors"),
            QuickTileItem("qr_scanner", "QR Scanner", "qr_scanner", "Launch camera barcode scanner", QrScannerTileService::class.java, "Everyday"),
            QuickTileItem("counter", "Counter", "counter", "Launch interactive tally counter", CounterTileService::class.java, "Everyday"),
            QuickTileItem("stopwatch", "Stopwatch", "stopwatch_timer", "Launch high-precision stopwatch", StopwatchTileService::class.java, "DateTime"),
            QuickTileItem("unit_converter", "Unit Converter", "unit_converter", "Launch multi-unit converter", UnitConverterTileService::class.java, "Converters"),
            QuickTileItem("calculator", "Calculator", "scientific_calculator", "Launch standard offline calculator", CalculatorTileService::class.java, "Calculators"),
            QuickTileItem("battery", "Battery Health", "battery_info", "Launch battery monitor", BatteryTileService::class.java, "Device"),
            QuickTileItem("speed_test", "Speed Test", "network_speed_test", "Launch network speed test", SpeedTestTileService::class.java, "Utilities"),
            QuickTileItem("mirror", "Mirror", "mirror", "Launch selfie mirror with light", MirrorTileService::class.java, "Everyday"),
            QuickTileItem("magnifier", "Magnifier", "magnifier", "Launch camera zoom magnifier", MagnifierTileService::class.java, "Everyday"),
            QuickTileItem("voice_recorder", "Voice Recorder", "voice_recorder", "Launch audio recorder", VoiceRecorderTileService::class.java, "Everyday"),
            QuickTileItem("notes", "Quick Notes", "notepad", "Launch notes scratchpad", NotesTileService::class.java, "Everyday"),
            QuickTileItem("pedometer", "Pedometer", "pedometer", "Launch step counter tracker", PedometerTileService::class.java, "Sensors"),
            QuickTileItem("bmi", "BMI Index", "bmr_calculator", "Launch BMI and calorie calculator", BmiTileService::class.java, "Calculators"),
            QuickTileItem("discount", "Discount Calculator", "discount_calculator", "Launch shopping discount calculator", DiscountTileService::class.java, "Calculators"),
            QuickTileItem("fuel", "Fuel Calculator", "fuel_calculator", "Launch trip gas & mileage calculator", FuelTileService::class.java, "Calculators"),
            QuickTileItem("lux", "Light Meter", "light_meter", "Launch ambient lux guide", LuxMeterTileService::class.java, "Sensors"),
            QuickTileItem("dimmer", "Screen Dimmer", "screen_dimmer", "Launch night blue light dimmer", ScreenDimmerTileService::class.java, "Utilities"),
            QuickTileItem("color_picker", "Color Picker", "color_picker", "Launch color picker & palette", ColorPickerTileService::class.java, "Everyday"),
            QuickTileItem("vibration", "Vibration Patterns", "vibration_patterns", "Launch vibration patterns", VibrationTileService::class.java, "Everyday"),
            QuickTileItem("white_noise", "White Noise", "white_noise", "Launch ambient sleep sounds", WhiteNoiseTileService::class.java, "Everyday"),
            QuickTileItem("protractor", "Protractor", "protractor", "Launch angle protractor tool", ProtractorTileService::class.java, "Sensors"),
            QuickTileItem("world_clock", "World Clock", "world_clock", "Launch international world clock", WorldClockTileService::class.java, "DateTime"),
        )
    }

    var customTileTitle by remember { mutableStateOf("") }
    var selectedTileTarget by remember { mutableStateOf(tileList[0]) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quick Tiles Management") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            // Header Info Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Widgets, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Quick Settings Tiles (${tileList.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Swipe down twice from the top of your screen, tap the pencil (Edit) icon, and drag Sark Tools tiles into your active Quick Settings shade.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f),
                        )
                    }
                }
            }

            // Quick Tile Setup & Creator Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    ),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "QUICK TILE CREATOR & SYSTEM REQUEST",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = customTileTitle,
                            onValueChange = { customTileTitle = it },
                            label = { Text("Tile Custom Note / Nickname") },
                            placeholder = { Text("e.g. Daily Speed Check") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        ExposedDropdownMenuBox(
                            expanded = dropdownExpanded,
                            onExpandedChange = { dropdownExpanded = !dropdownExpanded },
                        ) {
                            OutlinedTextField(
                                value = selectedTileTarget.name,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Select Tile to Add") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                            )
                            ExposedDropdownMenu(
                                expanded = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false },
                            ) {
                                tileList.forEach { tile ->
                                    DropdownMenuItem(
                                        text = { Text(tile.name) },
                                        onClick = {
                                            selectedTileTarget = tile
                                            dropdownExpanded = false
                                        },
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                requestAddTile(context, selectedTileTarget.serviceClass, selectedTileTarget.name) { success ->
                                    scope.launch {
                                        if (success) {
                                            snackbarHostState.showSnackbar("Tile add prompt sent to status bar")
                                        } else {
                                            snackbarHostState.showSnackbar("Swipe down notification shade, tap Edit (pencil) to add ${selectedTileTarget.name}")
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Request Add To Quick Settings")
                        }
                    }
                }
            }

            item {
                Text(
                    text = "PREDEFINED QUICK TILES (${tileList.size})",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            items(tileList, key = { it.id }) { tile ->
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
                                    text = tile.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = "Category: ${tile.category}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = tile.description,
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
                                    val intent = Intent(context, MainActivity::class.java).apply {
                                        action = Intent.ACTION_VIEW
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        putExtra("tool_id", tile.toolId)
                                    }
                                    context.startActivity(intent)
                                },
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Test Open")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    requestAddTile(context, tile.serviceClass, tile.name) { success ->
                                        scope.launch {
                                            if (success) {
                                                snackbarHostState.showSnackbar("Tile prompt sent for ${tile.name}")
                                            } else {
                                                snackbarHostState.showSnackbar("Swipe down shade -> Edit to place ${tile.name}")
                                            }
                                        }
                                    }
                                },
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add Tile")
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun requestAddTile(
    context: Context,
    serviceClass: Class<*>,
    label: String,
    onResult: (Boolean) -> Unit,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val statusBarManager = context.getSystemService(android.app.StatusBarManager::class.java)
        if (statusBarManager != null) {
            val componentName = ComponentName(context, serviceClass)
            val icon = android.graphics.drawable.Icon.createWithResource(context, com.toolbox.R.drawable.ic_flashlight_tile)
            statusBarManager.requestAddTileService(
                componentName,
                label,
                icon,
                context.mainExecutor,
            ) { resultCode ->
                onResult(resultCode == android.app.StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ADDED)
            }
            return
        }
    }
    onResult(false)
}
