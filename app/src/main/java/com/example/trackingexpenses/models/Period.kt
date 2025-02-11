package com.example.trackingexpenses.models

data class Period (
    val period: Int? = null,
    val expenses_for_the_period: Float? = null,
    val income_for_the_period: Float? = null,
)