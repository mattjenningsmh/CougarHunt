package com.example.myapplication;/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.example.cougarhunt.LocationLocal;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseIntegration extends AppCompatActivity {

    public DatabaseReference locations;
    public DatabaseReference admins;

    public DatabaseReference users;
    private static final FirebaseIntegration instance = new FirebaseIntegration();

    private FirebaseIntegration(){
        FirebaseDatabase test = FirebaseDatabase.getInstance();
        locations = test.getReference("locations"); // Reference to a "users" node
        admins = test.getReference("admins");
        users = test.getReference("users");
    }

    public static FirebaseIntegration getInstance() {
        return instance;
    }

    public void writeData() {
        // Create a user object (you can also use a custom model class)
        String locationId = locations.push().getKey();  // Generate a unique ID for the user
        LocationLocal location = new LocationLocal(
                "165 Calhoun St, Charleston, SC 29401",
                "Clyde Statue",
                R.drawable.clyde,
                null,
                new LatLng(32.78491587240443, -79.93796197951562),
                "1234"
        );
        if(locationId!= null){
            locations.child(locationId).setValue(location)
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
    }
    public void readData() {
        locations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Iterate over all the children of "users"
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String username = snapshot.child("username").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    Log.d("FirebaseData", "Username: " + username + ", Email: " + email);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseData", "Error reading data", databaseError.toException());
            }
        });
    }

}