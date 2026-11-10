package com.toolbox.devtools

import android.net.Uri
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toolbox.core.ui.copyToClipboard

@Composable
fun UrlBuilderScreen() {
    val context = LocalContext.current
    var baseUrl by remember { mutableStateOf("https://api.example.com/v1/search") }
    var paramKey by remember { mutableStateOf("query") }
    var paramValue by remember { mutableStateOf("sark tools") }
    val params = remember { mutableStateListOf(Pair("query", "sark tools"), Pair("limit", "25"), Pair("sort", "desc")) }
    var builtUrl by remember { mutableStateOf("") }

    fun build() {
        val uriBuilder = Uri.parse(baseUrl).buildUpon()
        params.forEach { (k, v) ->
            uriBuilder.appendQueryParameter(k, v)
        }
        builtUrl = uriBuilder.build().toString()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("URL & Query Parameter Builder", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Safely construct REST endpoints, parse query string parameters, and encode characters.")
            }
        }

        OutlinedTextField(
            value = baseUrl,
            onValueChange = { baseUrl = it },
            label = { Text("Base URL / Endpoint") },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Query Parameters:", fontWeight = FontWeight.SemiBold)
        params.forEachIndexed { index, (k, v) ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("$k = $v", modifier = Modifier.weight(1f), fontFamily = FontFamily.Monospace)
                OutlinedButton(onClick = { params.removeAt(index) }) {
                    Text("Remove")
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = paramKey,
                onValueChange = { paramKey = it },
                label = { Text("Key") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = paramValue,
                onValueChange = { paramValue = it },
                label = { Text("Value") },
                modifier = Modifier.weight(1f)
            )
        }
        Button(
            onClick = {
                if (paramKey.isNotBlank()) {
                    params.add(Pair(paramKey.trim(), paramValue.trim()))
                    paramKey = ""
                    paramValue = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Parameter")
        }

        Button(onClick = { build() }, modifier = Modifier.fillMaxWidth()) {
            Text("Construct Full URL")
        }

        if (builtUrl.isNotBlank()) {
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Constructed URI:", fontWeight = FontWeight.SemiBold)
                    Text(builtUrl, fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Button(onClick = { copyToClipboard(context, "URL", builtUrl) }, modifier = Modifier.fillMaxWidth()) {
                Text("Copy Full URL")
            }
        }
    }
}
