package com.example.otp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.otp.data.PersonalData
import com.example.otp.ui.components.*
import com.example.otp.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
    personalData: PersonalData,
    onContinueToLogin: () -> Unit,
    isFromLogin: Boolean = false,
    modifier: Modifier = Modifier
) {
    var showContent by remember { mutableStateOf(false) }

    // Animasi entrance
    LaunchedEffect(Unit) {
        delay(500)
        showContent = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (showContent) {
            // Logo dan header
            StarbucksLogo(
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "🎉",
                fontSize = 48.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isFromLogin)
                    "Selamat Datang Kembali,\n${personalData.fullName}!"
                else
                    "Selamat Datang,\n${personalData.fullName}!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                lineHeight = 34.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isFromLogin)
                    "Selamat datang kembali di Starbucks! Nikmati kopi favorit Anda."
                else
                    "Akun Starbucks Anda telah berhasil dibuat dan diverifikasi!",
                fontSize = 16.sp,
                color = StarbucksGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Informasi akun
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = StarbucksGreenLight
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Informasi Akun",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StarbucksGreen
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    InfoRow(label = "Nama", value = personalData.fullName)
                    InfoRow(label = "Tanggal Lahir", value = personalData.birthDate)
                    InfoRow(label = "Provinsi", value = personalData.province)
                    InfoRow(label = "Kota", value = personalData.city)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Benefits section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = StarbucksWhite,
                    contentColor = StarbucksBlack
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Keuntungan Menjadi Member",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StarbucksGreen
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    BenefitItem("⭐", "Kumpulkan stars setiap pembelian")
                    BenefitItem("🎁", "Dapatkan reward eksklusif")
                    BenefitItem("🔔", "Notifikasi promo dan penawaran spesial")
                    BenefitItem("📱", "Order ahead untuk skip antrian")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            StarbucksButton(
                text = if (isFromLogin) "Mulai Berbelanja" else "Mulai Berbelanja",
                onClick = onContinueToLogin
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isFromLogin)
                    "Selamat datang kembali! Nikmati pengalaman Starbucks terbaik."
                else
                    "Silakan login dengan akun yang baru saja Anda buat",
                fontSize = 14.sp,
                color = StarbucksGray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = StarbucksGray,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = StarbucksBlack,
            modifier = Modifier.weight(2f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun BenefitItem(
    icon: String,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            fontSize = 20.sp,
            modifier = Modifier.width(32.dp)
        )
        Text(
            text = text,
            fontSize = 14.sp,
            color = StarbucksGray,
            modifier = Modifier.weight(1f)
        )
    }
}