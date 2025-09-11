package com.example.agriautomationhub;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText usernameInput, passwordInput;
    Button loginButton;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView registerLink, forgotPasswordText;

    FirebaseFirestore db;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User already logged in, go to MainActivity
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_btn);
        progressBar = findViewById(R.id.progressBar);
        registerLink = findViewById(R.id.register_text);
        forgotPasswordText = findViewById(R.id.forgot_password_text);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance("profile-data");

        // Go to Register screen
        registerLink.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Register.class);
            startActivity(intent);
            finish();
        });

        // Forgot password flow
        forgotPasswordText.setOnClickListener(view -> {
            Editable editable = usernameInput.getText();
            if (editable == null || TextUtils.isEmpty(editable.toString())) {
                Toast.makeText(LoginActivity.this, "Please enter your email to reset password", Toast.LENGTH_SHORT).show();
                return;
            }

            String email = editable.toString();
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Login button
        loginButton.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            String email = String.valueOf(usernameInput.getText());
            String password = String.valueOf(passwordInput.getText());

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Fetch user profile from Firestore
                                DocumentReference docRef = db.collection("profile-data").document(user.getUid());
                                docRef.get().addOnSuccessListener(document -> {
                                    if (document.exists()) {
                                        String name = document.getString("name");
                                        Toast.makeText(LoginActivity.this, "Welcome back " + name, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Login Successful (No profile found)", Toast.LENGTH_SHORT).show();
                                    }

                                    // Go to MainActivity
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(LoginActivity.this, "Error loading profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                });
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
