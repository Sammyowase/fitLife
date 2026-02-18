package com.fitlife.app.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fitlife.app.R;
import com.fitlife.app.database.AppDatabase;
import com.fitlife.app.database.dao.EquipmentDao;
import com.fitlife.app.database.dao.WorkoutDao;
import com.fitlife.app.database.entities.User;
import com.fitlife.app.utils.PreferenceManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

/**
 * Profile Activity - Display user profile and statistics
 */
public class ProfileActivity extends AppCompatActivity {
    
    private TextView tvUserName;
    private TextView tvUserEmail;
    private TextView tvTotalWorkouts;
    private TextView tvCompletedWorkouts;
    private TextView tvEquipmentCount;
    private TextView tvMemberSince;
    private MaterialButton btnLogout;
    
    private AppDatabase database;
    private WorkoutDao workoutDao;
    private EquipmentDao equipmentDao;
    private PreferenceManager preferenceManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        initializeDatabase();
        initializeViews();
        setupToolbar();
        loadProfileData();
        setupListeners();
    }
    
    /**
     * Initialize database and DAOs
     */
    private void initializeDatabase() {
        database = AppDatabase.getInstance(this);
        workoutDao = database.workoutDao();
        equipmentDao = database.equipmentDao();
        preferenceManager = new PreferenceManager(this);
    }
    
    /**
     * Initialize UI components
     */
    private void initializeViews() {
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserEmail = findViewById(R.id.tv_user_email);
        tvMemberSince = findViewById(R.id.tv_member_since);
        btnLogout = findViewById(R.id.btn_logout);
        
        // Statistics TextViews (from included stat cards)
        // Note: These would need proper IDs in the layout
        // For now, we'll load them in loadProfileData
    }
    
    /**
     * Setup toolbar
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }
    
    /**
     * Load profile data
     */
    private void loadProfileData() {
        // Set user info from preferences
        tvUserName.setText(preferenceManager.getUserName());
        tvUserEmail.setText(preferenceManager.getUserEmail());
        
        int userId = preferenceManager.getUserId();
        
        // Load statistics
        Executors.newSingleThreadExecutor().execute(() -> {
            int totalWorkouts = workoutDao.getTotalWorkoutCount(userId);
            int completedWorkouts = workoutDao.getCompletedWorkoutCount(userId);
            int equipmentCount = equipmentDao.getTotalEquipmentCount(userId);
            
            // Get user creation date (would need to query User table)
            // For now, using a placeholder
            
            runOnUiThread(() -> {
                // Update statistics
                // TODO: Update stat card TextViews when properly referenced
                
                // Format member since date
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                tvMemberSince.setText(sdf.format(new Date()));
            });
        });
    }
    
    /**
     * Setup click listeners
     */
    private void setupListeners() {
        btnLogout.setOnClickListener(v -> showLogoutConfirmation());
    }
    
    /**
     * Show logout confirmation dialog
     */
    private void showLogoutConfirmation() {
        new MaterialAlertDialogBuilder(this)
            .setTitle(R.string.confirm_logout_title)
            .setMessage(R.string.confirm_logout_message)
            .setPositiveButton(R.string.yes, (dialog, which) -> {
                logout();
            })
            .setNegativeButton(R.string.no, null)
            .show();
    }
    
    /**
     * Logout user
     */
    private void logout() {
        preferenceManager.clearSession();
        
        // Clear activity stack and go to welcome
        android.content.Intent intent = new android.content.Intent(this, WelcomeActivity.class);
        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
