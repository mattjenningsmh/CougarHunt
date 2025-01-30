package com.example.myapplication

/*import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Response*/
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.CougarHuntTheme


// ...


class MainActivity : ComponentActivity() {
    //private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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

            else -> {
                // No location access granted.
            }
        }
    }
     fun requestLocationPermission() {
        locationPermissionRequest.launch(
            arrayOf(
                ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION
            )
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        requestLocationPermission()
        super.onCreate(savedInstanceState)

        setContent {
            CougarHuntTheme {
                val navController = rememberNavController()
                Surface(modifier = Modifier.fillMaxSize()) {
                    NavHost(navController = navController, startDestination = "homeScreen") {
                        composable("homeScreen") {
                            HomeScreen(navController)
                        }
                        composable("huntScreen") {
                            HuntScreen(navController)
                        }
                        composable("map") {

                        }
                    }
                }
            }
        }
    }

    @Composable
    fun HomeScreen(navController: NavController) {
        val intent = intent
        val isAdmin = intent.getBooleanExtra("isAdmin", false)
        if(isAdmin){
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Top
            ) {
                Button(
                    onClick = {callAddLocation()},
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = "Add Locations")
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.cougarlogo),
                contentDescription = "CougarHunt logo",
                modifier = Modifier.size(200.dp)
            )
            Text(
                text = "CougarHunt",
                style = MaterialTheme.typography.titleMedium
            )
            Button(
                onClick = { navController.navigate("huntScreen") },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Start Hunt")
            }
        }

    }


    @Composable
    fun HuntScreen(navController: NavController) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "hunt Screen", style = MaterialTheme.typography.titleMedium)
            Button(onClick = { callMapsActivity() }) {
                Text(text = "view objectives")
            }
        }
    }
    fun callMapsActivity(){
        val intent = Intent(this, ListViewer::class.java)
        startActivity(intent)
    }
    fun callAddLocation(){
        val intent = Intent(this, ModifyLocations::class.java)
        startActivity(intent)
    }

    @Preview
    @Composable
    fun HomeScreenPreview() {
        CougarHuntTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(android.graphics.Color.parseColor("#a1a3b4"))
            ) {
                HomeScreen(navController = NavController(context = LocalContext.current))
            }
        }
    }


}
