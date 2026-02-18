package com.fitlife.app.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fitlife.app.R;
import com.fitlife.app.database.entities.Workout;
import com.fitlife.app.interfaces.OnWorkoutClickListener;
import com.fitlife.app.utils.ImageHelper;

import java.util.List;

/**
 * Adapter for displaying workouts in RecyclerView
 */
public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {
    
    private final Context context;
    private final List<Workout> workouts;
    private final OnWorkoutClickListener listener;
    
    public WorkoutAdapter(Context context, List<Workout> workouts, OnWorkoutClickListener listener) {
        this.context = context;
        this.workouts = workouts;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_workout, parent, false);
        return new WorkoutViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        Workout workout = workouts.get(position);
        holder.bind(workout);
    }
    
    @Override
    public int getItemCount() {
        return workouts.size();
    }
    
    /**
     * Get workout at position
     */
    public Workout getWorkoutAt(int position) {
        return workouts.get(position);
    }
    
    /**
     * Remove workout at position
     */
    public void removeWorkout(int position) {
        workouts.remove(position);
        notifyItemRemoved(position);
    }
    
    /**
     * Update workout at position
     */
    public void updateWorkout(int position, Workout workout) {
        workouts.set(position, workout);
        notifyItemChanged(position);
    }
    
    /**
     * ViewHolder for workout items
     */
    public class WorkoutViewHolder extends RecyclerView.ViewHolder {
        
        private final ImageView ivWorkoutImage;
        private final TextView tvWorkoutName;
        private final TextView tvExerciseCount;
        private final TextView tvCompletionStatus;
        private final ImageView ivMore;
        private final View viewCompletionIndicator;
        private final GestureDetector gestureDetector;
        
        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            
            ivWorkoutImage = itemView.findViewById(R.id.iv_workout_image);
            tvWorkoutName = itemView.findViewById(R.id.tv_workout_name);
            tvExerciseCount = itemView.findViewById(R.id.tv_exercise_count);
            tvCompletionStatus = itemView.findViewById(R.id.tv_completion_status);
            ivMore = itemView.findViewById(R.id.iv_more);
            viewCompletionIndicator = itemView.findViewById(R.id.view_completion_indicator);
            
            // Initialize Gesture Detector
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onWorkoutClick(workouts.get(position));
                    }
                    return true;
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onWorkoutDoubleTap(workouts.get(position));
                    }
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onWorkoutLongClick(workouts.get(position), itemView);
                    }
                }
                
                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }
            });
            
            // Set Touch Listener to intercept events
            itemView.setOnTouchListener((v, event) -> {
                if (gestureDetector.onTouchEvent(event)) {
                    v.performClick(); // For accessibility
                    return true;
                }
                return false;
            });
        }
        
        public void bind(Workout workout) {
            // Set workout name
            tvWorkoutName.setText(workout.getWorkoutName());
            
            // Set exercise count (placeholder - will be updated with actual count)
            tvExerciseCount.setText(context.getString(R.string.exercises_count, 0));
            
            // Set completion status
            if (workout.isCompleted()) {
                tvCompletionStatus.setText(R.string.completed);
                tvCompletionStatus.setTextColor(context.getColor(R.color.success));
                viewCompletionIndicator.setVisibility(View.VISIBLE);
            } else {
                tvCompletionStatus.setText(R.string.in_progress);
                tvCompletionStatus.setTextColor(context.getColor(R.color.text_secondary));
                viewCompletionIndicator.setVisibility(View.GONE);
            }
            
            // Load workout image
            if (workout.getImagePath() != null) {
                Bitmap bitmap = ImageHelper.loadImageFromPath(workout.getImagePath());
                if (bitmap != null) {
                    ivWorkoutImage.setImageBitmap(bitmap);
                } else {
                    ivWorkoutImage.setImageResource(R.drawable.ic_launcher_placeholder);
                }
            } else {
                ivWorkoutImage.setImageResource(R.drawable.ic_launcher_placeholder);
            }
            
            // The item click listener is handled by GestureDetector, so we don't set itemView.setOnClickListener
            // We just need the more button listener
            
            ivMore.setOnClickListener(v -> {
                showOptionsMenu(workout);
            });
        }
        
        /**
         * Show options menu for workout
         */
        private void showOptionsMenu(Workout workout) {
            android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(context, ivMore);
            popupMenu.inflate(R.menu.menu_workout_context);
            
            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                
                if (id == R.id.action_edit) {
                    if (listener != null) {
                        listener.onEditWorkout(workout);
                    }
                    return true;
                } else if (id == R.id.action_share) {
                    if (listener != null) {
                        listener.onShareWorkout(workout);
                    }
                    return true;
                } else if (id == R.id.action_delete) {
                    if (listener != null) {
                        listener.onDeleteWorkout(workout);
                    }
                    return true;
                } else if (id == R.id.action_mark_complete) {
                    if (listener != null) {
                        listener.onWorkoutCompletionChanged(workout, !workout.isCompleted());
                    }
                    return true;
                }
                
                return false;
            });
            
            popupMenu.show();
        }
    }
}
