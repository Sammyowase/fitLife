package com.fitlife.app.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * User entity representing a user in the database
 */
@Entity(tableName = "users")
public class User {
    
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    private int userId;
    
    @ColumnInfo(name = "full_name")
    @NonNull
    private String fullName;
    
    @ColumnInfo(name = "email")
    @NonNull
    private String email;
    
    @ColumnInfo(name = "password_hash")
    @NonNull
    private String passwordHash;
    
    @ColumnInfo(name = "created_at")
    private long createdAt;
    
    @ColumnInfo(name = "last_login")
    private long lastLogin;
    
    // Constructor
    public User(@NonNull String fullName, @NonNull String email, @NonNull String passwordHash) {
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = System.currentTimeMillis();
        this.lastLogin = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    @NonNull
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(@NonNull String fullName) {
        this.fullName = fullName;
    }
    
    @NonNull
    public String getEmail() {
        return email;
    }
    
    public void setEmail(@NonNull String email) {
        this.email = email;
    }
    
    @NonNull
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(@NonNull String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    public long getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }
}
