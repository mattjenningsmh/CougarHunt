package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cougarhunt.LocationLocal
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ListViewer : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.location_list)

        val listView: ListView = findViewById(R.id.simpleListView)

        // Fetch locations from Firebase and populate the ListView
        fetchLocationsFromFirebase { locations ->
            val descriptions = locations.map { it.description ?: "No description available" }

            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                descriptions
            )

            listView.adapter = adapter

            // Add click listener to ListView
            listView.setOnItemClickListener { _, _, position, _ ->
                // Get the clicked location
                val clickedLocation = locations[position]
                locationPage(clickedLocation, locations, position)
            }
        }
    }



    // Function to fetch locations from Firebase
    private fun fetchLocationsFromFirebase(callback: (List<LocationLocal>) -> Unit) {
        val db = FirebaseIntegration.getInstance()
        val locations = mutableListOf<LocationLocal>()

        db.locations.addListenerForSingleValueEvent(object : ValueEventListener {
             override fun onDataChange(snapshot: DataSnapshot) {
                for (locationSnapshot in snapshot.children) {
                    val address = locationSnapshot.child("address").getValue(String::class.java)
                    val description = locationSnapshot.child("description").getValue(String::class.java)
                    val code = locationSnapshot.child("code").getValue(String::class.java)
                    val hint = locationSnapshot.child("hint").getValue(String::class.java)
                    val imageLinkLong = locationSnapshot.child("imageLink").getValue(Long::class.java)
                    val latitude = locationSnapshot.child("latLng").child("latitude").getValue(Double::class.java)
                    val longitude = locationSnapshot.child("latLng").child("longitude").getValue(Double::class.java)

                    if (address != null && code != null && latitude != null && longitude != null) {
                        val imageLink = imageLinkLong?.toInt() ?: 0
                        val latLng = LatLng(latitude, longitude)
                        val location = LocationLocal(address, description, imageLink, hint, latLng, code)
                        locations.add(location)
                    }
                }
                // Trigger the callback with the loaded locations
                callback(locations)
            }

             override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching locations: ${error.message}")
            }
        })
    }

    private fun  locationPage(locationLocal: LocationLocal, locations:List<LocationLocal>, position:Int){
        setContentView(R.layout.location_info)
        val imageView: ImageView = findViewById(R.id.locationImage)
        val desc: TextView = findViewById(R.id.locationDescription)
        val address: TextView = findViewById(R.id.locationAddress)
        val hint: TextView = findViewById(R.id.locationHints)
        val back: Button = findViewById(R.id.button)
        val input: EditText = findViewById(R.id.locationInput)
        back.text = "back"
        back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }


        imageView.setImageResource(R.drawable.clyde)
        desc.text = locationLocal.description
        address.text = locationLocal.address
        hint.text = locationLocal.hint

        val mapButton: Button = findViewById(R.id.showMap)
        mapButton.setOnClickListener{
            Toast.makeText(this, "Button clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MapsActivity2::class.java)
            intent.putExtra("location",locationLocal)
            startActivity(intent)
        }
    }

}