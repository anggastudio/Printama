<div style="background-color: maroon; border-left: 12px solid #ffa500; padding: 10px; border-radius: 8px;">
    <p><strong>🚀 Latest:</strong> Version 1.0.0 is now available with enhanced stability, new constant classes, and comprehensive API improvements!</p>
    <p><strong>✅ Recommended:</strong> Use 1.0.0 for the best experience across all Android versions.</p>
    <p><strong>✅ Recommended:</strong> Now support 3 Inches printer.</p>
    <p><strong>📱 Compatibility:</strong> Supports Android 13+ with optimized Bluetooth permissions.</p>
    <p><strong>⚠️ Deprecated:</strong> Versions 0.9.x are now legacy. Please migrate to 1.0.0.</p>
</div>

<p align="center">
  <h1 align="center">Printama</h1>
  <h4 align="center">🖨️ Professional Android library for Bluetooth thermal printing<br>✅ Extensively tested with 2-inch and 3-inch thermal printers<br>🎯 Built for developers who need reliable printing solutions</h4>
</p>

<p align="center">
  <img src="https://images.unsplash.com/photo-1598346762291-aee88549193f?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80"/>
</p>

<p align="center">
  <a href="https://jitpack.io/#anggastudio/Printama"><img src="https://jitpack.io/v/anggastudio/Printama.svg"></a>
  <a href="LICENSE"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"></a>
  <a href="https://github.com/anggastudio/Printama/pulls"><img alt="Pull request" src="https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat"></a>
  <a href="https://github.com/anggastudio/Printama/graphs/contributors"><img src="https://img.shields.io/github/contributors/anggastudio/Printama"></a>
  <a href="https://github.com/anggastudio"><img alt="Github" src="https://img.shields.io/github/followers/anggastudio?label=follow&style=social"></a>
</p>

## Screenshots

|Payment Receipt|Print Text and Images|
|---|---|
|![](images/struk_belanja.jpeg)|![](images/print_text_image.jpeg)|

|Photo|Photo Print Result|
|---|---|
|![](images/rose.jpeg)|![](images/rose_print.jpeg)|

|Screen Layout|Screen Layout Print Result|
|---|---|
|![](images/layout.jpeg)|![](images/print_layout.jpeg)|


## 💝 Support the Project

Printama saves developers countless hours with reliable printing solutions. Your support helps us expand compatibility and maintain this free resource.

### 🚀 How Your Support Helps
- **Hardware Acquisition:** Purchase various printer models for testing
- **Continuous Testing:** Ensure compatibility across different brands
- **Development Time:** Maintain and improve the library
- **Documentation:** Create better guides and tutorials

### ☕ Contribute Via

