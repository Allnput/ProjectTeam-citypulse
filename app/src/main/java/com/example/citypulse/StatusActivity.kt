package com.example.citypulse

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultCallback

class StatusActivity : AppCompatActivity() {

    private lateinit var edtStatus: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var selectedImage: ImageView
    private lateinit var btnChooseImage: Button
    private lateinit var btnPost: Button

    private var imageUri: Uri? = null // Uri untuk gambar yang dipilih

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)

        // Inisialisasi UI components
        edtStatus = findViewById(R.id.edtStatus)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        selectedImage = findViewById(R.id.selectedImage)
        btnChooseImage = findViewById(R.id.btnChooseImage)
        btnPost = findViewById(R.id.btnPost)

        // Menambahkan tingkat bahaya ke spinner
        val dangerLevels = arrayOf("Rendah", "Sedang", "Tinggi")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dangerLevels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        // Pilih gambar
        btnChooseImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            resultLauncher.launch(intent)
        }

        // Posting status
        btnPost.setOnClickListener {
            val statusText = edtStatus.text.toString()
            val selectedCategory = spinnerCategory.selectedItem.toString()

            if (statusText.isNotEmpty() && imageUri != null) {
                savePostToDatabase(statusText, selectedCategory)
            } else {
                Toast.makeText(this, "Tolong lengkapi semua informasi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fungsi untuk menyimpan status dan gambar ke Firebase
    private fun savePostToDatabase(status: String, category: String) {
        val database = FirebaseDatabase.getInstance().getReference("posts")

        // Menyimpan gambar ke Firebase Storage
        val storageRef = FirebaseStorage.getInstance().getReference("images/${System.currentTimeMillis()}.jpg")
        storageRef.putFile(imageUri!!).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                // Menyimpan URL gambar ke database bersama data lainnya
                val postId = database.push().key
                val postData = PostData(
                    status = status,
                    category = category,
                    imageUrl = uri.toString() // Menyimpan URL gambar
                )

                // Menyimpan data ke Firebase
                if (postId != null) {
                    database.child(postId).setValue(postData).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Posting berhasil", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "Gagal menyimpan postingan", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    // Menangani hasil pemilihan gambar dari galeri
    private val resultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                if (data != null) {
                    imageUri = data.data
                    selectedImage.setImageURI(imageUri)  // Set gambar yang dipilih ke ImageView
                }
            }
        }
}
