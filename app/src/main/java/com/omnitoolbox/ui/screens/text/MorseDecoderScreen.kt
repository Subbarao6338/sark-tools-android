package omni.toolbox.ui.screens.text

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen

@Composable
fun MorseDecoderScreen(navController: NavHostController) {
    var input by remember { mutableStateOf("") }
    val decoded = remember(input) { MorseUtils.decode(input) }
    val clipboardManager = LocalClipboardManager.current

    ToolScreen(
        title = "Morse Decoder",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("Morse Code (use space between letters, / for words)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("... --- ...") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Decoded Text", style = MaterialTheme.typography.labelSmall)
                    Text(decoded, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    IconButton(onClick = { clipboardManager.setText(AnnotatedString(decoded)) }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                    }
                }
            }
        }
    }
}