|Platform|Link|Features|
|---|---|---|
|**PayPal**|[Donate via PayPal](https://paypal.me/anggastudio)|Secure, worldwide|
|**Ko-fi**|[Support on Ko-fi](https://ko-fi.com/anggastudio)|Coffee-themed, easy|
|**Buy Me a Coffee**|[Buy Me a Coffee](https://www.buymeacoffee.com/anggastudio)|Popular, anonymous option|
|**Trakteer**|[Trakteer (ID)](https://trakteer.id/anggastudio)|Indonesian platform|
|**Saweria**|[Saweria (ID)](https://saweria.co/anggastudio)|Indonesian platform|
|**USDT (BEP20)**|[Pay via Trust Wallet](https://link.trustwallet.com/send?coin=20000714&address=0x7A65cc9d8031f67847662cC92Fa93b71dCc95605&token_id=0x55d398326f99059fF775485246999027B3197955)|Crypto, decentralized|

#### 💰 USDT Donation Details
**Network:** BEP20 (Binance Smart Chain)  
**Address:**
```
0x7A65cc9d8031f67847662cC92Fa93b71dCc95605
```

- **With Trust Wallet:** Click the USDT link above to open Trust Wallet directly
- **Manual Transfer:** Copy the address above and send USDT via any BEP20-compatible wallet

> 💡 **Tip:** The library includes a beautiful donation screen in the sample app!


## Quick Start

**Requirements:**
- Android SDK 16+
- Java 8+ configuration
- Bluetooth thermal printer (2-inch or 3-inch)

**Latest Features in 1.0.0:**
- 🆕 New constant classes (PA, PW) for better organization
- 🔧 Improved method parameter order for consistency
- 📱 Enhanced Android 13+ compatibility
- 🎨 Advanced column formatting (2-5 columns)
- 🛡️ Comprehensive stability improvements
- 📚 Complete API documentation
- 🔧 Improved text alignment and spacing
- 🎨 Better receipt layout capabilities

### Basic Setup
**Permissions in your Manifest**

For Android 12 (API 31) and higher:
```xml
<!-- Bluetooth permissions for Android 12+ -->
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" 
    android:usesPermissionFlags="neverForLocation" 
    tools:targetApi="31" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />

<!-- Legacy Bluetooth permissions for older devices -->
<uses-permission android:name="android.permission.BLUETOOTH" 
    android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" 
    android:maxSdkVersion="30" />

<!-- Location permission for Bluetooth scanning on older devices -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" 
    android:maxSdkVersion="30" />

<!-- Hardware features (optional) -->
<uses-feature android:name="android.hardware.bluetooth" 
    android:required="false" />
<uses-feature android:name="android.hardware.bluetooth_le" 
    android:required="false" />
```

**Permission handling in your Activity**

Add this permission request constant:
```java
private final int PERMISSION_REQUEST_BLUETOOTH_CONNECT = 432;
```

**Check and request permissions in your Activity:**
```java
private void checkBluetoothPermission() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
        // Permission granted - proceed with printer operations
        connectToPrinter();
    } else {
        // Request permissions
        requestBluetoothPermission();
    }
}

private void requestBluetoothPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        // Android 12+ - Request new Bluetooth permissions
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                },
                PERMISSION_REQUEST_BLUETOOTH_CONNECT);
    } else {
        // Older Android versions - Request Bluetooth enable
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, PERMISSION_REQUEST_BLUETOOTH_CONNECT);
    }
}

@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    
    if (requestCode == PERMISSION_REQUEST_BLUETOOTH_CONNECT) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted
            connectToPrinter();
        } else {
            // Permission denied
            Toast.makeText(this, "Bluetooth permission is required for printing", Toast.LENGTH_LONG).show();
        }
    }
}
```

**Call permission check in your onCreate() method:**
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    // Check permissions when activity starts
    checkBluetoothPermission();
}
```

**Show dialog to choose bonded device (printer list)** bind your device initially from the bluetooth config:

```java
Printama.showPrinterList(this, printerName -> {
    // Your code here
});
```

**Show dialog to choose bonded device (Custom Color)**

```java
Printama.showPrinterList(this, R.color.colorBlue, printerName -> {
    if (connectedPrinter != null) {
        // Your code here
    }
});
```

**Prepare the text**

```java
String text = "-------------\n" +
        "This will be printed\n" +
        "Left aligned\n" + // or Center or Right
        "cool isn't it?\n" +
        "------------------\n";
```

**Print Text LEFT aligned**

```java
Printama.with(context).connect(printama -> {
    printama.printText(text, PA.LEFT);
    printama.close();
});
```

**Print Text CENTER aligned**

```java
Printama.with(context).connect(printama -> {
    printama.printText(text, PA.CENTER);
    printama.close();
});
```

**Print Text RIGHT aligned**

```java
Printama.with(context).connect(printama -> {
    printama.printText(text, PA.RIGHT);
    printama.close();
});
```

**Print Text JUSTIFY aligned**

```java
Printama.with(this).connect(printama -> {
    printama.printTextJustify("text1", "text2");
    printama.printTextJustify("text1", "text2", "text3");
    printama.printTextJustify("text1", "text2", "text3", "text4");

    printama.printTextJustifyBold("text1", "text2");
    printama.printTextJustifyBold("text1", "text2", "text3");
    printama.printTextJustifyBold("text1", "text2", "text3", "text4");

    printama.setNormalText();
    printama.feedPaper();
    printama.close();
});
```

**🆕 Advanced Column Formatting (New in 0.9.80)**

Printama now includes powerful column formatting methods that automatically handle width calculation and text alignment:

```java
Printama.with(this).connect(printama -> {
    // Two columns with default widths (70% - 30%)
    printama.printTextln(printama.formatTwoColumns("Product", "Price"), PA.LEFT);
    printama.printTextln(printama.formatTwoColumns("Coffee", "$3.50"), PA.LEFT);
    
    // Three columns with default widths (50% - 20% - 30%)
    printama.printTextln(printama.formatThreeColumns("Item", "Qty", "Total"), PA.LEFT);
    printama.printTextln(printama.formatThreeColumns("Espresso", "2", "$7.00"), PA.LEFT);
    
    // Four columns with default widths (40% - 20% - 20% - 20%)
    printama.printTextln(printama.formatFourColumns("ID", "Name", "Stock", "Price"), PA.LEFT);
    printama.printTextln(printama.formatFourColumns("001", "Coffee", "50", "$3.50"), PA.LEFT);
    
    // Five columns with default widths (30% - 20% - 20% - 15% - 15%)
    printama.printTextln(printama.formatFiveColumns("ID", "Item", "Cat", "Qty", "$"), PA.LEFT);
    
    // Custom column widths (percentages must sum to 100)
    double[] customWidths = {60.0, 40.0}; // 60% - 40%
    printama.printTextln(printama.formatTwoColumns("Description", "Amount", customWidths), PA.LEFT);
    
    printama.close();
});
```

**Print Text with format**

```java
Printama.with(this).connect(printama -> {
    printama.setSmallText();
    printama.printText("small___________");
    printama.printTextln("TEXTtext");

    printama.setNormalText();
    printama.printText("normal__________");
    printama.printTextln("TEXTtext");

    printama.printTextNormal("bold____________");
    printama.printTextlnBold("TEXTtext");

    printama.setNormalText();
    printama.printTextNormal("tall____________");
    printama.printTextlnTall("TEXTtext");

    printama.printTextNormal("tall bold_______");
    printama.printTextlnTallBold("TEXTtext");

    printama.printTextNormal("wide____________");
    printama.printTextlnWide("TEXTtext");

    printama.printTextNormal("wide bold_______");
    printama.printTextlnWideBold("TEXTtext");

    printama.printTextNormal("wide tall_______");
    printama.printTextlnWideTall("TEXTtext");

    printama.printTextNormal("wide tall bold__");
    printama.printTextlnWideTallBold("TEXTtext");

    printama.printTextNormal("underline_______");
    printama.setUnderline();
    printama.printTextln("TEXTtext");

    printama.printTextNormal("delete line_____");
    printama.setDeleteLine();
    printama.printTextln("TEXTtext");

    printama.setNormalText();
    printama.feedPaper();
    printama.close();
});
```

**Print Bitmap / Image LEFT aligned**

```java
Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
Printama.with(context).connect(printama -> {
    printama.printImage(bitmap, 200, PA.LEFT);
    printama.close();
});
```

**Print Bitmap / Image CENTER aligned**

```java
Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
Printama.with(context).connect(printama -> {
    printama.printImage(bitmap, 200, PA.CENTER);
    printama.close();
});
```

**Print Bitmap / Image RIGHT aligned**

```java
Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
Printama.with(context).connect(printama -> {
    printama.printImage(bitmap, 200, PA.RIGHT);
    printama.close();
});
```

**Print Bitmap / Image FULL size**

```java
Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
Printama.with(context).connect(printama -> {
    printama.printImage(bitmap, PW.FULL_WIDTH);
    printama.close();
});
```

**Print Bitmap / Image ORIGINAL size**

```java
Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
Printama.with(context).connect(printama -> {
    printama.printImage(bitmap); // original size, centered as default
    printama.close();
});
```

**Print Drawable Vector**

```java
Bitmap bitmap = Printama.getBitmapFromVector(this, R.drawable.ic_launcher_background);
Printama.with(this).connect(printama -> {
    printama.printImage(bitmap, PW.ORIGINAL_WIDTH);
    printama.close();
});
```

**Print Layout View**

Using print layout view, you can design your receipt on your layout XML or DSL, and pass the root view as a parameter:

```java
View view = findViewById(R.id.root_view);
Printama.with(this).connect(printama -> {
    printama.printFromView(view);
    printama.close();
});
```

## Features

### 🔧 Core Printing Capabilities
* **Text Alignment:** LEFT, CENTER, RIGHT, and JUSTIFY alignment options
* **Image Printing:** Auto-grayscale bitmap printing with custom width and alignment
* **Photo Printing:** High-quality grayscaled photo output
* **Layout Printing:** Print Android views and layouts directly
* **Vector Support:** Print vector drawables with perfect scaling

### 🆕 Advanced Formatting (v0.9.80)
* **Column Formatting:** Smart 2-5 column layouts with automatic width calculation
* **Custom Widths:** Flexible column width percentages for precise control
* **Text Overflow:** Automatic text truncation to prevent layout breaks
* **Adaptive Sizing:** Auto-adjusts for 2-inch and 3-inch printer compatibility

### 📱 Device & Connectivity
* **Bluetooth Integration:** Seamless pairing with bonded device selection dialog
* **Custom UI:** Customizable printer selection dialog colors
* **Wide Compatibility:** Extensively tested with 2-inch and 3-inch thermal printers
* **Android 13+ Ready:** Optimized for latest Android versions with proper permissions

### 🎨 Text Styling Options
* **Font Sizes:** Small, normal, tall, and wide text variants
* **Text Effects:** Bold, underline, and strikethrough formatting
* **Line Spacing:** Customizable spacing for better receipt layout
* **Mixed Formatting:** Combine multiple text styles in single print job

## Contributing

You can simply :

* a pull request, or
* raise an issue ticket, or
* request additional feature by raise a ticket.

## Download

* Minimum Android SDK Version 16
* Always check [latest release](https://github.com/anggastudio/Printama/releases)

#### Gradle:

**Step 1.** Add it in your root build.gradle at the end of repositories:

```gradle
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

**Step 2.** Add the dependency

version: [![](https://jitpack.io/v/anggastudio/Printama.svg)](https://jitpack.io/#anggastudio/Printama)

```gradle
dependencies {
  ... 
  #groovy
  implementation 'com.github.anggastudio:Printama:<version>'
  #kts
  implementation("com.github.anggastudio:Printama:<version>")
  ...
}
```

#### Other like Maven, SBT, Leiningen:

**just visit the jitpack page** click here --> [![](https://jitpack.io/v/anggastudio/Printama.svg)](https://jitpack.io/#anggastudio/Printama)

## License

[Apache License 2.0](LICENSE)

## Thanks To:

- [imrankst1221](https://github.com/imrankst1221/Thermal-Printer-in-Android)
- [MFori](https://github.com/MFori/Android-Bluetooth-Printer)
- <a href="https://github.com/anggastudio/Printama/graphs/contributors"><img src="https://img.shields.io/github/contributors/anggastudio/Printama"></a>
- WP

## Roadmap

### ✅ Version 1.0.0 (Released)
- ✅ **Stability Focus:** Comprehensive testing and bug fixes
- ✅ **Documentation:** Complete API documentation and guides
- ✅ **New Constants:** Introduced PA and PW classes for better organization
- ✅ **Method Improvements:** Consistent parameter order across all methods
- ✅ **Migration Guide:** Complete migration documentation from 0.9.x

### 🔮 Future Enhancements
- **Multi-Brand Testing:** Expanded compatibility testing across printer manufacturers
- **Advanced Layouts:** Template-based receipt designs

### 📈 Recent Achievements (v1.0.0)
- ✅ New constant classes (PA, PW) for better code organization
- ✅ Consistent method parameter order
- ✅ Advanced column formatting system
- ✅ Improved Android 13+ compatibility
- ✅ Enhanced text alignment and spacing
- ✅ Comprehensive stability improvements
- ✅ Complete migration guide and documentation

## 🔄 Migration Guide (v1.0.0)

**Important:** Version 1.0.0 introduces new constant classes for better organization. The old constants are deprecated and will be removed in v2.0.0.

### Alignment Constants Migration

| ❌ Deprecated (v0.9.x) | ✅ New (v1.0.0+) | Description |
|---|---|---|
| `Printama.LEFT` | `PA.LEFT` | Left text alignment |
| `Printama.CENTER` | `PA.CENTER` | Center text alignment |
| `Printama.RIGHT` | `PA.RIGHT` | Right text alignment |

### Width Constants Migration

| ❌ Deprecated (v0.9.x) | ✅ New (v1.0.0+) | Description |
|---|---|---|
| `Printama.ORIGINAL_WIDTH` | `PW.ORIGINAL_WIDTH` | Original image width |
| `Printama.FULL_WIDTH` | `PW.FULL_WIDTH` | Full printer width |

### Method Parameter Order Changes

**Text Methods:**
```java
// ❌ Old way (deprecated)
printText(PA.CENTER, "Hello World");
printTextln(PA.RIGHT, "Hello World");

// ✅ New way
printText("Hello World", PA.CENTER);
printTextln("Hello World", PA.RIGHT);
```

**Image Methods:**
```java
// ❌ Old way (deprecated)
printImage(PA.CENTER, bitmap, PW.FULL_WIDTH);

// ✅ New way
printImage(bitmap, PW.FULL_WIDTH, PA.CENTER);
```

### Import Required Classes

```java
import com.anggastudio.printama.Printama;
import com.anggastudio.printama.constants.PA; // For alignment constants
import com.anggastudio.printama.constants.PW; // For width constants
```

### Quick Migration Checklist

- [ ] Replace `Printama.LEFT/CENTER/RIGHT` with `PA.LEFT/CENTER/RIGHT`
- [ ] Replace `Printama.ORIGINAL_WIDTH/FULL_WIDTH` with `PW.ORIGINAL_WIDTH/FULL_WIDTH`
- [ ] Update text method calls: `printText(align, text)` → `printText(text, align)`
- [ ] Update image method calls: `printImage(align, bitmap, width)` → `printImage(bitmap, width, align)`
- [ ] Add import statements for `PA` and `PW` classes
- [ ] Test your implementation with the new API

> 💡 **Tip:** The deprecated methods will continue to work in v1.0.0 but will show compiler warnings. Plan to migrate before v2.0.0 release.


