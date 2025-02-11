package com.example.trackingexpenses.models

data class User(
    val first_login: Boolean = false,
    val last_enter_in_app: String? = null,
    val total_income: Float = 0f,
    val total_expenditure: Float = 0f,
    val expenses_for_the_period: Float = 0f,
    val income_for_the_period: Float = 0f,
    val expenses_for_day: Float = 0f,
    val current_period: Int = 1
)