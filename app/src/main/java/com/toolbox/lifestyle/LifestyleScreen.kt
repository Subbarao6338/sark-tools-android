package com.toolbox.lifestyle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

object LifestyleLogic {
    fun convertShoeSize(usMenSize: Double): Map<String, String> {
        val usWomen = usMenSize + 1.5
        val uk = usMenSize - 0.5
        val eu = usMenSize + 33.0
        val cm = (usMenSize * 0.84) + 21.0
        return mapOf(
            "US Men" to String.format("%.1f", usMenSize),
            "US Women" to String.format("%.1f", usWomen),
            "UK" to String.format("%.1f", uk),
            "EU" to String.format("%.1f", eu),
            "CM / JP" to String.format("%.1f cm", cm)
        )
    }

    fun getClothingSizeChart(region: String): List<Pair<String, String>> {
        return when (region) {
            "US" -> listOf("XS" to "Chest 32-34 in", "S" to "Chest 35-37 in", "M" to "Chest 38-40 in", "L" to "Chest 41-43 in", "XL" to "Chest 44-46 in", "XXL" to "Chest 47-49 in")
            "EU" -> listOf("XS" to "Size 44 (84-88 cm)", "S" to "Size 46 (89-94 cm)", "M" to "Size 48 (95-101 cm)", "L" to "Size 50 (102-108 cm)", "XL" to "Size 52 (109-114 cm)")
            else -> listOf("S" to "36 / 38", "M" to "40 / 42", "L" to "44 / 46", "XL" to "48 / 50")
        }
    }
}

@Composable
fun LifestyleScreen(toolId: String = "fashion-guide") {
    var selectedTool by remember { mutableStateOf(toolId) }
    val tools = listOf(
        "fashion-guide" to "Fashion Styling",
        "clothes-guide" to "Clothes & Care",
        "traditional-guide" to "Ethnic & Traditional",
        "worldwide" to "World Fashion",
        "size-guide" to "Size Guide",
        "shoes" to "Footwear Guide",
        "accessories" to "Accessories"
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
            tools.drop(4).forEach { (id, name) ->
                FilterChip(selected = selectedTool == id, onClick = { selectedTool = id }, label = { Text(name) })
            }
        }

        when (selectedTool) {
            "shoes" -> ShoesGuideUi()
            "size-guide", "accessories" -> SizeGuideUi()
            "clothes-guide" -> ClothesCareUi()
            "traditional-guide" -> TraditionalFashionUi()
            "worldwide" -> WorldFashionUi()
            else -> FashionStylingUi()
        }
    }
}

@Composable
fun ShoesGuideUi() {
    var sizeInput by remember { mutableStateOf("9.0") }
    val size = sizeInput.toDoubleOrNull() ?: 9.0
    val converted = LifestyleLogic.convertShoeSize(size)

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("International Footwear Size Converter", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = sizeInput, onValueChange = { sizeInput = it }, label = { Text("US Men Size") }, modifier = Modifier.fillMaxWidth())
            Text("Equivalent International Sizes:", fontWeight = FontWeight.SemiBold)
            converted.forEach { (region, s) ->
                Text("$region: $s", fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun SizeGuideUi() {
    var selectedRegion by remember { mutableStateOf("US") }
    val chart = LifestyleLogic.getClothingSizeChart(selectedRegion)

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Body & Clothing Size Guide", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("US", "EU", "International").forEach { reg ->
                    FilterChip(selected = selectedRegion == reg, onClick = { selectedRegion = reg }, label = { Text(reg) })
                }
            }
            chart.forEach { (size, measure) ->
                Text("$size : $measure", fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
fun FashionStylingUi() {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Fashion Styling & Silhouette Guide", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Body Shape Styling Principles:", fontWeight = FontWeight.SemiBold)
            Text("• Hourglass: Highlight the natural waistline with wrap dresses and tailored blazers.")
            Text("• Rectangle: Add dimension with belts, layered jackets, and pleated trousers.")
            Text("• Pear / Triangle: Draw focus upward with structured shoulders, statement collars, and boatneck tops.")
            Text("• Inverted Triangle: Balance broader shoulders with A-line skirts and wide-leg pants.")
        }
    }
}

@Composable
fun ClothesCareUi() {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Fabric Care & Wardrobe Preservation", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("• Cotton & Linen: Wash in cold to warm water; steam or press while slightly damp.")
            Text("• Wool & Cashmere: Hand wash cold with gentle detergent; lay flat to dry, never wring.")
            Text("• Silk: Dry clean or delicate cold wash; store on padded hangers away from sunlight.")
            Text("• Synthetics (Polyester/Nylon): Wash warm; tumble dry on low to prevent fiber breakdown.")
        }
    }
}

@Composable
fun TraditionalFashionUi() {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Traditional & Ethnic Attire Guide", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("• Saree & Draping: Traditional Indian ethnic garment available in Kanjeevaram, Banarasi, and Chanderi weaves.")
            Text("• Kurta & Dhoti / Pyjama: Versatile celebratory wear paired with Nehru jackets or stoles.")
            Text("• Kimono & Yukata: Traditional Japanese robes with obi sashes for seasonal and festive ceremonies.")
            Text("• Hanbok: Vibrant Korean traditional dress with jeogori (jacket) and chima (skirt).")
        }
    }
}

@Composable
fun WorldFashionUi() {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("World Fashion & Cultural Attire Overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("• Europe: Parisian chic minimalism, Italian precision tailoring, Scandinavian functional aesthetics.")
            Text("• Africa: Dashiki, Kente cloth, Ankara wax prints celebrating heritage and storytelling.")
            Text("• Middle East: Flowing thobes, kaftans, and abayas combining modesty with haute couture.")
            Text("• Latin America: Ponchos, Guayaberas, and embroidered huipils made from natural handwoven fibers.")
        }
    }
}
