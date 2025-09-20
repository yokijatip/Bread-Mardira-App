package com.gity.breadmardira.model



data class User(
    val id: String = "",
    val email: String = "",
    val password: String = "",
    val role: String = "customer" // Admin | Customer
)
