package com.toolbox

import com.toolbox.ai.EpicAiScreen
import com.toolbox.automation.AutomationScreen
import com.toolbox.conversion.EpicConvertersCalculatorsScreen
import com.toolbox.datalab.DataLabScreen
import com.toolbox.datetime.EpicDateTimeScreen
import com.toolbox.devtools.EpicDevToolsScreen
import com.toolbox.doctools.EpicDocToolsScreen
import com.toolbox.lifestyle.LifestyleScreen
import com.toolbox.networktools.EpicNetworkToolsScreen
import com.toolbox.systemtools.SystemToolsScreen
import com.toolbox.web.EpicWebToolsScreen
import com.toolbox.widgets.DigitalClockConfigScreen
import com.toolbox.nav.*


import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import com.toolbox.conversion.aspectratio.AspectRatioScreen
import com.toolbox.conversion.bmi.BmiCalculatorScreen
import com.toolbox.conversion.calculator.CalculatorScreen
import com.toolbox.conversion.datecalc.DateCalculatorScreen
import com.toolbox.conversion.discount.DiscountCalculatorScreen
import com.toolbox.conversion.financial.FinancialCalculatorScreen
import com.toolbox.conversion.gst.GstCalculatorScreen
import com.toolbox.conversion.mathutils.MathUtilitiesScreen
import com.toolbox.conversion.numberbase.NumberBaseScreen
import com.toolbox.conversion.percentage.PercentageScreen
import com.toolbox.conversion.tipcalculator.TipCalculatorScreen
import com.toolbox.conversion.unitconverter.UnitConverterScreen
import com.toolbox.core.persistence.ThemeMode
import com.toolbox.core.ui.components.ToolScaffold
import com.toolbox.core.ui.theme.ToolboxTheme
import com.toolbox.dashboard.DashboardScreen
import com.toolbox.dashboard.allTools
import com.toolbox.favorites.FavoritesScreen
import com.toolbox.everyday.colorpicker.ColorPickerScreen
import com.toolbox.everyday.counter.CounterScreen
import com.toolbox.everyday.magnifier.MagnifierScreen
import com.toolbox.everyday.qrscanner.QrScannerScreen
import com.toolbox.everyday.wifiqr.WifiQrShareScreen
import com.toolbox.everyday.photocleanup.PhotoCleanupScreen
import com.toolbox.conversion.wiregauge.WireGaugeScreen
import com.toolbox.conversion.paint.PaintCalculatorScreen
import com.toolbox.conversion.screwbolt.ScrewBoltScreen
import com.toolbox.lighting.screengrid.ScreenGridScreen
import com.toolbox.everyday.ttsreader.TtsReaderScreen
import com.toolbox.everyday.vibration.VibrationPatternsScreen
import com.toolbox.everyday.ocr.OcrScreen
import com.toolbox.everyday.qrgen.QrGeneratorScreen
import com.toolbox.everyday.notepad.NotepadScreen
import com.toolbox.everyday.focustimer.FocusTimerScreen
import com.toolbox.everyday.textutils.TextUtilsScreen
import com.toolbox.conversion.unitprice.UnitPriceScreen
import com.toolbox.conversion.billsplit.BillSplitScreen
import com.toolbox.everyday.hashencode.HashEncodeScreen
import com.toolbox.everyday.worldclock.WorldClockScreen
import com.toolbox.everyday.sleeptimer.SleepTimerScreen
import com.toolbox.everyday.habits.HabitTrackerScreen
import com.toolbox.everyday.eyetest.EyeTestScreen
import com.toolbox.everyday.voicerecorder.VoiceRecorderScreen
import com.toolbox.everyday.pdftoolkit.PdfToolkitScreen
import com.toolbox.everyday.stitcher.ScreenshotStitcherScreen
import com.toolbox.everyday.docscanner.DocumentScannerScreen
import com.toolbox.everyday.screenrecorder.ScreenRecorderScreen
import com.toolbox.everyday.statussaver.StatusSaverScreen
import com.toolbox.everyday.duplicatephotos.DuplicatePhotosScreen
import com.toolbox.everyday.storageanalyzer.StorageAnalyzerScreen
import com.toolbox.nav.ScreenshotStitcher
import com.toolbox.nav.DocumentScanner
import com.toolbox.nav.ScreenRecorder
import com.toolbox.nav.StatusSaver
import com.toolbox.nav.DuplicatePhotos
import com.toolbox.nav.StorageAnalyzer
import com.toolbox.conversion.unitcircle.UnitCircleScreen
import com.toolbox.lighting.FlashlightScreen
import com.toolbox.everyday.random.RandomScreen
import com.toolbox.everyday.stopwatch.StopwatchTimerScreen
import com.toolbox.everyday.breathing.BreathingExerciseScreen
import com.toolbox.everyday.metronome.MetronomeScreen
import com.toolbox.everyday.morse.MorseCodeScreen
import com.toolbox.lighting.screenflash.ScreenFlashScreen
import com.toolbox.measurement.pitchtuner.PitchTunerScreen
import com.toolbox.measurement.plumbbob.PlumbBobScreen
import com.toolbox.measurement.wifisignal.WifiSignalScreen
import com.toolbox.measurement.bluetoothfinder.BluetoothFinderScreen
import com.toolbox.everyday.password.PasswordGeneratorScreen
import com.toolbox.everyday.whitenoise.WhiteNoiseScreen
import com.toolbox.everyday.heartrate.HeartRateScreen
import com.toolbox.everyday.mirror.MirrorScreen
import com.toolbox.motion.pedometer.PedometerScreen
import com.toolbox.measurement.barometer.BarometerScreen
import com.toolbox.measurement.gyroscope.GyroscopeScreen
import com.toolbox.measurement.humidity.HumidityScreen
import com.toolbox.measurement.compass.CompassScreen
import com.toolbox.measurement.level.LevelScreen
import com.toolbox.measurement.lightmeter.LightMeterScreen
import com.toolbox.measurement.metaldetector.MetalDetectorScreen
import com.toolbox.measurement.protractor.ProtractorScreen
import com.toolbox.measurement.ruler.RulerScreen
import com.toolbox.measurement.altitude.AltitudeScreen
import com.toolbox.measurement.soundmeter.SoundMeterScreen
import com.toolbox.measurement.speedometer.SpeedometerScreen
import com.toolbox.measurement.spectrum.SpectrumScreen
import com.toolbox.measurement.vibrometer.VibrometerScreen
import com.toolbox.conversion.emi.EmiCalculatorScreen
import com.toolbox.conversion.formula.FormulaScreen
import com.toolbox.measurement.deviceinfo.DeviceInfoScreen
import com.toolbox.measurement.networkinfo.NetworkInfoScreen
import com.toolbox.everyday.nfc.NfcToolkitScreen
import com.toolbox.everyday.tonegenerator.ToneGeneratorScreen
import com.toolbox.nav.AspectRatio
import com.toolbox.nav.BmiCalculator
import com.toolbox.nav.BreathingExercise
import com.toolbox.nav.DateCalculator
import com.toolbox.nav.Metronome
import com.toolbox.nav.PasswordGenerator
import com.toolbox.nav.PitchTuner
import com.toolbox.nav.PlumbBob
import com.toolbox.nav.ScreenFlash
import com.toolbox.nav.ScientificCalculator
import com.toolbox.nav.MorseCode
import com.toolbox.nav.WifiSignal
import com.toolbox.nav.BluetoothFinder
import com.toolbox.nav.WhiteNoise
import com.toolbox.nav.Barometer
import com.toolbox.nav.Humidity
import com.toolbox.nav.Gyroscope
import com.toolbox.nav.HeartRate
import com.toolbox.nav.Speedometer
import com.toolbox.nav.Altitude
import com.toolbox.nav.SpectrumAnalyzer
import com.toolbox.nav.Pedometer
import com.toolbox.nav.Compass
import com.toolbox.nav.ColorPicker
import com.toolbox.nav.Counter
import com.toolbox.nav.Dashboard
import com.toolbox.nav.Flashlight
import com.toolbox.nav.Level
import com.toolbox.nav.LightMeter
import com.toolbox.nav.Magnifier
import com.toolbox.nav.MetalDetector
import com.toolbox.nav.Mirror
import com.toolbox.nav.NumberBase
import com.toolbox.nav.PercentageCalculator
import com.toolbox.nav.Protractor
import com.toolbox.nav.QrScanner
import com.toolbox.nav.WifiQrShare
import com.toolbox.nav.PhotoCleanup
import com.toolbox.nav.WireGauge
import com.toolbox.nav.PaintCalculator
import com.toolbox.nav.ScrewBolt
import com.toolbox.nav.ScreenGrid
import com.toolbox.nav.TtsReader
import com.toolbox.nav.VibrationPatterns
import com.toolbox.nav.Ocr
import com.toolbox.nav.UnitCircle
import com.toolbox.nav.QrGenerator
import com.toolbox.nav.Notepad
import com.toolbox.nav.FocusTimer
import com.toolbox.nav.TextUtils
import com.toolbox.nav.UnitPrice
import com.toolbox.nav.BillSplit
import com.toolbox.nav.HashEncode
import com.toolbox.nav.WorldClock
import com.toolbox.nav.SleepTimer
import com.toolbox.nav.HabitTracker
import com.toolbox.nav.EyeTest
import com.toolbox.nav.VoiceRecorder
import com.toolbox.nav.PdfToolkit
import com.toolbox.nav.RandomGenerator
import com.toolbox.nav.Ruler
import com.toolbox.nav.SoundMeter
import com.toolbox.nav.StopwatchTimer
import com.toolbox.nav.TipCalculator
import com.toolbox.nav.UnitConverter
import com.toolbox.nav.Vibrometer
import com.toolbox.nav.EmiCalculator
import com.toolbox.nav.DeviceInfo
import com.toolbox.nav.NetworkInfo
import com.toolbox.nav.NfcToolkit
import com.toolbox.nav.ToneGenerator
import com.toolbox.nav.FormulaReference
import com.toolbox.settings.SettingsScreen
import com.toolbox.media.image.ImageEditorScreen
import com.toolbox.media.audio.AudioEditorScreen
import com.toolbox.media.video.VideoEditorScreen
import com.toolbox.documents.pdf.PdfEditorScreen
import com.toolbox.documents.word.WordEditorScreen
import com.toolbox.documents.creator.DocumentCreationScreen
import com.toolbox.web.WebToolsScreen
import com.toolbox.labs.deadpixel.DeadPixelTesterScreen
import com.toolbox.labs.touch.TouchTesterScreen
import com.toolbox.labs.dimmer.ScreenDimmerScreen
import com.toolbox.telephony.call.CallLogsAnalyticsScreen
import com.toolbox.pokedex.PokedexScreen
import com.toolbox.telephony.sms.MessagesAnalyticsScreen
import com.toolbox.education.EducationHubScreen
import com.toolbox.education.PeriodicTableScreen
import com.toolbox.education.ChemistryGamesScreen
import com.toolbox.education.ScienceLabsScreen
import com.toolbox.education.BrainGamesScreen
import com.toolbox.nav.PeriodicTableNav
import com.toolbox.nav.ChemistryGamesNav
import com.toolbox.nav.ScienceLabsNav
import com.toolbox.nav.BrainGamesNav
import com.toolbox.nav.EducationHub
import com.toolbox.nav.ImageEditor
import com.toolbox.nav.AudioEditor
import com.toolbox.nav.VideoEditor
import com.toolbox.nav.PdfEditor
import com.toolbox.nav.WordEditor
import com.toolbox.nav.DocumentCreation
import com.toolbox.nav.WebToolsNav
import com.toolbox.nav.DeadPixelTester
import com.toolbox.nav.TouchTester
import com.toolbox.nav.ScreenDimmer
import com.toolbox.nav.CallLogsAnalytics
import com.toolbox.nav.MessagesAnalytics
import com.toolbox.measurement.battery.BatteryInfoScreen
import com.toolbox.nav.Pokedex
import com.toolbox.conversion.currency.CurrencyConverterScreen
import com.toolbox.measurement.speedtest.NetworkSpeedTestScreen
import com.toolbox.conversion.fuel.FuelCalculatorScreen
import com.toolbox.measurement.luxguide.LuxGuideScreen
import com.toolbox.nav.BatteryInfo
import com.toolbox.nav.CurrencyConverter
import com.toolbox.nav.NetworkSpeedTest
import com.toolbox.nav.FuelCalculator
import com.toolbox.nav.LuxGuide
import com.toolbox.nav.DiscountCalculator
import com.toolbox.nav.GstCalculator
import com.toolbox.nav.FinancialCalculators
import com.toolbox.nav.MathUtilities
import com.toolbox.nav.BmrCalculator
import com.toolbox.nav.ContrastChecker
import com.toolbox.nav.RecipeScaler
import com.toolbox.nav.IntervalTimer
import com.toolbox.nav.LengthConverter
import com.toolbox.nav.WeightConverter
import com.toolbox.nav.TemperatureConverter
import com.toolbox.nav.StorageConverter
import com.toolbox.nav.VolumeConverter
import com.toolbox.nav.SpeedConverter
import com.toolbox.nav.AreaConverter
import com.toolbox.nav.TimeConverter
import com.toolbox.nav.PressureConverter
import com.toolbox.nav.EnergyConverter
import com.toolbox.nav.PowerConverter
import com.toolbox.nav.ForceConverter
import com.toolbox.nav.TorqueConverter
import com.toolbox.nav.DensityConverter
import com.toolbox.nav.FuelEconomyConverter
import com.toolbox.nav.AngleConverter
import com.toolbox.nav.FrequencyConverter
import com.toolbox.conversion.bmr.BmrCalculatorScreen
import com.toolbox.conversion.contrast.ContrastCheckerScreen
import com.toolbox.conversion.recipe.RecipeScalerScreen
import com.toolbox.everyday.interval.IntervalTimerScreen
import com.toolbox.conversion.units.LengthConverterScreen
import com.toolbox.conversion.units.WeightConverterScreen
import com.toolbox.conversion.units.TemperatureConverterScreen
import com.toolbox.conversion.units.DataStorageConverterScreen
import com.toolbox.conversion.units.DedicatedUnitConverterScreen
import com.toolbox.conversion.unitconverter.UnitCategory
import com.toolbox.datetime.timediff.TimeDiffScreen
import com.toolbox.datetime.moonphase.MoonPhaseScreen
import com.toolbox.social.hashtag.HashtagGeneratorScreen
import com.toolbox.social.biocard.SocialBioScreen
import com.toolbox.social.preview.SocialPreviewScreen
import com.toolbox.social.quotemaker.QuoteMakerScreen
import com.toolbox.nav.TimeDiff
import com.toolbox.nav.MoonPhase
import com.toolbox.nav.HashtagGenerator
import com.toolbox.nav.SocialBio
import com.toolbox.nav.SocialPreview
import com.toolbox.nav.QuoteMaker
import com.toolbox.nav.WidgetsManager
import com.toolbox.nav.QuickTilesManager
import com.toolbox.nav.ShortcutsManager
import com.toolbox.nav.EquationSolver
import com.toolbox.nav.GeometryCalculator
import com.toolbox.nav.InvestmentCalculator
import com.toolbox.nav.GpaCalculator
import com.toolbox.nav.PerchanceAi
import com.toolbox.nav.PerchanceTextGen
import com.toolbox.nav.PerchancePhotoGen
import com.toolbox.nav.PerchanceChat
import com.toolbox.nav.PerchanceVideoGen
import com.toolbox.nav.PerchanceStoryGen
import com.toolbox.nav.PerchanceCharacterGen
import com.toolbox.nav.PerchanceAnimeGen
import com.toolbox.nav.PerchanceRpgGen
import com.toolbox.nav.PerchanceWritingPrompt
import com.toolbox.nav.PerchanceLoreGen
import com.toolbox.nav.PerchanceDialogueGen
import com.toolbox.nav.PerchanceVoiceActor
import com.toolbox.nav.PerchanceArtPlugin
import com.toolbox.nav.PerchanceNameGen
import com.toolbox.nav.HuggingFaceAi
import com.toolbox.ai.AiWebViewScreen
import com.toolbox.nav.BasicCalculator
import com.toolbox.nav.FractionCalculator
import com.toolbox.nav.RatioCalculator
import com.toolbox.nav.SortDedupeLines
import com.toolbox.nav.FindReplace
import com.toolbox.nav.TextDiff
import com.toolbox.nav.LoremIpsum
import com.toolbox.nav.NumberToWords
import com.toolbox.nav.RomanNumerals
import com.toolbox.nav.FancyText
import com.toolbox.nav.EmojiPicker
import com.toolbox.nav.ImageResizer
import com.toolbox.nav.ImageFormatConverter
import com.toolbox.nav.PdfToImages
import com.toolbox.nav.CompressPdf
import com.toolbox.nav.BarcodeGenerator
import com.toolbox.nav.ScanHistory
import com.toolbox.nav.JsonFormatter
import com.toolbox.nav.UuidGenerator
import com.toolbox.nav.ColorConverter
import com.toolbox.nav.HtmlEntityTool
import com.toolbox.nav.TimestampConverter
import com.toolbox.nav.RegexTester
import com.toolbox.nav.WorkingDaysCalculator
import com.toolbox.nav.WaterReminder
import com.toolbox.nav.PregnancyCalculator
import com.toolbox.nav.IdealWeightCalculator
import com.toolbox.nav.SleepCycleCalculator
import com.toolbox.nav.SpinWheel
import com.toolbox.nav.RandomNamePicker
import com.toolbox.nav.PassphraseGenerator
import com.toolbox.nav.WaterIntakeCalc
import com.toolbox.nav.BodyFatCalc
import com.toolbox.nav.TargetHeartRate
import com.toolbox.nav.MacroCalc
import com.toolbox.nav.PaceCalc
import com.toolbox.nav.PasswordAudit
import com.toolbox.nav.AesEncryption
import com.toolbox.nav.ChecksumVerifier
import com.toolbox.nav.Steganography
import com.toolbox.nav.UuidValidator
import com.toolbox.nav.ColorPaletteGen
import com.toolbox.nav.SoundExposure
import com.toolbox.nav.MatrixCalc
import com.toolbox.nav.BinaryBitwise
import com.toolbox.nav.MorseTrainer
import com.toolbox.nav.ResistorColorCalc
import com.toolbox.healthfitness.WaterIntakeCalcScreen
import com.toolbox.healthfitness.BodyFatCalcScreen
import com.toolbox.healthfitness.TargetHeartRateScreen
import com.toolbox.healthfitness.MacroCalcScreen
import com.toolbox.healthfitness.PaceCalcScreen
import com.toolbox.securityprivacy.PasswordAuditScreen
import com.toolbox.securityprivacy.AesEncryptionScreen
import com.toolbox.securityprivacy.ChecksumVerifierScreen
import com.toolbox.securityprivacy.SteganographyScreen
import com.toolbox.securityprivacy.UuidValidatorScreen
import com.toolbox.media.imagesuite.ColorPaletteGenScreen
import com.toolbox.media.audio.SoundExposureScreen
import com.toolbox.conversion.algebra.MatrixCalcScreen
import com.toolbox.conversion.numberbase.BinaryBitwiseScreen
import com.toolbox.everyday.morse.MorseTrainerScreen
import com.toolbox.conversion.screwbolt.ResistorColorCalcScreen
import com.toolbox.conversion.basiccalc.BasicCalculatorScreen
import com.toolbox.conversion.fraction.FractionCalculatorScreen
import com.toolbox.conversion.ratio.RatioCalculatorScreen
import com.toolbox.everyday.textsuite.SortDedupeLinesScreen
import com.toolbox.everyday.textsuite.FindReplaceScreen
import com.toolbox.everyday.textsuite.TextDiffScreen
import com.toolbox.everyday.textsuite.LoremIpsumScreen
import com.toolbox.everyday.textsuite.NumberToWordsScreen
import com.toolbox.everyday.textsuite.RomanNumeralsScreen
import com.toolbox.everyday.textsuite.FancyTextScreen
import com.toolbox.everyday.textsuite.EmojiPickerScreen
import com.toolbox.media.imagesuite.ImageResizerScreen
import com.toolbox.media.imagesuite.ImageFormatConverterScreen
import com.toolbox.doctools.PdfToImagesScreen
import com.toolbox.doctools.CompressPdfScreen
import com.toolbox.everyday.barcodesuite.BarcodeGeneratorScreen
import com.toolbox.everyday.barcodesuite.ScanHistoryScreen
import com.toolbox.conversion.devsuite.JsonFormatterScreen
import com.toolbox.conversion.devsuite.UuidGeneratorScreen
import com.toolbox.conversion.devsuite.ColorConverterScreen
import com.toolbox.conversion.devsuite.HtmlEntityScreen
import com.toolbox.conversion.devsuite.TimestampConverterScreen
import com.toolbox.conversion.devsuite.RegexTesterScreen
import com.toolbox.datetime.workingdays.WorkingDaysScreen
import com.toolbox.conversion.healthsuite.WaterReminderScreen
import com.toolbox.conversion.healthsuite.PregnancyCalculatorScreen
import com.toolbox.conversion.healthsuite.IdealWeightCalculatorScreen
import com.toolbox.conversion.healthsuite.SleepCycleScreen
import com.toolbox.everyday.randomsuite.SpinWheelScreen
import com.toolbox.everyday.randomsuite.RandomNamePickerScreen
import com.toolbox.everyday.randomsuite.PassphraseGeneratorScreen


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ToolboxApp(
    themeMode: ThemeMode,
    natureTheme: com.toolbox.core.persistence.NatureTheme = com.toolbox.core.persistence.NatureTheme.Default,
    launchToolId: String? = null,
    defaultScreenId: String = "dashboard",
) {
    ToolboxTheme(themeMode = themeMode, natureTheme = natureTheme) {
        Surface(modifier = Modifier.fillMaxSize()) {
            val navController = rememberNavController()

            // Root-hosted "Rate us" prompt disabled per request
            // Startup is always Dashboard
            val isDefaultTool = false
            val startDest: Any = Dashboard
            val initialTab = 0
            // Handle deep-link from Quick Settings tile
            androidx.compose.runtime.LaunchedEffect(launchToolId) {
                if (launchToolId != null) {
                    val destination = toolDestination(launchToolId)
                    if (destination != null) {
                        navController.navigate(destination)
                    }
                }
            }

            SharedTransitionLayout {
                NavHost(
                    navController = navController,
                    startDestination = startDest,
                    enterTransition = { fadeIn(tween(220, easing = EaseOutCubic)) + slideInHorizontally(tween(220, easing = EaseOutCubic)) { it / 4 } },
                    exitTransition = { fadeOut(tween(220, easing = EaseOutCubic)) },
                    popEnterTransition = { fadeIn(tween(220, easing = EaseOutCubic)) },
                    popExitTransition = { fadeOut(tween(220, easing = EaseOutCubic)) + slideOutHorizontally(tween(220, easing = EaseOutCubic)) { it / 4 } },
                ) {
                    composable<Dashboard> {
                        var selectedTab by rememberSaveable { mutableIntStateOf(initialTab) }

                        Scaffold(
                            topBar = {
                                Column(
                                    modifier = Modifier
                                        .statusBarsPadding()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                ) {
                                    Text(
                                        text = when (selectedTab) {
                                            1 -> "Favorites"
                                            2 -> "Settings"
                                            else -> "Sark Tools"
                                        },
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    if (selectedTab == 1) {
                                        Text(
                                            text = "Your pinned tools for quick access.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            },
                            bottomBar = {
                                NavigationBar {
                                    NavigationBarItem(
                                        selected = selectedTab == 0,
                                        onClick = { selectedTab = 0 },
                                        icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                                        label = { Text("Dashboard") },
                                    )
                                    NavigationBarItem(
                                        selected = selectedTab == 1,
                                        onClick = { selectedTab = 1 },
                                        icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = null) },
                                        label = { Text("Favorites") },
                                    )
                                    NavigationBarItem(
                                        selected = selectedTab == 2,
                                        onClick = { selectedTab = 2 },
                                        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                                        label = { Text("Settings") },
                                    )
                                }
                            },
                        ) { padding ->
                            Box(modifier = Modifier.padding(padding)) {
                                when (selectedTab) {
                                    0 -> DashboardScreen(
                                        onToolClick = { tool ->
                                            val destination = toolDestination(tool.id)
                                            if (destination != null) {
                                                navController.navigate(destination)
                                            }
                                        },
                                        sharedTransitionScope = this@SharedTransitionLayout,
                                        animatedVisibilityScope = this@composable,
                                    )
                                    1 -> FavoritesScreen(
                                        onToolClick = { tool ->
                                            val destination = toolDestination(tool.id)
                                            if (destination != null) {
                                                navController.navigate(destination)
                                            }
                                        },
                                        onBrowseAll = { selectedTab = 0 },
                                    )
                                    2 -> SettingsScreen()
                                }
                            }
                        }
                    }

                    // Tool screens — when launched as the app root (default screen), back goes to Dashboard
                    composable<Level> { ToolScreen("Bubble Level", "level", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is Level) { LevelScreen() } }
                    composable<Compass> { ToolScreen("Compass", "compass", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is Compass) { CompassScreen() } }
                    composable<Protractor> { ToolScreen("Protractor", "protractor", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is Protractor) { ProtractorScreen() } }
                    composable<SoundMeter> { ToolScreen("Sound Meter", "sound_meter", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is SoundMeter) { SoundMeterScreen() } }
                    composable<Ruler> { ToolScreen("Ruler", "ruler", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is Ruler) { RulerScreen() } }
                    composable<UnitConverter> { ToolScreen("Unit Converter", "unit_converter", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is UnitConverter) { UnitConverterScreen() } }
                    composable<PercentageCalculator> { ToolScreen("Percentage", "percentage", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is PercentageCalculator) { PercentageScreen() } }
                    composable<TipCalculator> { ToolScreen("Tip Calculator", "tip_calculator", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is TipCalculator) { TipCalculatorScreen() } }
                    composable<NumberBase> { ToolScreen("Number Base", "number_base", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is NumberBase) { NumberBaseScreen() } }
                    composable<Flashlight> { ToolScreen("Flashlight", "flashlight", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is Flashlight) { FlashlightScreen() } }
                    composable<QrScanner> { ToolScreen("QR & Barcode", "qr_scanner", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is QrScanner) { QrScannerScreen() } }
                    composable<Counter> { ToolScreen("Counter", "counter", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is Counter) { CounterScreen() } }
                    composable<StopwatchTimer> { ToolScreen("Stopwatch & Timer", "stopwatch_timer", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is StopwatchTimer) { StopwatchTimerScreen() } }
                    composable<RandomGenerator> { ToolScreen("Random Generator", "random_generator", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is RandomGenerator) { RandomScreen() } }
                    composable<ColorPicker> { ToolScreen("Color Picker", "color_picker", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is ColorPicker) { ColorPickerScreen() } }
                    composable<Magnifier> { ToolScreen("Magnifier", "magnifier", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is Magnifier) { MagnifierScreen() } }
                    composable<Mirror> { ToolScreen("Mirror", "mirror", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is Mirror) { MirrorScreen() } }
                    composable<Vibrometer> { ToolScreen("Vibrometer", "vibrometer", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is Vibrometer) { VibrometerScreen() } }
                    composable<LightMeter> { ToolScreen("Light Meter", "light_meter", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is LightMeter) { LightMeterScreen() } }
                    composable<MetalDetector> { ToolScreen("Metal Detector", "metal_detector", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is MetalDetector) { MetalDetectorScreen() } }
                    composable<Barometer> { ToolScreen("Barometer", "barometer", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is Barometer) { BarometerScreen() } }
                    composable<Humidity> { ToolScreen("Humidity", "humidity", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is Humidity) { HumidityScreen() } }
                    composable<Pedometer> { ToolScreen("Pedometer", "pedometer", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is Pedometer) { PedometerScreen() } }
                    composable<Gyroscope> { ToolScreen("Gyroscope", "gyroscope", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is Gyroscope) { GyroscopeScreen() } }
                    composable<HeartRate> { ToolScreen("Heart Rate", "heart_rate", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is HeartRate) { HeartRateScreen() } }
                    composable<SpectrumAnalyzer> { ToolScreen("Spectrum Analyzer", "spectrum_analyzer", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is SpectrumAnalyzer) { SpectrumScreen() } }
                    composable<Speedometer> { ToolScreen("Speedometer", "speedometer", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is Speedometer) { SpeedometerScreen() } }
                    composable<Altitude> { ToolScreen("Altitude", "altitude", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is Altitude) { AltitudeScreen() } }
                    composable<BreathingExercise> { ToolScreen("Breathing Exercise", "breathing_exercise", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is BreathingExercise) { BreathingExerciseScreen() } }
                    composable<ScientificCalculator> { ToolScreen("Calculator", "scientific_calculator", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is ScientificCalculator) { CalculatorScreen() } }
                    composable<MorseCode> { ToolScreen("Morse Code", "morse_code", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is MorseCode) { MorseCodeScreen() } }
                    composable<DateCalculator> { ToolScreen("Date/Age Calculator", "date_calculator", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is DateCalculator) { DateCalculatorScreen() } }
                    composable<BmiCalculator> { ToolScreen("BMI Calculator", "bmi_calculator", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is BmiCalculator) { BmiCalculatorScreen() } }
                    composable<AspectRatio> { ToolScreen("Aspect Ratio", "aspect_ratio", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is AspectRatio) { AspectRatioScreen() } }
                    composable<PasswordGenerator> { ToolScreen("Password Generator", "password_generator", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is PasswordGenerator) { PasswordGeneratorScreen() } }
                    composable<WhiteNoise> { ToolScreen("Ambient Sounds", "white_noise", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is WhiteNoise) { WhiteNoiseScreen() } }
                    composable<Metronome> { ToolScreen("Metronome", "metronome", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is Metronome) { MetronomeScreen() } }
                    composable<PitchTuner> { ToolScreen("Pitch Tuner", "pitch_tuner", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is PitchTuner) { PitchTunerScreen() } }
                    composable<ScreenFlash> { ToolScreen("Screen Flash", "screen_flash", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is ScreenFlash) { ScreenFlashScreen() } }
                    composable<PlumbBob> { ToolScreen("Plumb Bob", "plumb_bob", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is PlumbBob) { PlumbBobScreen() } }
                    composable<WifiSignal> { ToolScreen("WiFi Signal", "wifi_signal", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is WifiSignal) { WifiSignalScreen() } }
                    composable<BluetoothFinder> { ToolScreen("Bluetooth Finder", "bluetooth_finder", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is BluetoothFinder, accent = androidx.compose.ui.graphics.Color(0xFF00B0FF)) { BluetoothFinderScreen() } }

                    // Batch 2 Tools
                    composable<EmiCalculator> { ToolScreen("Loan/EMI Calculator", "emi_calculator", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is EmiCalculator) { EmiCalculatorScreen() } }
                    composable<DeviceInfo> { ToolScreen("Device Info", "device_info", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is DeviceInfo) { DeviceInfoScreen() } }
                    composable<NetworkInfo> { ToolScreen("Network Info", "network_info", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is NetworkInfo) { NetworkInfoScreen() } }
                    composable<NfcToolkit> { ToolScreen("NFC Toolkit", "nfc_toolkit", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is NfcToolkit) { NfcToolkitScreen() } }
                    composable<ToneGenerator> { ToolScreen("Tone Generator", "tone_generator", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is ToneGenerator) { ToneGeneratorScreen() } }
                    composable<FormulaReference> { ToolScreen("Formulas", "formula_reference", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is FormulaReference) { FormulaScreen() } }
                    composable<WifiQrShare> { ToolScreen("WiFi QR Share", "wifi_qr_share", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is WifiQrShare) { WifiQrShareScreen() } }
                    composable<PhotoCleanup> { ToolScreen("Photo Cleanup", "photo_cleanup", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is PhotoCleanup) { PhotoCleanupScreen() } }
                    composable<WireGauge> { ToolScreen("Wire Gauge", "wire_gauge", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is WireGauge) { WireGaugeScreen() } }
                    composable<PaintCalculator> { ToolScreen("Paint Calculator", "paint_calculator", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is PaintCalculator) { PaintCalculatorScreen() } }
                    composable<ScrewBolt> { ToolScreen("Screw & Bolt", "screw_bolt", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is ScrewBolt) { ScrewBoltScreen() } }
                    composable<ScreenGrid> { ToolScreen("Screen Test Grid", "screen_grid", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is ScreenGrid) { ScreenGridScreen() } }
                    composable<TtsReader> { ToolScreen("TTS Reader", "tts_reader", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is TtsReader) { TtsReaderScreen() } }
                    composable<VibrationPatterns> { ToolScreen("Vibration Patterns", "vibration_patterns", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is VibrationPatterns) { VibrationPatternsScreen() } }
                    composable<Ocr> { ToolScreen("Text Scanner", "ocr", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is Ocr) { OcrScreen() } }
                    composable<UnitCircle> { ToolScreen("Unit Circle", "unit_circle", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is UnitCircle) { UnitCircleScreen() } }
                    composable<QrGenerator> { ToolScreen("QR Generator", "qr_generator", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is QrGenerator, accent = androidx.compose.ui.graphics.Color(0xFF00897B)) { QrGeneratorScreen() } }
                    composable<Notepad> { ToolScreen("Notepad", "notepad", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is Notepad, accent = androidx.compose.ui.graphics.Color(0xFFF9A825)) { NotepadScreen() } }
                    composable<FocusTimer> { ToolScreen("Focus Timer", "focus_timer", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is FocusTimer) { FocusTimerScreen() } }
                    composable<TextUtils> { ToolScreen("Text Utilities", "text_utilities", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is TextUtils, accent = androidx.compose.ui.graphics.Color(0xFF3949AB)) { TextUtilsScreen() } }
                    composable<UnitPrice> { ToolScreen("Unit Price", "unit_price", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is UnitPrice, accent = androidx.compose.ui.graphics.Color(0xFF2E7D32)) { UnitPriceScreen() } }
                    composable<BillSplit> { ToolScreen("Bill Split", "bill_split", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is BillSplit, accent = androidx.compose.ui.graphics.Color(0xFF43A047)) { BillSplitScreen() } }
                    composable<HashEncode> { ToolScreen("Hash & Encode", "hash_encode", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is HashEncode, accent = androidx.compose.ui.graphics.Color(0xFF5E35B1)) { HashEncodeScreen() } }
                    composable<WorldClock> { ToolScreen("World Clock", "world_clock", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is WorldClock, accent = androidx.compose.ui.graphics.Color(0xFF1E88E5)) { WorldClockScreen() } }
                    composable<SleepTimer> { ToolScreen("Sleep Timer", "sleep_timer", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is SleepTimer, accent = androidx.compose.ui.graphics.Color(0xFF3F51B5)) { SleepTimerScreen() } }
                    composable<HabitTracker> { ToolScreen("Habit Tracker", "habit_tracker", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is HabitTracker, accent = androidx.compose.ui.graphics.Color(0xFFEF6C00)) { HabitTrackerScreen() } }
                    composable<EyeTest> { ToolScreen("Eye Test", "eye_test", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is EyeTest, accent = androidx.compose.ui.graphics.Color(0xFF00ACC1)) { EyeTestScreen() } }
                    composable<VoiceRecorder> { ToolScreen("Voice Recorder", "voice_recorder", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is VoiceRecorder, accent = androidx.compose.ui.graphics.Color(0xFFD81B60)) { VoiceRecorderScreen() } }
                    composable<PdfToolkit> { ToolScreen("PDF Toolkit", "pdf_toolkit", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is PdfToolkit, accent = androidx.compose.ui.graphics.Color(0xFFE53935)) { PdfToolkitScreen() } }
                    composable<ScreenshotStitcher> { ToolScreen("Screenshot Stitcher", "screenshot_stitcher", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is ScreenshotStitcher, accent = androidx.compose.ui.graphics.Color(0xFF8E24AA)) { ScreenshotStitcherScreen() } }
                    composable<DocumentScanner> { ToolScreen("Document Scanner", "document_scanner", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is DocumentScanner, accent = androidx.compose.ui.graphics.Color(0xFF039BE5)) { DocumentScannerScreen() } }
                    composable<ScreenRecorder> { ToolScreen("Screen Recorder", "screen_recorder", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is ScreenRecorder, accent = androidx.compose.ui.graphics.Color(0xFFC62828)) { ScreenRecorderScreen() } }
                    composable<StatusSaver> { ToolScreen("Status Saver", "status_saver", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is StatusSaver, accent = androidx.compose.ui.graphics.Color(0xFF2E7D32)) { StatusSaverScreen() } }
                    composable<DuplicatePhotos> { ToolScreen("Duplicate Photos", "duplicate_photos", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is DuplicatePhotos, accent = androidx.compose.ui.graphics.Color(0xFFF4511E)) { DuplicatePhotosScreen() } }
                    composable<StorageAnalyzer> { ToolScreen("Storage Analyzer", "storage_analyzer", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is StorageAnalyzer, accent = androidx.compose.ui.graphics.Color(0xFF5E35B1)) { StorageAnalyzerScreen() } }
                    composable<ImageEditor> { ToolScreen("Image Editor", "image_editor", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is ImageEditor, accent = androidx.compose.ui.graphics.Color(0xFFD81B60)) { ImageEditorScreen() } }
                    composable<AudioEditor> { ToolScreen("Audio Editor", "audio_editor", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is AudioEditor, accent = androidx.compose.ui.graphics.Color(0xFF3949AB)) { AudioEditorScreen() } }
                    composable<VideoEditor> { ToolScreen("Video Editor", "video_editor", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is VideoEditor, accent = androidx.compose.ui.graphics.Color(0xFFE53935)) { VideoEditorScreen() } }
                    composable<PdfEditor> { ToolScreen("PDF Editor", "pdf_editor", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is PdfEditor, accent = androidx.compose.ui.graphics.Color(0xFF00897B)) { PdfEditorScreen() } }
                    composable<WordEditor> { ToolScreen("Word Editor", "word_editor", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is WordEditor, accent = androidx.compose.ui.graphics.Color(0xFF1E88E5)) { WordEditorScreen() } }
                    composable<DocumentCreation> { ToolScreen("Document Creation", "document_creation", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is DocumentCreation, accent = androidx.compose.ui.graphics.Color(0xFF00ACC1)) { DocumentCreationScreen() } }
                    composable<WebToolsNav> { ToolScreen("Web Tools", "web_tools", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is WebToolsNav, accent = androidx.compose.ui.graphics.Color(0xFF0288D1)) { WebToolsScreen() } }
                    composable<DeadPixelTester> { ToolScreen("Dead Pixel Tester", "dead_pixel_tester", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is DeadPixelTester) { DeadPixelTesterScreen() } }
                    composable<TouchTester> { ToolScreen("Touch Tester", "touch_tester", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is TouchTester) { TouchTesterScreen() } }
                    composable<ScreenDimmer> { ToolScreen("Screen Dimmer", "screen_dimmer", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is ScreenDimmer) { ScreenDimmerScreen() } }
                    composable<CallLogsAnalytics> { ToolScreen("Call Logs Analytics", "call_logs_analytics", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is CallLogsAnalytics, accent = androidx.compose.ui.graphics.Color(0xFF2E7D32)) { CallLogsAnalyticsScreen() } }
                    composable<EducationHub> { ToolScreen("Education", "education_hub", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is EducationHub, accent = androidx.compose.ui.graphics.Color(0xFF5E35B1)) { EducationHubScreen() } }
                    composable<PeriodicTableNav> { ToolScreen("Periodic Table", "periodic_table", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is PeriodicTableNav, accent = androidx.compose.ui.graphics.Color(0xFF00897B)) { PeriodicTableScreen() } }
                    composable<ChemistryGamesNav> { ToolScreen("Chemistry Games", "chemistry_games", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is ChemistryGamesNav, accent = androidx.compose.ui.graphics.Color(0xFFD81B60)) { ChemistryGamesScreen() } }
                    composable<ScienceLabsNav> { ToolScreen("Science Labs", "science_labs", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is ScienceLabsNav, accent = androidx.compose.ui.graphics.Color(0xFF3949AB)) { ScienceLabsScreen() } }
                    composable<BrainGamesNav> { ToolScreen("Brain & IQ Games", "brain_games", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is BrainGamesNav, accent = androidx.compose.ui.graphics.Color(0xFF8E24AA)) { BrainGamesScreen() } }
                    composable<Pokedex> { ToolScreen("Pokédex", "pokedex", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is Pokedex, accent = androidx.compose.ui.graphics.Color(0xFFE53935)) { PokedexScreen() } }
                    composable<MessagesAnalytics> { ToolScreen("Messages Analytics", "messages_analytics", navController, this@SharedTransitionLayout, this@composable, isDefaultRoot = isDefaultTool && startDest is MessagesAnalytics, accent = androidx.compose.ui.graphics.Color(0xFFF57F17)) { MessagesAnalyticsScreen() } }

                    // Newly Integrated Offline Tools
                    composable<BatteryInfo> { ToolScreen("Battery Health", "battery_info", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFF2E7D32)) { BatteryInfoScreen() } }
                    composable<CurrencyConverter> { ToolScreen("Currency", "currency_converter", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFF1976D2)) { CurrencyConverterScreen() } }
                    composable<NetworkSpeedTest> { ToolScreen("Speed Test", "network_speed_test", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFFD84315)) { NetworkSpeedTestScreen() } }
                    composable<FuelCalculator> { ToolScreen("Fuel Economy", "fuel_calculator", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFFE65100)) { FuelCalculatorScreen() } }
                    composable<LuxGuide> { ToolScreen("Lux & Exposure", "lux_guide", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFFF57F17)) { LuxGuideScreen() } }

                    // All-in-One Calculator Category Routes
                    composable<DiscountCalculator> { ToolScreen("Discount Calculator", "discount_calculator", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFFD81B60)) { DiscountCalculatorScreen() } }
                    composable<GstCalculator> { ToolScreen("GST / Tax Calculator", "gst_calculator", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFF1976D2)) { GstCalculatorScreen() } }
                    composable<FinancialCalculators> { ToolScreen("Financial Calculators", "financial_calculators", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFF2E7D32)) { FinancialCalculatorScreen() } }
                    composable<MathUtilities> { ToolScreen("Math Utilities", "math_utilities", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFF6B5BAE)) { MathUtilitiesScreen() } }

                    // Extended Suite Tools
                    composable<BmrCalculator> { ToolScreen("BMR & Calories", "bmr_calculator", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFF1976D2)) { BmrCalculatorScreen() } }
                    composable<ContrastChecker> { ToolScreen("Contrast Checker", "contrast_checker", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFF0097A7)) { ContrastCheckerScreen() } }
                    composable<RecipeScaler> { ToolScreen("Recipe Scaler", "recipe_scaler", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFFE65100)) { RecipeScalerScreen() } }
                    composable<IntervalTimer> { ToolScreen("Interval Timer", "interval_timer", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFFD32F2F)) { IntervalTimerScreen() } }

                    // Unit Converters Category Screens
                    composable<LengthConverter> { ToolScreen("Length Converter", "length_converter", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFF2E7D32)) { LengthConverterScreen() } }
                    composable<WeightConverter> { ToolScreen("Weight Converter", "weight_converter", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFF2E7D32)) { WeightConverterScreen() } }
                    composable<TemperatureConverter> { ToolScreen("Temperature Converter", "temperature_converter", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFF2E7D32)) { TemperatureConverterScreen() } }
                    composable<StorageConverter> { ToolScreen("Data Storage Converter", "storage_converter", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFF2E7D32)) { DataStorageConverterScreen() } }
                    composable<VolumeConverter> { ToolScreen("Volume Converter", "volume_converter", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFF2E7D32)) { DedicatedUnitConverterScreen(UnitCategory.Volume, "Volume") } }
                    composable<SpeedConverter> { ToolScreen("Speed Converter", "speed_converter", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFF2E7D32)) { DedicatedUnitConverterScreen(UnitCategory.Speed, "Speed") } }
                    composable<AreaConverter> { ToolScreen("Area Converter", "area_converter", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFF2E7D32)) { DedicatedUnitConverterScreen(UnitCategory.Area, "Area") } }
                    composable<TimeConverter> { ToolScreen("Time Converter", "time_converter", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFF2E7D32)) { DedicatedUnitConverterScreen(UnitCategory.Time, "Time") } }
                    composable<PressureConverter> { ToolScreen("Pressure Converter", "pressure_converter", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFF2E7D32)) { DedicatedUnitConverterScreen(UnitCategory.Pressure, "Pressure") } }
                    composable<EnergyConverter> { ToolScreen("Energy Converter", "energy_converter", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFF2E7D32)) { DedicatedUnitConverterScreen(UnitCategory.Energy, "Energy") } }
                    composable<PowerConverter> { ToolScreen("Power Converter", "power_converter", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFF2E7D32)) { DedicatedUnitConverterScreen(UnitCategory.Power, "Power") } }
                    composable<ForceConverter> { ToolScreen("Force Converter", "force_converter", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFF2E7D32)) { DedicatedUnitConverterScreen(UnitCategory.Force, "Force") } }
                    composable<TorqueConverter> { ToolScreen("Torque Converter", "torque_converter", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFF2E7D32)) { DedicatedUnitConverterScreen(UnitCategory.Torque, "Torque") } }
                    composable<DensityConverter> { ToolScreen("Density Converter", "density_converter", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFF2E7D32)) { DedicatedUnitConverterScreen(UnitCategory.Density, "Density") } }
                    composable<FuelEconomyConverter> { ToolScreen("Fuel Economy Converter", "fuel_economy_converter", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFF2E7D32)) { DedicatedUnitConverterScreen(UnitCategory.FuelEconomy, "Fuel Economy") } }
                    composable<AngleConverter> { ToolScreen("Angle Converter", "angle_converter", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFF2E7D32)) { DedicatedUnitConverterScreen(UnitCategory.Angle, "Angle") } }
                    composable<FrequencyConverter> { ToolScreen("Frequency Converter", "frequency_converter", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFF2E7D32)) { DedicatedUnitConverterScreen(UnitCategory.Frequency, "Frequency") } }

                    // New Date & Time Category Screens
                    composable<TimeDiff> { ToolScreen("Time Planner", "time_diff", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFF0288D1)) { TimeDiffScreen() } }
                    composable<MoonPhase> { ToolScreen("Moon & Sun", "moon_phase", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFF0288D1)) { MoonPhaseScreen() } }

                    // New Social Category Screens
                    composable<HashtagGenerator> { ToolScreen("Hashtags", "hashtag_generator", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFFE91E63)) { HashtagGeneratorScreen() } }
                    composable<SocialBio> { ToolScreen("Bio Link Card", "social_bio", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFFE91E63)) { SocialBioScreen() } }
                    composable<SocialPreview> { ToolScreen("Post Previewer", "social_preview", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFFE91E63)) { SocialPreviewScreen() } }
                    composable<QuoteMaker> { ToolScreen("Quote Creator", "quote_maker", navController, this@SharedTransitionLayout, this@composable, accent = androidx.compose.ui.graphics.Color(0xFFE91E63)) { QuoteMakerScreen() } }
                    composable<WidgetsManager> {
                        com.toolbox.widgets.WidgetsManagementScreen(onBack = { navController.popBackStack() })
                    }
                    composable<QuickTilesManager> {
                        com.toolbox.tiles.QuickTilesManagementScreen(onBack = { navController.popBackStack() })
                    }
                    composable<ShortcutsManager> {
                        com.toolbox.core.shortcuts.ShortcutsManagementScreen(onBack = { navController.popBackStack() })
                    }

                    // All-in-One Calculator Suite & Perchance AI Additions
                    composable<EquationSolver> { ToolScreen("Equation Solver", "equation_solver", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.conversion.algebra.EquationSolverScreen() } }
                    composable<GeometryCalculator> { ToolScreen("Geometry & Shapes", "geometry_calculator", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.conversion.geometry.GeometryCalculatorScreen() } }
                    composable<InvestmentCalculator> { ToolScreen("Investment & TVM", "investment_calculator", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.conversion.financial.InvestmentCalculatorScreen() } }
                    composable<GpaCalculator> { ToolScreen("GPA / CGPA", "gpa_calculator", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.conversion.gpa.GpaCalculatorScreen() } }
                    composable<PerchanceAi> {
                        ToolScreen("Perchance AI", "perchance_ai", navController, this@SharedTransitionLayout, this@composable) {
                            com.toolbox.ai.PerchanceAiScreen(
                                onLaunchWebView = { url, _, _ ->
                                    when {
                                        url.contains("text-generator") || url.contains("story") -> navController.navigate(PerchanceTextGen)
                                        url.contains("photo-generator") || url.contains("art") || url.contains("anime") -> navController.navigate(PerchancePhotoGen)
                                        url.contains("chat") -> navController.navigate(PerchanceChat)
                                        else -> navController.navigate(PerchanceTextGen)
                                    }
                                }
                            )
                        }
                    }
                    composable<PerchanceTextGen> {
                        AiWebViewScreen(
                            initialUrl = "https://perchance.org/ai-text-generator",
                            toolKey = "perchance_text_gen",
                            toolName = "Perchance AI Text Generator",
                            defaultPresets = listOf(
                                "Text Gen" to "https://perchance.org/ai-text-generator",
                                "Story Gen" to "https://perchance.org/ai-story-generator",
                                "Character Bio" to "https://perchance.org/character-generator",
                                "Writing Prompt" to "https://perchance.org/writing-prompt-generator",
                            ),
                            onBack = { navController.popBackStack() },
                        )
                    }
                    composable<PerchancePhotoGen> {
                        AiWebViewScreen(
                            initialUrl = "https://perchance.org/ai-photo-generator",
                            toolKey = "perchance_photo_gen",
                            toolName = "Perchance AI Photo Generator",
                            defaultPresets = listOf(
                                "Photo Gen" to "https://perchance.org/ai-photo-generator",
                                "Anime Gen" to "https://perchance.org/ai-anime-generator",
                                "Art Gen" to "https://perchance.org/ai-art-generator",
                                "Text to Image" to "https://perchance.org/text-to-image-plugin",
                            ),
                            onBack = { navController.popBackStack() },
                        )
                    }
                    composable<PerchanceChat> {
                        AiWebViewScreen(
                            initialUrl = "https://perchance.org/ai-chat",
                            toolKey = "perchance_chat",
                            toolName = "Perchance AI Chat",
                            defaultPresets = listOf(
                                "AI Chat" to "https://perchance.org/ai-chat",
                                "Character Chat" to "https://perchance.org/ai-character-chat",
                                "Roleplay Hub" to "https://perchance.org/ai-rpg",
                            ),
                            onBack = { navController.popBackStack() },
                        )
                    }
                    composable<PerchanceVideoGen> {
                        AiWebViewScreen(
                            initialUrl = "https://perchance.org/ai-video-generator",
                            toolKey = "perchance_video_gen",
                            toolName = "Perchance AI Video Generator",
                            defaultPresets = listOf(
                                "Video Gen" to "https://perchance.org/ai-video-generator",
                                "Animation" to "https://perchance.org/ai-cinematic-video-generator",
                                "Text-to-Video" to "https://perchance.org/text-to-video",
                            ),
                            onBack = { navController.popBackStack() },
                        )
                    }
                    composable<PerchanceStoryGen> {
                        AiWebViewScreen(
                            initialUrl = "https://perchance.org/ai-story-generator",
                            toolKey = "perchance_story_gen",
                            toolName = "Perchance AI Story Generator",
                            defaultPresets = listOf(
                                "Story Gen" to "https://perchance.org/ai-story-generator",
                                "Novel Outline" to "https://perchance.org/ai-novel-generator",
                                "Plot Twist" to "https://perchance.org/plot-generator",
                            ),
                            onBack = { navController.popBackStack() },
                        )
                    }
                    composable<PerchanceCharacterGen> {
                        AiWebViewScreen(
                            initialUrl = "https://perchance.org/character-generator",
                            toolKey = "perchance_character_gen",
                            toolName = "Perchance Character Generator",
                            defaultPresets = listOf(
                                "Character Bio" to "https://perchance.org/character-generator",
                                "NPC Maker" to "https://perchance.org/npc-generator",
                                "Persona" to "https://perchance.org/ai-character-profile",
                            ),
                            onBack = { navController.popBackStack() },
                        )
                    }
                    composable<PerchanceAnimeGen> {
                        AiWebViewScreen(
                            initialUrl = "https://perchance.org/ai-anime-generator",
                            toolKey = "perchance_anime_gen",
                            toolName = "Perchance AI Anime Generator",
                            defaultPresets = listOf(
                                "Anime Gen" to "https://perchance.org/ai-anime-generator",
                                "Manga Art" to "https://perchance.org/manga-generator",
                                "Anime Avatar" to "https://perchance.org/anime-character-generator",
                            ),
                            onBack = { navController.popBackStack() },
                        )
                    }
                    composable<PerchanceRpgGen> {
                        AiWebViewScreen(
                            initialUrl = "https://perchance.org/ai-rpg",
                            toolKey = "perchance_rpg_gen",
                            toolName = "Perchance AI RPG",
                            defaultPresets = listOf(
                                "RPG Hub" to "https://perchance.org/ai-rpg",
                                "Text Adventure" to "https://perchance.org/ai-text-adventure",
                                "Dungeon Crawl" to "https://perchance.org/dungeon-generator",
                            ),
                            onBack = { navController.popBackStack() },
                        )
                    }
                    composable<PerchanceWritingPrompt> {
                        AiWebViewScreen(
                            initialUrl = "https://perchance.org/writing-prompt-generator",
                            toolKey = "perchance_writing_prompt",
                            toolName = "Perchance Writing Prompts",
                            defaultPresets = listOf(
                                "Writing Prompt" to "https://perchance.org/writing-prompt-generator",
                                "Dialogue Prompts" to "https://perchance.org/dialogue-prompt-generator",
                                "First Lines" to "https://perchance.org/first-line-generator",
                            ),
                            onBack = { navController.popBackStack() },
                        )
                    }
                    composable<PerchanceLoreGen> {
                        AiWebViewScreen(
                            initialUrl = "https://perchance.org/fantasy-world-generator",
                            toolKey = "perchance_lore_gen",
                            toolName = "Perchance World & Lore",
                            defaultPresets = listOf(
                                "World Gen" to "https://perchance.org/fantasy-world-generator",
                                "Magic System" to "https://perchance.org/magic-system-generator",
                                "Faction Maker" to "https://perchance.org/faction-generator",
                            ),
                            onBack = { navController.popBackStack() },
                        )
                    }
                    composable<PerchanceDialogueGen> {
                        AiWebViewScreen(
                            initialUrl = "https://perchance.org/dialogue-generator",
                            toolKey = "perchance_dialogue_gen",
                            toolName = "Perchance AI Dialogue",
                            defaultPresets = listOf(
                                "Dialogue Gen" to "https://perchance.org/dialogue-generator",
                                "Script Banter" to "https://perchance.org/banter-generator",
                                "Quotes" to "https://perchance.org/quote-generator",
                            ),
                            onBack = { navController.popBackStack() },
                        )
                    }
                    composable<PerchanceVoiceActor> {
                        AiWebViewScreen(
                            initialUrl = "https://perchance.org/ai-voice-generator",
                            toolKey = "perchance_voice_actor",
                            toolName = "Perchance AI Voice & Audio",
                            defaultPresets = listOf(
                                "Voice Gen" to "https://perchance.org/ai-voice-generator",
                                "TTS Engine" to "https://perchance.org/text-to-speech-generator",
                                "Sound Effects" to "https://perchance.org/sound-effect-generator",
                            ),
                            onBack = { navController.popBackStack() },
                        )
                    }
                    composable<PerchanceArtPlugin> {
                        AiWebViewScreen(
                            initialUrl = "https://perchance.org/text-to-image-plugin",
                            toolKey = "perchance_art_plugin",
                            toolName = "Perchance Art Plugin",
                            defaultPresets = listOf(
                                "Art Plugin" to "https://perchance.org/text-to-image-plugin",
                                "AI Art Studio" to "https://perchance.org/ai-art-generator",
                                "Styles" to "https://perchance.org/art-style-generator",
                            ),
                            onBack = { navController.popBackStack() },
                        )
                    }
                    composable<PerchanceNameGen> {
                        AiWebViewScreen(
                            initialUrl = "https://perchance.org/fantasy-name-generator",
                            toolKey = "perchance_name_gen",
                            toolName = "Perchance Fantasy Names",
                            defaultPresets = listOf(
                                "Fantasy Names" to "https://perchance.org/fantasy-name-generator",
                                "Sci-Fi Names" to "https://perchance.org/scifi-name-generator",
                                "Town Names" to "https://perchance.org/town-name-generator",
                            ),
                            onBack = { navController.popBackStack() },
                        )
                    }
                    composable<HuggingFaceAi> {
                        AiWebViewScreen(
                            initialUrl = "https://huggingface.co/chat",
                            toolKey = "huggingface_ai",
                            toolName = "Hugging Face AI",
                            defaultPresets = listOf(
                                "HuggingChat" to "https://huggingface.co/chat",
                                "Models" to "https://huggingface.co/models",
                                "Spaces" to "https://huggingface.co/spaces",
                                "Datasets" to "https://huggingface.co/datasets",
                            ),
                            onBack = { navController.popBackStack() },
                        )
                    }

                    // MultiTool Clean-Room Spec Routes
                    composable<BasicCalculator> { ToolScreen("Basic Calculator", "basic_calculator", navController, this@SharedTransitionLayout, this@composable) { BasicCalculatorScreen() } }
                    composable<FractionCalculator> { ToolScreen("Fraction Calculator", "fraction_calculator", navController, this@SharedTransitionLayout, this@composable) { FractionCalculatorScreen() } }
                    composable<RatioCalculator> { ToolScreen("Ratio & Proportion", "ratio_calculator", navController, this@SharedTransitionLayout, this@composable) { RatioCalculatorScreen() } }
                    composable<SortDedupeLines> { ToolScreen("Sort & Dedupe Lines", "sort_dedupe_lines", navController, this@SharedTransitionLayout, this@composable) { SortDedupeLinesScreen() } }
                    composable<FindReplace> { ToolScreen("Find & Replace", "find_replace", navController, this@SharedTransitionLayout, this@composable) { FindReplaceScreen() } }
                    composable<TextDiff> { ToolScreen("Text Diff", "text_diff", navController, this@SharedTransitionLayout, this@composable) { TextDiffScreen() } }
                    composable<LoremIpsum> { ToolScreen("Lorem Ipsum", "lorem_ipsum", navController, this@SharedTransitionLayout, this@composable) { LoremIpsumScreen() } }
                    composable<NumberToWords> { ToolScreen("Number to Words", "number_to_words", navController, this@SharedTransitionLayout, this@composable) { NumberToWordsScreen() } }
                    composable<RomanNumerals> { ToolScreen("Roman Numerals", "roman_numerals", navController, this@SharedTransitionLayout, this@composable) { RomanNumeralsScreen() } }
                    composable<FancyText> { ToolScreen("Fancy Text", "fancy_text", navController, this@SharedTransitionLayout, this@composable) { FancyTextScreen() } }
                    composable<EmojiPicker> { ToolScreen("Emoji & Symbols", "emoji_picker", navController, this@SharedTransitionLayout, this@composable) { EmojiPickerScreen() } }
                    composable<ImageResizer> { ToolScreen("Image Resizer", "image_resizer", navController, this@SharedTransitionLayout, this@composable) { ImageResizerScreen() } }
                    composable<ImageFormatConverter> { ToolScreen("Image Converter", "image_format_converter", navController, this@SharedTransitionLayout, this@composable) { ImageFormatConverterScreen() } }
                    composable<PdfToImages> { ToolScreen("PDF to Images", "pdf_to_images", navController, this@SharedTransitionLayout, this@composable) { PdfToImagesScreen() } }
                    composable<CompressPdf> { ToolScreen("Compress PDF", "compress_pdf", navController, this@SharedTransitionLayout, this@composable) { CompressPdfScreen() } }
                    composable<BarcodeGenerator> { ToolScreen("Barcode Generator", "barcode_generator", navController, this@SharedTransitionLayout, this@composable) { BarcodeGeneratorScreen() } }
                    composable<ScanHistory> { ToolScreen("Scan History", "scan_history", navController, this@SharedTransitionLayout, this@composable) { ScanHistoryScreen() } }
                    composable<JsonFormatter> { ToolScreen("JSON Formatter", "json_formatter", navController, this@SharedTransitionLayout, this@composable) { JsonFormatterScreen() } }
                    composable<UuidGenerator> { ToolScreen("UUID Generator", "uuid_generator", navController, this@SharedTransitionLayout, this@composable) { UuidGeneratorScreen() } }
                    composable<ColorConverter> { ToolScreen("Color Converter", "color_converter", navController, this@SharedTransitionLayout, this@composable) { ColorConverterScreen() } }
                    composable<HtmlEntityTool> { ToolScreen("HTML Entities", "html_entity_tool", navController, this@SharedTransitionLayout, this@composable) { HtmlEntityScreen() } }
                    composable<TimestampConverter> { ToolScreen("Timestamp Converter", "timestamp_converter", navController, this@SharedTransitionLayout, this@composable) { TimestampConverterScreen() } }
                    composable<RegexTester> { ToolScreen("Regex Tester", "regex_tester", navController, this@SharedTransitionLayout, this@composable) { RegexTesterScreen() } }
                    composable<WorkingDaysCalculator> { ToolScreen("Working Days", "working_days_calculator", navController, this@SharedTransitionLayout, this@composable) { WorkingDaysScreen() } }
                    composable<WaterReminder> { ToolScreen("Water Tracker", "water_reminder", navController, this@SharedTransitionLayout, this@composable) { WaterReminderScreen() } }
                    composable<PregnancyCalculator> { ToolScreen("Pregnancy Calculator", "pregnancy_calculator", navController, this@SharedTransitionLayout, this@composable) { PregnancyCalculatorScreen() } }
                    composable<IdealWeightCalculator> { ToolScreen("Ideal Weight", "ideal_weight_calculator", navController, this@SharedTransitionLayout, this@composable) { IdealWeightCalculatorScreen() } }
                    composable<SleepCycleCalculator> { ToolScreen("Sleep Cycle", "sleep_cycle_calculator", navController, this@SharedTransitionLayout, this@composable) { SleepCycleScreen() } }
                    composable<SpinWheel> { ToolScreen("Spin the Wheel", "spin_wheel", navController, this@SharedTransitionLayout, this@composable) { SpinWheelScreen() } }
                    composable<RandomNamePicker> { ToolScreen("Name / List Picker", "random_name_picker", navController, this@SharedTransitionLayout, this@composable) { RandomNamePickerScreen() } }
                    composable<PassphraseGenerator> { ToolScreen("Passphrase Generator", "passphrase_generator", navController, this@SharedTransitionLayout, this@composable) { PassphraseGeneratorScreen() } }
                    composable<WaterIntakeCalc> { ToolScreen("Hydration Calculator", "water_intake_calc", navController, this@SharedTransitionLayout, this@composable) { WaterIntakeCalcScreen() } }
                    composable<BodyFatCalc> { ToolScreen("Body Fat Calculator", "body_fat_calc", navController, this@SharedTransitionLayout, this@composable) { BodyFatCalcScreen() } }
                    composable<TargetHeartRate> { ToolScreen("Target Heart Rate", "target_heart_rate", navController, this@SharedTransitionLayout, this@composable) { TargetHeartRateScreen() } }
                    composable<MacroCalc> { ToolScreen("Macro Calculator", "macro_calc", navController, this@SharedTransitionLayout, this@composable) { MacroCalcScreen() } }
                    composable<PaceCalc> { ToolScreen("Running Pace", "pace_calc", navController, this@SharedTransitionLayout, this@composable) { PaceCalcScreen() } }
                    composable<PasswordAudit> { ToolScreen("Password Auditor", "password_audit", navController, this@SharedTransitionLayout, this@composable) { PasswordAuditScreen() } }
                    composable<AesEncryption> { ToolScreen("AES-256 Encryptor", "aes_encryption", navController, this@SharedTransitionLayout, this@composable) { AesEncryptionScreen() } }
                    composable<ChecksumVerifier> { ToolScreen("Checksum Verifier", "checksum_verifier", navController, this@SharedTransitionLayout, this@composable) { ChecksumVerifierScreen() } }
                    composable<Steganography> { ToolScreen("Text Steganography", "steganography", navController, this@SharedTransitionLayout, this@composable) { SteganographyScreen() } }
                    composable<UuidValidator> { ToolScreen("UUID Inspector", "uuid_validator", navController, this@SharedTransitionLayout, this@composable) { UuidValidatorScreen() } }
                    composable<ColorPaletteGen> { ToolScreen("Palette Generator", "color_palette_gen", navController, this@SharedTransitionLayout, this@composable) { ColorPaletteGenScreen() } }
                    composable<SoundExposure> { ToolScreen("Sound Exposure", "sound_exposure", navController, this@SharedTransitionLayout, this@composable) { SoundExposureScreen() } }
                    composable<MatrixCalc> { ToolScreen("Matrix Calculator", "matrix_calc", navController, this@SharedTransitionLayout, this@composable) { MatrixCalcScreen() } }
                    composable<BinaryBitwise> { ToolScreen("Bitwise Calculator", "binary_bitwise", navController, this@SharedTransitionLayout, this@composable) { BinaryBitwiseScreen() } }
                    composable<MorseTrainer> { ToolScreen("Morse Trainer", "morse_trainer", navController, this@SharedTransitionLayout, this@composable) { MorseTrainerScreen() } }
                    composable<ResistorColorCalc> { ToolScreen("Resistor Color Code", "resistor_color_calc", navController, this@SharedTransitionLayout, this@composable) { ResistorColorCalcScreen() } }
                    composable<EpicImageGen> { ToolScreen("AI Image Gen", "image-gen", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.ai.EpicAiScreen("image-gen") } }
                    composable<EpicChat> { ToolScreen("AI Chat Assistant", "chat", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.ai.EpicAiScreen("chat") } }
                    composable<EpicLocal> { ToolScreen("Local AI Utilities", "local", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.ai.EpicAiScreen("local") } }
                    composable<EpicAgentIngest> { ToolScreen("Code Ingestion", "agent-ingest", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.automation.AutomationScreen("agent-ingest") } }
                    composable<EpicAgentGenerate> { ToolScreen("Test Generation", "agent-generate", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.automation.AutomationScreen("agent-generate") } }
                    composable<EpicAgentResults> { ToolScreen("View Results", "agent-results", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.automation.AutomationScreen("agent-results") } }
                    composable<EpicAgentSetup> { ToolScreen("API Setup", "agent-setup", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.automation.AutomationScreen("agent-setup") } }
                    composable<EpicNotionIngest> { ToolScreen("Notion Ingest", "notion-ingest", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.automation.AutomationScreen("notion-ingest") } }
                    composable<EpicNotionFolder> { ToolScreen("Folder Sync", "notion-folder", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.automation.AutomationScreen("notion-folder") } }
                    composable<EpicNotionScraper> { ToolScreen("Web Scraper", "notion-scraper", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.automation.AutomationScreen("notion-scraper") } }
                    composable<EpicNotionHistory> { ToolScreen("Sync History", "notion-history", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.automation.AutomationScreen("notion-history") } }
                    composable<EpicNotionSetup> { ToolScreen("Integration Setup", "notion-setup", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.automation.AutomationScreen("notion-setup") } }
                    composable<EpicDatediff> { ToolScreen("Date Difference", "datediff", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.conversion.EpicConvertersCalculatorsScreen("datediff") } }
                    composable<EpicSubnet> { ToolScreen("Subnet Calculator", "subnet", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.conversion.EpicConvertersCalculatorsScreen("subnet") } }
                    composable<EpicWordRank> { ToolScreen("Word Rank Calculator", "word-rank", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.conversion.EpicConvertersCalculatorsScreen("word-rank") } }
                    composable<EpicJsonCsv> { ToolScreen("JSON ↔ CSV Converter", "json-csv", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.conversion.EpicConvertersCalculatorsScreen("json-csv") } }
                    composable<EpicXmlJson> { ToolScreen("XML ↔ JSON Converter", "xml-json", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.conversion.EpicConvertersCalculatorsScreen("xml-json") } }
                    composable<EpicYaml> { ToolScreen("YAML ↔ JSON Converter", "yaml", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.conversion.EpicConvertersCalculatorsScreen("yaml") } }
                    composable<EpicBase64> { ToolScreen("Base64 Converter", "base64", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.conversion.EpicConvertersCalculatorsScreen("base64") } }
                    composable<EpicTimezone> { ToolScreen("Timezone Converter", "timezone", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.conversion.EpicConvertersCalculatorsScreen("timezone") } }
                    composable<EpicBatch> { ToolScreen("Batch Converter", "batch", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.conversion.EpicConvertersCalculatorsScreen("batch") } }
                    composable<EpicViewer> { ToolScreen("Data Viewer", "viewer", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.datalab.DataLabScreen("viewer") } }
                    composable<EpicScience> { ToolScreen("Data Science Hub", "science", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.datalab.DataLabScreen("science") } }
                    composable<EpicAdvData> { ToolScreen("Advanced Data Hub", "adv-data", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.datalab.DataLabScreen("adv-data") } }
                    composable<EpicReconcile> { ToolScreen("Reconciliation Tool", "reconcile", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.datalab.DataLabScreen("reconcile") } }
                    composable<EpicMock> { ToolScreen("Mock Data Generator", "mock", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.datalab.DataLabScreen("mock") } }
                    composable<EpicSynthetic> { ToolScreen("Synthetic Data Tool", "synthetic", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.datalab.DataLabScreen("synthetic") } }
                    composable<EpicPomodoro> { ToolScreen("Pomodoro Timer", "pomodoro", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.datetime.EpicDateTimeScreen("pomodoro") } }
                    composable<EpicCountdown> { ToolScreen("Countdown Timer", "countdown", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.datetime.EpicDateTimeScreen("countdown") } }
                    composable<EpicPanchangam> { ToolScreen("Telugu Panchangam", "panchangam", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.datetime.EpicDateTimeScreen("panchangam") } }
                    composable<EpicApiTester> { ToolScreen("REST API Tester", "api-tester", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.devtools.EpicDevToolsScreen("api-tester") } }
                    composable<EpicSql> { ToolScreen("SQL Formatter", "sql", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.devtools.EpicDevToolsScreen("sql") } }
                    composable<EpicXmlFmt> { ToolScreen("XML Formatter", "xml-fmt", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.devtools.EpicDevToolsScreen("xml-fmt") } }
                    composable<EpicJsonTs> { ToolScreen("JSON to TS Interface", "json-ts", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.devtools.EpicDevToolsScreen("json-ts") } }
                    composable<EpicKusto> { ToolScreen("KQL Formatter", "kusto", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.devtools.EpicDevToolsScreen("kusto") } }
                    composable<EpicDiff> { ToolScreen("Diff Viewer", "diff", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.devtools.EpicDevToolsScreen("diff") } }
                    composable<EpicJwt> { ToolScreen("JWT Debugger", "jwt", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.devtools.EpicDevToolsScreen("jwt") } }
                    composable<EpicUrl> { ToolScreen("URL Tool", "url", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.devtools.EpicDevToolsScreen("url") } }
                    composable<EpicCron> { ToolScreen("Cron Parser", "cron", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.devtools.EpicDevToolsScreen("cron") } }
                    composable<EpicMinifier> { ToolScreen("Code Minifier", "minifier", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.devtools.EpicDevToolsScreen("minifier") } }
                    composable<EpicQrBarcode> { ToolScreen("QR & Barcode Generator", "qr-barcode", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.devtools.EpicDevToolsScreen("qr-barcode") } }
                    composable<EpicInspiration> { ToolScreen("Code Inspiration", "inspiration", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.devtools.EpicDevToolsScreen("inspiration") } }
                    composable<EpicMdTable> { ToolScreen("Markdown Table", "md-table", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.devtools.EpicDevToolsScreen("md-table") } }
                    composable<EpicSqlBuilder> { ToolScreen("SQL Builder", "sql-builder", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.devtools.EpicDevToolsScreen("sql-builder") } }
                    composable<EpicPdf> { ToolScreen("PDF Hub", "pdf", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.doctools.EpicDocToolsScreen("pdf") } }
                    composable<EpicDocxMd> { ToolScreen("Word to MD Converter", "docx-md", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.doctools.EpicDocToolsScreen("docx-md") } }
                    composable<EpicDocTranslator> { ToolScreen("Doc Translator", "doc-translator", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.doctools.EpicDocToolsScreen("doc-translator") } }
                    composable<EpicText> { ToolScreen("Text Hub", "text", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.doctools.EpicDocToolsScreen("text") } }
                    composable<EpicMdEditor> { ToolScreen("Markdown Editor", "md-editor", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.doctools.EpicDocToolsScreen("md-editor") } }
                    composable<EpicFinance> { ToolScreen("Finance Hub", "finance", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.doctools.EpicDocToolsScreen("finance") } }
                    composable<EpicImage> { ToolScreen("Image Hub", "image", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.doctools.EpicDocToolsScreen("image") } }
                    composable<EpicImageLab> { ToolScreen("Image Lab", "image-lab", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.doctools.EpicDocToolsScreen("image-lab") } }
                    composable<EpicFashionGuide> { ToolScreen("Fashion Styling Guide", "fashion-guide", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.lifestyle.LifestyleScreen("fashion-guide") } }
                    composable<EpicClothesGuide> { ToolScreen("Clothes & Care Guide", "clothes-guide", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.lifestyle.LifestyleScreen("clothes-guide") } }
                    composable<EpicTraditionalGuide> { ToolScreen("Traditional & Ethnic Guide", "traditional-guide", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.lifestyle.LifestyleScreen("traditional-guide") } }
                    composable<EpicWorldwide> { ToolScreen("World Fashion Guide", "worldwide", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.lifestyle.LifestyleScreen("worldwide") } }
                    composable<EpicPing> { ToolScreen("Ping Tester", "ping", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.networktools.EpicNetworkToolsScreen("ping") } }
                    composable<EpicDns> { ToolScreen("DNS Lookup", "dns", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.networktools.EpicNetworkToolsScreen("dns") } }
                    composable<EpicWhois> { ToolScreen("WHOIS Record", "whois", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.networktools.EpicNetworkToolsScreen("whois") } }
                    composable<EpicIpInfo> { ToolScreen("IP Information", "ip-info", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.networktools.EpicNetworkToolsScreen("ip-info") } }
                    composable<EpicGeo> { ToolScreen("Geolocation Tool", "geo", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.networktools.EpicNetworkToolsScreen("geo") } }
                    composable<EpicSsl> { ToolScreen("SSL Checker", "ssl", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.networktools.EpicNetworkToolsScreen("ssl") } }
                    composable<EpicAnonymizer> { ToolScreen("PII Anonymizer", "anonymizer", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.networktools.EpicNetworkToolsScreen("anonymizer") } }
                    composable<EpicSecurity> { ToolScreen("Hash & HMAC Generator", "security", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.networktools.EpicNetworkToolsScreen("security") } }
                    composable<EpicRsa> { ToolScreen("RSA Key Generator", "rsa", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.networktools.EpicNetworkToolsScreen("rsa") } }
                    composable<EpicOtp> { ToolScreen("OTP Generator", "otp", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.networktools.EpicNetworkToolsScreen("otp") } }
                    composable<EpicSizeGuide> { ToolScreen("Size & Body Guide", "size-guide", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.lifestyle.LifestyleScreen("size-guide") } }
                    composable<EpicShoes> { ToolScreen("Footwear Guide", "shoes", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.lifestyle.LifestyleScreen("shoes") } }
                    composable<EpicAccessories> { ToolScreen("Accessories Guide", "accessories", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.lifestyle.LifestyleScreen("accessories") } }
                    composable<EpicSocial> { ToolScreen("Social Audit", "social", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.web.EpicWebToolsScreen("social") } }
                    composable<EpicSocialDownloader> { ToolScreen("Social Downloader", "social-downloader", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.web.EpicWebToolsScreen("social-downloader") } }
                    composable<EpicStatus> { ToolScreen("System Status", "status", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.systemtools.SystemToolsScreen("status") } }
                    composable<EpicTelemetry> { ToolScreen("Live Telemetry", "telemetry", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.systemtools.SystemToolsScreen("telemetry") } }
                    composable<EpicLineage> { ToolScreen("Data Lineage", "lineage", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.systemtools.SystemToolsScreen("lineage") } }
                    composable<EpicExtensionSources> { ToolScreen("Extension Sources", "extension-sources", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.web.EpicWebToolsScreen("extension-sources") } }
                    composable<EpicArchive> { ToolScreen("Web Archive", "archive", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.web.EpicWebToolsScreen("archive") } }
                    composable<EpicUrl2pdf> { ToolScreen("URL to PDF", "url2pdf", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.web.EpicWebToolsScreen("url2pdf") } }
                    composable<EpicUserscripts> { ToolScreen("User Scripts", "userscripts", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.web.EpicWebToolsScreen("userscripts") } }
                    composable<EpicBookmarklets> { ToolScreen("Bookmarklets", "bookmarklets", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.web.EpicWebToolsScreen("bookmarklets") } }
                    composable<EpicUrl2markdown> { ToolScreen("Web URL to Markdown", "url2markdown", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.web.EpicWebToolsScreen("url2markdown") } }
                    composable<EpicBluetooth> { ToolScreen("Bluetooth Scanner", "bluetooth", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.networktools.EpicNetworkToolsScreen("bluetooth") } }
                    composable<DigitalClockWidgetConfig> {
                        com.toolbox.widgets.WidgetsManagementScreen(onBack = { navController.popBackStack() }, initialTab = 1)
                    }
                    composable<TsvConverterDest> { ToolScreen("TSV Converter", "tsv_converter", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.conversion.TsvConverterScreen() } }
                    composable<LoremGeneratorDest> { ToolScreen("Lorem Generator", "lorem_generator", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.everyday.LoremGeneratorScreen() } }
                    composable<CsvCleanerDest> { ToolScreen("CSV Cleaner", "csv_cleaner", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.datalab.CsvCleanerScreen() } }
                    composable<AspectRatioCalcDest> { ToolScreen("Aspect Ratio Pro", "aspect_ratio_calc", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.calculators.AspectRatioScreen() } }
                    composable<MorseAudioSynthDest> { ToolScreen("Morse Tone Synth", "morse_audio_synth", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.audiotools.MorseAudioSynthScreen() } }
                    composable<FileChecksumDest> { ToolScreen("Multi Checksum", "file_checksum_calc", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.securityprivacy.FileChecksumScreen() } }
                    composable<SystemUptimeDest> { ToolScreen("System Uptime", "system_uptime", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.device.SystemUptimeScreen() } }
                    composable<ColorContrastMatrixDest> { ToolScreen("WCAG Contrast", "color_contrast_matrix", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.devtools.ColorContrastMatrixScreen() } }
                    composable<BaseConverterProDest> { ToolScreen("Base Converter Pro", "base_converter_pro", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.programmingtools.BaseConverterProScreen() } }
                    composable<WifiChannelScannerDest> { ToolScreen("Wi-Fi Channels", "wifi_channel_scanner", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.networktools.WifiChannelScannerScreen() } }
                    composable<CalorieBurnCalcDest> { ToolScreen("Calorie Burn", "calorie_burn_calc", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.healthfitness.CalorieBurnCalcScreen() } }
                    composable<QuoteGeneratorDest> { ToolScreen("Daily Quotes", "quote_generator", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.lifestyle.QuoteGeneratorScreen() } }
                    composable<UrlBuilderDest> { ToolScreen("URL Builder", "url_builder", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.devtools.UrlBuilderScreen() } }
                    composable<CronExplainerDest> { ToolScreen("Cron Explainer", "cron_explainer", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.devtools.CronExplainerScreen() } }
                    composable<JsonSchemaValidatorDest> { ToolScreen("Schema Validator", "json_schema_validator", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.devtools.JsonSchemaValidatorScreen() } }
                    composable<TextDiffViewerDest> { ToolScreen("Diff Viewer", "text_diff_viewer", navController, this@SharedTransitionLayout, this@composable) { com.toolbox.everyday.TextDiffViewerScreen() } }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun ToolScreen(
    title: String,
    toolId: String,
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    isDefaultRoot: Boolean = false,
    accent: androidx.compose.ui.graphics.Color? = null,
    content: @Composable () -> Unit,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val repository = remember { com.toolbox.core.persistence.UserPreferencesRepository(context) }
    val favoriteIds by repository.favoriteToolIds.collectAsState(initial = emptySet())
    val isFavorite = toolId in favoriteIds
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    // When this tool IS the default root and has no previous back stack entry (i.e. the user
    // launched the app directly into this tool), intercept BOTH the system back gesture and the
    // scaffold back button so they navigate to Dashboard instead of exiting the app.
    val isAtRoot = isDefaultRoot && navController.previousBackStackEntry == null
    BackHandler(enabled = isAtRoot) {
        navController.navigate(Dashboard) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }
    val onBack: () -> Unit = if (isAtRoot) {
        {
            navController.navigate(Dashboard) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    } else {
        { navController.popBackStack() }
    }

    val helpText = remember(toolId) { allTools.find { it.id == toolId }?.description?.takeIf { it.isNotBlank() } }

    val sharedModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            Modifier.sharedBounds(
                rememberSharedContentState(key = "tool_$toolId"),
                animatedVisibilityScope = animatedVisibilityScope,
                resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                enter = fadeIn(),
                exit = fadeOut(),
            )
        }
    } else {
        Modifier
    }

    ToolScaffold(
        title = title,
        onBack = onBack,
        modifier = sharedModifier,
        helpText = helpText,
        actions = {
            IconButton(onClick = { scope.launch { repository.toggleFavorite(toolId) } }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (accent != null) {
                androidx.compose.runtime.CompositionLocalProvider(
                    com.toolbox.core.ui.LocalAccent provides accent,
                ) {
                    // Recolor the tool's content so buttons, result numbers, chips and sliders
                    // pick up the tool's vivid accent automatically.
                    MaterialTheme(
                        colorScheme = MaterialTheme.colorScheme.copy(
                            primary = accent,
                            onPrimary = androidx.compose.ui.graphics.Color.White,
                            outline = accent,
                            outlineVariant = accent.copy(alpha = 0.5f),
                        ),
                        typography = MaterialTheme.typography,
                        shapes = MaterialTheme.shapes,
                    ) {
                        com.toolbox.core.ui.PlayfulEntrance {
                            Column(modifier = Modifier.fillMaxSize()) {
                                val toolIcon = allTools.find { it.id == toolId }?.icon
                                if (toolIcon != null) {
                                    com.toolbox.core.ui.ToolIntro(icon = toolIcon, title = title, subtitle = helpText)
                                }
                                Box(modifier = Modifier.weight(1f).fillMaxWidth()) { content() }
                            }
                        }
                    }
                }
            } else {
                content()
            }
        }
    }
}

