package com.gity.breadmardira.uiadmin.management.viewProduct

import androidx.lifecycle.*
import com.gity.breadmardira.model.Product
import com.gity.breadmardira.repository.ProductRepository
import kotlinx.coroutines.launch

class ViewProductViewModel(
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
}
