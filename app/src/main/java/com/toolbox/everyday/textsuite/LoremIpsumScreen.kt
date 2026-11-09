package com.toolbox.everyday.textsuite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toolbox.core.ui.copyToClipboard
import kotlin.random.Random

@Composable
fun LoremIpsumScreen() {
    val context = LocalContext.current
    var unitType by rememberSaveable { mutableIntStateOf(2) }
    var count by rememberSaveable { mutableFloatStateOf(3f) }
    var startWithStandard by rememberSaveable { mutableStateOf(true) }
    var generatedText by rememberSaveable { mutableStateOf("") }

    val wordsBank = listOf(
        "lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing", "elit",
        "sed", "do", "eiusmod", "tempor", "incididunt", "ut", "labore", "et", "dolore",
        "magna", "aliqua", "ut", "enim", "ad", "minim", "veniam", "quis", "nostrud",
        "exercitation", "ullamco", "laboris", "nisi", "ut", "aliquip", "ex", "ea",
        "commodo", "consequat", "duis", "aute", "irure", "dolor", "in", "reprehenderit",
        "in", "voluptate", "velit", "esse", "cillum", "dolore", "eu", "fugiat", "nulla",
        "pariatur", "excepteur", "sint", "occaecat", "cupidatat", "non", "proident",
        "sunt", "in", "culpa", "qui", "officia", "deserunt", "mollit", "anim", "id", "est", "laborum"
    )

    fun generateSentence(rng: Random): String {
        val len = rng.nextInt(6, 16)
        val sWords = (1..len).map { wordsBank.random(rng) }.toMutableList()
        sWords[0] = sWords[0].replaceFirstChar { it.uppercase() }
        return sWords.joinToString(" ") + "."
    }

    fun generateParagraph(rng: Random): String {
        val sCount = rng.nextInt(3, 7)
        return (1..sCount).joinToString(" ") { generateSentence(rng) }
    }

    fun generate(): String {
        val rng = Random(System.currentTimeMillis())
        val c = count.toInt()
        val standardPrefix = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
        return when (unitType) {
            0 -> {
                val list = mutableListOf<String>()
                if (startWithStandard && c >= 5) {
                    list.addAll(listOf("Lorem", "ipsum", "dolor", "sit", "amet"))
                    while (list.size < c) list.add(wordsBank.random(rng))
                } else {
                    for (i in 1..c) list.add(wordsBank.random(rng))
                }
                list.joinToString(" ")
            }
            1 -> {
                val sentences = mutableListOf<String>()
                if (startWithStandard) sentences.add(standardPrefix)
                while (sentences.size < c) sentences.add(generateSentence(rng))
                sentences.take(c).joinToString(" ")
            }
            else -> {
                val paragraphs = mutableListOf<String>()
                if (startWithStandard) paragraphs.add(standardPrefix + " " + generateSentence(rng) + " " + generateSentence(rng))
                while (paragraphs.size < c) paragraphs.add(generateParagraph(rng))
                paragraphs.take(c).joinToString("\n\n")
            }
        }
    }

    if (generatedText.isEmpty()) {
        generatedText = generate()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Generator Options", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    listOf("Words", "Sentences", "Paragraphs").forEachIndexed { index, label ->
                        FilterChip(
                            selected = unitType == index,
                            onClick = {
                                unitType = index
                                count = when (index) {
                                    0 -> 50f
                                    1 -> 5f
                                    else -> 3f
                                }
                            },
                            label = { Text(label) },
                        )
                    }
                }

                val maxSlider = when (unitType) {
                    0 -> 200f
                    1 -> 30f
                    else -> 10f
                }
                Text("Count: ${count.toInt()}")
                Slider(
                    value = count,
                    onValueChange = { count = it },
                    valueRange = 1f..maxSlider,
                    steps = (maxSlider - 2).toInt(),
                )

                FilterChip(
                    selected = startWithStandard,
                    onClick = { startWithStandard = !startWithStandard },
                    label = { Text("Start with 'Lorem ipsum...'") },
                )

                Button(
                    onClick = { generatedText = generate() },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Generate New Text")
                }
            }
        }

        OutlinedTextField(
            value = generatedText,
            onValueChange = { generatedText = it },
            label = { Text("Generated Lorem Ipsum") },
            modifier = Modifier.fillMaxWidth().height(220.dp),
        )

        OutlinedButton(
            onClick = { copyToClipboard(context, "Lorem Ipsum", generatedText) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Copy text")
        }
    }
}
