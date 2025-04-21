package com.example.currencyconvercalculator.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.currencyconvercalculator.components.CurrencyDropdown
import com.example.currencyconvercalculator.components.CurrencyInput
import com.example.currencyconvercalculator.data.CurrencyRepository
import com.example.currencyconvercalculator.model.Currency
import java.text.DecimalFormat

@Composable
fun ConverterScreen() {
    val currencies = CurrencyRepository.currencies

    var fromCurrency by remember { mutableStateOf(currencies.first { it.code == "IDR" }) }
    var toCurrency by remember { mutableStateOf(currencies.first { it.code == "USD" }) }
    var amount by remember { mutableStateOf("") }
    var convertedAmount by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Currency Converter",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Current Exchange Rate",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                val rateDisplay = if (fromCurrency.code == "IDR") {
                    "1 ${fromCurrency.code} = ${DecimalFormat("#.####").format(1 / toCurrency.rateToIdr)} ${toCurrency.code}"
                } else if (toCurrency.code == "IDR") {
                    "1 ${fromCurrency.code} = ${DecimalFormat("#.##").format(fromCurrency.rateToIdr)} ${toCurrency.code}"
                } else {
                    val rate = fromCurrency.rateToIdr / toCurrency.rateToIdr
                    "1 ${fromCurrency.code} = ${DecimalFormat("#.####").format(rate)} ${toCurrency.code}"
                }

                Text(
                    text = rateDisplay,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        CurrencyInput(
            value = amount,
            onValueChange = {
                amount = it
                if (it.isNotEmpty()) {
                    try {
                        val amountValue = it.toDouble()
                        val converted = CurrencyRepository.convert(amountValue, fromCurrency, toCurrency)
                        convertedAmount = DecimalFormat("#,###.####").format(converted)
                    } catch (e: NumberFormatException) {
                        convertedAmount = ""
                    }
                } else {
                    convertedAmount = ""
                }
            },
            currency = fromCurrency,
            label = "Amount to convert"
        )

        CurrencyDropdown(
            currencies = currencies,
            selectedCurrency = fromCurrency,
            onCurrencySelected = {
                fromCurrency = it
                if (amount.isNotEmpty()) {
                    try {
                        val amountValue = amount.toDouble()
                        val converted = CurrencyRepository.convert(amountValue, fromCurrency, toCurrency)
                        convertedAmount = DecimalFormat("#,###.####").format(converted)
                    } catch (e: NumberFormatException) {
                        convertedAmount = ""
                    }
                }
            }
        )

        Button(
            onClick = {
                val temp = fromCurrency
                fromCurrency = toCurrency
                toCurrency = temp
                if (amount.isNotEmpty()) {
                    try {
                        val amountValue = amount.toDouble()
                        val converted = CurrencyRepository.convert(amountValue, fromCurrency, toCurrency)
                        convertedAmount = DecimalFormat("#,###.####").format(converted)
                    } catch (e: NumberFormatException) {
                        convertedAmount = ""
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Swap Currencies")
        }

        CurrencyDropdown(
            currencies = currencies,
            selectedCurrency = toCurrency,
            onCurrencySelected = {
                toCurrency = it
                if (amount.isNotEmpty()) {
                    try {
                        val amountValue = amount.toDouble()
                        val converted = CurrencyRepository.convert(amountValue, fromCurrency, toCurrency)
                        convertedAmount = DecimalFormat("#,###.####").format(converted)
                    } catch (e: NumberFormatException) {
                        convertedAmount = ""
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (convertedAmount.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Converted Amount",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${toCurrency.symbol} $convertedAmount",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "* Exchange rates are fixed and for educational purposes only",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}