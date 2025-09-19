package com.gity.breadmardira.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomerData(
    var address: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var name: String? = null,
    var notes: String? = null,
    var phone: String? = null,
    var timestamp: Long? = null
) : Parcelable