package com.example.agriautomationhub;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PrivacyTermsActivity extends AppCompatActivity {

    TextView titleText, contentText;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_terms_activity);

        titleText = findViewById(R.id.titleText);
        contentText = findViewById(R.id.contentText);

        // Retrieve which document to load
        String type = getIntent().getStringExtra("type");

        if (type == null) type = "privacy"; // default

        switch (type) {
            case "terms":
                titleText.setText(getString(R.string.terms_title));
                contentText.setText(Html.fromHtml(getString(R.string.terms_content), Html.FROM_HTML_MODE_LEGACY));
                break;

            case "support":
                titleText.setText(getString(R.string.support_title));
                contentText.setText(Html.fromHtml(getString(R.string.support_content), Html.FROM_HTML_MODE_LEGACY));
                break;

            case "privacy":
            default:
                titleText.setText(getString(R.string.privacy_title));
                contentText.setText(Html.fromHtml(getString(R.string.privacy_content), Html.FROM_HTML_MODE_LEGACY));
                break;


        }

        back = findViewById(R.id.back_btn);

        back.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_privacy_terms);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if(id == R.id.navigation_home)
            {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }else if (id == R.id.navigation_news) {
                // Handle News navigation
                startActivity(new Intent(getApplicationContext(), FieldMeasureActivity.class));
                return true;
            } else if (id == R.id.navigation_profile) {
                startActivity(new Intent(getApplicationContext(), ProfilePageActivity.class));
                return true;
            } else if (id == R.id.navigation_mandi) {
                startActivity(new Intent(getApplicationContext(), StatewiseMandiActivity.class));
                return true;
            }
            return false;
        });
    }
}

