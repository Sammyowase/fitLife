package com.fitlife.app.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.fitlife.app.database.entities.User;

/**
 * Data Access Object for User entity
 */
@Dao
public interface UserDao {
    
    /**
     * Insert a new user
     * @param user User to insert
     * @return Row ID of inserted user
     */
    @Insert
    long insert(User user);
    
    /**
     * Update an existing user
     * @param user User to update
     */
    @Update
    void update(User user);
    
    /**
     * Delete a user
     * @param user User to delete
     */
    @Delete
    void delete(User user);
    
    /**
     * Get user by email
     * @param email User email
     * @return User object or null
     */
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User getUserByEmail(String email);
    
    /**
     * Get user by ID
     * @param userId User ID
     * @return User object or null
     */
    @Query("SELECT * FROM users WHERE user_id = :userId LIMIT 1")
    User getUserById(int userId);
    
    /**
     * Check if email exists
     * @param email Email to check
     * @return Count of users with this email
     */
    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    int emailExists(String email);
    
    /**
     * Update last login timestamp
     * @param userId User ID
     * @param timestamp Login timestamp
     */
    @Query("UPDATE users SET last_login = :timestamp WHERE user_id = :userId")
    void updateLastLogin(int userId, long timestamp);
}
