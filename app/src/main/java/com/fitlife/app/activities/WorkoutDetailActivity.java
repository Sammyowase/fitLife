package com.fitlife.app.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fitlife.app.R;
import com.fitlife.app.adapters.ExerciseAdapter;
import com.fitlife.app.database.AppDatabase;
import com.fitlife.app.database.dao.EquipmentDao;
import com.fitlife.app.database.dao.ExerciseDao;
import com.fitlife.app.database.dao.WorkoutDao;
import com.fitlife.app.database.entities.Exercise;
import com.fitlife.app.database.entities.Workout;
import com.fitlife.app.interfaces.OnExerciseActionListener;
import com.fitlife.app.utils.Constants;
import com.fitlife.app.utils.ImageHelper;
import com.fitlife.app.utils.SMSHelper;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Workout Detail Activity - View workout details and exercises
 * Supports editing and sharing (via SMS)
 */
public class WorkoutDetailActivity extends AppCompatActivity {
    
    private ImageView ivWorkoutImage;
    private TextView tvWorkoutName;
    private TextView tvWorkoutDescription;
    private MaterialButton btnShare;
    private MaterialButton btnEdit;
    private RecyclerView rvExercises;
    private TextView tvEquipmentList;
    
    private AppDatabase database;
    private WorkoutDao workoutDao;
    private ExerciseDao exerciseDao;
    private EquipmentDao equipmentDao;
    
