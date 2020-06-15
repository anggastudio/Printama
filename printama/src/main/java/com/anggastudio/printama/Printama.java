package com.anggastudio.printama;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.graphics.Bitmap;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

public class Printama {

    private final BluetoothPrinter btPrinter;
    private BluetoothDevice printer;

    public Printama() {
        printer = getPrinter();
        btPrinter = new BluetoothPrinter(printer);
    }

    public static BluetoothDevice getPrinter() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice printer = null;
        for (BluetoothDevice device : defaultAdapter.getBondedDevices()) {
            if (device.getName().equalsIgnoreCase("MPT-II")) {
                printer = device;
            }
        }
        return printer;
    }

    public void printImage(Bitmap bitmap) {
        btPrinter.printImage(bitmap);
    }

    public void printText(String text) {
        btPrinter.printText(text);
    }

    public void close() {
        btPrinter.finish();
    }

    public void connect(final OnConnected onConnected) {
        connect(onConnected, null);
    }

    public void connect(final OnConnected onConnected, final OnFailed onFailed) {

        btPrinter.connectPrinter(new BluetoothPrinter.PrinterConnectListener() {

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

    public void printImage(Bitmap bitmap, int width) {
        btPrinter.printImage(bitmap, width);
    }

    public void scan(FragmentActivity activity, OnConnectPrinter onConnectPrinter) {
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
