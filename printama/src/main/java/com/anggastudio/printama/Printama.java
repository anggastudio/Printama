package com.anggastudio.printama;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Bitmap;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

public class Printama {

    public static final int CENTER = -1;
    public static final int RIGHT = -2;
    public static final int LEFT = 0;
    public static final int FULL_WIDTH = -1;
    public static final int ORIGINAL_WIDTH = 0;
    private final PrinterUtil util;
    private BluetoothDevice printer;

    public Printama(Context context) {
        Pref.init(context);
        printer = getPrinter();
        util = new PrinterUtil(printer);
    }

    public static BluetoothDevice getPrinter() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice printer = null;
        for (BluetoothDevice device : defaultAdapter.getBondedDevices()) {
            if (device.getName().equalsIgnoreCase(Pref.getString(Pref.SAVED_DEVICE))) {
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

    public void close() {
        util.finish();
    }

    public void connect(final OnConnected onConnected) {
        connect(onConnected, null);
    }

    public void connect(final OnConnected onConnected, final OnFailed onFailed) {

        util.connectPrinter(new PrinterUtil.PrinterConnectListener() {

            @Override
            public void onConnected() {
                if (onConnected != null) onConnected.onConnected();
            }

            @Override
            public void onFailed() {
                if (onFailed != null) onFailed.onFailed("Failed to connect printer");
            }

        });
    }

    public void printImage(Bitmap bitmap) {
        util.printImage(bitmap);
    }

    public void printImage(int alignment, Bitmap bitmap, int width) {
        util.printImage(alignment, bitmap, width);
    }


    public static void scan(FragmentActivity activity, OnConnectPrinter onConnectPrinter) {
        Pref.init(activity);
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null && !defaultAdapter.getBondedDevices().isEmpty()) {
            FragmentManager fm = activity.getSupportFragmentManager();
            DeviceListFragment fragment = DeviceListFragment.newInstance();
            fragment.setDeviceList(defaultAdapter.getBondedDevices());
            fragment.setOnConnectPrinter(onConnectPrinter);
            fragment.show(fm, "fragment_add_edit_table");
        } else {
            onConnectPrinter.onConnectPrinter("failed to connect printer");
        }
    }

    public void printImage(Bitmap bitmap, int width) {
        util.printImage(bitmap, width);
    }

    public void printDashedLine() {
        util.printDashedLine();
    }

    public void printDoubleDashedLine() {
        util.printDoubleDashedLine();
    }

    public void addNewLine() {
        util.addNewLine();
    }

    public void addNewLine(int count) {
        util.addNewLine(count);
    }

    public interface OnConnected {
        void onConnected();
    }

    public interface OnFailed {
        void onFailed(String message);
    }

    public interface OnConnectPrinter {
        void onConnectPrinter(String printerName);
    }
}
