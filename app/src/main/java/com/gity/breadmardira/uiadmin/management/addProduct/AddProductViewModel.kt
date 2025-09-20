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
    private val imageList = listOf("https://res.cloudinary.com/dsptxtm6k/image/upload/v1758271625/image4_rpxail.jpg", "https://res.cloudinary.com/dsptxtm6k/image/upload/v1758271625/image1_cwdbby.jpg", "https://res.cloudinary.com/dsptxtm6k/image/upload/v1758271625/image2_zxm5fi.jpg", "https://res.cloudinary.com/dsptxtm6k/image/upload/v1758271624/image3_kutsrs.jpg", "https://res.cloudinary.com/dsptxtm6k/image/upload/v1758271624/image5_ijb0we.jpg")

    fun addProduct(name: String, desc: String, price: Double) {
        // Pilih gambar acak dari daftar beserta ekstensinya
        val randomImageName = imageList.random()
        val randomImageResName = randomImageName

        val product = Product(name = name, description = desc, price = price, imageRes = randomImageResName)

        viewModelScope.launch {
            val result = repository.addProduct(product)
            _addState.value = result
        }
    }
}