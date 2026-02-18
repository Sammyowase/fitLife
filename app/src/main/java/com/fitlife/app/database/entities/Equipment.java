package com.fitlife.app.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Equipment entity representing equipment needed for an exercise
 */
@Entity(tableName = "equipment",
        foreignKeys = @ForeignKey(entity = Exercise.class,
                parentColumns = "exercise_id",
                childColumns = "exercise_id",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("exercise_id")})
public class Equipment {
    
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "equipment_id")
    private int equipmentId;
    
    @ColumnInfo(name = "exercise_id")
    private int exerciseId;
    
    @ColumnInfo(name = "equipment_name")
    @NonNull
    private String equipmentName;
    
    // Constructor
    public Equipment(int exerciseId, @NonNull String equipmentName) {
        this.exerciseId = exerciseId;
        this.equipmentName = equipmentName;
    }
    
    // Getters and Setters
    public int getEquipmentId() {
        return equipmentId;
    }
    
    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }
    
    public int getExerciseId() {
        return exerciseId;
    }
    
    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }
    
    @NonNull
    public String getEquipmentName() {
        return equipmentName;
    }
    
    public void setEquipmentName(@NonNull String equipmentName) {
        this.equipmentName = equipmentName;
    }
}
