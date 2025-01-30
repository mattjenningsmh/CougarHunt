package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cougarhunt.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;

public class RegisterActivity extends AppCompatActivity {


    FirebaseIntegration db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UI elements
        EditText usernameEditText = findViewById(R.id.username);
        EditText emailEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.password);
        Button registerButton = findViewById(R.id.register_button);
        TextView loginLink = findViewById(R.id.login_link);

        // Set up the Register button click listener
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    // Show success message
                    Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();

                    //add the user to the database here:
                    db = FirebaseIntegration.getInstance();

                    //check if ends in cofc.edu or something else
                    boolean adminAccount=false;
                    int symbolInd = email.indexOf('@');
                    String emailCheck = email.substring(symbolInd+1);

                    if(emailCheck.equals("cofc.edu")){
                        addAdmin(username,email,password);
                    }
                    else{
                        addUser(username, email, password);
                    }

                    // Navigate to MainActivity
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Show error message
                    Toast.makeText(RegisterActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set up the Login link click listener
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to LoginActivity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void addUser(String username, String email, String password){
        // Create a user object (you can also use a custom model class)
        String userId = db.users.push().getKey(); //unique id for the user in database
        User user = new User(
                username,
                email,
                password
        );
        if(userId != null){
            db.users.child(userId).setValue(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Log.d("Firebase", "user data added successfully");
                            }
                            else{
                                Log.e("Firebase", "error adding user data");
                            }
                        }
                    });
        }
    }

    private void addAdmin(String username, String email, String password){
        // Create a user object (you can also use a custom model class)
        String adminId = db.admins.push().getKey(); //unique id for the user in database
        Admin admin = new Admin(
                username,
                email,
                password
        );
        if(adminId != null){
            db.admins.child(adminId).setValue(admin)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Log.d("Firebase", "admin data added successfully");
                            }
                            else{
                                Log.e("Firebase", "error adding admin data");
                            }
                        }
                    });
        }
    }
}