    private Workout workout;
    private List<Exercise> exercises;
    private ExerciseAdapter exerciseAdapter;
    private List<String> equipmentList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_detail);
        
        initializeDatabase();
        initializeViews();
        setupToolbar();
        loadWorkoutData();
        setupListeners();
    }
    
    /**
     * Initialize database and DAOs
     */
    private void initializeDatabase() {
        database = AppDatabase.getInstance(this);
        workoutDao = database.workoutDao();
        exerciseDao = database.exerciseDao();
        equipmentDao = database.equipmentDao();
    }
    
    /**
     * Initialize UI components
     */
    private void initializeViews() {
        ivWorkoutImage = findViewById(R.id.iv_workout_image);
        tvWorkoutName = findViewById(R.id.tv_workout_name);
        tvWorkoutDescription = findViewById(R.id.tv_workout_description);
        btnShare = findViewById(R.id.btn_share);
        btnEdit = findViewById(R.id.btn_edit);
        rvExercises = findViewById(R.id.rv_exercises);
        tvEquipmentList = findViewById(R.id.tv_equipment_list);
        
        // Setup RecyclerView
        rvExercises.setLayoutManager(new LinearLayoutManager(this));
        exercises = new ArrayList<>();
        exerciseAdapter = new ExerciseAdapter(this, exercises, new OnExerciseActionListener() {
            @Override
            public void onExerciseClick(Exercise exercise) {
                boolean newState = !exercise.isCompleted();
                onExerciseCompletionChanged(exercise, newState);
            }

            @Override
            public void onEditExercise(Exercise exercise, int position) {
                Toast.makeText(WorkoutDetailActivity.this, "Edit via 'Edit Workout' button", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteExercise(Exercise exercise, int position) {
                 Toast.makeText(WorkoutDetailActivity.this, "Delete via 'Edit Workout' button", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onExerciseCompletionChanged(Exercise exercise, boolean isCompleted) {
                updateExerciseCompletion(exercise, isCompleted);
            }
        });
        rvExercises.setAdapter(exerciseAdapter);
    }
    
    /**
     * Update exercise completion status
     */
    private void updateExerciseCompletion(Exercise exercise, boolean isCompleted) {
        Executors.newSingleThreadExecutor().execute(() -> {
            exercise.setCompleted(isCompleted);
            exerciseDao.update(exercise);
            checkWorkoutCompletion();
        });
    }
    
    /**
     * Check if all exercises are complete
     */
    private void checkWorkoutCompletion() {
        if (workout == null) return;
        // Logic for checking workout completion could go here
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
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * Load workout data
     */
    private void loadWorkoutData() {
        int workoutId = getIntent().getIntExtra(Constants.EXTRA_WORKOUT_ID, -1);
        
        if (workoutId == -1) {
            Toast.makeText(this, "Error loading workout", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        Executors.newSingleThreadExecutor().execute(() -> {
            workout = workoutDao.getWorkoutById(workoutId);
            List<Exercise> dbExercises = exerciseDao.getExercisesByWorkoutId(workoutId);
            equipmentList = equipmentDao.getUniqueEquipmentNames(workoutId);
            
            runOnUiThread(() -> {
                if (workout != null) {
                    tvWorkoutName.setText(workout.getWorkoutName());
                    tvWorkoutDescription.setText(workout.getDescription());
                    
                    // Load image
                    if (workout.getImagePath() != null) {
                        Bitmap bitmap = ImageHelper.loadImageFromPath(workout.getImagePath());
                        if (bitmap != null) {
                            ivWorkoutImage.setImageBitmap(bitmap);
                        }
                    }
                    
                    // Display equipment list
                    if (equipmentList != null && !equipmentList.isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < equipmentList.size(); i++) {
                            sb.append("• ").append(equipmentList.get(i));
                            if (i < equipmentList.size() - 1) {
                                sb.append("\n");
                            }
                        }
                        tvEquipmentList.setText(sb.toString());
                    } else {
                        tvEquipmentList.setText(R.string.no_equipment);
                    }
                    
                    // Update exercises
                    exercises.clear();
                    exercises.addAll(dbExercises);
                    exerciseAdapter.notifyDataSetChanged();
                }
            });
        });
    }
    
    /**
     * Setup click listeners
     */
    private void setupListeners() {
        btnShare.setOnClickListener(v -> shareWorkout());
        btnEdit.setOnClickListener(v -> editWorkout());
    }
    
    /**
     * Share workout via SMS
     */
    private void shareWorkout() {
        if (workout == null) return;
        SMSHelper.openContactPicker(this, Constants.REQUEST_CONTACT_PICK);
    }
    
    /**
     * Edit workout
     */
    private void editWorkout() {
        if (workout == null) return;
        
        Intent intent = new Intent(this, EditWorkoutActivity.class);
        intent.putExtra(Constants.EXTRA_WORKOUT_ID, workout.getWorkoutId());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    
    // SMS Delegation Logic
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == Constants.REQUEST_CONTACT_PICK && resultCode == RESULT_OK && data != null) {
             Uri contactUri = data.getData();
             if (contactUri != null) {
                 String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
                 
                 try (Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null)) {
                     if (cursor != null && cursor.moveToFirst()) {
                         int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                         if (numberIndex >= 0) {
                             String number = cursor.getString(numberIndex);
                             checkSmsPermissionAndSend(number);
                         } else {
                            Toast.makeText(this, "Could not find phone number", Toast.LENGTH_SHORT).show();
                         }
                     }
                 } catch (Exception e) {
                     e.printStackTrace();
                     Toast.makeText(this, "Failed to retrieve phone number", Toast.LENGTH_SHORT).show();
                 }
             }
        }
    }
    
    private void checkSmsPermissionAndSend(String phoneNumber) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, 
                new String[]{android.Manifest.permission.SEND_SMS}, Constants.REQUEST_SMS_PERMISSION);
        } else {
            sendWorkoutSms(phoneNumber);
        }
    }

    private void sendWorkoutSms(String phoneNumber) {
        if (workout == null) return;
        
        Executors.newSingleThreadExecutor().execute(() -> {
            String message = SMSHelper.formatWorkoutMessage(workout, exercises, equipmentList);
            
            runOnUiThread(() -> {
                SMSHelper.sendSMS(this, phoneNumber, message);
            });
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                          @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_SMS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted. Please share the workout again.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "SMS permission required to share workouts.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (workout != null) {
            loadWorkoutData();
        }
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
