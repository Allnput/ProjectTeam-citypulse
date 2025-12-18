package com.example.citypulse

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.citypulse.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapsBinding
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMap()
        setupBottomNavigation()

        // --- TAMBAHAN UNTUK FITUR BARU ---

        // 1. Klik pada Tombol Tambah (+)
        binding.btnAddIncident.setOnClickListener {
            // Membuka Activity PostData untuk input kejadian
            val intent = Intent(this, PostData::class.java)
            startActivity(intent)
        }

        // 2. Klik pada Search Bar (CardView)
        binding.searchCard.setOnClickListener {
            // Anda bisa mengarahkan ke halaman pencarian atau
            // memunculkan keyboard di sini
        }

        // Mengaktifkan kembali tombol notifikasi yang tadi di comment
//        binding.btnNotification.setOnClickListener {
//            val intent = Intent(this, NotifActivity::class.java)
//            startActivity(intent)
//        }

//        setupNotification() // Tetap saya biarkan seperti permintaan anda
    }

    private fun setupMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val jogja = LatLng(-7.797068, 110.370529)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(jogja, 13f))

        enableMyLocation()
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
        }
    }

    private fun setupBottomNavigation() {
        binding.navHome.setOnClickListener {
            // sudah di home
        }

//        binding.navProfile.setOnClickListener {
//            // Biasanya profile diarahkan ke ProfileActivity,
//            // pastikan ProfileActivity sudah dibuat
//            startActivity(Intent(this, ProfileActivity::class.java))
//        }

        binding.navSetting.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
    }

//    private fun setupNotification() {
//        binding.btnNotification.setOnClickListener {
//            startActivity(Intent(this, NotifActivity::class.java))
//        }
//    }
}