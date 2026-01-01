package com.example.citypulse

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.citypulse.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapsBinding
    private var mMap: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val database = FirebaseDatabase.getInstance().getReference("posts")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setupMap()
        setupBottomNavigation()
        setupNotification()
        binding.btnAddIncident.setOnClickListener {
            startActivity(Intent(this, StatusActivity::class.java))
        }
        binding.searchCard.setOnClickListener {
            startActivity(Intent(this, StatusActivity::class.java))
        }
    }
    private fun setupMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntentLocation(intent)
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.setPadding(0, 210, 0, 180)
        setupCustomInfoWindow()

        enableMyLocation()
        fetchMarkersFromFirebase()
        handleIntentLocation(intent)
    }
    private fun setupCustomInfoWindow() {
        mMap?.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoWindow(marker: Marker): View? {
                return null
            }
            override fun getInfoContents(marker: Marker): View {
                val infoView = layoutInflater.inflate(R.layout.custom_info_window, null)
                val post = marker.tag as? PostData

                val tvTitle: TextView = infoView.findViewById(R.id.infoTitle)
                val tvSnippet: TextView = infoView.findViewById(R.id.infoSnippet)
                val ivImage: ImageView = infoView.findViewById(R.id.infoImage)

                tvTitle.text = marker.title
                tvSnippet.text = marker.snippet
                if (post != null && !post.imageUrl.isNullOrEmpty()) {
                    try {
                        val imageBytes = Base64.decode(post.imageUrl, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        ivImage.setImageBitmap(bitmap)
                        ivImage.visibility = View.VISIBLE
                    } catch (e: Exception) {
                        ivImage.visibility = View.GONE
                    }
                } else {
                    ivImage.visibility = View.GONE
                }

                return infoView
            }
        })
        mMap?.setOnInfoWindowClickListener { marker ->
            Toast.makeText(this, "Melihat detail ${marker.title}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun fetchMarkersFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mMap?.clear()
                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(PostData::class.java)
                    if (post != null && post.latitude != 0.0) {
                        val location = LatLng(post.latitude, post.longitude)
                        val markerColor = when (post.kategori) {
                            "Rendah" -> BitmapDescriptorFactory.HUE_GREEN
                            "Sedang" -> BitmapDescriptorFactory.HUE_YELLOW
                            "Darurat" -> BitmapDescriptorFactory.HUE_RED
                            else -> BitmapDescriptorFactory.HUE_BLUE
                        }

                        val marker = mMap?.addMarker(
                            MarkerOptions()
                                .position(location)
                                .title(post.kategori)
                                .snippet("${post.pembuat}: ${post.deskripsi}")
                                .icon(BitmapDescriptorFactory.defaultMarker(markerColor))
                        )
                        marker?.tag = post
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun handleIntentLocation(intent: Intent) {
        val targetLat = intent.getDoubleExtra("EXTRA_LAT", 0.0)
        val targetLng = intent.getDoubleExtra("EXTRA_LNG", 0.0)

        if (targetLat != 0.0 && targetLng != 0.0) {
            val incidentLoc = LatLng(targetLat, targetLng)
            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(incidentLoc, 17f))
        } else {
            getDeviceLocation()
        }
    }
    private fun getDeviceLocation() {
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful && task.result != null) {
                        val loc = task.result
                        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            LatLng(loc.latitude, loc.longitude), 15f))
                    } else {
                        val jogja = LatLng(-7.797068, 110.370529)
                        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(jogja, 13f))
                    }
                }
            }
        } catch (e: SecurityException) { e.printStackTrace() }
    }
    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mMap?.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        }
    }
    private fun setupBottomNavigation() {
        binding.navHome.setOnClickListener { /* Sudah di Home */ }
        binding.navProfile.setOnClickListener { startActivity(Intent(this, ProfileActivity::class.java)) }
        binding.navSetting.setOnClickListener { startActivity(Intent(this, SettingActivity::class.java)) }
    }
    private fun setupNotification() {
        binding.btnNotification.setOnClickListener { startActivity(Intent(this, NotifActivity::class.java)) }
    }
}