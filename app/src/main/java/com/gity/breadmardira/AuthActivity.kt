package com.gity.breadmardira

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gity.breadmardira.databinding.ActivityAuthBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tampilkan splash lebih dulu
        setContentView(R.layout.activity_splash)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            Firebase.firestore.collection("users")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { doc ->
                    val role = doc.getString("role") ?: "customer"
                    if (role == "admin") {
                        startActivity(Intent(this, AdminActivity::class.java))
                    } else {
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    finish()
                }
                .addOnFailureListener {
                    // jika gagal cek role, tampilkan Auth UI normal
                    showAuthUI()
                }
        } else {
            // Tidak login, tampilkan Auth UI normal
            showAuthUI()
        }
    }

    private fun showAuthUI() {
        // baru load tampilan login/register
        val binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}


