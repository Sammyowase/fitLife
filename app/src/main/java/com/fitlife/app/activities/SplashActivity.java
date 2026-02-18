package com.fitlife.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.fitlife.app.R;
import com.fitlife.app.utils.Constants;
import com.fitlife.app.utils.PreferenceManager;

/**
 * Splash Activity - Entry point of the application
 * Displays app logo and checks if user is logged in
 */
public class SplashActivity extends AppCompatActivity {
    
    private PreferenceManager preferenceManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        preferenceManager = new PreferenceManager(this);
        
        // Delay for splash screen, then navigate
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            navigateToNextScreen();
        }, Constants.SPLASH_DISPLAY_LENGTH);
    }
    
    /**
     * Navigate to appropriate screen based on login status
     */
    private void navigateToNextScreen() {
        Intent intent;
        
        if (preferenceManager.isLoggedIn()) {
            // User is logged in, go to Home
            intent = new Intent(this, HomeActivity.class);
        } else {
            // User not logged in, go to Welcome
            intent = new Intent(this, WelcomeActivity.class);
        }
        
        startActivity(intent);
        finish(); // Prevent going back to splash
    }
}
