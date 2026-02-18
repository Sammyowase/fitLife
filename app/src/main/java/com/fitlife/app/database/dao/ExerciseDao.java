package com.fitlife.app.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.fitlife.app.database.entities.Exercise;

import java.util.List;

/**
 * Data Access Object for Exercise entity
 */
@Dao
public interface ExerciseDao {
    
    /**
     * Insert a new exercise
     * @param exercise Exercise to insert
     * @return Row ID of inserted exercise
     */
    @Insert
    long insert(Exercise exercise);
    
    /**
     * Insert multiple exercises
     * @param exercises List of exercises to insert
     * @return List of row IDs
     */
    @Insert
    List<Long> insertAll(List<Exercise> exercises);
    
    /**
     * Update an existing exercise
     * @param exercise Exercise to update
     */
    @Update
    void update(Exercise exercise);
    
    /**
     * Delete an exercise
     * @param exercise Exercise to delete
     */
    @Delete
    void delete(Exercise exercise);
    
    /**
     * Get all exercises for a workout
     * @param workoutId Workout ID
     * @return List of exercises ordered by order_index
     */
    @Query("SELECT * FROM exercises WHERE workout_id = :workoutId ORDER BY order_index ASC")
    List<Exercise> getExercisesByWorkoutId(int workoutId);
    
    /**
     * Get exercise by ID
     * @param exerciseId Exercise ID
     * @return Exercise object or null
     */
    @Query("SELECT * FROM exercises WHERE exercise_id = :exerciseId LIMIT 1")
    Exercise getExerciseById(int exerciseId);
    
    /**
     * Get completed exercises for a workout
     * @param workoutId Workout ID
     * @return List of completed exercises
     */
    @Query("SELECT * FROM exercises WHERE workout_id = :workoutId AND is_completed = 1 ORDER BY order_index ASC")
    List<Exercise> getCompletedExercises(int workoutId);
    
    /**
     * Mark exercise as completed
     * @param exerciseId Exercise ID
     * @param isCompleted Completion status
     */
    @Query("UPDATE exercises SET is_completed = :isCompleted WHERE exercise_id = :exerciseId")
    void updateCompletionStatus(int exerciseId, boolean isCompleted);
    
    /**
     * Reset all exercises in a workout to incomplete
     * @param workoutId Workout ID
     */
    @Query("UPDATE exercises SET is_completed = 0 WHERE workout_id = :workoutId")
    void resetExercises(int workoutId);
    
    /**
     * Get exercise count for a workout
     * @param workoutId Workout ID
     * @return Number of exercises
     */
    @Query("SELECT COUNT(*) FROM exercises WHERE workout_id = :workoutId")
    int getExerciseCount(int workoutId);
    
    /**
     * Delete all exercises for a workout
     * @param workoutId Workout ID
     */
    @Query("DELETE FROM exercises WHERE workout_id = :workoutId")
    void deleteExercisesByWorkoutId(int workoutId);

    /**
     * Insert multiple exercises (alias for insertAll)
     * @param exercises List of exercises to insert
     * @return List of row IDs
     */
    @Insert
    List<Long> insertBack(List<Exercise> exercises);

    /**
     * Delete multiple exercises
     * @param exercises List of exercises to delete
     */
    @Delete
    void delete(List<Exercise> exercises);
}
