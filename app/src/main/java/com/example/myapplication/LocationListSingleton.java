package com.example.myapplication;

import com.example.cougarhunt.LocationLocal;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class LocationListSingleton {

    // Eagerly create the instance of the class
    private static final LocationListSingleton instance = new LocationListSingleton();
    List<LocationLocal> locations = new ArrayList<LocationLocal>();
    // Private constructor to prevent instantiation
    private LocationListSingleton() {
        //initialize default locations and later, pull locations from backend
        locations = new ArrayList<LocationLocal>();
        locations.add(new LocationLocal(
                "165 Calhoun St, Charleston, SC 29401",
                "Clyde Statue",
                R.drawable.clyde,
                null,
                new LatLng(32.78491587240443, -79.93796197951562),
                "1234"
        ));
        locations.add(new LocationLocal(
                "66 George St, Charleston, SC 29424",
                "The Cistern",
                R.drawable.cougarlogo,
                null,
                new LatLng(32.7838038347409, -79.9373079499025),
                "1234"
        ));
    }
    public List<LocationLocal> getLocations(){
        return locations;
    }

    // Public method to access the single instance
    public static LocationListSingleton getInstance() {
        return instance;
    }

    // Other methods of the class
    public void showMessage() {
        System.out.println("Hello from Singleton!");
    }

}
