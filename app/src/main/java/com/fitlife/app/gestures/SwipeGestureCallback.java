package com.fitlife.app.gestures;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.fitlife.app.R;

/**
 * Swipe Gesture Callback for RecyclerView
 * Handles swipe left (delete) and swipe right (mark complete) gestures
 */
public abstract class SwipeGestureCallback extends ItemTouchHelper.SimpleCallback {
    
    private final Context context;
    private final Paint paint;
    
    public SwipeGestureCallback(Context context) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.context = context;
        this.paint = new Paint();
    }
    
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                         @NonNull RecyclerView.ViewHolder viewHolder,
                         @NonNull RecyclerView.ViewHolder target) {
        return false; // We don't support drag-and-drop
    }
    
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        
        if (direction == ItemTouchHelper.RIGHT) {
            // Swipe right - mark as complete
            onSwipeRight(position);
        } else if (direction == ItemTouchHelper.LEFT) {
            // Swipe left - delete
            onSwipeLeft(position);
        }
    }
    
    @Override
    public void onChildDraw(@NonNull Canvas c,
                           @NonNull RecyclerView recyclerView,
                           @NonNull RecyclerView.ViewHolder viewHolder,
                           float dX, float dY,
                           int actionState,
                           boolean isCurrentlyActive) {
        
        View itemView = viewHolder.itemView;
        
        if (dX > 0) {
            // Swipe right - draw green background with checkmark
            drawCompleteBackground(c, itemView, dX);
        } else if (dX < 0) {
            // Swipe left - draw red background with delete icon
            drawDeleteBackground(c, itemView, dX);
        }
        
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
    
    /**
     * Draw green background for complete gesture
     */
    private void drawCompleteBackground(Canvas c, View itemView, float dX) {
        paint.setColor(ContextCompat.getColor(context, R.color.gesture_complete));
        
        RectF background = new RectF(
            itemView.getLeft(),
            itemView.getTop(),
            itemView.getLeft() + dX,
            itemView.getBottom()
        );
        c.drawRect(background, paint);
        
        // Draw checkmark icon
        Drawable icon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_save);
        if (icon != null) {
            int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconTop = itemView.getTop() + iconMargin;
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = iconLeft + icon.getIntrinsicWidth();
            int iconBottom = iconTop + icon.getIntrinsicHeight();
            
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            icon.setTint(ContextCompat.getColor(context, R.color.white));
            icon.draw(c);
        }
    }
    
    /**
     * Draw red background for delete gesture
     */
    private void drawDeleteBackground(Canvas c, View itemView, float dX) {
        paint.setColor(ContextCompat.getColor(context, R.color.gesture_delete));
        
        RectF background = new RectF(
            itemView.getRight() + dX,
            itemView.getTop(),
            itemView.getRight(),
            itemView.getBottom()
        );
        c.drawRect(background, paint);
        
        // Draw delete icon
        Drawable icon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_delete);
        if (icon != null) {
            int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconTop = itemView.getTop() + iconMargin;
            int iconRight = itemView.getRight() - iconMargin;
            int iconLeft = iconRight - icon.getIntrinsicWidth();
            int iconBottom = iconTop + icon.getIntrinsicHeight();
            
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            icon.setTint(ContextCompat.getColor(context, R.color.white));
            icon.draw(c);
        }
    }
    
    /**
     * Called when user swipes right (mark complete)
     * @param position Position of the item
     */
    public abstract void onSwipeRight(int position);
    
    /**
     * Called when user swipes left (delete)
     * @param position Position of the item
     */
    public abstract void onSwipeLeft(int position);
}
