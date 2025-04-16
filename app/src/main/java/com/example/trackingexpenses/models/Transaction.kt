package com.example.trackingexpenses.models

import com.google.firebase.Timestamp

data class Transaction(
    val id: String = "",
    val coast: String = "",
    val notes: String = "",
    val date: String = "",
    val time: String = "",
    val category: String = "",
    val type: String = "",
    val dateTime: Timestamp = Timestamp.now(),
    var period: Int = 1
)