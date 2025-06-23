package com.example.otp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.otp.ui.components.*
import com.example.otp.ui.theme.*

@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

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

        return isValid
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        StarbucksHeader(
            title = "Selamat Datang",
            subtitle = "Masuk ke akun Starbucks Anda untuk melanjutkan"
        )

        Spacer(modifier = Modifier.height(48.dp))

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

        Spacer(modifier = Modifier.height(32.dp))

        StarbucksButton(
            text = "Masuk",
            onClick = {
                if (validateInputs()) {
                    onLoginClick(email, password)
                }
            },
            enabled = email.isNotBlank() && password.isNotBlank()
        )

        Spacer(modifier = Modifier.height(16.dp))

        StarbucksOutlinedButton(
            text = "Daftar Akun Baru",
            onClick = onRegisterClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Dengan masuk, Anda menyetujui Syarat & Ketentuan dan Kebijakan Privasi kami",
            style = MaterialTheme.typography.bodySmall,
            color = StarbucksGray,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}