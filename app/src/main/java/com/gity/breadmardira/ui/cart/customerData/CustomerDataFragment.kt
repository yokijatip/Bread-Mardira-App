package com.gity.breadmardira.ui.cart.customerData

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.gity.breadmardira.databinding.FragmentCustomerDataBinding
import com.google.android.gms.location.LocationServices

@Suppress("DEPRECATION")
class CustomerDataFragment : Fragment() {

    private var _binding: FragmentCustomerDataBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = CustomerDataFragment()
    }

    private val viewModel: CustomerDataViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnGetGps.setOnClickListener { getCurrentLocation() }
    }

    private fun getCurrentLocation() {
        val fused = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fused.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    binding.tvLocation.text = "Lokasi: ${it.latitude}, ${it.longitude}"
                }
            }
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}