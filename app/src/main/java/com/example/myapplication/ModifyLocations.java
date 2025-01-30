package com.example.myapplication;

import static android.text.TextUtils.isEmpty;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cougarhunt.LocationLocal;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ModifyLocations extends AppCompatActivity {
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

        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call another function
                addLocation();
            }
        });

        getLocations();
    }
    private void addLocation(){
        //all elements
        setContentView(R.layout.activity_add_location);
        EditText etAddress = findViewById(R.id.et_address);
        EditText etDescription = findViewById(R.id.et_description);
        EditText etImageLink = findViewById(R.id.et_image_link);
        EditText etHint = findViewById(R.id.et_hint);
        EditText etLatitude = findViewById(R.id.et_latitude);
        EditText etLongitude = findViewById(R.id.et_longitude);
        EditText etCode = findViewById(R.id.et_code);
        Button btnSubmit = findViewById(R.id.btn_submit);


        Button backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(v -> {
            // Finish the current activity and return to the previous one
            modifyScreen();
        });

        btnSubmit.setOnClickListener(v -> {
            // Validate inputs
            if (isEmpty(etAddress.getText())) {
                showToast("Address is required");
                return;
            }
            if (isEmpty( etDescription.getText())) {
                showToast("Description is required");
                return;
            }
            if (isEmpty( etImageLink.getText())) {
                showToast("Image Link is required");
                return;
            }
            if (isEmpty( etLatitude.getText())) {
                showToast("Latitude is required");
                return;
            }
            if (isEmpty( etLongitude.getText())) {
                showToast("Longitude is required");
                return;
            }
            if (isEmpty( etCode.getText())) {
                showToast("Code is required");
                return;
            }
            // Get input values
            String address = etAddress.getText().toString();
            String description = etDescription.getText().toString();
            int imageLink = Integer.parseInt(etImageLink.getText().toString());
            String hint = etHint.getText().toString(); // Optional field
            double latitude = Double.parseDouble(etLatitude.getText().toString());
            double longitude = Double.parseDouble(etLongitude.getText().toString());
            String code = etCode.getText().toString();
            // Create LatLng object
            LatLng latLng = new LatLng(latitude, longitude);

            db = FirebaseIntegration.getInstance();
            String locationId = db.locations.push().getKey();
            LocationLocal location = new LocationLocal(
                    address,
                    description,
                    imageLink,
                    hint,
                    latLng,
                    code
            );
            if(locationId!=null){
                db.locations.child(locationId).setValue(location)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("Firebase", "User data added successfully");
                                } else {
                                    Log.e("Firebase", "Error adding user data", task.getException());
                                }
                            }
                        });
            }
            showToast("Submission successful!");

        });

    }
                // Helper method to show a toast message
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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

}
