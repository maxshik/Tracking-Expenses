package com.example.trackingexpenses.models

data class User(
    val firstLogin: Boolean = false,
    val lastEnterInApp: String? = null,
    val totalIncome: Float = 0f,
    val totalExpenditure: Float = 0f,
    val expensesForThePeriod: Float = 0f,
    val incomeForThePeriod: Float = 0f,
    val expensesForDay: Float = 0f,
    val dayLimit: Float? = null,
    val currentPeriod: Int = 1
)