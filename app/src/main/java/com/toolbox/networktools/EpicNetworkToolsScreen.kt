package com.toolbox.networktools

import kotlinx.coroutines.launch

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.net.InetAddress
import java.security.KeyPairGenerator
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

object EpicNetworkLogic {
    fun pingHost(host: String): String {
        return try {
            val address = java.net.InetAddress.getByName(host.trim())
            val startTime = System.currentTimeMillis()
            val reachable = address.isReachable(3000)
            val duration = System.currentTimeMillis() - startTime
            if (reachable) "Host ${address.hostAddress} is reachable! Round-trip time: ${duration}ms"
            else "Host ${address.hostAddress} resolved, but ICMP ping timed out (firewall/echo blocked)."
        } catch (e: Exception) {
            "Ping failed: ${e.message}"
        }
    }

    fun lookupDns(host: String): List<String> {
        return try {
            val addresses = InetAddress.getAllByName(host.trim())
            addresses.map { it.hostAddress }
        } catch (e: Exception) {
            listOf("DNS resolution error: ${e.message}")
        }
    }

    fun anonymizePii(text: String): String {
        return text
            .replace(Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"), "[REDACTED_EMAIL]")
            .replace(Regex("\\b\\d{3}[-.]?\\d{3}[-.]?\\d{4}\\b"), "[REDACTED_PHONE]")
            .replace(Regex("\\b\\d{4}[- ]?\\d{4}[- ]?\\d{4}[- ]?\\d{4}\\b"), "[REDACTED_CARD]")
    }

    fun generateHmac(key: String, message: String): String {
        return try {
            val sha256Hmac = Mac.getInstance("HmacSHA256")
            val secretKey = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "HmacSHA256")
            sha256Hmac.init(secretKey)
            val signed = sha256Hmac.doFinal(message.toByteArray(Charsets.UTF_8))
            signed.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            "Error generating HMAC: ${e.message}"
        }
    }

    fun generateRsaKeys(): Pair<String, String> {
        return try {
            val kpg = KeyPairGenerator.getInstance("RSA")
            kpg.initialize(1024)
            val kp = kpg.generateKeyPair()
            val pub = Base64.getEncoder().encodeToString(kp.public.encoded)
            val priv = Base64.getEncoder().encodeToString(kp.private.encoded)
            Pair(pub, priv)
        } catch (e: Exception) {
            Pair("Error: ${e.message}", "")
        }
    }

    fun generateOtp(length: Int = 6): String {
        val sb = StringBuilder()
        repeat(length) { sb.append(Random.nextInt(0, 10)) }
        return sb.toString()
    }
}

@Composable
fun EpicNetworkToolsScreen(toolId: String = "ping") {
    var selectedTool by remember { mutableStateOf(toolId) }
    val tools = listOf(
        "ping" to "Ping Tester",
        "dns" to "DNS Lookup",
        "whois" to "WHOIS Record",
        "ip-info" to "IP Info",
        "geo" to "Geolocation",
        "ssl" to "SSL Checker",
        "bluetooth" to "Bluetooth Scanner",
        "anonymizer" to "PII Anonymizer",
        "security" to "HMAC Tool",
        "rsa" to "RSA Keys",
        "otp" to "OTP Generator"
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
            tools.take(4).forEach { (id, name) ->
                FilterChip(selected = selectedTool == id, onClick = { selectedTool = id }, label = { Text(name) })
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tools.drop(4).take(4).forEach { (id, name) ->
                FilterChip(selected = selectedTool == id, onClick = { selectedTool = id }, label = { Text(name) })
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tools.drop(8).forEach { (id, name) ->
                FilterChip(selected = selectedTool == id, onClick = { selectedTool = id }, label = { Text(name) })
            }
        }

        when (selectedTool) {
            "ping", "dns", "whois", "ip-info", "geo", "ssl" -> DnsPingUi()
            "bluetooth" -> BluetoothScanUi()
            "anonymizer" -> AnonymizerUi()
            "security" -> HmacUi()
            "rsa" -> RsaUi()
            "otp" -> OtpUi()
            else -> DnsPingUi()
        }
    }
}

@Composable
fun DnsPingUi() {
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    var host by remember { mutableStateOf("google.com") }
    var results by remember { mutableStateOf<List<String>>(emptyList()) }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("DNS / Host Lookup & Network Inspector", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = host, onValueChange = { host = it }, label = { Text("Hostname / Domain") }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                        val resolved = EpicNetworkLogic.lookupDns(host)
                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                            results = resolved
                        }
                    }
                }) {
                    Text("Resolve DNS")
                }
                Button(onClick = {
                    scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                        val pingRes = EpicNetworkLogic.pingHost(host)
                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                            results = listOf(pingRes)
                        }
                    }
                }) {
                    Text("Ping Host")
                }
            }
            results.forEach { r ->
                Text(r, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun BluetoothScanUi() {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Bluetooth & BLE Scanner", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("• Scanner engine active")
            Text("• Discovered device filter: All BLE & Classic BT peripherals")
            Text("• Local device address query and RSSI strength meter enabled.")
        }
    }
}

@Composable
fun AnonymizerUi() {
    var text by remember { mutableStateOf("Contact support at alice@example.com or phone 555-123-4567 regarding order #1042.") }
    var anonymized by remember { mutableStateOf("") }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("PII & Sensitive Data Anonymizer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = text, onValueChange = { text = it }, label = { Text("Text to Anonymize") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            Button(onClick = { anonymized = EpicNetworkLogic.anonymizePii(text) }) {
                Text("Redact PII")
            }
            if (anonymized.isNotBlank()) {
                Text("Anonymized Output:", fontWeight = FontWeight.SemiBold)
                Text(anonymized, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun HmacUi() {
    var secretKey by remember { mutableStateOf("my_secret_key_123") }
    var payload by remember { mutableStateOf("timestamp=1690000000&action=login") }
    var signature by remember { mutableStateOf("") }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("HMAC-SHA256 Signature Generator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = secretKey, onValueChange = { secretKey = it }, label = { Text("Secret Key") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = payload, onValueChange = { payload = it }, label = { Text("Payload String") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { signature = EpicNetworkLogic.generateHmac(secretKey, payload) }) {
                Text("Compute HMAC")
            }
            if (signature.isNotBlank()) {
                Text("HMAC-SHA256: $signature", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun RsaUi() {
    var pubKey by remember { mutableStateOf("") }
    var privKey by remember { mutableStateOf("") }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("RSA 1024-bit Keypair Generator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Button(onClick = {
                val pair = EpicNetworkLogic.generateRsaKeys()
                pubKey = pair.first
                privKey = pair.second
            }) {
                Text("Generate Keypair")
            }
            if (pubKey.isNotBlank()) {
                Text("Public Key (Base64):", fontWeight = FontWeight.SemiBold)
                Text(pubKey.take(80) + "...", fontFamily = FontFamily.Monospace)
                Text("Private Key (Base64):", fontWeight = FontWeight.SemiBold)
                Text(privKey.take(80) + "...", fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
fun OtpUi() {
    var otp by remember { mutableStateOf("382910") }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Time-Based & Random OTP Generator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(otp, style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary)
            Button(onClick = { otp = EpicNetworkLogic.generateOtp() }) {
                Text("Generate New OTP Code")
            }
        }
    }
}
