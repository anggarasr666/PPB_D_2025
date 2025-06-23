package com.example.otp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.otp.data.IndonesiaData
import com.example.otp.data.PersonalData
import com.example.otp.ui.components.*
import com.example.otp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalDataScreen(
    onDataComplete: (PersonalData) -> Unit,
    modifier: Modifier = Modifier
) {
    var fullName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var selectedProvince by remember { mutableStateOf("") }
    var selectedCity by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf("") }
    var birthDateError by remember { mutableStateOf("") }
    var provinceError by remember { mutableStateOf("") }
    var cityError by remember { mutableStateOf("") }

    var showProvinceDropdown by remember { mutableStateOf(false) }
    var showCityDropdown by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()

    fun validateInputs(): Boolean {
        var isValid = true

        if (fullName.isBlank()) {
            nameError = "Nama lengkap tidak boleh kosong"
            isValid = false
        } else if (fullName.length < 2) {
            nameError = "Nama minimal 2 karakter"
            isValid = false
        } else {
            nameError = ""
        }

        if (birthDate.isBlank()) {
            birthDateError = "Tanggal lahir tidak boleh kosong"
            isValid = false
        } else {
            birthDateError = ""
        }

        if (selectedProvince.isBlank()) {
            provinceError = "Provinsi harus dipilih"
            isValid = false
        } else {
            provinceError = ""
        }

        if (selectedCity.isBlank()) {
            cityError = "Kota harus dipilih"
            isValid = false
        } else {
            cityError = ""
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
            title = "Data Diri",
            subtitle = "Lengkapi profil Anda untuk pengalaman yang lebih personal"
        )

        Spacer(modifier = Modifier.height(32.dp))

        StarbucksTextField(
            value = fullName,
            onValueChange = {
                fullName = it
                if (nameError.isNotEmpty()) nameError = ""
            },
            label = "Nama Lengkap",
            isError = nameError.isNotEmpty(),
            errorMessage = nameError,
            leadingIcon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = if (nameError.isNotEmpty()) StarbucksError else StarbucksGray
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Date Picker Field
        StarbucksTextField(
            value = birthDate,
            onValueChange = { },
            label = "Tanggal Lahir",
            isError = birthDateError.isNotEmpty(),
            errorMessage = birthDateError,
            leadingIcon = {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    tint = if (birthDateError.isNotEmpty()) StarbucksError else StarbucksGray
                )
            },
            modifier = Modifier.then(
                Modifier.fillMaxWidth()
            )
        )

        // Date Picker Button overlay
        Button(
            onClick = { showDatePicker = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .offset(y = (-56).dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = androidx.compose.ui.graphics.Color.Transparent,
                contentColor = androidx.compose.ui.graphics.Color.Transparent
            )
        ) { }

        Spacer(modifier = Modifier.height(0.dp))

        // Province Dropdown
        ExposedDropdownMenuBox(
            expanded = showProvinceDropdown,
            onExpandedChange = { showProvinceDropdown = !showProvinceDropdown }
        ) {
            StarbucksTextField(
                value = selectedProvince,
                onValueChange = { },
                label = "Provinsi",
                modifier = Modifier.menuAnchor(),
                isError = provinceError.isNotEmpty(),
                errorMessage = provinceError,
                leadingIcon = {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = if (provinceError.isNotEmpty()) StarbucksError else StarbucksGray
                    )
                }
            )

            ExposedDropdownMenu(
                expanded = showProvinceDropdown,
                onDismissRequest = { showProvinceDropdown = false }
            ) {
                IndonesiaData.provinces.keys.forEach { province ->
                    DropdownMenuItem(
                        text = { Text(province) },
                        onClick = {
                            selectedProvince = province
                            selectedCity = "" // Reset city when province changes
                            showProvinceDropdown = false
                            if (provinceError.isNotEmpty()) provinceError = ""
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // City Dropdown
        ExposedDropdownMenuBox(
            expanded = showCityDropdown,
            onExpandedChange = {
                if (selectedProvince.isNotEmpty()) {
                    showCityDropdown = !showCityDropdown
                }
            }
        ) {
            StarbucksTextField(
                value = selectedCity,
                onValueChange = { },
                label = "Kota",
                modifier = Modifier.menuAnchor(),
                isError = cityError.isNotEmpty(),
                errorMessage = cityError,
                leadingIcon = {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = if (cityError.isNotEmpty()) StarbucksError else StarbucksGray
                    )
                }
            )

            if (selectedProvince.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = showCityDropdown,
                    onDismissRequest = { showCityDropdown = false }
                ) {
                    IndonesiaData.provinces[selectedProvince]?.forEach { city ->
                        DropdownMenuItem(
                            text = { Text(city) },
                            onClick = {
                                selectedCity = city
                                showCityDropdown = false
                                if (cityError.isNotEmpty()) cityError = ""
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        StarbucksButton(
            text = "Selesai",
            onClick = {
                if (validateInputs()) {
                    val personalData = PersonalData(
                        fullName = fullName,
                        birthDate = birthDate,
                        province = selectedProvince,
                        city = selectedCity
                    )
                    onDataComplete(personalData)
                }
            },
            enabled = fullName.isNotBlank() && birthDate.isNotBlank() &&
                    selectedProvince.isNotBlank() && selectedCity.isNotBlank()
        )

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                                .format(java.util.Date(millis))
                            birthDate = date
                            if (birthDateError.isNotEmpty()) birthDateError = ""
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = StarbucksGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal", color = StarbucksGray)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = StarbucksGreen,
                    todayDateBorderColor = StarbucksGreen
                )
            )
        }
    }
}