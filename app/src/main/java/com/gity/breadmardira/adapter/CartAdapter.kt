package com.gity.breadmardira.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gity.breadmardira.R
import com.gity.breadmardira.databinding.ItemCartBinding
import com.gity.breadmardira.model.CartItem
import java.text.NumberFormat
import java.util.*

class CartAdapter(
    private val onQuantityChanged: (CartItem, Int) -> Unit = { _, _ -> },
    private val onRemoveItem: (CartItem) -> Unit = { }
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(
        private val binding: ItemCartBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            with(binding) {
                // Set product name
                tvCartName.text = cartItem.name

                // Set product price (format to Indonesian Rupiah)
                val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                tvCartPrice.text = formatter.format(cartItem.price)

                // Set quantity
                tvQuantity.text = cartItem.quantity.toString()

                // Set total price for this item
                val totalPrice = cartItem.price * cartItem.quantity

                // Load product image using Glide (since you're using Cloudinary)
                Glide.with(itemView.context)
                    .load(cartItem.imageRes)
                    .placeholder(R.drawable.ic_image_placeholder) // Ganti dengan placeholder yang sesuai
                    .into(imgCart)

                // Handle quantity increase
                btnPlus.setOnClickListener {
                    val newQuantity = cartItem.quantity + 1
                    onQuantityChanged(cartItem, newQuantity)
                }

                // Handle quantity decrease
                btnMinus.setOnClickListener {
                    val newQuantity = (cartItem.quantity - 1).coerceAtLeast(1)
                    onQuantityChanged(cartItem, newQuantity)
                }

                // Handle remove item
                btnRemove.setOnClickListener {
                    onRemoveItem(cartItem)
                }
            }
        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}