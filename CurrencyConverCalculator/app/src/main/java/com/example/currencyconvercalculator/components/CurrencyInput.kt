package com.example.currencyconvercalculator.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.currencyconvercalculator.model.Currency

@Composable
fun CurrencyInput(
    value: String,
    onValueChange: (String) -> Unit,
    currency: Currency,
    label: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            val cleanValue = newValue.replace(Regex("[^0-9.]"), "")
            val decimalCount = cleanValue.count { it == '.' }
            if (decimalCount <= 1) {
                onValueChange(cleanValue)
            }
        },
        label = { Text(label) },
        leadingIcon = { Text(currency.symbol) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}