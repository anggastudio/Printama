package com.anggastudio.printama;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.RequiresPermission;
import androidx.core.content.ContextCompat;

import com.anggastudio.printama.constants.PA;
import com.anggastudio.printama.constants.PW;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Printama {

    /**
     * @deprecated As of release 1.0.0, replaced by {@link PA#CENTER}.
     * <p>Migration example:</p>
     * <pre>
     * // Old way (deprecated)
     * printText(Printama.CENTER, "Hello");
     * 
     * // New way
     * printText("Hello", PA.CENTER);
     * </pre>
     * This constant will be removed in version 2.0.0.
     */
    @Deprecated
    public static final int CENTER = -1;
    
    /**
     * @deprecated As of release 1.0.0, replaced by {@link PA#RIGHT}.
     * <p>Migration example:</p>
     * <pre>
     * // Old way (deprecated)
     * printText(Printama.RIGHT, "Hello");
     * 
     * // New way
     * printText("Hello", PA.RIGHT);
     * </pre>
     * This constant will be removed in version 2.0.0.
     */
    @Deprecated
    public static final int RIGHT = -2;
    
    /**
     * @deprecated As of release 1.0.0, replaced by {@link PA#LEFT}.
     * <p>Migration example:</p>
     * <pre>
     * // Old way (deprecated)
     * printText(Printama.LEFT, "Hello");
     * 
     * // New way
     * printText("Hello", PA.LEFT);
     * </pre>
     * This constant will be removed in version 2.0.0.
     */
    @Deprecated
    public static final int LEFT = 0;
    
    /**
     * @deprecated As of release 1.0.0, replaced by {@link PW#FULL_WIDTH}.
     * <p>Migration example:</p>
     * <pre>
     * // Old way (deprecated)
     * printImage(Printama.CENTER, bitmap, Printama.FULL_WIDTH);
     * 
     * // New way
     * printImage(bitmap, PW.FULL_WIDTH, PA.CENTER);
     * </pre>
     * This constant will be removed in version 2.0.0.
     */
    @Deprecated
    public static final int FULL_WIDTH = -1;
    
    /**
     * @deprecated As of release 1.0.0, replaced by {@link PW#ORIGINAL_WIDTH}.
     * <p>Migration example:</p>
     * <pre>
     * // Old way (deprecated)
     * printImage(Printama.CENTER, bitmap, Printama.ORIGINAL_WIDTH);
     * 
     * // New way
     * printImage(bitmap, PW.ORIGINAL_WIDTH, PA.CENTER);
     * </pre>
     * This constant will be removed in version 2.0.0.
     */
    @Deprecated
    public static final int ORIGINAL_WIDTH = 0;

    private static Printama _printama;

    private final PrinterUtil _util;
    private final BluetoothDevice _printer;

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    /**
     * Creates a new Printama instance using the default saved printer.
     * 
     * @param context The application context
     * @throws SecurityException if BLUETOOTH_CONNECT permission is not granted
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public Printama(Context context) {
        Pref.init(context);
        _printer = getPrinter();
        _util = new PrinterUtil(_printer);
        _util.isIs3InchPrinter(is3inchesPrinter());
    }

    /**
     * Creates a new Printama instance with a specific printer address.
     * 
     * @param context The application context
     * @param printerAddress The Bluetooth address of the target printer
     * @throws SecurityException if BLUETOOTH_CONNECT permission is not granted
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public Printama(Context context, String printerAddress) {
        Pref.init(context);
        _printer = getPrinter(printerAddress);
        _util = new PrinterUtil(_printer);
        _util.isIs3InchPrinter(is3inchesPrinter());
    }

    /**
     * Creates a Printama instance with callback for connection events.
     * 
     * @param context The application context
     * @param callback Callback to receive the Printama instance
     * @return Printama instance
     * @throws SecurityException if BLUETOOTH_CONNECT permission is not granted
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public static Printama with(Context context, Callback callback) {
        Printama printama = new Printama(context);
        callback.printama(printama);
        return printama;
    }

    /**
     * Creates a Printama instance using the default saved printer.
     * 
     * @param context The application context
     * @return Printama instance
     * @throws SecurityException if BLUETOOTH_CONNECT permission is not granted
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public static Printama with(Context context) {
        _printama = new Printama(context);
        return _printama;
    }

    /**
     * Creates a Printama instance with a specific printer name.
     * 
     * @param context The application context
     * @param printerName The name of the target printer
     * @return Printama instance
     * @throws SecurityException if BLUETOOTH_CONNECT permission is not granted
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public static Printama with(Context context, String printerName) {
        _printama = new Printama(context, printerName);
        return _printama;
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public static BluetoothDevice getPrinter() {
        return getPrinter(Pref.getString(Pref.SAVED_DEVICE));
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private static BluetoothDevice getPrinter(String printerAddress) {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice printer = null;
        if (defaultAdapter == null) return null;
        for (BluetoothDevice device : defaultAdapter.getBondedDevices()) {
            if (isBluetoothPrinter(device) && device.getAddress().equalsIgnoreCase(printerAddress)) {
                printer = device;
                break;
            }
        }
        return printer;
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public static boolean isBluetoothPrinter(BluetoothDevice device) {
        if (device.getBluetoothClass() != null) {
            int majorClass = device.getBluetoothClass().getMajorDeviceClass();
            // Printers are in the IMAGING major class (0x0600)
            if (majorClass == BluetoothClass.Device.Major.IMAGING) {
                // Additional check to ensure it's specifically a printer device
                int deviceClass = device.getBluetoothClass().getDeviceClass();
                // Printer is typically indicated by bits 8-11 being set to 0x0004
                // 0x0680 represents an imaging printer device
                return (deviceClass & 0x0680) == 0x0680;
            }
        }
        return false;
    }

    public static void is3inchesPrinter(boolean is3inches) {
        Pref.setBoolean(Pref.IS_PRINTER_3INCH, is3inches);
    }

    public static boolean is3inchesPrinter() {
        return Pref.getBoolean(Pref.IS_PRINTER_3INCH);
    }

    public static void resetPrinterConnection() {
        Pref.setString(Pref.SAVED_DEVICE, "");
        Pref.setBoolean(Pref.IS_PRINTER_3INCH, false);
    }

    public static void savePrinter(String mPrinterAddress) {
        Pref.setString(Pref.SAVED_DEVICE, mPrinterAddress);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public BluetoothDevice getConnectedPrinter() {
        return getPrinter();
    }

    public void connect(final OnConnected onConnected) {
        connect(onConnected, null);
    }

    public void connect(final OnConnected onConnected, final OnFailed onFailed) {
        _util.isIs3InchPrinter(is3inchesPrinter());
        _util.connectPrinter(() -> {
            if (onConnected != null) onConnected.onConnected(this);
        }, () -> {
            if (onFailed != null) onFailed.onFailed("Failed to connect printer");
        });
    }

    public boolean isConnected() {
        return _util.isConnected();
    }

    public void close() {
        setNormalText();
        new Handler().postDelayed(_util::finish, 2000);
    }

    //----------------------------------------------------------------------------------------------
    // PRINT TEST
    //----------------------------------------------------------------------------------------------

    public void printTest() {
        _printama.connect(printama -> {
            printama.setNormalText();
            _util.setAlign(PA.CENTER);
            if (!_util.isIs3InchPrinter()) {
                printTextln("X------------------------------X");
            } else {
                printTextln("X----------------------------------------------X");
            }
            printama.printTextln("Print Test", PA.CENTER);
            _util.setAlign(PA.CENTER);
            if (!_util.isIs3InchPrinter()) {
                printTextln("X==============================X");
            } else {
                printTextln("X==============================================X");
            }
            printama.feedPaper();
            printama.close();
        });
    }


    //----------------------------------------------------------------------------------------------
    // PRINTER COMMANDS
    //----------------------------------------------------------------------------------------------

    public void setLineSpacing(int lineSpacing) {
        _util.setLineSpacing(lineSpacing);
    }

    public void feedPaper() {
        _util.feedPaper();
    }


    public void printDashedLine() {
        _util.setAlign(PA.CENTER);
        if (!_util.isIs3InchPrinter()) {
            printTextln("--------------------------------");
        } else {
            printTextln("------------------------------------------------");
        }
    }

    public void printLine() {
        _util.setAlign(PA.CENTER);
        if (!_util.isIs3InchPrinter()) {
            printTextln("________________________________");
        } else {
            printTextln("________________________________________________");
        }
    }

    public void printDoubleDashedLine() {
        _util.setAlign(PA.CENTER);
        if (!_util.isIs3InchPrinter()) {
            printTextln("================================");
        } else {
            printTextln("================================================");
        }
    }

    public void addNewLine() {
        _util.addNewLine();
    }

    public void addNewLine(int count) {
        _util.addNewLine(count);
    }

    //----------------------------------------------------------------------------------------------
    // PRINT IMAGE BITMAP
    //----------------------------------------------------------------------------------------------

    public boolean printImage(Bitmap bitmap) {
        return _util.printImage(bitmap);
    }

    /**
     * @deprecated As of release 1.0.0, parameter order changed for consistency.
     * Use {@link #printImage(Bitmap, int, int)} instead.
     * <p>Migration example:</p>
     * <pre>
     * // Old way (deprecated)
     * printImage(PA.CENTER, bitmap, PW.FULL_WIDTH);
     * 
     * // New way
     * printImage(bitmap, PW.FULL_WIDTH, PA.CENTER);
     * </pre>
     * This method will be removed in version 2.0.0.
     */
    @Deprecated
    public boolean printImage(int alignment, Bitmap bitmap, int width) {
        return _util.printImage(alignment, bitmap, width);
    }

    public boolean printImage(Bitmap bitmap, int width, int alignment) {
        return _util.printImage(alignment, bitmap, width);
    }

    public boolean printImage(Bitmap bitmap, int width) {
        return _util.printImage(bitmap, width);
    }

    public static Bitmap getBitmapFromVector(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        return getBitmapFromVector(drawable);
    }

    public static Bitmap getBitmapFromVector(Drawable drawable) {
        if (drawable != null) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
        return null;
    }

    public void printFromView(View view) {
        ViewTreeObserver vto = view.getViewTreeObserver();
        View finalView = view; // needs to create new variable
        AtomicInteger viewWidth = new AtomicInteger(view.getMeasuredWidth());
        AtomicInteger viewHeight = new AtomicInteger(view.getMeasuredHeight());
        vto.addOnGlobalLayoutListener(() -> {
            viewWidth.set(finalView.getMeasuredWidth());
            viewHeight.set(finalView.getMeasuredHeight());
        });
        new Handler().postDelayed(() -> loadBitmapAndPrint(view, viewWidth.get(), viewHeight.get()), 500);
    }

    private void loadBitmapAndPrint(View view, int viewWidth, int viewHeight) {
        Bitmap b = loadBitmapFromView(view, viewWidth, viewHeight);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> _printama.printImage(b));
    }

    public Bitmap loadBitmapFromView(View view, int viewWidth, int viewHeight) {
        Bitmap bitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        ColorMatrix ma = new ColorMatrix();
        ma.setSaturation(0);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(ma));
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return bitmap;
    }

    //----------------------------------------------------------------------------------------------
    // PRINT TEXT
    //----------------------------------------------------------------------------------------------

    public void printText(String text) {
        printText(text, PA.LEFT);
    }

    /**
     * @deprecated As of release 1.0.0, replaced by {@link #printText(String, int)}.
     * <p>Migration example:</p>
     * <pre>
     * // Old way (deprecated)
     * printText(Printama.CENTER, "Hello");
     * 
     * // New way
     * printText("Hello", PA.CENTER);
     * </pre>
     * This method will be removed in version 2.0.0.
     */
    @Deprecated
    public void printText(int align, String text) {
        _util.setAlign(align);
        _util.printText(text);
    }

    public void printText(String text, int align) {
        _util.setAlign(align);
        _util.printText(text);
    }

    /**
     * @deprecated As of release 1.0.0, parameter order changed for consistency.
     * Use {@link #printTextln(String, int)} instead.
     * <p>Migration example:</p>
     * <pre>
     * // Old way (deprecated)
     * printTextln(PA.CENTER, "Hello World");
     * 
     * // New way
     * printTextln("Hello World", PA.CENTER);
     * </pre>
     * This method will be removed in version 2.0.0.
     */
    @Deprecated
    public void printTextln(int align, String text) {
        _util.setAlign(align);
        printTextln(text);
    }

    public void printTextln(String text, int align) {
        _util.setAlign(align);
        printTextln(text);
    }

    public void printTextln(String text) {
        text = text + "\n";
        _util.printText(text);
    }

    //----------------------------------------------------------------------------------------------
    // PRINT TEXT JUSTIFY ALIGNMENT
    //----------------------------------------------------------------------------------------------

    public void printTextJustify(String text1, String text2) {
        String justifiedText = getJustifiedText(text1, text2);
        printTextln(justifiedText);
    }

    public void printTextJustify(String text1, String text2, String text3) {
        String justifiedText = getJustifiedText(text1, text2, text3);
        printTextln(justifiedText);
    }

    public void printTextJustify(String text1, String text2, String text3, String text4) {
        String justifiedText = getJustifiedText(text1, text2, text3, text4);
        printTextln(justifiedText);
    }

    public void printTextJustifyBold(String text1, String text2) {
        String justifiedText = getJustifiedText(text1, text2);
        printTextlnBold(justifiedText);
    }

    public void printTextJustifyBold(String text1, String text2, String text3) {
        String justifiedText = getJustifiedText(text1, text2, text3);
        printTextlnBold(justifiedText);
    }

    public void printTextJustifyBold(String text1, String text2, String text3, String text4) {
        String justifiedText = getJustifiedText(text1, text2, text3, text4);
        printTextlnBold(justifiedText);
    }

    private String getJustifiedText(String text1, String text2) {
        String justifiedText = "";
        justifiedText = text1 + getSpaces(text1, text2) + text2;
        return justifiedText;
    }

    private String getJustifiedText(String text1, String text2, String text3) {
        String justifiedText = "";
        String text12 = text1 + getSpaces(text1, text2, text3) + text2;
        justifiedText = text12 + getSpaces(text12, text3) + text3;
        return justifiedText;
    }

    private String getJustifiedText(String text1, String text2, String text3, String text4) {
        String justifiedText = "";
        String text12 = text1 + getSpaces(text1, text2, text3, text4) + text2;
        String text123 = text12 + getSpaces(text12, text3, text4) + text3;
        justifiedText = text123 + getSpaces(text123, text4) + text4;
        return justifiedText;
    }

    private String getSpaces(String text1, String text2) {
        int text1Length = text1.length();
        int text2Length = text2.length();
        int maxChars = _util.getMaxChar();
        int spacesCount = maxChars - text1Length - text2Length;
        StringBuilder spaces = new StringBuilder();
        for (int i = 0; i < spacesCount; i++) {
            spaces.append(" ");
        }
        return spaces.toString();
    }

    private String getSpaces(String text1, String text2, String text3) {
        int text1Length = text1.length();
        int text2Length = text2.length();
        int text3Length = text3.length();
        int maxChars = _util.getMaxChar();
        int spacesCount = (maxChars - text1Length - text2Length - text3Length) / 2;
        StringBuilder spaces = new StringBuilder();
        for (int i = 0; i < spacesCount; i++) {
            spaces.append(" ");
        }
        return spaces.toString();
    }

    private String getSpaces(String text1, String text2, String text3, String text4) {
        int text1Length = text1.length();
        int text2Length = text2.length();
        int text3Length = text3.length();
        int text4Length = text4.length();
        int maxChars = _util.getMaxChar();
        int spacesCount = (maxChars - text1Length - text2Length - text3Length - text4Length) / 3;
        StringBuilder spaces = new StringBuilder();
        for (int i = 0; i < spacesCount; i++) {
            spaces.append(" ");
        }
        return spaces.toString();
    }

    //----------------------------------------------------------------------------------------------
    // COLUMN FORMATTERS
    //----------------------------------------------------------------------------------------------

    /**
     * Format text into two columns with specified width percentages
     * @param col1 First column text
     * @param col2 Second column text
     * @param col1Percent Width percentage for first column (0.0 to 1.0)
     * @param col2Percent Width percentage for second column (0.0 to 1.0)
     * @return Formatted string with proper column alignment
     */
    public String formatTwoColumns(String col1, String col2, double col1Percent, double col2Percent) {
        int maxChars = _util.getMaxChar();
        int col1Width = (int) (maxChars * col1Percent);
        int col2Width = (int) (maxChars * col2Percent);
        
        String truncatedCol1 = truncateString(col1, col1Width);
        String truncatedCol2 = truncateString(col2, col2Width);
        
        return String.format("%-" + col1Width + "s%" + col2Width + "s", truncatedCol1, truncatedCol2);
    }

    /**
     * Format text into two columns with default widths (70% | 30%)
     */
    public String formatTwoColumns(String col1, String col2) {
        return formatTwoColumns(col1, col2, 0.7, 0.3);
    }

    /**
     * Format text into three columns with specified width percentages
     * @param col1 First column text
     * @param col2 Second column text
     * @param col3 Third column text
     * @param col1Percent Width percentage for first column (0.0 to 1.0)
     * @param col2Percent Width percentage for second column (0.0 to 1.0)
     * @param col3Percent Width percentage for third column (0.0 to 1.0)
     * @return Formatted string with proper column alignment
     */
    public String formatThreeColumns(String col1, String col2, String col3, double col1Percent, double col2Percent, double col3Percent) {
        int maxChars = _util.getMaxChar();
        int col1Width = (int) (maxChars * col1Percent);
        int col2Width = (int) (maxChars * col2Percent);
        int col3Width = (int) (maxChars * col3Percent);
        
        String truncatedCol1 = truncateString(col1, col1Width);
        String truncatedCol2 = truncateString(col2, col2Width);
        String truncatedCol3 = truncateString(col3, col3Width);
        
        return String.format("%-" + col1Width + "s%-" + col2Width + "s%" + col3Width + "s", truncatedCol1, truncatedCol2, truncatedCol3);
    }

    /**
     * Format text into three columns with default widths (50% | 20% | 30%)
     */
    public String formatThreeColumns(String col1, String col2, String col3) {
        return formatThreeColumns(col1, col2, col3, 0.5, 0.2, 0.3);
    }

    /**
     * Format text into four columns with specified width percentages
     * @param col1 First column text
     * @param col2 Second column text
     * @param col3 Third column text
     * @param col4 Fourth column text
     * @param col1Percent Width percentage for first column (0.0 to 1.0)
     * @param col2Percent Width percentage for second column (0.0 to 1.0)
     * @param col3Percent Width percentage for third column (0.0 to 1.0)
     * @param col4Percent Width percentage for fourth column (0.0 to 1.0)
     * @return Formatted string with proper column alignment
     */
    public String formatFourColumns(String col1, String col2, String col3, String col4, double col1Percent, double col2Percent, double col3Percent, double col4Percent) {
        int maxChars = _util.getMaxChar();
        int col1Width = (int) (maxChars * col1Percent);
        int col2Width = (int) (maxChars * col2Percent);
        int col3Width = (int) (maxChars * col3Percent);
        int col4Width = (int) (maxChars * col4Percent);
        
        String truncatedCol1 = truncateString(col1, col1Width);
        String truncatedCol2 = truncateString(col2, col2Width);
        String truncatedCol3 = truncateString(col3, col3Width);
        String truncatedCol4 = truncateString(col4, col4Width);
        
        return String.format("%-" + col1Width + "s%-" + col2Width + "s%-" + col3Width + "s%" + col4Width + "s", truncatedCol1, truncatedCol2, truncatedCol3, truncatedCol4);
    }

    /**
     * Format text into four columns with default widths (40% | 20% | 20% | 20%)
     */
    public String formatFourColumns(String col1, String col2, String col3, String col4) {
        return formatFourColumns(col1, col2, col3, col4, 0.4, 0.2, 0.2, 0.2);
    }

    /**
     * Format text into five columns with specified width percentages
     * @param col1 First column text
     * @param col2 Second column text
     * @param col3 Third column text
     * @param col4 Fourth column text
     * @param col5 Fifth column text
     * @param col1Percent Width percentage for first column (0.0 to 1.0)
     * @param col2Percent Width percentage for second column (0.0 to 1.0)
     * @param col3Percent Width percentage for third column (0.0 to 1.0)
     * @param col4Percent Width percentage for fourth column (0.0 to 1.0)
     * @param col5Percent Width percentage for fifth column (0.0 to 1.0)
     * @return Formatted string with proper column alignment
     */
    public String formatFiveColumns(String col1, String col2, String col3, String col4, String col5, double col1Percent, double col2Percent, double col3Percent, double col4Percent, double col5Percent) {
        int maxChars = _util.getMaxChar();
        int col1Width = (int) (maxChars * col1Percent);
        int col2Width = (int) (maxChars * col2Percent);
        int col3Width = (int) (maxChars * col3Percent);
        int col4Width = (int) (maxChars * col4Percent);
        int col5Width = (int) (maxChars * col5Percent);
        
        String truncatedCol1 = truncateString(col1, col1Width);
        String truncatedCol2 = truncateString(col2, col2Width);
        String truncatedCol3 = truncateString(col3, col3Width);
        String truncatedCol4 = truncateString(col4, col4Width);
        String truncatedCol5 = truncateString(col5, col5Width);
        
        return String.format("%-" + col1Width + "s%-" + col2Width + "s%-" + col3Width + "s%-" + col4Width + "s%" + col5Width + "s", truncatedCol1, truncatedCol2, truncatedCol3, truncatedCol4, truncatedCol5);
    }

    /**
     * Format text into five columns with default widths (30% | 15% | 15% | 20% | 20%)
     */
    public String formatFiveColumns(String col1, String col2, String col3, String col4, String col5) {
        return formatFiveColumns(col1, col2, col3, col4, col5, 0.3, 0.15, 0.15, 0.2, 0.2);
    }

    /**
     * Truncate string to specified width, adding "." if truncated
     * @param text Original text
     * @param width Maximum width
     * @return Truncated string
     */
    private String truncateString(String text, int width) {
        if (text == null) text = "";
        if (text.length() <= width) {
            return text;
        }
        return text.substring(0, width - 1) + ".";
    }

    //----------------------------------------------------------------------------------------------
    // PRINT TEXT WITH FORMATTING
    //----------------------------------------------------------------------------------------------

    // Normal
    public void printTextNormal(String text) {
        setNormalText();
        printText(text, PA.LEFT);
    }

    /**
     * @deprecated As of release 1.0.0, parameter order changed for consistency.
     * Use {@link #printTextNormal(String, int)} instead.
     * <p>Migration example:</p>
     * <pre>
     * // Old way (deprecated)
     * printTextNormal(PA.CENTER, "Hello World");
     * 
     * // New way
     * printTextNormal("Hello World", PA.CENTER);
     * </pre>
     * This method will be removed in version 2.0.0.
     */
    @Deprecated
    public void printTextNormal(int align, String text) {
        setNormalText();
        _util.setAlign(align);
        _util.printText(text);
    }

    public void printTextNormal(String text, int align) {
        setNormalText();
        _util.setAlign(align);
        _util.printText(text);
    }

    /**
     * @deprecated As of release 1.0.0, parameter order changed for consistency.
     * Use {@link #printTextlnNormal(String, int)} instead.
     * <p>Migration example:</p>
     * <pre>
     * // Old way (deprecated)
     * printTextlnNormal(PA.CENTER, "Hello World");
     * 
     * // New way
     * printTextlnNormal("Hello World", PA.CENTER);
     * </pre>
     * This method will be removed in version 2.0.0.
     */
    @Deprecated
    public void printTextlnNormal(int align, String text) {
        setNormalText();
        _util.setAlign(align);
        printTextln(text);
    }

    public void printTextlnNormal(String text, int align) {
        setNormalText();
        _util.setAlign(align);
        printTextln(text);
    }

    public void printTextlnNormal(String text) {
        setNormalText();
        text = text + "\n";
        _util.printText(text);
    }

    // Bold
    public void printTextBold(String text) {
        setBold();
        printText(text, PA.LEFT);
        setNormalText();
    }

    /**
     * @deprecated As of release 1.0.0, parameter order changed for consistency.
     * Use {@link #printTextBold(String, int)} instead.
     * <p>Migration example:</p>
     * <pre>
     * // Old way (deprecated)
     * printTextBold(PA.CENTER, "Hello World");
     * 
     * // New way
     * printTextBold("Hello World", PA.CENTER);
     * </pre>
     * This method will be removed in version 2.0.0.
     */
    @Deprecated
    public void printTextBold(int align, String text) {
        setBold();
        _util.setAlign(align);
        _util.printText(text);
        setNormalText();
    }

    public void printTextBold(String text, int align) {
        setBold();
        _util.setAlign(align);
        _util.printText(text);
        setNormalText();
    }

    /**
     * @deprecated As of release 1.0.0, parameter order changed for consistency.
     * Use {@link #printTextlnBold(String, int)} instead.
     * <p>Migration example:</p>
     * <pre>
     * // Old way (deprecated)
     * printTextlnBold(PA.CENTER, "Hello World");
     * 
     * // New way
     * printTextlnBold("Hello World", PA.CENTER);
     * </pre>
     * This method will be removed in version 2.0.0.
     */
    @Deprecated
    public void printTextlnBold(int align, String text) {
        setBold();
        _util.setAlign(align);
        printTextln(text);
        setNormalText();
    }

    public void printTextlnBold(String text, int align) {
        setBold();
        _util.setAlign(align);
        printTextln(text);
        setNormalText();
    }

    public void printTextlnBold(String text) {
        setBold();
        text = text + "\n";
        _util.printText(text);
        setNormalText();
    }

    // Tall
    public void printTextTall(String text) {
        setTall();
        printText(text, PA.LEFT);
        setNormalText();
    }

    /**
     * @deprecated As of release 1.0.0, parameter order changed for consistency.
     * Use {@link #printTextTall(String, int)} instead.
     * <p>Migration example:</p>
     * <pre>
     * // Old way (deprecated)
     * printTextTall(PA.CENTER, "Hello World");
     * 
     * // New way
     * printTextTall("Hello World", PA.CENTER);
     * </pre>
     * This method will be removed in version 2.0.0.
     */
    @Deprecated
    public void printTextTall(int align, String text) {
        setTall();
        _util.setAlign(align);
        _util.printText(text);
        setNormalText();
    }

    public void printTextTall(String text, int align) {
        setTall();
        _util.setAlign(align);
        _util.printText(text);
        setNormalText();
    }

    /**
     * @deprecated As of release 1.0.0, parameter order changed for consistency.
     * Use {@link #printTextlnTall(String, int)} instead.
     * <p>Migration example:</p>
     * <pre>
     * // Old way (deprecated)
     * printTextlnTall(PA.CENTER, "Hello World");
     * 
     * // New way
     * printTextlnTall("Hello World", PA.CENTER);
     * </pre>
     * This method will be removed in version 2.0.0.
     */
    @Deprecated
    public void printTextlnTall(int align, String text) {
        setTall();
        _util.setAlign(align);
        printTextln(text);
        setNormalText();
    }

    public void printTextlnTall(String text, int align) {
        setTall();
        _util.setAlign(align);
        printTextln(text);
        setNormalText();
    }

    public void printTextlnTall(String text) {
        setTall();
        text = text + "\n";
        _util.printText(text);
        setNormalText();
    }

    // TallBold
    public void printTextTallBold(String text) {
        setTallBold();
        printText(text, PA.LEFT);
        setNormalText();
    }

    /**
     * @deprecated As of release 1.0.0, parameter order changed for consistency.
     * Use {@link #printTextTallBold(String, int)} instead.
     * <p>Migration example:</p>
     * <pre>
     * // Old way (deprecated)
     * printTextTallBold(PA.CENTER, "Hello World");
     * 
     * // New way
     * printTextTallBold("Hello World", PA.CENTER);
     * </pre>
     * This method will be removed in version 2.0.0.
     */
    @Deprecated
    public void printTextTallBold(int align, String text) {
        setTallBold();
        _util.setAlign(align);
        _util.printText(text);
        setNormalText();
    }

    public void printTextTallBold(String text, int align) {
        setTallBold();
        _util.setAlign(align);
        _util.printText(text);
        setNormalText();
    }

    /**
     * @deprecated As of release 1.0.0, parameter order changed for consistency.
     * Use {@link #printTextlnTallBold(String, int)} instead.
     * <p>Migration example:</p>
     * <pre>
     * // Old way (deprecated)
     * printTextlnTallBold(PA.CENTER, "Hello World");
     * 
     * // New way
     * printTextlnTallBold("Hello World", PA.CENTER);
     * </pre>
     * This method will be removed in version 2.0.0.
     */
    @Deprecated
    public void printTextlnTallBold(int align, String text) {
        setTallBold();
        _util.setAlign(align);
        printTextln(text);
        setNormalText();
    }

    public void printTextlnTallBold(String text, int align) {
        setTallBold();
        _util.setAlign(align);
        printTextln(text);
        setNormalText();
    }

    public void printTextlnTallBold(String text) {
        setTallBold();
        text = text + "\n";
        _util.printText(text);
        setNormalText();
    }

    // Wide
    public void printTextWide(String text) {
        setWide();
        printText(text, PA.LEFT);
        setNormalText();
    }

    /**
     * @deprecated As of release 1.0.0, parameter order changed for consistency.
     * Use {@link #printTextWide(String, int)} instead.
     * <p>Migration example:</p>
     * <pre>
     * // Old way (deprecated)
     * printTextWide(PA.CENTER, "Hello World");
     * 
     * // New way
     * printTextWide("Hello World", PA.CENTER);
     * </pre>
     * This method will be removed in version 2.0.0.
     */
    @Deprecated
    public void printTextWide(int align, String text) {
        setWide();
        _util.setAlign(align);
        _util.printText(text);
        setNormalText();
    }

    public void printTextWide(String text, int align) {
        setWide();
        _util.setAlign(align);
        _util.printText(text);
        setNormalText();
    }

    /**
     * @deprecated As of release 1.0.0, parameter order changed for consistency.
     * Use {@link #printTextlnWide(String, int)} instead.
     * <p>Migration example:</p>
     * <pre>
     * // Old way (deprecated)
     * printTextlnWide(PA.CENTER, "Hello World");
     * 
     * // New way
     * printTextlnWide("Hello World", PA.CENTER);
     * </pre>
     * This method will be removed in version 2.0.0.
     */
    @Deprecated
    public void printTextlnWide(int align, String text) {
        setWide();
        _util.setAlign(align);
        printTextln(text);
        setNormalText();
    }

    public void printTextlnWide(String text, int align) {
        setWide();
        _util.setAlign(align);
        printTextln(text);
        setNormalText();
    }

    public void printTextlnWide(String text) {
        setWide();
        text = text + "\n";
        _util.printText(text);
        setNormalText();
    }

    // WideBold
    public void printTextWideBold(String text) {
        setWideBold();
        printText(text, PA.LEFT);
        setNormalText();
    }

    /**
     * @deprecated As of release 1.0.0, parameter order changed for consistency.
     * Use {@link #printTextWideBold(String, int)} instead.
     * <p>Migration example:</p>
     * <pre>
     * // Old way (deprecated)
     * printTextWideBold(PA.CENTER, "Hello World");
     * 
     * // New way
     * printTextWideBold("Hello World", PA.CENTER);
     * </pre>
     * This method will be removed in version 2.0.0.
     */
    @Deprecated
    public void printTextWideBold(int align, String text) {
        setWideBold();
        _util.setAlign(align);
        _util.printText(text);
        setNormalText();
    }

    public void printTextWideBold(String text, int align) {
        setWideBold();
        _util.setAlign(align);
        _util.printText(text);
        setNormalText();
    }

    /**
     * @deprecated As of release 1.0.0, parameter order changed for consistency.
     * Use {@link #printTextlnWideBold(String, int)} instead.
     * <p>Migration example:</p>
     * <pre>
     * // Old way (deprecated)
     * printTextlnWideBold(PA.CENTER, "Hello World");
     * 
     * // New way
     * printTextlnWideBold("Hello World", PA.CENTER);
     * </pre>
     * This method will be removed in version 2.0.0.
     */
    @Deprecated
    public void printTextlnWideBold(int align, String text) {
        setWideBold();
        _util.setAlign(align);
        printTextln(text);
        setNormalText();
    }

    public void printTextlnWideBold(String text, int align) {
        setWideBold();
        _util.setAlign(align);
        printTextln(text);
        setNormalText();
    }

    public void printTextlnWideBold(String text) {
        setWideBold();
        text = text + "\n";
        _util.printText(text);
        setNormalText();
    }

    // WideTall
    public void printTextWideTall(String text) {
        setWideTall();
        printText(text, PA.LEFT);
        setNormalText();
    }

    /**
     * @deprecated As of release 1.0.0, parameter order changed for consistency.
     * Use {@link #printTextWideTall(String, int)} instead.
     * <p>Migration example:</p>
     * <pre>
     * // Old way (deprecated)
     * printTextWideTall(PA.CENTER, "Hello World");
     * 
     * // New way
     * printTextWideTall("Hello World", PA.CENTER);
     * </pre>
     * This method will be removed in version 2.0.0.
     */
    @Deprecated
    public void printTextWideTall(int align, String text) {
        setWideTall();
        _util.setAlign(align);
        _util.printText(text);
        setNormalText();
    }

    public void printTextWideTall(String text, int align) {
        setWideTall();
        _util.setAlign(align);
        _util.printText(text);
        setNormalText();
    }

    /**
     * @deprecated As of release 1.0.0, parameter order changed for consistency.
     * Use {@link #printTextlnWideTall(String, int)} instead.
     * <p>Migration example:</p>
     * <pre>
     * // Old way (deprecated)
     * printTextlnWideTall(PA.CENTER, "Hello World");
     * 
     * // New way
     * printTextlnWideTall("Hello World", PA.CENTER);
     * </pre>
     * This method will be removed in version 2.0.0.
     */
    @Deprecated
    public void printTextlnWideTall(int align, String text) {
        setWideTall();
        _util.setAlign(align);
        printTextln(text);
        setNormalText();
    }

    public void printTextlnWideTall(String text, int align) {
        setWideTall();
        _util.setAlign(align);
        printTextln(text);
        setNormalText();
    }

    public void printTextlnWideTall(String text) {
        setWideTall();
        text = text + "\n";
        _util.printText(text);
        setNormalText();
    }

    // WideTallBold
    public void printTextWideTallBold(String text) {
        setWideTallBold();
        printText(text, PA.LEFT);
        setNormalText();
    }

    /**
     * @deprecated As of release 1.0.0, parameter order changed for consistency.
     * Use {@link #printTextWideTallBold(String, int)} instead.
     * <p>Migration example:</p>
     * <pre>
     * // Old way (deprecated)
     * printTextWideTallBold(PA.CENTER, "Hello World");
     * 
     * // New way
     * printTextWideTallBold("Hello World", PA.CENTER);
     * </pre>
     * This method will be removed in version 2.0.0.
     */
    @Deprecated
    public void printTextWideTallBold(int align, String text) {
        setWideTallBold();
        _util.setAlign(align);
        _util.printText(text);
        setNormalText();
    }

    public void printTextWideTallBold(String text, int align) {
        setWideTallBold();
        _util.setAlign(align);
        _util.printText(text);
        setNormalText();
    }

    /**
     * @deprecated As of release 1.0.0, parameter order changed for consistency.
     * Use {@link #printTextlnWideTallBold(String, int)} instead.
     * <p>Migration example:</p>
     * <pre>
     * // Old way (deprecated)
     * printTextlnWideTallBold(PA.CENTER, "Hello World");
     * 
     * // New way
     * printTextlnWideTallBold("Hello World", PA.CENTER);
     * </pre>
     * This method will be removed in version 2.0.0.
     */
    @Deprecated
    public void printTextlnWideTallBold(int align, String text) {
        setWideTallBold();
        _util.setAlign(align);
        printTextln(text);
        setNormalText();
    }

    public void printTextlnWideTallBold(String text, int align) {
        setWideTallBold();
        _util.setAlign(align);
        printTextln(text);
        setNormalText();
    }

    public void printTextlnWideTallBold(String text) {
        setWideTallBold();
        text = text + "\n";
        _util.printText(text);
        setNormalText();
    }

    //----------------------------------------------------------------------------------------------
    // TEXT FORMAT
    //----------------------------------------------------------------------------------------------

    public void setNormalText() {
        _util.setNormalText();
    }

    public void setSmallText() {
        _util.setSmallText();
    }

    public void setBold() {
        _util.setBold();
    }

    public void setUnderline() {
        _util.setUnderline();
    }

    public void setDeleteLine() {
        _util.setDeleteLine();
    }

    public void setTall() {
        _util.setTall();
    }

    public void setWide() {
        _util.setWide();
    }

    public void setWideBold() {
        _util.setWideBold();
    }

    public void setTallBold() {
        _util.setTallBold();
    }

    public void setWideTall() {
        _util.setWideTall();
    }

    public void setWideTallBold() {
        _util.setWideTallBold();
    }

    //----------------------------------------------------------------------------------------------
    // INTERFACES
    //----------------------------------------------------------------------------------------------

    public interface OnConnected {
        void onConnected(Printama printama);
    }

    public interface OnFailed {
        void onFailed(String message);
    }

    public interface OnConnectPrinter {
        void onConnectPrinter(BluetoothDevice device);
    }

    public interface OnChoosePrinterWidth {
        void onChoosePrinterWidth(boolean is3inches);
    }

    public interface Callback {
        void printama(Printama printama);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public static String getDeviceNameDisplay(BluetoothDevice device) {
        if (device == null) {
            return null;
        }
        String deviceInfo = device.getName();
        if (device.getAddress() != null) {
            deviceInfo += "_" + device.getAddress().substring(device.getAddress().length() - 5);
        }
        return deviceInfo;
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public static String getSavedPrinterName(Context context) {
        BluetoothDevice connectedPrinter = Printama.with(context).getConnectedPrinter();
        return getDeviceNameDisplay(connectedPrinter);
    }

}


