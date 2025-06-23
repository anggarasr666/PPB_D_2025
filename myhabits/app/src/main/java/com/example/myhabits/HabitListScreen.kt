package com.example.myhabits

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class HabitFilter {
    ALL, COMPLETED, SKIPPED
}

@Composable
fun HabitListScreen(
    habitRepository: HabitRepository,
    onCreateHabit: () -> Unit,
    onEditHabit: (Habit) -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var habitsWithEntries by remember { mutableStateOf(emptyList<HabitWithEntries>()) }
    var showMenu by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf(HabitFilter.ALL) }

    LaunchedEffect(Unit) {
        habitsWithEntries = habitRepository.getHabitsWithEntries()
    }

    val refreshHabits = {
        habitsWithEntries = habitRepository.getHabitsWithEntries()
    }

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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Habits",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )

            Row {
                // Filter button - only show on Today tab
                if (selectedTab == 0) {
                    Box {
                        IconButton(onClick = { showFilterMenu = !showFilterMenu }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filter")
                        }

                        DropdownMenu(
                            expanded = showFilterMenu,
                            onDismissRequest = { showFilterMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All") },
                                onClick = {
                                    selectedFilter = HabitFilter.ALL
                                    showFilterMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Completed") },
                                onClick = {
                                    selectedFilter = HabitFilter.COMPLETED
                                    showFilterMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Skipped") },
                                onClick = {
                                    selectedFilter = HabitFilter.SKIPPED
                                    showFilterMenu = false
                                }
                            )
                        }
                    }
                }

                IconButton(onClick = onCreateHabit) {
                    Icon(Icons.Default.Add, contentDescription = "Add habit")
                }

                Box {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                showMenu = false
                                onLogout()
                            }
                        )
                    }
                }
            }
        }

        // Simple Tab Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Today", "Weekly", "Overall").forEachIndexed { index, title ->
                Button(
                    onClick = {
                        selectedTab = index
                        selectedFilter = HabitFilter.ALL
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (selectedTab == index) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        title,
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            0 -> TodayView(habitsWithEntries, habitRepository, refreshHabits, onEditHabit, selectedFilter, onCreateHabit)
            1 -> WeeklyView(habitsWithEntries, onEditHabit)
            2 -> MonthlyView(habitsWithEntries, onEditHabit)
        }
    }
}

