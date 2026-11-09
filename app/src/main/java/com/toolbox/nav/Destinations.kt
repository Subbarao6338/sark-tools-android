package com.toolbox.nav

import kotlinx.serialization.Serializable

@Serializable object Dashboard

// Measurement & Sensors
@Serializable object Level
@Serializable object Compass
@Serializable object Protractor
@Serializable object SoundMeter
@Serializable object Ruler

// Conversion & Calculation
@Serializable object UnitConverter
@Serializable object PercentageCalculator
@Serializable object TipCalculator
@Serializable object NumberBase

// Lighting & Display
@Serializable object Flashlight

// Everyday Tools
@Serializable object QrScanner
@Serializable object Counter
@Serializable object StopwatchTimer
@Serializable object RandomGenerator
@Serializable object ColorPicker
@Serializable object Magnifier
@Serializable object Mirror

// Phase 6 Tools
@Serializable object Vibrometer
@Serializable object LightMeter
@Serializable object MetalDetector
@Serializable object Barometer
@Serializable object Humidity

// Phase 8 Tools
@Serializable object Pedometer
@Serializable object Gyroscope

// Phase 9 Tools
@Serializable object HeartRate
@Serializable object SpectrumAnalyzer

// Phase 10 Tools
@Serializable object Speedometer
@Serializable object Altitude

// Phase 11 Tools
@Serializable object BreathingExercise
@Serializable object ScientificCalculator
@Serializable object MorseCode

// Phase 11 Tools (Audio)
@Serializable object WhiteNoise
@Serializable object PitchTuner
@Serializable object Metronome

// Phase 13 Tools
@Serializable object ScreenFlash
@Serializable object PlumbBob

// Phase 14 Tools
@Serializable object WifiSignal
@Serializable object BluetoothFinder

// Phase 12 Tools
@Serializable object DateCalculator
@Serializable object BmiCalculator
@Serializable object AspectRatio
@Serializable object PasswordGenerator

// Batch 2 Tools
@Serializable object EmiCalculator
@Serializable object DeviceInfo
@Serializable object NetworkInfo
@Serializable object NfcToolkit
@Serializable object ToneGenerator
@Serializable object FormulaReference

// Batch 3 Tools
@Serializable object WifiQrShare
@Serializable object PhotoCleanup

// Phase 3 Backlog Tools
@Serializable object WireGauge
@Serializable object PaintCalculator
@Serializable object ScrewBolt
@Serializable object ScreenGrid
@Serializable object TtsReader
@Serializable object VibrationPatterns
@Serializable object Ocr
@Serializable object UnitCircle

// Batch 4 Tools
@Serializable object QrGenerator
@Serializable object Notepad
@Serializable object FocusTimer

// Batch 5 Tools
@Serializable object TextUtils
@Serializable object UnitPrice
@Serializable object BillSplit
@Serializable object HashEncode
@Serializable object WorldClock
@Serializable object SleepTimer
@Serializable object HabitTracker
@Serializable object EyeTest
@Serializable object VoiceRecorder
@Serializable object PdfToolkit

// Batch 6 Tools
@Serializable object ScreenshotStitcher
@Serializable object DocumentScanner
@Serializable object ScreenRecorder
@Serializable object StatusSaver

// Batch 7 Tools
@Serializable object DuplicatePhotos
@Serializable object StorageAnalyzer

// New Roadmap & Category Tools
@Serializable object ImageEditor
@Serializable object AudioEditor
@Serializable object VideoEditor
@Serializable object PdfEditor
@Serializable object WordEditor
@Serializable object DocumentCreation
@Serializable object WebToolsNav
@Serializable object DeadPixelTester
@Serializable object TouchTester
@Serializable object ScreenDimmer
@Serializable object CallLogsAnalytics
@Serializable object MessagesAnalytics

@Serializable object EducationHub
@Serializable object PeriodicTableNav
@Serializable object ChemistryGamesNav
@Serializable object ScienceLabsNav
@Serializable object BrainGamesNav
@Serializable object Pokedex

// Newly Added Tools
@Serializable object BatteryInfo
@Serializable object CurrencyConverter
@Serializable object NetworkSpeedTest
@Serializable object FuelCalculator
@Serializable object LuxGuide

// All-in-One Calculators
@Serializable object DiscountCalculator
@Serializable object GstCalculator
@Serializable object FinancialCalculators
@Serializable object MathUtilities

// New Extended Tools
@Serializable object BmrCalculator
@Serializable object ContrastChecker
@Serializable object RecipeScaler
@Serializable object IntervalTimer

// Unit Converters Category Tools
@Serializable object LengthConverter
@Serializable object WeightConverter
@Serializable object TemperatureConverter
@Serializable object StorageConverter
@Serializable object VolumeConverter
@Serializable object SpeedConverter
@Serializable object AreaConverter
@Serializable object TimeConverter
@Serializable object PressureConverter
@Serializable object EnergyConverter
@Serializable object PowerConverter
@Serializable object ForceConverter
@Serializable object TorqueConverter
@Serializable object DensityConverter
@Serializable object FuelEconomyConverter
@Serializable object AngleConverter
@Serializable object FrequencyConverter

// New Date & Time Destinations
@Serializable object TimeDiff
@Serializable object MoonPhase

// New Social Destinations
@Serializable object HashtagGenerator
@Serializable object SocialBio
@Serializable object SocialPreview
@Serializable object QuoteMaker

// Management & Creation Destinations
@Serializable object WidgetsManager
@Serializable object QuickTilesManager
@Serializable object ShortcutsManager

