package com.fitlife.app.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fitlife.app.R;
import com.fitlife.app.adapters.WorkoutAdapter;
import com.fitlife.app.database.AppDatabase;
import com.fitlife.app.database.dao.EquipmentDao;
import com.fitlife.app.database.dao.ExerciseDao;
import com.fitlife.app.database.dao.WorkoutDao;
import com.fitlife.app.database.entities.Exercise;
import com.fitlife.app.database.entities.Workout;
import com.fitlife.app.gestures.ShakeDetector;
import com.fitlife.app.gestures.SwipeGestureCallback;
import com.fitlife.app.interfaces.OnWorkoutClickListener;
import com.fitlife.app.utils.Constants;
import com.fitlife.app.utils.PreferenceManager;
import com.fitlife.app.utils.SMSHelper;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Home Activity - Main dashboard showing workout list
 * Includes shake gesture, swipe gestures, long-press/double-tap interactions,
 * and handles empty/loading states.
 */
public class HomeActivity extends AppCompatActivity {
    
    private TextView tvWelcome;
    private TextView tvTotalWorkouts;
    private TextView tvCompletedWorkouts;
    private TextView tvEquipmentCount;
    private RecyclerView rvWorkouts;
    private View layoutEmptyState;
    private ProgressBar progressBar;
    private FloatingActionButton fabCreateWorkout;
    
    private AppDatabase database;
    private WorkoutDao workoutDao;
    private EquipmentDao equipmentDao;
    private ExerciseDao exerciseDao;
    private PreferenceManager preferenceManager;
    
    private List<Workout> workoutList;
    private WorkoutAdapter workoutAdapter;
    private Workout pendingShareWorkout;
    
