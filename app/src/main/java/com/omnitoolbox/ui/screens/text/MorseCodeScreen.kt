package omni.toolbox.ui.screens.text

import android.content.Context
import android.hardware.camera2.CameraManager
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import kotlinx.coroutines.delay

@Composable
fun MorseCodeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var text by remember { mutableStateOf("") }
    var isSignaling by remember { mutableStateOf(false) }

    val cameraManager = remember { context.getSystemService(Context.CAMERA_SERVICE) as CameraManager }
    val cameraId = remember {
        cameraManager.cameraIdList.firstOrNull { id ->
            cameraManager.getCameraCharacteristics(id).get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
    }

    val morseOutput = remember(text) { MorseUtils.encode(text) }

    LaunchedEffect(isSignaling) {
        if (isSignaling && cameraId != null) {
            try {
                for (char in morseOutput) {
                    when (char) {
                        '.' -> {
                            cameraManager.setTorchMode(cameraId, true)
                            delay(200)
                            cameraManager.setTorchMode(cameraId, false)
                            delay(200)
                        }
                        '-' -> {
                            cameraManager.setTorchMode(cameraId, true)
                            delay(600)
                            cameraManager.setTorchMode(cameraId, false)
                            delay(200)
                        }
                        '/' -> delay(600)
                        ' ' -> delay(200)
                    }
                }
            } finally {
                isSignaling = false
                cameraManager.setTorchMode(cameraId, false)
            }
        }
    }

    ToolScreen(title = "Morse Code", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Enter text") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if (text.isNotEmpty()) {
                        IconButton(onClick = { text = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Morse Code", style = MaterialTheme.typography.labelLarge)
                if (morseOutput.isNotEmpty() && !morseOutput.contains("?")) {
                    Row {
                        IconButton(
                            onClick = { isSignaling = !isSignaling },
                            enabled = cameraId != null && !isSignaling
                        ) {
                            Icon(
                                Icons.Default.FlashlightOn,
                                contentDescription = "Signal with Flashlight",
                                tint = if (isSignaling) MaterialTheme.colorScheme.primary else LocalContentColor.current
                            )
                        }
                        IconButton(onClick = { clipboardManager.setText(AnnotatedString(morseOutput)) }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                modifier = Modifier.fillMaxWidth().weight(1f),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    morseOutput,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}