// All-in-One Suite & Perchance AI Additions
@Serializable object EquationSolver
@Serializable object GeometryCalculator
@Serializable object InvestmentCalculator
@Serializable object GpaCalculator
@Serializable object PerchanceAi

// AI Tools Category Additions
@Serializable object PerchanceTextGen
@Serializable object PerchancePhotoGen
@Serializable object PerchanceChat
@Serializable object PerchanceVideoGen
@Serializable object PerchanceStoryGen
@Serializable object PerchanceCharacterGen
@Serializable object PerchanceAnimeGen
@Serializable object PerchanceRpgGen
@Serializable object PerchanceWritingPrompt
@Serializable object PerchanceLoreGen
@Serializable object PerchanceDialogueGen
@Serializable object PerchanceVoiceActor
@Serializable object PerchanceArtPlugin
@Serializable object PerchanceNameGen
@Serializable object HuggingFaceAi

// MultiTool Clean-Room Spec Suite Destinations
@Serializable object BasicCalculator
@Serializable object FractionCalculator
@Serializable object RatioCalculator
@Serializable object SortDedupeLines
@Serializable object FindReplace
@Serializable object TextDiff
@Serializable object LoremIpsum
@Serializable object NumberToWords
@Serializable object RomanNumerals
@Serializable object FancyText
@Serializable object EmojiPicker
@Serializable object ImageResizer
@Serializable object ImageFormatConverter
@Serializable object PdfToImages
@Serializable object CompressPdf
@Serializable object BarcodeGenerator
@Serializable object ScanHistory
@Serializable object JsonFormatter
@Serializable object UuidGenerator
@Serializable object ColorConverter
@Serializable object HtmlEntityTool
@Serializable object TimestampConverter
@Serializable object RegexTester
@Serializable object WorkingDaysCalculator
@Serializable object WaterReminder
@Serializable object PregnancyCalculator
@Serializable object IdealWeightCalculator
@Serializable object SleepCycleCalculator
@Serializable object SpinWheel
@Serializable object RandomNamePicker
@Serializable object PassphraseGenerator

@Serializable object WaterIntakeCalc
@Serializable object BodyFatCalc
@Serializable object TargetHeartRate
@Serializable object MacroCalc
@Serializable object PaceCalc
@Serializable object PasswordAudit
@Serializable object AesEncryption
@Serializable object ChecksumVerifier
@Serializable object Steganography
@Serializable object UuidValidator
@Serializable object ColorPaletteGen
@Serializable object SoundExposure
@Serializable object MatrixCalc
@Serializable object BinaryBitwise
@Serializable object MorseTrainer
@Serializable object ResistorColorCalc

// Epic Tools & Digital Clock Destinations
@Serializable object EpicImageGen
@Serializable object EpicChat
@Serializable object EpicLocal
@Serializable object EpicAgentIngest
@Serializable object EpicAgentGenerate
@Serializable object EpicAgentResults
@Serializable object EpicAgentSetup
@Serializable object EpicNotionIngest
@Serializable object EpicNotionFolder
@Serializable object EpicNotionScraper
@Serializable object EpicNotionHistory
@Serializable object EpicNotionSetup
@Serializable object EpicDatediff
@Serializable object EpicSubnet
@Serializable object EpicWordRank
@Serializable object EpicJsonCsv
@Serializable object EpicXmlJson
@Serializable object EpicYaml
@Serializable object EpicBase64
@Serializable object EpicTimezone
@Serializable object EpicBatch
@Serializable object EpicViewer
@Serializable object EpicScience
@Serializable object EpicAdvData
@Serializable object EpicReconcile
@Serializable object EpicMock
@Serializable object EpicSynthetic
@Serializable object EpicPomodoro
@Serializable object EpicCountdown
@Serializable object EpicPanchangam
@Serializable object EpicApiTester
@Serializable object EpicSql
@Serializable object EpicXmlFmt
@Serializable object EpicJsonTs
@Serializable object EpicKusto
@Serializable object EpicDiff
@Serializable object EpicJwt
@Serializable object EpicUrl
@Serializable object EpicCron
@Serializable object EpicMinifier
@Serializable object EpicQrBarcode
@Serializable object EpicInspiration
@Serializable object EpicMdTable
@Serializable object EpicSqlBuilder
@Serializable object EpicPdf
@Serializable object EpicDocxMd
@Serializable object EpicDocTranslator
@Serializable object EpicText
@Serializable object EpicMdEditor
@Serializable object EpicFinance
@Serializable object EpicImage
@Serializable object EpicImageLab
@Serializable object EpicFashionGuide
@Serializable object EpicClothesGuide
@Serializable object EpicTraditionalGuide
@Serializable object EpicWorldwide
@Serializable object EpicPing
@Serializable object EpicDns
@Serializable object EpicWhois
@Serializable object EpicIpInfo
@Serializable object EpicGeo
@Serializable object EpicSsl
@Serializable object EpicAnonymizer
@Serializable object EpicSecurity
@Serializable object EpicRsa
@Serializable object EpicOtp
@Serializable object EpicSizeGuide
@Serializable object EpicShoes
@Serializable object EpicAccessories
@Serializable object EpicSocial
@Serializable object EpicSocialDownloader
@Serializable object EpicStatus
@Serializable object EpicTelemetry
@Serializable object EpicLineage
@Serializable object EpicExtensionSources
@Serializable object EpicArchive
@Serializable object EpicUrl2pdf
@Serializable object EpicUserscripts
@Serializable object EpicBookmarklets
@Serializable object EpicUrl2markdown
@Serializable object EpicBluetooth
@Serializable object DigitalClockWidgetConfig
