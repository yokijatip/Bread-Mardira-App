package com.gity.breadmardira.repository

import com.gity.breadmardira.model.CartItem
import com.gity.breadmardira.model.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object OrderRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun userCartRef() =
        db.collection("carts").document(auth.currentUser?.uid ?: "guest").collection("items")

    suspend fun placeOrder(name: String, address: String, latitude: Double?, longitude: Double?): Result<String> {
        try {
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            // ambil item cart
            val snap = userCartRef().get().await()
            val items = snap.toObjects(CartItem::class.java)
            if (items.isEmpty()) return Result.failure(Exception("Keranjang kosong"))

            // hitung total
            val total = items.sumByDouble { it.price * it.quantity }

            // buat order
            val orderRef = db.collection("orders").document()
            val order = Order(
                id = orderRef.id,
                userId = uid,
                name = name,
                address = address,
                latitude = latitude,
                longitude = longitude,
                items = items,
                total = total,
                status = "pending",
                timestamp = System.currentTimeMillis()
            )
            orderRef.set(order).await()

            // hapus semua item cart (batch)
            val docs = userCartRef().get().await().documents
            val batch = db.batch()
            for (d in docs) batch.delete(d.reference)
            batch.commit().await()

            return Result.success(orderRef.id)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    suspend fun getCartSummary(): Result<Pair<List<CartItem>, Double>> {
        return try {
            val snap = userCartRef().get().await()
            val items = snap.toObjects(CartItem::class.java)
            val total = items.sumByDouble { it.price * it.quantity }
            Result.success(Pair(items, total))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
