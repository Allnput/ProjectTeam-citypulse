package com.example.citypulse

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class StatusActivity : AppCompatActivity() {

    private lateinit var edtStatus: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var selectedImage: ImageView
    private lateinit var btnChooseImage: Button
    private lateinit var btnPost: Button
    private lateinit var btnBack: ImageView

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)

        // 1. Inisialisasi UI
        edtStatus = findViewById(R.id.edtStatus)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        selectedImage = findViewById(R.id.selectedImage)
        btnChooseImage = findViewById(R.id.btnChooseImage)
        btnPost = findViewById(R.id.btnPost)
        // btnBack = findViewById(R.id.btnBack) // Opsional jika ada tombol kembali di header

        // 2. Setup Spinner
        val dangerLevels = arrayOf("Rendah", "Sedang", "Tinggi")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dangerLevels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        // 3. Pilih Gambar
        btnChooseImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            resultLauncher.launch(intent)
        }

        // 4. Posting Status
        btnPost.setOnClickListener {
            val statusText = edtStatus.text.toString().trim()
            val selectedCategory = spinnerCategory.selectedItem.toString()

            if (statusText.isNotEmpty() && imageUri != null) {
                uploadImageAndSavePost(statusText, selectedCategory)
            } else {
                Toast.makeText(this, "Tolong isi deskripsi dan pilih gambar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImageAndSavePost(status: String, category: String) {
        // Tampilkan loading sederhana
        btnPost.isEnabled = false
        btnPost.text = "Mengirim..."

        val storageRef = FirebaseStorage.getInstance().getReference("post_images/${System.currentTimeMillis()}.jpg")

        imageUri?.let { uri ->
            storageRef.putFile(uri).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    saveToRealtimeDatabase(status, category, downloadUrl.toString())
                }
            }.addOnFailureListener {
                btnPost.isEnabled = true
                btnPost.text = "POST"
                Toast.makeText(this, "Gagal upload gambar: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveToRealtimeDatabase(status: String, category: String, imageUrl: String) {
        val database = FirebaseDatabase.getInstance().getReference("posts")

        // AMBIL USER ID dari SharedPreferences (Sangat Penting untuk Profile)
        val sharedPrefs = getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE)
        val currentUserId = sharedPrefs.getString("CURRENT_USER_ID", "") ?: "anonymous"

        val postId = database.push().key

        // Data Postingan Lengkap
        val postData = PostData(
            status = status,
            category = category,
            imageUrl = imageUrl,
            userId = currentUserId, // Menghubungkan postingan dengan akun user
            latitude = -7.797068,   // Koordinat contoh (Yogyakarta)
            longitude = 110.370529  // Di masa depan bisa diambil dari GPS MapsActivity
        )

        if (postId != null) {
            database.child(postId).setValue(postData).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Berhasil diposting!", Toast.LENGTH_SHORT).show()
                    // Kembali ke MapsActivity dan tutup halaman ini
                    finish()
                } else {
                    btnPost.isEnabled = true
                    btnPost.text = "POST"
                    Toast.makeText(this, "Gagal simpan data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            imageUri = result.data?.data
            selectedImage.setImageURI(imageUri)
            selectedImage.visibility = android.view.View.VISIBLE
        }
    }
}