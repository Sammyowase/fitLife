package com.fitlife.app.database;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.fitlife.app.database.dao.WorkoutDao;
import com.fitlife.app.database.entities.Workout;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class WorkoutDaoTest {
    private WorkoutDao workoutDao;
    private AppDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        workoutDao = db.workoutDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void testInsertAndRetrieveWorkout() throws Exception {
        Workout workout = new Workout(1, "Morning Cardio", "Run and Jump", null);
        long id = workoutDao.insert(workout);
        
        Workout byId = workoutDao.getWorkoutById((int) id);
        assertNotNull(byId);
        assertEquals("Morning Cardio", byId.getWorkoutName());
        assertEquals(1, byId.getUserId());
    }

    @Test
    public void testWorkoutCounts() throws Exception {
        workoutDao.insert(new Workout(1, "Workout 1", "", null));
        workoutDao.insert(new Workout(1, "Workout 2", "", null));
        
        int count = workoutDao.getTotalWorkoutCount(1);
        assertEquals(2, count);
    }
    
    @Test
    public void testCompletionStatus() throws Exception {
        Workout workout = new Workout(1, "Test Workout", "", null);
        long id = workoutDao.insert(workout);
        
        workoutDao.updateCompletionStatus((int)id, true, System.currentTimeMillis());
        
        Workout updated = workoutDao.getWorkoutById((int)id);
        assertTrue(updated.isCompleted());
        assertEquals(1, workoutDao.getCompletedWorkoutCount(1));
    }
}
