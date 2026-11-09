package com.toolbox.device

import androidx.compose.runtime.Composable
import com.toolbox.measurement.bluetoothfinder.BluetoothFinderScreen
import com.toolbox.measurement.deviceinfo.DeviceInfoScreen
import com.toolbox.everyday.nfc.NfcToolkitScreen
import com.toolbox.lighting.screengrid.ScreenGridScreen
import com.toolbox.everyday.vibration.VibrationPatternsScreen
import com.toolbox.everyday.storageanalyzer.StorageAnalyzerScreen
import com.toolbox.labs.deadpixel.DeadPixelTesterScreen
import com.toolbox.labs.touch.TouchTesterScreen
import com.toolbox.labs.dimmer.ScreenDimmerScreen
import com.toolbox.telephony.call.CallLogsAnalyticsScreen
import com.toolbox.measurement.battery.BatteryInfoScreen

object DeviceToolsRegistry {
    @Composable fun BluetoothFinder() = BluetoothFinderScreen()
    @Composable fun DeviceInfo() = DeviceInfoScreen()
    @Composable fun NfcToolkit() = NfcToolkitScreen()
    @Composable fun ScreenGrid() = ScreenGridScreen()
    @Composable fun VibrationPatterns() = VibrationPatternsScreen()
    @Composable fun StorageAnalyzer() = StorageAnalyzerScreen()
    @Composable fun DeadPixelTester() = DeadPixelTesterScreen()
    @Composable fun TouchTester() = TouchTesterScreen()
    @Composable fun ScreenDimmer() = ScreenDimmerScreen()
    @Composable fun CallLogsAnalytics() = CallLogsAnalyticsScreen()
    @Composable fun BatteryInfo() = BatteryInfoScreen()
}
