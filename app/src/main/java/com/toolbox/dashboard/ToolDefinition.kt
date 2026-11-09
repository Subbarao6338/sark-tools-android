package com.toolbox.dashboard

import android.hardware.Sensor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.ExposurePlus1
import androidx.compose.material.icons.filled.Flare
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.automirrored.filled.BluetoothSearching
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.ScreenSearchDesktop
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Brightness2
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.BatteryStd
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Hub
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class ToolCategory(
    val label: String,
    val tileColor: Color,
    val iconTint: Color,
) {
    UnitConverters(
        label = "Unit Converters",
        tileColor = Color(0xFFE8F5E9),
        iconTint = Color(0xFF2E7D32),
    ),
    Converters(
        label = "Converters",
        tileColor = Color(0xFFE5E0F6),
        iconTint = Color(0xFF6B5BAE),
    ),
    Calculators(
        label = "Calculators",
        tileColor = Color(0xFFDCEEFD),
        iconTint = Color(0xFF1976D2),
    ),
    ImageTools(
        label = "Image tools",
        tileColor = Color(0xFFFCE4EC),
        iconTint = Color(0xFFD81B60),
    ),
    AudioTools(
        label = "Audio tools",
        tileColor = Color(0xFFE8EAF6),
        iconTint = Color(0xFF3949AB),
    ),
    DocTools(
        label = "Doc tools",
        tileColor = Color(0xFFE0F2F1),
        iconTint = Color(0xFF00897B),
    ),
    VideoTools(
        label = "Video tools",
        tileColor = Color(0xFFFFEBEE),
        iconTint = Color(0xFFE53935),
    ),
    Utilities(
        label = "Utilities",
        tileColor = Color(0xFFFFF3D0),
        iconTint = Color(0xFFA67C00),
    ),
    EverydayTools(
        label = "Everyday tools",
        tileColor = Color(0xFFD4F5E0),
        iconTint = Color(0xFF2E8B57),
    ),
    DateTime(
        label = "Date & Time tools",
        tileColor = Color(0xFFE1F5FE),
        iconTint = Color(0xFF0288D1),
    ),
    Social(
        label = "Social tools",
        tileColor = Color(0xFFFCE4EC),
        iconTint = Color(0xFFE91E63),
    ),
    Sensors(
        label = "Sensors",
        tileColor = Color(0xFFE0F7FA),
        iconTint = Color(0xFF0097A7),
    ),
    Device(
        label = "Device",
        tileColor = Color(0xFFF3E5F5),
        iconTint = Color(0xFF8E24AA),
    ),
    Education(
        label = "Education",
        tileColor = Color(0xFFEDE7F6),
        iconTint = Color(0xFF5E35B1),
    ),
    Games(
        label = "Games",
        tileColor = Color(0xFFFCE4EC),
        iconTint = Color(0xFFC2185B),
    ),
    AiTools(
        label = "AI tools",
        tileColor = Color(0xFFEDE7F6),
        iconTint = Color(0xFF7C4DFF),
    ),
    AppTools(
        label = "App tools",
        tileColor = Color(0xFFE8EAF6),
        iconTint = Color(0xFF3F51B5),
    ),
    DevTools(
        label = "Dev tools",
        tileColor = Color(0xFFE0F7FA),
        iconTint = Color(0xFF00838F),
    ),
    ProgrammingTools(
        label = "Programming tools",
        tileColor = Color(0xFFECEFF1),
        iconTint = Color(0xFF455A64),
    ),
    FilesTools(
        label = "Files tools",
        tileColor = Color(0xFFFFF8E1),
        iconTint = Color(0xFFFF8F00),
    ),
    NetworkTools(
        label = "Network tools",
        tileColor = Color(0xFFE1F5FE),
        iconTint = Color(0xFF0277BD),
    ),
    MediaConverters(
        label = "Media converters",
        tileColor = Color(0xFFF3E5F5),
        iconTint = Color(0xFF7B1FA2),
    ),
    Others(
        label = "Others",
        tileColor = Color(0xFFFBE9E7),
        iconTint = Color(0xFFD84315),
    ),
}

@Composable
fun ToolCategory.themeTileColor(matchWithTheme: Boolean): Color {
    if (!matchWithTheme) return this.tileColor
    return when (this) {
        ToolCategory.UnitConverters -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
        ToolCategory.Converters -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
        ToolCategory.Calculators -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
        ToolCategory.ImageTools -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.55f)
        ToolCategory.AudioTools -> MaterialTheme.colorScheme.surfaceContainerHighest
        ToolCategory.DocTools -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.40f)
        ToolCategory.VideoTools -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
        ToolCategory.Utilities -> MaterialTheme.colorScheme.surfaceContainerHigh
        ToolCategory.EverydayTools -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
        ToolCategory.DateTime -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.50f)
        ToolCategory.Social -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.50f)
        ToolCategory.Sensors -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.45f)
        ToolCategory.Device -> MaterialTheme.colorScheme.surfaceContainerHighest
        ToolCategory.Education -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
        ToolCategory.Games -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.50f)
        ToolCategory.AiTools -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.60f)
        ToolCategory.AppTools -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.40f)
        ToolCategory.DevTools -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.45f)
        ToolCategory.ProgrammingTools -> MaterialTheme.colorScheme.surfaceContainerHigh
        ToolCategory.FilesTools -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        ToolCategory.NetworkTools -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.50f)
        ToolCategory.MediaConverters -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.40f)
        ToolCategory.Others -> MaterialTheme.colorScheme.surfaceContainerHigh
    }
}

@Composable
fun ToolCategory.themeIconTint(matchWithTheme: Boolean): Color {
    if (!matchWithTheme) return this.iconTint
    return when (this) {
        ToolCategory.UnitConverters -> MaterialTheme.colorScheme.primary
        ToolCategory.Converters -> MaterialTheme.colorScheme.primary
        ToolCategory.Calculators -> MaterialTheme.colorScheme.secondary
        ToolCategory.ImageTools -> MaterialTheme.colorScheme.tertiary
        ToolCategory.AudioTools -> MaterialTheme.colorScheme.primary
        ToolCategory.DocTools -> MaterialTheme.colorScheme.secondary
        ToolCategory.VideoTools -> MaterialTheme.colorScheme.error
        ToolCategory.Utilities -> MaterialTheme.colorScheme.tertiary
        ToolCategory.EverydayTools -> MaterialTheme.colorScheme.primary
        ToolCategory.DateTime -> MaterialTheme.colorScheme.primary
        ToolCategory.Social -> MaterialTheme.colorScheme.tertiary
        ToolCategory.Sensors -> MaterialTheme.colorScheme.secondary
        ToolCategory.Device -> MaterialTheme.colorScheme.primary
        ToolCategory.Education -> MaterialTheme.colorScheme.primary
        ToolCategory.Games -> MaterialTheme.colorScheme.secondary
        ToolCategory.AiTools -> MaterialTheme.colorScheme.primary
        ToolCategory.AppTools -> MaterialTheme.colorScheme.primary
        ToolCategory.DevTools -> MaterialTheme.colorScheme.tertiary
        ToolCategory.ProgrammingTools -> MaterialTheme.colorScheme.secondary
        ToolCategory.FilesTools -> MaterialTheme.colorScheme.primary
        ToolCategory.NetworkTools -> MaterialTheme.colorScheme.tertiary
        ToolCategory.MediaConverters -> MaterialTheme.colorScheme.secondary
        ToolCategory.Others -> MaterialTheme.colorScheme.secondary
    }
}

data class ToolDefinition(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val category: ToolCategory,
    val description: String = "",
    val requiredSensorType: Int? = null,
    val requiresCamera: Boolean = false,
    val searchKeywords: List<String> = emptyList(),
)

