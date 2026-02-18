package com.fitlife.app.interfaces;

import android.view.View;
import com.fitlife.app.database.entities.Workout;

/**
 * Interface for handling workout item click events
 */
public interface OnWorkoutClickListener {
    
    /**
     * Called when a workout item is clicked
     * @param workout The clicked workout
     */
    void onWorkoutClick(Workout workout);
    
    /**
     * Called when edit action is triggered
     * @param workout The workout to edit
     */
    void onEditWorkout(Workout workout);
    
    /**
     * Called when delete action is triggered
     * @param workout The workout to delete
     */
    void onDeleteWorkout(Workout workout);
    
    /**
     * Called when share action is triggered
     * @param workout The workout to share
     */
    void onShareWorkout(Workout workout);
    
    /**
     * Called when workout completion status changes
     * @param workout The workout
     * @param isCompleted New completion status
     */
    void onWorkoutCompletionChanged(Workout workout, boolean isCompleted);

    /**
     * Called when a workout item is long-pressed
     * @param workout The workout
     * @param view The anchor view for popup menu
     */
    void onWorkoutLongClick(Workout workout, View view);

    /**
     * Called when a workout item is double-tapped
     * @param workout The workout
     */
    void onWorkoutDoubleTap(Workout workout);
}
