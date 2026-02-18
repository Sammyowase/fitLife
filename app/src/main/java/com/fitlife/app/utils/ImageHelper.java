package com.fitlife.app.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Image Helper utility for handling image operations
 * Includes saving, loading, compressing, and rotating images
 */
public class ImageHelper {
    
    /**
     * Save bitmap to internal storage
     * @param context Application context
     * @param bitmap Bitmap to save
     * @param workoutId Workout ID for filename
     * @return Absolute path to saved image, or null if failed
     */
    public static String saveImageToInternalStorage(Context context, Bitmap bitmap, int workoutId) {
        // Create directory if it doesn't exist
        File directory = new File(context.getFilesDir(), Constants.IMAGE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        // Generate unique filename
        String filename = "workout_" + workoutId + "_" + System.currentTimeMillis() + ".jpg";
        File file = new File(directory, filename);
        
        try (FileOutputStream fos = new FileOutputStream(file)) {
            // Compress and save bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, Constants.IMAGE_COMPRESSION_QUALITY, fos);
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Load bitmap from file path
     * @param path Absolute path to image file
     * @return Bitmap or null if failed
     */
    public static Bitmap loadImageFromPath(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        
        return BitmapFactory.decodeFile(path);
    }
    
    /**
     * Delete image from storage
     * @param path Absolute path to image file
     * @return true if deleted successfully
     */
    public static boolean deleteImage(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        
        File file = new File(path);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }
    
    /**
     * Load and resize bitmap from URI
     * @param context Application context
     * @param uri Image URI
     * @return Resized bitmap or null if failed
     */
    public static Bitmap loadAndResizeBitmap(Context context, Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;
            
            // Decode with inJustDecodeBounds to get dimensions
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();
            
            // Calculate sample size
            options.inSampleSize = calculateInSampleSize(options, 
                Constants.MAX_IMAGE_WIDTH, Constants.MAX_IMAGE_HEIGHT);
            
            // Decode with sample size
            options.inJustDecodeBounds = false;
            inputStream = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();
            
            // Rotate if needed based on EXIF data
            return rotateImageIfRequired(context, bitmap, uri);
            
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Calculate sample size for bitmap loading
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, 
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        
        return inSampleSize;
    }
    
    /**
     * Rotate image based on EXIF orientation
     */
    private static Bitmap rotateImageIfRequired(Context context, Bitmap bitmap, Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) return bitmap;
            
            ExifInterface exif = new ExifInterface(inputStream);
            int orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            );
            inputStream.close();
            
            int rotation = 0;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotation = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotation = 270;
                    break;
            }
            
            if (rotation != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(rotation);
                return Bitmap.createBitmap(bitmap, 0, 0, 
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return bitmap;
    }
}
