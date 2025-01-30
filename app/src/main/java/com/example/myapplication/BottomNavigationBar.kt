package com.example.myapplication

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf("Home", "Hunt Map", "Badges", "Profile")
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    BottomAppBar {
        items.forEach { screen ->
            val route = when (screen) {
                "Home" -> "Home"
                "Hunt Map" -> "Hunt Map"
                "Badges" -> "Badges"
                "Profile" -> "Profile/John Doe/johndoe@g.cofc.edu/Student"
                else -> "Home"
            }

            NavigationBarItem(
                selected = currentBackStackEntry?.destination?.route == route,
                onClick = { navController.navigate(route) },
                label = { Text(screen) },
                icon = {
                    Icon(
                        painter = painterResource(id = when(screen) {
                            "Home" -> R.drawable.ic_home
                            "Hunt Map" -> R.drawable.ic_map
                            "Badges" -> R.drawable.ic_badges
                            "Profile" -> R.drawable.ic_profile
                            else -> R.drawable.default_badge
                        }),
                        contentDescription = null
                    )
                },
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
