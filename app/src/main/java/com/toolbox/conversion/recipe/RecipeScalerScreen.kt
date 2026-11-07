package com.toolbox.conversion.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

data class RecipeIngredient(
    val name: String,
    val amount: Double,
    val unit: String,
)

object RecipeScalerEngine {
    fun calculateScaleFactor(originalServings: Double, targetServings: Double): Double {
        if (originalServings <= 0.0 || targetServings <= 0.0) return 1.0
        return targetServings / originalServings
    }

    fun scaleAmount(amount: Double, scaleFactor: Double): Double {
        return amount * scaleFactor
    }

    fun formatAmount(amount: Double): String {
        return if (amount % 1.0 == 0.0) {
            amount.toInt().toString()
        } else {
            String.format("%.2f", amount).trimEnd('0').trimEnd('.')
        }
    }
}

@Composable
fun RecipeScalerScreen() {
    var originalServingsText by remember { mutableStateOf("4") }
    var targetServingsText by remember { mutableStateOf("6") }

    val originalServings = originalServingsText.toDoubleOrNull() ?: 1.0
    val targetServings = targetServingsText.toDoubleOrNull() ?: 1.0
    val scaleFactor = RecipeScalerEngine.calculateScaleFactor(originalServings, targetServings)

    var newIngredientName by remember { mutableStateOf("") }
    var newIngredientAmount by remember { mutableStateOf("") }
    var newIngredientUnit by remember { mutableStateOf("g") }

    val ingredients = remember {
        mutableStateListOf(
            RecipeIngredient("All-Purpose Flour", 250.0, "g"),
            RecipeIngredient("Granulated Sugar", 100.0, "g"),
            RecipeIngredient("Whole Milk", 200.0, "ml"),
            RecipeIngredient("Eggs", 2.0, "pcs"),
            RecipeIngredient("Baking Powder", 1.5, "tsp"),
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Servings Scaler Header Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Servings & Multiplier",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        OutlinedTextField(
                            value = originalServingsText,
                            onValueChange = { originalServingsText = it },
                            label = { Text("Original Servings") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                        )
                        Text("➔", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        OutlinedTextField(
                            value = targetServingsText,
                            onValueChange = { targetServingsText = it },
                            label = { Text("Target Servings") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Scale Factor: ${String.format("%.2f", scaleFactor)}x",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }

        // Add Ingredient Section
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Add Ingredient",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        OutlinedTextField(
                            value = newIngredientName,
                            onValueChange = { newIngredientName = it },
                            label = { Text("Name") },
                            modifier = Modifier.weight(1.8f),
                            singleLine = true,
                        )
                        OutlinedTextField(
                            value = newIngredientAmount,
                            onValueChange = { newIngredientAmount = it },
                            label = { Text("Amount") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                        )
                        OutlinedTextField(
                            value = newIngredientUnit,
                            onValueChange = { newIngredientUnit = it },
                            label = { Text("Unit") },
                            modifier = Modifier.weight(0.9f),
                            singleLine = true,
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            val amt = newIngredientAmount.toDoubleOrNull() ?: 0.0
                            if (newIngredientName.isNotBlank() && amt > 0.0) {
                                ingredients.add(RecipeIngredient(newIngredientName.trim(), amt, newIngredientUnit.trim()))
                                newIngredientName = ""
                                newIngredientAmount = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add to Recipe")
                    }
                }
            }
        }

        // Ingredients Scaled List
        item {
            Text(
                text = "Scaled Recipe Ingredients (${ingredients.size})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
        }

        itemsIndexed(ingredients) { index, item ->
            IngredientRowItem(
                item = item,
                scaleFactor = scaleFactor,
                onDelete = { ingredients.removeAt(index) },
            )
        }
    }
}

@Composable
private fun IngredientRowItem(
    item: RecipeIngredient,
    scaleFactor: Double,
    onDelete: () -> Unit,
) {
    val scaledAmt = RecipeScalerEngine.scaleAmount(item.amount, scaleFactor)
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "Original: ${RecipeScalerEngine.formatAmount(item.amount)} ${item.unit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Text(
                        text = "${RecipeScalerEngine.formatAmount(scaledAmt)} ${item.unit}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.outlineVariant,
                    )
                }
            }
        }
    }
}