@Composable
private fun PlaceholderTab(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}


private fun toolDestination(toolId: String): Any? = when (toolId) {
    "level" -> Level
    "compass" -> Compass
    "protractor" -> Protractor
    "sound_meter" -> SoundMeter
    "ruler" -> Ruler
    "unit_converter" -> UnitConverter
    "percentage" -> PercentageCalculator
    "tip_calculator" -> TipCalculator
    "number_base" -> NumberBase
    "flashlight" -> Flashlight
    "qr_scanner" -> QrScanner
    "counter" -> Counter
    "stopwatch_timer" -> StopwatchTimer
    "random_generator" -> RandomGenerator
    "color_picker" -> ColorPicker
    "magnifier" -> Magnifier
    "mirror" -> Mirror
    "vibrometer" -> Vibrometer
    "light_meter" -> LightMeter
    "metal_detector" -> MetalDetector
    "barometer" -> Barometer
    "humidity" -> Humidity
    "pedometer" -> Pedometer
    "gyroscope" -> Gyroscope
    "heart_rate" -> HeartRate
    "spectrum_analyzer" -> SpectrumAnalyzer
    "speedometer" -> Speedometer
    "altitude" -> Altitude
    "breathing_exercise" -> BreathingExercise
    "scientific_calculator" -> ScientificCalculator
    "morse_code" -> MorseCode
    "date_calculator" -> DateCalculator
    "bmi_calculator" -> BmiCalculator
    "aspect_ratio" -> AspectRatio
    "password_generator" -> PasswordGenerator
    "white_noise" -> WhiteNoise
    "metronome" -> Metronome
    "pitch_tuner" -> PitchTuner
    "screen_flash" -> ScreenFlash
    "plumb_bob" -> PlumbBob
    "wifi_signal" -> WifiSignal
    "bluetooth_finder" -> BluetoothFinder
    "emi_calculator" -> EmiCalculator
    "device_info" -> DeviceInfo
    "network_info" -> NetworkInfo
    "nfc_toolkit" -> NfcToolkit
    "tone_generator" -> ToneGenerator
    "formula_reference" -> FormulaReference
    "wifi_qr_share" -> WifiQrShare
    "photo_cleanup" -> PhotoCleanup
    "wire_gauge" -> WireGauge
    "paint_calculator" -> PaintCalculator
    "screw_bolt" -> ScrewBolt
    "screen_grid" -> ScreenGrid
    "tts_reader" -> TtsReader
    "vibration_patterns" -> VibrationPatterns
    "ocr" -> Ocr
    "unit_circle" -> UnitCircle
    "qr_generator" -> QrGenerator
    "notepad" -> Notepad
    "focus_timer" -> FocusTimer
    "text_utilities" -> TextUtils
    "unit_price" -> UnitPrice
    "bill_split" -> BillSplit
    "hash_encode" -> HashEncode
    "world_clock" -> WorldClock
    "sleep_timer" -> SleepTimer
    "habit_tracker" -> HabitTracker
    "eye_test" -> EyeTest
    "voice_recorder" -> VoiceRecorder
    "pdf_toolkit" -> PdfToolkit
    "screenshot_stitcher" -> ScreenshotStitcher
    "document_scanner" -> DocumentScanner
    "screen_recorder" -> ScreenRecorder
    "status_saver" -> StatusSaver
    "duplicate_photos" -> DuplicatePhotos
    "storage_analyzer" -> StorageAnalyzer
        "image_editor" -> ImageEditor
    "audio_editor" -> AudioEditor
    "video_editor" -> VideoEditor
    "pdf_editor" -> PdfEditor
    "word_editor" -> WordEditor
    "document_creation" -> DocumentCreation
    "web_tools" -> WebToolsNav
    "dead_pixel_tester" -> DeadPixelTester
    "touch_tester" -> TouchTester
    "screen_dimmer" -> ScreenDimmer
    "call_logs_analytics" -> CallLogsAnalytics
    "messages_analytics" -> MessagesAnalytics
    "education_hub" -> EducationHub
    "periodic_table" -> PeriodicTableNav
    "chemistry_games" -> ChemistryGamesNav
    "science_labs" -> ScienceLabsNav
    "brain_games" -> BrainGamesNav
    "pokedex" -> Pokedex
    "battery_info" -> BatteryInfo
    "currency_converter" -> CurrencyConverter
    "network_speed_test" -> NetworkSpeedTest
    "fuel_calculator" -> FuelCalculator
    "lux_guide" -> LuxGuide
    "discount_calculator" -> DiscountCalculator
    "gst_calculator" -> GstCalculator
    "financial_calculators" -> FinancialCalculators
    "math_utilities" -> MathUtilities
    "bmr_calculator" -> BmrCalculator
    "contrast_checker" -> ContrastChecker
    "recipe_scaler" -> RecipeScaler
    "interval_timer" -> IntervalTimer
    "length_converter" -> LengthConverter
    "weight_converter" -> WeightConverter
    "temperature_converter" -> TemperatureConverter
    "storage_converter" -> StorageConverter
    "volume_converter" -> VolumeConverter
    "speed_converter" -> SpeedConverter
    "area_converter" -> AreaConverter
    "time_converter" -> TimeConverter
    "pressure_converter" -> PressureConverter
    "energy_converter" -> EnergyConverter
    "power_converter" -> PowerConverter
    "force_converter" -> ForceConverter
    "torque_converter" -> TorqueConverter
    "density_converter" -> DensityConverter
    "fuel_economy_converter" -> FuelEconomyConverter
    "angle_converter" -> AngleConverter
    "frequency_converter" -> FrequencyConverter
    "time_diff" -> TimeDiff
    "moon_phase" -> MoonPhase
    "hashtag_generator" -> HashtagGenerator
    "social_bio" -> SocialBio
    "social_preview" -> SocialPreview
    "quote_maker" -> QuoteMaker
    "widgets_manager" -> WidgetsManager
    "quick_tiles_manager" -> QuickTilesManager
    "shortcuts_manager" -> ShortcutsManager
    "equation_solver" -> EquationSolver
    "geometry_calculator" -> GeometryCalculator
    "investment_calculator" -> InvestmentCalculator
    "gpa_calculator" -> GpaCalculator
    "perchance_ai" -> PerchanceAi
    "perchance_text_gen" -> PerchanceTextGen
    "perchance_photo_gen" -> PerchancePhotoGen
    "perchance_chat" -> PerchanceChat
    "perchance_video_gen" -> PerchanceVideoGen
    "perchance_story_gen" -> PerchanceStoryGen
    "perchance_character_gen" -> PerchanceCharacterGen
    "perchance_anime_gen" -> PerchanceAnimeGen
    "perchance_rpg_gen" -> PerchanceRpgGen
    "perchance_writing_prompt" -> PerchanceWritingPrompt
    "perchance_lore_gen" -> PerchanceLoreGen
    "perchance_dialogue_gen" -> PerchanceDialogueGen
    "perchance_voice_actor" -> PerchanceVoiceActor
    "perchance_art_plugin" -> PerchanceArtPlugin
    "perchance_name_gen" -> PerchanceNameGen
    "huggingface_ai" -> HuggingFaceAi
    "basic_calculator" -> BasicCalculator
    "fraction_calculator" -> FractionCalculator
    "ratio_calculator" -> RatioCalculator
    "sort_dedupe_lines" -> SortDedupeLines
    "find_replace" -> FindReplace
    "text_diff" -> TextDiff
    "lorem_ipsum" -> LoremIpsum
    "number_to_words" -> NumberToWords
    "roman_numerals" -> RomanNumerals
    "fancy_text" -> FancyText
    "emoji_picker" -> EmojiPicker
    "image_resizer" -> ImageResizer
    "image_format_converter" -> ImageFormatConverter
    "pdf_to_images" -> PdfToImages
    "compress_pdf" -> CompressPdf
    "barcode_generator" -> BarcodeGenerator
    "scan_history" -> ScanHistory
    "json_formatter" -> JsonFormatter
    "uuid_generator" -> UuidGenerator
    "color_converter" -> ColorConverter
    "html_entity_tool" -> HtmlEntityTool
    "timestamp_converter" -> TimestampConverter
    "regex_tester" -> RegexTester
    "working_days_calculator" -> WorkingDaysCalculator
    "water_reminder" -> WaterReminder
    "pregnancy_calculator" -> PregnancyCalculator
    "ideal_weight_calculator" -> IdealWeightCalculator
    "sleep_cycle_calculator" -> SleepCycleCalculator
    "spin_wheel" -> SpinWheel
    "random_name_picker" -> RandomNamePicker
    "passphrase_generator" -> PassphraseGenerator
    "water_intake_calc" -> WaterIntakeCalc
    "body_fat_calc" -> BodyFatCalc
    "target_heart_rate" -> TargetHeartRate
    "macro_calc" -> MacroCalc
    "pace_calc" -> PaceCalc
    "password_audit" -> PasswordAudit
    "aes_encryption" -> AesEncryption
    "checksum_verifier" -> ChecksumVerifier
    "steganography" -> Steganography
    "uuid_validator" -> UuidValidator
    "color_palette_gen" -> ColorPaletteGen
    "sound_exposure" -> SoundExposure
    "matrix_calc" -> MatrixCalc
    "binary_bitwise" -> BinaryBitwise
    "morse_trainer" -> MorseTrainer
    "resistor_color_calc" -> ResistorColorCalc
    "image-gen" -> EpicImageGen
    "chat" -> EpicChat
    "local" -> EpicLocal
    "agent-ingest" -> EpicAgentIngest
    "agent-generate" -> EpicAgentGenerate
    "agent-results" -> EpicAgentResults
    "agent-setup" -> EpicAgentSetup
    "notion-ingest" -> EpicNotionIngest
    "notion-folder" -> EpicNotionFolder
    "notion-scraper" -> EpicNotionScraper
    "notion-history" -> EpicNotionHistory
    "notion-setup" -> EpicNotionSetup
    "datediff" -> EpicDatediff
    "subnet" -> EpicSubnet
    "word-rank" -> EpicWordRank
    "json-csv" -> EpicJsonCsv
    "xml-json" -> EpicXmlJson
    "yaml" -> EpicYaml
    "base64" -> EpicBase64
    "timezone" -> EpicTimezone
    "batch" -> EpicBatch
    "viewer" -> EpicViewer
    "science" -> EpicScience
    "adv-data" -> EpicAdvData
    "reconcile" -> EpicReconcile
    "mock" -> EpicMock
    "synthetic" -> EpicSynthetic
    "pomodoro" -> EpicPomodoro
    "countdown" -> EpicCountdown
    "panchangam" -> EpicPanchangam
    "api-tester" -> EpicApiTester
    "sql" -> EpicSql
    "xml-fmt" -> EpicXmlFmt
    "json-ts" -> EpicJsonTs
    "kusto" -> EpicKusto
    "diff" -> EpicDiff
    "jwt" -> EpicJwt
    "url" -> EpicUrl
    "cron" -> EpicCron
    "minifier" -> EpicMinifier
    "qr-barcode" -> EpicQrBarcode
    "inspiration" -> EpicInspiration
    "md-table" -> EpicMdTable
    "sql-builder" -> EpicSqlBuilder
    "pdf" -> EpicPdf
    "docx-md" -> EpicDocxMd
    "doc-translator" -> EpicDocTranslator
    "text" -> EpicText
    "md-editor" -> EpicMdEditor
    "finance" -> EpicFinance
    "image" -> EpicImage
    "image-lab" -> EpicImageLab
    "fashion-guide" -> EpicFashionGuide
    "clothes-guide" -> EpicClothesGuide
    "traditional-guide" -> EpicTraditionalGuide
    "worldwide" -> EpicWorldwide
    "ping" -> EpicPing
    "dns" -> EpicDns
    "whois" -> EpicWhois
    "ip-info" -> EpicIpInfo
    "geo" -> EpicGeo
    "ssl" -> EpicSsl
    "anonymizer" -> EpicAnonymizer
    "security" -> EpicSecurity
    "rsa" -> EpicRsa
    "otp" -> EpicOtp
    "size-guide" -> EpicSizeGuide
    "shoes" -> EpicShoes
    "accessories" -> EpicAccessories
    "social" -> EpicSocial
    "social-downloader" -> EpicSocialDownloader
    "status" -> EpicStatus
    "telemetry" -> EpicTelemetry
    "lineage" -> EpicLineage
    "extension-sources" -> EpicExtensionSources
    "archive" -> EpicArchive
    "url2pdf" -> EpicUrl2pdf
    "userscripts" -> EpicUserscripts
    "bookmarklets" -> EpicBookmarklets
    "url2markdown" -> EpicUrl2markdown
    "bluetooth" -> EpicBluetooth
    "digital_clock_widget" -> DigitalClockWidgetConfig

    // Merged Epic Tool Fallbacks
    "age" -> DateCalculator
    "converter" -> UnitConverter
    "currency" -> CurrencyConverter
    "json-fmt" -> JsonFormatter
    "password" -> PasswordGenerator
    "regex" -> RegexTester
    "speed" -> NetworkSpeedTest
    "stopwatch" -> StopwatchTimer
    "timestamp" -> TimestampConverter
    "worldclock" -> WorldClock
    "color" -> ColorPicker

    "tsv_converter" -> TsvConverterDest
    "lorem_generator" -> LoremGeneratorDest
    "csv_cleaner" -> CsvCleanerDest
    "aspect_ratio_calc" -> AspectRatioCalcDest
    "morse_audio_synth" -> MorseAudioSynthDest
    "file_checksum_calc" -> FileChecksumDest
    "system_uptime" -> SystemUptimeDest
    "color_contrast_matrix" -> ColorContrastMatrixDest
    "base_converter_pro" -> BaseConverterProDest
    "wifi_channel_scanner" -> WifiChannelScannerDest
    "calorie_burn_calc" -> CalorieBurnCalcDest
    "quote_generator" -> QuoteGeneratorDest
    "url_builder" -> UrlBuilderDest
    "cron_explainer" -> CronExplainerDest
    "json_schema_validator" -> JsonSchemaValidatorDest
    "text_diff_viewer" -> TextDiffViewerDest
    else -> null
}
