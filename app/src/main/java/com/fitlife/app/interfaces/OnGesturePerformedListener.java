package com.fitlife.app.interfaces;

/**
 * Interface for handling gesture events
 */
public interface OnGesturePerformedListener {
    
    /**
     * Called when swipe right gesture is performed
     * @param position Position of the item
     */
    void onSwipeRight(int position);
    
    /**
     * Called when swipe left gesture is performed
     * @param position Position of the item
     */
    void onSwipeLeft(int position);
    
    /**
     * Called when shake gesture is detected
     */
    void onShake();
    
    /**
     * Called when long press gesture is performed
     * @param position Position of the item
     */
    void onLongPress(int position);
    
    /**
     * Called when double tap gesture is performed
     * @param position Position of the item
     */
    void onDoubleTap(int position);
}
