package com.example.trackingexpenses.models

data class Period (
    val period: Int? = null,
    val expensesForThePeriod: Float? = null,
    val incomeForThePeriod: Float? = null,
)