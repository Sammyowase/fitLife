package com.fitlife.app.database;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.fitlife.app.database.dao.UserDao;
import com.fitlife.app.database.entities.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class UserDaoTest {
    private UserDao userDao;
    private AppDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        userDao = db.userDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void writeUserAndReadInList() throws Exception {
        User user = new User("Test User", "test@example.com", "hashed_password");
        user.setCreatedAt(System.currentTimeMillis());
        
        long id = userDao.insert(user);
        User byId = userDao.getUserById((int) id);
        
        assertEquals(byId.getEmail(), "test@example.com");
        assertEquals(byId.getFullName(), "Test User");
    }

    @Test
    public void checkEmailExists() throws Exception {
        User user = new User("Test User", "test@example.com", "hashed_password");
        
        userDao.insert(user);
        
        assertTrue(userDao.emailExists("test@example.com") > 0);
        assertEquals(0, userDao.emailExists("other@example.com"));
    }
}
