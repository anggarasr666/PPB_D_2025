package com.example.myhabits

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CreateHabitScreen(
    habitRepository: HabitRepository,
    habitToEdit: Habit?,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    var title by remember { mutableStateOf(habitToEdit?.title ?: "") }
    var description by remember { mutableStateOf(habitToEdit?.description ?: "") }
    var selectedColor by remember { mutableStateOf(Color(habitToEdit?.color ?: Color.Blue.toArgb())) }
    var isRepeating by remember { mutableStateOf(habitToEdit?.isRepeating ?: true) }
    var repeatType by remember { mutableStateOf(habitToEdit?.repeatType ?: RepeatType.DAILY) }
    var selectedDays by remember { mutableStateOf(habitToEdit?.selectedDays ?: listOf(1,2,3,4,5,6,7)) }
    var monthlyDate by remember { mutableStateOf(habitToEdit?.monthlyDate ?: 18) }
    var hasReminder by remember { mutableStateOf(habitToEdit?.hasReminder ?: false) }
    var hasGoal by remember { mutableStateOf(habitToEdit?.hasGoal ?: false) }
    var routine by remember { mutableStateOf(habitToEdit?.routine ?: "") }

    val isEditMode = habitToEdit != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                if (isEditMode) "Edit Habit" else "Create Habit",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Title Field
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Description Field
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 3,
                shape = RoundedCornerShape(12.dp)
            )

            // Color Picker
            ColorPickerSection(
                selectedColor = selectedColor,
                onColorSelected = { selectedColor = it }
            )

            // Repeat Toggle
            RepeatSection(
                isRepeating = isRepeating,
                onRepeatToggle = { isRepeating = it },
                repeatType = repeatType,
                onRepeatTypeChange = { repeatType = it }
            )

            // Frequency based on repeat type
            when (repeatType) {
                RepeatType.DAILY -> {
                    FrequencySection()
                }
                RepeatType.WEEKLY -> {
                    DaySelectionSection(
                        selectedDays = selectedDays,
                        onDaysChanged = { selectedDays = it }
                    )
                }
                RepeatType.MONTHLY -> {
                    MonthlyDateSection(
                        selectedDate = monthlyDate,
                        onDateChanged = { monthlyDate = it }
                    )
                }
            }

            // Reminder Toggle
            ToggleSection(
                title = "Reminder",
                isEnabled = hasReminder,
                onToggle = { hasReminder = it }
            )

            // Goal Toggle
            ToggleSection(
                title = "Goal",
                isEnabled = hasGoal,
                onToggle = { hasGoal = it }
            )

            // Routine Selection
            RoutineSection(
                routine = routine,
                onRoutineChange = { routine = it }
            )

            // Save Button
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val habit = if (isEditMode) {
                            habitToEdit!!.copy(
                                title = title,
                                description = description,
                                color = selectedColor.toArgb(),
                                isRepeating = isRepeating,
                                repeatType = repeatType,
                                selectedDays = selectedDays,
                                monthlyDate = monthlyDate,
                                hasReminder = hasReminder,
                                hasGoal = hasGoal,
                                routine = routine
                            )
                        } else {
                            Habit(
                                title = title,
                                description = description,
                                color = selectedColor.toArgb(),
                                isRepeating = isRepeating,
                                repeatType = repeatType,
                                selectedDays = selectedDays,
                                monthlyDate = monthlyDate,
                                hasReminder = hasReminder,
                                hasGoal = hasGoal,
                                routine = routine
                            )
                        }

                        habitRepository.saveHabit(habit)
                        onSaved()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = title.isNotBlank()
            ) {
                Text(
                    "Save",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Delete button for edit mode
            if (isEditMode) {
                OutlinedButton(
                    onClick = {
                        habitRepository.deleteHabit(habitToEdit!!.id)
                        onSaved()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(
                        "Delete Habit",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun ColorPickerSection(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    val colors = listOf(
        Color(0xFF6C63FF), Color(0xFF3B82F6), Color(0xFF10B981),
        Color(0xFFF59E0B), Color(0xFFEF4444), Color(0xFF8B5CF6),
        Color(0xFFEC4899), Color(0xFF06B6D4), Color(0xFF84CC16),
        Color(0xFFF97316), Color(0xFF6366F1), Color(0xFF14B8A6)
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Color",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(selectedColor)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(80.dp)
        ) {
            items(colors) { color ->
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = if (selectedColor == color) 3.dp else 0.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                        .clickable { onColorSelected(color) }
                )
            }
        }
    }
}

@Composable
fun RepeatSection(
    isRepeating: Boolean,
    onRepeatToggle: (Boolean) -> Unit,
    repeatType: RepeatType,
    onRepeatTypeChange: (RepeatType) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Repeat",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Switch(
                checked = isRepeating,
                onCheckedChange = onRepeatToggle
            )
        }

        if (isRepeating) {
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(RepeatType.values()) { type ->
                    val isSelected = repeatType == type
                    Button(
                        onClick = { onRepeatTypeChange(type) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            when (type) {
                                RepeatType.DAILY -> "Daily"
                                RepeatType.WEEKLY -> "Weekly"
                                RepeatType.MONTHLY -> "Monthly"
                            },
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FrequencySection() {
    Column {
        Text(
            "Frequency",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            "Everyday",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun DaySelectionSection(
    selectedDays: List<Int>,
    onDaysChanged: (List<Int>) -> Unit
) {
    Column {
        Text(
            "On these days",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(12.dp))

        val dayNames = listOf("M", "T", "W", "T", "F", "S", "S")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            dayNames.forEachIndexed { index, day ->
                val dayNumber = index + 1
                val isSelected = selectedDays.contains(dayNumber)

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable {
                            val newDays = if (isSelected) {
                                selectedDays - dayNumber
                            } else {
                                selectedDays + dayNumber
                            }
                            onDaysChanged(newDays)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        day,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun MonthlyDateSection(
    selectedDate: Int,
    onDateChanged: (Int) -> Unit
) {
    Column {
        Text(
            "Every month on $selectedDate",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(200.dp)
        ) {
            items((1..31).toList()) { date ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .background(
                            if (selectedDate == date) MaterialTheme.colorScheme.primary
                            else Color.Transparent
                        )
                        .clickable { onDateChanged(date) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        date.toString(),
                        color = if (selectedDate == date) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (selectedDate == date) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun ToggleSection(
    title: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle
        )
    }
}

@Composable
fun RoutineSection(
    routine: String,
    onRoutineChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Routine",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        TextButton(
            onClick = { /* Open routine selection */ }
        ) {
            Text(
                if (routine.isNotEmpty()) routine else "Select",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}