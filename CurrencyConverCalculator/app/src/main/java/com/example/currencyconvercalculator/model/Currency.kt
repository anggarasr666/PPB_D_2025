package com.example.currencyconvercalculator.model

data class Currency(
    val code: String,
    val name: String,
    val symbol: String,
    val flagEmoji: String,
    val rateToIdr: Double
)