package com.anggastudio.printama;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Printama {

    public static final int CENTER = -1;
    public static final int RIGHT = -2;
    public static final int LEFT = 0;
    public static final int FULL_WIDTH = -1;
    public static final int ORIGINAL_WIDTH = 0;
    public static final int GET_PRINTER_CODE = 921;

    private static final int MAX_CHAR = 32;
    private static final int MAX_CHAR_WIDE = MAX_CHAR / 2;

    private static Printama printama;
    private static final int REQUEST_ENABLE_BT = 1101;
    private final PrinterUtil util;
    private final BluetoothDevice printer;

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public Printama(Context context) {
        Pref.init(context);
        printer = getPrinter();
        util = new PrinterUtil(printer);
    }

    public Printama(Context context, String printerName) {
        Pref.init(context);
        printer = getPrinter(printerName);
        util = new PrinterUtil(printer);
    }

    public static Printama with(Context context, Callback callback) {
        Printama printama = new Printama(context);
        callback.printama(printama);
        return printama;
    }

    public static Printama with(Context context) {
        printama = new Printama(context);
        return printama;
    }

    static Printama with(Context context, String printerName) {
        printama = new Printama(context, printerName);
        return printama;
    }

    private static BluetoothDevice getPrinter() {
        return getPrinter(Pref.getString(Pref.SAVED_DEVICE));
    }

    private static BluetoothDevice getPrinter(String printerName) {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice printer = null;
        if (defaultAdapter == null) return null;
        for (BluetoothDevice device : defaultAdapter.getBondedDevices()) {
            if (device.getName().equalsIgnoreCase(printerName)) {
                printer = device;
            }
        }
        return printer;
    }

    public BluetoothDevice getConnectedPrinter() {
        return getPrinter();
    }

    public void connect(final OnConnected onConnected) {
        connect(onConnected, null);
    }

    public void connect(final OnConnected onConnected, final OnFailed onFailed) {
        util.connectPrinter(() -> {
            if (onConnected != null) onConnected.onConnected(this);
        }, () -> {
            if (onFailed != null) onFailed.onFailed("Failed to connect printer");
        });
    }

    public boolean isConnected() {
        return util.isConnected();
    }

    public void close() {
        setNormalText();
        new Handler().postDelayed(util::finish, 2000);
    }

    //----------------------------------------------------------------------------------------------
    // PRINT TEST
    //----------------------------------------------------------------------------------------------

    public void printTest() {
        printama.connect(printama -> {
            printama.setNormalText();
            printama.printTextln("------------------", Printama.CENTER);
            printama.printTextln("Print Test", Printama.CENTER);
            printama.printTextln("------------------", Printama.CENTER);
            printama.feedPaper();
            printama.close();
        });
    }

    //----------------------------------------------------------------------------------------------
    // PRINTER LIST OVERLAY
    //----------------------------------------------------------------------------------------------

    public static void showPrinterList(FragmentActivity activity, OnConnectPrinter onConnectPrinter) {
        showPrinterList(activity, 0, 0, onConnectPrinter);
    }

    public static void showPrinterList(FragmentActivity activity, int activeColor, OnConnectPrinter onConnectPrinter) {
        showPrinterList(activity, activeColor, 0, onConnectPrinter);
    }

    public static void showPrinterList(FragmentActivity activity, int activeColor, int inactiveColor, OnConnectPrinter onConnectPrinter) {
        Pref.init(activity);
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        int activeColorResource = activeColor == 0 ? activeColor : ContextCompat.getColor(activity, activeColor);
        int inactiveColorResource = inactiveColor == 0 ? inactiveColor : ContextCompat.getColor(activity, inactiveColor);

        // Check if Bluetooth is enabled
        if (defaultAdapter == null || !defaultAdapter.isEnabled()) {
            // Bluetooth is not enabled, prompt user to enable it
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }

        if (!defaultAdapter.getBondedDevices().isEmpty()) {
            FragmentManager fm = activity.getSupportFragmentManager();
            DeviceListFragment fragment = DeviceListFragment.newInstance();
            fragment.setDeviceList(defaultAdapter.getBondedDevices());
            fragment.setOnConnectPrinter(onConnectPrinter);
            fragment.setColorTheme(activeColorResource, inactiveColorResource);
            fragment.show(fm, "DeviceListFragment");
        } else {
            onConnectPrinter.onConnectPrinter("failed to connect printer, please try again");
        }
    }

    public static void showPrinterList(Activity activity) {
        Pref.init(activity);
        Intent intent = new Intent(activity, ChoosePrinterActivity.class);
        activity.startActivityForResult(intent, Printama.GET_PRINTER_CODE);
    }

    public static String getPrinterResult(int resultCode, int requestCode, Intent data) {
        String printerName = "Failed to get printer, try again!";
        if (-1 == resultCode && Printama.GET_PRINTER_CODE == requestCode && data != null) {
            printerName = data.getStringExtra("printama");
        }
        return printerName;
    }

    //----------------------------------------------------------------------------------------------
    // PRINTER COMMANDS
    //----------------------------------------------------------------------------------------------

    public void setLineSpacing(int lineSpacing) {
        util.setLineSpacing(lineSpacing);
    }

    public void feedPaper() {
        util.feedPaper();
    }


    public void printDashedLine() {
        util.setAlign(LEFT);
        util.printText("--------------------------------");
    }

    public void printLine() {
        util.setAlign(LEFT);
        util.printText("________________________________");
    }

    public void printDoubleDashedLine() {
        util.setAlign(LEFT);
        util.printText("================================");
    }

    public void addNewLine() {
        util.addNewLine();
    }

    public void addNewLine(int count) {
        util.addNewLine(count);
    }

    //----------------------------------------------------------------------------------------------
    // PRINT IMAGE BITMAP
    //----------------------------------------------------------------------------------------------

    public boolean printImage(Bitmap bitmap) {
        return util.printImage(bitmap);
    }

    /**
     * @deprecated As of release 1.0.0,
     * replaced by {@link Printama#printImage(Bitmap bitmap, int width, int alignment)} instead
     */
    @Deprecated
    public boolean printImage(int alignment, Bitmap bitmap, int width) {
        return util.printImage(alignment, bitmap, width);
    }

    public boolean printImage(Bitmap bitmap, int width, int alignment) {
        return util.printImage(alignment, bitmap, width);
    }

    public boolean printImage(Bitmap bitmap, int width) {
        return util.printImage(bitmap, width);
    }

    public static Bitmap getBitmapFromVector(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        return getBitmapFromVector(drawable);
    }

    public static Bitmap getBitmapFromVector(Drawable drawable) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = drawable != null ? (DrawableCompat.wrap(drawable)).mutate() : null;
        }
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
        executorService.execute(() -> printama.printImage(b));
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
        printText(text, LEFT);
    }

    /**
     * @deprecated As of release 1.0.0,
     * replaced by {@link Printama#printText(String text, int align)} instead
     */
    @Deprecated
    public void printText(int align, String text) {
        util.setAlign(align);
        util.printText(text);
    }

    public void printText(String text, int align) {
        util.setAlign(align);
        util.printText(text);
    }

    /**
     * @deprecated As of release 1.0.0,
     * replaced by {@link Printama#printTextln(String text, int align)} instead
     */
    @Deprecated
    public void printTextln(int align, String text) {
        util.setAlign(align);
        printTextln(text);
    }

    public void printTextln(String text, int align) {
        util.setAlign(align);
        printTextln(text);
    }

    public void printTextln(String text) {
        text = text + "\n";
        util.printText(text);
    }

    //----------------------------------------------------------------------------------------------
    // PRINT TEXT JUSTIFY ALIGNMENT
    //----------------------------------------------------------------------------------------------

    public void printTextJustify(String text1, String text2) {
        String justifiedText = getJustifiedText(text1, text2);
        printText(justifiedText);
    }

    public void printTextJustify(String text1, String text2, String text3) {
        String justifiedText = getJustifiedText(text1, text2, text3);
        printText(justifiedText);
    }

    public void printTextJustify(String text1, String text2, String text3, String text4) {
        String justifiedText = getJustifiedText(text1, text2, text3, text4);
        printText(justifiedText);
    }

    public void printTextJustifyBold(String text1, String text2) {
        String justifiedText = getJustifiedText(text1, text2);
        printTextBold(justifiedText);
    }

    public void printTextJustifyBold(String text1, String text2, String text3) {
        String justifiedText = getJustifiedText(text1, text2, text3);
        printTextBold(justifiedText);
    }

    public void printTextJustifyBold(String text1, String text2, String text3, String text4) {
        String justifiedText = getJustifiedText(text1, text2, text3, text4);
        printTextBold(justifiedText);
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
        int spacesCount = MAX_CHAR - text1Length - text2Length;
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
        int spacesCount = (MAX_CHAR - text1Length - text2Length - text3Length) / 2;
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
        int spacesCount = (MAX_CHAR - text1Length - text2Length - text3Length - text4Length) / 3;
        StringBuilder spaces = new StringBuilder();
        for (int i = 0; i < spacesCount; i++) {
            spaces.append(" ");
        }
        return spaces.toString();
    }

    //----------------------------------------------------------------------------------------------
    // PRINT TEXT WITH FORMATTING
    //----------------------------------------------------------------------------------------------

    // Normal
    public void printTextNormal(String text) {
        setNormalText();
        printText(text, LEFT);
    }

    /**
     * @deprecated As of release 1.0.0,
     * replaced by {@link Printama#printTextNormal(String text, int align)} instead
     */
    @Deprecated
    public void printTextNormal(int align, String text) {
        setNormalText();
        util.setAlign(align);
        util.printText(text);
    }

    public void printTextNormal(String text, int align) {
        setNormalText();
        util.setAlign(align);
        util.printText(text);
    }

    /**
     * @deprecated As of release 1.0.0,
     * replaced by {@link Printama#printTextlnNormal(String text, int align)} instead
     */
    @Deprecated
    public void printTextlnNormal(int align, String text) {
        setNormalText();
        util.setAlign(align);
        printTextln(text);
    }

    public void printTextlnNormal(String text, int align) {
        setNormalText();
        util.setAlign(align);
        printTextln(text);
    }

    public void printTextlnNormal(String text) {
        setNormalText();
        text = text + "\n";
        util.printText(text);
    }

    // Bold
    public void printTextBold(String text) {
        setBold();
        printText(text, LEFT);
        setNormalText();
    }

    /**
     * @deprecated As of release 1.0.0,
     * replaced by {@link Printama#printTextBold(String text, int align)} instead
     */
    @Deprecated
    public void printTextBold(int align, String text) {
        setBold();
        util.setAlign(align);
        util.printText(text);
        setNormalText();
    }

    public void printTextBold(String text, int align) {
        setBold();
        util.setAlign(align);
        util.printText(text);
        setNormalText();
    }

    /**
     * @deprecated As of release 1.0.0,
     * replaced by {@link Printama#printTextlnBold(String text, int align)} instead
     */
    @Deprecated
    public void printTextlnBold(int align, String text) {
        setBold();
        util.setAlign(align);
        printTextln(text);
        setNormalText();
    }

    public void printTextlnBold(String text, int align) {
        setBold();
        util.setAlign(align);
        printTextln(text);
        setNormalText();
    }

    public void printTextlnBold(String text) {
        setBold();
        text = text + "\n";
        util.printText(text);
        setNormalText();
    }

    // Tall
    public void printTextTall(String text) {
        setTall();
        printText(text, LEFT);
        setNormalText();
    }

    /**
     * @deprecated As of release 1.0.0,
     * replaced by {@link Printama#printTextTall(String text, int align)} instead
     */
    @Deprecated
    public void printTextTall(int align, String text) {
        setTall();
        util.setAlign(align);
        util.printText(text);
        setNormalText();
    }

    public void printTextTall(String text, int align) {
        setTall();
        util.setAlign(align);
        util.printText(text);
        setNormalText();
    }

    /**
     * @deprecated As of release 1.0.0,
     * replaced by {@link Printama#printTextlnTall(String text, int align)} instead
     */
    @Deprecated
    public void printTextlnTall(int align, String text) {
        setTall();
        util.setAlign(align);
        printTextln(text);
        setNormalText();
    }

    public void printTextlnTall(String text, int align) {
        setTall();
        util.setAlign(align);
        printTextln(text);
        setNormalText();
    }

    public void printTextlnTall(String text) {
        setTall();
        text = text + "\n";
        util.printText(text);
        setNormalText();
    }

    // TallBold
    public void printTextTallBold(String text) {
        setTallBold();
        printText(text, LEFT);
        setNormalText();
    }

    /**
     * @deprecated As of release 1.0.0,
     * replaced by {@link Printama#printTextTallBold(String text, int align)} instead
     */
    @Deprecated
    public void printTextTallBold(int align, String text) {
        setTallBold();
        util.setAlign(align);
        util.printText(text);
        setNormalText();
    }

    public void printTextTallBold(String text, int align) {
        setTallBold();
        util.setAlign(align);
        util.printText(text);
        setNormalText();
    }

    /**
     * @deprecated As of release 1.0.0,
     * replaced by {@link Printama#printTextlnTallBold(String text, int align)} instead
     */
    @Deprecated
    public void printTextlnTallBold(int align, String text) {
        setTallBold();
        util.setAlign(align);
        printTextln(text);
        setNormalText();
    }

    public void printTextlnTallBold(String text, int align) {
        setTallBold();
        util.setAlign(align);
        printTextln(text);
        setNormalText();
    }

    public void printTextlnTallBold(String text) {
        setTallBold();
        text = text + "\n";
        util.printText(text);
        setNormalText();
    }

    // Wide
    public void printTextWide(String text) {
        setWide();
        printText(text, LEFT);
        setNormalText();
    }

    /**
     * @deprecated As of release 1.0.0,
     * replaced by {@link Printama#printTextWide(String text, int align)} instead
     */
    @Deprecated
    public void printTextWide(int align, String text) {
        setWide();
        util.setAlign(align);
        util.printText(text);
        setNormalText();
    }

    public void printTextWide(String text, int align) {
        setWide();
        util.setAlign(align);
        util.printText(text);
        setNormalText();
    }

    /**
     * @deprecated As of release 1.0.0,
     * replaced by {@link Printama#printTextlnWide(String text, int align)} instead
     */
    @Deprecated
    public void printTextlnWide(int align, String text) {
        setWide();
        util.setAlign(align);
        printTextln(text);
        setNormalText();
    }

    public void printTextlnWide(String text, int align) {
        setWide();
        util.setAlign(align);
        printTextln(text);
        setNormalText();
    }

    public void printTextlnWide(String text) {
        setWide();
        text = text + "\n";
        util.printText(text);
        setNormalText();
    }

    // WideBold
    public void printTextWideBold(String text) {
        setWideBold();
        printText(text, LEFT);
        setNormalText();
    }

    /**
     * @deprecated As of release 1.0.0,
     * replaced by {@link Printama#printTextWideBold(String text, int align)} instead
     */
    @Deprecated
    public void printTextWideBold(int align, String text) {
        setWideBold();
        util.setAlign(align);
        util.printText(text);
        setNormalText();
    }

    public void printTextWideBold(String text, int align) {
        setWideBold();
        util.setAlign(align);
        util.printText(text);
        setNormalText();
    }

    /**
     * @deprecated As of release 1.0.0,
     * replaced by {@link Printama#printTextlnWideBold(String text, int align)} instead
     */
    @Deprecated
    public void printTextlnWideBold(int align, String text) {
        setWideBold();
        util.setAlign(align);
        printTextln(text);
        setNormalText();
    }

    public void printTextlnWideBold(String text, int align) {
        setWideBold();
        util.setAlign(align);
        printTextln(text);
        setNormalText();
    }

    public void printTextlnWideBold(String text) {
        setWideBold();
        text = text + "\n";
        util.printText(text);
        setNormalText();
    }

    // WideTall
    public void printTextWideTall(String text) {
        setWideTall();
        printText(text, LEFT);
        setNormalText();
    }

    /**
     * @deprecated As of release 1.0.0,
     * replaced by {@link Printama#printTextWideTall(String text, int align)} instead
     */
    @Deprecated
    public void printTextWideTall(int align, String text) {
        setWideTall();
        util.setAlign(align);
        util.printText(text);
        setNormalText();
    }

    public void printTextWideTall(String text, int align) {
        setWideTall();
        util.setAlign(align);
        util.printText(text);
        setNormalText();
    }

    /**
     * @deprecated As of release 1.0.0,
     * replaced by {@link Printama#printTextlnWideTall(String text, int align)} instead
     */
    @Deprecated
    public void printTextlnWideTall(int align, String text) {
        setWideTall();
        util.setAlign(align);
        printTextln(text);
        setNormalText();
    }

    public void printTextlnWideTall(String text, int align) {
        setWideTall();
        util.setAlign(align);
        printTextln(text);
        setNormalText();
    }

    public void printTextlnWideTall(String text) {
        setWideTall();
        text = text + "\n";
        util.printText(text);
        setNormalText();
    }

    // WideTallBold
    public void printTextWideTallBold(String text) {
        setWideTallBold();
        printText(text, LEFT);
        setNormalText();
    }

    /**
     * @deprecated As of release 1.0.0,
     * replaced by {@link Printama#printTextWideTallBold(String text, int align)} instead
     */
    @Deprecated
    public void printTextWideTallBold(int align, String text) {
        setWideTallBold();
        util.setAlign(align);
        util.printText(text);
        setNormalText();
    }

    public void printTextWideTallBold(String text, int align) {
        setWideTallBold();
        util.setAlign(align);
        util.printText(text);
        setNormalText();
    }

    /**
     * @deprecated As of release 1.0.0,
     * replaced by {@link Printama#printTextlnWideTallBold(String text, int align)} instead
     */
    @Deprecated
    public void printTextlnWideTallBold(int align, String text) {
        setWideTallBold();
        util.setAlign(align);
        printTextln(text);
        setNormalText();
    }

    public void printTextlnWideTallBold(String text, int align) {
        setWideTallBold();
        util.setAlign(align);
        printTextln(text);
        setNormalText();
    }

    public void printTextlnWideTallBold(String text) {
        setWideTallBold();
        text = text + "\n";
        util.printText(text);
        setNormalText();
    }

    //----------------------------------------------------------------------------------------------
    // TEXT FORMAT
    //----------------------------------------------------------------------------------------------

    public void setNormalText() {
        util.setNormalText();
    }

    public void setSmallText() {
        util.setSmallText();
    }

    public void setBold() {
        util.setBold();
    }

    public void setUnderline() {
        util.setUnderline();
    }

    public void setDeleteLine() {
        util.setDeleteLine();
    }

    public void setTall() {
        util.setTall();
    }

    public void setWide() {
        util.setWide();
    }

    public void setWideBold() {
        util.setWideBold();
    }

    public void setTallBold() {
        util.setTallBold();
    }

    public void setWideTall() {
        util.setWideTall();
    }

    public void setWideTallBold() {
        util.setWideTallBold();
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
        void onConnectPrinter(String printerName);
    }

    public interface Callback {
        void printama(Printama printama);
    }


}
