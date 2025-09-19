package com.gity.breadmardira.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gity.breadmardira.R
import com.gity.breadmardira.adapter.CartAdapter
import com.gity.breadmardira.databinding.FragmentCartBinding
import com.gity.breadmardira.model.CartItem
import java.text.NumberFormat
import java.util.*

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CartViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup adapter dengan callback untuk update quantity dan remove item
        val adapter = CartAdapter(
            onQuantityChanged = { cartItem, newQuantity ->
                viewModel.updateCartItemQuantity(cartItem, newQuantity)
            },
            onRemoveItem = { cartItem ->
                viewModel.removeCartItem(cartItem)
            }
        )

        binding.rvCartItems.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCartItems.adapter = adapter

        // Observe data keranjang dari ViewModel
        viewModel.cartItems.observe(viewLifecycleOwner) { items: List<CartItem> ->
            adapter.submitList(items)

            // Calculate total price
            val total = items.sumOf { it.price * it.quantity }
            val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            binding.tvTotalPrice.text = "Total: ${formatter.format(total)}"

            // Enable/disable order button based on cart content
            binding.btnOrder.isEnabled = items.isNotEmpty()
        }

        // Tombol Checkout → ke CustomerDataFragment
        binding.btnOrder.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_cart_to_customerDataFragment)
        }

        // Load data awal
        viewModel.loadCart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}