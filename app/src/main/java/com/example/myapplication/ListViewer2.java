package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cougarhunt.LocationLocal;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListViewer2 extends AppCompatActivity {
    private List<LocationLocal> locations;
    private FirebaseIntegration db;

    private ListView locationListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        modifyScreen();
    }
    private void modifyScreen(){
        setContentView(R.layout.activity_location_dashboard);
        locationListView = findViewById(R.id.simpleListView);
        // Find the Toolbar and set it as the app's action bar

        Button addLocation = findViewById(R.id.add);
        Button removeLocation = findViewById(R.id.remove);
        // Remove the button
        if (addLocation != null) {
            // Get the parent layout of the button
            ViewGroup parentLayout = (ViewGroup) addLocation.getParent();
            if (parentLayout != null) {
                // Remove the button from the parent layout
                parentLayout.removeView(addLocation);
            }
        }
        if (removeLocation != null) {
            // Get the parent layout of the button
            ViewGroup parentLayout = (ViewGroup) removeLocation.getParent();
            if (parentLayout != null) {
                // Remove the button from the parent layout
                parentLayout.removeView(removeLocation);
            }
        }
        getLocations();
    }
    private void getLocations(){
        locations = new ArrayList<LocationLocal>();
        /*locations.add(new LocationLocal(
                "165 Calhoun St, Charleston, SC 29401",
                "Clyde Statue",
                2131165350,
                "3rd word on cougar statue",
                new LatLng(32.78491587240443,-79.93796197951562),
                "1234"

        ));*/
        db = FirebaseIntegration.getInstance();
        db.locations.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Iterate through all location nodes in Firebase
                for (DataSnapshot locationSnapshot : snapshot.getChildren()) {
                    // Retrieve values from each child of the current locationSnapshot
                    String address = locationSnapshot.child("address").getValue(String.class);
                    String description = locationSnapshot.child("description").getValue(String.class);
                    String code = locationSnapshot.child("code").getValue(String.class);
                    String hint = locationSnapshot.child("hint").getValue(String.class);
                    Long imageLinkLong = locationSnapshot.child("imageLink").getValue(Long.class); // Firebase stores numbers as Long by default
                    double latitude = locationSnapshot.child("latLng").child("latitude").getValue(Double.class);
                    double longitude = locationSnapshot.child("latLng").child("longitude").getValue(Double.class);

                    // Convert Long to int for imageLink
                    int imageLink = (imageLinkLong != null) ? imageLinkLong.intValue() : 0;

                    // Create a LatLng object using latitude and longitude
                    LatLng latLng = new LatLng(latitude, longitude);

                    // Create a new LocationLocal object and add it to the list
                    assert address != null;
                    assert code != null;
                    LocationLocal location = new LocationLocal(address, description, imageLink, hint, latLng, code);
                    locations.add(location);
                }
                // Once all data has been added to locations, you can update the UI, etc.
                generateListView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase","something messed up");
            }
        });
    }
    private void generateListView(){
        List<String> descriptions = new ArrayList<>();
        for (LocationLocal location : locations) {
            descriptions.add(location.getDescription() != null ? location.getDescription() : "No description available");
        }

        // Create an ArrayAdapter with the descriptions list
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, descriptions);

        // Set the adapter to the ListView
        locationListView.setAdapter(adapter);
    }
}
