package com.fitlife.app.models;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.fitlife.app.database.entities.Exercise;
import com.fitlife.app.database.entities.Workout;

import java.util.List;

/**
 * Model class representing a Workout with its associated Exercises
 * Used for querying workouts with their exercises in a single query
 */
public class WorkoutWithExercises {
    
    @Embedded
    public Workout workout;
    
    @Relation(
        parentColumn = "workout_id",
        entityColumn = "workout_id"
    )
    public List<Exercise> exercises;
    
    /**
     * Get the total number of exercises in this workout
     */
    public int getExerciseCount() {
        return exercises != null ? exercises.size() : 0;
    }
    
    /**
     * Get the number of completed exercises
     */
    public int getCompletedExerciseCount() {
        if (exercises == null) return 0;
        int count = 0;
        for (Exercise exercise : exercises) {
            if (exercise.isCompleted()) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Check if all exercises are completed
     */
    public boolean isFullyCompleted() {
        if (exercises == null || exercises.isEmpty()) return false;
        for (Exercise exercise : exercises) {
            if (!exercise.isCompleted()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Get completion percentage (0-100)
     */
    public int getCompletionPercentage() {
        if (exercises == null || exercises.isEmpty()) return 0;
        return (int) ((getCompletedExerciseCount() * 100.0) / getExerciseCount());
    }
}
