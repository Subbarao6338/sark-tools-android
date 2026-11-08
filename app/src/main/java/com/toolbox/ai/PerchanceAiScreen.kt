package com.toolbox.ai

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toolbox.core.ui.components.TileSelector
import com.toolbox.core.ui.copyToClipboard
import kotlin.random.Random

object PerchanceGeneratorEngine {

    private val adjectives = listOf(
        "luminescent", "ephemeral", "cybernetic", "melancholy", "astral", "clandestine",
        "serendipitous", "enigmatic", "hyper-detailed", "iridescent", "sublime", "ancient",
        "quantum", "ethereal", "clandestine", "celestial", "dystopian", "mythic", "fractal"
    )

    private val characters = listOf(
        "a rogue quantum physicist fleeing an alternate timeline",
        "a cybernetically enhanced courier carrying an encrypted neural seed",
        "an interstellar archivist cataloging extinct alien languages",
        "a Victorian clockmaker who accidentally constructed an AI automaton",
        "a lone wanderer navigating the neon ruins of Old Tokyo",
        "a celestial botanist cultivating bioluminescent zero-g flora",
        "an eccentric digital archaeologist mining forgotten 20th-century memories"
    )

    private val motivations = listOf(
        "to restore the fractured remnants of humanity's collective subconscious",
        "to settle a personal vendetta against an all-seeing megacorporation",
        "to decipher a radio signal transmitted from inside a singularity",
        "to protect the last sentient organic ecosystem from planetary strip-mining",
        "to reclaim an ancient heirloom imbued with non-Euclidean geometries"
    )

    private val plotTwists = listOf(
        "The primary transmission was never sent from deep space; it was recorded 300 years ago beneath the Arctic ice shelf.",
        "The trusted AI navigation guide was quietly executing a forgotten decommission protocol the entire journey.",
        "The enemy they hunted across star clusters was their own biological clone from a divergent reality.",
        "The encrypted vault contained no weapon, but an untouched seed vault and a message of forgiveness.",
        "The city’s power grid was not powered by nuclear fusion, but by the dreaming minds of stasis passengers."
    )

    private val artStyles = listOf(
        "Cinematic 35mm photograph, chiaroscuro lighting, anamorphic lens flare, award-winning national geographic style, 8k resolution, photorealistic",
        "Studio Ghibli aesthetic, lush hand-painted watercolor skies, vibrant blooming moss, nostalgic golden hour atmosphere",
        "Retro-futuristic synthwave illustration, neon magenta and turquoise tones, glowing wireframe horizon, highly stylized",
        "Hyper-detailed dark fantasy concept art, Unreal Engine 5 render, volumetrical fog, intricate golden filigree armor",
        "Minimalist Bauhaus line art, bold primary geometric accents, crisp clean negative space, museum lithograph"
    )

    private val codingPrompts = listOf(
        "Write an idiomatic Kotlin Coroutines flow processor with exponential backoff retry and Mutex-guarded state cache.",
        "Design a high-performance offline zero-allocation circular audio ring buffer in Kotlin for real-time FFT visualization.",
        "Implement a custom Jetpack Compose Layout that arranges children into an interactive responsive hexagonal grid.",
        "Write a robust finite state machine (FSM) in pure Kotlin to parse and evaluate mathematical infix expressions.",
        "Design a lightweight SQLite / Room migration pipeline with automated JSON backup and cryptographic checksums."
    )

    fun generateCreativeBackstory(): String {
        val hero = characters.random()
        val motive = motivations.random()
        val trait = adjectives.random()
        val twist = plotTwists.random()
        return "Protagonist: $hero possessing a $trait aura.\n\nCore Objective: Seeking $motive.\n\nNarrative Twist: $twist"
    }

    fun generateAiPhotoPrompt(): String {
        val subject = listOf(
            "An ancient moss-covered guardian golem sitting beside an Alpine mountain lake",
            "A bustling futuristic night market in Neo-Seoul illuminated by holographic lanterns",
            "A cosmic library floating within a kaleidoscopic planetary nebula",
            "An astronaut kneeling before a colossal glowing crystalline obelisk on an alien desert",
            "A cozy steampunk café inside the hollowed clock tower of an airship armada"
        ).random()
        val style = artStyles.random()
        return "Prompt: $subject.\n\nStyle & Aesthetics: $style.\nNegative Prompt: blurry, deformed, lowres, watermark, oversaturated, text artifact."
    }

