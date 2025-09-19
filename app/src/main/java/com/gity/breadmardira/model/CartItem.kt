package com.gity.breadmardira.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    var productId: String = "",
    var name: String = "",
    var price: Double = 0.0,
    var quantity: Int = 1,
    var imageRes: String? = null,

    // Alternative field names that might exist in Firebase
    var id: String? = null,
    var title: String? = null,
    var image: String? = null,
    var imageUrl: String? = null
) : Parcelable {

    // Helper properties for backward compatibility
    val itemName: String
        get() = if (name.isNotEmpty()) name else (title ?: "Unknown Item")

    val itemImage: String?
        get() = imageRes ?: image ?: imageUrl

    val itemId: String
        get() = if (productId.isNotEmpty()) productId else (id ?: "")
}
