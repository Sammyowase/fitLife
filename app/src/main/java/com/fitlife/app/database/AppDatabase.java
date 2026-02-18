package com.fitlife.app.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.fitlife.app.database.dao.EquipmentDao;
import com.fitlife.app.database.dao.ExerciseDao;
import com.fitlife.app.database.dao.UserDao;
import com.fitlife.app.database.dao.WorkoutDao;
import com.fitlife.app.database.entities.Equipment;
import com.fitlife.app.database.entities.Exercise;
import com.fitlife.app.database.entities.User;
import com.fitlife.app.database.entities.Workout;
import com.fitlife.app.utils.Constants;

/**
 * Room Database class for FitLife application
 * Singleton pattern ensures only one database instance exists
 */
@Database(
    entities = {User.class, Workout.class, Exercise.class, Equipment.class},
    version = Constants.DATABASE_VERSION,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    
    // Singleton instance
    private static AppDatabase instance;
    
    // Abstract methods to get DAOs
    public abstract UserDao userDao();
    public abstract WorkoutDao workoutDao();
    public abstract ExerciseDao exerciseDao();
    public abstract EquipmentDao equipmentDao();
    
    /**
     * Get singleton instance of the database
     * Thread-safe implementation using synchronized block
     * 
     * @param context Application context
     * @return AppDatabase instance
     */
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                context.getApplicationContext(),
                AppDatabase.class,
                Constants.DATABASE_NAME
            )
            .fallbackToDestructiveMigration() // For development - recreate DB on schema changes
            .build();
        }
        return instance;
    }
    
    /**
     * Destroy the database instance
     * Useful for testing or when you need to recreate the database
     */
    public static void destroyInstance() {
        if (instance != null && instance.isOpen()) {
            instance.close();
        }
        instance = null;
    }
}
