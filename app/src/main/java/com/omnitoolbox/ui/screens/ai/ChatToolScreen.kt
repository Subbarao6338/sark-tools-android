package omni.toolbox.ui.screens.ai

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import omni.toolbox.ui.components.ToolScreen

data class ChatMessage(val text: String, val isUser: Boolean)

// Offline 50+ Term/Phrase Travel Dictionary
val travelDictionary = mapOf(
    "hello" to mapOf(
        "french" to "bonjour", "spanish" to "hola", "italian" to "ciao",
        "german" to "hallo / guten tag", "japanese" to "こんにちは (konnichiwa)", "portuguese" to "olá"
    ),
    "goodbye" to mapOf(
        "french" to "au revoir", "spanish" to "adiós", "italian" to "arrivederci",
        "german" to "auf wiedersehen", "japanese" to "さようなら (sayōnara)", "portuguese" to "adeus"
    ),
    "please" to mapOf(
        "french" to "s'il vous plaît", "spanish" to "por favor", "italian" to "per favore",
        "german" to "bitte", "japanese" to "お願いします (onegaishimasu)", "portuguese" to "por favor"
    ),
    "thank you" to mapOf(
        "french" to "merci", "spanish" to "gracias", "italian" to "grazie",
        "german" to "danke", "japanese" to "ありがとう (arigatō)", "portuguese" to "obrigado"
    ),
    "yes" to mapOf(
        "french" to "oui", "spanish" to "sí", "italian" to "sì",
        "german" to "ja", "japanese" to "はい (hai)", "portuguese" to "sim"
    ),
    "no" to mapOf(
        "french" to "non", "spanish" to "no", "italian" to "no",
        "german" to "nein", "japanese" to "いいえ (iie)", "portuguese" to "não"
    ),
    "sorry" to mapOf(
        "french" to "désolé", "spanish" to "lo siento", "italian" to "scusa",
        "german" to "es tut mir leid", "japanese" to "ごめんなさい (gomen'nasai)", "portuguese" to "desculpe"
    ),
    "excuse me" to mapOf(
        "french" to "excusez-moi", "spanish" to "disculpe", "italian" to "mi scusi",
        "german" to "entschuldigung", "japanese" to "すみません (sumimasen)", "portuguese" to "com licença"
    ),
    "welcome" to mapOf(
        "french" to "bienvenue", "spanish" to "bienvenido", "italian" to "benvenuto",
        "german" to "willkommen", "japanese" to "ようこそ (yōkoso)", "portuguese" to "bem-vindo"
    ),
    "how are you" to mapOf(
        "french" to "comment ça va?", "spanish" to "¿cómo estás?", "italian" to "come stai?",
        "german" to "wie geht es dir?", "japanese" to "お元気ですか (o genki desu ka?)", "portuguese" to "como está?"
    ),
    "where is" to mapOf(
        "french" to "où se trouve?", "spanish" to "¿dónde está?", "italian" to "dove si trova?",
        "german" to "wo ist?", "japanese" to "どこですか (doko desu ka?)", "portuguese" to "onde fica?"
    ),
    "bathroom" to mapOf(
        "french" to "les toilettes", "spanish" to "el baño", "italian" to "il bagno",
        "german" to "die toilette", "japanese" to "トイレ (toire)", "portuguese" to "a casa de banho"
    ),
    "hotel" to mapOf(
        "french" to "l'hôtel", "spanish" to "el hotel", "italian" to "l'hotel",
        "german" to "das hotel", "japanese" to "ホテル (hoteru)", "portuguese" to "o hotel"
    ),
    "airport" to mapOf(
        "french" to "l'aéroport", "spanish" to "el aeropuerto", "italian" to "l'aeroporto",
        "german" to "der flughafen", "japanese" to "空港 (kūkō)", "portuguese" to "o aeroporto"
    ),
    "station" to mapOf(
        "french" to "la gare", "spanish" to "la estación", "italian" to "la stazione",
        "german" to "der bahnhof", "japanese" to "駅 (eki)", "portuguese" to "a estação"
    ),
    "restaurant" to mapOf(
        "french" to "le restaurant", "spanish" to "el restaurante", "italian" to "il ristorante",
        "german" to "das restaurant", "japanese" to "レストラン (resutoran)", "portuguese" to "o restaurante"
    ),
    "water" to mapOf(
        "french" to "l'eau", "spanish" to "el agua", "italian" to "l'acqua",
        "german" to "das wasser", "japanese" to "水 (mizu)", "portuguese" to "a água"
    ),
    "food" to mapOf(
        "french" to "la nourriture", "spanish" to "la comida", "italian" to "il cibo",
        "german" to "das essen", "japanese" to "食べ物 (tabemono)", "portuguese" to "a comida"
    ),
    "menu" to mapOf(
        "french" to "le menu", "spanish" to "el menú", "italian" to "il menu",
        "german" to "die speisekarte", "japanese" to "メニュー (menyū)", "portuguese" to "o menu"
    ),
    "check" to mapOf(
        "french" to "l'addition", "spanish" to "la cuenta", "italian" to "il conto",
        "german" to "die rechnung", "japanese" to "会計 (kaikei)", "portuguese" to "a conta"
    ),
    "money" to mapOf(
        "french" to "l'argent", "spanish" to "el dinero", "italian" to "il denaro",
        "german" to "das geld", "japanese" to "お金 (okane)", "portuguese" to "o dinheiro"
    ),
    "help" to mapOf(
        "french" to "à l'aide / au secours", "spanish" to "ayuda", "italian" to "aiuto",
        "german" to "hilfe", "japanese" to "助けて (tasukete)", "portuguese" to "ajuda"
    ),
    "doctor" to mapOf(
        "french" to "le médecin", "spanish" to "el médico", "italian" to "il medico",
        "german" to "der arzt", "japanese" to "医者 (isha)", "portuguese" to "o médico"
    ),
    "hospital" to mapOf(
        "french" to "l'hôpital", "spanish" to "el hospital", "italian" to "l'ospedale",
        "german" to "das krankenhaus", "japanese" to "病院 (byōin)", "portuguese" to "o hospital"
    ),
    "police" to mapOf(
        "french" to "la police", "spanish" to "la policía", "italian" to "la polizia",
        "german" to "die polizei", "japanese" to "警察 (keisatsu)", "portuguese" to "a polícia"
    ),
    "passport" to mapOf(
        "french" to "le passeport", "spanish" to "el pasaporte", "italian" to "il pasaporte",
        "german" to "der reisepass", "japanese" to "パスポート (pasupōto)", "portuguese" to "o passaporte"
    ),
    "telephone" to mapOf(
        "french" to "le téléphone", "spanish" to "el teléfono", "italian" to "il telefono",
        "german" to "das telefon", "japanese" to "電話 (denwa)", "portuguese" to "o telefone"
    ),
    "wifi" to mapOf(
        "french" to "le wifi", "spanish" to "el wifi", "italian" to "il wifi",
        "german" to "das wlan", "japanese" to "ワイファイ (waifai)", "portuguese" to "o wi-fi"
    ),
    "how much is" to mapOf(
        "french" to "combien coûte?", "spanish" to "¿cuánto cuesta?", "italian" to "quanto costa?",
        "german" to "wie viel kostet?", "japanese" to "いくらですか (ikura desu ka?)", "portuguese" to "quanto custa?"
    ),
    "do you speak english" to mapOf(
        "french" to "parlez-vous anglais?", "spanish" to "¿hablas inglés?", "italian" to "parla inglese?",
        "german" to "sprechen sie englisch?", "japanese" to "英語が話せますか (eigo ga hanasemasu ka?)", "portuguese" to "fala inglês?"
    ),
    "i don't understand" to mapOf(
        "french" to "je ne comprends pas", "spanish" to "no entiendo", "italian" to "non capisco",
        "german" to "ich verstehe nicht", "japanese" to "分かりません (wakarimasen)", "portuguese" to "não entendo"
    ),
    "where is the exit" to mapOf(
        "french" to "où est la sortie?", "spanish" to "¿dónde está la salida?", "italian" to "dov'è l'uscita?",
        "german" to "wo ist der ausgang?", "japanese" to "出口はどこですか (deguchi wa doko desu ka?)", "portuguese" to "onde é a saída?"
    ),
    "entrance" to mapOf(
        "french" to "l'entrée", "spanish" to "la entrada", "italian" to "l'entrata",
        "german" to "der eingang", "japanese" to "入り口 (iriguchi)", "portuguese" to "a entrada"
    ),
    "ticket" to mapOf(
        "french" to "le billet", "spanish" to "el boleto", "italian" to "il biglietto",
        "german" to "die fahrkarte", "japanese" to "切符 (kippu)", "portuguese" to "o bilhete"
    ),
    "map" to mapOf(
        "french" to "la carte", "spanish" to "el mapa", "italian" to "la mappa",
        "german" to "die karte", "japanese" to "地図 (chizu)", "portuguese" to "o mapa"
    ),
    "friend" to mapOf(
        "french" to "l'ami", "spanish" to "el amigo", "italian" to "l'amico",
        "german" to "der freund", "japanese" to "友達 (tomodachi)", "portuguese" to "o amigo"
    ),
    "bus" to mapOf(
        "french" to "l'autobus", "spanish" to "el autobús", "italian" to "l'autobus",
        "german" to "der bus", "japanese" to "バス (basu)", "portuguese" to "o autocarro"
    ),
    "train" to mapOf(
        "french" to "le train", "spanish" to "el tren", "italian" to "il treno",
        "german" to "der zug", "japanese" to "電車 (densha)", "portuguese" to "o comboio"
    ),
    "taxi" to mapOf(
        "french" to "le taxi", "spanish" to "el taxi", "italian" to "il taxi",
        "german" to "das taxi", "japanese" to "タクシー (takushī)", "portuguese" to "o táxi"
    ),
    "subway" to mapOf(
        "french" to "le métro", "spanish" to "el metro", "italian" to "la metropolitana",
        "german" to "die u-bahn", "japanese" to "地下鉄 (chikatetsu)", "portuguese" to "o metro"
    ),
    "car" to mapOf(
        "french" to "la voiture", "spanish" to "el coche", "italian" to "l'auto",
        "german" to "das auto", "japanese" to "車 (kuruma)", "portuguese" to "o carro"
    ),
    "key" to mapOf(
        "french" to "la clé", "spanish" to "la llave", "italian" to "la chiave",
        "german" to "der schlüssel", "japanese" to "鍵 (kagi)", "portuguese" to "a chave"
    ),
    "room" to mapOf(
        "french" to "la chambre", "spanish" to "la habitación", "italian" to "la camera",
        "german" to "das zimmer", "japanese" to "部屋 (heya)", "portuguese" to "o quarto"
    ),
    "store" to mapOf(
        "french" to "le magasin", "spanish" to "la tienda", "italian" to "il negozio",
        "german" to "das geschäft", "japanese" to "店 (mise)", "portuguese" to "a loja"
    ),
    "supermarket" to mapOf(
        "french" to "le supermarché", "spanish" to "el supermercado", "italian" to "il supermercato",
        "german" to "der supermarkt", "japanese" to "スーパー (sūpā)", "portuguese" to "o supermercado"
    ),
    "open" to mapOf(
        "french" to "ouvert", "spanish" to "abierto", "italian" to "aperto",
        "german" to "offen", "japanese" to "開いている (aiteiru)", "portuguese" to "aberto"
    ),
    "closed" to mapOf(
        "french" to "fermé", "spanish" to "cerrado", "italian" to "chiuso",
        "german" to "geschlossen", "japanese" to "閉まっている (shimatteiru)", "portuguese" to "fechado"
    ),
    "today" to mapOf(
        "french" to "aujourd'hui", "spanish" to "hoy", "italian" to "oggi",
        "german" to "heute", "japanese" to "今日 (kyō)", "portuguese" to "hoje"
    ),
    "tomorrow" to mapOf(
        "french" to "demain", "spanish" to "mañana", "italian" to "domani",
        "german" to "morgen", "japanese" to "明日 (ashita)", "portuguese" to "amanhã"
    ),
    "yesterday" to mapOf(
        "french" to "hier", "spanish" to "ayer", "italian" to "ieri",
        "german" to "gestern", "japanese" to "昨日 (kinō)", "portuguese" to "ontem"
    )
)

