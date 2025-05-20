package com.example.loginpage

import androidx.compose.runtime.mutableStateOf

// Simple user repository to store user credentials
object UserRepository {
    // Pre-defined users (in a real app you would use a database or API)
    private val users = mapOf(
        "admin" to "password123",
        "mahasiswa" to "mahasiswa123",
        "dosen" to "dosen456"
    )

    // Login status state
    val isLoggedIn = mutableStateOf(false)
    val currentUser = mutableStateOf("")

    // Login function that validates credentials
    fun login(username: String, password: String): Boolean {
        val isValid = users[username] == password
        if (isValid) {
            isLoggedIn.value = true
            currentUser.value = username
        }
        return isValid
    }

    // Logout function
    fun logout() {
        isLoggedIn.value = false
        currentUser.value = ""
    }
}