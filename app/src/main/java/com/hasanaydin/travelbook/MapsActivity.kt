package com.hasanaydin.travelbook

import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Camera
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.hasanaydin.travelbook.databinding.ActivityMapsBinding
import java.lang.Exception
import java.util.*
import java.util.jar.Manifest

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    //

    private lateinit var locationManager : LocationManager
    private lateinit var locationListener: LocationListener

    //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapLongClickListener(myListener)

        // LocationManager & LocationListener

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener{
            override fun onLocationChanged(location: Location) {

                if (location != null){

                    val sharedPreferences = this@MapsActivity.getSharedPreferences("com.hasanaydin.travelbook",Context.MODE_PRIVATE)
                    val firstTimeCheck = sharedPreferences.getBoolean("notFirstTime",false)
                    if (firstTimeCheck == false){

                        mMap.clear()
                        val newUserLocation = LatLng(location.latitude,location.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newUserLocation,15f))
                        mMap.addMarker(MarkerOptions().position(newUserLocation).title("Your Location"))
                        sharedPreferences.edit().putBoolean("notFirstTime",true).apply()

                    }

                }

            }



        }

        //  Permissions

        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1)
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2,2f,locationListener)

            val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastLocation != null){
                mMap.clear()
                val lastLocationLatLng = LatLng(lastLocation.latitude,lastLocation.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocationLatLng,15f))
                mMap.addMarker(MarkerOptions().position(lastLocationLatLng).title("Your Location"))
            }
        }

    }

    val myListener = object : GoogleMap.OnMapLongClickListener{
        override fun onMapLongClick(p0: LatLng) {
            val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
            var address = ""

            if (p0 != null){

                try {
                    val addressList = geocoder.getFromLocation(p0.latitude,p0.longitude,1)

                    if(addressList != null && addressList.size > 0){

                        if(addressList[0].thoroughfare != null){
                            address += addressList[0].thoroughfare
                            if (addressList[0].subThoroughfare != null){
                                address += addressList[0].subThoroughfare
                            }
                        }

                    }else{
                        address = "New Place"
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
                mMap.clear()

                mMap.addMarker(MarkerOptions().position(p0).title(address))

                val dialog = AlertDialog.Builder(this@MapsActivity)
                dialog.setCancelable(false)
                dialog.setTitle("Are You Sure")
                dialog.setMessage(address)
                dialog.setPositiveButton("Yes"){ dialog, which ->

                    // SQLite Save

                }.setNegativeButton("No",) { dialog, which ->
                    Toast.makeText(this@MapsActivity, "Canceled!", Toast.LENGTH_LONG).show()
                }
                dialog.show()
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == 1){
            if (grantResults.size > 0){
                if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2,2f,locationListener)
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}