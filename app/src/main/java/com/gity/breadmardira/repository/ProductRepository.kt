package com.gity.breadmardira.repository

import com.gity.breadmardira.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProductRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val productsRef = firestore.collection("products")

    suspend fun addProduct(product: Product): Result<Void?> {
        return try {
            val docRef = productsRef.document()
            val newProduct = product.copy(id = docRef.id)
            docRef.set(newProduct).await()
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllProducts(): Result<List<Product>> {
        return try {
            val snapshot = productsRef.get().await()
            val list = snapshot.toObjects(Product::class.java)
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}