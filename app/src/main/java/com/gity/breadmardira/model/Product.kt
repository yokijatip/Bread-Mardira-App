package com.gity.breadmardira.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageRes: String = "" // nama drawable, misal "image1"
): Parcelable
