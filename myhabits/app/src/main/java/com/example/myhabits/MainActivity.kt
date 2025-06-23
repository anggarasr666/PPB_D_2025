package com.example.myhabits

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.myhabits.ui.theme.MyhabitsTheme

class MainActivity : ComponentActivity() {
    private lateinit var habitRepository: HabitRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        habitRepository = HabitRepository(this)

        setContent {
            MyhabitsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HabitApp(habitRepository = habitRepository)
                }
            }
        }
    }
}

@Composable
fun HabitApp(habitRepository: HabitRepository) {
    var currentUser by remember { mutableStateOf(habitRepository.getCurrentUser()) }
    var currentScreen by remember { mutableStateOf(if (currentUser != null) Screen.HABIT_LIST else Screen.LOGIN) }
    var habitToEdit by remember { mutableStateOf<Habit?>(null) }

    when (currentScreen) {
        Screen.LOGIN -> {
            LoginScreen(
                habitRepository = habitRepository,
                onLoginSuccess = { user ->
                    currentUser = user
                    currentScreen = Screen.HABIT_LIST
                }
            )
        }
        Screen.HABIT_LIST -> {
            HabitListScreen(
                habitRepository = habitRepository,
                onCreateHabit = {
                    habitToEdit = null
                    currentScreen = Screen.CREATE_HABIT
                },
                onEditHabit = { habit ->
                    habitToEdit = habit
                    currentScreen = Screen.CREATE_HABIT
                },
                onLogout = {
                    habitRepository.logout()
                    currentUser = null
                    currentScreen = Screen.LOGIN
                }
            )
        }
        Screen.CREATE_HABIT -> {
            CreateHabitScreen(
                habitRepository = habitRepository,
                habitToEdit = habitToEdit,
                onBack = {
                    currentScreen = Screen.HABIT_LIST
                },
                onSaved = {
                    currentScreen = Screen.HABIT_LIST
                }
            )
        }
    }
}

enum class Screen {
    LOGIN, HABIT_LIST, CREATE_HABIT
}