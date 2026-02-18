package com.fitlife.app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.fitlife.app.database.entities.Exercise;
import com.fitlife.app.database.entities.Workout;

import java.util.ArrayList;
import java.util.List;

/**
 * SMS Helper utility for sending workout information via SMS
 */
public class SMSHelper {
    
    /**
     * Format workout information as SMS message
     * @param workout Workout object
     * @param exercises List of exercises in the workout
     * @param equipmentList List of unique equipment names
     * @return Formatted SMS message
     */
    public static String formatWorkoutMessage(Workout workout, 
                                             List<Exercise> exercises,
                                             List<String> equipmentList) {
        StringBuilder message = new StringBuilder();
        
        // Header
        message.append("FitLife Workout: ").append(workout.getWorkoutName()).append("\n\n");
        
        // Equipment section
        if (equipmentList != null && !equipmentList.isEmpty()) {
            message.append("Equipment Needed:\n");
            for (String equipment : equipmentList) {
                message.append("- ").append(equipment).append("\n");
            }
            message.append("\n");
        }
        
        // Exercises section
        if (exercises != null && !exercises.isEmpty()) {
            message.append("Exercises:\n");
            for (int i = 0; i < exercises.size(); i++) {
                Exercise exercise = exercises.get(i);
                message.append(i + 1).append(". ")
                       .append(exercise.getExerciseName())
                       .append(" - ")
                       .append(exercise.getSets()).append("x")
                       .append(exercise.getReps())
                       .append("\n");
            }
            message.append("\n");
        }
        
        // Footer
        message.append("Let's train together!");
        
        return message.toString();
    }
    
    /**
     * Send SMS message
     * @param context Application context
     * @param phoneNumber Recipient phone number
     * @param message Message content
     * @return true if sent successfully
     */
    public static boolean sendSMS(Context context, String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            
            // If message is too long, divide it into multiple parts
            if (message.length() > 160) {
                List<String> parts = smsManager.divideMessage(message);
                smsManager.sendMultipartTextMessage(phoneNumber, null, 
                    (ArrayList<String>) parts, null, null);
            } else {
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            }
            
            Toast.makeText(context, "SMS sent successfully!", Toast.LENGTH_SHORT).show();
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to send SMS: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
            return false;
        }
    }
    
    /**
     * Open contact picker to select a phone number
     * @param activity Activity context
     * @param requestCode Request code for onActivityResult
     */
    public static void openContactPicker(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        activity.startActivityForResult(intent, requestCode);
    }
}
