package com.example.currencyconvercalculator.data

import com.example.currencyconvercalculator.model.Currency

object CurrencyRepository {
    val currencies = listOf(
        Currency("IDR", "Indonesian Rupiah", "Rp", "🇮🇩", 1.0),
        Currency("USD", "US Dollar", "$", "🇺🇸", 15735.0),
        Currency("EUR", "Euro", "€", "🇪🇺", 16821.0),
        Currency("JPY", "Japanese Yen", "¥", "🇯🇵", 102.45),
        Currency("GBP", "British Pound", "£", "🇬🇧", 19578.0),
        Currency("SGD", "Singapore Dollar", "S$", "🇸🇬", 11542.0)
    )

    fun convert(amount: Double, fromCurrency: Currency, toCurrency: Currency): Double {
        val amountInIdr = if (fromCurrency.code == "IDR") {
            amount
        } else {
            amount * fromCurrency.rateToIdr
        }

        return if (toCurrency.code == "IDR") {
            amountInIdr
        } else {
            amountInIdr / toCurrency.rateToIdr
        }
    }
}