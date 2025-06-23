package com.example.otp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.otp.data.RegisterData
import com.example.otp.ui.components.*
import com.example.otp.ui.theme.*

@Composable
fun RegisterScreen(
    onRegisterClick: (RegisterData) -> Unit,
    onBackToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var agreedToTerms by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }

    fun validateInputs(): Boolean {
        var isValid = true

        // Validasi email
        if (email.isBlank()) {
            emailError = "Email tidak boleh kosong"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Format email tidak valid"
            isValid = false
        } else {
            emailError = ""
        }

        // Validasi password
        if (password.isBlank()) {
            passwordError = "Password tidak boleh kosong"
            isValid = false
        } else if (password.length < 6) {
            passwordError = "Password minimal 6 karakter"
            isValid = false
        } else {
            passwordError = ""
        }

        // Validasi confirm password
        if (confirmPassword.isBlank()) {
            confirmPasswordError = "Konfirmasi password tidak boleh kosong"
            isValid = false
        } else if (password != confirmPassword) {
            confirmPasswordError = "Password tidak sama"
            isValid = false
        } else {
            confirmPasswordError = ""
        }

        // Validasi nomor telepon
        if (phoneNumber.isBlank()) {
            phoneError = "Nomor handphone tidak boleh kosong"
            isValid = false
        } else if (phoneNumber.length < 10) {
            phoneError = "Nomor handphone minimal 10 digit"
            isValid = false
        } else {
            phoneError = ""
        }

        return isValid
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        StarbucksHeader(
            title = "Daftar Akun",
            subtitle = "Bergabunglah dengan komunitas Starbucks dan nikmati pengalaman yang personal"
        )

        Spacer(modifier = Modifier.height(32.dp))

        StarbucksTextField(
            value = email,
            onValueChange = {
                email = it
                if (emailError.isNotEmpty()) emailError = ""
            },
            label = "Email",
            keyboardType = KeyboardType.Email,
            isError = emailError.isNotEmpty(),
            errorMessage = emailError,
            leadingIcon = {
                Icon(
                    Icons.Default.Email,
                    contentDescription = null,
                    tint = if (emailError.isNotEmpty()) StarbucksError else StarbucksGray
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        StarbucksTextField(
            value = password,
            onValueChange = {
                password = it
                if (passwordError.isNotEmpty()) passwordError = ""
            },
            label = "Password",
            isPassword = true,
            isError = passwordError.isNotEmpty(),
            errorMessage = passwordError,
            leadingIcon = {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (passwordError.isNotEmpty()) StarbucksError else StarbucksGray
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        StarbucksTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                if (confirmPasswordError.isNotEmpty()) confirmPasswordError = ""
            },
            label = "Konfirmasi Password",
            isPassword = true,
            isError = confirmPasswordError.isNotEmpty(),
            errorMessage = confirmPasswordError,
            leadingIcon = {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (confirmPasswordError.isNotEmpty()) StarbucksError else StarbucksGray
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        StarbucksTextField(
            value = phoneNumber,
            onValueChange = {
                // Hanya izinkan angka
                val filtered = it.filter { char -> char.isDigit() }
                phoneNumber = filtered
                if (phoneError.isNotEmpty()) phoneError = ""
            },
            label = "Nomor Handphone",
            keyboardType = KeyboardType.Phone,
            isError = phoneError.isNotEmpty(),
            errorMessage = phoneError,
            leadingIcon = {
                Icon(
                    Icons.Default.Phone,
                    contentDescription = null,
                    tint = if (phoneError.isNotEmpty()) StarbucksError else StarbucksGray
                )
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = agreedToTerms,
                onCheckedChange = { agreedToTerms = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = StarbucksGreen,
                    uncheckedColor = StarbucksGray
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Saya telah membaca dan menyetujui Syarat & Ketentuan serta Kebijakan Privasi",
                style = MaterialTheme.typography.bodySmall,
                color = StarbucksGray,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        StarbucksButton(
            text = "Daftar",
            onClick = {
                if (validateInputs() && agreedToTerms) {
                    val registerData = RegisterData(
                        email = email,
                        password = password,
                        confirmPassword = confirmPassword,
                        phoneNumber = phoneNumber,
                        agreedToTerms = agreedToTerms
                    )
                    onRegisterClick(registerData)
                }
            },
            enabled = email.isNotBlank() && password.isNotBlank() &&
                    confirmPassword.isNotBlank() && phoneNumber.isNotBlank() && agreedToTerms
        )

        Spacer(modifier = Modifier.height(16.dp))

        StarbucksOutlinedButton(
            text = "Kembali ke Masuk",
            onClick = onBackToLogin
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}