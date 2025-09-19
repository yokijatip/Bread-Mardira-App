package com.gity.breadmardira.ui.cart.customerData

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.gity.breadmardira.databinding.FragmentCustomerDataBinding
import com.gity.breadmardira.model.CustomerData
import com.gity.breadmardira.utils.AddressInfo
import com.gity.breadmardira.utils.GeocodingService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

class CustomerDataFragment : Fragment() {

    private var _binding: FragmentCustomerDataBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CustomerDataViewModel by viewModels()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val geocodingService = GeocodingService()

    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null

    // Permission launcher
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            getCurrentLocation()
        } else {
            Toast.makeText(requireContext(), "Izin lokasi diperlukan untuk menentukan alamat pengiriman", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        // Button untuk mendapatkan lokasi
        binding.btnGetLocation.setOnClickListener {
            checkLocationPermissionAndGetLocation()
        }

        // Button untuk submit order
        binding.btnSubmitOrder.setOnClickListener {
            submitOrder()
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSubmitOrder.isEnabled = !isLoading
        }

        viewModel.orderSubmitted.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Pesanan berhasil dikirim!", Toast.LENGTH_LONG).show()
                // Navigate back or to success page
            }
        }
    }

    private fun checkLocationPermissionAndGetLocation() {
        when {
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getCurrentLocation()
            }
            else -> {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // Show loading
        binding.tvLocationStatus.text = "Mendapatkan lokasi..."
        binding.btnGetLocation.isEnabled = false

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            binding.btnGetLocation.isEnabled = true

            if (location != null) {
                currentLatitude = location.latitude
                currentLongitude = location.longitude

                // Update coordinate display
                binding.tvCoordinates.text = "Koordinat: ${location.latitude}, ${location.longitude}"
                binding.tvLocationStatus.text = "Mengonversi koordinat ke alamat..."

                // Convert coordinates to address
                lifecycleScope.launch {
                    val addressInfo = geocodingService.reverseGeocode(location.latitude, location.longitude)
                    updateAddressDisplay(addressInfo)
                }
            } else {
                binding.tvLocationStatus.text = "Tidak dapat mendapatkan lokasi. Coba lagi."
                Toast.makeText(requireContext(), "Tidak dapat mendapatkan lokasi. Pastikan GPS aktif.", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            binding.btnGetLocation.isEnabled = true
            binding.tvLocationStatus.text = "Gagal mendapatkan lokasi"
            Toast.makeText(requireContext(), "Gagal mendapatkan lokasi: ${it.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateAddressDisplay(addressInfo: AddressInfo?) {
        if (addressInfo != null) {
            val formattedAddress = geocodingService.formatAddress(addressInfo)

            binding.tvLocationStatus.text = "Lokasi berhasil didapatkan ✓"
            binding.tvAddress.text = formattedAddress
            binding.tvAddress.visibility = View.VISIBLE
        } else {
            binding.tvLocationStatus.text = "Gagal mengonversi koordinat ke alamat"
            binding.tvAddress.text = "Alamat tidak dapat ditentukan"
            binding.tvAddress.visibility = View.VISIBLE
        }
    }

    private fun submitOrder() {
        val name = binding.etCustomerName.text.toString().trim()
        val phone = binding.etCustomerPhone.text.toString().trim()
        val notes = binding.etOrderNotes.text.toString().trim()
        val address = binding.tvAddress.text.toString()

        // Submit order dengan data customer (pakai default koordinat kalau ga ada)
        val customerData = CustomerData(
            name = name.ifEmpty { "Customer" },
            phone = phone.ifEmpty { "000-000-0000" },
            address = address.ifEmpty { "Alamat tidak tersedia" },
            latitude = currentLatitude ?: 0.0,
            longitude = currentLongitude ?: 0.0,
            notes = notes
        )

        viewModel.submitOrder(customerData)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}