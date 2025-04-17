package com.example.jaylabs.pastreports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jaylabs.models.Diagnosis
import com.example.jaylabs.models.ModelResponse
import kotlin.math.max // Ensure max is imported if not already

@Composable
fun ReportDetailScreen(
    data: String?
) {
    val response = parseModelResponse(data ?: "")
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Use theme background
            .padding(16.dp).padding(top = 100.dp)
            .verticalScroll(scrollState), // Make content scrollable if it overflows
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp) // Add space between main elements
    ) {
        Text(
            text = "Report Details",
            style = MaterialTheme.typography.headlineSmall, // Larger title
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (response != null) {
            // Card for Diagnosis Chart
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(12.dp), // Softer corners
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) // Subtle background
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Diagnosis Probability",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    DiagnosisBarChart(
                        melanoma = response.diagnosis.Melanoma.toFloat(),
                        nevus = response.diagnosis.Nevus.toFloat()
                    )
                }
            }

            // Card for Text Details
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // Standard surface color
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp) // Space between text items
                ) {
                    ProbabilityRow("Melanoma:", response.diagnosis.Melanoma)
                    ProbabilityRow("Nevus:", response.diagnosis.Nevus)

                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f) // Subtle divider
                    )

                    Text(
                        text = "Interpretation:",
                        style = MaterialTheme.typography.titleMedium, // Slightly larger heading
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    // Spacer(modifier = Modifier.height(4.dp)) // Reduced spacer, handled by Arrangement.spacedBy

                    Text(
                        text = response.interpretation,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 15.sp, // Slightly larger body text
                        lineHeight = 22.sp, // Improve readability
                        color = MaterialTheme.colorScheme.onSurfaceVariant // Slightly muted text color
                    )
                }
            }
        } else {
            // Enhanced Error Message Display
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp), // Give more space from top if error
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = "Invalid or missing report data.",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ProbabilityRow(label: String, value: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween, // Align label left, value right
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium, // Make label slightly bolder
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "%.2f".format(value),
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold, // Make value stand out
            color = MaterialTheme.colorScheme.primary // Use primary color for emphasis
        )
    }
}


// --- Bar Chart (Tweaked Spacing and Appearance) ---

@Composable
fun DiagnosisBarChart(melanoma: Float, nevus: Float) {
    val maxHeight = 180.dp // Slightly reduced max height to fit better in card
    val maxProb = maxOf(melanoma, nevus, 0.01f) // Ensure max is at least 0.01 to avoid tiny bars for zero probs

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(maxHeight + 48.dp) // Increased total height for labels/padding
            .padding(horizontal = 16.dp), // Add horizontal padding
        horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally), // More space between bars and center them
        verticalAlignment = Alignment.Bottom
    ) {
        // Using weights to allow bars to take up proportional space if needed,
        // but fixed width is also fine if preferred. Let's stick to fixed width for simplicity here.
        BarItem(
            label = "Melanoma",
            probability = melanoma,
            maxProbability = maxProb,
            barColor = MaterialTheme.colorScheme.primary, // Consistent color usage
            barWidth = 50.dp, // Slightly wider bars
            maxHeight = maxHeight
        )
        BarItem(
            label = "Nevus",
            probability = nevus,
            maxProbability = maxProb,
            barColor = MaterialTheme.colorScheme.secondary, // Consistent color usage
            barWidth = 50.dp,
            maxHeight = maxHeight
        )
    }
}

@Composable
fun BarItem(
    label: String,
    probability: Float,
    maxProbability: Float,
    barColor: Color,
    barWidth: Dp,
    maxHeight: Dp
) {
    // Calculate height fraction, ensuring it's between 0 and 1
    val heightFraction = (probability / maxProbability).coerceIn(0f, 1f)
    val barHeight = maxHeight * heightFraction

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.height(maxHeight + 48.dp) // Ensure column has enough height for bar + labels
    ) {
        Box(
            modifier = Modifier
                .width(barWidth)
                .height(barHeight)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)) // Round only top corners
                .background(barColor)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "%.2f".format(probability),
            fontSize = 13.sp, // Slightly larger probability text
            fontWeight = FontWeight.SemiBold, // Make value clearer
            color = barColor // Match text color to bar color for association
        )
    }
}


