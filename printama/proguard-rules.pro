# Printama Library - Internal ProGuard Rules
# These rules apply only when building the library itself

# Keep all public API - essential for library
-keep public class com.anggastudio.printama.** {
    public *;
    protected *;
}

# Keep internal classes that are accessed via reflection or JNI
-keep class com.anggastudio.printama.PrinterUtil {
    *;
}

-keep class com.anggastudio.printama.Pref {
    *;
}

# Keep adapter classes
-keep class com.anggastudio.printama.ui.DeviceListAdapter {
    *;
}

# Keep fragment classes
-keep class com.anggastudio.printama.ui.** extends androidx.fragment.app.Fragment {
    *;
}

# Keep classes with native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep classes that are used in AndroidManifest.xml
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# Keep Bluetooth related functionality
-keep class * extends android.bluetooth.** {
    *;
}

# Keep AsyncTask classes
-keep class * extends android.os.AsyncTask {
    *;
}

# Preserve all annotations
-keepattributes *Annotation*

# Preserve generic signatures
-keepattributes Signature

# Preserve line numbers for debugging
-keepattributes SourceFile,LineNumberTable

# Keep inner classes
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Don't warn about missing classes (common in Android libraries)
-dontwarn java.lang.invoke.**
-dontwarn javax.annotation.**

# Optimize but don't over-optimize
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontpreverify

# Keep custom exceptions
-keep public class * extends java.lang.Exception

# Keep parcelable classes
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep classes with @Keep annotation
-keep @androidx.annotation.Keep class *
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}