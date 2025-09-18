package com.gity.breadmardira.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class User(
    val id: String = "",
    val email: String = "",
    val password: String = "",
    val role: String = "customer" // Admin | Customer
)
