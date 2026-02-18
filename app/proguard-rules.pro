# ProGuard rules for FitLife

# Keep Room database classes
-keep class com.fitlife.app.database.** { *; }
-keep class com.fitlife.app.database.entities.** { *; }

# Keep model classes
-keep class com.fitlife.app.models.** { *; }

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
