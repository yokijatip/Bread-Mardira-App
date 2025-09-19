package com.gity.breadmardira.uiadmin.management.addProduct

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gity.breadmardira.model.Product
import com.gity.breadmardira.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddProductViewModel(
    private val repository: ProductRepository = ProductRepository()
) : ViewModel() {

    private val _addState = MutableStateFlow<Result<Void?>?>(null)
    val addState: StateFlow<Result<Void?>?> = _addState

    // Daftar nama file gambar di drawable
    private val imageList = listOf("image1", "image2", "image3", "image4", "image5")
    // Daftar ekstensi file gambar yang mungkin
    private val imageExtensions = listOf(".jpg", ".jpeg")

    fun addProduct(name: String, desc: String, price: Double) {
        // Pilih gambar acak dari daftar beserta ekstensinya
        val randomImageName = imageList.random()
        val randomImageExtension = imageExtensions.random()
        val randomImageResName = "$randomImageName$randomImageExtension"

        val product = Product(name = name, description = desc, price = price, imageRes = randomImageResName)

        viewModelScope.launch {
            val result = repository.addProduct(product)
            _addState.value = result
        }
    }
}