package com.example.agriautomationhub;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Locale;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Soil_Fertility_check extends AppCompatActivity {

    EditText etNitrogen, etPhosphorus, etPotassium, etPh;
    TextView tvResult;
    Button btnCheck;
    ImageView back;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soil_fertility_check);

        back = findViewById(R.id.back_btn);

        back.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
        });

        etNitrogen = findViewById(R.id.etNitrogen);
        etPhosphorus = findViewById(R.id.etPhosphorus);
        etPotassium = findViewById(R.id.etPotassium);
        etPh = findViewById(R.id.etPh);
        tvResult = findViewById(R.id.tvResult);
        btnCheck = findViewById(R.id.btnCheck);

        btnCheck.setOnClickListener(v -> checkSoilFertility());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_home) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            } else if (id == R.id.navigation_news) {
                // Handle News navigation
                startActivity(new Intent(getApplicationContext(), NewsActivity.class));
                return true;
            } else if (id == R.id.navigation_mandi) {
                openWebsite();
                return true;
            }
            return false;
        });
    }

    private void checkSoilFertility() {
        double nitrogen = Double.parseDouble(etNitrogen.getText().toString());
        double phosphorus = Double.parseDouble(etPhosphorus.getText().toString());
        double potassium = Double.parseDouble(etPotassium.getText().toString());
        double ph = Double.parseDouble(etPh.getText().toString());

        // Define ranges for each nutrient and pH level
        double nitrogenMin = 20, nitrogenMax = 50;
        double phosphorusMin = 20, phosphorusMax = 50;
        double potassiumMin = 150, potassiumMax = 300;
        double phMin = 6, phMax = 8.5;

        // Calculate percentage of soil fertility for each nutrient and pH level
        double nitrogenPercentage = calculatePercentage(nitrogen, nitrogenMin, nitrogenMax);
        double phosphorusPercentage = calculatePercentage(phosphorus, phosphorusMin, phosphorusMax);
        double potassiumPercentage = calculatePercentage(potassium, potassiumMin, potassiumMax);
        double phPercentage = calculatePercentage(ph, phMin, phMax);

        // Calculate overall soil fertility percentage
        double overallPercentage = (nitrogenPercentage + phosphorusPercentage + potassiumPercentage + phPercentage) / 4.0;

        // Generate the result message
        Locale locale = Locale.US; // Specify the desired locale

        String resultMessage = "Soil Fertility Check Result:\n\n" +
                "Nitrogen: " + String.format(locale, "%.2f", nitrogenPercentage) + "%\n" +
                "Phosphorus: " + String.format(locale, "%.2f", phosphorusPercentage) + "%\n" +
                "Potassium: " + String.format(locale, "%.2f", potassiumPercentage) + "%\n" +
                "pH: " + String.format(locale, "%.2f", phPercentage) + "%\n" +
                "Overall Soil Fertility: " + String.format(locale, "%.2f", overallPercentage) + "%";

        // Display the result message
        tvResult.setText(resultMessage);

    }

    private double calculatePercentage(double value, double min, double max) {
        if (value < min) {
            return 0.0;
        } else if (value > max) {
            return 100.0;
        } else {
            return ((value - min) / (max - min)) * 100.0;
        }
    }

    public void openWebsite() {
        String url = "https://eanugya.mp.gov.in/Inward_Quote.aspx";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}