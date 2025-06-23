package com.example.otp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.otp.ui.components.*
import com.example.otp.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun OTPVerificationScreen(
    phoneNumber: String,
    generatedOTP: String,
    onOTPVerified: () -> Unit,
    onResendOTP: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var otpCode by remember { mutableStateOf("") }
    var isVerifying by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var remainingTime by remember { mutableIntStateOf(60) }
    var canResend by remember { mutableStateOf(false) }

    // Countdown timer untuk kirim ulang OTP
    LaunchedEffect(remainingTime) {
        if (remainingTime > 0) {
            delay(1000)
            remainingTime--
        } else {
            canResend = true
        }
    }

    // Auto verifikasi ketika OTP 6 digit
    LaunchedEffect(otpCode) {
        if (otpCode.length == 6) {
            isVerifying = true
            delay(1500) // Simulasi verifikasi

            // Verifikasi dengan OTP yang di-generate
            if (otpCode == generatedOTP) {
                onOTPVerified()
            } else {
                errorMessage = "Kode OTP tidak valid"
                isVerifying = false
            }
        } else {
            errorMessage = ""
        }
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
            title = "Verifikasi OTP",
            subtitle = "Masukkan 6 digit kode yang telah dikirim ke nomor $phoneNumber"
        )

        Spacer(modifier = Modifier.height(48.dp))

        // OTP Input Field yang bisa diklik dan menerima input
        OTPInputField(
            value = otpCode,
            onValueChange = { newValue ->
                if (newValue.length <= 6 && newValue.all { it.isDigit() }) {
                    otpCode = newValue
                    errorMessage = ""
                }
            }
        )

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage,
                color = StarbucksError,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (isVerifying) {
            CircularProgressIndicator(
                color = StarbucksGreen,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Memverifikasi kode...",
                color = StarbucksGray,
                fontSize = 16.sp
            )
        } else {
            StarbucksButton(
                text = "Verifikasi",
                onClick = {
                    if (otpCode.length == 6) {
                        isVerifying = true
                        // Verifikasi dengan OTP yang di-generate
                        if (otpCode == generatedOTP) {
                            onOTPVerified()
                        } else {
                            errorMessage = "Kode OTP tidak valid"
                            isVerifying = false
                        }
                    } else {
                        errorMessage = "Kode OTP harus 6 digit"
                    }
                },
                enabled = otpCode.length == 6
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tidak menerima kode? ",
                color = StarbucksGray,
                fontSize = 14.sp
            )

            if (canResend) {
                TextButton(
                    onClick = {
                        onResendOTP()
                        remainingTime = 60
                        canResend = false
                        otpCode = ""
                        errorMessage = ""
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = StarbucksGreen
                    )
                ) {
                    Text(
                        text = "Kirim Ulang",
                        fontSize = 14.sp
                    )
                }
            } else {
                Text(
                    text = "Kirim ulang dalam ${remainingTime}s",
                    color = StarbucksGray,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        StarbucksOutlinedButton(
            text = "Kembali",
            onClick = onBackClick
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = StarbucksGreenLight
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📱 Info Verifikasi",
                    fontSize = 16.sp,
                    color = StarbucksGreen,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "• ",
                        fontSize = 14.sp,
                        color = StarbucksGray
                    )
                    Text(
                        text = "Kode OTP telah dikirim via SMS",
                        fontSize = 14.sp,
                        color = StarbucksGray,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "• ",
                        fontSize = 14.sp,
                        color = StarbucksGray
                    )
                    Text(
                        text = "Masukkan 6 digit kode di kotak di atas",
                        fontSize = 14.sp,
                        color = StarbucksGray,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "• ",
                        fontSize = 14.sp,
                        color = StarbucksGray
                    )
                    Text(
                        text = "Kode berlaku selama 5 menit",
                        fontSize = 14.sp,
                        color = StarbucksGray,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "💡 Demo: Cek console log Android Studio untuk melihat kode OTP",
                    fontSize = 12.sp,
                    color = StarbucksGray.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}