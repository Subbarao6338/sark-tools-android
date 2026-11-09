package com.toolbox.datetime

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.time.LocalDate

object EpicDateTimeLogic {

    fun getTeluguPanchangam(date: LocalDate): Map<String, String> {
        val tithis = listOf(
            "Prathama (పాడ్యమి)", "Dvitiya (విదియ)", "Tritiya (తదియ)", "Chaturthi (చవితి)",
            "Panchami (పంచమి)", "Shashti (షష్ఠి)", "Saptami (సప్తమి)", "Ashtami (అష్టమి)",
            "Navami (నవమి)", "Dashami (దశమి)", "Ekadashi (ఏకాదశి)", "Dvadashi (ద్వాదశి)",
            "Trayodashi (త్రయోదశి)", "Chaturdashi (చతుర్దశి)", "Purnima / Amavasya (పూర్ణిమ / అమావాస్య)"
        )
        val nakshatras = listOf(
            "Ashwini", "Bharani", "Krittika", "Rohini", "Mrigashira", "Ardra",
            "Punarvasu", "Pushya", "Ashlesha", "Magha", "Purva Phalguni", "Uttara Phalguni",
            "Hasta", "Chitra", "Swati", "Vishakha", "Anuradha", "Jyeshtha",
            "Mula", "Purva Ashadha", "Uttara Ashadha", "Shravana", "Dhanishta",
            "Shatabhisha", "Purva Bhadrapada", "Uttara Bhadrapada", "Revati"
        )
        val yogas = listOf("Vishkambha", "Priti", "Ayushman", "Saubhagya", "Shobhana", "Atiganda", "Sukarma", "Dhriti", "Shula", "Ganda")
        val varas = listOf("Ravivaram (ఆదివారం)", "Somavaram (సోమవారం)", "Mangalavaram (మంగళవారం)", "Budhavaram (బుధవారం)", "Guruvaram (గురువారం)", "Shukravaram (శుక్రవారం)", "Shanivaram (శనివారం)")

        val dayOfYear = date.dayOfYear
        val dayOfWeekIdx = date.dayOfWeek.value % 7 // 0 for Sunday
        val tithiIdx = (dayOfYear % tithis.size)
        val nakshatraIdx = (dayOfYear % nakshatras.size)
        val yogaIdx = (dayOfYear % yogas.size)
        val paksha = if ((dayOfYear / 15) % 2 == 0) "Shukla Paksha (శుక్ల పక్షం)" else "Krishna Paksha (కృష్ణ పక్షం)"

        return mapOf(
            "Date" to date.toString(),
            "Vara (వారం)" to varas[dayOfWeekIdx],
            "Paksha (పక్షం)" to paksha,
            "Tithi (తిథి)" to tithis[tithiIdx],
            "Nakshatra (నక్షత్రం)" to nakshatras[nakshatraIdx],
            "Yoga (యోగం)" to yogas[yogaIdx],
            "Karana (కరణం)" to "Bava / Balava"
        )
    }
}

@Composable
fun EpicDateTimeScreen(toolId: String = "panchangam") {
    var selectedTool by remember { mutableStateOf(toolId) }
    val tools = listOf(
        "panchangam" to "Telugu Panchangam",
        "pomodoro" to "Pomodoro Timer",
        "countdown" to "Countdown Timer"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tools.forEach { (id, name) ->
                FilterChip(selected = selectedTool == id, onClick = { selectedTool = id }, label = { Text(name) })
            }
        }

        when (selectedTool) {
            "panchangam" -> PanchangamUi()
            "pomodoro" -> PomodoroUi()
            "countdown" -> CountdownUi()
            else -> PanchangamUi()
        }
    }
}

@Composable
fun PanchangamUi() {
    var dateStr by remember { mutableStateOf(LocalDate.now().toString()) }
    val date = try { LocalDate.parse(dateStr.trim()) } catch (e: Exception) { LocalDate.now() }
    val panchangam = EpicDateTimeLogic.getTeluguPanchangam(date)

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Telugu Panchangam Calendar (తెలుగు పంచాంగం)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = dateStr, onValueChange = { dateStr = it }, label = { Text("Date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
            panchangam.forEach { (k, v) ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(k, fontWeight = FontWeight.SemiBold)
                    Text(v, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun PomodoroUi() {
    var isRunning by remember { mutableStateOf(false) }
    var secondsLeft by remember { mutableIntStateOf(25 * 60) }

    LaunchedEffect(isRunning) {
        while (isRunning && secondsLeft > 0) {
            delay(1000L)
            secondsLeft--
        }
    }

    val minutes = secondsLeft / 60
    val secs = secondsLeft % 60
    val timeFormatted = String.format("%02d:%02d", minutes, secs)

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Pomodoro Focus Timer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(timeFormatted, style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { isRunning = !isRunning }) {
                    Text(if (isRunning) "Pause" else "Start 25m Focus")
                }
                Button(onClick = {
                    isRunning = false
                    secondsLeft = 25 * 60
                }) {
                    Text("Reset")
                }
            }
        }
    }
}

@Composable
fun CountdownUi() {
    var targetHours by remember { mutableStateOf("1") }
    var running by remember { mutableStateOf(false) }
    var remainingSeconds by remember { mutableIntStateOf(3600) }

    LaunchedEffect(running) {
        while (running && remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
        }
    }

    val h = remainingSeconds / 3600
    val m = (remainingSeconds % 3600) / 60
    val s = remainingSeconds % 60
    val formatted = String.format("%02d:%02d:%02d", h, m, s)

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Countdown Timer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = targetHours, onValueChange = { targetHours = it }, label = { Text("Hours") }, modifier = Modifier.fillMaxWidth())
            Text(formatted, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    val hrs = targetHours.toIntOrNull() ?: 1
                    remainingSeconds = hrs * 3600
                    running = true
                }) {
                    Text("Start")
                }
                Button(onClick = { running = false }) {
                    Text("Stop")
                }
            }
        }
    }
}
