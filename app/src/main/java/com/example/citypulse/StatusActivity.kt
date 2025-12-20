package com.example.citypulse

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import androidx.activity.result.contract.ActivityResultContracts
import com.example.citypulse.databinding.ActivityStatusBinding

class StatusActivity : AppCompatActivity() {

    private lateinit var tvUserName: TextView
    private lateinit var edtStatus: EditText
    private lateinit var btnPost: Button
    private lateinit var statusSpinner: Spinner
    private lateinit var btnSelectImage: Button
    private lateinit var ivPreview: ImageView

    private var selectedCategoryColor: Int = R.color.colorRendah  // Default color for "Rendah" (Low danger)
    private var tempImageUri: Uri? = null  // Variable to store selected image URI

    // Launcher to handle image selection
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            tempImageUri = uri
            ivPreview.setImageURI(uri)  // Preview selected image in ImageView
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)

        tvUserName = findViewById(R.id.tvUserName)
        edtStatus = findViewById(R.id.edtStatus)
        btnPost = findViewById(R.id.btnPost)
        statusSpinner = findViewById(R.id.statusSpinner)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        ivPreview = findViewById(R.id.ivPreview)

        // Get current user name from Firebase Authentication
        val user = FirebaseAuth.getInstance().currentUser
        tvUserName.text = user?.displayName ?: "Nama Akun"

        // Set up Spinner for danger categories
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.danger_categories,  // Array resource from strings.xml
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        statusSpinner.adapter = adapter

        // Set color based on selected category
        statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategoryColor = when (position) {
                    0 -> ContextCompat.getColor(this@StatusActivity, R.color.colorRendah) // Low danger
                    1 -> ContextCompat.getColor(this@StatusActivity, R.color.colorSedang) // Medium danger
                    2 -> ContextCompat.getColor(this@StatusActivity, R.color.colorTinggi) // High danger
                    else -> R.color.colorRendah // Default color
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Default selection if nothing is selected
            }
        }

        // Button to select image from gallery
        btnSelectImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Post status button
        btnPost.setOnClickListener {
            postStatus()
        }
    }

    private fun postStatus() {
        val statusText = edtStatus.text.toString()
        val statusCategory = statusSpinner.selectedItem.toString()

        if (statusText.isEmpty()) {
            Toast.makeText(this, "Status tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val postData = PostData(
            title = statusCategory,
            message = statusText,
            categoryColor = selectedCategoryColor,  // Save the selected color for category
            postImageUri = tempImageUri,  // Include the selected image URI
            timestamp = System.currentTimeMillis(),
            userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        )

        // Post status to Firebase Realtime Database
        val database = FirebaseDatabase.getInstance().reference
        val postRef = database.child("posts").push() // Generate a new unique post ID
        postRef.setValue(postData)
            .addOnCompleteListener {
                Toast.makeText(this, "Status berhasil diposting", Toast.LENGTH_SHORT).show()
                finish() // Close the activity after posting
            }
            .addOnFailureListener {
                Toast.makeText(this, "Terjadi kesalahan saat posting", Toast.LENGTH_SHORT).show()
            }
    }
}
