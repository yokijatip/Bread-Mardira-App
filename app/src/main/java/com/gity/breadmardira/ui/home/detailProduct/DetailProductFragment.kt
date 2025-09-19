package com.gity.breadmardira.ui.home.detailProduct

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gity.breadmardira.R
import com.gity.breadmardira.databinding.FragmentDetailProductBinding

class DetailProductFragment : Fragment() {

    private var _binding: FragmentDetailProductBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = DetailProductFragmentArgs.fromBundle(requireArguments())
        val product = args.product

        binding.tvName.text = product.name
        binding.tvDescription.text = product.description
        binding.tvPrice.text = "Rp ${product.price}"
        binding.ivProduct.setImageResource(
            resources.getIdentifier(product.imageRes, "drawable", requireContext().packageName)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
