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

    // Inisialisasi Database Reference
    private val database = FirebaseDatabase.getInstance().getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Logika Switch Notifikasi
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Aksi saat aktif
                Toast.makeText(this, "Notifikasi Aktif", Toast.LENGTH_SHORT).show()
                // Contoh: mengubah warna icon notifikasi menjadi biru saat aktif
                binding.itemNotifications.getChildAt(0).alpha = 1.0f
            } else {
                // Aksi saat mati
                Toast.makeText(this, "Notifikasi Dimatikan", Toast.LENGTH_SHORT).show()
                binding.itemNotifications.getChildAt(0).alpha = 0.5f
            }
        }


        // 3. Tombol Deactivate Account (Hapus Permanen)
        binding.btnDeactivate.setOnClickListener {
            showDeactivateDialog()
        }

        // 4. Tombol Logout (Kembali ke Register)
        binding.btnLogout.setOnClickListener {
            performLogout()
        }

        // --- Navigasi Bottom Bar ---
        binding.navHome.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
            finish()
        }

        binding.navProfile.setOnClickListener {
            // Berpindah ke Halaman Profile
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performLogout() {
        val sharedPrefs = getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE)
        sharedPrefs.edit {
            // Sesuai permintaan: Logout reset status agar bisa register lagi
            clear()
        }

        Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show()
        navigateToRegister()
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        // Clear task agar user tidak bisa back ke halaman setting
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

        // Pastikan key "CURRENT_USER_ID" sama persis dengan yang di-set saat login
        val userId = sharedPrefs.getString("CURRENT_USER_ID", "")

        if (!userId.isNullOrEmpty()) {
            // Melakukan penghapusan di node 'users/ID_USER_NYA'
            database.child(userId).removeValue()
                .addOnSuccessListener {
                    Toast.makeText(this, "Akun $userId dihapus", Toast.LENGTH_LONG).show()

                    // Bersihkan SharedPreferences total
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
            // Jika masuk ke sini, berarti SharedPreferences CURRENT_USER_ID Anda kosong
            Toast.makeText(this, "Gagal: Sesi ID User tidak ditemukan", Toast.LENGTH_SHORT).show()
            sharedPrefs.edit().clear().apply()
            navigateToRegister()
        }
    }
}