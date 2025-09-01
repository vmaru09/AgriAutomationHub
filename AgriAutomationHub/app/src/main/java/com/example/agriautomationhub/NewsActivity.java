package com.example.agriautomationhub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        // Initialize the RecyclerView and set its layout manager
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        back = findViewById(R.id.back_btn_news);

        back.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Fetch news data from the API
        fetchNews();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_news);
        bottomNavigationView.setSelectedItemId(R.id.navigation_news);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                return false;
            } else if (id == R.id.navigation_profile) {
                // Handle News navigation
                startActivity(new Intent(getApplicationContext(), ProfilePageActivity.class));
                return false;
            } else if (id == R.id.navigation_mandi) {
                startActivity(new Intent(getApplicationContext(), StatewiseMandiActivity.class));
                return false;
            }
            return false;
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_news);
        bottomNavigationView.setSelectedItemId(R.id.navigation_news);
    }

    // Method to fetch news from the API using Retrofit
    private void fetchNews() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.webz.io/") // Base URL for the API
                .addConverterFactory(GsonConverterFactory.create()) // Add Gson for JSON conversion
                .build();

        NewsApiService apiService = retrofit.create(NewsApiService.class);

        // Make the API call
        Call<NewsResponse> call = apiService.getNews(
                "d661a399-de2f-4a4b-b7b3-8b1a24ce0183", // Your API key
                "Agriculture", // Query for "Agriculture" news
                "in", // Country code for India
                "hindi", // Language code for Hindi
                "news" // News type
        );

        // Asynchronous request to handle the response
        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Post> posts = response.body().getPosts();
                    if (posts != null && !posts.isEmpty()) {
                        // Convert each post to NewsThread and add it to the list
                        List<NewsThread> newsArticles = new ArrayList<>();
                        for (Post post : posts) {
                            newsArticles.add(post.getThread());
                        }

                        // Pass the list to the adapter and set the adapter to the RecyclerView
                        newsAdapter= new NewsAdapter(newsArticles);
                        recyclerView.setAdapter(newsAdapter);
                    } else {
                        Toast.makeText(NewsActivity.this, "No news articles found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle unsuccessful responses
                    Toast.makeText(NewsActivity.this, "No news found in the response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                // Log the error for debugging
                Log.e("NewsActivity", "API call failed", t);
                Toast.makeText(NewsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                // Ensure the adapter is set with an empty list to avoid null references
                newsAdapter = new NewsAdapter(new ArrayList<>());
                recyclerView.setAdapter(newsAdapter);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            return logoutUser();
        }
        if (id == R.id.action_profile) {
            return settings();
        }
        if (id == R.id.action_help) {
            Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        return true;
    }

    private boolean settings() {
        Intent intent = new Intent(getApplicationContext(), ProfilePageActivity.class);
        startActivity(intent);
        return true;
    }
}

