<p align="center">
  <h1 align="center">Printama</h1>
</p>

<p align="center">
  <img src="https://images.unsplash.com/photo-1605978505713-4fa239312995?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80"/>
</p>


<p align="center">
  <a href="#"><img alt="bintray" src="https://img.shields.io/jitpack/v/github/anggastudio/Printama"></a>
  <a href="LICENSE"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"></a>
  <a href="https://github.com/anggastudio/Printama/pulls"><img alt="Pull request" src="https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat"></a>
  <a href="https://github.com/anggastudio"><img alt="Fcm docs" src="https://img.shields.io/github/contributors/anggastudio/Printama"></a>
  <a href="https://twitter.com/angga_studio"><img alt="Instagram" src="https://img.shields.io/twitter/follow/angga_studio"></a>
  <a href="https://github.com/anggastudio"><img alt="Github" src="https://img.shields.io/github/followers/anggastudio?label=follow&style=social"></a>
  <p align="center">Android library for bluetooth thermal printer.<br>Tested to many 2 inch bluetooth thermal printers.</p>
</p>


## Screenshot
|Payment Receipt|Print Text and Images|
|---|---|
|![](images/struk_belanja.jpeg)|![](images/print_text_image.jpeg)|

|Photo|Photo Print Result|
|---|---|
|![](images/rose.jpeg)|![](images/rose_print.jpeg)|

|Screen Layout|Screen Layout Print Result|
|---|---|
|![](images/layout.jpeg)|![](images/print_layout.jpeg)|


## Usage
Currently still in Alpha. Make sure to use java 8+ configuration.
Documentation is in progress. Just take a look at sample project as an
example.

But here for an insight:

**Show dialog to choose bonded device** bind your device initially from
the bluetooth config:
```java
Printama.showPrinterList(this, printerName -> {
    ...
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
using print layout view, you can design your receipt on your layout xml or dsl, and pass the root view as parameter
```java
View view = findViewById(R.id.root_view);
Printama.with(this).connect(printama -> {
    printama.printFromView(view);
    printama.close();
});
```

## Feature
* Dialog to choose bonded bluetooth device list.
* Print Text with Custom Alignment.
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

Minimum Android SDK Version 16

#### Gradle
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
```gradle
dependencies {
  implementation 'com.github.anggastudio:Printama:0.9.1'
}
```
#### Other like Maven, SBT, Leiningen
**just visit the jitpack page**
[Printama Jitpack](https://jitpack.io/#anggastudio/Printama)

## License

[Apache License 2.0](LICENSE)


## Thanks To:

- [imrankst1221](https://github.com/imrankst1221/Thermal-Printer-in-Android)
- [MFori](https://github.com/MFori/Android-Bluetooth-Printer)
- WP

## Contributor:

- [utsmannn](https://github.com/utsmannn)
- you (maybe)
