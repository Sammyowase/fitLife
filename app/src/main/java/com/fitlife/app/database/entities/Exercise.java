package com.fitlife.app.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Exercise entity representing an exercise within a workout
 */
@Entity(tableName = "exercises",
        foreignKeys = @ForeignKey(entity = Workout.class,
                parentColumns = "workout_id",
                childColumns = "workout_id",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("workout_id")})
public class Exercise {
    
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "exercise_id")
    private int exerciseId;
    
    @ColumnInfo(name = "workout_id")
    private int workoutId;
    
    @ColumnInfo(name = "exercise_name")
    @NonNull
    private String exerciseName;
    
    @ColumnInfo(name = "sets")
    private int sets;
    
    @ColumnInfo(name = "reps")
    private int reps;
    
    @ColumnInfo(name = "instructions")
    private String instructions;
    
    @ColumnInfo(name = "is_completed")
    private boolean isCompleted;
    
    @ColumnInfo(name = "order_index")
    private int orderIndex;
    
    // Constructor
    public Exercise(int workoutId, @NonNull String exerciseName, int sets, int reps, 
                   String instructions, int orderIndex) {
        this.workoutId = workoutId;
        this.exerciseName = exerciseName;
        this.sets = sets;
        this.reps = reps;
        this.instructions = instructions;
        this.orderIndex = orderIndex;
        this.isCompleted = false;
    }

    // Constructor for backward compatibility
    @Ignore
    public Exercise(int workoutId, @NonNull String exerciseName, int sets, int reps) {
        this(workoutId, exerciseName, sets, reps, null, 0);
    }
    
    // Getters and Setters
    public int getExerciseId() {
        return exerciseId;
    }
    
    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }
    
    public int getWorkoutId() {
        return workoutId;
    }
    
    public void setWorkoutId(int workoutId) {
        this.workoutId = workoutId;
    }
    
    @NonNull
    public String getExerciseName() {
        return exerciseName;
    }
    
    public void setExerciseName(@NonNull String exerciseName) {
        this.exerciseName = exerciseName;
    }
    
    public int getSets() {
        return sets;
    }
    
    public void setSets(int sets) {
        this.sets = sets;
    }
    
    public int getReps() {
        return reps;
    }
    
    public void setReps(int reps) {
        this.reps = reps;
    }
    
    public String getInstructions() {
        return instructions;
    }
    
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    
    public boolean isCompleted() {
        return isCompleted;
    }
    
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
    
    public int getOrderIndex() {
        return orderIndex;
    }
    
    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }
}
