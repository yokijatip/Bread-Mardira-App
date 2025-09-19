package com.gity.breadmardira.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val address: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val items: List<CartItem> = emptyList(),
    val total: Double = 0.0,
    val status: String = "pending",
    val timestamp: Long = System.currentTimeMillis(),
    val customerData: CustomerData? = null
) : Parcelable

