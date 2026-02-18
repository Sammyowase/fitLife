package com.fitlife.app.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.fitlife.app.database.entities.Workout;

import java.util.List;

/**
 * Data Access Object for Workout entity
 */
@Dao
public interface WorkoutDao {
    
    /**
     * Insert a new workout
     * @param workout Workout to insert
     * @return Row ID of inserted workout
     */
    @Insert
    long insert(Workout workout);
    
    /**
     * Update an existing workout
     * @param workout Workout to update
     */
    @Update
    void update(Workout workout);
    
    /**
     * Delete a workout (cascades to exercises and equipment)
     * @param workout Workout to delete
     */
    @Delete
    void delete(Workout workout);
    
    /**
     * Get all workouts for a user
     * @param userId User ID
     * @return List of workouts
     */
    @Query("SELECT * FROM workouts WHERE user_id = :userId ORDER BY created_at DESC")
    List<Workout> getWorkoutsByUserId(int userId);
    
    /**
     * Get workout by ID
     * @param workoutId Workout ID
     * @return Workout object or null
     */
    @Query("SELECT * FROM workouts WHERE workout_id = :workoutId LIMIT 1")
    Workout getWorkoutById(int workoutId);
    
    /**
     * Get completed workouts for a user
     * @param userId User ID
     * @return List of completed workouts
     */
    @Query("SELECT * FROM workouts WHERE user_id = :userId AND is_completed = 1 ORDER BY updated_at DESC")
    List<Workout> getCompletedWorkouts(int userId);
    
    /**
     * Get incomplete workouts for a user
     * @param userId User ID
     * @return List of incomplete workouts
     */
    @Query("SELECT * FROM workouts WHERE user_id = :userId AND is_completed = 0 ORDER BY created_at DESC")
    List<Workout> getIncompleteWorkouts(int userId);
    
    /**
     * Mark workout as completed
     * @param workoutId Workout ID
     * @param isCompleted Completion status
     */
    @Query("UPDATE workouts SET is_completed = :isCompleted, updated_at = :timestamp WHERE workout_id = :workoutId")
    void updateCompletionStatus(int workoutId, boolean isCompleted, long timestamp);
    
    /**
     * Get total workout count for a user
     * @param userId User ID
     * @return Total number of workouts
     */
    @Query("SELECT COUNT(*) FROM workouts WHERE user_id = :userId")
    int getTotalWorkoutCount(int userId);
    
    /**
     * Get completed workout count for a user
     * @param userId User ID
     * @return Number of completed workouts
     */
    @Query("SELECT COUNT(*) FROM workouts WHERE user_id = :userId AND is_completed = 1")
    int getCompletedWorkoutCount(int userId);
    
    /**
     * Reset all workouts to incomplete for a user
     * @param userId User ID
     */
    @Query("UPDATE workouts SET is_completed = 0 WHERE user_id = :userId")
    void resetAllWorkouts(int userId);
}
