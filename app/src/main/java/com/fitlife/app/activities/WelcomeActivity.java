package com.fitlife.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.fitlife.app.R;


public class WelcomeActivity extends AppCompatActivity {
    
    private Button btnLogin;
    private Button btnSignUp;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        
        initializeViews();
        setupListeners();
    }
    
   
    private void initializeViews() {
        btnLogin = findViewById(R.id.btn_login);
        btnSignUp = findViewById(R.id.btn_sign_up);
    }
    
    
    private void setupListeners() {
        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });
        
        btnSignUp.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
    
    @Override
    public void onBackPressed() {
        
        super.onBackPressed();
        finishAffinity();
    }
}
