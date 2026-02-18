package com.fitlife.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fitlife.app.R;
import com.fitlife.app.database.AppDatabase;
import com.fitlife.app.database.dao.UserDao;
import com.fitlife.app.database.entities.User;
import com.fitlife.app.utils.PreferenceManager;
import com.fitlife.app.utils.SecurityHelper;
import com.fitlife.app.utils.ValidationHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.Executors;


public class LoginActivity extends AppCompatActivity {
    
    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private Button btnLogin;
    private TextView tvSignUp;
    
    private AppDatabase database;
    private UserDao userDao;
    private PreferenceManager preferenceManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        initializeDatabase();
        initializeViews();
        setupListeners();
    }
    
    
    private void initializeDatabase() {
        database = AppDatabase.getInstance(this);
        userDao = database.userDao();
        preferenceManager = new PreferenceManager(this);
    }
    
    
    private void initializeViews() {
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvSignUp = findViewById(R.id.tv_sign_up);
    }
    
    
    private void setupListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());
        
        tvSignUp.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });
    }
    
    
    private void attemptLogin() {
     
        tilEmail.setError(null);
        tilPassword.setError(null);
        
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
       
        if (!ValidationHelper.isValidEmail(email)) {
            tilEmail.setError(getString(R.string.error_invalid_email));
            etEmail.requestFocus();
            return;
        }
        
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError(getString(R.string.error_empty_field));
            etPassword.requestFocus();
            return;
        }
        
        btnLogin.setEnabled(false);
        
        Executors.newSingleThreadExecutor().execute(() -> {
            User user = userDao.getUserByEmail(email);
            
            runOnUiThread(() -> {
                btnLogin.setEnabled(true);
                
                if (user == null) {
                    tilEmail.setError(getString(R.string.error_invalid_credentials));
                    return;
                }
                
                if (SecurityHelper.verifyPassword(password, user.getPasswordHash())) {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        userDao.updateLastLogin(user.getUserId(), System.currentTimeMillis());
                    });
                    
                    preferenceManager.saveUserSession(
                        user.getUserId(),
                        user.getFullName(),
                        user.getEmail()
                    );
                    
                    Toast.makeText(this, getString(R.string.success_login), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, HomeActivity.class));
                    finish();
                } else {
                    tilPassword.setError(getString(R.string.error_invalid_credentials));
                }
            });
        });
    }
}
