package com.gity.breadmardira.uiadmin.management

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gity.breadmardira.R
import com.gity.breadmardira.databinding.FragmentManagementBinding

class ManagementFragment : Fragment() {

    private var _binding: FragmentManagementBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ManagementViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // tombol Add Product
        binding.cardAddProduct.setOnClickListener {
            findNavController().navigate(
                R.id.action_navigation_management_to_addProductFragment
            )
        }

        // tombol View Products
        binding.cardViewProduct.setOnClickListener {
            findNavController().navigate(
                R.id.action_navigation_management_to_viewProductsFragment
            )
        }

        binding.cardDeleteProduct.setOnClickListener {
            findNavController().navigate(
                R.id.action_navigation_management_to_deleteProductFragment
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
