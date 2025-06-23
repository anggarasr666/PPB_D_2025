package com.example.myhabits

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HabitRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("myhabits_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // User Management
    fun saveUser(user: User) {
        prefs.edit().putString("current_user", gson.toJson(user)).apply()
    }

    fun getCurrentUser(): User? {
        val userJson = prefs.getString("current_user", null)
        return if (userJson != null) {
            gson.fromJson(userJson, User::class.java)
        } else null
    }

    fun getUserByCredentials(email: String, password: String): User? {
        val users = getAllUsers()
        return users.find { it.email == email && it.password == password }
    }

    private fun isEmailExists(email: String): Boolean {
        val users = getAllUsers()
        return users.any { it.email == email }
    }

    fun registerUser(user: User): Boolean {
        if (isEmailExists(user.email)) return false

        val users = getAllUsers().toMutableList()
        users.add(user)
        saveAllUsers(users)
        return true
    }

    private fun getAllUsers(): List<User> {
        val usersJson = prefs.getString("all_users", "[]")
        val type = object : TypeToken<List<User>>() {}.type
        return gson.fromJson(usersJson, type) ?: emptyList()
    }

    private fun saveAllUsers(users: List<User>) {
        prefs.edit().putString("all_users", gson.toJson(users)).apply()
    }

    fun logout() {
        prefs.edit().remove("current_user").apply()
    }

    // Habit Management
    fun saveHabit(habit: Habit) {
        val habits = getAllHabits().toMutableList()
        val existingIndex = habits.indexOfFirst { it.id == habit.id }

        if (existingIndex >= 0) {
            habits[existingIndex] = habit
        } else {
            habits.add(habit)
        }

        saveAllHabits(habits)
    }

    private fun getAllHabits(): List<Habit> {
        val habitsJson = prefs.getString("habits", "[]")
        val type = object : TypeToken<List<Habit>>() {}.type
        return gson.fromJson(habitsJson, type) ?: emptyList()
    }

    private fun saveAllHabits(habits: List<Habit>) {
        prefs.edit().putString("habits", gson.toJson(habits)).apply()
    }

    fun deleteHabit(habitId: String) {
        val habits = getAllHabits().filter { it.id != habitId }
        saveAllHabits(habits)

        // Also delete all entries for this habit
        val entries = getAllEntries().filter { it.habitId != habitId }
        saveAllEntries(entries)
    }

    // Habit Entry Management
    private fun saveHabitEntry(entry: HabitEntry) {
        val entries = getAllEntries().toMutableList()
        val existingIndex = entries.indexOfFirst {
            it.habitId == entry.habitId && it.date == entry.date
        }

        if (existingIndex >= 0) {
            entries[existingIndex] = entry
        } else {
            entries.add(entry)
        }

        saveAllEntries(entries)
    }

    private fun getAllEntries(): List<HabitEntry> {
        val entriesJson = prefs.getString("habit_entries", "[]")
        val type = object : TypeToken<List<HabitEntry>>() {}.type
        return gson.fromJson(entriesJson, type) ?: emptyList()
    }

    private fun saveAllEntries(entries: List<HabitEntry>) {
        prefs.edit().putString("habit_entries", gson.toJson(entries)).apply()
    }

    private fun getEntriesForHabit(habitId: String): List<HabitEntry> {
        return getAllEntries().filter { it.habitId == habitId }
    }

    private fun getTodayEntry(habitId: String): HabitEntry? {
        val today = LocalDate.now().toString()
        return getAllEntries().find { it.habitId == habitId && it.date == today }
    }

    private fun getWeeklyEntries(habitId: String): List<HabitEntry> {
        val today = LocalDate.now()
        val weekStart = today.minusDays(6) // Last 7 days including today

        return getAllEntries().filter { entry ->
            entry.habitId == habitId &&
                    LocalDate.parse(entry.date) >= weekStart &&
                    LocalDate.parse(entry.date) <= today
        }
    }

    fun getHabitsWithEntries(): List<HabitWithEntries> {
        val habits = getAllHabits().filter { it.isActive }
        return habits.map { habit ->
            val todayEntry = getTodayEntry(habit.id)
            val weeklyEntries = getWeeklyEntries(habit.id)
            val allEntries = getEntriesForHabit(habit.id)

            HabitWithEntries(
                habit = habit,
                todayEntry = todayEntry,
                weeklyEntries = weeklyEntries,
                allEntries = allEntries
            )
        }
    }

    fun completeHabit(habitId: String) {
        val today = LocalDate.now().toString()
        val entry = HabitEntry(
            habitId = habitId,
            date = today,
            status = HabitStatus.COMPLETED,
            completedAt = today
        )
        saveHabitEntry(entry)
    }

    fun skipHabit(habitId: String) {
        val today = LocalDate.now().toString()
        val entry = HabitEntry(
            habitId = habitId,
            date = today,
            status = HabitStatus.SKIPPED
        )
        saveHabitEntry(entry)
    }

    // Helper methods
    fun getTodayHabits(): List<HabitWithEntries> {
        return getHabitsWithEntries().filter { habitWithEntries ->
            val habit = habitWithEntries.habit
            val today = LocalDate.now()

            when (habit.repeatType) {
                RepeatType.DAILY -> true
                RepeatType.WEEKLY -> {
                    val dayOfWeek = today.dayOfWeek.value // 1=Monday, 7=Sunday
                    habit.selectedDays.contains(dayOfWeek)
                }
                RepeatType.MONTHLY -> {
                    today.dayOfMonth == habit.monthlyDate
                }
            }
        }
    }
}