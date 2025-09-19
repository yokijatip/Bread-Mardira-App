package com.gity.breadmardira.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    val productId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val imageRes: String = "",
    var quantity: Int = 1
) : Parcelable
