package com.gity.breadmardira.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gity.breadmardira.R
import com.gity.breadmardira.model.Order

class OrderAdapter(private val onClickListener: (Order) -> Unit) :
    ListAdapter<Order, OrderAdapter.ViewHolder>(OrderDiffCallback()) {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        val tvTotal: TextView = itemView.findViewById(R.id.tvTotalPrice)
        val tvStatus: TextView = itemView.findViewById(R.id.tvOrderStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = getItem(position)
        holder.tvName.text = order.name
        holder.tvAddress.text = order.address
        holder.tvTotal.text = "Rp ${order.total.toInt()}"
        holder.tvStatus.text = order.status

        holder.itemView.setOnClickListener {
            onClickListener(order)
        }
    }
}

class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
    override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem == newItem
    }
}