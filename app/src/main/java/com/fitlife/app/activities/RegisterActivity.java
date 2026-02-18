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

/**
 * Register Activity - User registration screen
 */
public class RegisterActivity extends AppCompatActivity {
    
    private TextInputLayout tilFullName;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputLayout tilConfirmPassword;
    private TextInputEditText etFullName;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;
    
    private AppDatabase database;
    private UserDao userDao;
    private PreferenceManager preferenceManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        initializeDatabase();
        initializeViews();
        setupListeners();
    }
    
    /**
     * Initialize database and DAOs
     */
    private void initializeDatabase() {
        database = AppDatabase.getInstance(this);
        userDao = database.userDao();
        preferenceManager = new PreferenceManager(this);
    }
    
    /**
     * Initialize UI components
     */
    private void initializeViews() {
        tilFullName = findViewById(R.id.til_full_name);
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);
    }
    
    /**
     * Set up click listeners
     */
    private void setupListeners() {
        btnRegister.setOnClickListener(v -> attemptRegistration());
        
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
    
    /**
     * Validate input and attempt registration
     */
    private void attemptRegistration() {
        // Clear previous errors
        tilFullName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
        
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        
        // Validate inputs
        if (!ValidationHelper.isValidName(fullName)) {
            tilFullName.setError(getString(R.string.error_empty_field));
            etFullName.requestFocus();
            return;
        }
        
        if (!ValidationHelper.isValidEmail(email)) {
            tilEmail.setError(getString(R.string.error_invalid_email));
            etEmail.requestFocus();
            return;
        }
        
        if (!ValidationHelper.isValidPassword(password)) {
            tilPassword.setError(getString(R.string.error_password_length));
            etPassword.requestFocus();
            return;
        }
        
        if (!ValidationHelper.passwordsMatch(password, confirmPassword)) {
            tilConfirmPassword.setError(getString(R.string.error_password_mismatch));
            etConfirmPassword.requestFocus();
            return;
        }
        
        // Disable button to prevent multiple clicks
        btnRegister.setEnabled(false);
        
        // Perform registration in background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            // Check if email already exists
            int emailCount = userDao.emailExists(email);
            
            if (emailCount > 0) {
                runOnUiThread(() -> {
                    btnRegister.setEnabled(true);
                    tilEmail.setError(getString(R.string.error_email_exists));
                });
                return;
            }
            
            // Hash password and create user
            String passwordHash = SecurityHelper.hashPassword(password);
            User newUser = new User(fullName, email, passwordHash);
            
            // Insert user and get ID
            long userId = userDao.insert(newUser);
            newUser.setUserId((int) userId);
            
            runOnUiThread(() -> {
                btnRegister.setEnabled(true);
                
                // Save session
                preferenceManager.saveUserSession(
                    newUser.getUserId(),
                    newUser.getFullName(),
                    newUser.getEmail()
                );
                
                // Navigate to home
                Toast.makeText(this, getString(R.string.success_registration), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            });
        });
    }
}
