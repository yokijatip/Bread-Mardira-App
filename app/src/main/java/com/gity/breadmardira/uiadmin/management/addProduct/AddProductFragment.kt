package com.gity.breadmardira.uiadmin.management.addProduct

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.gity.breadmardira.databinding.FragmentAddProductBinding
import kotlinx.coroutines.flow.collectLatest

class AddProductFragment : Fragment() {

    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddProductViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddProduct.setOnClickListener {
            val name = binding.etProductName.text.toString().trim()
            val desc = binding.etProductDescription.text.toString().trim()
            val priceStr = binding.etProductPrice.text.toString().trim()

            if (name.isEmpty() || desc.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(requireContext(), "Lengkapi semua field", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val price = priceStr.toDoubleOrNull()
            if (price == null) {
                Toast.makeText(requireContext(), "Harga tidak valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.addProduct(name, desc, price)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addState.collectLatest { result ->
                result?.onSuccess {
                    Toast.makeText(requireContext(), "Produk berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack() // kembali ke list/management
                }?.onFailure {
                    Toast.makeText(requireContext(), "Gagal: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}