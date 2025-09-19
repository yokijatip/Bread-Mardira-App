package com.gity.breadmardira.repository

import android.util.Log
import com.gity.breadmardira.model.CartItem
import com.gity.breadmardira.model.CustomerData
import com.gity.breadmardira.model.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class OrderRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val TAG = "OrderRepository"

    private fun userCartRef() =
        db.collection("carts").document(auth.currentUser?.uid ?: "guest").collection("items")

    suspend fun placeOrder(
        name: String,
        address: String,
        latitude: Double?,
        longitude: Double?
    ): Result<String> {
        try {
            Log.d(TAG, "placeOrder: Attempting to place order for user")
            val uid = auth.currentUser?.uid
            if (uid == null) {
                Log.e(TAG, "placeOrder: User not logged in")
                return Result.failure(Exception("User not logged in"))
            }
            Log.d(TAG, "placeOrder: User UID: $uid")

            // ambil item cart
            val snap = userCartRef().get().await()
            val items = snap.toObjects(CartItem::class.java)
            if (items.isEmpty()) {
                Log.w(TAG, "placeOrder: Cart is empty")
                return Result.failure(Exception("Keranjang kosong"))
            }
            Log.d(TAG, "placeOrder: Cart items fetched: ${items.size} items")

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
            Log.d(TAG, "placeOrder: Order created with ID: ${orderRef.id}")

            // hapus semua item cart (batch)
            Log.d(TAG, "placeOrder: Attempting to clear cart")
            val docs = userCartRef().get().await().documents
            val batch = db.batch()
            for (d in docs) batch.delete(d.reference)
            batch.commit().await()
            Log.d(TAG, "placeOrder: Cart cleared successfully")

            Log.d(TAG, "placeOrder: Order placed successfully with ID: ${orderRef.id}")
            return Result.success(orderRef.id)
        } catch (e: Exception) {
            Log.e(TAG, "placeOrder: Error placing order", e)
            return Result.failure(e)
        }
    }

    suspend fun getCartSummary(): Result<Pair<List<CartItem>, Double>> {
        Log.d(TAG, "getCartSummary: Attempting to get cart summary")
        return try {
            val snap = userCartRef().get().await()
            val items = snap.toObjects(CartItem::class.java)
            val total = items.sumByDouble { it.price * it.quantity }
            Log.d(TAG, "getCartSummary: Cart summary fetched: ${items.size} items, total: $total")
            Result.success(Pair(items, total))
        } catch (e: Exception) {
            Log.e(TAG, "getCartSummary: Error getting cart summary", e)
            Result.failure(e)
        }
    }

    // Enhanced getAllOrders with better error handling and debugging
    suspend fun getAllOrders(): Result<List<Order>> {
        return try {
            Log.d(TAG, "getAllOrders: Starting to fetch ALL orders (Admin Mode)")

            // Check Firebase connection
            val currentUser = auth.currentUser
            Log.d(TAG, "getAllOrders: Current user: ${currentUser?.uid ?: "No user"}")

            Log.d(TAG, "getAllOrders: Testing Firestore connection...")

            // Try orderBy createdAt instead of timestamp since that's what exists in Firebase
            val snapshot = try {
                db.collection("orders")
                    .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .get()
                    .await()
            } catch (e: Exception) {
                Log.w(TAG, "getAllOrders: Failed to order by createdAt, trying without orderBy", e)
                // Fallback: get all orders without ordering
                db.collection("orders")
                    .get()
                    .await()
            }

            Log.d(TAG, "getAllOrders: Query executed successfully")
            Log.d(TAG, "getAllOrders: Documents found: ${snapshot.documents.size}")

            if (snapshot.isEmpty) {
                Log.w(TAG, "getAllOrders: No documents found in orders collection")
                return Result.success(emptyList())
            }

            // Convert to objects with better error handling
            val orders = mutableListOf<Order>()

            snapshot.documents.forEachIndexed { index, document ->
                try {
                    Log.d(TAG, "getAllOrders: Processing document $index - ID: ${document.id}")
                    Log.d(TAG, "getAllOrders: Document data: ${document.data}")

                    val order = document.toObject(Order::class.java)
                    if (order != null) {
                        // Set ID dari document ID
                        order.id = document.id

                        // Calculate total from cartItems if not present
                        if (order.total == 0.0 && !order.cartItems.isNullOrEmpty()) {
                            order.total = order.cartItems!!.sumOf { it.price * it.quantity }
                        }

                        // Set status jika belum ada
                        if (order.status.isNullOrEmpty()) {
                            order.status = "pending"
                        }

                        orders.add(order)

                        Log.d(TAG, "getAllOrders: Order added - ID: ${order.id}")
                        Log.d(TAG, "  - Customer: ${order.customerName}")
                        Log.d(TAG, "  - Address: ${order.customerAddress}")
                        Log.d(TAG, "  - Total: ${order.total}")
                        Log.d(TAG, "  - Status: ${order.status}")
                        Log.d(TAG, "  - Items: ${order.orderItems?.size}")

                    } else {
                        Log.w(TAG, "getAllOrders: Failed to convert document ${document.id} to Order object")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "getAllOrders: Error converting document ${document.id}", e)

                    // Try manual mapping as fallback
                    try {
                        val data = document.data
                        if (data != null) {
                            val order = createOrderFromMap(document.id, data)
                            if (order != null) {
                                orders.add(order)
                                Log.d(TAG, "getAllOrders: Manual mapping successful for ${document.id}")
                            }
                        }
                    } catch (e2: Exception) {
                        Log.e(TAG, "getAllOrders: Manual mapping also failed for ${document.id}", e2)
                    }
                }
            }

            Log.d(TAG, "getAllOrders: Successfully processed ${orders.size} out of ${snapshot.documents.size} documents")

            Result.success(orders)

        } catch (e: Exception) {
            Log.e(TAG, "getAllOrders: Error fetching orders - ${e.javaClass.simpleName}: ${e.message}", e)

            // Provide more specific error messages
            val errorMessage = when (e) {
                is com.google.firebase.firestore.FirebaseFirestoreException -> {
                    "Firestore error: ${e.message} (Code: ${e.code})"
                }
                is java.net.UnknownHostException -> {
                    "Network error: Please check your internet connection"
                }
                is java.util.concurrent.TimeoutException -> {
                    "Request timeout: Please try again"
                }
                else -> {
                    "Unexpected error: ${e.message}"
                }
            }

            Result.failure(Exception(errorMessage, e))
        }
    }

    // Helper function to manually create Order from Map
    private fun createOrderFromMap(documentId: String, data: Map<String, Any?>): Order? {
        return try {
            val order = Order(
                id = documentId,
                status = "pending", // default status
                total = 0.0,
                createdAt = data["createdAt"] as? Long
            )

            // Extract customer data
            val customerData = data["customerData"] as? Map<String, Any?>
            if (customerData != null) {
                order.customerData = CustomerData(
                    name = customerData["name"] as? String,
                    address = customerData["address"] as? String,
                    latitude = (customerData["latitude"] as? Number)?.toDouble(),
                    longitude = (customerData["longitude"] as? Number)?.toDouble(),
                    phone = customerData["phone"] as? String,
                    notes = customerData["notes"] as? String,
                    timestamp = customerData["timestamp"] as? Long
                )
            }

            // Extract cart items
            val cartItemsData = data["cartItems"] as? List<Map<String, Any?>>
            if (cartItemsData != null) {
                val cartItems = cartItemsData.mapNotNull { itemData ->
                    try {
                        CartItem(
                            productId = itemData["productId"] as? String ?: "",
                            name = itemData["name"] as? String ?: "",
                            price = (itemData["price"] as? Number)?.toDouble() ?: 0.0,
                            quantity = (itemData["quantity"] as? Number)?.toInt() ?: 1,
                            imageRes = itemData["imageRes"] as? String
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing cart item", e)
                        null
                    }
                }
                order.cartItems = cartItems
                // Calculate total
                order.total = cartItems.sumOf { it.price * it.quantity }
            }

            Log.d(TAG, "createOrderFromMap: Manual mapping successful for $documentId")
            order

        } catch (e: Exception) {
            Log.e(TAG, "createOrderFromMap: Error creating order from map", e)
            null
        }
    }

    // Helper function to check if orders collection exists
    suspend fun checkOrdersCollectionExists(): Result<Boolean> {
        return try {
            Log.d(TAG, "checkOrdersCollectionExists: Checking if orders collection exists")

            val snapshot = db.collection("orders").limit(1).get().await()
            val exists = !snapshot.isEmpty

            Log.d(TAG, "checkOrdersCollectionExists: Orders collection exists: $exists")
            Result.success(exists)

        } catch (e: Exception) {
            Log.e(TAG, "checkOrdersCollectionExists: Error checking collection", e)
            Result.failure(e)
        }
    }
}