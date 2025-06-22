<div style="background-color: maroon; border-left: 12px solid #ffa500; padding: 10px; border-radius: 8px;">
    <p><strong>🚀 Latest:</strong> Version 0.9.80 is now available with enhanced column formatting and improved stability!</p>
    <p><strong>✅ Recommended:</strong> Use 0.9.80 for the best experience across all Android versions.</p>
    <p><strong>✅ Recommended:</strong> Now support 3 Inches printer.</p>
    <p><strong>📱 Compatibility:</strong> Supports Android 13+ with optimized Bluetooth permissions.</p>
    <p><strong>⚠️ Deprecated:</strong> Versions 0.9.71 and 0.9.72 are no longer supported.</p>
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

**Latest Features in 0.9.80:**
- 🆕 Advanced column formatting (2-5 columns)
- 🔧 Improved text alignment and spacing
- 📱 Enhanced Android 13+ compatibility
- 🎨 Better receipt layout capabilities

### Basic Setup
**Permissions in your Manifest**

```
<uses-permission android:name="android.permission.BLUETOOTH_SCAN"  android:usesPermissionFlags="neverForLocation"/>
<uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
```

**Permission check in your Activity. Call checkPermissions() in your onCreate() method**

```java
private static String[] PERMISSIONS_STORAGE = {
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
    Manifest.permission.BLUETOOTH_SCAN,
    Manifest.permission.BLUETOOTH_CONNECT,
    Manifest.permission.BLUETOOTH_PRIVILEGED
};

private static String[] PERMISSIONS_LOCATION = {
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
    Manifest.permission.BLUETOOTH_SCAN,
    Manifest.permission.BLUETOOTH_CONNECT,
    Manifest.permission.BLUETOOTH_PRIVILEGED
};

private void checkPermissions() {
    int permission1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    int permission2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN);

    if (permission1 != PackageManager.PERMISSION_GRANTED) {
        // We don't have permission so prompt the user
        ActivityCompat.requestPermissions(
                this,
                PERMISSIONS_STORAGE,
                1
        );
    } else if (permission2 != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(
                this,
                PERMISSIONS_LOCATION,
                1
        );
    }
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
    printama.printText(Printama.LEFT, text);
    printama.close();
});
```

**Print Text CENTER aligned**

```java
Printama.with(context).connect(printama -> {
    printama.printText(Printama.CENTER, text);
    printama.close();
});
```

**Print Text RIGHT aligned**

```java
Printama.with(context).connect(printama -> {
    printama.printText(Printama.RIGHT, text);
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
    printama.printTextln(printama.formatTwoColumns("Product", "Price"), Printama.LEFT);
    printama.printTextln(printama.formatTwoColumns("Coffee", "$3.50"), Printama.LEFT);
    
    // Three columns with default widths (50% - 20% - 30%)
    printama.printTextln(printama.formatThreeColumns("Item", "Qty", "Total"), Printama.LEFT);
    printama.printTextln(printama.formatThreeColumns("Espresso", "2", "$7.00"), Printama.LEFT);
    
    // Four columns with default widths (40% - 20% - 20% - 20%)
    printama.printTextln(printama.formatFourColumns("ID", "Name", "Stock", "Price"), Printama.LEFT);
    printama.printTextln(printama.formatFourColumns("001", "Coffee", "50", "$3.50"), Printama.LEFT);
    
    // Five columns with default widths (30% - 20% - 20% - 15% - 15%)
    printama.printTextln(printama.formatFiveColumns("ID", "Item", "Cat", "Qty", "$"), Printama.LEFT);
    
    // Custom column widths (percentages must sum to 100)
    double[] customWidths = {60.0, 40.0}; // 60% - 40%
    printama.printTextln(printama.formatTwoColumns("Description", "Amount", customWidths), Printama.LEFT);
    
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
    printama.printImage(Printama.LEFT, bitmap, 200);
    printama.close();
});
```

**Print Bitmap / Image CENTER aligned**

```java
Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
Printama.with(context).connect(printama -> {
    printama.printImage(Printama.CENTER, bitmap, 200);
    printama.close();
});
```

**Print Bitmap / Image RIGHT aligned**

```java
Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
Printama.with(context).connect(printama -> {
    printama.printImage(Printama.RIGHT, bitmap, 200);
    printama.close();
});
```

**Print Bitmap / Image FULL size**

```java
Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
Printama.with(context).connect(printama -> {
    printama.printImage(bitmap, Printama.FULL_WIDTH);
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
    printama.printImage(bitmap, Printama.ORIGINAL_WIDTH);
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

### 🎯 Version 1.0.0 (Coming Soon)
- **Stability Focus:** Comprehensive testing and bug fixes
- **Documentation:** Complete API documentation and guides
- **Video Tutorials:** Step-by-step implementation tutorials
- **Kotlin Migration:** Modern Kotlin-first API design

### 🔮 Future Enhancements
- **3-inch Printer Optimization:** Enhanced support for wider thermal printers
- **Multi-Brand Testing:** Expanded compatibility testing across printer manufacturers
- **Advanced Layouts:** Template-based receipt designs
- **Performance Optimization:** Faster printing and reduced memory usage
- **Cloud Integration:** Remote printing capabilities

### 📈 Recent Achievements (v0.9.80)
- ✅ now support 3 Inches printer
- ✅ Advanced column formatting system
- ✅ Improved Android 13+ compatibility
- ✅ Enhanced text alignment and spacing
- ✅ Better error handling and stability


