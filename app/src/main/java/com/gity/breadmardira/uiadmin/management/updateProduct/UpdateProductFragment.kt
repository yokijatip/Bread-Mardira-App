package com.gity.breadmardira.uiadmin.management.updateProduct

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gity.breadmardira.databinding.FragmentUpdateProductBinding
import com.gity.breadmardira.model.Product

class UpdateProductFragment : Fragment() {

    private var _binding: FragmentUpdateProductBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UpdateProductViewModel by viewModels()
    private val args: UpdateProductFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInitialData()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupInitialData() {
        // Populate fields dengan data dari product yang dipilih
        val product = args.product
        binding.etProductName.setText(product.name)
        binding.etProductDescription.setText(product.description)
        binding.etProductPrice.setText(product.price.toString())
    }

    private fun setupClickListeners() {
        binding.btnAddProduct.setOnClickListener {
            updateProduct()
        }
    }

    private fun updateProduct() {
        val name = binding.etProductName.text.toString().trim()
        val desc = binding.etProductDescription.text.toString().trim()
        val priceStr = binding.etProductPrice.text.toString().trim()

        if (name.isEmpty() || desc.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(requireContext(), "Lengkapi semua field", Toast.LENGTH_SHORT).show()
            return
        }

        val price = priceStr.toDoubleOrNull()
        if (price == null) {
            Toast.makeText(requireContext(), "Harga tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        // PENTING: Gunakan ID dan imageRes dari product yang dipilih
        val updatedProduct = Product(
            id = args.product.id, // Gunakan ID asli
            name = name,
            description = desc,
            price = price,
            imageRes = args.product.imageRes // JANGAN ubah imageRes
        )

        viewModel.updateProduct(updatedProduct)
    }

    private fun observeViewModel() {
        viewModel.updateSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(requireContext(), "Product berhasil diupdate!", Toast.LENGTH_SHORT).show()
                viewModel.resetUpdateSuccess()
                findNavController().navigateUp() // Kembali ke ViewProducts
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnAddProduct.isEnabled = !isLoading
            binding.btnAddProduct.text = if (isLoading) "Updating..." else "Update Product"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}