package com.example.trackingexpenses.models

data class FamilyMember(
    val userId: String,
    val name: String?,
    val email: String,
    val img: String?,
    val status: String
)