package com.fitlife.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Preference Manager for handling SharedPreferences operations
 */
public class PreferenceManager {
    
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    
    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    
    /**
     * Save user login session
     */
    public void saveUserSession(int userId, String userName, String userEmail) {
        editor.putInt(Constants.KEY_USER_ID, userId);
        editor.putString(Constants.KEY_USER_NAME, userName);
        editor.putString(Constants.KEY_USER_EMAIL, userEmail);
        editor.putBoolean(Constants.KEY_IS_LOGGED_IN, true);
        editor.apply();
    }
    
    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(Constants.KEY_IS_LOGGED_IN, false);
    }
    
    /**
     * Get logged-in user ID
     */
    public int getUserId() {
        return sharedPreferences.getInt(Constants.KEY_USER_ID, -1);
    }
    
    /**
     * Get logged-in user name
     */
    public String getUserName() {
        return sharedPreferences.getString(Constants.KEY_USER_NAME, "");
    }
    
    /**
     * Get logged-in user email
     */
    public String getUserEmail() {
        return sharedPreferences.getString(Constants.KEY_USER_EMAIL, "");
    }
    
    /**
     * Clear user session (logout)
     */
    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}
