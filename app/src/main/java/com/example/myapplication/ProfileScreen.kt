package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.myapplication.ui.theme.BlueGrey
import com.example.myapplication.ui.theme.CougarHuntTheme
import com.example.myapplication.ui.theme.prettyCrimson

@Composable
fun ProfileScreen(navController: NavController, name: String, email: String, role: String) {
    var isEditMode by remember { mutableStateOf(false) }
    var editableName by remember { mutableStateOf(name) }
    var aboutMe by remember { mutableStateOf("Placeholder about me") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Image(
            painter = painterResource(id = R.drawable.blank_profile),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(prettyCrimson)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isEditMode) {
            OutlinedTextField(
                value = editableName,
                onValueChange = { editableName = it },
                label = { Text("Name") },
                singleLine = true
            )
        } else {
            Text(text = editableName, style = MaterialTheme.typography.headlineLarge, color = BlueGrey)
        }
        Text(text = email, style = MaterialTheme.typography.bodyLarge, color = BlueGrey)
        Spacer(modifier = Modifier.height(8.dp))
        Chip(text = role)

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "About Me", style = MaterialTheme.typography.titleMedium, color = BlueGrey)
                if (isEditMode) {
                    OutlinedTextField(
                        value = aboutMe,
                        onValueChange = { aboutMe = it },
                        label = { Text("About Me") }
                    )
                } else {
                    Text(
                        text = aboutMe,
                        style = MaterialTheme.typography.bodyMedium,
                        color = BlueGrey
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Achievements", style = MaterialTheme.typography.titleMedium, color = BlueGrey)
                Text(
                    text = "Placeholder achievements.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BlueGrey
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { isEditMode = !isEditMode },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(containerColor = prettyCrimson)
        ) {
            Text(text = if (isEditMode) "Save" else "Edit Profile")
        }
    }
}

@Composable
fun Chip(text: String) {
    Surface(
        modifier = Modifier.padding(4.dp),
        shape = MaterialTheme.shapes.small,
        color = BlueGrey,
        contentColor = Color.White
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    CougarHuntTheme {
        ProfileScreen(
            navController = NavController(context = LocalContext.current),
            name = "Jane Doe",
            email = "janedoe@g.cofc.edu",
            role = "Student"
        )
    }
}
