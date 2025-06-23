package com.example.myhabits

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class User(
    val id: String = UUID.randomUUID().toString(),
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val createdAt: String = LocalDateTime.now().toString()
)

data class Habit(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val color: Int = Color.Blue.toArgb(),
    val isRepeating: Boolean = true,
    val repeatType: RepeatType = RepeatType.DAILY,
    val selectedDays: List<Int> = listOf(1,2,3,4,5,6,7), // 1=Monday, 7=Sunday
    val monthlyDate: Int = 1, // for monthly habits
    val hasReminder: Boolean = false,
    val hasGoal: Boolean = false,
    val goalTarget: Int = 1,
    val routine: String = "",
    val createdAt: String = LocalDate.now().toString(),
    val isActive: Boolean = true
)

enum class RepeatType {
    DAILY, WEEKLY, MONTHLY
}

data class HabitEntry(
    val id: String = UUID.randomUUID().toString(),
    val habitId: String = "",
    val date: String = LocalDate.now().toString(),
    val status: HabitStatus = HabitStatus.PENDING,
    val completedAt: String? = null
)

enum class HabitStatus {
    PENDING, COMPLETED, SKIPPED
}

data class HabitWithEntries(
    val habit: Habit,
    val todayEntry: HabitEntry?,
    val weeklyEntries: List<HabitEntry> = emptyList(),
    val allEntries: List<HabitEntry> = emptyList()
) {
    // Utility properties for completion rates
    fun getCompletionRate(): Float {
        val totalDays = allEntries.size
        val completedDays = allEntries.count { it.status == HabitStatus.COMPLETED }
        return if (totalDays > 0) completedDays.toFloat() / totalDays else 0f
    }

    fun getWeeklyCompletionRate(): Float {
        val totalDays = weeklyEntries.size
        val completedDays = weeklyEntries.count { it.status == HabitStatus.COMPLETED }
        return if (totalDays > 0) completedDays.toFloat() / totalDays else 0f
    }

    // Helper properties for display
    val isCompletedToday: Boolean
        get() = todayEntry?.status == HabitStatus.COMPLETED

    val isSkippedToday: Boolean
        get() = todayEntry?.status == HabitStatus.SKIPPED

    val hasTodayEntry: Boolean
        get() = todayEntry != null

    val weeklyProgress: String
        get() {
            val completed = weeklyEntries.count { it.status == HabitStatus.COMPLETED }
            val total = weeklyEntries.size
            return "$completed/$total"
        }

    val overallProgress: String
        get() {
            val completed = allEntries.count { it.status == HabitStatus.COMPLETED }
            val total = allEntries.size
            return "$completed/$total"
        }
}