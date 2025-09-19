package com.gity.breadmardira.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gity.breadmardira.model.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CartViewModel : ViewModel() {

    private val _cartItems = MutableLiveData<List<CartItem>>(emptyList())
    val cartItems: LiveData<List<CartItem>> = _cartItems

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun loadCart() {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch
            _isLoading.postValue(true)

            try {
                val snap = db.collection("carts")
                    .document(uid)
                    .collection("items")
                    .get()
                    .await()
                val list = snap.toObjects(CartItem::class.java)
                _cartItems.postValue(list)
            } catch (e: Exception) {
                _cartItems.postValue(emptyList())
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun updateCartItemQuantity(cartItem: CartItem, newQuantity: Int) {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch

            try {
                // Update quantity di Firestore
                db.collection("carts")
                    .document(uid)
                    .collection("items")
                    .document(cartItem.productId)
                    .update("quantity", newQuantity)
                    .await()

                // Update local data
                val currentList = _cartItems.value?.toMutableList() ?: mutableListOf()
                val index = currentList.indexOfFirst { it.productId == cartItem.productId }
                if (index != -1) {
                    currentList[index] = currentList[index].copy(quantity = newQuantity)
                    _cartItems.postValue(currentList)
                }
            } catch (e: Exception) {
                // Handle error - bisa ditambahkan error handling
            }
        }
    }

    fun removeCartItem(cartItem: CartItem) {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch

            try {
                // Remove item dari Firestore
                db.collection("carts")
                    .document(uid)
                    .collection("items")
                    .document(cartItem.productId)
                    .delete()
                    .await()

                // Update local data
                val currentList = _cartItems.value?.toMutableList() ?: mutableListOf()
                currentList.removeAll { it.productId == cartItem.productId }
                _cartItems.postValue(currentList)
            } catch (e: Exception) {
                // Handle error - bisa ditambahkan error handling
            }
        }
    }
}