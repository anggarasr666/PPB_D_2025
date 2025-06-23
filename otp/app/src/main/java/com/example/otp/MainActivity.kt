package com.example.otp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.otp.data.*
import com.example.otp.ui.screens.*
import com.example.otp.ui.theme.OtpTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OtpTheme {
                OTPApp()
            }
        }
    }
}

@Composable
fun OTPApp() {
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Login) }
    var registerData by remember { mutableStateOf(RegisterData()) }
    var personalData by remember { mutableStateOf(PersonalData()) }
    var user by remember { mutableStateOf(User()) }
    var currentOTP by remember { mutableStateOf("") }
    var isFromLogin by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Fungsi generate OTP sederhana
    fun generateOTP(): String {
        return (100000..999999).random().toString()
    }

    // Fungsi showToast
    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    // Fungsi showSnackbar
    fun showSnackbar(message: String) {
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    // Fungsi untuk demo - tampilkan OTP di console dan temporary toast
    fun showOTPForDemo(otp: String, phoneNumber: String) {
        // Print ke console untuk developer
        println("=== OTP DEMO ===")
        println("OTP: $otp")
        println("Sent to: $phoneNumber")
        println("================")

        // Toast sementara untuk demo (dalam production, ini dihapus)
        showToast("📱 SMS terkirim ke $phoneNumber")

        // Snackbar dengan OTP untuk kemudahan development
        scope.launch {
            snackbarHostState.showSnackbar(
                message = "🔑 Demo OTP: $otp",
                duration = SnackbarDuration.Long
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        when (currentScreen) {
            AppScreen.Login -> {
                LoginScreen(
                    onLoginClick = { email, password ->
                        // Simulasi login - cek apakah user sudah terdaftar
                        if (user.isRegistered && user.email == email) {
                            showToast("Login berhasil! Selamat datang kembali.")
                            isFromLogin = true // Set flag untuk welcome screen
                            // Pastikan personalData terisi dari user yang sudah register
                            personalData = user.personalData
                            // Pindah ke Welcome screen atau home screen
                            currentScreen = AppScreen.Welcome
                        } else {
                            showToast("Email atau password salah")
                        }
                    },
                    onRegisterClick = {
                        currentScreen = AppScreen.Register
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppScreen.Register -> {
                RegisterScreen(
                    onRegisterClick = { data ->
                        registerData = data
                        // Generate OTP baru
                        currentOTP = generateOTP()

                        // Show OTP untuk demo
                        showOTPForDemo(currentOTP, data.phoneNumber)

                        currentScreen = AppScreen.OTPVerification
                    },
                    onBackToLogin = {
                        currentScreen = AppScreen.Login
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppScreen.OTPVerification -> {
                OTPVerificationScreen(
                    phoneNumber = registerData.phoneNumber,
                    generatedOTP = currentOTP, // Pass OTP yang di-generate
                    onOTPVerified = {
                        showToast("OTP berhasil diverifikasi!")
                        currentScreen = AppScreen.PersonalData
                    },
                    onResendOTP = {
                        // Generate OTP baru lagi
                        currentOTP = generateOTP()
                        showOTPForDemo(currentOTP, registerData.phoneNumber)
                    },
                    onBackClick = {
                        currentScreen = AppScreen.Register
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppScreen.PersonalData -> {
                PersonalDataScreen(
                    onDataComplete = { data ->
                        personalData = data
                        user = user.copy(
                            email = registerData.email,
                            phoneNumber = registerData.phoneNumber,
                            personalData = data,
                            isRegistered = true,
                            isVerified = true
                        )
                        isFromLogin = false // Set flag untuk welcome screen setelah registrasi
                        showToast("Registrasi berhasil!")
                        currentScreen = AppScreen.Welcome
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppScreen.Welcome -> {
                WelcomeScreen(
                    personalData = personalData,
                    isFromLogin = isFromLogin,
                    onContinueToLogin = {
                        if (isFromLogin) {
                            showToast("Mulai berbelanja di Starbucks!")
                            // Di sini bisa tambahkan navigasi ke home/dashboard
                        } else {
                            showToast("Silakan login dengan akun baru Anda")
                        }
                        currentScreen = AppScreen.Login
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}