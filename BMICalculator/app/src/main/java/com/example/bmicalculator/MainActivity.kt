package com.example.bmicalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bmicalculator.ui.theme.BMICalculatorTheme
import kotlin.math.pow
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BMICalculatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Content(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

data class BMICategory(
    val category: String,
    val description: String,
    val color: Color
)

val bmiCategories = listOf(
    BMICategory("Underweight", "BMI less than 18.5", Color(0xFF64B5F6)),
    BMICategory("Normal", "BMI between 18.5 and 24.9", Color(0xFF81C784)),
    BMICategory("Overweight", "BMI between 25 and 29.9", Color(0xFFFFD54F)),
    BMICategory("Obese", "BMI 30 or greater", Color(0xFFE57373))
)

fun getBmiCategory(bmi: Float): BMICategory {
    return when {
        bmi < 18.5f -> bmiCategories[0]
        bmi < 25f -> bmiCategories[1]
        bmi < 30f -> bmiCategories[2]
        else -> bmiCategories[3]
    }
}

@Composable
fun Content(modifier: Modifier = Modifier) {
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var bmiResult by remember { mutableStateOf(0f) }
    var showResult by remember { mutableStateOf(false) }
    var heightError by remember { mutableStateOf(false) }
    var weightError by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val currentCategory = getBmiCategory(bmiResult)

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "BMI Calculator",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Enter Your Details",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Height Input
                    OutlinedTextField(
                        value = height,
                        onValueChange = {
                            if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                                height = it
                                heightError = false
                            }
                        },
                        label = { Text("Height (cm)") },
                        isError = heightError,
                        supportingText = {
                            if (heightError) {
                                Text(
                                    text = "Please enter a valid height",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 18.sp)
                    )

                    // Weight Input
                    OutlinedTextField(
                        value = weight,
                        onValueChange = {
                            if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                                weight = it
                                weightError = false
                            }
                        },
                        label = { Text("Weight (kg)") },
                        isError = weightError,
                        supportingText = {
                            if (weightError) {
                                Text(
                                    text = "Please enter a valid weight",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 18.sp)
                    )

                    Button(
                        onClick = {
                            // Validate inputs
                            val heightValue = height.toFloatOrNull()
                            val weightValue = weight.toFloatOrNull()

                            heightError = heightValue == null || heightValue <= 0
                            weightError = weightValue == null || weightValue <= 0

                            if (!heightError && !weightError) {
                                // Calculate BMI: weight (kg) / (height (m))²
                                val heightInMeters = heightValue!! / 100
                                bmiResult = (weightValue!! / heightInMeters.pow(2) * 10).roundToInt() / 10f
                                showResult = true
                                focusManager.clearFocus()
                            } else {
                                showResult = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "Calculate BMI",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }

            if (showResult) {
                BmiResultCard(bmiResult, currentCategory)
            }
        }
    }
}

@Composable
fun BmiResultCard(bmiResult: Float, category: BMICategory) {
    val animatedBmiValue by animateFloatAsState(
        targetValue = bmiResult,
        animationSpec = tween(durationMillis = 800),
        label = "BMI Animation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            category.color.copy(alpha = 0.2f),
                            category.color.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Your BMI Result",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(category.color.copy(alpha = 0.15f))
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = String.format("%.1f", animatedBmiValue),
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        color = category.color
                    )

                    Text(
                        text = category.category,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = category.color
                    )
                }
            }

            // Category Legend
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CategoryLegendItems(category)
            }
        }
    }
}

@Composable
fun CategoryLegendItems(currentCategory: BMICategory) {
    bmiCategories.forEach { category ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(category.color, CircleShape)
            )
            Text(
                text = category.category,
                fontSize = 12.sp,
                fontWeight = if (category.category == currentCategory.category)
                    FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContentPreview() {
    BMICalculatorTheme {
        Content()
    }
}