// --- Parsing Function (Unchanged) ---
fun parseModelResponse(input: String): ModelResponse? {
    // Basic sanity check for minimum length or key markers
    if (!input.contains("Diagnosis(") || !input.contains("interpretation=")) {
        return null
    }

    return try {
        // More robust parsing: Find indices carefully
        val diagnosisPrefix = "Diagnosis("
        val interpretationPrefix = "interpretation="

        val diagnosisStartIndex = input.indexOf(diagnosisPrefix)
        if (diagnosisStartIndex == -1) return null // Prefix not found

        val diagnosisEndIndex = input.indexOf(")", diagnosisStartIndex)
        if (diagnosisEndIndex == -1) return null // Closing parenthesis not found

        // Extract diagnosis string: "Melanoma=0.XXX, Nevus=0.YYY"
        val diagnosisStr = input.substring(diagnosisStartIndex + diagnosisPrefix.length, diagnosisEndIndex)

        // Use map or key-value parsing for more flexibility if order changes
        val parts = diagnosisStr.split(',').map { it.trim() }
        var melanomaValue: Double? = null
        var nevusValue: Double? = null

        for (part in parts) {
            val keyValue = part.split('=')
            if (keyValue.size == 2) {
                val key = keyValue[0].trim()
                val valueStr = keyValue[1].trim()
                when (key) {
                    "Melanoma" -> melanomaValue = valueStr.toDoubleOrNull()
                    "Nevus" -> nevusValue = valueStr.toDoubleOrNull()
                }
            }
        }

        // Ensure both values were found and parsed
        if (melanomaValue == null || nevusValue == null) return null

        val interpretationStartIndex = input.indexOf(interpretationPrefix, diagnosisEndIndex) // Search after diagnosis part
        if (interpretationStartIndex == -1) return null // Prefix not found

        // Interpretation is the rest of the string after the prefix
        val interpretation = input.substring(interpretationStartIndex + interpretationPrefix.length).trim()

        // Handle potential trailing ')' if the input format is ModelResponse(...)
        val finalInterpretation = if (interpretation.endsWith(")")) {
            interpretation.dropLast(1).trim()
        } else {
            interpretation
        }


        ModelResponse(
            diagnosis = Diagnosis(
                Melanoma = melanomaValue,
                Nevus = nevusValue
            ),
            interpretation = finalInterpretation
        )
    } catch (e: Exception) {
        // Log the exception in a real app: Log.e("ParseError", "Failed to parse: $input", e)
        null // Return null on any parsing error
    }
}


// --- Preview (Updated with Sample Data) ---
@Preview(showBackground = true)
@Composable
fun ReportDetailScreenPreview() {
    // Sample data string matching the expected format
    val sampleData = "ModelResponse(diagnosis=Diagnosis(Melanoma=0.85, Nevus=0.15), interpretation=High probability of Melanoma detected. Further investigation recommended.)"
    // Preview with valid data
    MaterialTheme { // Wrap preview in MaterialTheme
        ReportDetailScreen(data = sampleData)
    }
}

@Preview(showBackground = true)
@Composable
fun ReportDetailScreenErrorPreview() {
    // Preview with invalid data
    MaterialTheme { // Wrap preview in MaterialTheme
        ReportDetailScreen(data = "Invalid data string")
    }
}

//
// --- Dummy Models (Ensure these match your actual models) ---
// Keep these or ensure they match your project's models
// package com.example.jaylabs.models
//
// data class ModelResponse(
//     val diagnosis: Diagnosis,
//     val interpretation: String
// )
//
// data class Diagnosis(
//     val Melanoma: Double,
//     val Nevus: Double
// )
//sure