# Printama
Android library for bluetooth thermal printer.

![Sample 1](images/struk_belanja.jpeg)

## Usage
Currently still in Alpha. Make sure to use java 8+ configuration.
Documentation is in progress. Just take a look at sample project as an
example.

But here for an insight:

**Show dialog to choose bonded device** bind your device initially from
the bluetooth config:
```java
Printama.scan(this, printerName -> {
    ...
});
```

**Print Text**
```java
Printama.with(context).connect(printama -> {
    printama.printText(Printama.CENTER,
            "-------------\n" +
            "This will be printed\n" +
            "Center aligned\n" +
            "cool isn't it?\n" +
            "------------------\n");
    printama.close();
});
```

**Print Bitmap / Image**
```java
Printama.with(context).connect(printama -> {
    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
    printama.printImage(bitmap); // original size, centered as default
    printama.close();
});
```


## Feature
* Dialog to choose bonded bluetooth device.
* Print Text with Custom Alignment.
* Print auto grayscale Bitmap with Custom width and Alignment.
* Tested with 58mm Bluetooth Thermal Printer.


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
  implementation 'com.github.anggastudio:Printama:0.8.1'
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