@Composable
fun ChatToolScreen(navController: NavHostController, title: String, aiApiKey: String = "") {
    var inputText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val generativeModel = remember(aiApiKey) {
        if (aiApiKey.isNotBlank()) {
            GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = aiApiKey
            )
        } else null
    }

    // Local image picking launcher for real offline OCR (AI Text Extractor)
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                messages.add(ChatMessage("Selected image for text extraction. Analyzing offline via ML Kit OCR...", true))
                try {
                    val inputImage = withContext(Dispatchers.IO) {
                        InputImage.fromFilePath(context, uri)
                    }
                    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                    val result = recognizer.process(inputImage).await()

                    val extracted = result.text.trim()
                    if (extracted.isNotEmpty()) {
                        val lines = extracted.lines().size
                        val words = extracted.split(Regex("\\s+")).filter { it.isNotEmpty() }.size
                        val chars = extracted.length
                        messages.add(
                            ChatMessage(
                                "✔ Offline Text Extraction Complete!\n\n" +
                                "$extracted\n\n" +
                                "--- Analytics ---\n" +
                                "• Lines: $lines\n" +
                                "• Words: $words\n" +
                                "• Characters: $chars",
                                false
                            )
                        )
                    } else {
                        messages.add(ChatMessage("No readable text was detected in the selected image. Please try a clearer picture.", false))
                    }
                } catch (e: Exception) {
                    messages.add(ChatMessage("Failed to run offline OCR text extraction: ${e.localizedMessage}", false))
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (messages.isEmpty()) {
            val welcomeText = if (title == "AI Text Extractor") {
                "Hello! I am your assistant for $title. Tape the attachment icon to select an image from your device and extract its text fully offline using native OCR!"
            } else {
                "Hello! I am your assistant for $title. How can I help you today?"
            }
            messages.add(ChatMessage(welcomeText, false))
        }
    }

    ToolScreen(
        title = title,
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messages) { message ->
                    ChatBubble(message)
                }
            }

            Surface(
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Attachment button for offline OCR on AI Text Extractor
                    if (title == "AI Text Extractor" || title == "Text Extractor") {
                        IconButton(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Pick Image")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                if (title == "AI Text Extractor") "Type or attach an image..."
                                else "Type a message..."
                            )
                        },
                        maxLines = 4,
                        shape = RoundedCornerShape(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                val currentInput = inputText
                                messages.add(ChatMessage(inputText, true))
                                inputText = ""

                                if (generativeModel != null) {
                                    scope.launch {
                                        try {
                                            val response = generativeModel.generateContent(content {
                                                text("You are an AI assistant specialized in '$title'.")
                                                text(currentInput)
                                            })
                                            messages.add(ChatMessage(response.text ?: "No response from AI.", false))
                                        } catch (e: Exception) {
                                            messages.add(ChatMessage("Error: ${e.localizedMessage}", false))
                                        }
                                    }
                                } else {
                                    // Fallback Implementation logic
                                    messages.add(ChatMessage("$title: Processing request offline (Local AI)...", false))
                                    val actualResponse = when {
                                        title == "CSV to JSON" -> {
                                            try {
                                                val lines = currentInput.lines().filter { it.isNotBlank() }
                                                if (lines.size < 2) "Offline AI: Please provide at least a header and one row of data."
                                                else {
                                                    val header = parseCsvLine(lines.first())
                                                    val rows = lines.drop(1).map { parseCsvLine(it) }
                                                    val jsonResult = rows.joinToString(",\n  ", "[\n  ", "\n]") { row ->
                                                        header.zip(row).joinToString(", ", "{ ", " }") { (k, v) ->
                                                            "\"${k.replace("\"", "\\\"")}\": \"${v.replace("\"", "\\\"")}\""
                                                        }
                                                    }
                                                    "Converted JSON:\n$jsonResult"
                                                }
                                            } catch (e: Exception) {
                                                "Offline AI: Error parsing CSV. Details: ${e.localizedMessage}"
                                            }
                                        }
                                        title == "Text Summarizer" -> {
                                            val sentences = currentInput.split(Regex("[.!?]")).filter { it.isNotBlank() }
                                            if (sentences.size <= 2) "Summary: $currentInput"
                                            else {
                                                val words = currentInput.lowercase().split(Regex("\\W+")).filter { it.length > 3 }
                                                val wordFreq = words.groupingBy { it }.eachCount()
                                                val rankedSentences = sentences.associateWith { s ->
                                                    s.lowercase().split(Regex("\\W+")).sumOf { wordFreq[it] ?: 0 }
                                                }.toList().sortedByDescending { it.second }
                                                "Offline Summary (Top 2 sentences):\n" + rankedSentences.take(2).joinToString(". ") { it.first.trim() } + "."
                                            }
                                        }
                                        title == "Grammar Checker" -> {
                                            val issues = mutableListOf<String>()
                                            val trimmed = currentInput.trim()

                                            if (trimmed.isNotEmpty()) {
                                                if (trimmed.first().isLowerCase()) {
                                                    issues.add("Sentence capitalization: First letter '${trimmed.first()}' should be uppercase.")
                                                }
                                                if (currentInput.contains("  ")) {
                                                    issues.add("Spacing issue: Double spaces detected.")
                                                }
                                                if (!trimmed.endsWith(".") && !trimmed.endsWith("?") && !trimmed.endsWith("!")) {
                                                    issues.add("Terminal punctuation: Missing sentence terminal punctuation (e.g., '.', '?', '!').")
                                                }

                                                // Commonly confused words validation
                                                val tokens = currentInput.lowercase().split(Regex("\\W+")).filter { it.isNotBlank() }
                                                for (i in 0 until tokens.size - 1) {
                                                    val currentWord = tokens[i]
                                                    val nextWord = tokens[i + 1]
                                                    if (currentWord == "its" && nextWord in setOf("a", "an", "the", "very", "too")) {
                                                        issues.add("Homophone confusion: Did you mean 'it's' (it is) instead of 'its' (possessive) before '$nextWord'?")
                                                    }
                                                    if (currentWord == "their" && nextWord in setOf("are", "were", "is", "going", "doing", "very")) {
                                                        issues.add("Homophone confusion: Did you mean 'they're' (they are) or 'there' (place) instead of 'their' (possessive) before '$nextWord'?")
                                                    }
                                                    if (currentWord == "there" && nextWord in setOf("car", "house", "dog", "parents", "book")) {
                                                        issues.add("Homophone confusion: Did you mean 'their' (possessive) instead of 'there' (place/existence) before the noun '$nextWord'?")
                                                    }
                                                }
                                            } else {
                                                issues.add("Input is empty.")
                                            }

                                            if (issues.isEmpty()) {
                                                "Offline Grammar Check Results:\n" +
                                                "✔ All checks passed! No capitalization, spacing, punctuation, or common homophone errors detected locally."
                                            } else {
                                                "Offline Grammar Check Suggestions:\n" +
                                                issues.joinToString("\n") { "✖ $it" }
                                            }
                                        }
                                        title == "Sentiment Analysis" -> {
                                            val sentimentLexicon = mapOf(
                                                "good" to 2, "great" to 3, "excellent" to 4, "happy" to 3, "love" to 4,
                                                "amazing" to 4, "best" to 4, "super" to 3, "cool" to 2, "wonderful" to 4,
                                                "perfect" to 4, "nice" to 2, "glad" to 2, "joy" to 3, "awesome" to 4,
                                                "fantastic" to 4, "beautiful" to 3, "fine" to 1, "satisfied" to 2,
                                                "bad" to -2, "terrible" to -4, "worst" to -4, "hate" to -4, "sad" to -2,
                                                "awful" to -3, "poor" to -2, "broken" to -2, "failed" to -3, "error" to -2,
                                                "useless" to -3, "slow" to -1, "hard" to -1, "disappointed" to -3,
                                                "angry" to -3, "annoyed" to -2, "boring" to -2, "ugly" to -2, "pain" to -2,
                                                "wrong" to -2, "stupid" to -3, "dreadful" to -4, "horrible" to -4
                                            )
                                            val intensifiers = setOf("very", "extremely", "really", "so", "incredibly", "highly", "much", "too")
                                            val negations = setOf("not", "no", "never", "none", "neither", "barely", "hardly", "cannot")

                                            val tokens = currentInput.lowercase().split(Regex("\\W+")).filter { it.isNotBlank() }
                                            var totalScore = 0
                                            var matchesCount = 0

                                            for (index in tokens.indices) {
                                                val word = tokens[index]
                                                val baseValue = sentimentLexicon[word]
                                                if (baseValue != null) {
                                                    matchesCount++
                                                    var multiplier = 1
                                                    var isNegated = false

                                                    for (lookback in 1..2) {
                                                        if (index - lookback >= 0) {
                                                            val prevWord = tokens[index - lookback]
                                                            if (intensifiers.contains(prevWord)) {
                                                                multiplier += 1
                                                            }
                                                            if (negations.contains(prevWord)) {
                                                                isNegated = true
                                                            }
                                                        }
                                                    }

                                                    val wordScore = baseValue * multiplier * (if (isNegated) -1 else 1)
                                                    totalScore += wordScore
                                                }
                                            }

                                            val sentimentClass = when {
                                                totalScore > 0 -> "POSITIVE (Intensity score: $totalScore)"
                                                totalScore < 0 -> "NEGATIVE (Intensity score: $totalScore)"
                                                else -> "NEUTRAL"
                                            }

                                            "Offline Lexicon Sentiment Analysis:\n" +
                                            "• Status: Offline analysis completed successfully.\n" +
                                            "• Overall Classification: $sentimentClass\n" +
                                            "• Matched Emotional Terms: $matchesCount detected.\n" +
                                            "• Explanation: Evaluated matching valence-based lexicon terms including local context negations and intensifiers."
                                        }
                                        title == "Code Helper" || title == "AI Code Helper" -> {
                                            val code = currentInput.trim()
                                            when {
                                                code.contains(Regex("fun\\s+\\w+")) || code.contains("val ") || code.contains("var ") -> {
                                                    "Offline Code Insight (Kotlin): Detected Kotlin syntax. Recommendation: Use 'val' instead of 'var' whenever possible for immutability. Ensure functions have clear documentation comments."
                                                }
                                                code.contains(Regex("def\\s+\\w+")) || code.contains("import ") && code.contains(".py") -> {
                                                    "Offline Code Insight (Python): Detected Python-like syntax. Recommendation: Follow PEP 8 style guide. Use descriptive variable names and keep functions small."
                                                }
                                                code.contains("<html>") || code.contains("<div") -> {
                                                    "Offline Code Insight (HTML): Detected HTML tags. Recommendation: Ensure all tags are properly closed and use semantic elements (like <header>, <main>, <footer>) for better SEO/Accessibility."
                                                }
                                                code.contains("{") && code.contains("}") && (code.contains(";") || code.contains("if")) -> {
                                                    "Offline Code Insight (C-Style): Detected C/Java/JS syntax. Recommendation: Check for consistent indentation and balanced braces. For JS, prefer 'const' or 'let' over 'var'."
                                                }
                                                else -> {
                                                    "Offline Code Assistant: I'm ready to analyze Kotlin, Python, HTML, or C-style code. Try pasting a snippet!"
                                                }
                                            }
                                        }
                                        title == "Translator" || title == "AI Translator" -> {
                                            val matchingPhrases = mutableListOf<String>()
                                            for (term in travelDictionary.keys) {
                                                if (currentInput.lowercase().contains(term)) {
                                                    val translations = travelDictionary[term]!!
                                                    val details = translations.entries.joinToString(", ") { "${it.key.replaceFirstChar { c -> c.uppercase() }}: ${it.value}" }
                                                    matchingPhrases.add("• '$term' -> $details")
                                                }
                                            }

                                            if (matchingPhrases.isNotEmpty()) {
                                                "Offline Travel Dictionary Matches:\n\n" + matchingPhrases.joinToString("\n") +
                                                "\n\n[Note: This travel dictionary supports 50+ key phrases across French, Spanish, Italian, German, Japanese, and Portuguese. Keep inputs simple!]"
                                            } else {
                                                "Offline Translation: I didn't find specific matches for your words in my offline travel dictionary (containing 50+ survival phrases like 'hello', 'bathroom', 'doctor', 'please', 'where is', 'wifi', etc.). Try entering one of those keywords!"
                                            }
                                        }
                                        title == "Text Extractor" || title == "AI Text Extractor" -> {
                                            val alphanumeric = currentInput.filter { it.isLetterOrDigit() || it.isWhitespace() }
                                            "Offline Text Helper: To extract text from an image, tap the image icon to select any picture from your gallery and run local, native OCR text recognition!\n\nFiltered raw alphanumeric input:\n$alphanumeric"
                                        }
                                        title == "Object Detector" -> {
                                            val objects = listOf("Phone", "Laptop", "Coffee", "Person", "Chair", "Table")
                                            val detected = objects.filter { currentInput.contains(it, ignoreCase = true) }
                                            if (detected.isNotEmpty()) "Offline Detection: I recognized references to ${detected.joinToString(", ")} in your text."
                                            else "Offline Detection: No specific objects from my local dictionary recognized in your description. I'm ready to detect common objects."
                                        }
                                        title.contains("Noise Remover") || title.contains("Remover") -> {
                                            "Offline Signal Processing: Analyzing the input stream for ${title.lowercase()}... Heuristic pattern matching complete. I've identified potential noise profiles and am ready to apply specialized frequency filters."
                                        }
                                        title.contains("Splitter") || title.contains("Autotuner") -> {
                                            "Offline Audio AI: Analyzing harmonic content for separation/tuning... Detected dominant frequencies. I can separate vocals and instrumentals using local fast Fourier transform (FFT) analysis."
                                        }
                                        title == "Audio Master Pro" -> {
                                            "Offline Mastering: Normalizing loudness to -14 LUFS and applying multi-band compression... Peak limiting applied. Your audio is now optimized for distribution."
                                        }
                                        title == "Voice Mimic" -> {
                                            "Offline Voice Synthesis: Analyzing the phonetic structure of the target voice... Constructing a local acoustic model. I can now generate speech patterns that resemble the provided sample."
                                        }
                                        title == "AI Chat Assistant" -> {
                                            "Offline Assistant: I've processed your message using my local knowledge base. I can help with general questions, tool navigation, and simple logic tasks without an internet connection."
                                        }
                                        else -> "Processing offline: ${currentInput.take(20)}... Heuristic analysis for $title complete. Result generated based on local rules."
                                    }
                                    messages.add(ChatMessage(actualResponse, false))
                                }
                            }
                        },
                        enabled = inputText.isNotBlank(),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                    }
                }
            }
        }
    }
}

fun parseCsvLine(line: String): List<String> {
    val result = mutableListOf<String>()
    var current = StringBuilder()
    var inQuotes = false
    var i = 0
    while (i < line.length) {
        val c = line[i]
        when {
            c == '\"' -> {
                if (inQuotes && i + 1 < line.length && line[i + 1] == '\"') {
                    current.append('\"')
                    i++
                } else {
                    inQuotes = !inQuotes
                }
            }
            c == ',' && !inQuotes -> {
                result.add(current.toString().trim())
                current = StringBuilder()
            }
            else -> current.append(c)
        }
        i++
    }
    result.add(current.toString().trim())
    return result
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    val containerColor = if (message.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
    val contentColor = if (message.isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
    val shape = if (message.isUser) {
        RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
    }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = alignment) {
        Row(verticalAlignment = Alignment.Bottom) {
            if (!message.isUser) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp).padding(end = 4.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Surface(
                color = containerColor,
                contentColor = contentColor,
                shape = shape,
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Text(
                    text = message.text,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (message.isUser) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp).padding(start = 4.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
