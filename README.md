# Printama
Android library for bluetooth thermal printer.

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
Printama printama = new Printama(this);
printama.connect(() -> {
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
Printama printama = new Printama(this);
printama.connect(() -> {
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

You can do :
* a pull request, or
* raise a an issue ticket, or
* request additional feature by raise a ticket.


## License

What license? 

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
#### Maven
**Step 1.**
```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
```

**Step 2.** Add the dependency
```xml
<dependency>
  <groupId>com.github.anggastudio</groupId>
  <artifactId>Printama</artifactId>
  <version>0.8.1</version>
</dependency>
```

