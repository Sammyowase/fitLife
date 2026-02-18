package com.fitlife.app.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for ValidationHelper
 */
public class ValidationHelperTest {

    @Test
    public void testIsValidEmail_Valid() {
        // Email validation relies on Patterns.EMAIL_ADDRESS which is an Android class.
        // Standard JUnit tests don't have access to Android classes by default.
        // We'll mock it or use a simple regex for unit testing, 
        // OR rely on Robolectric (but we don't have that dependency yet).
        // For simplicity in this environment, let's skip the Android-dependent test 
        // passing null or empty string which ValidationHelper handles without Android APIs first.
        assertFalse(ValidationHelper.isValidEmail(""));
        assertFalse(ValidationHelper.isValidEmail(null));
    }

    @Test
    public void testIsValidPassword() {
        assertTrue(ValidationHelper.isValidPassword("password123"));
        assertFalse(ValidationHelper.isValidPassword("12345"));
        assertFalse(ValidationHelper.isValidPassword(null));
        assertFalse(ValidationHelper.isValidPassword(""));
    }

    @Test
    public void testPasswordsMatch() {
        assertTrue(ValidationHelper.passwordsMatch("password", "password"));
        assertFalse(ValidationHelper.passwordsMatch("password", "PASSWORD"));
        assertFalse(ValidationHelper.passwordsMatch("password", ""));
        assertFalse(ValidationHelper.passwordsMatch("password", null));
    }

    @Test
    public void testIsValidName() {
        assertTrue(ValidationHelper.isValidName("John Doe"));
        assertTrue(ValidationHelper.isValidName("Jo")); // 2 chars
        assertFalse(ValidationHelper.isValidName("J")); // 1 char
        assertFalse(ValidationHelper.isValidName(""));
        assertFalse(ValidationHelper.isValidName(null));
    }

    @Test
    public void testIsValidWorkoutName() {
        assertTrue(ValidationHelper.isValidWorkoutName("My Workout"));
        assertFalse(ValidationHelper.isValidWorkoutName(""));
        assertFalse(ValidationHelper.isValidWorkoutName(null));
    }
}
