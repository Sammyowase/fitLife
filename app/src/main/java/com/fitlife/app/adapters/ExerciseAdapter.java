package com.fitlife.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fitlife.app.R;
import com.fitlife.app.database.entities.Exercise;
import com.fitlife.app.interfaces.OnExerciseActionListener;

import java.util.List;

/**
 * Adapter for displaying exercises in RecyclerView
 */
public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {
    
    private final Context context;
    private final List<Exercise> exercises;
    private final OnExerciseActionListener listener;
    
    public ExerciseAdapter(Context context, List<Exercise> exercises, OnExerciseActionListener listener) {
        this.context = context;
        this.exercises = exercises;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.bind(exercise, position);
    }
    
    @Override
    public int getItemCount() {
        return exercises.size();
    }
    
    /**
     * Get exercise at position
     */
    public Exercise getExerciseAt(int position) {
        return exercises.get(position);
    }
    
    /**
     * Remove exercise at position
     */
    public void removeExercise(int position) {
        exercises.remove(position);
        notifyItemRemoved(position);
    }
    
    /**
     * Add exercise
     */
    public void addExercise(Exercise exercise) {
        exercises.add(exercise);
        notifyItemInserted(exercises.size() - 1);
    }
    
    /**
     * Update exercise at position
     */
    public void updateExercise(int position, Exercise exercise) {
        exercises.set(position, exercise);
        notifyItemChanged(position);
    }
    
    /**
     * Get all exercises
     */
    public List<Exercise> getExercises() {
        return exercises;
    }
    
    /**
     * ViewHolder for exercise items
     */
    class ExerciseViewHolder extends RecyclerView.ViewHolder {
        
        private final CheckBox cbCompleted;
        private final TextView tvExerciseName;
        private final TextView tvSetsReps;
        private final TextView tvEquipment;
        private final ImageView ivMore;
        
        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            
            cbCompleted = itemView.findViewById(R.id.cb_completed);
            tvExerciseName = itemView.findViewById(R.id.tv_exercise_name);
            tvSetsReps = itemView.findViewById(R.id.tv_sets_reps);
            tvEquipment = itemView.findViewById(R.id.tv_equipment);
            ivMore = itemView.findViewById(R.id.iv_more);
        }
        
        public void bind(Exercise exercise, int position) {
            // Set exercise name
            tvExerciseName.setText(exercise.getExerciseName());
            
            // Set sets and reps
            String setsReps = exercise.getSets() + " sets × " + exercise.getReps() + " reps";
            tvSetsReps.setText(setsReps);
            
            // Set equipment (placeholder - will be loaded from database)
            tvEquipment.setText(R.string.no_equipment);
            tvEquipment.setVisibility(View.GONE);
            
            // Set completion status
            cbCompleted.setChecked(exercise.isCompleted());
            
            // Apply strikethrough if completed
            if (exercise.isCompleted()) {
                tvExerciseName.setPaintFlags(tvExerciseName.getPaintFlags() | 
                    android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                tvSetsReps.setAlpha(0.6f);
            } else {
                tvExerciseName.setPaintFlags(tvExerciseName.getPaintFlags() & 
                    ~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                tvSetsReps.setAlpha(1.0f);
            }
            
            // Set listeners
            cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onExerciseCompletionChanged(exercise, isChecked);
                }
            });
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onExerciseClick(exercise);
                }
            });
            
            ivMore.setOnClickListener(v -> {
                showOptionsMenu(exercise, position);
            });
        }
        
        /**
         * Show options menu for exercise
         */
        private void showOptionsMenu(Exercise exercise, int position) {
            android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(context, ivMore);
            popupMenu.getMenu().add(0, 1, 0, R.string.edit);
            popupMenu.getMenu().add(0, 2, 0, R.string.delete);
            
            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                
                if (id == 1) {
                    if (listener != null) {
                        listener.onEditExercise(exercise, position);
                    }
                    return true;
                } else if (id == 2) {
                    if (listener != null) {
                        listener.onDeleteExercise(exercise, position);
                    }
                    return true;
                }
                
                return false;
            });
            
            popupMenu.show();
        }
    }
}
