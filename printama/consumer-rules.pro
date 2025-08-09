# Printama Library - Consumer ProGuard Rules
# These rules will be automatically applied to apps that use this library

# Keep all public API classes and methods
-keep public class com.anggastudio.printama.Printama {
    public *;
}

-keep public class com.anggastudio.printama.PrintamaUI {
    public *;
}

# Keep all callback interfaces
-keep interface com.anggastudio.printama.Printama$OnConnected {
    *;
}
-keep interface com.anggastudio.printama.Printama$OnFailed {
    *;
}
-keep interface com.anggastudio.printama.Printama$OnConnectPrinter {
    *;
}
-keep interface com.anggastudio.printama.Printama$OnChoosePrinterWidth {
    *;
}
-keep interface com.anggastudio.printama.Printama$Callback {
    *;
}

# Keep constants classes
-keep class com.anggastudio.printama.constants.PA {
    public static final *;
}
-keep class com.anggastudio.printama.constants.PW {
    public static final *;
}

# Keep UI Activity classes (they might be started via Intent)
-keep class com.anggastudio.printama.ui.ChoosePrinterActivity {
    *;
}

# Keep utility classes that might be used via reflection
-keep class com.anggastudio.printama.util.StrUtil {
    public static *;
}

# Keep Bluetooth related classes and methods
-keep class * extends android.bluetooth.BluetoothDevice {
    *;
}

# Keep classes that might be used in serialization
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Preserve line numbers for debugging
-keepattributes SourceFile,LineNumberTable

# Keep generic signatures
-keepattributes Signature

# Keep annotations
-keepattributes *Annotation*