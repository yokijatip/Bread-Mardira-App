package com.gity.breadmardira.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gity.breadmardira.MainActivity
import com.gity.breadmardira.R
import com.gity.breadmardira.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = LoginFragment()
    }


    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        Menjalankan fungsi navigasi ke register
        binding.tvToRegister.setOnClickListener {
            navigateToRegister()
        }

        // sementara langsung buka MainActivity saat button login ditekan
        binding.btnLogin.setOnClickListener {
            login()
        }

        viewModel.loginResult.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                // Login berhasil, arahkan ke MainActivity
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            } else {
                Toast.makeText(requireContext(), "Login gagal", Toast.LENGTH_SHORT).show()
            }
            // Login gagal, tampilkan pesan kesalahan atau lakukan tindakan lain sesuai kebutuhan
        }
    }

    //    Login Process and get Username dan Password
    private fun login() {
        val username = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()
        viewModel.login(username, password)
    }

    //    Navigate ke Register Fragment
    private fun navigateToRegister() {
        findNavController().navigate(
            R.id.action_loginFragment_to_registerFragment
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}