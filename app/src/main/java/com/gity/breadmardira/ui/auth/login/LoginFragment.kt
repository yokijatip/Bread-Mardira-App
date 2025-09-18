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
import com.gity.breadmardira.R
import com.gity.breadmardira.databinding.FragmentLoginBinding
import com.gity.breadmardira.MainActivity
import com.gity.breadmardira.AdminActivity   // buat Activity ini untuk admin

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            viewModel.login(email, password)
        }

        navigateToRegister()

        viewModel.loginState.observe(viewLifecycleOwner) { result ->
            result.onSuccess { role ->
                Toast.makeText(requireContext(), "Login sukses", Toast.LENGTH_SHORT).show()

                if (role == "admin") {
                    // ke halaman admin
                    startActivity(Intent(requireContext(), AdminActivity::class.java))
                } else {
                    // ke halaman customer (MainActivity yang pakai mobile_navigation)
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                }

                requireActivity().finish() // tutup AuthActivity
            }.onFailure {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToRegister() {
        binding.tvToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
