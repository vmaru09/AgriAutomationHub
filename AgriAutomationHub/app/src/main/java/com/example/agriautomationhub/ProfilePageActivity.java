package com.example.agriautomationhub;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ProfilePageActivity extends AppCompatActivity {
    private ImageView profileImage;
    private EditText usernameText, emailText;
    private Uri selectedImageUri = null;

    private final ActivityResultLauncher<CropImageContractOptions> cropImage =
            registerForActivityResult(new CropImageContract(), result -> {
                if (result.isSuccessful() && result.getUriContent() != null) {
                    selectedImageUri = result.getUriContent();
                    Glide.with(this).load(selectedImageUri).circleCrop().into(profileImage);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleHelper.setLocale(this);
        setContentView(R.layout.activity_profile_page);

        profileImage = findViewById(R.id.profile_image);
        usernameText = findViewById(R.id.editTextName);
        emailText = findViewById(R.id.editTextEmail);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            usernameText.setText(user.getDisplayName() != null ? user.getDisplayName() : "Username");
            emailText.setText(user.getEmail());
            if (user.getPhotoUrl() != null) {
                Glide.with(this).load(user.getPhotoUrl()).circleCrop().into(profileImage);
            } else {
                profileImage.setImageResource(R.drawable.ic_profile);
            }
        }

        Button saveButton = findViewById(R.id.btnUpdate);
        saveButton.setOnClickListener(v -> updateProfile());

        profileImage.setOnClickListener(v -> launchImageCropper());

        Button changePassword = findViewById(R.id.btnChangePassword);
        changePassword.setOnClickListener(view -> {
            String email = emailText.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(ProfilePageActivity.this, "Please enter your email to reset password", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfilePageActivity.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProfilePageActivity.this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                return true;
            } else if (id == R.id.navigation_marketView) {
                // Handle News navigation
                startActivity(new Intent(getApplicationContext(), MarketViewActivity.class));
                return false;
            }else if (id == R.id.navigation_news) {
                // Handle News navigation
                startActivity(new Intent(getApplicationContext(), NewsActivity.class));
                return false;
            } else if (id == R.id.navigation_mandi) {
                startActivity(new Intent(getApplicationContext(), MandiActivity.class));
                return true;
            }
            return false;
        });
    }

    private void launchImageCropper() {
        CropImageOptions cropImageOptions = new CropImageOptions();
        cropImageOptions.guidelines = CropImageView.Guidelines.ON;
        cropImageOptions.cropShape = CropImageView.CropShape.OVAL;
        cropImageOptions.aspectRatioX = 1;
        cropImageOptions.aspectRatioY = 1;

        cropImage.launch(new CropImageContractOptions(null, cropImageOptions));
    }

    private void updateProfile() {
        String newName = usernameText.getText().toString().trim();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName);

        if (selectedImageUri != null) {
            builder.setPhotoUri(selectedImageUri);
        }

        user.updateProfile(builder.build()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
