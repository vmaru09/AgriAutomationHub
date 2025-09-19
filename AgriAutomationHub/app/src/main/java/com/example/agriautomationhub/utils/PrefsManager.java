package com.example.agriautomationhub.utils; // or your package

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager {

    private static final String PREF_NAME = "UserData";
    private final SharedPreferences prefs;

    public PrefsManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveUser(String name, String phone, String email, String imageUrl) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("name", name != null ? name : "User");
        editor.putString("phone", phone != null ? phone : "");
        editor.putString("email", email != null ? email : "");
        editor.putString("imageUrl", imageUrl != null ? imageUrl : "");
        editor.apply();
    }

    public String getName() {
        return prefs.getString("name", "User");
    }

    public String getPhone() {
        return prefs.getString("phone", "");
    }

    public String getEmail() {
        return prefs.getString("email", "");
    }

    public String getImageUrl() {
        return prefs.getString("imageUrl", "");
    }

    public void clearUser() {
        prefs.edit().clear().apply();
    }
}
