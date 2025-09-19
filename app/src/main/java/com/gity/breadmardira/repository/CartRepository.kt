package com.gity.breadmardira.repository

import com.gity.breadmardira.model.CartItem
import com.gity.breadmardira.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object CartRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun userCartRef() =
        db.collection("carts").document(auth.currentUser?.uid ?: "guest").collection("items")

    suspend fun addToCart(product: Product): Result<Void?> {
        return try {
            val doc = userCartRef().document(product.id)
            db.runTransaction { txn ->
                val snap = txn.get(doc)
                if (snap.exists()) {
                    val currentQty = snap.getLong("quantity")?.toInt() ?: 1
                    txn.update(doc, "quantity", currentQty + 1)
                } else {
                    val cartItem = CartItem(
                        productId = product.id,
                        name = product.name,
                        price = product.price,
                        imageRes = product.imageRes,
                        quantity = 1
                    )
                    txn.set(doc, cartItem)
                }
            }.await()
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCartItems(): Result<List<CartItem>> {
        return try {
            val snap = userCartRef().get().await()
            Result.success(snap.toObjects(CartItem::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateQuantity(productId: String, quantity: Int): Result<Void?> {
        return try {
            userCartRef().document(productId)
                .update("quantity", quantity).await()
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeItem(productId: String): Result<Void?> {
        return try {
            userCartRef().document(productId).delete().await()
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}