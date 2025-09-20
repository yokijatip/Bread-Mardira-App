package com.gity.breadmardira.uiadmin.management.deleteProduct

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gity.breadmardira.model.Product
import com.gity.breadmardira.repository.ProductRepository
import kotlinx.coroutines.launch

class DeleteProductViewModel(
    private val repository: ProductRepository = ProductRepository()
) : ViewModel() {
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadProducts() {
        viewModelScope.launch {
            val result = repository.getAllProducts()
            result
                .onSuccess { _products.value = it }
                .onFailure { e -> _error.value = e.message }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            val result = repository.deleteProduct(productId)
            result
                .onSuccess { loadProducts() }
                .onFailure { e -> _error.value = e.message }
        }
    }
}