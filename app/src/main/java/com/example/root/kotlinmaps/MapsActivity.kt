package com.example.root.kotlinmaps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    var locationManager: LocationManager? = null
    var locationListener: LocationListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapLongClickListener(myListener)

//        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = object : LocationListener{
            override fun onLocationChanged(p0: Location?) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

                if (p0 != null){

                    //erase old markers
                    mMap.clear()

                    var userLocation = LatLng(p0!!.latitude, p0!!.longitude)
                    mMap.addMarker(MarkerOptions().position(userLocation).title("Your Location"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,20f))

                    val geocoder = Geocoder(applicationContext, Locale.getDefault())

                    try {

                        val addressList = geocoder.getFromLocation(p0.latitude, p0.longitude, 1)

                        if (addressList != null && addressList.size > 0){
                            println("Address info: " +addressList[0].toString())
                        }
                    }catch (e: Exception){
                        e.printStackTrace()
                    }

                }



            }

            override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onProviderEnabled(p0: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onProviderDisabled(p0: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)

        }else{

            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER,100,2f,locationListener)

            var lastLocation = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            var userLastLocation = LatLng(lastLocation.latitude, lastLocation.longitude )

            mMap.addMarker(MarkerOptions().position(userLastLocation).title("Your Location"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLastLocation,20f))
        }



    }

    val myListener = object : GoogleMap.OnMapLongClickListener{
        override fun onMapLongClick(p0: LatLng?) {

            mMap.clear()

            var address = ""

            val geocoder = Geocoder(applicationContext, Locale.getDefault())
            try {
                val addressList = geocoder.getFromLocation(p0!!.latitude, p0!!.longitude,1)

                if (addressList != null && addressList.size >0){

                    address += addressList[0].thoroughfare

                    if (addressList[0].subThoroughfare != null){
                        address += addressList[0].subThoroughfare
                    }
                }


            }catch (e: Exception){

                e.printStackTrace()

            }

            if (address.equals("")){
                address = "No Address"
            }
            mMap.addMarker(MarkerOptions().position(p0!!).title(address))

        }

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode == 1){

            if (grantResults.size > 0 ){
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED){
                    locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 2f, locationListener)

                }
            }

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


}
