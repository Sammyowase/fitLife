package com.fitlife.app.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Workout entity representing a workout routine in the database
 */
@Entity(tableName = "workouts",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "user_id",
                childColumns = "user_id",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("user_id")})
public class Workout {
    
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "workout_id")
    private int workoutId;
    
    @ColumnInfo(name = "user_id")
    private int userId;
    
    @ColumnInfo(name = "workout_name")
    @NonNull
    private String workoutName;
    
    @ColumnInfo(name = "description")
    private String description;
    
    @ColumnInfo(name = "image_path")
    private String imagePath;
    
    @ColumnInfo(name = "created_at")
    private long createdAt;
    
    @ColumnInfo(name = "updated_at")
    private long updatedAt;
    
    @ColumnInfo(name = "is_completed")
    private boolean isCompleted;
    
    // Constructor
    public Workout(int userId, @NonNull String workoutName, String description, String imagePath) {
        this.userId = userId;
        this.workoutName = workoutName;
        this.description = description;
        this.imagePath = imagePath;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.isCompleted = false;
    }

    // Constructor for backward compatibility
    @Ignore
    public Workout(int userId, @NonNull String workoutName, String description) {
        this(userId, workoutName, description, null);
    }
    
    // Getters and Setters
    public int getWorkoutId() {
        return workoutId;
    }
    
    public void setWorkoutId(int workoutId) {
        this.workoutId = workoutId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    @NonNull
    public String getWorkoutName() {
        return workoutName;
    }
    
    public void setWorkoutName(@NonNull String workoutName) {
        this.workoutName = workoutName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getImagePath() {
        return imagePath;
    }
    
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    public long getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public boolean isCompleted() {
        return isCompleted;
    }
    
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
