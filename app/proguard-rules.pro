# ================================
# PRINTAMA SAMPLE APP PROGUARD RULES
# ================================

# Keep line numbers for crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep all annotations
-keepattributes *Annotation*

# ================================
# ANDROID FRAMEWORK
# ================================

# Keep all Activities, Services, Receivers, Providers
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.Application
-keep public class * extends android.preference.Preference

# Keep Activity lifecycle methods
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
    public void onCreate(android.os.Bundle);
    public void onStart();
    public void onResume();
    public void onPause();
    public void onStop();
    public void onDestroy();
}

# Keep Fragment lifecycle methods
-keep public class * extends androidx.fragment.app.Fragment
-keepclassmembers class * extends androidx.fragment.app.Fragment {
    public void onCreate(android.os.Bundle);
    public android.view.View onCreateView(...);
    public void onViewCreated(android.view.View, android.os.Bundle);
    public void onStart();
    public void onResume();
    public void onPause();
    public void onStop();
    public void onDestroyView();
    public void onDestroy();
}

# ================================
# SAMPLE APP SPECIFIC
# ================================

# Keep all sample app activities
-keep class com.anggastudio.printama_sample.** { *; }

# Keep menu click handlers
-keepclassmembers class * {
    public boolean onOptionsItemSelected(android.view.MenuItem);
    public void onBackPressed();
}

# Keep View click handlers
-keepclassmembers class * {
    public void onClick(android.view.View);
    public boolean onLongClick(android.view.View);
    public void onItemClick(...);
    public boolean onItemLongClick(...);
}

# ================================
# PRINTAMA LIBRARY
# ================================

# Keep all Printama public APIs
-keep class com.anggastudio.printama.Printama { *; }
-keep class com.anggastudio.printama.PrintamaUI { *; }
-keep class com.anggastudio.printama.constants.** { *; }

# Keep Printama callbacks and interfaces
-keep interface com.anggastudio.printama.** { *; }

# ================================
# THIRD-PARTY LIBRARIES
# ================================

# QRGen Library
-keep class net.glxn.qrgen.** { *; }
-keep class com.google.zxing.** { *; }
-dontwarn com.google.zxing.**

# AndroidX
-keep class androidx.** { *; }
-dontwarn androidx.**

# Material Design
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**

# ================================
# SERIALIZATION & REFLECTION
# ================================

# Keep Serializable classes
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep Parcelable classes
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# ================================
# DEBUGGING & LOGGING
# ================================

# Remove Log calls in release
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# ================================
# OPTIMIZATION
# ================================

# Enable aggressive optimizations
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontpreverify