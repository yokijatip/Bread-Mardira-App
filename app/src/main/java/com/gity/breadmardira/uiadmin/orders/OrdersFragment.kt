package com.gity.breadmardira.uiadmin.orders

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gity.breadmardira.adapter.OrderAdapter
import com.gity.breadmardira.databinding.FragmentOrdersBinding

class OrdersFragment : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrdersViewModel by viewModels()
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(emptyList()) { order ->
            // Arahkan ke DetailOrderFragment menggunakan SafeArgs
            val action = OrdersFragmentDirections.actionNavigationOrdersToDetailOrderFragment(order)
            findNavController().navigate(action)
        }

        binding.rvOrders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        // Observer untuk orders
        viewModel.orders.observe(viewLifecycleOwner) { orders ->
            Log.d("OrdersFragment", "Orders observed: ${orders?.size ?: 0} items")

            if (orders != null && orders.isNotEmpty()) {
                Log.d("OrdersFragment", "Successfully retrieved ${orders.size} orders.")
                binding.rvOrders.visibility = View.VISIBLE
                // Pastikan untuk memanggil updateData atau notifyDataSetChanged
                orderAdapter.updateData(orders)

                // Log detail orders untuk debugging
                orders.forEachIndexed { index, order ->
                    Log.d("OrdersFragment", "Order $index: ID=${order.id}, Status=${order.status}, Total=${order.total}")
                }
            } else {
                Log.d("OrdersFragment", "No orders found or orders list is empty.")
                // Tampilkan pesan kosong atau hide RecyclerView
                binding.rvOrders.visibility = View.GONE
            }
        }

        // Observer untuk error
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Log.e("OrdersFragment", "Error observed: $error")
                // Tampilkan error ke user, misalnya dengan Toast atau Snackbar
                // Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
            }
        }

        // Observer untuk loading state (jika ada)
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            Log.d("OrdersFragment", "Loading state: $isLoading")
            // Tampilkan/sembunyikan loading indicator
            // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh data ketika fragment di-resume
        Log.d("OrdersFragment", "Fragment resumed, refreshing orders...")
        viewModel.fetchOrders()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}