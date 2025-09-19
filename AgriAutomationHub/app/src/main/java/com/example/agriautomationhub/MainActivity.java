//package com.example.agriautomationhub;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.content.res.Configuration;
//import android.os.Bundle;
//import android.view.Menu;
//import android.view.View;
//import android.widget.GridLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.ActionBarDrawerToggle;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.core.app.ActivityCompat;
//import androidx.core.view.GravityCompat;
//import androidx.drawerlayout.widget.DrawerLayout;
//
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.android.material.navigation.NavigationView;
//import com.google.firebase.auth.FirebaseAuth;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//public class MainActivity extends AppCompatActivity {
//
//    private static final String TAG = "MainActivity";
//    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
//
//    private DrawerLayout drawerLayout;
//    private NavigationView navigationView;
//
//    private TextView weatherInfo;
//    private TextView weatherLocation;
//
//    private static final String API_KEY = "7e23b9a25a90846111d856e437e11535";
//    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
//
//    private FusedLocationProviderClient fusedLocationClient;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
//        String language = preferences.getString("My_Lang", "");
//        setLocale(language);
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // Firebase init
//        FirebaseAuth.getInstance();
//
//        // Drawer & Toolbar setup
//        drawerLayout = findViewById(R.id.drawer_layout);
//        navigationView = findViewById(R.id.navigation_view);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawerLayout, toolbar,
//                R.string.navigation_drawer_open,
//                R.string.navigation_drawer_close);
//        drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();
//
//        navigationView.setNavigationItemSelectedListener(item -> {
//            int id = item.getItemId();
//            if (id == R.id.drawer_close) {
//                drawerLayout.closeDrawer(GravityCompat.START);
//                return true;
//            } else if (id == R.id.nav_profile) {
//                startActivity(new Intent(this, ProfilePageActivity.class));
//            } else if (id == R.id.nav_language) {
//               showLanguageSelectionDialog();
//            } else if (id == R.id.nav_logout) {
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(this, LoginActivity.class));
//                finish();
//            }
//            drawerLayout.closeDrawer(GravityCompat.START);
//            return true;
//        });
//
//        weatherInfo = findViewById(R.id.weather_info);
//        weatherLocation = findViewById(R.id.weather_location);
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//
//        // Location Permission
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                        != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
//                            Manifest.permission.ACCESS_COARSE_LOCATION},
//                    LOCATION_PERMISSION_REQUEST_CODE);
//        } else {
//            getLastLocation();
//        }
//
//        // FAB Chatbot
//        FloatingActionButton fabChatBot = findViewById(R.id.fabChatBot);
//        fabChatBot.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, ChatActivity.class)));
//
//        initializeServices();
//        initializeBottomNavigation();
//    }
//
//    private void showLanguageSelectionDialog() {
//        String[] languages = {"English", "Hindi"};
//        new androidx.appcompat.app.AlertDialog.Builder(this)
//                .setTitle("Select Language")
//                .setItems(languages, (dialog, which) -> {
//                    switch (which) {
//                        case 0:
//                            LocaleHelper.setLocale(this, "en");
//                            break;
//                        case 1:
//                            LocaleHelper.setLocale(this, "hi");
//                            break;
//                    }
//                })
//                .show();
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }
//
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_main);
//        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
//    }
//
//    private void setLocale(String lang) {
//        Locale locale = new Locale(lang);
//        Locale.setDefault(locale);
//        Configuration config = new Configuration();
//        config.locale = locale;
//        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
//    }
//
//    private void initializeServices() {
//        GridLayout gridLayout = findViewById(R.id.grid_smart_support);
//        int childCount = gridLayout.getChildCount();
//
//        for (int i = 0; i < childCount; i++) {
//            View serviceView = gridLayout.getChildAt(i);
//            final int index = i;
//            serviceView.setOnClickListener(v -> {
//                switch (index) {
//                    case 0:
//                        startActivity(new Intent(MainActivity.this, Automatic_Irrigation.class));
//                        break;
//                    case 1:
//                        startActivity(new Intent(MainActivity.this, CropCareActivity.class));
//                        break;
//                    case 2:
//                        startActivity(new Intent(MainActivity.this, CropRecommenderActivity.class));
//                        break;
//                    case 3:
//                        startActivity(new Intent(MainActivity.this, SellingPriceCalculatorActivity.class));
//                        break;
//                    case 4:
//                        startActivity(new Intent(MainActivity.this, NewsActivity.class));
//                        break;
//                    case 5:
//                        startActivity(new Intent(MainActivity.this, StatewiseMandiActivity.class));
//                        break;
//                }
//            });
//        }
//    }
//
//    private void initializeBottomNavigation() {
//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_main);
//        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
//        bottomNavigationView.setOnItemSelectedListener(item -> {
//            int id = item.getItemId();
//            if (id == R.id.navigation_news) {
//                startActivity(new Intent(MainActivity.this, NewsActivity.class));
//                return false;
//            } else if (id == R.id.navigation_profile) {
//                startActivity(new Intent(MainActivity.this, ProfilePageActivity.class));
//                return false;
//            } else if (id == R.id.navigation_mandi) {
//                startActivity(new Intent(MainActivity.this, StatewiseMandiActivity.class));
//                return false;
//            }
//            return true;
//        });
//    }
//
//    @SuppressLint("SetTextI18n")
//    private void getLastLocation() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
//                            Manifest.permission.ACCESS_COARSE_LOCATION},
//                    LOCATION_PERMISSION_REQUEST_CODE);
//            return;
//        }
//        fusedLocationClient.getLastLocation()
//                .addOnSuccessListener(this, location -> {
//                    if (location != null) {
//                        getWeatherData(location.getLatitude(), location.getLongitude());
//                    } else {
//                        weatherInfo.setText("Unable to get location.");
//                    }
//                });
//    }
//
//    private void getWeatherData(double latitude, double longitude) {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        WeatherApiService apiService = retrofit.create(WeatherApiService.class);
//        Call<WeatherResponse> call = apiService.getCurrentWeather(latitude, longitude, API_KEY, "metric");
//
//        call.enqueue(new Callback<WeatherResponse>() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onResponse(@NonNull Call<WeatherResponse> call,
//                                   @NonNull Response<WeatherResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    WeatherResponse weatherResponse = response.body();
//                    double temp = weatherResponse.getMain().getTemp();
//                    int humidity = weatherResponse.getMain().getHumidity();
//                    String description = weatherResponse.getWeather()[0].getDescription();
//                    String location = weatherResponse.getName();
//                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
//                    String currentDate = dateFormat.format(new Date());
//
//                    weatherInfo.setText("Temperature: " + temp + "°C\nHumidity: " + humidity + "%\nCondition: " + description);
//                    weatherLocation.setText(location + " , " + currentDate);
//                } else {
//                    weatherInfo.setText("Failed to get weather data");
//                }
//            }
//
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
//                weatherInfo.setText("Failed to get weather data");
//            }
//        });
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getLastLocation();
//            } else {
//                weatherInfo.setText("Location permission denied");
//            }
//        }
//    }
//
////    @Override
////    public void onServiceClick(Service service) {
////        if (service != null) {
////            int serviceNameResId = service.getName();
////            if (serviceNameResId == R.string.auto_irrigation) {
////                startActivity(new Intent(this, Automatic_Irrigation.class));
////            } else if (serviceNameResId == R.string.crop_care) {
////                startActivity(new Intent(this, CropCareActivity.class));
////            } else if (serviceNameResId == R.string.crop_recommendation) {
////                startActivity(new Intent(this, CropRecommenderActivity.class));
////            } else if (serviceNameResId == R.string.selling_price_calculator) {
////                startActivity(new Intent(MainActivity.this, SellingPriceCalculatorActivity.class));
////            }
////        }
////    }
//}

