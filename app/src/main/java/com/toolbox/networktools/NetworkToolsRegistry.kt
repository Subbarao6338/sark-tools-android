package com.toolbox.networktools

import androidx.compose.runtime.Composable
import com.toolbox.measurement.wifisignal.WifiSignalScreen
import com.toolbox.measurement.networkinfo.NetworkInfoScreen
import com.toolbox.everyday.wifiqr.WifiQrShareScreen
import com.toolbox.web.WebToolsScreen
import com.toolbox.measurement.speedtest.NetworkSpeedTestScreen

object NetworkToolsRegistry {
    @Composable fun WifiSignal() = WifiSignalScreen()
    @Composable fun NetworkInfo() = NetworkInfoScreen()
    @Composable fun WifiQrShare() = WifiQrShareScreen()
    @Composable fun WebTools() = WebToolsScreen()
    @Composable fun NetworkSpeedTest() = NetworkSpeedTestScreen()
}
