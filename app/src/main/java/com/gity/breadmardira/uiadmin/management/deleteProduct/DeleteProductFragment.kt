package com.gity.breadmardira.uiadmin.management.deleteProduct

import ProductAdapter
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.gity.breadmardira.databinding.FragmentDeleteProductBinding

class DeleteProductFragment : Fragment() {

    private var _binding: FragmentDeleteProductBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DeleteProductViewModel by viewModels()
    private lateinit var productAdapter: ProductAdapter

    private var productId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeleteProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productAdapter = ProductAdapter(emptyList()) {
            // Handle item click if needed
            // Tampilkan dialog konfirmasi sebelum menghapus
            AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Hapus")
                .setMessage("Apakah Anda yakin ingin menghapus produk ini?")
                .setPositiveButton("Ya") { _, _ ->
                    // Panggil fungsi deleteProduct jika pengguna menekan "Ya"
                    productId?.let {
                        viewModel.deleteProduct(it)
                    }
                }
                .setNegativeButton("Tidak", null) // Tidak ada tindakan jika pengguna menekan "Tidak"
                .show()

        }

        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
        }


        observeViewModel()
        viewModel.loadProducts()
    }

    private fun observeViewModel() {
        viewModel.products.observe(viewLifecycleOwner) { list ->
            productAdapter.updateData(list)
            if (list.isNotEmpty()){
                productId = list[0].id
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { err ->
            err?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteProduct(productId: String) {
        binding.apply {
            viewModel.deleteProduct(productId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}