val allTools = listOf(
    // UnitConverters
    ToolDefinition(
        id = "unit_converter",
        name = "Unit Converter",
        icon = Icons.Default.SyncAlt,
        category = ToolCategory.UnitConverters,
        description = "Convert between units across 17+ categories including length, weight, temperature, volume, speed, area, and more. Supports all common units with instant real-time conversion.",
        searchKeywords = listOf("convert", "length", "weight", "temperature", "volume"),
    ),
    ToolDefinition(
        id = "length_converter",
        name = "Length",
        icon = Icons.Default.Straighten,
        category = ToolCategory.UnitConverters,
        description = "High-precision length and distance converter across meters, kilometers, centimeters, millimeters, inches, feet, yards, and miles.",
        searchKeywords = listOf("length", "distance", "meter", "kilometer", "mile", "inch", "foot", "yard", "cm", "mm"),
    ),
    ToolDefinition(
        id = "weight_converter",
        name = "Weight & Mass",
        icon = Icons.Default.FitnessCenter,
        category = ToolCategory.UnitConverters,
        description = "Convert mass and weight between kilograms, grams, milligrams, pounds, ounces, stones, and metric tons.",
        searchKeywords = listOf("weight", "mass", "kg", "gram", "pound", "lbs", "ounce", "oz", "stone", "ton"),
    ),
    ToolDefinition(
        id = "temperature_converter",
        name = "Temperature",
        icon = Icons.Default.Flare,
        category = ToolCategory.UnitConverters,
        description = "Instant bidirectional conversion between Celsius, Fahrenheit, Kelvin, and Rankine with physical reference marks.",
        searchKeywords = listOf("temperature", "celsius", "fahrenheit", "kelvin", "rankine", "degrees", "heat", "weather"),
    ),
    ToolDefinition(
        id = "storage_converter",
        name = "Data Storage",
        icon = Icons.Default.Tag,
        category = ToolCategory.UnitConverters,
        description = "Data storage and memory units: Bytes, KB, MB, GB, TB, as well as binary standards KiB, MiB, GiB, TiB.",
        searchKeywords = listOf("storage", "data", "byte", "kb", "mb", "gb", "tb", "gigabyte", "megabyte", "binary"),
    ),
    ToolDefinition(
        id = "volume_converter",
        name = "Volume",
        icon = Icons.Default.LocalDrink,
        category = ToolCategory.UnitConverters,
        description = "Convert volume across liters, milliliters, gallons, quarts, cups, fluid ounces, tablespoons, teaspoons, and cubic meters.",
        searchKeywords = listOf("volume", "liquid", "liter", "gallon", "cup", "fluid", "ounce", "ml", "m3"),
    ),
    ToolDefinition(
        id = "speed_converter",
        name = "Speed",
        icon = Icons.Default.Speed,
        category = ToolCategory.UnitConverters,
        description = "Convert velocity between m/s, km/h, mph, knots, and ft/s with instant precision calculations.",
        searchKeywords = listOf("speed", "velocity", "kmh", "mph", "knots", "pace", "fast"),
    ),
    ToolDefinition(
        id = "area_converter",
        name = "Area",
        icon = Icons.Default.SquareFoot,
        category = ToolCategory.UnitConverters,
        description = "Convert land and surface area between square meters, square feet, acres, hectares, square kilometers, and square miles.",
        searchKeywords = listOf("area", "square", "acre", "hectare", "sqft", "sqm", "land", "surface"),
    ),
    ToolDefinition(
        id = "time_converter",
        name = "Time",
        icon = Icons.Default.Schedule,
        category = ToolCategory.UnitConverters,
        description = "Convert time intervals between milliseconds, seconds, minutes, hours, days, weeks, months, and years.",
        searchKeywords = listOf("time", "second", "minute", "hour", "day", "week", "month", "year", "duration"),
    ),
    ToolDefinition(
        id = "pressure_converter",
        name = "Pressure",
        icon = Icons.Default.Compress,
        category = ToolCategory.UnitConverters,
        description = "Convert atmospheric and mechanical pressure between Pascal, kPa, Bar, PSI, Atmosphere (atm), and mmHg.",
        searchKeywords = listOf("pressure", "pascal", "bar", "psi", "atm", "kpa", "mmhg", "atmosphere"),
    ),
    ToolDefinition(
        id = "energy_converter",
        name = "Energy",
        icon = Icons.Default.Bolt,
        category = ToolCategory.UnitConverters,
        description = "Convert energy and work across Joules, Kilojoules, Calories, Kilocalories, Watt-hours, and kilowatt-hours (kWh).",
        searchKeywords = listOf("energy", "joule", "calorie", "kcal", "kwh", "work", "heat", "power"),
    ),
    ToolDefinition(
        id = "power_converter",
        name = "Power",
        icon = Icons.Default.Power,
        category = ToolCategory.UnitConverters,
        description = "Convert mechanical and electrical power between Watts, Kilowatts, Megawatts, Horsepower (hp), and BTU/hr.",
        searchKeywords = listOf("power", "watt", "kilowatt", "horsepower", "hp", "btu", "electricity"),
    ),
    ToolDefinition(
        id = "force_converter",
        name = "Force",
        icon = Icons.Default.OpenWith,
        category = ToolCategory.UnitConverters,
        description = "Convert physical force between Newtons, Kilonewtons, Pound-force (lbf), Dynes, and Kilogram-force (kgf).",
        searchKeywords = listOf("force", "newton", "pound-force", "lbf", "dyne", "kgf", "physics"),
    ),
    ToolDefinition(
        id = "torque_converter",
        name = "Torque",
        icon = Icons.AutoMirrored.Filled.RotateRight,
        category = ToolCategory.UnitConverters,
        description = "Convert rotational force and torque between Newton-meters (N·m), Foot-pounds (ft·lb), Inch-pounds, and Kilogram-meters.",
        searchKeywords = listOf("torque", "rotational", "newton-meter", "foot-pound", "nm", "ft-lb", "moment"),
    ),
    ToolDefinition(
        id = "density_converter",
        name = "Density",
        icon = Icons.Default.Science,
        category = ToolCategory.UnitConverters,
        description = "Convert volumetric mass density across kg/m³, g/cm³, lb/ft³, and lb/gal.",
        searchKeywords = listOf("density", "mass", "volume", "kg/m3", "g/cm3", "science"),
    ),
    ToolDefinition(
        id = "fuel_economy_converter",
        name = "Fuel Economy",
        icon = Icons.Default.LocalGasStation,
        category = ToolCategory.UnitConverters,
        description = "Convert vehicle fuel consumption ratings between km/L, MPG (US), MPG (UK), and L/100km.",
        searchKeywords = listOf("fuel", "economy", "mileage", "mpg", "km/l", "consumption", "l/100km", "gas"),
    ),
    ToolDefinition(
        id = "angle_converter",
        name = "Angle",
        icon = Icons.Default.Architecture,
        category = ToolCategory.UnitConverters,
        description = "Convert planar angles between Degrees (°), Radians (rad), Gradians (grad), Arcminutes, and Arcseconds.",
        searchKeywords = listOf("angle", "degree", "radian", "gradian", "arcminute", "arcsecond", "geometry"),
    ),
    ToolDefinition(
        id = "frequency_converter",
        name = "Frequency",
        icon = Icons.Default.GraphicEq,
        category = ToolCategory.UnitConverters,
        description = "Convert cyclic frequencies between Hertz (Hz), Kilohertz (kHz), Megahertz (MHz), Gigahertz (GHz), and RPM.",
        searchKeywords = listOf("frequency", "hertz", "hz", "khz", "mhz", "ghz", "rpm", "cycles"),
    ),
    // Converters
    ToolDefinition(
        id = "morse_code",
        name = "Morse Code",
        icon = Icons.Default.GraphicEq,
        category = ToolCategory.Converters,
        description = "Translate text to Morse code and back. Play Morse output as sound, haptic vibration, or flashlight blinks. Adjustable speed from 5 to 25 WPM.",
        searchKeywords = listOf("morse", "code", "dots", "dashes", "sos", "translate", "signal"),
    ),
    ToolDefinition(
        id = "wire_gauge",
        name = "Wire Gauge",
        icon = Icons.Default.ElectricalServices,
        category = ToolCategory.Converters,
        description = "AWG wire gauge reference table with metric (mm², diameter) equivalents and approximate ampacity for chassis and power-transmission use.",
        searchKeywords = listOf("wire", "gauge", "awg", "ampacity", "amp", "current", "electrical", "cable", "mm2"),
    ),
    ToolDefinition(
        id = "screw_bolt",
        name = "Screw & Bolt",
        icon = Icons.Default.Hardware,
        category = ToolCategory.Converters,
        description = "Reference table of metric (M3-M20) and imperial (UNC) fasteners with thread pitch, tap drill, clearance hole, and hex wrench size.",
        searchKeywords = listOf("screw", "bolt", "thread", "metric", "imperial", "tap", "drill", "hex", "wrench", "fastener", "unc"),
    ),
    ToolDefinition(
        id = "currency_converter",
        name = "Currency",
        icon = Icons.Default.CurrencyExchange,
        category = ToolCategory.Converters,
        description = "Convert between world currencies using benchmark offline exchange rates with instant calculations.",
        searchKeywords = listOf("currency", "exchange", "money", "usd", "inr", "eur", "gbp", "jpy", "rate"),
    ),
    ToolDefinition(
        id = "number_to_words",
        name = "Number to Words",
        icon = Icons.Default.Numbers,
        category = ToolCategory.Converters,
        description = "Convert numbers into words in English and Hindi transliteration with Indian numbering (Lakhs/Crores) or Western format.",
        searchKeywords = listOf("words", "number to words", "cheque", "spelling", "currency", "lakh", "crore"),
    ),
    ToolDefinition(
        id = "roman_numerals",
        name = "Roman Numerals",
        icon = Icons.Default.Pin,
        category = ToolCategory.Converters,
        description = "Bidirectional converter between Arabic numbers (1-3999) and Roman numerals (I, V, X, L, C, D, M) with input validation.",
        searchKeywords = listOf("roman", "numeral", "arabic", "convert", "latin", "numbers"),
    ),
    ToolDefinition(
        id = "color_converter",
        name = "Color Converter",
        icon = Icons.Default.Palette,
        category = ToolCategory.Converters,
        description = "Convert color codes between HEX, RGB, HSL, HSV, and CMYK in real time with interactive RGBA color sliders.",
        searchKeywords = listOf("color", "hex", "rgb", "hsl", "hsv", "cmyk", "converter", "palette"),
    ),
    // Calculators
    ToolDefinition(
        id = "percentage",
        name = "Percentage",
        icon = Icons.Default.Percent,
        category = ToolCategory.Calculators,
        description = "Quickly calculate percentages, discounts, markups, and percentage changes. Enter any two values to find the third. Perfect for shopping discounts or calculating tips.",
        searchKeywords = listOf("percent", "discount", "markup", "ratio"),
    ),
    ToolDefinition(
        id = "tip_calculator",
        name = "Tip Calculator",
        icon = Icons.Default.Payments,
        category = ToolCategory.Calculators,
        description = "Calculate tips and split bills easily. Enter the bill amount, choose a tip percentage, and split among any number of people. Shows per-person totals with tip included.",
        searchKeywords = listOf("tip", "bill", "split", "restaurant"),
    ),
    ToolDefinition(
        id = "scientific_calculator",
        name = "Calculator",
        icon = Icons.Default.Calculate,
        category = ToolCategory.Calculators,
        description = "Full scientific calculator with trigonometric functions, logarithms, exponents, factorial, and more. Supports DEG/RAD modes and expression-based input.",
        searchKeywords = listOf("calculator", "scientific", "math", "sin", "cos", "tan", "log", "sqrt"),
    ),
    ToolDefinition(
        id = "bmi_calculator",
        name = "BMI",
        icon = Icons.Default.FitnessCenter,
        category = ToolCategory.Calculators,
        description = "Calculate Body Mass Index from height and weight. Supports metric and imperial units with health category display and healthy weight range.",
        searchKeywords = listOf("bmi", "body", "mass", "index", "weight", "health", "obesity"),
    ),
    ToolDefinition(
        id = "aspect_ratio",
        name = "Aspect Ratio",
        icon = Icons.Default.AspectRatio,
        category = ToolCategory.Calculators,
        description = "Calculate and simplify aspect ratios. Resize dimensions proportionally with ratio lock. Includes common presets like 16:9, 4:3, and A4.",
        searchKeywords = listOf("aspect", "ratio", "resize", "dimension", "proportion", "16:9", "4:3"),
    ),
    ToolDefinition(
        id = "emi_calculator",
        name = "Loan/EMI",
        icon = Icons.Default.AccountBalance,
        category = ToolCategory.Calculators,
        description = "Calculate monthly EMI, total interest, and total payment for loans. View principal vs. interest breakdown with pie chart and full amortization schedule. Supports reducing balance and flat-rate methods.",
        searchKeywords = listOf("loan", "emi", "mortgage", "interest", "amortization", "calculator", "principal"),
    ),
    ToolDefinition(
        id = "paint_calculator",
        name = "Paint Calculator",
        icon = Icons.Default.Brush,
        category = ToolCategory.Calculators,
        description = "Estimate paint needed for a room. Enter dimensions, doors, windows, and number of coats — get liters or gallons required.",
        searchKeywords = listOf("paint", "wall", "coverage", "room", "gallon", "liter", "renovation"),
    ),
    ToolDefinition(
        id = "unit_price",
        name = "Unit Price",
        icon = Icons.Default.ShoppingCart,
        category = ToolCategory.Calculators,
        description = "Compare two products by price per unit to find the better value while shopping.",
        searchKeywords = listOf("unit price", "compare", "cheaper", "per unit", "shopping", "grocery", "value"),
    ),
    ToolDefinition(
        id = "bill_split",
        name = "Bill Split",
        icon = Icons.Default.Groups,
        category = ToolCategory.Calculators,
        description = "Split a bill across people with an optional tip, and see the per-person amount.",
        searchKeywords = listOf("bill", "split", "tip", "restaurant", "share", "per person", "divide"),
    ),
    ToolDefinition(
        id = "fuel_calculator",
        name = "Fuel Economy",
        icon = Icons.Default.LocalGasStation,
        category = ToolCategory.Calculators,
        description = "Calculate road trip fuel requirements, fuel economy, total journey cost, and cost per km/mile.",
        searchKeywords = listOf("fuel", "gas", "mileage", "trip", "cost", "mpg", "economy", "petrol", "diesel"),
    ),
    ToolDefinition(
        id = "discount_calculator",
        name = "Discount",
        icon = Icons.Default.LocalOffer,
        category = ToolCategory.Calculators,
        description = "Calculate discounted final prices, multiple successive discounts, post-discount tax, and compare two shopping deals.",
        searchKeywords = listOf("discount", "sale", "shopping", "deal", "savings", "offer", "bargain", "off"),
    ),
    ToolDefinition(
        id = "gst_calculator",
        name = "GST / Tax",
        icon = Icons.AutoMirrored.Filled.ReceiptLong,
        category = ToolCategory.Calculators,
        description = "Compute GST inclusive and exclusive amounts with standard 0%, 5%, 12%, 18%, 28% and custom rates plus CGST/SGST/IGST breakdown.",
        searchKeywords = listOf("gst", "tax", "vat", "invoice", "cgst", "sgst", "igst", "sales tax"),
    ),
    ToolDefinition(
        id = "financial_calculators",
        name = "Finance",
        icon = Icons.AutoMirrored.Filled.TrendingUp,
        category = ToolCategory.Calculators,
        description = "Suite of financial calculators: Simple Interest, Compound Interest, SIP investment, wealth accumulation, Profit & Loss, and net salary take-home.",
        searchKeywords = listOf("finance", "interest", "simple interest", "compound interest", "sip", "investment", "profit", "loss", "salary", "wealth"),
    ),
    ToolDefinition(
        id = "math_utilities",
        name = "Math Utilities",
        icon = Icons.Default.Numbers,
        category = ToolCategory.Calculators,
        description = "Mathematical tools: Prime number checking and factors, factorial (n!), LCM, GCD, average and descriptive statistics, and random number generator.",
        searchKeywords = listOf("math", "prime", "factorial", "lcm", "gcd", "average", "median", "mode", "statistics", "random", "hcf"),
    ),
    ToolDefinition(
        id = "bmr_calculator",
        name = "BMR & Calories",
        icon = Icons.Default.FitnessCenter,
        category = ToolCategory.Calculators,
        description = "Calculate Basal Metabolic Rate (BMR) and Total Daily Energy Expenditure (TDEE) with Mifflin-St Jeor formula and custom macro splits.",
        searchKeywords = listOf("bmr", "tdee", "calorie", "calories", "macro", "macros", "diet", "nutrition", "protein", "carbs", "fat"),
    ),
    ToolDefinition(
        id = "recipe_scaler",
        name = "Recipe Scaler",
        icon = Icons.Default.Restaurant,
        category = ToolCategory.Calculators,
        description = "Scale recipe ingredients up or down based on original and desired serving portions with instant conversions.",
        searchKeywords = listOf("recipe", "cook", "bake", "scale", "serving", "servings", "ingredients", "portion", "kitchen"),
    ),
    ToolDefinition(
        id = "equation_solver",
        name = "Equation Solver",
        icon = Icons.Default.Functions,
        category = ToolCategory.Calculators,
        description = "Solve linear equations, 2x2 linear systems with Cramer's Rule, quadratic equations (real & complex conjugate roots), and cubic equations with Cardano's method.",
        searchKeywords = listOf("equation", "solver", "algebra", "quadratic", "linear", "system", "cramer", "cubic", "roots", "math"),
    ),
    ToolDefinition(
        id = "geometry_calculator",
        name = "Geometry & Shapes",
        icon = Icons.Default.Architecture,
        category = ToolCategory.Calculators,
        description = "Right triangle (3-4-5 rule, Pythagoras), 2D shapes area & perimeter, 3D bodies volume & surface, and construction concrete & tile estimators.",
        searchKeywords = listOf("geometry", "shape", "triangle", "pythagoras", "circle", "cylinder", "sphere", "tile", "concrete", "construction"),
    ),
    ToolDefinition(
        id = "investment_calculator",
        name = "Investment & TVM",
        icon = Icons.Default.AccountBalanceWallet,
        category = ToolCategory.Calculators,
        description = "ROI & annualized CAGR, time value of money (TVM) compound growth, savings target goals, and fixed mortgage amortization.",
        searchKeywords = listOf("investment", "tvm", "compound", "interest", "roi", "cagr", "wealth", "mortgage", "savings", "goal", "finance"),
    ),
    ToolDefinition(
        id = "basic_calculator",
        name = "Basic Calculator",
        icon = Icons.Default.Calculate,
        category = ToolCategory.Calculators,
        description = "Everyday standard calculator with calculation history tape, memory registers (M+, M-, MR, MC), percentage, and backspace.",
        searchKeywords = listOf("calculator", "basic", "math", "add", "subtract", "multiply", "divide", "memory"),
    ),
    ToolDefinition(
        id = "fraction_calculator",
        name = "Fraction Calculator",
        icon = Icons.Default.Functions,
        category = ToolCategory.Calculators,
        description = "Fraction arithmetic (+, -, ×, ÷), mixed numbers, fraction simplification, and two-way decimal to fraction conversion.",
        searchKeywords = listOf("fraction", "numerator", "denominator", "simplify", "mixed", "math", "decimal"),
    ),
    ToolDefinition(
        id = "ratio_calculator",
        name = "Ratio & Proportion",
        icon = Icons.Default.AspectRatio,
        category = ToolCategory.Calculators,
        description = "Solve proportions (A:B = C:D), simplify ratios to lowest terms, and scale quantities across ratio parts.",
        searchKeywords = listOf("ratio", "proportion", "scale", "divide", "simplify", "math", "factor"),
    ),
    ToolDefinition(
        id = "ideal_weight_calculator",
        name = "Ideal Weight",
        icon = Icons.Default.FitnessCenter,
        category = ToolCategory.Calculators,
        description = "Calculate ideal body weight using clinical Devine, Robinson, Miller, and Hamwi equations alongside healthy BMI targets.",
        searchKeywords = listOf("ideal weight", "ibw", "devine", "robinson", "healthy weight", "bmi", "fitness"),
    ),
    // ImageTools
    ToolDefinition(
        id = "color_picker",
        name = "Color Picker",
        icon = Icons.Default.Palette,
        category = ToolCategory.ImageTools,
        description = "Pick colors from the real world using your camera. Point at any object to capture its color with HEX, RGB, and HSL values. Save colors to a palette for design reference.",
        requiresCamera = true,
        searchKeywords = listOf("color", "hex", "rgb", "eyedropper", "palette"),
    ),
    ToolDefinition(
        id = "photo_cleanup",
        name = "Photo Cleanup",
        icon = Icons.Default.Photo,
        category = ToolCategory.ImageTools,
        description = "Compress photos to save space, or strip EXIF metadata (location, device, timestamps) before sharing. Originals are never touched — results save to Pictures/Toolbox.",
        searchKeywords = listOf("photo", "image", "compress", "resize", "exif", "metadata", "strip", "privacy", "shrink", "size"),
    ),
    ToolDefinition(
        id = "screenshot_stitcher",
        name = "Screenshot Stitcher",
        icon = Icons.Default.Dashboard,
        category = ToolCategory.ImageTools,
        description = "Combine several screenshots into one long image, then save or share it.",
        searchKeywords = listOf("screenshot", "stitch", "combine", "long", "image", "merge"),
    ),
    ToolDefinition(
        id = "image_editor",
        name = "Image Editor",
        icon = Icons.Default.Image,
        category = ToolCategory.ImageTools,
        description = "Crop, rotate, adjust brightness and contrast, apply artistic filters, and draw sketches on your photos.",
        searchKeywords = listOf("image", "photo", "editor", "crop", "rotate", "filter", "draw", "brush"),
    ),
    // AudioTools
    ToolDefinition(
        id = "sound_meter",
        name = "Sound Meter",
        icon = Icons.Default.GraphicEq,
        category = ToolCategory.AudioTools,
        description = "Measure ambient noise levels in decibels (dB) using your phone's microphone. Shows real-time sound level, min/max readings, and a noise level gauge with reference labels.",
        searchKeywords = listOf("decibel", "db", "noise", "volume", "microphone"),
    ),
    ToolDefinition(
        id = "white_noise",
        name = "Ambient Sounds",
        icon = Icons.Default.Nightlight,
        category = ToolCategory.AudioTools,
        description = "Ambient sound generator with white, pink, and brown noise plus nature sounds. Features sleep timer for focus, sleep, and relaxation.",
        searchKeywords = listOf("noise", "white", "pink", "brown", "rain", "ocean", "sleep", "ambient", "focus", "fan"),
    ),
    ToolDefinition(
        id = "metronome",
        name = "Metronome",
        icon = Icons.Default.MusicNote,
        category = ToolCategory.AudioTools,
        description = "Adjustable-tempo metronome with audible click, visual beat indicator, and haptic pulse. Supports tap tempo and time signatures from 2/4 to 6/8.",
        searchKeywords = listOf("metronome", "bpm", "tempo", "beat", "rhythm", "music", "click"),
    ),
    ToolDefinition(
        id = "pitch_tuner",
        name = "Pitch Tuner",
        icon = Icons.Default.Tune,
        category = ToolCategory.AudioTools,
        description = "Detect musical pitch from microphone input. Shows nearest note, frequency, and cents deviation with a visual tuning gauge. Includes reference tone generator.",
        searchKeywords = listOf("pitch", "tuner", "note", "music", "frequency", "tune", "guitar"),
    ),
    ToolDefinition(
        id = "spectrum_analyzer",
        name = "Spectrum",
        icon = Icons.Default.Equalizer,
        category = ToolCategory.AudioTools,
        description = "Visualize audio frequencies in real time using FFT analysis. Shows frequency bands from sub-bass to brilliance with bar and waterfall (spectrogram) views. Useful for music, acoustics, and audio troubleshooting.",
        searchKeywords = listOf("spectrum", "fft", "frequency", "audio", "analyzer", "equalizer"),
    ),
    ToolDefinition(
        id = "tone_generator",
        name = "Tone Generator",
        icon = Icons.Default.GraphicEq,
        category = ToolCategory.AudioTools,
        description = "Generate continuous tones from 20 Hz to 20 kHz with sine, square, triangle, and sawtooth waveforms. Includes presets for standard tuning (440 Hz), musical notes, and high frequency. Visual waveform preview.",
        searchKeywords = listOf("tone", "frequency", "generator", "sine", "wave", "hertz", "hz", "audio", "sound"),
    ),
    ToolDefinition(
        id = "voice_recorder",
        name = "Voice Recorder",
        icon = Icons.Default.Mic,
        category = ToolCategory.AudioTools,
        description = "Record voice memos, play them back, share, and delete. Recordings stay on your device.",
        searchKeywords = listOf("voice", "recorder", "record", "audio", "memo", "dictaphone", "sound"),
    ),
    ToolDefinition(
        id = "audio_editor",
        name = "Audio Editor",
        icon = Icons.Default.Audiotrack,
        category = ToolCategory.AudioTools,
        description = "Trim audio tracks, visualize waveforms, adjust volume gain, and export audio clips.",
        searchKeywords = listOf("audio", "sound", "editor", "trim", "cut", "waveform", "volume", "mp3"),
    ),
    // DocTools
    ToolDefinition(
        id = "ocr",
        name = "Text Scanner",
        icon = Icons.Default.TextFields,
        category = ToolCategory.DocTools,
        description = "Extract text from any photo using on-device ML Kit recognition. Pick a photo of a sign, receipt, business card, or document — copy or share the recognized text. No image leaves your device.",
        searchKeywords = listOf("ocr", "text", "scanner", "recognize", "extract", "card", "receipt", "ml", "kit", "mlkit"),
    ),
    ToolDefinition(
        id = "notepad",
        name = "Notepad",
        icon = Icons.Default.EditNote,
        category = ToolCategory.DocTools,
        description = "A quick local scratchpad for notes. Autosaves as you type, works offline, and never leaves your device.",
        searchKeywords = listOf("notepad", "notes", "note", "memo", "scratchpad", "text", "write"),
    ),
    ToolDefinition(
        id = "pdf_toolkit",
        name = "PDF Toolkit",
        icon = Icons.Default.PictureAsPdf,
        category = ToolCategory.DocTools,
        description = "Merge, rotate, extract pages, and add your signature to PDF files. Runs fully offline on your device.",
        searchKeywords = listOf("pdf", "merge", "combine", "rotate", "split", "extract", "sign", "signature", "document"),
    ),
    ToolDefinition(
        id = "document_scanner",
        name = "Document Scanner",
        icon = Icons.Default.DocumentScanner,
        category = ToolCategory.DocTools,
        description = "Capture or pick photos of documents and combine them into a single PDF. Fully offline.",
        searchKeywords = listOf("scan", "scanner", "document", "pdf", "camera", "photo to pdf"),
    ),
    ToolDefinition(
        id = "pdf_editor",
        name = "PDF Editor",
        icon = Icons.Default.PictureAsPdf,
        category = ToolCategory.DocTools,
        description = "Reorder pages, delete pages, add custom watermarks and stamps, and save modified PDF documents.",
        searchKeywords = listOf("pdf", "editor", "watermark", "stamp", "reorder", "delete page", "document"),
    ),
    ToolDefinition(
        id = "word_editor",
        name = "Word Editor",
        icon = Icons.Default.Description,
        category = ToolCategory.DocTools,
        description = "Format text documents with headings, bold, italic, and bullet lists. Export to formatted PDF or plain text.",
        searchKeywords = listOf("word", "doc", "editor", "text", "write", "heading", "bold", "export", "pdf"),
    ),
    ToolDefinition(
        id = "document_creation",
        name = "Doc Creator",
        icon = Icons.Default.PostAdd,
        category = ToolCategory.DocTools,
        description = "Generate invoices, resumes, meeting minutes, and simple agreements with customizable fields and instant PDF export.",
        searchKeywords = listOf("document", "create", "invoice", "resume", "cv", "agreement", "template", "generator"),
    ),
    ToolDefinition(
        id = "pdf_to_images",
        name = "PDF to Images",
        icon = Icons.Default.PictureAsPdf,
        category = ToolCategory.DocTools,
        description = "Render and extract individual pages of any PDF document into high-resolution PNG images completely offline.",
        searchKeywords = listOf("pdf", "images", "extract", "pages", "pdf to image", "document", "render"),
    ),
    ToolDefinition(
        id = "compress_pdf",
        name = "Compress PDF",
        icon = Icons.Default.Compress,
        category = ToolCategory.DocTools,
        description = "Reduce PDF file sizes by rendering and compressing document pages on-device with custom quality control.",
        searchKeywords = listOf("compress", "pdf", "shrink", "reduce size", "document", "smaller"),
    ),
    // VideoTools
    ToolDefinition(
        id = "screen_recorder",
        name = "Screen Recorder",
        icon = Icons.Default.Videocam,
        category = ToolCategory.VideoTools,
        description = "Record your screen to a video file, then play back or share it. Keeps recording while you use other apps.",
        searchKeywords = listOf("screen", "recorder", "record", "video", "capture", "screencast"),
    ),
    ToolDefinition(
        id = "video_editor",
        name = "Video Editor",
        icon = Icons.Default.Movie,
        category = ToolCategory.VideoTools,
        description = "Trim video duration, preview video clips, toggle audio mute, and export trimmed video files.",
        searchKeywords = listOf("video", "editor", "trim", "cut", "movie", "mute", "clip", "mp4"),
    ),
    // Utilities
    ToolDefinition(
        id = "qr_scanner",
        name = "QR & Barcode",
        icon = Icons.Default.QrCodeScanner,
        category = ToolCategory.Utilities,
        description = "Scan QR codes and barcodes (EAN, UPC, Code 128, etc.) using your camera. Instantly opens links, copies text, or shows encoded data.",
        requiresCamera = true,
        searchKeywords = listOf("qr", "barcode", "scan", "generate", "code"),
    ),
    ToolDefinition(
        id = "qr_generator",
        name = "QR Generator",
        icon = Icons.Default.QrCode2,
        category = ToolCategory.Utilities,
        description = "Generate QR codes for text, links, contacts, email, phone, SMS, and locations. Live preview, share as an image, or save to your gallery. Fully offline.",
        searchKeywords = listOf("qr", "generate", "generator", "vcard", "contact", "url", "link", "code", "barcode"),
    ),
    ToolDefinition(
        id = "text_utilities",
        name = "Text Utilities",
        icon = Icons.Default.TextFields,
        category = ToolCategory.Utilities,
        description = "Transform and inspect text: change case, count words and characters, trim spaces, remove line breaks, slugify, and reverse.",
        searchKeywords = listOf("text", "case", "uppercase", "lowercase", "word count", "character count", "slug", "trim"),
    ),
    ToolDefinition(
        id = "contrast_checker",
        name = "Contrast Checker",
        icon = Icons.Default.Contrast,
        category = ToolCategory.Utilities,
        description = "Calculate WCAG 2.1 color contrast ratio with AA and AAA compliance ratings, luminance analysis, and interactive color sliders.",
        searchKeywords = listOf("contrast", "wcag", "accessibility", "color", "checker", "ratio", "aa", "aaa", "design"),
    ),
    ToolDefinition(
        id = "sort_dedupe_lines",
        name = "Sort & Dedupe Lines",
        icon = Icons.Default.TextFields,
        category = ToolCategory.Utilities,
        description = "Remove duplicate lines and sort lists alphabetically (A-Z, Z-A), numerically, by line length, or in reverse order.",
        searchKeywords = listOf("sort", "dedupe", "duplicate", "lines", "alphabetical", "list", "text"),
    ),
    ToolDefinition(
        id = "find_replace",
        name = "Find & Replace",
        icon = Icons.Default.Search,
        category = ToolCategory.Utilities,
        description = "Fast find and replace with regex support, case-sensitivity toggle, live match count preview, and instant replace all.",
        searchKeywords = listOf("find", "replace", "search", "regex", "text", "substitute"),
    ),
    ToolDefinition(
        id = "text_diff",
        name = "Text Diff",
        icon = Icons.Default.SyncAlt,
        category = ToolCategory.Utilities,
        description = "Side-by-side and line-by-line comparison of two text inputs highlighting additions, deletions, and modifications.",
        searchKeywords = listOf("diff", "compare", "text diff", "changes", "difference", "patch"),
    ),
    ToolDefinition(
        id = "lorem_ipsum",
        name = "Lorem Ipsum",
        icon = Icons.Default.Description,
        category = ToolCategory.Utilities,
        description = "Generate standard placeholder dummy text by words, sentences, or paragraphs for design, mockups, and layout testing.",
        searchKeywords = listOf("lorem", "ipsum", "dummy", "placeholder", "text", "generator"),
    ),
    ToolDefinition(
        id = "fancy_text",
        name = "Fancy Text",
        icon = Icons.Default.Draw,
        category = ToolCategory.Utilities,
        description = "Transform normal text into creative Unicode typography: bold, cursive script, fraktur gothic, monospace, circled, and upside-down.",
        searchKeywords = listOf("fancy", "unicode", "font", "style", "bold", "cursive", "script", "gothic"),
    ),
    ToolDefinition(
        id = "emoji_picker",
        name = "Emoji & Symbols",
        icon = Icons.Default.AutoAwesome,
        category = ToolCategory.Utilities,
        description = "Searchable emoji and Unicode symbol keyboard with categorized tabs (Smileys, Food, Animals, Math, Symbols) and 1-tap copy.",
        searchKeywords = listOf("emoji", "symbol", "picker", "emoticon", "math symbols", "copy"),
    ),
    ToolDefinition(
        id = "barcode_generator",
        name = "Barcode Generator",
        icon = Icons.Default.QrCode2,
        category = ToolCategory.Utilities,
        description = "Generate standard 1D and 2D barcodes: Code 128, EAN-13, UPC-A, Code 39, and QR Codes with instant gallery save.",
        searchKeywords = listOf("barcode", "code128", "ean13", "upca", "code39", "qr", "generator"),
    ),
    ToolDefinition(
        id = "scan_history",
        name = "Scan History",
        icon = Icons.Default.QrCodeScanner,
        category = ToolCategory.Utilities,
        description = "View, search, copy, and export historical QR and barcode scans saved directly on your device.",
        searchKeywords = listOf("scan", "history", "qr", "barcode", "logs", "recents"),
    ),
    // EverydayTools
    ToolDefinition(
        id = "flashlight",
        name = "Flashlight",
        icon = Icons.Default.FlashlightOn,
        category = ToolCategory.EverydayTools,
        description = "Turn your phone into a bright flashlight using the camera flash LED. Features adjustable brightness, SOS mode for emergencies, and strobe mode.",
        searchKeywords = listOf("torch", "light", "sos", "strobe"),
    ),
    ToolDefinition(
        id = "counter",
        name = "Counter",
        icon = Icons.Default.ExposurePlus1,
        category = ToolCategory.EverydayTools,
        description = "A simple tally counter for counting anything. Tap to increment, with undo support. Great for counting people, inventory items, repetitions, or anything else.",
        searchKeywords = listOf("count", "tally", "increment", "clicker"),
    ),
    ToolDefinition(
        id = "magnifier",
        name = "Magnifier",
        icon = Icons.Default.ZoomIn,
        category = ToolCategory.EverydayTools,
        description = "Use your camera as a magnifying glass to zoom in on small text, labels, or objects. Supports pinch-to-zoom and flashlight toggle for better visibility.",
        requiresCamera = true,
        searchKeywords = listOf("magnify", "zoom", "magnifying glass", "enlarge"),
    ),
    ToolDefinition(
        id = "mirror",
        name = "Mirror",
        icon = Icons.Default.Person,
        category = ToolCategory.EverydayTools,
        description = "Use the front camera as a mirror. Includes a true mirror mode that shows your un-flipped reflection, so you see yourself as others see you.",
        requiresCamera = true,
        searchKeywords = listOf("mirror", "selfie", "front camera", "reflection", "true mirror"),
    ),
    ToolDefinition(
        id = "breathing_exercise",
        name = "Breathing",
        icon = Icons.Default.Air,
        category = ToolCategory.EverydayTools,
        description = "Follow guided breathing patterns to relax and reduce stress. Choose from Box Breathing, 4-7-8, or Relaxing techniques with visual animation, sound cues, and haptic feedback.",
        searchKeywords = listOf("breathe", "breathing", "relax", "meditation", "calm", "stress", "wellness"),
    ),
    ToolDefinition(
        id = "password_generator",
        name = "Password",
        icon = Icons.Default.Lock,
        category = ToolCategory.EverydayTools,
        description = "Generate secure random passwords with configurable length and character sets. Features strength indicator, copy to clipboard, and recent history.",
        searchKeywords = listOf("password", "generate", "secure", "random", "key", "strong"),
    ),
    ToolDefinition(
        id = "screen_flash",
        name = "Screen Flash",
        icon = Icons.Default.FlashOn,
        category = ToolCategory.EverydayTools,
        description = "Full-screen color flash for concerts, emergencies, or visual signaling. Supports solid color, strobe mode, and SOS pattern with adjustable frequency.",
        searchKeywords = listOf("flash", "strobe", "screen", "color", "sos", "signal", "emergency"),
    ),
    ToolDefinition(
        id = "tts_reader",
        name = "TTS Reader",
        icon = Icons.Default.RecordVoiceOver,
        category = ToolCategory.EverydayTools,
        description = "Paste any text and have your phone read it aloud. Adjust speech rate and pitch. Uses the device's built-in text-to-speech engine — no network needed.",
        searchKeywords = listOf("tts", "text", "speech", "read", "voice", "speak", "accessibility", "narrate"),
    ),
    ToolDefinition(
        id = "habit_tracker",
        name = "Habit Tracker",
        icon = Icons.Default.CheckCircle,
        category = ToolCategory.EverydayTools,
        description = "Track daily habits and build streaks. Mark each habit done every day and watch your streak grow.",
        searchKeywords = listOf("habit", "tracker", "streak", "daily", "routine", "goal", "check"),
    ),
    ToolDefinition(
        id = "water_reminder",
        name = "Water Tracker",
        icon = Icons.Default.WaterDrop,
        category = ToolCategory.EverydayTools,
        description = "Daily hydration tracker with custom intake goals, quick-logging buttons, progress visualization, and daily reset.",
        searchKeywords = listOf("water", "hydration", "drink", "health", "tracker", "reminder", "cups"),
    ),
    ToolDefinition(
        id = "passphrase_generator",
        name = "Passphrase Generator",
        icon = Icons.Default.Lock,
        category = ToolCategory.EverydayTools,
        description = "Generate memorable, cryptographically strong multi-word Diceware passphrases with custom separators and capitalization.",
        searchKeywords = listOf("passphrase", "password", "diceware", "words", "security", "entropy"),
    ),
    // DateTime
    ToolDefinition(
        id = "date_calculator",
        name = "Date/Age",
        icon = Icons.Default.DateRange,
        category = ToolCategory.DateTime,
        description = "Calculate days between dates, exact age in years/months/days, and add or subtract days from any date. Includes countdown to next birthday.",
        searchKeywords = listOf("date", "age", "birthday", "difference", "days", "calendar"),
    ),
    ToolDefinition(
        id = "stopwatch_timer",
        name = "Stopwatch",
        icon = Icons.Default.Timer,
        category = ToolCategory.DateTime,
        description = "Stopwatch with lap timing and countdown timer in one tool. Start, stop, and record laps with precision. The timer supports custom durations with alarm notification.",
        searchKeywords = listOf("stopwatch", "timer", "countdown", "lap"),
    ),
    ToolDefinition(
        id = "focus_timer",
        name = "Focus Timer",
        icon = Icons.Default.Timelapse,
        category = ToolCategory.DateTime,
        description = "A Pomodoro focus timer with work and break cycles. Runs in the background so your session keeps counting with the screen off.",
        searchKeywords = listOf("focus", "pomodoro", "timer", "productivity", "study", "work", "break"),
    ),
    ToolDefinition(
        id = "world_clock",
        name = "World Clock",
        icon = Icons.Default.Public,
        category = ToolCategory.DateTime,
        description = "See the current time in cities around the world at a glance. Add and remove cities; everything stays on your device.",
        searchKeywords = listOf("world", "clock", "time", "timezone", "zone", "city", "utc", "gmt"),
    ),
    ToolDefinition(
        id = "sleep_timer",
        name = "Sleep Timer",
        icon = Icons.Default.Bedtime,
        category = ToolCategory.DateTime,
        description = "Play gentle ambient sound for a set time to fall asleep to; it stops automatically when the timer ends.",
        searchKeywords = listOf("sleep", "timer", "nap", "white noise", "ambient", "bedtime", "relax"),
    ),
    ToolDefinition(
        id = "interval_timer",
        name = "Interval Timer",
        icon = Icons.Default.HourglassBottom,
        category = ToolCategory.DateTime,
        description = "High-Intensity Interval Training (HIIT) & Tabata workout timer with customizable Warmup, Work, Rest intervals, and Set counters.",
        searchKeywords = listOf("hiit", "interval", "timer", "tabata", "workout", "sets", "rounds", "fitness", "training"),
    ),
    ToolDefinition(
        id = "time_diff",
        name = "Time Planner",
        icon = Icons.Default.Schedule,
        category = ToolCategory.DateTime,
        description = "Compare time zones across world cities, calculate precise time differences, and plan meetings with golden working-hour overlap indicators.",
        searchKeywords = listOf("time", "difference", "timezone", "meeting", "planner", "world", "overlap", "hours", "gmt", "utc"),
    ),
    ToolDefinition(
        id = "moon_phase",
        name = "Moon & Sun",
        icon = Icons.Default.Nightlight,
        category = ToolCategory.DateTime,
        description = "Precise lunar phase tracker, illumination percentage, moon age in days, next full and new moon dates, and local sunrise/sunset calculator.",
        searchKeywords = listOf("moon", "lunar", "phase", "sun", "sunrise", "sunset", "illumination", "astronomy", "full moon", "new moon"),
    ),
    ToolDefinition(
        id = "timestamp_converter",
        name = "Timestamp Converter",
        icon = Icons.Default.Schedule,
        category = ToolCategory.DateTime,
        description = "Convert Unix epoch timestamps (seconds and milliseconds) to formatted local and UTC date-times and parse date strings.",
        searchKeywords = listOf("timestamp", "epoch", "unix", "date", "time", "utc", "iso8601"),
    ),
    ToolDefinition(
        id = "working_days_calculator",
        name = "Working Days",
        icon = Icons.Default.DateRange,
        category = ToolCategory.DateTime,
        description = "Calculate business and working days between dates, excluding custom weekends (Sat/Sun) and public holidays.",
        searchKeywords = listOf("working days", "business days", "weekdays", "holidays", "date diff", "calendar"),
    ),
    ToolDefinition(
        id = "pregnancy_calculator",
        name = "Pregnancy Calculator",
        icon = Icons.Default.Favorite,
        category = ToolCategory.DateTime,
        description = "Estimate baby due date (EDD), current gestational age in weeks and days, and trimester breakdown based on LMP or conception.",
        searchKeywords = listOf("pregnancy", "due date", "gestational", "trimester", "baby", "edd", "lmp"),
    ),
    ToolDefinition(
        id = "sleep_cycle_calculator",
        name = "Sleep Cycle",
        icon = Icons.Default.Bedtime,
        category = ToolCategory.DateTime,
        description = "Calculate optimal wake-up times and bedtimes based on natural 90-minute sleep cycles to wake up energized.",
        searchKeywords = listOf("sleep", "cycle", "wake up", "bedtime", "nap", "rem", "health"),
    ),
    // Social
    ToolDefinition(
        id = "status_saver",
        name = "Status Saver",
        icon = Icons.Default.Download,
        category = ToolCategory.Social,
        description = "Save photos and videos from WhatsApp statuses you've viewed, before they disappear. Grant access to the statuses folder to begin.",
        searchKeywords = listOf("status", "saver", "whatsapp", "download", "save", "story", "social"),
    ),
    ToolDefinition(
        id = "messages_analytics",
        name = "SMS Analytics",
        icon = Icons.Default.Sms,
        category = ToolCategory.Social,
        description = "Visualize messaging volume, sent versus received ratio, and top SMS senders and conversation threads.",
        searchKeywords = listOf("sms", "messages", "analytics", "stats", "texts", "senders", "threads", "inbox"),
    ),
    ToolDefinition(
        id = "hashtag_generator",
        name = "Hashtags",
        icon = Icons.Default.Tag,
        category = ToolCategory.Social,
        description = "Generate trending and niche hashtags for Instagram, Twitter/X, LinkedIn, TikTok, and YouTube with tag counters and instant copy.",
        searchKeywords = listOf("hashtag", "tags", "social", "instagram", "twitter", "reach", "engagement", "creator", "trending"),
    ),
    ToolDefinition(
        id = "social_bio",
        name = "Bio Link Card",
        icon = Icons.Default.Person,
        category = ToolCategory.Social,
        description = "Design and build your customizable personal Bio Link landing card with social icons, portfolio links, and quick vCard or QR export.",
        searchKeywords = listOf("bio", "link", "social", "profile", "card", "landing", "creator", "portfolio", "links"),
    ),
    ToolDefinition(
        id = "social_preview",
        name = "Post Previewer",
        icon = Icons.Default.Visibility,
        category = ToolCategory.Social,
        description = "Preview how your posts, title, description, and link OpenGraph cards will look across Twitter/X, Instagram, Facebook, and LinkedIn before publishing.",
        searchKeywords = listOf("preview", "post", "social", "opengraph", "card", "meta", "twitter", "linkedin", "facebook", "feed"),
    ),
    ToolDefinition(
        id = "quote_maker",
        name = "Quote Creator",
        icon = Icons.Default.Palette,
        category = ToolCategory.Social,
        description = "Create aesthetic quote banners and social story graphics with customizable nature gradients, author attribution, typography, and image export.",
        searchKeywords = listOf("quote", "maker", "creator", "banner", "story", "status", "typography", "gradient", "instagram", "graphic"),
    ),
    // Sensors
    ToolDefinition(
        id = "level",
        name = "Bubble Level",
        icon = Icons.Default.Flare,
        category = ToolCategory.Sensors,
        description = "Check if surfaces are perfectly horizontal or vertical. Place your phone on any surface to see real-time tilt angles. Great for hanging pictures, leveling shelves, or aligning furniture.",
        requiredSensorType = Sensor.TYPE_ACCELEROMETER,
        searchKeywords = listOf("bubble", "spirit", "horizontal", "tilt", "level"),
    ),
    ToolDefinition(
        id = "compass",
        name = "Compass",
        icon = Icons.Default.Explore,
        category = ToolCategory.Sensors,
        description = "Find cardinal directions using your phone's magnetic sensor. Shows heading in degrees, cardinal direction, and a rotating compass dial. Useful for navigation, hiking, or orienting yourself outdoors.",
        requiredSensorType = Sensor.TYPE_MAGNETIC_FIELD,
        searchKeywords = listOf("direction", "north", "bearing", "heading"),
    ),
    ToolDefinition(
        id = "protractor",
        name = "Protractor",
        icon = Icons.Default.Architecture,
        category = ToolCategory.Sensors,
        description = "Measure angles by tilting your phone or using the on-screen protractor. Perfect for measuring slopes, roof pitches, or any angle you need to check.",
        searchKeywords = listOf("angle", "degree", "measure"),
    ),
    ToolDefinition(
        id = "ruler",
        name = "Ruler",
        icon = Icons.Default.Straighten,
        category = ToolCategory.Sensors,
        description = "Measure lengths directly on your phone screen in centimeters or inches. Drag markers to measure objects placed against your screen. Calibrated to your device's display.",
        searchKeywords = listOf("measure", "inch", "centimeter", "cm", "length"),
    ),
    ToolDefinition(
        id = "vibrometer",
        name = "Vibrometer",
        icon = Icons.Default.Vibration,
        category = ToolCategory.Sensors,
        description = "Detect and measure vibrations using the accelerometer. Shows real-time vibration frequency and amplitude with a live waveform display. Useful for checking appliance vibrations or surface stability.",
        requiredSensorType = Sensor.TYPE_ACCELEROMETER,
        searchKeywords = listOf("vibration", "frequency", "waveform", "surface"),
    ),
    ToolDefinition(
        id = "light_meter",
        name = "Light Meter",
        icon = Icons.Default.LightMode,
        category = ToolCategory.Sensors,
        description = "Measure ambient light intensity in lux using your phone's light sensor. Helpful for photography, checking workplace lighting conditions, or comparing light levels in different rooms.",
        requiredSensorType = Sensor.TYPE_LIGHT,
        searchKeywords = listOf("lux", "light", "ambient", "brightness", "illuminance"),
    ),
    ToolDefinition(
        id = "barometer",
        name = "Barometer",
        icon = Icons.Default.Speed,
        category = ToolCategory.Sensors,
        description = "Measure atmospheric pressure in hPa using the built-in pressure sensor. Track pressure trends to predict weather changes. Also estimates altitude based on barometric readings.",
        requiredSensorType = Sensor.TYPE_PRESSURE,
        searchKeywords = listOf("barometer", "pressure", "altitude", "weather", "hpa"),
    ),
    ToolDefinition(
        id = "humidity",
        name = "Humidity",
        icon = Icons.Default.WaterDrop,
        category = ToolCategory.Sensors,
        description = "Monitor relative humidity percentage using the device's humidity sensor. Tracks comfort level with dew point and heat index calculations. Useful for monitoring indoor climate.",
        requiredSensorType = Sensor.TYPE_RELATIVE_HUMIDITY,
        searchKeywords = listOf("humidity", "moisture", "dew", "relative", "comfort"),
    ),
    ToolDefinition(
        id = "pedometer",
        name = "Pedometer",
        icon = Icons.AutoMirrored.Filled.DirectionsWalk,
        category = ToolCategory.Sensors,
        description = "Count your steps and estimate distance walked using the step counter sensor. Track daily walking activity with a simple start/stop interface. Shows steps, distance, and calories.",
        requiredSensorType = Sensor.TYPE_STEP_COUNTER,
        searchKeywords = listOf("step", "walk", "pedometer", "counter", "fitness", "distance"),
    ),
    ToolDefinition(
        id = "gyroscope",
        name = "Gyroscope",
        icon = Icons.Default.ScreenRotation,
        category = ToolCategory.Sensors,
        description = "Visualize rotational motion in real time across pitch, roll, and yaw axes. Shows angular velocity data with a 3D orientation display. Useful for testing device sensors or understanding rotation.",
        requiredSensorType = Sensor.TYPE_GYROSCOPE,
        searchKeywords = listOf("gyroscope", "rotation", "pitch", "roll", "yaw", "angular"),
    ),
    ToolDefinition(
        id = "metal_detector",
        name = "Metal Detector",
        icon = Icons.Default.Search,
        category = ToolCategory.Sensors,
        description = "Detect nearby metal objects using the magnetic field sensor. Shows magnetic field strength and alerts when metal is detected. Can help find studs in walls, lost metal objects, or magnetic sources.",
        requiredSensorType = Sensor.TYPE_MAGNETIC_FIELD,
        searchKeywords = listOf("metal", "magnet", "magnetic", "field", "stud", "ferrous"),
    ),
    ToolDefinition(
        id = "heart_rate",
        name = "Heart Rate",
        icon = Icons.Default.Favorite,
        category = ToolCategory.Sensors,
        description = "Measure your heart rate by placing your finger over the camera lens. Uses photoplethysmography (PPG) to detect pulse from blood flow changes. For reference only, not a medical device.",
        requiresCamera = true,
        searchKeywords = listOf("heart", "pulse", "bpm", "heartbeat", "ppg", "monitor"),
    ),
    ToolDefinition(
        id = "plumb_bob",
        name = "Plumb Bob",
        icon = Icons.Default.Plumbing,
        category = ToolCategory.Sensors,
        description = "Camera overlay with a vertical plumb line for checking vertical alignment. Color-coded deviation indicator shows how far from true vertical.",
        requiresCamera = true,
        requiredSensorType = Sensor.TYPE_ACCELEROMETER,
        searchKeywords = listOf("plumb", "vertical", "level", "alignment", "construction"),
    ),
    ToolDefinition(
        id = "speedometer",
        name = "Speedometer",
        icon = Icons.Default.Speed,
        category = ToolCategory.Sensors,
        description = "Measure your speed using GPS with a real-time gauge display. Supports km/h, mph, m/s, and knots. Track trips with distance, max speed, and average speed statistics.",
        searchKeywords = listOf("speed", "velocity", "gps", "kmh", "mph", "trip", "distance"),
    ),
    ToolDefinition(
        id = "altitude",
        name = "Altitude",
        icon = Icons.Default.Height,
        category = ToolCategory.Sensors,
        description = "Measure your altitude above sea level using GPS. Track elevation changes with total ascent and descent. Shows altitude chart, min/max readings, and GPS accuracy.",
        searchKeywords = listOf("altitude", "elevation", "gps", "height", "ascent", "descent"),
    ),
    ToolDefinition(
        id = "lux_guide",
        name = "Lux & Exposure",
        icon = Icons.Default.Lightbulb,
        category = ToolCategory.Sensors,
        description = "Compare ambient light against standard architectural illumination levels and photography Exposure Value (EV).",
        requiredSensorType = Sensor.TYPE_LIGHT,
        searchKeywords = listOf("lux", "light", "exposure", "ev", "photography", "standards", "study", "office"),
    ),
    // Device
    ToolDefinition(
        id = "bluetooth_finder",
        name = "Bluetooth Finder",
        icon = Icons.AutoMirrored.Filled.BluetoothSearching,
        category = ToolCategory.Device,
        description = "Find lost Bluetooth devices like earbuds, trackers, and speakers by signal strength. Pick a nearby device and walk around — a playful radar gets warmer, faster, and buzzes as you close in.",
        searchKeywords = listOf("bluetooth", "ble", "finder", "find", "lost", "earbuds", "headphones", "tracker", "signal", "rssi", "proximity"),
    ),
    ToolDefinition(
        id = "device_info",
        name = "Device Info",
        icon = Icons.Default.PhoneAndroid,
        category = ToolCategory.Device,
        description = "View detailed device information: model, Android version, display specs, storage, RAM, CPU, battery health, and sensor list. All hardware details in one place.",
        searchKeywords = listOf("device", "info", "phone", "specs", "hardware", "android", "battery", "cpu", "ram", "storage"),
    ),
    ToolDefinition(
        id = "nfc_toolkit",
        name = "NFC Toolkit",
        icon = Icons.Default.Nfc,
        category = ToolCategory.Device,
        description = "Read, write, format, and erase NFC tags. View tag details including type, UID, memory size, and hex dump of raw data. Supports NDEF text, URL, and contact records.",
        searchKeywords = listOf("nfc", "tag", "ndef", "read", "write", "rfid", "mifare", "contactless"),
    ),
    ToolDefinition(
        id = "screen_grid",
        name = "Screen Test Grid",
        icon = Icons.Default.GridOn,
        category = ToolCategory.Device,
        description = "Full-screen color and grid pattern at maximum brightness. Inspect screen protectors for dust and scratches, or test for stuck pixels.",
        searchKeywords = listOf("screen", "test", "grid", "dead", "pixel", "protector", "dust", "scratch", "display"),
    ),
    ToolDefinition(
        id = "vibration_patterns",
        name = "Vibration Patterns",
        icon = Icons.Default.Waves,
        category = ToolCategory.Device,
        description = "Play predefined vibration patterns: tap, double-tap, heartbeat, pulse, wave, and SOS. Useful for testing the haptic motor or designing custom alerts.",
        searchKeywords = listOf("vibration", "vibrate", "haptic", "pattern", "buzz", "pulse", "sos", "heartbeat"),
    ),
    ToolDefinition(
        id = "dead_pixel_tester",
        name = "Dead Pixel",
        icon = Icons.Default.ScreenSearchDesktop,
        category = ToolCategory.Device,
        description = "Cycle through fullscreen primary and secondary colors to inspect screen for dead, stuck, or broken pixels.",
        searchKeywords = listOf("pixel", "dead pixel", "screen", "display", "stuck pixel", "monitor", "lcd", "oled"),
    ),
    ToolDefinition(
        id = "touch_tester",
        name = "Touch Tester",
        icon = Icons.Default.TouchApp,
        category = ToolCategory.Device,
        description = "Test multi-touch screen capabilities, touch coordinates, pressure, and gesture tracking with live pointer feedback.",
        searchKeywords = listOf("touch", "multitouch", "pointer", "screen", "finger", "gesture", "hardware"),
    ),
    ToolDefinition(
        id = "screen_dimmer",
        name = "Screen Dimmer",
        icon = Icons.Default.Brightness2,
        category = ToolCategory.Device,
        description = "Lower screen brightness below system minimum and apply amber blue-light warmth filters for comfortable night reading.",
        searchKeywords = listOf("dimmer", "screen", "night", "dark", "blue light", "warmth", "reading", "filter"),
    ),
    ToolDefinition(
        id = "call_logs_analytics",
        name = "Call Analytics",
        icon = Icons.Default.Call,
        category = ToolCategory.Device,
        description = "Analyze incoming, outgoing, and missed call statistics, total conversation duration, and top frequent contacts.",
        searchKeywords = listOf("call", "calls", "log", "analytics", "stats", "duration", "contacts", "phone"),
    ),
    ToolDefinition(
        id = "battery_info",
        name = "Battery Health",
        icon = Icons.Default.BatteryStd,
        category = ToolCategory.Device,
        description = "Monitor battery temperature, voltage, charging health status, technology, and health recommendations.",
        searchKeywords = listOf("battery", "power", "charge", "health", "voltage", "temperature", "mah"),
    ),
    // Education
    ToolDefinition(
        id = "formula_reference",
        name = "Formulas",
        icon = Icons.Default.Functions,
        category = ToolCategory.Education,
        description = "Browse and solve formulas from Math, Physics, Chemistry, and Finance. Enter known values to solve for any unknown with step-by-step substitution. Search, browse by subject, and favorite formulas.",
        searchKeywords = listOf("formula", "solver", "math", "physics", "chemistry", "finance", "equation", "reference"),
    ),
    ToolDefinition(
        id = "unit_circle",
        name = "Unit Circle",
        icon = Icons.Default.PieChart,
        category = ToolCategory.Education,
        description = "Interactive unit circle for visualizing trigonometric functions. Drag to set the angle and see sin, cos, and tan values along with degree and radian readouts.",
        searchKeywords = listOf("unit", "circle", "trigonometry", "trig", "sin", "cos", "tan", "angle", "degree", "radian", "math"),
    ),
    ToolDefinition(
        id = "eye_test",
        name = "Eye Test",
        icon = Icons.Default.Visibility,
        category = ToolCategory.Education,
        description = "Quick self-checks: a visual-acuity chart, an astigmatism fan, and a color-discrimination game. Not a medical diagnosis.",
        searchKeywords = listOf("eye", "test", "vision", "acuity", "snellen", "astigmatism", "color blind", "sight"),
    ),
    ToolDefinition(
        id = "education_hub",
        name = "Education",
        icon = Icons.Default.School,
        category = ToolCategory.Education,
        description = "Comprehensive science, physics, chemistry laws, interactive 118-element periodic table, physical constants, and games.",
        searchKeywords = listOf("education", "school", "learn", "study", "physics", "chemistry", "quiz", "elements", "periodic table", "games"),
    ),
    ToolDefinition(
        id = "periodic_table",
        name = "Periodic Table",
        icon = Icons.Default.Science,
        category = ToolCategory.Education,
        description = "Complete 118-element interactive periodic table with 18-column grid view, Bohr atomic shells, detailed chemical properties, and state filters.",
        searchKeywords = listOf("periodic table", "chemistry", "elements", "atoms", "mendeleev", "science", "chemical"),
    ),
    ToolDefinition(
        id = "science_labs",
        name = "Science Labs",
        icon = Icons.Default.Biotech,
        category = ToolCategory.Education,
        description = "Interactive physics simulations (Harmonic Pendulum, Projectile Motion) and genetics labs (DNA/RNA codon synthesizer, Punnett square).",
        searchKeywords = listOf("physics", "biology", "dna", "pendulum", "projectile", "punnett", "lab", "simulation", "stem"),
    ),
    ToolDefinition(
        id = "gpa_calculator",
        name = "GPA / CGPA",
        icon = Icons.Default.Grade,
        category = ToolCategory.Education,
        description = "Calculate semester GPA, cumulative CGPA across terms, and project required grades with the target GPA planner.",
        searchKeywords = listOf("gpa", "cgpa", "grade", "college", "university", "credits", "semester", "education", "target gpa"),
    ),
    // Games
    ToolDefinition(
        id = "random_generator",
        name = "Random/Dice",
        icon = Icons.Default.Casino,
        category = ToolCategory.Games,
        description = "Generate random numbers, flip coins, and roll dice. Set custom ranges for number generation. Features animated coin flip and dice roll for games and decision making.",
        searchKeywords = listOf("random", "coin", "flip", "dice", "number"),
    ),
    ToolDefinition(
        id = "chemistry_games",
        name = "Chemistry Games",
        icon = Icons.Default.Science,
        category = ToolCategory.Games,
        description = "Element Symbol Quiz, Atomic Number Speed Run, and Compound Synthesis Reaction Lab.",
        searchKeywords = listOf("chemistry", "game", "quiz", "element quiz", "compounds", "reactions", "lab"),
    ),
    ToolDefinition(
        id = "brain_games",
        name = "Brain & IQ Games",
        icon = Icons.Default.Psychology,
        category = ToolCategory.Games,
        description = "Speed Math workout, Sequence Solver pattern game, Visual Memory Matrix recall test, and Science Trivia quiz.",
        searchKeywords = listOf("brain", "iq", "math", "memory", "trivia", "quiz", "puzzle", "games"),
    ),
    ToolDefinition(
        id = "pokedex",
        name = "Pokédex",
        icon = Icons.Default.CatchingPokemon,
        category = ToolCategory.Games,
        description = "Complete National Pokédex with Pokémon across all generations including forms, shinies, and Legends: Z-A. Detailed base stats, elemental types, lore, and Who's That Pokémon mini-game.",
        searchKeywords = listOf("pokedex", "pokemon", "game", "stats", "dex", "anime", "monsters", "quiz", "types", "shiny", "mega", "za"),
    ),
    ToolDefinition(
        id = "spin_wheel",
        name = "Spin the Wheel",
        icon = Icons.Default.Casino,
        category = ToolCategory.Games,
        description = "Interactive randomized decision-making wheel with custom choices, smooth animated spinning, and winning slice selector.",
        searchKeywords = listOf("spin", "wheel", "decision", "picker", "random", "game", "roulette"),
    ),
    ToolDefinition(
        id = "random_name_picker",
        name = "Name / List Picker",
        icon = Icons.Default.Groups,
        category = ToolCategory.Games,
        description = "Pick random winners from any list of names or items with multi-pick support and optional drawn item removal.",
        searchKeywords = listOf("picker", "name", "random", "raffle", "giveaway", "list", "draw"),
    ),
    // AiTools
    ToolDefinition(
        id = "perchance_ai",
        name = "Perchance AI",
        icon = Icons.Default.AutoAwesome,
        category = ToolCategory.AiTools,
        description = "Offline procedural generator inspired by Perchance engine (characters, AI art prompts, story hooks, code challenges) and integrated in-app Web Hub launcher.",
        searchKeywords = listOf("perchance", "ai", "generator", "prompt", "creative", "story", "art", "character", "chat"),
    ),
    ToolDefinition(
        id = "perchance_text_gen",
        name = "Perchance Text",
        icon = Icons.Default.Draw,
        category = ToolCategory.AiTools,
        description = "AI text generator and interactive story writing sandbox running directly in-app with multi-profile support.",
        searchKeywords = listOf("perchance", "ai", "text", "story", "generator", "writing", "fiction", "novel"),
    ),
    ToolDefinition(
        id = "perchance_photo_gen",
        name = "Perchance Photo",
        icon = Icons.Default.Image,
        category = ToolCategory.AiTools,
        description = "AI image and art generator powered by text-to-image models directly in-app with multi-profile support.",
        searchKeywords = listOf("perchance", "ai", "photo", "image", "art", "picture", "diffusion", "generator"),
    ),
    ToolDefinition(
        id = "perchance_chat",
        name = "Perchance Chat",
        icon = Icons.AutoMirrored.Filled.Chat,
        category = ToolCategory.AiTools,
        description = "AI character conversational playground and roleplay chat directly in-app with multi-profile support.",
        searchKeywords = listOf("perchance", "ai", "chat", "character", "bot", "assistant", "roleplay"),
    ),
    ToolDefinition(
        id = "perchance_video_gen",
        name = "Perchance Video",
        icon = Icons.Default.Videocam,
        category = ToolCategory.AiTools,
        description = "AI video generator creating clips and animations from text prompts directly in-app with multi-profile support.",
        searchKeywords = listOf("perchance", "ai", "video", "generator", "animation", "motion", "clip", "prompt"),
    ),
    ToolDefinition(
        id = "perchance_story_gen",
        name = "Perchance Story",
        icon = Icons.Default.EditNote,
        category = ToolCategory.AiTools,
        description = "AI story generator crafting rich fiction, narrative arcs, and dynamic plotlines directly in-app with multi-profile support.",
        searchKeywords = listOf("perchance", "ai", "story", "generator", "fiction", "novel", "narrative", "author"),
    ),
    ToolDefinition(
        id = "perchance_character_gen",
        name = "Perchance Character",
        icon = Icons.Default.Person,
        category = ToolCategory.AiTools,
        description = "Detailed AI character generator with backstory, personality traits, and motivation profiles directly in-app.",
        searchKeywords = listOf("perchance", "ai", "character", "bio", "personality", "traits", "profile", "avatar"),
    ),
    ToolDefinition(
        id = "perchance_anime_gen",
        name = "Perchance Anime",
        icon = Icons.Default.Palette,
        category = ToolCategory.AiTools,
        description = "AI anime and manga art generator creating vivid stylized illustrations directly in-app with multi-profile support.",
        searchKeywords = listOf("perchance", "ai", "anime", "manga", "art", "illustration", "waifu", "drawing"),
    ),
    ToolDefinition(
        id = "perchance_rpg_gen",
        name = "Perchance RPG",
        icon = Icons.Default.Casino,
        category = ToolCategory.AiTools,
        description = "Interactive AI roleplay game and text adventure sandbox directly in-app with multi-profile support.",
        searchKeywords = listOf("perchance", "ai", "rpg", "game", "adventure", "quest", "roleplay", "dungeon"),
    ),
    ToolDefinition(
        id = "perchance_writing_prompt",
        name = "Writing Prompts",
        icon = Icons.Default.Lightbulb,
        category = ToolCategory.AiTools,
        description = "Creative AI writing prompt generator for novelists, screenwriters, and creative exercises directly in-app.",
        searchKeywords = listOf("perchance", "ai", "prompt", "writing", "creative", "idea", "inspiration", "story"),
    ),
    ToolDefinition(
        id = "perchance_lore_gen",
        name = "World & Lore",
        icon = Icons.Default.Public,
        category = ToolCategory.AiTools,
        description = "Fantasy and sci-fi worldbuilder generating factions, planets, magic systems, and ancient lore directly in-app.",
        searchKeywords = listOf("perchance", "ai", "world", "lore", "fantasy", "scifi", "worldbuilding", "planet", "magic"),
    ),
    ToolDefinition(
        id = "perchance_dialogue_gen",
        name = "AI Dialogue",
        icon = Icons.AutoMirrored.Filled.Chat,
        category = ToolCategory.AiTools,
        description = "Generates realistic and stylized character dialogue, banter, and dramatic monologues directly in-app.",
        searchKeywords = listOf("perchance", "ai", "dialogue", "script", "monologue", "conversation", "banter", "speech"),
    ),
    ToolDefinition(
        id = "perchance_voice_actor",
        name = "AI Voice & Audio",
        icon = Icons.Default.RecordVoiceOver,
        category = ToolCategory.AiTools,
        description = "AI voice actor and audio speech synthesis generator directly in-app with multi-profile support.",
        searchKeywords = listOf("perchance", "ai", "voice", "audio", "speech", "tts", "actor", "sound"),
    ),
    ToolDefinition(
        id = "perchance_art_plugin",
        name = "Perchance Art Plugin",
        icon = Icons.Default.Brush,
        category = ToolCategory.AiTools,
        description = "Customizable text-to-image neural plugin and advanced art settings generator directly in-app.",
        searchKeywords = listOf("perchance", "ai", "art", "plugin", "image", "diffusion", "custom", "plugin"),
    ),
    ToolDefinition(
        id = "perchance_name_gen",
        name = "Fantasy Names",
        icon = Icons.Default.Tag,
        category = ToolCategory.AiTools,
        description = "Fantasy and sci-fi name generator for characters, kingdoms, spells, starships, and artifacts directly in-app.",
        searchKeywords = listOf("perchance", "ai", "name", "fantasy", "generator", "titles", "characters", "naming"),
    ),
    ToolDefinition(
        id = "huggingface_ai",
        name = "Hugging Face AI",
        icon = Icons.Default.SmartToy,
        category = ToolCategory.AiTools,
        description = "Direct access to Hugging Face AI models, HuggingChat, AI Spaces, and assistants inside the app with multi-profile support.",
        searchKeywords = listOf("huggingface", "ai", "huggingchat", "spaces", "models", "llm", "chat", "assistant", "ml"),
    ),
    // AppTools
    ToolDefinition(
        id = "widgets_manager",
        name = "Widgets",
        icon = Icons.Default.GridOn,
        category = ToolCategory.AppTools,
        description = "Browse, customize, configure, and pin 10+ precision interactive widgets directly to your Android Home Screen.",
        searchKeywords = listOf("widget", "widgets", "creator", "manager", "pin", "glance", "home screen"),
    ),
    ToolDefinition(
        id = "quick_tiles_manager",
        name = "Quick Tiles",
        icon = Icons.Default.GridOn,
        category = ToolCategory.AppTools,
        description = "Configure, test, and request system addition for 25+ Quick Settings tiles in your Android notification shade.",
        searchKeywords = listOf("quick tile", "tiles", "quick settings", "shade", "status bar", "creator", "manager"),
    ),
    ToolDefinition(
        id = "shortcuts_manager",
        name = "Shortcuts",
        icon = Icons.Default.Pin,
        category = ToolCategory.AppTools,
        description = "Create custom home screen shortcuts with custom colors & labels, and manage 20+ predefined 1-tap app shortcuts.",
        searchKeywords = listOf("shortcut", "shortcuts", "pin", "launcher", "creator", "manager", "icon"),
    ),
    // DevTools
    ToolDefinition(
        id = "json_formatter",
        name = "JSON Formatter",
        icon = Icons.Default.Tag,
        category = ToolCategory.DevTools,
        description = "Format, beautify, and minify JSON with custom indentation, syntax error detection, and validity checks.",
        searchKeywords = listOf("json", "format", "beautify", "minify", "validate", "developer", "parse"),
    ),
    ToolDefinition(
        id = "uuid_generator",
        name = "UUID Generator",
        icon = Icons.Default.Key,
        category = ToolCategory.DevTools,
        description = "Generate RFC 4122 Version 4 UUIDs (universally unique identifiers) individually or in bulk with uppercase options.",
        searchKeywords = listOf("uuid", "guid", "generator", "random", "v4", "unique", "developer"),
    ),
    ToolDefinition(
        id = "html_entity_tool",
        name = "HTML Entities",
        icon = Icons.Default.Language,
        category = ToolCategory.DevTools,
        description = "Encode special characters into HTML entities (&amp;, &lt;, numeric codes) and decode entity strings back to text.",
        searchKeywords = listOf("html", "entities", "encode", "decode", "escape", "developer", "web"),
    ),
    ToolDefinition(
        id = "regex_tester",
        name = "Regex Tester",
        icon = Icons.Default.Search,
        category = ToolCategory.DevTools,
        description = "Test regular expressions with real-time match highlighting, group extraction, error diagnostics, and regex flag toggles.",
        searchKeywords = listOf("regex", "regular expression", "test", "match", "groups", "pattern"),
    ),
    // ProgrammingTools
    ToolDefinition(
        id = "number_base",
        name = "Number Base",
        icon = Icons.Default.Pin,
        category = ToolCategory.ProgrammingTools,
        description = "Convert numbers between decimal, binary, hexadecimal, and octal systems. Useful for programmers, students, and anyone working with different number formats.",
        searchKeywords = listOf("binary", "hex", "octal", "decimal", "base"),
    ),
    ToolDefinition(
        id = "hash_encode",
        name = "Hash & Encode",
        icon = Icons.Default.Tag,
        category = ToolCategory.ProgrammingTools,
        description = "Generate MD5, SHA-1 and SHA-256 hashes, plus Base64 and URL encoding for any text. Tap a result to copy.",
        searchKeywords = listOf("hash", "md5", "sha", "sha256", "base64", "url encode", "checksum", "encode"),
    ),
    // FilesTools
    ToolDefinition(
        id = "duplicate_photos",
        name = "Duplicate Photos",
        icon = Icons.Default.Photo,
        category = ToolCategory.FilesTools,
        description = "Find and delete exact duplicate photos to free up space. All comparison happens on-device — nothing is uploaded.",
        searchKeywords = listOf("duplicate", "photos", "dupes", "clean", "space", "gallery", "storage"),
    ),
    ToolDefinition(
        id = "storage_analyzer",
        name = "Storage Analyzer",
        icon = Icons.Default.PieChart,
        category = ToolCategory.FilesTools,
        description = "See what's using your storage — largest files, biggest folders, and a breakdown by file type. Delete space hogs with confirmation.",
        searchKeywords = listOf("storage", "analyzer", "space", "cleaner", "files", "folders", "disk", "usage"),
    ),
    // NetworkTools
    ToolDefinition(
        id = "wifi_signal",
        name = "WiFi Signal",
        icon = Icons.Default.Wifi,
        category = ToolCategory.NetworkTools,
        description = "Measure WiFi signal strength in dBm with quality rating. Log readings at different locations and export as CSV for network troubleshooting.",
        searchKeywords = listOf("wifi", "signal", "strength", "rssi", "network", "dbm", "wireless"),
    ),
    ToolDefinition(
        id = "network_info",
        name = "Network Info",
        icon = Icons.Default.NetworkCheck,
        category = ToolCategory.NetworkTools,
        description = "View network connection details: Wi-Fi SSID, BSSID, signal strength, frequency, channel, IP addresses (local and public), and mobile carrier info. Copy any value to clipboard.",
        searchKeywords = listOf("network", "ip", "address", "carrier", "wifi", "ssid", "bssid", "4g", "5g"),
    ),
    ToolDefinition(
        id = "wifi_qr_share",
        name = "WiFi QR Share",
        icon = Icons.Default.Wifi,
        category = ToolCategory.NetworkTools,
        description = "Generate a QR code from a WiFi network name and password so guests can scan and connect. Everything stays on device — nothing is sent anywhere.",
        searchKeywords = listOf("wifi", "qr", "share", "guest", "network", "password", "ssid", "hotspot"),
    ),
    ToolDefinition(
        id = "web_tools",
        name = "Web Tools",
        icon = Icons.Default.Language,
        category = ToolCategory.NetworkTools,
        description = "Inspect URLs, test HTTP response status codes, resolve DNS lookups, check latency ping, and inspect User-Agent.",
        searchKeywords = listOf("web", "http", "url", "dns", "ping", "user agent", "latency", "headers"),
    ),
    ToolDefinition(
        id = "network_speed_test",
        name = "Speed Test",
        icon = Icons.Default.Speed,
        category = ToolCategory.NetworkTools,
        description = "Measure network latency ping, packet jitter, and throughput speed with latency health guidelines.",
        searchKeywords = listOf("speed", "test", "ping", "latency", "network", "throughput", "bandwidth", "jitter"),
    ),
    // MediaConverters
    ToolDefinition(
        id = "image_resizer",
        name = "Image Resizer",
        icon = Icons.Default.Photo,
        category = ToolCategory.MediaConverters,
        description = "Resize images by exact pixel dimensions or percentage scale with aspect ratio lock and quick 1080p/720p presets.",
        searchKeywords = listOf("resize", "dimensions", "scale", "image", "photo", "pixel", "aspect ratio"),
    ),
    ToolDefinition(
        id = "image_format_converter",
        name = "Image Converter",
        icon = Icons.Default.Image,
        category = ToolCategory.MediaConverters,
        description = "Convert images between JPEG, PNG, and WEBP formats with adjustable quality slider and file size estimation.",
        searchKeywords = listOf("convert", "format", "jpeg", "png", "webp", "image", "compression"),
    ),
)
