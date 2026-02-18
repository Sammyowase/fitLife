package com.fitlife.app.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.fitlife.app.database.entities.Equipment;

import java.util.List;

/**
 * Data Access Object for Equipment entity
 */
@Dao
public interface EquipmentDao {
    
    /**
     * Insert a new equipment item
     * @param equipment Equipment to insert
     * @return Row ID of inserted equipment
     */
    @Insert
    long insert(Equipment equipment);
    
    /**
     * Insert multiple equipment items
     * @param equipmentList List of equipment to insert
     * @return List of row IDs
     */
    @Insert
    List<Long> insertAll(List<Equipment> equipmentList);
    
    /**
     * Update an existing equipment item
     * @param equipment Equipment to update
     */
    @Update
    void update(Equipment equipment);
    
    /**
     * Delete an equipment item
     * @param equipment Equipment to delete
     */
    @Delete
    void delete(Equipment equipment);
    
    /**
     * Get all equipment for an exercise
     * @param exerciseId Exercise ID
     * @return List of equipment
     */
    @Query("SELECT * FROM equipment WHERE exercise_id = :exerciseId")
    List<Equipment> getEquipmentByExerciseId(int exerciseId);
    
    /**
     * Get all equipment for a workout (across all exercises)
     * @param workoutId Workout ID
     * @return List of equipment
     */
    @Query("SELECT DISTINCT e.* FROM equipment e " +
           "INNER JOIN exercises ex ON e.exercise_id = ex.exercise_id " +
           "WHERE ex.workout_id = :workoutId")
    List<Equipment> getEquipmentByWorkoutId(int workoutId);
    
    /**
     * Get unique equipment names for a workout (for checklist)
     * @param workoutId Workout ID
     * @return List of unique equipment names
     */
    @Query("SELECT DISTINCT e.equipment_name FROM equipment e " +
           "INNER JOIN exercises ex ON e.exercise_id = ex.exercise_id " +
           "WHERE ex.workout_id = :workoutId " +
           "ORDER BY e.equipment_name ASC")
    List<String> getUniqueEquipmentNames(int workoutId);
    
    /**
     * Delete all equipment for an exercise
     * @param exerciseId Exercise ID
     */
    @Query("DELETE FROM equipment WHERE exercise_id = :exerciseId")
    void deleteEquipmentByExerciseId(int exerciseId);
    
    /**
     * Get total equipment count for a user (across all workouts)
     * @param userId User ID
     * @return Total number of unique equipment items
     */
    @Query("SELECT COUNT(DISTINCT e.equipment_name) FROM equipment e " +
           "INNER JOIN exercises ex ON e.exercise_id = ex.exercise_id " +
           "INNER JOIN workouts w ON ex.workout_id = w.workout_id " +
           "WHERE w.user_id = :userId")
    int getTotalEquipmentCount(int userId);
}
