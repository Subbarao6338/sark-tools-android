package com.toolbox.conversion.gpa

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.toolbox.core.ui.components.TileSelector

data class CourseEntry(
    val name: String = "",
    val credits: String = "3",
    val gradePoint: String = "4.0",
)

@Composable
fun GpaCalculatorScreen() {
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val modes = listOf("Semester GPA", "Cumulative CGPA", "Target GPA Planner")

    Column(modifier = Modifier.fillMaxSize()) {
        TileSelector(
            options = modes,
            selectedIndex = selectedIndex,
            onSelectIndex = { selectedIndex = it },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            when (selectedIndex) {
                0 -> SemesterGpaCalculator()
                1 -> CumulativeCgpaCalculator()
                2 -> TargetGpaPlanner()
            }
        }
    }
}

@Composable
private fun SemesterGpaCalculator() {
    val courses = remember {
        mutableStateListOf(
            CourseEntry("Course 1", "4", "4.0"),
            CourseEntry("Course 2", "3", "3.7"),
            CourseEntry("Course 3", "3", "3.3"),
            CourseEntry("Course 4", "3", "3.0"),
        )
    }

    var totalCredits = 0.0
    var totalWeightedPoints = 0.0

    courses.forEach { course ->
        val cr = course.credits.toDoubleOrNull() ?: 0.0
        val gp = course.gradePoint.toDoubleOrNull() ?: 0.0
        totalCredits += cr
        totalWeightedPoints += cr * gp
    }

    val gpa = if (totalCredits > 0) totalWeightedPoints / totalCredits else 0.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Semester GPA Result", style = MaterialTheme.typography.titleSmall)
            Text(
                "%.2f / 4.00".format(gpa),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text("Total Credit Hours: %.1f | Quality Points: %.2f".format(totalCredits, totalWeightedPoints), fontFamily = FontFamily.Monospace)
        }
    }

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
                Text("Courses", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                OutlinedButton(onClick = { courses.add(CourseEntry("Course ${courses.size + 1}", "3", "4.0")) }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.padding(horizontal = 2.dp))
                    Text("Add Course")
                }
            }

            courses.forEachIndexed { index, course ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = course.name,
                        onValueChange = { courses[index] = course.copy(name = it) },
                        label = { Text("Course") },
                        modifier = Modifier.weight(1.4f),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = course.credits,
                        onValueChange = { courses[index] = course.copy(credits = it) },
                        label = { Text("Credits") },
                        modifier = Modifier.weight(0.9f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    OutlinedTextField(
                        value = course.gradePoint,
                        onValueChange = { courses[index] = course.copy(gradePoint = it) },
                        label = { Text("Points (0-4)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    if (courses.size > 1) {
                        IconButton(onClick = { courses.removeAt(index) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete course", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CumulativeCgpaCalculator() {
    var prevCgpaStr by rememberSaveable { mutableStateOf("3.50") }
    var prevCreditsStr by rememberSaveable { mutableStateOf("60") }
    var curGpaStr by rememberSaveable { mutableStateOf("3.80") }
    var curCreditsStr by rememberSaveable { mutableStateOf("15") }

    val prevCgpa = prevCgpaStr.toDoubleOrNull() ?: 0.0
    val prevCredits = prevCreditsStr.toDoubleOrNull() ?: 0.0
    val curGpa = curGpaStr.toDoubleOrNull() ?: 0.0
    val curCredits = curCreditsStr.toDoubleOrNull() ?: 0.0

    val totalCredits = prevCredits + curCredits
    val totalPoints = (prevCgpa * prevCredits) + (curGpa * curCredits)
    val newCgpa = if (totalCredits > 0) totalPoints / totalCredits else 0.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Cumulative CGPA Calculator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = prevCgpaStr, onValueChange = { prevCgpaStr = it }, label = { Text("Previous CGPA") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = prevCreditsStr, onValueChange = { prevCreditsStr = it }, label = { Text("Previous Credits") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = curGpaStr, onValueChange = { curGpaStr = it }, label = { Text("Current Term GPA") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = curCreditsStr, onValueChange = { curCreditsStr = it }, label = { Text("Current Term Credits") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Updated Cumulative CGPA", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            HorizontalDivider()

            Text("%.2f".format(newCgpa), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("Total Earned Credits: %.1f".format(totalCredits), fontFamily = FontFamily.Monospace)
            val delta = newCgpa - prevCgpa
            Text("CGPA Shift: %+.2f".format(delta), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun TargetGpaPlanner() {
    var curCgpaStr by rememberSaveable { mutableStateOf("3.20") }
    var completedCreditsStr by rememberSaveable { mutableStateOf("45") }
    var targetCgpaStr by rememberSaveable { mutableStateOf("3.50") }
    var upcomingCreditsStr by rememberSaveable { mutableStateOf("30") }

    val curCgpa = curCgpaStr.toDoubleOrNull() ?: 0.0
    val completedCr = completedCreditsStr.toDoubleOrNull() ?: 0.0
    val targetCgpa = targetCgpaStr.toDoubleOrNull() ?: 0.0
    val upcomingCr = upcomingCreditsStr.toDoubleOrNull() ?: 0.0

    // Target = (curCgpa * completedCr + requiredGpa * upcomingCr) / (completedCr + upcomingCr)
    // requiredGpa = (targetCgpa * (completedCr + upcomingCr) - curCgpa * completedCr) / upcomingCr
    val requiredGpa = if (upcomingCr > 0) {
        (targetCgpa * (completedCr + upcomingCr) - curCgpa * completedCr) / upcomingCr
    } else 0.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Target GPA Planner", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = curCgpaStr, onValueChange = { curCgpaStr = it }, label = { Text("Current CGPA") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = completedCreditsStr, onValueChange = { completedCreditsStr = it }, label = { Text("Completed Credits") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = targetCgpaStr, onValueChange = { targetCgpaStr = it }, label = { Text("Target Goal CGPA") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = upcomingCreditsStr, onValueChange = { upcomingCreditsStr = it }, label = { Text("Upcoming Credits") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Required Minimum Average in Upcoming Terms", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            HorizontalDivider()

            if (requiredGpa > 4.0) {
                Text(
                    "%.2f (Mathematically Unattainable on 4.0 Scale)".format(requiredGpa),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                )
                Text("Consider increasing your remaining credit horizon to achieve this target.")
            } else if (requiredGpa < 0.0) {
                Text("Target already surpassed! Minimum required is 0.00.", color = MaterialTheme.colorScheme.primary)
            } else {
                Text(
                    "%.2f / 4.00".format(requiredGpa),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text("Maintain an average of at least %.2f across the next %.1f credits to reach %.2f CGPA.".format(requiredGpa, upcomingCr, targetCgpa))
            }
        }
    }
}