    fun generateStoryHook(): String {
        val hook = listOf(
            "The morning broadcast announced that the sun had set 14 minutes ahead of schedule.",
            "Everyone in the district woke up today remembering a stranger's childhood as their own.",
            "The satellite imagery showed a labyrinth carved into the seabed that wasn't there yesterday.",
            "The emergency siren sounded across the orbital station, followed immediately by silence.",
            "The antique telegraph on the museum shelf began clicking in modern Morse code."
        ).random()
        val complication = listOf(
            "and the authorities are strictly censoring all private radio frequencies.",
            "while atmospheric pressure sensors began dropping to impossible readings.",
            "as local compasses began spinning continuously toward the horizon.",
            "and all digital clocks in the building began counting backward from 72:00:00."
        ).random()
        return "$hook $complication"
    }

    fun generateCodingChallenge(): String {
        return codingPrompts.random()
    }
}

@Composable
fun PerchanceAiScreen() {
    val context = LocalContext.current
    var selectedCategory by rememberSaveable { mutableIntStateOf(0) }
    val categories = listOf("Character & Backstory", "AI Image Prompts", "Story Hooks", "Code Prompts", "Perchance Web Hub")

    var generatedText by rememberSaveable { mutableStateOf(PerchanceGeneratorEngine.generateCreativeBackstory()) }

    fun refreshGeneration(cat: Int) {
        generatedText = when (cat) {
            0 -> PerchanceGeneratorEngine.generateCreativeBackstory()
            1 -> PerchanceGeneratorEngine.generateAiPhotoPrompt()
            2 -> PerchanceGeneratorEngine.generateStoryHook()
            3 -> PerchanceGeneratorEngine.generateCodingChallenge()
            else -> generatedText
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TileSelector(
            options = categories,
            selectedIndex = selectedCategory,
            onSelectIndex = {
                selectedCategory = it
                if (it < 4) refreshGeneration(it)
            },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (selectedCategory < 4) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Text("Perchance Procedural Engine", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                            IconButton(onClick = { copyToClipboard(context, "Perchance AI Output", generatedText) }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                            }
                        }

                        HorizontalDivider()

                        Text(
                            text = generatedText,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 24.sp,
                            fontFamily = if (selectedCategory == 3) FontFamily.Monospace else FontFamily.Default,
                            color = MaterialTheme.colorScheme.onSurface,
                        )

                        Spacer(Modifier.height(4.dp))

                        Button(
                            onClick = { refreshGeneration(selectedCategory) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(Modifier.padding(horizontal = 4.dp))
                            Text("Regenerate Output")
                        }
                    }
                }
            } else {
                // Perchance Web Hub
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Perchance AI Online Services", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            "Launch official Perchance AI cloud generators directly in your browser. All external services run safely with zero app telemetry.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        PerchanceHubButton(
                            title = "Perchance AI Text Generator",
                            subtitle = "Interactive neural text completion & story sandbox",
                            url = "https://perchance.org/ai-text-generator",
                            onLaunch = { launchUrl(context, it) },
                        )

                        PerchanceHubButton(
                            title = "Perchance AI Photo Generator",
                            subtitle = "Free text-to-image synthesis powered by Stable Diffusion",
                            url = "https://perchance.org/ai-photo-generator",
                            onLaunch = { launchUrl(context, it) },
                        )

                        PerchanceHubButton(
                            title = "Perchance AI Chat",
                            subtitle = "Conversational AI character playground",
                            url = "https://perchance.org/ai-chat",
                            onLaunch = { launchUrl(context, it) },
                        )

                        PerchanceHubButton(
                            title = "Perchance Generator Studio",
                            subtitle = "Community repository of 10,000+ procedural generators",
                            url = "https://perchance.org/welcome",
                            onLaunch = { launchUrl(context, it) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PerchanceHubButton(
    title: String,
    subtitle: String,
    url: String,
    onLaunch: (String) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyLarge)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            OutlinedButton(onClick = { onLaunch(url) }) {
                Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.padding(horizontal = 2.dp))
                Text("Open")
            }
        }
    }
}

private fun launchUrl(context: android.content.Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (_: Exception) {
    }
}
