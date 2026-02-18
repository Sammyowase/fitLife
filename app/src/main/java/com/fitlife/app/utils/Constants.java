package com.fitlife.app.utils;

/**
 * Constants class containing all application-wide constant values
 */
public class Constants {
    
    // SharedPreferences Keys
    public static final String PREF_NAME = "FitLifePreferences";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_EMAIL = "user_email";
    
    // Database
    public static final String DATABASE_NAME = "fitlife_database";
    public static final int DATABASE_VERSION = 1;
    
    // Intent Extras
    public static final String EXTRA_WORKOUT_ID = "workout_id";
    public static final String EXTRA_EXERCISE_ID = "exercise_id";
    public static final String EXTRA_IS_EDIT_MODE = "is_edit_mode";
    
    // Request Codes
    public static final int REQUEST_CODE_CAMERA = 1001;
    public static final int REQUEST_CODE_GALLERY = 1002;
    public static final int REQUEST_CODE_CONTACTS = 1003;
    public static final int REQUEST_CODE_SMS_PERMISSION = 1004;
    public static final int REQUEST_CODE_CAMERA_PERMISSION = 1005;
    
    // Legacy mapping for missing symbols
    public static final int REQUEST_IMAGE_PICK = REQUEST_CODE_GALLERY;
    public static final int REQUEST_CONTACT_PICK = REQUEST_CODE_CONTACTS;
    public static final int REQUEST_SMS_PERMISSION = REQUEST_CODE_SMS_PERMISSION;
    public static final int REQUEST_CODE_STORAGE_PERMISSION = 1006;
    public static final int REQUEST_CODE_CONTACTS_PERMISSION = 1007;
    
    // Image Storage
    public static final String IMAGE_DIRECTORY = "workout_images";
    public static final int IMAGE_COMPRESSION_QUALITY = 85;
    public static final int MAX_IMAGE_WIDTH = 1024;
    public static final int MAX_IMAGE_HEIGHT = 1024;
    
    // Validation
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_WORKOUT_NAME_LENGTH = 100;
    public static final int MAX_DESCRIPTION_LENGTH = 500;
    
    // Gesture Detection
    public static final float SHAKE_THRESHOLD = 15.0f;
    public static final int SHAKE_TIMEOUT_MS = 500;
    
    // Animation Durations
    public static final int ANIMATION_DURATION_SHORT = 200;
    public static final int ANIMATION_DURATION_MEDIUM = 300;
    public static final int ANIMATION_DURATION_LONG = 400;
    
    // Splash Screen
    public static final int SPLASH_DISPLAY_LENGTH = 2000; // 2 seconds
    
    private Constants() {
        // Private constructor to prevent instantiation
    }
}
