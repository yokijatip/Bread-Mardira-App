package com.gity.breadmardira.ui.cart.customerData

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gity.breadmardira.model.CartItem
import com.gity.breadmardira.model.CustomerData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CustomerDataViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _orderSubmitted = MutableLiveData<Boolean>(false)
    val orderSubmitted: LiveData<Boolean> = _orderSubmitted

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun submitOrder(customerData: CustomerData) {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch
            _isLoading.postValue(true)

            try {
                // Get cart items
                val cartSnapshot = db.collection("carts")
                    .document(uid)
                    .collection("items")
                    .get()
                    .await()

                val cartItems = cartSnapshot.toObjects(CartItem::class.java)

                if (cartItems.isEmpty()) {
                    _isLoading.postValue(false)
                    return@launch
                }

                // Calculate total
                val totalPrice = cartItems.sumOf { it.price * it.quantity }

                // Create order data
                val orderData = hashMapOf(
                    "customerData" to customerData,
                    "cartItems" to cartItems,
                    "totalPrice" to totalPrice,
                    "status" to "pending",
                    "orderId" to generateOrderId(),
                    "userId" to uid,
                    "createdAt" to System.currentTimeMillis()
                )

                // Save order to Firestore
                db.collection("orders")
                    .add(orderData)
                    .await()

                // Clear cart after successful order
                clearCart(uid)

                _orderSubmitted.postValue(true)

            } catch (e: Exception) {
                e.printStackTrace()
                _orderSubmitted.postValue(false)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    private suspend fun clearCart(uid: String) {
        try {
            val cartSnapshot = db.collection("carts")
                .document(uid)
                .collection("items")
                .get()
                .await()

            val batch = db.batch()
            for (document in cartSnapshot.documents) {
                batch.delete(document.reference)
            }
            batch.commit().await()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun generateOrderId(): String {
        return "ORDER-${System.currentTimeMillis()}-${(1000..9999).random()}"
    }
}