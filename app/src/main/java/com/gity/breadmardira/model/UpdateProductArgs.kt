package com.gity.breadmardira.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UpdateProductArgs(
    val productId: String,
    val productName: String,
    val productDescription: String,
    val productPrice: Double
) : Parcelable