@Composable
fun TodayView(
    habitsWithEntries: List<HabitWithEntries>,
    habitRepository: HabitRepository,
    refreshHabits: () -> Unit,
    onEditHabit: (Habit) -> Unit,
    selectedFilter: HabitFilter,
    onCreateHabit: () -> Unit
) {
    val todayHabits = remember(habitsWithEntries) { habitRepository.getTodayHabits() }

    // Apply filter
    val filteredHabits = remember(todayHabits, selectedFilter) {
        when (selectedFilter) {
            HabitFilter.ALL -> todayHabits
            HabitFilter.COMPLETED -> todayHabits.filter { it.isCompletedToday }
            HabitFilter.SKIPPED -> todayHabits.filter { it.isSkippedToday }
        }
    }

    if (todayHabits.isEmpty()) {
        EmptyState(
            icon = Icons.Default.SelfImprovement,
            title = "No habits for today",
            subtitle = "There is no habit for today. Create one?",
            actionText = "Create"
        ) { onCreateHabit() }
    } else {
        val completedCount = todayHabits.count { it.isCompletedToday }
        val skippedCount = todayHabits.count { it.isSkippedToday }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Only show progress card if filter is ALL
            if (selectedFilter == HabitFilter.ALL) {
                item {
                    ProgressCard(completedCount, skippedCount, todayHabits.size)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            items(filteredHabits) { habitWithEntries ->
                SimpleHabitCard(
                    habitWithEntries = habitWithEntries,
                    onComplete = {
                        habitRepository.completeHabit(habitWithEntries.habit.id)
                        refreshHabits()
                    },
                    onSkip = {
                        habitRepository.skipHabit(habitWithEntries.habit.id)
                        refreshHabits()
                    },
                    onEdit = { onEditHabit(habitWithEntries.habit) }
                )
            }

            if (filteredHabits.isEmpty() && selectedFilter != HabitFilter.ALL) {
                item {
                    EmptyFilterState(selectedFilter)
                }
            }
        }
    }
}

@Composable
fun WeeklyView(
    habitsWithEntries: List<HabitWithEntries>,
    onEditHabit: (Habit) -> Unit
) {
    // Filter untuk habits yang bukan monthly
    val nonMonthlyHabits = habitsWithEntries.filter {
        it.habit.repeatType != RepeatType.MONTHLY
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (nonMonthlyHabits.isEmpty()) {
            item {
                EmptyWeeklyState()
            }
        } else {
            items(nonMonthlyHabits) { habitWithEntries ->
                WeeklyHabitCard(habitWithEntries, onEditHabit)
            }
        }
    }
}

@Composable
fun EmptyWeeklyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.DateRange,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color(0xFF6C63FF).copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No weekly habits",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Create a daily or weekly habit to see it here",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun WeeklyHabitCard(
    habitWithEntries: HabitWithEntries,
    onEditHabit: (Habit) -> Unit
) {
    val habit = habitWithEntries.habit
    val scheduleText = when (habit.repeatType) {
        RepeatType.DAILY -> "Everyday"
        RepeatType.WEEKLY -> {
            val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            val selectedDayNames = habit.selectedDays.sorted().map { dayNames[it - 1] }
            if (selectedDayNames.size == 7) "Everyday"
            else selectedDayNames.joinToString(", ")
        }
        RepeatType.MONTHLY -> "Monthly (${habit.monthlyDate})"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onEditHabit(habit) }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color(habit.color))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = habit.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = scheduleText,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun MonthlyView(
    habitsWithEntries: List<HabitWithEntries>,
    onEditHabit: (Habit) -> Unit
) {
    // Filter untuk monthly habits saja
    val monthlyHabits = habitsWithEntries.filter {
        it.habit.repeatType == RepeatType.MONTHLY
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (monthlyHabits.isEmpty()) {
            item {
                EmptyMonthlyState()
            }
        } else {
            items(monthlyHabits) { habitWithEntries ->
                MonthlyHabitCard(habitWithEntries, onEditHabit)
            }
        }
    }
}

@Composable
fun MonthlyHabitCard(
    habitWithEntries: HabitWithEntries,
    onEditHabit: (Habit) -> Unit
) {
    val habit = habitWithEntries.habit

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onEditHabit(habit) }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color(habit.color))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = habit.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Every month on day ${habit.monthlyDate}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EmptyMonthlyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.CalendarMonth,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color(0xFF6C63FF).copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No monthly habits",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Create a monthly habit to see it here",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SimpleHabitCard(
    habitWithEntries: HabitWithEntries,
    onComplete: () -> Unit,
    onSkip: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onEdit
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color(habitWithEntries.habit.color))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = habitWithEntries.habit.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                if (habitWithEntries.habit.description.isNotEmpty()) {
                    Text(
                        text = habitWithEntries.habit.description,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Always show buttons, but change behavior based on status
            Row {
                Button(
                    onClick = onComplete,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (habitWithEntries.isCompletedToday)
                            Color(0xFF4CAF50) else Color(0xFF4CAF50).copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.size(40.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Complete",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onSkip,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (habitWithEntries.isSkippedToday)
                            Color(0xFFFF9800) else Color(0xFFFF9800).copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.size(40.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        Icons.Default.SkipNext,
                        contentDescription = "Skip",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BasicHabitCard(
    habitWithEntries: HabitWithEntries,
    onEditHabit: (Habit) -> Unit,
    statusText: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onEditHabit(habitWithEntries.habit) }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color(habitWithEntries.habit.color))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = habitWithEntries.habit.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = statusText,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ProgressCard(completed: Int, skipped: Int, total: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "$completed completed",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "$skipped skipped",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Simple progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White.copy(alpha = 0.3f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(if (total > 0) completed.toFloat() / total else 0f)
                        .fillMaxHeight()
                        .background(Color(0xFF4CAF50))
                )
            }
        }
    }
}

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String,
    actionText: String,
    onAction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = Color(0xFF6C63FF).copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subtitle,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onAction,
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(actionText)
        }
    }
}

@Composable
fun EmptyFilterState(filter: HabitFilter) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            when (filter) {
                HabitFilter.COMPLETED -> Icons.Default.CheckCircle
                HabitFilter.SKIPPED -> Icons.Default.Cancel
                else -> Icons.Default.FilterList
            },
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color(0xFF6C63FF).copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = when (filter) {
                HabitFilter.COMPLETED -> "No completed habits today"
                HabitFilter.SKIPPED -> "No skipped habits today"
                else -> "No habits found"
            },
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}