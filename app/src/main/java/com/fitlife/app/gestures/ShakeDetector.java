package com.fitlife.app.gestures;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.fitlife.app.utils.Constants;

/**
 * Shake Detector using accelerometer sensor
 * Detects when the device is shaken
 */
public class ShakeDetector implements SensorEventListener {
    
    private OnShakeListener listener;
    private long lastShakeTime = 0;
    
    /**
     * Interface for shake detection callback
     */
    public interface OnShakeListener {
        void onShake();
    }
    
    /**
     * Constructor
     * @param listener Callback listener for shake events
     */
    public ShakeDetector(OnShakeListener listener) {
        this.listener = listener;
    }
    
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            
            // Calculate acceleration magnitude (excluding gravity)
            float acceleration = (float) Math.sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH;
            
            long currentTime = System.currentTimeMillis();
            
            // Check if acceleration exceeds threshold
            if (acceleration > Constants.SHAKE_THRESHOLD) {
                // Prevent multiple shake detections in quick succession
                if (currentTime - lastShakeTime > Constants.SHAKE_TIMEOUT_MS) {
                    lastShakeTime = currentTime;
                    if (listener != null) {
                        listener.onShake();
                    }
                }
            }
        }
    }
    
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for this implementation
    }
    
    /**
     * Set the shake listener
     * @param listener OnShakeListener callback
     */
    public void setOnShakeListener(OnShakeListener listener) {
        this.listener = listener;
    }
}
