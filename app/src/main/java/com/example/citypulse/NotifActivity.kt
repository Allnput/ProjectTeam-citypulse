package com.example.citypulse

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotifActivity : AppCompatActivity() {

    private var mMap: GoogleMap? = null
    private lateinit var postRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notif)

        val notificationList: LinearLayout = findViewById(R.id.notificationList)
        val btnBack: Button = findViewById(R.id.btnBack)

        btnBack.setOnClickListener { finish() }

        // Firebase node posts
        postRef = FirebaseDatabase.getInstance().getReference("posts")

        postRef.limitToLast(50).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notificationList.removeAllViews()

                val items = mutableListOf<PostData>()
                for (data in snapshot.children) {
                    val post = data.getValue(PostData::class.java) ?: continue
                    items.add(post)
                }

                items.sortByDescending { it.timestamp }

                for (post in items) {
                    val itemView = NotificationItemView(this@NotifActivity)

                    val timeText = if (post.timestamp > 0L) {
                        SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
                            .format(Date(post.timestamp))
                    } else {
                        "-"
                    }

                    itemView.bind(
                        title = post.title,
                        message = post.message,
                        time = timeText
                    )

                    // Menambahkan setOnClickListener pada setiap notifikasi
                    itemView.setOnClickListener {
                        // Membuat Intent untuk menuju MapsActivity
                        val intent = Intent(this@NotifActivity, MapsActivity::class.java)
                        // Gunakan this, tanpa @NotifActivity


                        // Menyertakan data lokasi (latitude dan longitude)
                        intent.putExtra("LATITUDE", post.latitude ?: 0.0)
                        intent.putExtra("LONGITUDE", post.longitude ?: 0.0)
                        intent.putExtra("TITLE", post.title)

                        // Menjalankan Intent untuk pindah ke MapsActivity
                        startActivity(intent)
                    }


                    notificationList.addView(itemView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
            }
        })
    }
}

