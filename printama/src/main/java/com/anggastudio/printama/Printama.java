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
    private static Printama printama;
    private PrinterUtil util;
    private BluetoothDevice printer;

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

    public void printTest() {
        printama.connect(printama -> {
            printama.normalText();
            printama.printTextln(Printama.CENTER, "------------------");
            printama.printTextln(Printama.CENTER, "Print Test");
            printama.printTextln(Printama.CENTER, "------------------");
            printama.feedPaper();
            printama.close();
        });
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

    public void printText(String text) {
        printText(LEFT, text);
    }

    public void printText(int align, String text) {
        util.setAlign(align);
        util.printText(text);
    }

    public void printTextln(int align, String text) {
        util.setAlign(align);
        printTextln(text);
    }

    public void printTextln(String text) {
        text = text + "\n";
        util.printText(text);
    }

    public void setLineSpacing(int lineSpacing) {
        util.setLineSpacing(lineSpacing);
    }

    public void feedPaper() {
        util.feedPaper();
    }

    public void close() {
        normalText();
        new Handler().postDelayed(util::finish, 2000);
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

    public boolean printImage(Bitmap bitmap) {
        return util.printImage(bitmap);
    }

    public boolean printImage(int alignment, Bitmap bitmap, int width) {
        return util.printImage(alignment, bitmap, width);
    }

    public boolean printImage(Bitmap bitmap, int width) {
        return util.printImage(bitmap, width);
    }

    public static void showPrinterList(FragmentActivity activity, OnConnectPrinter onConnectPrinter) {
        Pref.init(activity);
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null && !defaultAdapter.getBondedDevices().isEmpty()) {
            FragmentManager fm = activity.getSupportFragmentManager();
            DeviceListFragment fragment = DeviceListFragment.newInstance();
            fragment.setDeviceList(defaultAdapter.getBondedDevices());
            fragment.setOnConnectPrinter(onConnectPrinter);
            fragment.show(fm, "DeviceListFragment");
        } else {
            onConnectPrinter.onConnectPrinter("failed to connect printer");
        }
    }

    public static void showPrinterList(Activity activity) {
        Pref.init(activity);
        Intent intent = new Intent(activity, ChoosePrinterActivity.class);
        activity.startActivityForResult(intent, Printama.GET_PRINTER_CODE);
    }

    public void printDashedLine() {
        util.printText("--------------------------------");
    }

    public void printLine() {
        util.printText("________________________________");
    }

    public void printDoubleDashedLine() {
        util.printText("================================");
    }

    public void addNewLine() {
        util.addNewLine();
    }

    public void addNewLine(int count) {
        util.addNewLine(count);
    }

    public boolean isConnected() {
        return util.isConnected();
    }

    public BluetoothDevice getConnectedPrinter() {
        return getPrinter();
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
        View finalView = view;
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

    public static String getPrinterResult(int resultCode, int requestCode, Intent data) {
        String printerName = "failed to get printer";
        if (-1 == resultCode && Printama.GET_PRINTER_CODE == requestCode && data != null) {
            printerName = data.getStringExtra("printama");
        }
        return printerName;
    }

    //----------------------------------------------------------------------------------------------
    // TEXT FORMAT
    //----------------------------------------------------------------------------------------------

    public void normalText() {
        util.normalText();
    }

    public void setBold() {
        util.setBold();
    }

    public void setSmall() {
        util.setSmall();
    }

    public void setUnderline() {
        util.setUnderline();
    }

    public void setTall() {
        util.setTall();
    }

    public void setWide() {
        util.setWide();
    }

    public void setBig() {
        util.setBig();
    }

    public void setBigBold() {
        util.setBigBold();
    }

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
