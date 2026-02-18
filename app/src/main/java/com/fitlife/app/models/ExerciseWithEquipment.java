package com.fitlife.app.models;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.fitlife.app.database.entities.Equipment;
import com.fitlife.app.database.entities.Exercise;

import java.util.List;

/**
 * Model class representing an Exercise with its associated Equipment
 * Used for querying exercises with their equipment in a single query
 */
public class ExerciseWithEquipment {
    
    @Embedded
    public Exercise exercise;
    
    @Relation(
        parentColumn = "exercise_id",
        entityColumn = "exercise_id"
    )
    public List<Equipment> equipment;
    
    /**
     * Get equipment names as a comma-separated string
     */
    public String getEquipmentNamesString() {
        if (equipment == null || equipment.isEmpty()) {
            return "No equipment needed";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < equipment.size(); i++) {
            sb.append(equipment.get(i).getEquipmentName());
            if (i < equipment.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
    
    /**
     * Get the number of equipment items needed
     */
    public int getEquipmentCount() {
        return equipment != null ? equipment.size() : 0;
    }
    
    /**
     * Check if exercise requires equipment
     */
    public boolean requiresEquipment() {
        return equipment != null && !equipment.isEmpty();
    }
}
