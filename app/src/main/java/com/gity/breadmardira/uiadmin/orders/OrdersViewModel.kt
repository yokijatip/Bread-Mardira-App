package com.gity.breadmardira.uiadmin.orders

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gity.breadmardira.model.Order
import com.gity.breadmardira.repository.OrderRepository
import kotlinx.coroutines.launch

class OrdersViewModel(
    private val repository: OrderRepository = OrderRepository()
) : ViewModel() {

    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> = _orders

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        Log.d("OrdersViewModel", "ViewModel initialized, fetching orders...")
        fetchOrders()
    }

    fun fetchOrders() {
        viewModelScope.launch {
            Log.d("OrdersViewModel", "fetchOrders: Starting to fetch orders")
            _isLoading.value = true

            try {
                val result = repository.getAllOrders()
                Log.d("OrdersViewModel", "fetchOrders: Repository call completed")

                result
                    .onSuccess { list ->
                        Log.d("OrdersViewModel", "fetchOrders: Success - ${list.size} orders received")
                        _orders.value = list
                        _error.value = null // Clear previous errors

                        // Log detail setiap order untuk debugging
                        list.forEachIndexed { index, order ->
                            Log.d("OrdersViewModel", "Order $index: ID=${order.id}, UserId=${order.userId}, Status=${order.status}, Items=${order.items?.size}")
                        }
                    }
                    .onFailure { e ->
                        val errorMessage = e.message ?: "Terjadi kesalahan saat mengambil data pesanan"
                        Log.e("OrdersViewModel", "fetchOrders: Failed - $errorMessage", e)
                        _error.value = errorMessage
                        _orders.value = emptyList() // Set empty list on failure
                    }

            } catch (e: Exception) {
                val errorMessage = "Terjadi kesalahan saat mengambil data pesanan: ${e.message}"
                Log.e("OrdersViewModel", "fetchOrders: Exception caught - $errorMessage", e)
                _error.value = errorMessage
                _orders.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshOrders() {
        Log.d("OrdersViewModel", "refreshOrders: Manual refresh triggered")
        fetchOrders()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("OrdersViewModel", "ViewModel cleared")
    }
}