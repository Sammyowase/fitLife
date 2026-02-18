package com.fitlife.app.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.fitlife.app.R;
import com.fitlife.app.database.entities.Exercise;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Dialog helper for adding/editing exercises
 */
public class ExerciseDialogHelper {
    
    /**
     * Interface for exercise dialog callbacks
     */
    public interface OnExerciseDialogListener {
        void onExerciseSaved(Exercise exercise);
    }
    
    /**
     * Show add exercise dialog
     */
    public static void showAddExerciseDialog(Context context, int workoutId, OnExerciseDialogListener listener) {
        showExerciseDialog(context, null, workoutId, listener);
    }
    
    /**
     * Show edit exercise dialog
     */
    public static void showEditExerciseDialog(Context context, Exercise exercise, OnExerciseDialogListener listener) {
        showExerciseDialog(context, exercise, exercise.getWorkoutId(), listener);
    }
    
    /**
     * Show exercise dialog (add or edit)
     */
    private static void showExerciseDialog(Context context, Exercise existingExercise, int workoutId, OnExerciseDialogListener listener) {
        // Inflate custom dialog layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_exercise, null);
        
        // Get views
        TextInputLayout tilExerciseName = dialogView.findViewById(R.id.til_exercise_name);
        TextInputLayout tilSets = dialogView.findViewById(R.id.til_sets);
        TextInputLayout tilReps = dialogView.findViewById(R.id.til_reps);
        TextInputLayout tilInstructions = dialogView.findViewById(R.id.til_instructions);
        
        TextInputEditText etExerciseName = dialogView.findViewById(R.id.et_exercise_name);
        TextInputEditText etSets = dialogView.findViewById(R.id.et_sets);
        TextInputEditText etReps = dialogView.findViewById(R.id.et_reps);
        TextInputEditText etInstructions = dialogView.findViewById(R.id.et_instructions);
        
        // Pre-fill if editing
        if (existingExercise != null) {
            etExerciseName.setText(existingExercise.getExerciseName());
            etSets.setText(String.valueOf(existingExercise.getSets()));
            etReps.setText(String.valueOf(existingExercise.getReps()));
            etInstructions.setText(existingExercise.getInstructions());
        }
        
        // Create dialog
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context)
            .setTitle(existingExercise == null ? R.string.add_exercise : R.string.edit_exercise)
            .setView(dialogView)
            .setPositiveButton(R.string.save, null)
            .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        
        Dialog dialog = builder.create();
        dialog.show();
        
        // Override positive button to validate before dismissing
        ((androidx.appcompat.app.AlertDialog) dialog).getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            // Clear errors
            tilExerciseName.setError(null);
            tilSets.setError(null);
            tilReps.setError(null);
            
            String exerciseName = etExerciseName.getText().toString().trim();
            String setsStr = etSets.getText().toString().trim();
            String repsStr = etReps.getText().toString().trim();
            String instructions = etInstructions.getText().toString().trim();
            
            // Validate
            if (!ValidationHelper.isValidExerciseName(exerciseName)) {
                tilExerciseName.setError(context.getString(R.string.error_empty_field));
                return;
            }
            
            if (!ValidationHelper.isValidSetsReps(setsStr)) {
                tilSets.setError(context.getString(R.string.error_invalid_number));
                return;
            }
            
            if (!ValidationHelper.isValidSetsReps(repsStr)) {
                tilReps.setError(context.getString(R.string.error_invalid_number));
                return;
            }
            
            int sets = Integer.parseInt(setsStr);
            int reps = Integer.parseInt(repsStr);
            
            // Create or update exercise
            Exercise exercise;
            if (existingExercise != null) {
                exercise = existingExercise;
                exercise.setExerciseName(exerciseName);
                exercise.setSets(sets);
                exercise.setReps(reps);
                exercise.setInstructions(instructions);
            } else {
                exercise = new Exercise(workoutId, exerciseName, sets, reps);
                exercise.setInstructions(instructions);
            }
            
            // Callback
            if (listener != null) {
                listener.onExerciseSaved(exercise);
            }
            
            dialog.dismiss();
        });
    }
}
