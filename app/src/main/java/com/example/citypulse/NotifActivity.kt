package com.example.citypulse

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class NotifActivity : AppCompatActivity() {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notif)

        // Inisialisasi map fragment
        val mapFragment = fragmentManager.findFragmentById(R.id.mapFrame) as MapFragment?
        mapFragment?.getMapAsync { googleMap ->
            mMap = googleMap
        }

        // Inisialisasi komponen UI
        val notificationList: LinearLayout = findViewById(R.id.notificationList)

        // Menambahkan notifikasi dengan data lokasi
        val notifications = listOf(
            NotificationItem("Banjir", "Banjir di daerah X.", "2h", -6.175110, 106.865039), // Contoh: Latitude dan Longitude
            NotificationItem("Macet", "Kemacetan di Jalan Y.", "1d", -6.200000, 106.820000),
            NotificationItem("Jalan Rusak", "Jalan rusak di daerah Z.", "3d", -6.140000, 106.900000)
        )

        // Menambahkan setiap notifikasi ke dalam daftar
        for (notification in notifications) {
            val notificationView = LayoutInflater.from(this).inflate(R.layout.notification_item, null)
            val tvTitle: TextView = notificationView.findViewById(R.id.tvTitle)
            val tvMessage: TextView = notificationView.findViewById(R.id.tvMessage)
            val tvTime: TextView = notificationView.findViewById(R.id.tvTime)

            tvTitle.text = notification.title
            tvMessage.text = notification.message
            tvTime.text = notification.time

            // Menambahkan click listener pada setiap notifikasi
            notificationView.setOnClickListener {
                // Menambahkan marker dan memperbesar peta ke lokasi yang diberikan
                val location = LatLng(notification.latitude, notification.longitude)
                mMap.addMarker(MarkerOptions().position(location).title(notification.title))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f)) // Memperbesar peta ke lokasi
            }

            notificationList.addView(notificationView)
        }
    }
}
