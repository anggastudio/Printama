<p align="center">
  <h1 align="center">Printama</h1>
  <h4 align="center">Android library for bluetooth thermal printer.<br>Tested to many 2 inch bluetooth thermal printers.</h4>
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


## Support me:

I notice that some of you are very happy with this library and some of you want to give support. But I also noticed some of
you want to support me anonymously. Well then, I appreciate it if you bought me a coffee :coffee:

|:coffee:|Link|
|---|---|
|buymeacoffee|https://www.buymeacoffee.com/anggastudio|
|ko-fi|https://ko-fi.com/anggastudio|


## Usage

Currently still in Alpha. Make sure to use Java 8+ configuration. Documentation is in progress. Just take a look at
sample project as an example.

But here for an insight:
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

## Feature

* Dialog to choose bonded bluetooth device list.
* Print Text with LEFT, CENTER, or RIGHT Alignment.
* Print Text with JUSTIFY Alignment.
* Print auto grayscale Bitmap with Custom width and Alignment.
* Print photo (grayscaled)
* Print your android screen or layout by passing the root view
* Print vector drawable
* Tested with 2 inch Bluetooth Thermal Printers.

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
  implementation 'com.github.anggastudio:Printama:<version>'
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

## Next Step:

- release 1.0.0 to make it more stable
- enhance documentation
- create a video tutorial
- 3 inches printer
- testing on more printer brands
- (still in my mind) migrate to kotlin

