package com.example.myapplication

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.INTERNET
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.cougarhunt.LocationLocal
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    lateinit var mMap: GoogleMap
    lateinit var lastLocation: Location
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback
    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
            }

            permissions.getOrDefault(ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
            }
            permissions.getOrDefault(INTERNET, false) -> {
            // asdf a
            }

            else -> {
                // No location access granted.
            }
        }
    }
    fun requestLocationPermission() {
        locationPermissionRequest.launch(
            arrayOf(
                ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION,
                INTERNET
            )
        )
    }
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)



        //behavior for back button
        val backButton: Button = findViewById(R.id.button3)
        backButton.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true

        mMap.setOnMarkerClickListener (this)

        // Add the locationLocal pin


        setUpMap()

        //setUpLocationTracking()
    }


    private fun setUpMap(){
        var currentLatLong = LatLng(0.0,0.0)
        if(ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            requestLocationPermission()
            return
        }
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) {location ->
            if (location != null){
                lastLocation = location
                currentLatLong = LatLng(location.latitude,location.longitude)
                placeMarkerOnMap(currentLatLong)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong,12f))
            }
        }
        val locationLocal = intent.getParcelableExtra<LocationLocal>("location")
        if (locationLocal != null) {
            placeDestination(locationLocal.latLng)
            plotRoute(currentLatLong,locationLocal.latLng)
        }

    }

    private fun placeDestination(destination: LatLng){
        val destinationPin = MarkerOptions().position(destination)
        destinationPin.title("$destination")
        mMap.addMarker(destinationPin)
    }
    private fun placeMarkerOnMap(currentLatLong: LatLng){
        val markerOptions = MarkerOptions().position(currentLatLong)

        markerOptions.title("$currentLatLong")

    }
    interface DirectionsApiService {
        @GET("directions/json")
        suspend fun getDirections(
            @Query("origin") origin: String,
            @Query("destination") destination: String,
            @Query("key") apiKey: String
        ): DirectionsResponse
    }
    // Data classes to parse the Directions API response
    data class DirectionsResponse(
        val routes: List<Route>
    )

    data class Route(
        val overview_polyline: PolylineData
    )

    data class PolylineData(
        val points: String
    )
    private fun plotRoute(currentLatLong: LatLng,destination: LatLng){
// Define the origin and destination in "lat,lng" format
        val origin = "${currentLatLong.latitude},${currentLatLong.longitude}"
        val dest = "${destination.latitude},${destination.longitude}"

        // Replace with your actual API Key from Google Cloud Console
        val apiKey = "$YOUR_API_KEY"

        // Create Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create the Directions API service
        val service = retrofit.create(DirectionsApiService::class.java)

        // Start a coroutine to fetch the route data from the Directions API
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Fetch the directions
                val response = service.getDirections(origin, dest, apiKey)

                // Check if we got a valid response
                if (response.routes.isNotEmpty()) {
                    // Extract the polyline points from the response
                    val polylinePoints = response.routes[0].overview_polyline.points

                    // Decode the polyline into LatLng points
                    val decodedPath = decodePolyline(polylinePoints)

                    // Add the polyline to the map on the main thread
                    withContext(Dispatchers.Main) {
                        val polylineOptions = PolylineOptions().addAll(decodedPath)
                        polylineOptions.width(10f)  // Set line width
                        polylineOptions.color(0xFF0000FF.toInt())  // Set line color (blue)
                        mMap.addPolyline(polylineOptions)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        // Handle no routes found
                        println("No routes found")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Handle error
                    println("Error fetching directions: ${e.localizedMessage}")
                }
            }
        }

    }
    // Utility function to decode the polyline
    fun decodePolyline(encoded: String): List<LatLng> {
        val polyline = mutableListOf<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            polyline.add(LatLng(lat / 1E5, lng / 1E5))
        }

        return polyline
    }

    override fun onMarkerClick(p0: Marker): Boolean = false

}
