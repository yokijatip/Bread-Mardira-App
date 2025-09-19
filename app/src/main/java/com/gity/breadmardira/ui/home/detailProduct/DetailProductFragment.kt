package com.gity.breadmardira.ui.home.detailProduct

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.gity.breadmardira.databinding.FragmentDetailProductBinding
import com.gity.breadmardira.repository.CartRepository
import kotlinx.coroutines.launch

class DetailProductFragment : Fragment() {

    private var _binding: FragmentDetailProductBinding? = null
    private val binding get() = _binding!!

    private val args: DetailProductFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product

        // Tampilkan data produk
        binding.tvName.text = product.name
        binding.tvPrice.text = "Rp ${product.price}"
        binding.tvDescription.text = product.description

        // Tampilkan gambar dari drawable resource name (misal: "image1")
        val imageProduct = product.imageRes
        Glide.with(this)
            .load(imageProduct)
            .placeholder(android.R.color.darker_gray)
            .into(binding.ivProduct)

        // Tombol tambah ke keranjang
        binding.btnAddToCart.setOnClickListener {
            lifecycleScope.launch {
                val result = CartRepository.addToCart(product)
                if (result.isSuccess) {
                    Toast.makeText(requireContext(), "Ditambahkan ke keranjang", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(),
                        "Gagal menambahkan: ${result.exceptionOrNull()?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
