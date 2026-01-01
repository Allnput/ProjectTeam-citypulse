package com.example.citypulse

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class PickLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var selectedLatLng: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_location)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_pick) as SupportMapFragment
        mapFragment.getMapAsync(this)

        findViewById<Button>(R.id.btnConfirmLocation).setOnClickListener {
            if (selectedLatLng != null) {
                val intent = Intent()
                intent.putExtra("LAT", selectedLatLng!!.latitude)
                intent.putExtra("LNG", selectedLatLng!!.longitude)
                setResult(RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(this, "Silakan pilih lokasi di peta terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val startLoc = LatLng(-7.797068, 110.370529)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLoc, 15f))
        mMap.setOnMapClickListener { latLng ->
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(latLng).title("Lokasi Kejadian"))
            selectedLatLng = latLng
        }
    }
}