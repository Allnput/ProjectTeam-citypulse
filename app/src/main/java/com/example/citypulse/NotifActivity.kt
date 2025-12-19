package com.example.citypulse

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class NotifActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notif)

        // 1. Inisialisasi Map menggunakan SupportMapFragment (sesuai XML)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // 2. Logika Tombol Kembali ke MapsActivity
        val btnBack: Button = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            // Menggunakan FLAG_ACTIVITY_CLEAR_TOP agar tidak menumpuk halaman
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish() // Menutup NotifActivity
        }

        // 3. Inisialisasi daftar notifikasi
        setupNotificationList()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Set posisi awal (opsional)
        val defaultLoc = LatLng(-6.200000, 106.820000)
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLoc, 10f))
    }

    private fun setupNotificationList() {
        val notificationList: LinearLayout = findViewById(R.id.notificationList)

        val notifications = listOf(
            NotificationItem("Banjir", "Banjir di daerah X.", "2h", -6.175110, 106.865039),
            NotificationItem("Macet", "Kemacetan di Jalan Y.", "1d", -6.200000, 106.820000),
            NotificationItem("Jalan Rusak", "Jalan rusak di daerah Z.", "3d", -6.140000, 106.900000)
        )

        for (notification in notifications) {
            val notificationView = LayoutInflater.from(this).inflate(R.layout.notification_item, null)

            val tvTitle: TextView = notificationView.findViewById(R.id.tvTitle)
            val tvMessage: TextView = notificationView.findViewById(R.id.tvMessage)
            val tvTime: TextView = notificationView.findViewById(R.id.tvTime)

            tvTitle.text = notification.title
            tvMessage.text = notification.message
            tvTime.text = notification.time

            notificationView.setOnClickListener {
                mMap?.let { map ->
                    val location = LatLng(notification.latitude, notification.longitude)
                    map.clear() // Bersihkan marker lama
                    map.addMarker(MarkerOptions().position(location).title(notification.title))
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
                }
            }
            notificationList.addView(notificationView)
        }
    }
}