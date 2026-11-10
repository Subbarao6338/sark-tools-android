package com.toolbox.tiles

import android.content.Intent
import android.service.quicksettings.TileService
import com.toolbox.MainActivity

/**
 * Base tile service that deep-links to a specific tool screen.
 * Subclasses just specify the tool ID.
 */
abstract class ToolLaunchTileService(private val toolId: String) : TileService() {

    override fun onClick() {
        super.onClick()
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("tool_id", toolId)
        }
        @Suppress("DEPRECATION")
        startActivityAndCollapse(intent)
    }
}

// 25+ Predefined Quick Settings Tile Services
class SoundMeterTileService : ToolLaunchTileService("sound_meter")
class LevelTileService : ToolLaunchTileService("level")
class TimerTileService : ToolLaunchTileService("stopwatch_timer")
class CompassTileService : ToolLaunchTileService("compass")
class RulerTileService : ToolLaunchTileService("ruler")
class QrScannerTileService : ToolLaunchTileService("qr_scanner")
class CounterTileService : ToolLaunchTileService("counter")
class StopwatchTileService : ToolLaunchTileService("stopwatch_timer")
class UnitConverterTileService : ToolLaunchTileService("unit_converter")
class CalculatorTileService : ToolLaunchTileService("scientific_calculator")
class BatteryTileService : ToolLaunchTileService("battery_info")
class SpeedTestTileService : ToolLaunchTileService("network_speed_test")
class MirrorTileService : ToolLaunchTileService("mirror")
class MagnifierTileService : ToolLaunchTileService("magnifier")
class VoiceRecorderTileService : ToolLaunchTileService("voice_recorder")
class NotesTileService : ToolLaunchTileService("notepad")
class PedometerTileService : ToolLaunchTileService("pedometer")
class BmiTileService : ToolLaunchTileService("bmr_calculator")
class DiscountTileService : ToolLaunchTileService("discount_calculator")
class FuelTileService : ToolLaunchTileService("fuel_calculator")
class LuxMeterTileService : ToolLaunchTileService("light_meter")
class ScreenDimmerTileService : ToolLaunchTileService("screen_dimmer")
class ColorPickerTileService : ToolLaunchTileService("color_picker")
class VibrationTileService : ToolLaunchTileService("vibration_patterns")
class WhiteNoiseTileService : ToolLaunchTileService("white_noise")
class ProtractorTileService : ToolLaunchTileService("protractor")
class WorldClockTileService : ToolLaunchTileService("world_clock")

class TipTileService : ToolLaunchTileService("tip_calculator")
class DiceTileService : ToolLaunchTileService("random_generator")
class TextScannerTileService : ToolLaunchTileService("ocr")
class NetworkInfoTileService : ToolLaunchTileService("network_info")
class HabitTileService : ToolLaunchTileService("habit_tracker")
