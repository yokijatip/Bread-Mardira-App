package com.gity.breadmardira

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.gity.breadmardira.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.*
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() } // 🔑 FirebaseAuth instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    /** -------- MENU BAR ---------- */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                showLogoutDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /** -------- LOGOUT ---------- */
    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                // 🔑 1. Sign out dari FirebaseAuth
                firebaseAuth.signOut()
                // 🔑 3. Arahkan ke AuthActivity (login/register)
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            }
            .setNegativeButton("No", null)
            .create()
            .show()
    }

    /** ===== UI Handler sesuai Role ===== */
    private fun showAdminUI() {
        // contoh menampilkan menu khusus admin
        // binding.navView.menu.findItem(R.id.navigation_dashboard).isVisible = true
    }

    private fun showCustomerUI() {
        // contoh menyembunyikan menu untuk customer
        // binding.navView.menu.findItem(R.id.navigation_dashboard).isVisible = false
    }
}
