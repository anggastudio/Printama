# ================================
# DEBUG-SPECIFIC PROGUARD RULES
# ================================

# Keep more debugging information
-keepattributes LocalVariableTable,LocalVariableTypeTable
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Don't remove Log calls in debug
-dontwarn android.util.Log

# Keep all class names for easier debugging
-keepnames class ** { *; }

# Keep method names for stack traces
-keepclassmembernames class * {
    java.lang.Class class$(java.lang.String);
    java.lang.Class class$(java.lang.String, boolean);
}

# Less aggressive optimization for debug
-dontoptimize
-dontobfuscate

# Keep test classes
-keep class **Test { *; }
-keep class **Tests { *; }
-keep class **.*Test { *; }
-keep class **.*Tests { *; }

# Keep debug utilities
-keep class **.debug.** { *; }
-keep class **.Debug** { *; }

# Verbose ProGuard output for debugging
-verbose