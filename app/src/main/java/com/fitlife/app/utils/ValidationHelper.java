package com.fitlife.app.utils;

import android.text.TextUtils;
import android.util.Patterns;

/**
 * Validation Helper for form field validation
 */
public class ValidationHelper {
    
    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    
    /**
     * Validate password length
     */
    public static boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= Constants.MIN_PASSWORD_LENGTH;
    }
    
    /**
     * Check if passwords match
     */
    public static boolean passwordsMatch(String password, String confirmPassword) {
        return !TextUtils.isEmpty(password) && password.equals(confirmPassword);
    }
    
    /**
     * Validate name (not empty and reasonable length)
     */
    public static boolean isValidName(String name) {
        return !TextUtils.isEmpty(name) && name.trim().length() >= 2 && name.trim().length() <= 50;
    }
    
    /**
     * Validate workout name
     */
    public static boolean isValidWorkoutName(String workoutName) {
        return !TextUtils.isEmpty(workoutName) && 
               workoutName.trim().length() > 0 && 
               workoutName.length() <= Constants.MAX_WORKOUT_NAME_LENGTH;
    }
    
    /**
     * Validate description length
     */
    public static boolean isValidDescription(String description) {
        // Description is optional, but if provided, check length
        return description == null || description.length() <= Constants.MAX_DESCRIPTION_LENGTH;
    }
    
    /**
     * Validate exercise name
     */
    public static boolean isValidExerciseName(String exerciseName) {
        return !TextUtils.isEmpty(exerciseName) && exerciseName.trim().length() > 0;
    }
    
    /**
     * Validate sets/reps (must be positive integer)
     */
    public static boolean isValidSetsOrReps(int value) {
        return value > 0 && value <= 100;
    }

    /**
     * Validate sets/reps from string input
     */
    public static boolean isValidSetsReps(String value) {
        if (TextUtils.isEmpty(value)) return false;
        try {
            int intValue = Integer.parseInt(value);
            return isValidSetsOrReps(intValue);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
