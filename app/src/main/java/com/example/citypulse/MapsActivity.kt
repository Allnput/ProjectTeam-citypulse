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

        // Inisialisasi Peta
        setupMap()

        // Inisialisasi Navigasi Bottom Bar
        setupBottomNavigation()

        // Inisialisasi Tombol Notifikasi di Header
        setupNotification()

        // --- LOGIKA FITUR FLOATING UI ---

        // 1. Klik pada Tombol Tambah (+) untuk input kejadian baru
        binding.btnAddIncident.setOnClickListener {
            val intent = Intent(this, StatusActivity::class.java)
            startActivity(intent)
        }

// 2. Klik pada Search Bar (CardView)
        binding.searchCard.setOnClickListener {
            val intent = Intent(this, StatusActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Set lokasi default ke Yogyakarta
        val jogja = LatLng(-7.797068, 110.370529)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(jogja, 13f))

        // Cek dan aktifkan izin lokasi
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
            // Tetap di halaman ini (Home)
        }

        binding.navProfile.setOnClickListener {
            // Berpindah ke Halaman Profile
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.navSetting.setOnClickListener {
            // Berpindah ke Halaman Setting
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupNotification() {
        // Mengaktifkan klik pada ikon lonceng di header biru
        binding.btnNotification.setOnClickListener {
            val intent = Intent(this, NotifActivity::class.java)
            startActivity(intent)
        }
    }
}