package com.gity.breadmardira.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gity.breadmardira.databinding.ItemOrderBinding
import com.gity.breadmardira.model.Order
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class OrderAdapter(
    private var orders: List<Order>,
    private val onOrderClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    companion object {
        private const val TAG = "OrderAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order, onOrderClick)
    }

    override fun getItemCount(): Int = orders.size

    fun updateData(newOrders: List<Order>) {
        Log.d(TAG, "updateData: Updating adapter with ${newOrders.size} orders")

        val diffCallback = OrderDiffCallback(orders, newOrders)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        orders = newOrders
        diffResult.dispatchUpdatesTo(this)

        Log.d(TAG, "updateData: Adapter updated, item count: $itemCount")
    }

    class OrderViewHolder(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order, onOrderClick: (Order) -> Unit) {
            try {
                Log.d(TAG, "Binding order: ${order.id}")

                // Set order ID
                binding.tvOrderId.text = "#${order.id?.take(8) ?: "Unknown"}"

                // Set customer name - use helper property
                binding.tvCustomerName.text = order.customerName ?: "Unknown Customer"

                // Set order total
                val total = if (order.total > 0) order.total else {
                    // Calculate from items if total is 0
                    order.orderItems?.sumOf { it.price * it.quantity } ?: 0.0
                }
                val formattedTotal = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(total)
                binding.tvOrderTotal.text = formattedTotal

                // Set order status with color coding
                binding.tvOrderStatus.text = (order.status ?: "PENDING").uppercase()
                setStatusColor(order.status ?: "pending")

                // Set timestamp - use helper property
                val timestamp = order.orderTimestamp ?: System.currentTimeMillis()
                val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                val formattedDate = dateFormat.format(Date(timestamp))
                binding.tvOrderDate.text = formattedDate

                // Set address - use helper property
                val address = order.customerAddress ?: "No Address"
                binding.tvOrderAddress.text = if (address.length > 50) {
                    "${address.take(50)}..."
                } else {
                    address
                }

                // Set item count - use helper property
                val itemCount = order.orderItems?.sumOf { it.quantity } ?: 0
                binding.tvItemCount.text = "$itemCount item(s)"

                // Set click listener
                binding.root.setOnClickListener {
                    Log.d(TAG, "Order clicked: ${order.id}")
                    onOrderClick(order)
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error binding order ${order.id}", e)
                // Set fallback values
                binding.tvOrderId.text = order.id ?: "Error"
                binding.tvCustomerName.text = "Error loading order"
            }
        }

        private fun setStatusColor(status: String?) {
            val context = binding.root.context
            val colorRes = when (status?.lowercase()) {
                "pending" -> android.R.color.holo_orange_dark
                "processing" -> android.R.color.holo_blue_dark
                "delivered" -> android.R.color.holo_green_dark
                "cancelled" -> android.R.color.holo_red_dark
                else -> android.R.color.darker_gray
            }
            binding.tvOrderStatus.setTextColor(context.getColor(colorRes))
        }
    }

    // DiffUtil callback for efficient updates
    private class OrderDiffCallback(
        private val oldList: List<Order>,
        private val newList: List<Order>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldOrder = oldList[oldItemPosition]
            val newOrder = newList[newItemPosition]

            return oldOrder.id == newOrder.id &&
                    oldOrder.status == newOrder.status &&
                    oldOrder.total == newOrder.total &&
                    oldOrder.name == newOrder.name
        }
    }
}