package com.fitlife.app.interfaces;

import com.fitlife.app.database.entities.Exercise;

/**
 * Interface for handling exercise item actions
 */
public interface OnExerciseActionListener {
    
    /**
     * Called when an exercise is clicked
     * @param exercise The clicked exercise
     */
    void onExerciseClick(Exercise exercise);
    
    /**
     * Called when edit action is triggered
     * @param exercise The exercise to edit
     * @param position Position in the list
     */
    void onEditExercise(Exercise exercise, int position);
    
    /**
     * Called when delete action is triggered
     * @param exercise The exercise to delete
     * @param position Position in the list
     */
    void onDeleteExercise(Exercise exercise, int position);
    
    /**
     * Called when exercise completion status changes
     * @param exercise The exercise
     * @param isCompleted New completion status
     */
    void onExerciseCompletionChanged(Exercise exercise, boolean isCompleted);
}
