package com.gity.breadmardira.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    var id: String? = null,
    var userId: String? = null,
    var name: String? = null,
    var address: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var items: List<CartItem>? = null,
    var total: Double = 0.0,
    var status: String? = null,
    var timestamp: Long? = null,

    // Fields sesuai struktur Firebase
    var customerData: CustomerData? = null,
    var cartItems: List<CartItem>? = null,
    var createdAt: Long? = null
) : Parcelable {

    // Helper properties untuk backward compatibility
    val customerName: String?
        get() = name ?: customerData?.name

    val customerAddress: String?
        get() = address ?: customerData?.address

    val customerLatitude: Double?
        get() = latitude ?: customerData?.latitude

    val customerLongitude: Double?
        get() = longitude ?: customerData?.longitude

    val orderItems: List<CartItem>?
        get() = items ?: cartItems

    val orderTimestamp: Long?
        get() = timestamp ?: createdAt
}


