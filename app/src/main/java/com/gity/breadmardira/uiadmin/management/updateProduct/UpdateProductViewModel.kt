package com.gity.breadmardira.uiadmin.management.updateProduct

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gity.breadmardira.model.Product
import com.gity.breadmardira.repository.ProductRepository
import kotlinx.coroutines.launch

class UpdateProductViewModel(
    private val repository: ProductRepository = ProductRepository()
) : ViewModel() {

    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> = _updateSuccess

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.updateProduct(product)
            result
                .onSuccess {
                    _updateSuccess.value = true
                    _isLoading.value = false
                }
                .onFailure { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
        }
    }

    fun resetUpdateSuccess() {
        _updateSuccess.value = false
    }
}