package com.example.agriautomationhub;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.agriautomationhub.utils.PrefsManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private TextView weatherInfo;
    private TextView weatherLocation;

    private TextView navUserName, navUserPhone, navUserEmail;
    private CircleImageView navUserImage;

    private static final String API_KEY = "7e23b9a25a90846111d856e437e11535";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";

    private FusedLocationProviderClient fusedLocationClient;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private PrefsManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = preferences.getString("My_Lang", "");
        setLocale(language);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase init
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance("profile-data");
        prefs = new PrefsManager(this);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            // if user not logged in → go to login
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // Drawer & Toolbar setup
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Fetch navigation header views
        View headerView = navigationView.getHeaderView(0);
        navUserName = headerView.findViewById(R.id.nav_user_name);
        navUserPhone = headerView.findViewById(R.id.nav_user_phone);
        navUserEmail = headerView.findViewById(R.id.nav_user_email);
        navUserImage = headerView.findViewById(R.id.nav_user_image);

        if (currentUser != null) {
            String uid = currentUser.getUid();

            // 1️⃣ Fetch once after login (saves in SharedPreferences)
            fetchUserData(uid);

            // 2️⃣ Load instantly from SharedPreferences
            loadUserProfile(uid);

            // 3️⃣ Keep listening for updates (realtime sync)
            listenForUserUpdates(uid);
        }

        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                super.onDrawerClosed(drawerView);
                navigationView.getMenu().setGroupCheckable(0, true, false);
                for (int i = 0; i < navigationView.getMenu().size(); i++) {
                    navigationView.getMenu().getItem(i).setChecked(false);
                }
                navigationView.getMenu().setGroupCheckable(0, true, true);
            }
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfilePageActivity.class));
            } else if (id == R.id.nav_language) {
                showLanguageSelectionDialog();
            } else if (id == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                prefs.clearUser();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);

            // 🔑 clear highlight
            for (int i = 0; i < navigationView.getMenu().size(); i++) {
                navigationView.getMenu().getItem(i).setChecked(false);
            }

            return true;
        });

        weatherInfo = findViewById(R.id.weather_info);
        weatherLocation = findViewById(R.id.weather_location);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Location Permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLastLocation();
        }

        // FAB Chatbot
        FloatingActionButton fabChatBot = findViewById(R.id.fabChatBot);
        fabChatBot.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, ChatActivity.class)));

        initializeServices();
        initializeBottomNavigation();
    }

    private void fetchUserData(String uid) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String phone = documentSnapshot.getString("phone");
                        String email = documentSnapshot.getString("email");
                        String imageUrl = documentSnapshot.getString("photoUrl");

                        prefs.saveUser(name, phone, email, imageUrl);

                        // Update NavDrawer immediately
                        loadUserProfile(uid);
                    }
                });
    }

    private void loadUserProfile(String uid) {
        navUserName.setText(prefs.getName());
        navUserPhone.setText(prefs.getPhone());
        navUserEmail.setText(prefs.getEmail());

        Glide.with(this)
                .load(prefs.getImageUrl())
                .placeholder(R.drawable.ic_person_placeholder) // fallback image
                .into(navUserImage);
    }

    private void listenForUserUpdates(String uid) {
        db.collection("users").document(uid)
                .addSnapshotListener((snapshot, e) -> {
                    if (snapshot != null && snapshot.exists()) {
                        String name = snapshot.getString("name");
                        String phone = snapshot.getString("phone");
                        String email = snapshot.getString("email");
                        String imageUrl = snapshot.getString("photoUrl");

                        prefs.saveUser(name, phone, email, imageUrl);

                        // Update NavDrawer UI instantly
                        loadUserProfile(uid);
                    }
                });
    }

    private void showLanguageSelectionDialog() {
        String[] languages = {"English", "Hindi"};
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Select Language")
                .setItems(languages, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            LocaleHelper.setLocale(this, "en");
                            break;
                        case 1:
                            LocaleHelper.setLocale(this, "hi");
                            break;
                    }
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START); // close drawer first
        } else {
            finishAffinity(); // closes all activities
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_main);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private void initializeServices() {
        GridLayout gridLayout = findViewById(R.id.grid_smart_support);
        int childCount = gridLayout.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View serviceView = gridLayout.getChildAt(i);
            final int index = i;
            serviceView.setOnClickListener(v -> {
                switch (index) {
                    case 0:
                        startActivity(new Intent(MainActivity.this, Automatic_Irrigation.class));
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this, CropCareActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(MainActivity.this, CropRecommenderActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(MainActivity.this, SellingPriceCalculatorActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(MainActivity.this, NewsActivity.class));
                        break;
                    case 5:
                        startActivity(new Intent(MainActivity.this, StatewiseMandiActivity.class));
                        break;
                }
            });
        }
    }

    private void initializeBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_main);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_news) {
                startActivity(new Intent(MainActivity.this, NewsActivity.class));
                return false;
            } else if (id == R.id.navigation_profile) {
                startActivity(new Intent(MainActivity.this, ProfilePageActivity.class));
                return false;
            } else if (id == R.id.navigation_mandi) {
                startActivity(new Intent(MainActivity.this, StatewiseMandiActivity.class));
                return false;
            }
            return true;
        });
    }

    @SuppressLint("SetTextI18n")
    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        getWeatherData(location.getLatitude(), location.getLongitude());
                    } else {
                        weatherInfo.setText("Unable to get location.");
                    }
                });
    }

    private void getWeatherData(double latitude, double longitude) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApiService apiService = retrofit.create(WeatherApiService.class);
        Call<WeatherResponse> call = apiService.getCurrentWeather(latitude, longitude, API_KEY, "metric");

        call.enqueue(new Callback<WeatherResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call,
                                   @NonNull Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();
                    double temp = weatherResponse.getMain().getTemp();
                    int humidity = weatherResponse.getMain().getHumidity();
                    String description = weatherResponse.getWeather()[0].getDescription();
                    String location = weatherResponse.getName();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
                    String currentDate = dateFormat.format(new Date());

                    weatherInfo.setText("Temperature: " + temp + "°C\nHumidity: " + humidity + "%\nCondition: " + description);
                    weatherLocation.setText(location + " , " + currentDate);
                } else {
                    weatherInfo.setText("Failed to get weather data");
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                weatherInfo.setText("Failed to get weather data");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                weatherInfo.setText("Location permission denied");
            }
        }
    }
}