    // Shake detection
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ShakeDetector shakeDetector;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        initializeDatabase();
        initializeViews();
        setupToolbar();
        setupShakeDetector();
        loadData();
        setupListeners();
    }
    
    /**
     * Initialize database and DAOs
     */
    private void initializeDatabase() {
        database = AppDatabase.getInstance(this);
        workoutDao = database.workoutDao();
        equipmentDao = database.equipmentDao();
        exerciseDao = database.exerciseDao();
        preferenceManager = new PreferenceManager(this);
    }
    
    /**
     * Initialize UI components
     */
    private void initializeViews() {
        tvWelcome = findViewById(R.id.tv_welcome);
        View layoutTotalWorkouts = findViewById(R.id.layout_total_workouts);
        View layoutCompletedWorkouts = findViewById(R.id.layout_completed_workouts);
        View layoutEquipmentCount = findViewById(R.id.layout_equipment_count);
        
        tvTotalWorkouts = layoutTotalWorkouts.findViewById(R.id.tv_stat_value);
        TextView tvLabelTotal = layoutTotalWorkouts.findViewById(R.id.tv_stat_label);
        tvLabelTotal.setText(R.string.total_workouts);
        
        tvCompletedWorkouts = layoutCompletedWorkouts.findViewById(R.id.tv_stat_value);
        TextView tvLabelCompleted = layoutCompletedWorkouts.findViewById(R.id.tv_stat_label);
        tvLabelCompleted.setText(R.string.completed_this_week);
        
        tvEquipmentCount = layoutEquipmentCount.findViewById(R.id.tv_stat_value);
        TextView tvLabelEquipment = layoutEquipmentCount.findViewById(R.id.tv_stat_label);
        tvLabelEquipment.setText(R.string.equipment_items);
        rvWorkouts = findViewById(R.id.rv_workouts);
        layoutEmptyState = findViewById(R.id.layout_empty_state);
        progressBar = findViewById(R.id.progressBar);
        fabCreateWorkout = findViewById(R.id.fab_create_workout);
        
        // Set welcome message
        String userName = preferenceManager.getUserName();
        tvWelcome.setText(getString(R.string.hi_user, userName));
        
        // Setup RecyclerView
        rvWorkouts.setLayoutManager(new LinearLayoutManager(this));
        workoutList = new ArrayList<>();
        workoutAdapter = new WorkoutAdapter(this, workoutList, new OnWorkoutClickListener() {
            @Override
            public void onWorkoutClick(Workout workout) {
                openWorkoutDetail(workout);
            }

            @Override
            public void onEditWorkout(Workout workout) {
                editWorkout(workout);
            }

            @Override
            public void onShareWorkout(Workout workout) {
                shareWorkout(workout);
            }

            @Override
            public void onDeleteWorkout(Workout workout) {
                confirmDeleteWorkout(workout);
            }

            @Override
            public void onWorkoutCompletionChanged(Workout workout, boolean isCompleted) {
                updateWorkoutCompletion(workout, isCompleted);
            }

            @Override
            public void onWorkoutLongClick(Workout workout, View view) {
                 showWorkoutOptionsMenu(workout, view);
            }

            @Override
            public void onWorkoutDoubleTap(Workout workout) {
                 showQuickViewDialog(workout);
            }
        });
        rvWorkouts.setAdapter(workoutAdapter);
        
        // Setup swipe gestures
        setupSwipeGestures();
    }
    
    /**
     * Show options menu for workout
     */
    private void showWorkoutOptionsMenu(Workout workout, View anchor) {
        android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(this, anchor);
        popupMenu.inflate(R.menu.menu_workout_context);
        
        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_edit) {
                editWorkout(workout);
                return true;
            } else if (id == R.id.action_share) {
                shareWorkout(workout);
                return true;
            } else if (id == R.id.action_delete) {
                confirmDeleteWorkout(workout);
                return true;
            } else if (id == R.id.action_mark_complete) {
                updateWorkoutCompletion(workout, !workout.isCompleted());
                return true;
            }
            return false;
        });
        
        popupMenu.show();
    }

    /**
     * Show quick view dialog
     */
    private void showQuickViewDialog(Workout workout) {
        new MaterialAlertDialogBuilder(this)
            .setTitle(workout.getWorkoutName())
            .setMessage(workout.getDescription() + "\n\n" + 
                       (workout.isCompleted() ? getString(R.string.completed) : getString(R.string.in_progress)))
            .setPositiveButton(R.string.view_details, (dialog, which) -> {
                 openWorkoutDetail(workout);
            })
            .setNeutralButton(R.string.close, null)
            .show();
    }
    
    /**
     * Setup swipe gestures
     */
    private void setupSwipeGestures() {
        SwipeGestureCallback swipeCallback = new SwipeGestureCallback(this) {
            @Override
            public void onSwipeRight(int position) {
                Workout workout = workoutAdapter.getWorkoutAt(position);
                updateWorkoutCompletion(workout, true);
            }

            @Override
            public void onSwipeLeft(int position) {
                Workout workout = workoutAdapter.getWorkoutAt(position);
                confirmDeleteWorkout(workout);
            }
        };
        
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeCallback);
        itemTouchHelper.attachToRecyclerView(rvWorkouts);
    }
    
    /**
     * Setup toolbar
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
    }
    
    /**
     * Setup shake detector
     */
    private void setupShakeDetector() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
        shakeDetector = new ShakeDetector(() -> {
            new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.confirm_reset_title)
                .setMessage(R.string.confirm_reset_message)
                .setPositiveButton(R.string.reset, (dialog, which) -> {
                    resetAllWorkouts();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
        });
    }
    
    /**
     * Load workout data and statistics with loading state
     */
    private void loadData() {
        int userId = preferenceManager.getUserId();
        
        // Show loading state
        progressBar.setVisibility(View.VISIBLE);
        // Don't hide RV yet to prevent flickering if data is cached/fast
        layoutEmptyState.setVisibility(View.GONE);
        
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Load workouts
                List<Workout> workouts = workoutDao.getWorkoutsByUserId(userId);
                
                // Load statistics
                int totalWorkouts = workoutDao.getTotalWorkoutCount(userId);
                int completedWorkouts = workoutDao.getCompletedWorkoutCount(userId);
                int equipmentCount = equipmentDao.getTotalEquipmentCount(userId);
                
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    workoutList.clear();
                    workoutList.addAll(workouts);
                    workoutAdapter.notifyDataSetChanged();
                    
                    // Toggle empty state
                    if (workoutList.isEmpty()) {
                        layoutEmptyState.setVisibility(View.VISIBLE);
                        rvWorkouts.setVisibility(View.GONE);
                    } else {
                        layoutEmptyState.setVisibility(View.GONE);
                        rvWorkouts.setVisibility(View.VISIBLE);
                    }
                    
                    // Update statistics
                    tvTotalWorkouts.setText(String.valueOf(totalWorkouts));
                    tvCompletedWorkouts.setText(String.valueOf(completedWorkouts));
                    tvEquipmentCount.setText(String.valueOf(equipmentCount));
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(HomeActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    /**
     * Setup click listeners
     */
    private void setupListeners() {
        fabCreateWorkout.setOnClickListener(v -> {
            startActivity(new Intent(this, CreateWorkoutActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }
    
    /**
     * Reset all workouts
     */
    private void resetAllWorkouts() {
        int userId = preferenceManager.getUserId();
        
        Executors.newSingleThreadExecutor().execute(() -> {
            workoutDao.resetAllWorkouts(userId);
            
            runOnUiThread(() -> {
                Toast.makeText(this, "All workouts reset!", Toast.LENGTH_SHORT).show();
                loadData();
            });
        });
    }
    
    /**
     * Navigation methods
     */
    private void openWorkoutDetail(Workout workout) {
        Intent intent = new Intent(this, WorkoutDetailActivity.class);
        intent.putExtra(Constants.EXTRA_WORKOUT_ID, workout.getWorkoutId());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void editWorkout(Workout workout) {
        Intent intent = new Intent(this, EditWorkoutActivity.class);
        intent.putExtra(Constants.EXTRA_WORKOUT_ID, workout.getWorkoutId());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void shareWorkout(Workout workout) {
        pendingShareWorkout = workout;
        SMSHelper.openContactPicker(this, Constants.REQUEST_CONTACT_PICK);
    }

    private void confirmDeleteWorkout(Workout workout) {
        new MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_workout)
            .setMessage(R.string.confirm_delete_workout)
            .setPositiveButton(R.string.delete, (dialog, which) -> {
                deleteWorkout(workout);
            })
            .setNegativeButton(R.string.cancel, (dialog, which) -> {
                workoutAdapter.notifyDataSetChanged();
            })
            .show();
    }

    private void deleteWorkout(Workout workout) {
        Executors.newSingleThreadExecutor().execute(() -> {
            workoutDao.delete(workout);
            runOnUiThread(() -> {
                Toast.makeText(this, getString(R.string.workout_deleted), Toast.LENGTH_SHORT).show();
                loadData();
            });
        });
    }

    private void updateWorkoutCompletion(Workout workout, boolean isCompleted) {
        Executors.newSingleThreadExecutor().execute(() -> {
            workout.setCompleted(isCompleted);
            workout.setUpdatedAt(System.currentTimeMillis());
            workoutDao.update(workout);
            
            runOnUiThread(() -> {
                workoutAdapter.notifyDataSetChanged();
                loadData();
            });
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == Constants.REQUEST_CONTACT_PICK && resultCode == RESULT_OK && data != null) {
             if (pendingShareWorkout != null) {
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
        if (pendingShareWorkout == null) return;
        
        Executors.newSingleThreadExecutor().execute(() -> {
            int workoutId = pendingShareWorkout.getWorkoutId();
            List<Exercise> exercises = exerciseDao.getExercisesByWorkoutId(workoutId);
            List<String> equipment = equipmentDao.getUniqueEquipmentNames(workoutId);
            
            String message = SMSHelper.formatWorkoutMessage(pendingShareWorkout, exercises, equipment);
            
            runOnUiThread(() -> {
                boolean sent = SMSHelper.sendSMS(this, phoneNumber, message);
                if (sent) {
                    pendingShareWorkout = null;
                }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            return true;
        } else if (id == R.id.action_logout) {
            showLogoutConfirmation();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
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
    
    private void logout() {
        preferenceManager.clearSession();
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(shakeDetector, accelerometer, 
                SensorManager.SENSOR_DELAY_UI);
        }
        loadData();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(shakeDetector);
    }
}
