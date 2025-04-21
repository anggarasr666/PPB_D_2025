package com.example.currencyconvercalculator.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.currencyconvercalculator.model.Currency

@Composable
fun CurrencyDropdown(
    currencies: List<Currency>,
    selectedCurrency: Currency,
    onCurrencySelected: (Currency) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${selectedCurrency.flagEmoji} ${selectedCurrency.code} - ${selectedCurrency.name}",
                    modifier = Modifier.weight(1f)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                currencies.forEach { currency ->
                    DropdownMenuItem(
                        text = {
                            Text("${currency.flagEmoji} ${currency.code} - ${currency.name}")
                        },
                        onClick = {
                            onCurrencySelected(currency)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
