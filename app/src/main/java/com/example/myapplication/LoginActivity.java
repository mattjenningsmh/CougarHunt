package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private FirebaseIntegration db = FirebaseIntegration.getInstance();
    private boolean isAdmin = false;
    private boolean isUser = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        EditText usernameEditText = findViewById(R.id.username);
        EditText passwordEditText = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login_button);
        Button registerButton = findViewById(R.id.register_button);

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            checkLoginCredentials(username, password);
        });

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }
    private void checkLoginCredentials(String email, String password) {
        // Attach a listener to read the data from both "users" and "admins"
        db.users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                boolean loginSuccessful = false;
                // Iterate through all the user nodes
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userEmail = userSnapshot.child("email").getValue(String.class);
                    String userPassword = userSnapshot.child("password").getValue(String.class);

                    // Check if the email matches and if the password is correct for user
                    if (userEmail != null && userEmail.equals(email) && userPassword != null && userPassword.equals(password)) {
                        loginSuccessful = true;
                        isUser = true;
                        break;  // Exit the loop if credentials match
                    }
                }

                // If login for user failed, check the admins
                if (!loginSuccessful) {
                    db.admins.addListenerForSingleValueEvent(new ValueEventListener() {
                        boolean loginSuccessful = false;
                        @Override
                        public void onDataChange(@NonNull DataSnapshot adminSnapshot) {
                            // Check the admin credentials
                            for (DataSnapshot adminData : adminSnapshot.getChildren()) {
                                String adminEmail = adminData.child("email").getValue(String.class);
                                String adminPassword = adminData.child("password").getValue(String.class);

                                if (adminEmail != null && adminEmail.equals(email) && adminPassword != null && adminPassword.equals(password)) {
                                    loginSuccessful = true;
                                    isAdmin = true;
                                    break;  // Exit the loop if credentials match
                                }
                            }

                            // Show result based on login verification
                            if (loginSuccessful) {
                                // Login success
                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                // You can navigate to another activity here
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                if (isUser) {
                                    intent.putExtra("isUser", isUser);
                                } else if (isAdmin) {
                                    intent.putExtra("isAdmin", isAdmin);
                                }
                                startActivity(intent);
                                finish();
                            } else {
                                // Login failed
                                Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle errors if the data retrieval is canceled or fails
                            Toast.makeText(LoginActivity.this, "Error checking credentials", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Login successful for the user, proceed with the result
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("isAdmin", isAdmin);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors if the data retrieval is canceled or fails
                Toast.makeText(LoginActivity.this, "Error checking credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

