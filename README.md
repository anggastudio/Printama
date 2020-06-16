# Printama
android library for bluetooth thermal printer

## Usage
Documentation is in progress. Just take a look at sample project as an example.

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

