package com.gity.breadmardira.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomerData(
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable