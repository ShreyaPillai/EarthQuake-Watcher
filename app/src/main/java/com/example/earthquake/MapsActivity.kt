package com.example.earthquake

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.earthquake.Model.EarthQuake
import com.example.earthquake.UI.CustomsWindow
import com.example.earthquake.Util.Constants
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONException
import java.text.DateFormat
import java.util.*

class MapsActivity : FragmentActivity(), OnMapReadyCallback, OnInfoWindowClickListener, OnMarkerClickListener {
    private var mMap: GoogleMap? = null
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null
    private var queue: RequestQueue? = null
    private val moreInfo: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        queue = Volley.newRequestQueue(this)
        earthQuakes
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.setInfoWindowAdapter(CustomsWindow(applicationContext))
        mMap!!.setOnInfoWindowClickListener(this)
        mMap!!.setOnMarkerClickListener(this)
        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // here to request the missing permissions, and then overriding
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        } else {
            locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
            }
        }
    }//Get geometry object
    //get coordinates array

    // Log.d("Quake",  lon + " " + lat);
    /** get all earthquake objects
     *
     */
    private val earthQuakes: Unit
        private get() {
            val earthQuake = EarthQuake()
            val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, Constants.URL, null, { response ->
                try {
                    val features = response.getJSONArray("features")
                    for (i in 0 until Constants.LIMIT) {
                        val properties = features.getJSONObject(i).getJSONObject("properties")


                        //Get geometry object
                        val geometry = features.getJSONObject(i).getJSONObject("geometry")
                        //get coordinates array
                        val coordinates = geometry.getJSONArray("coordinates")
                        val lon = coordinates.getDouble(0)
                        val lat = coordinates.getDouble(1)

                        // Log.d("Quake",  lon + " " + lat);
                        earthQuake.place = properties.getString("place")
                        earthQuake.type = properties.getString("type")
                        earthQuake.time = properties.getLong("time")
                        earthQuake.magnitude = properties.getDouble("mag")
                        earthQuake.detailLink = properties.getString("detail")
                        val dateFormat = DateFormat.getDateInstance()
                        val formattedDate = dateFormat.format(Date(java.lang.Long.valueOf(properties.getLong("time")))
                                .time)
                        val markerOptions = MarkerOptions()
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                        markerOptions.title(earthQuake.place)
                        markerOptions.position(LatLng(lat, lon))
                        markerOptions.snippet("""
    Magnitude: ${earthQuake.magnitude}
    Date: $formattedDate
    Type: ${earthQuake.type}
    """.trimIndent())
                        val marker = mMap!!.addMarker(markerOptions)
                        marker.tag = earthQuake.detailLink
                        mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lon), 1f))
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }) { }
            queue!!.add(jsonObjectRequest)
        }

    override fun onInfoWindowClick(marker: Marker) {
        getQuakeDetails("https://earthquake.usgs.gov/earthquakes/feed/v1.0/detail/nc73464341.geojson")
    }

    private fun getQuakeDetails(url: String) {
        
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        return false
    }
}