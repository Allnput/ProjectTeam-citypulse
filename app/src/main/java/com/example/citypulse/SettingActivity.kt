package com.example.citypulse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.citypulse.databinding.ActivitySettingBinding
import com.google.firebase.database.FirebaseDatabase
import androidx.core.content.edit

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    private val database = FirebaseDatabase.getInstance().getReference("users")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Notifikasi Aktif", Toast.LENGTH_SHORT).show()
                binding.itemNotifications.getChildAt(0).alpha = 1.0f
            } else {
                Toast.makeText(this, "Notifikasi Dimatikan", Toast.LENGTH_SHORT).show()
                binding.itemNotifications.getChildAt(0).alpha = 0.5f
            }
        }
        binding.btnDeactivate.setOnClickListener {
            showDeactivateDialog()
        }
        binding.btnLogout.setOnClickListener {
            performLogout()
        }
        binding.navHome.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
            finish()
        }
        binding.navProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
    private fun performLogout() {
        val sharedPrefs = getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE)
        sharedPrefs.edit {
            clear()
        }

        Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show()
        navigateToRegister()
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showDeactivateDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Hapus Akun Permanen")
            setMessage("Apakah Anda yakin? Data di database akan dihapus permanen dan Anda harus mendaftar ulang.")
            setPositiveButton("Ya, Hapus") { _, _ -> deleteAccountProcess() }
            setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            show()
        }
    }
    private fun deleteAccountProcess() {
        val sharedPrefs = getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE)
        val userId = sharedPrefs.getString("CURRENT_USER_ID", "")

        if (!userId.isNullOrEmpty()) {
            database.child(userId).removeValue()
                .addOnSuccessListener {
                    Toast.makeText(this, "Akun $userId dihapus", Toast.LENGTH_LONG).show()
                    sharedPrefs.edit().clear().apply()
                    navigateToRegister()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Gagal hapus di Firebase: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            Toast.makeText(this, "Gagal: Sesi ID User tidak ditemukan", Toast.LENGTH_SHORT).show()
            sharedPrefs.edit().clear().apply()
            navigateToRegister()
        }
    }
}