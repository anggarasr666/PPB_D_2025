package com.example.otp.data

// Data classes untuk menyimpan state aplikasi
data class RegisterData(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val phoneNumber: String = "",
    val agreedToTerms: Boolean = false
)

data class PersonalData(
    val fullName: String = "",
    val birthDate: String = "",
    val province: String = "",
    val city: String = ""
)

data class User(
    val email: String = "",
    val phoneNumber: String = "",
    val personalData: PersonalData = PersonalData(),
    val isRegistered: Boolean = false,
    val isVerified: Boolean = false
)

// App navigation states
sealed class AppScreen {
    object Login : AppScreen()
    object Register : AppScreen()
    object OTPVerification : AppScreen()
    object PersonalData : AppScreen()
    object Welcome : AppScreen()
}

// Daftar provinsi dan kota di Indonesia (simplified)
object IndonesiaData {
    val provinces = mapOf(
        "Jawa Timur" to listOf("Surabaya", "Malang", "Mojokerto", "Pasuruan", "Kediri", "Madiun", "Jember"),
        "Jawa Barat" to listOf("Bandung", "Bekasi", "Depok", "Bogor", "Cirebon", "Sukabumi", "Tasikmalaya"),
        "Jawa Tengah" to listOf("Semarang", "Solo", "Yogyakarta", "Magelang", "Purwokerto", "Tegal", "Pekalongan"),
        "DKI Jakarta" to listOf("Jakarta Pusat", "Jakarta Utara", "Jakarta Selatan", "Jakarta Barat", "Jakarta Timur"),
        "Bali" to listOf("Denpasar", "Ubud", "Singaraja", "Tabanan", "Gianyar", "Klungkung"),
        "Sumatera Utara" to listOf("Medan", "Binjai", "Pematangsiantar", "Tanjungbalai", "Sibolga"),
        "Sumatera Barat" to listOf("Padang", "Bukittinggi", "Payakumbuh", "Solok", "Sawahlunto"),
        "Kalimantan Timur" to listOf("Samarinda", "Balikpapan", "Bontang", "Tarakan", "Sangatta")
    )
}