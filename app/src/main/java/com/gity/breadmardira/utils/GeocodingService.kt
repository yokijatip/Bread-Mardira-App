package com.gity.breadmardira.utils


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

data class AddressInfo(
    val displayName: String = "",
    val road: String = "",
    val houseNumber: String = "",
    val village: String = "",
    val city: String = "",
    val state: String = "",
    val postcode: String = "",
    val country: String = ""
)

class GeocodingService {

    companion object {
        private const val BASE_URL = "https://nominatim.openstreetmap.org/reverse"
    }

    suspend fun reverseGeocode(latitude: Double, longitude: Double): AddressInfo? {
        return withContext(Dispatchers.IO) {
            try {
                val urlString = "${BASE_URL}?format=json&lat=${latitude}&lon=${longitude}&addressdetails=1"
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection

                // Set headers
                connection.requestMethod = "GET"
                connection.setRequestProperty("User-Agent", "BreadMardira Android App")
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = reader.readText()
                    reader.close()

                    parseResponse(response)
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun parseResponse(jsonResponse: String): AddressInfo {
        return try {
            val jsonObject = JSONObject(jsonResponse)
            val displayName = jsonObject.optString("display_name", "")

            val addressObject = jsonObject.optJSONObject("address")

            AddressInfo(
                displayName = displayName,
                road = addressObject?.optString("road", "") ?: "",
                houseNumber = addressObject?.optString("house_number", "") ?: "",
                village = addressObject?.optString("village", "")
                    ?: addressObject?.optString("suburb", "")
                    ?: addressObject?.optString("neighbourhood", "") ?: "",
                city = addressObject?.optString("city", "")
                    ?: addressObject?.optString("town", "")
                    ?: addressObject?.optString("municipality", "") ?: "",
                state = addressObject?.optString("state", "") ?: "",
                postcode = addressObject?.optString("postcode", "") ?: "",
                country = addressObject?.optString("country", "") ?: ""
            )
        } catch (e: Exception) {
            AddressInfo(displayName = "Alamat tidak dapat ditemukan")
        }
    }

    fun formatAddress(addressInfo: AddressInfo): String {
        val parts = mutableListOf<String>()

        // Jalan dan nomor
        if (addressInfo.road.isNotEmpty()) {
            if (addressInfo.houseNumber.isNotEmpty()) {
                parts.add("${addressInfo.road} No.${addressInfo.houseNumber}")
            } else {
                parts.add(addressInfo.road)
            }
        }

        // Kelurahan/Desa
        if (addressInfo.village.isNotEmpty()) {
            parts.add(addressInfo.village)
        }

        // Kota
        if (addressInfo.city.isNotEmpty()) {
            parts.add(addressInfo.city)
        }

        // Provinsi
        if (addressInfo.state.isNotEmpty()) {
            parts.add(addressInfo.state)
        }

        // Kode pos
        if (addressInfo.postcode.isNotEmpty()) {
            parts.add(addressInfo.postcode)
        }

        return if (parts.isNotEmpty()) {
            parts.joinToString(", ")
        } else {
            addressInfo.displayName.ifEmpty { "Alamat tidak diketahui" }
        }
    }
}