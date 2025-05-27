package com.anggastudio.printama;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;

import androidx.annotation.ColorRes;
import androidx.annotation.RequiresPermission;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.anggastudio.printama.ui.ChoosePrinterActivity;
import com.anggastudio.printama.ui.ChoosePrinterWidthFragment;
import com.anggastudio.printama.ui.DeviceListFragment;

import java.util.HashSet;
import java.util.Set;

public class PrintamaUI {

    /**
     * Used for request code to get bluetooth paired printer list
     */
    public static final int GET_PRINTER_CODE = 921;

    private static final int _REQUEST_ENABLE_BT = 1101;

    /**
     * Only use this if your project is not androidX.
     * <p>
     * This method will call startActivityForResult to open Choose Printer Page.
     * You can get the result from onActivityResult and call Printama.getPrinterResult() and set all params.
     *
     * @param activity
     */
    public static void showPrinterList(Activity activity) {
        Pref.init(activity);
        Intent intent = new Intent(activity, ChoosePrinterActivity.class);
        activity.startActivityForResult(intent, GET_PRINTER_CODE);
    }

    /**
     * Only use this if your project is not androidX.
     * <p>
     * This method will call startActivityForResult to open Choose Printer Page.
     * You can get the result from onActivityResult and call Printama.getPrinterResult() and set all params.
     *
     * @param activity
     */
    public static void showIs3inchesDialog(Activity activity) {
        Pref.init(activity);
        Intent intent = new Intent(activity, ChoosePrinterActivity.class);
        activity.startActivityForResult(intent, GET_PRINTER_CODE);
    }

    /**
     * Will return printer MAC address if success.
     * Will return empty string if failed.
     * <p>
     * Call this method from onActivityResult and set all the params.
     *
     * @param resultCode
     * @param requestCode
     * @param data
     * @return
     */
    public static String showIs3inchesDialog(int resultCode, int requestCode, Intent data) {
        String printerAddress = "";
        if (-1 == resultCode && GET_PRINTER_CODE == requestCode && data != null) {
            printerAddress = data.getStringExtra("printama");
        }
        return printerAddress;
    }

    /**
     * to choose bluetooth printer which already paired to your device
     *
     * @param activity
     * @param activeColor          @ColorRes example: R.color.black
     * @param inactiveColor        @ColorRes example: R.color.black
     * @param onChoosePrinterWidth
     */
    public static void showIs3inchesDialog(FragmentActivity activity, int activeColor, int inactiveColor, Printama.OnChoosePrinterWidth onChoosePrinterWidth) {
        Pref.init(activity);

        int activeColorResource = activeColor == 0 ? activeColor : ContextCompat.getColor(activity, activeColor);
        int inactiveColorResource = inactiveColor == 0 ? inactiveColor : ContextCompat.getColor(activity, inactiveColor);

        FragmentManager fm = activity.getSupportFragmentManager();
        ChoosePrinterWidthFragment fragment = ChoosePrinterWidthFragment.newInstance();
        fragment.setOnChoosePrinterWidth(onChoosePrinterWidth);
        fragment.setColorTheme(activeColorResource, inactiveColorResource);
        fragment.show(fm, "DeviceListFragment");
    }

    //----------------------------------------------------------------------------------------------
    // PRINTER LIST OVERLAY
    //----------------------------------------------------------------------------------------------

    /**
     * to choose printer width
     * will return integer 58 if 2 inches printer
     * will return integer 80 if 3 inches printer
     *
     * @param activity
     * @param onChoosePrinterWidth
     */
    public static void showIs3inchesDialog(FragmentActivity activity, Printama.OnChoosePrinterWidth onChoosePrinterWidth) {
        showIs3inchesDialog(activity, 0, 0, onChoosePrinterWidth);
    }

    /**
     * to choose bluetooth printer which already paired to your device
     *
     * @param activity
     * @param activeColor          @ColorRes example: R.color.black
     * @param onChoosePrinterWidth
     */
    public static void showIs3inchesDialog(FragmentActivity activity, @ColorRes int activeColor, Printama.OnChoosePrinterWidth onChoosePrinterWidth) {
        showIs3inchesDialog(activity, activeColor, 0, onChoosePrinterWidth);
    }

    //----------------------------------------------------------------------------------------------
    // PRINTER LIST OVERLAY
    //----------------------------------------------------------------------------------------------

    /**
     * to choose bluetooth printer which already paired to your device
     *
     * @param activity
     * @param onConnectPrinter
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public static void showPrinterList(FragmentActivity activity, Printama.OnConnectPrinter onConnectPrinter) {
        showPrinterList(activity, 0, 0, onConnectPrinter);
    }

    /**
     * to choose bluetooth printer which already paired to your device
     *
     * @param activity
     * @param activeColor      @ColorRes example: R.color.black
     * @param onConnectPrinter
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public static void showPrinterList(FragmentActivity activity, @ColorRes int activeColor, Printama.OnConnectPrinter onConnectPrinter) {
        showPrinterList(activity, activeColor, 0, onConnectPrinter);
    }


    /**
     * to choose bluetooth printer which already paired to your device
     *
     * @param activity
     * @param activeColor      @ColorRes example: R.color.black
     * @param inactiveColor    @ColorRes example: R.color.black
     * @param onConnectPrinter
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public static void showPrinterList(FragmentActivity activity, int activeColor, int inactiveColor, Printama.OnConnectPrinter onConnectPrinter) {
        Pref.init(activity);
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        int activeColorResource = activeColor == 0 ? activeColor : ContextCompat.getColor(activity, activeColor);
        int inactiveColorResource = inactiveColor == 0 ? inactiveColor : ContextCompat.getColor(activity, inactiveColor);

        // Check if Bluetooth is enabled
        if (defaultAdapter == null || !defaultAdapter.isEnabled()) {
            // Bluetooth is not enabled, prompt user to enable it
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, _REQUEST_ENABLE_BT);
            return;
        }

        if (!defaultAdapter.getBondedDevices().isEmpty()) {
            // Filter only printer devices
            Set<BluetoothDevice> allDevices = defaultAdapter.getBondedDevices();
            Set<BluetoothDevice> printerDevices = new HashSet<>();

            for (BluetoothDevice device : allDevices) {
                if (Printama.isBluetoothPrinter(device)) {
                    printerDevices.add(device);
                }
            }

            FragmentManager fm = activity.getSupportFragmentManager();
            DeviceListFragment fragment = DeviceListFragment.newInstance();
            fragment.setDeviceList(printerDevices);
            fragment.setOnConnectPrinter(onConnectPrinter);
            fragment.setColorTheme(activeColorResource, inactiveColorResource);
            fragment.show(fm, "DeviceListFragment");
        } else {
            onConnectPrinter.onConnectPrinter(null);
        }
    }


    /**
     * Will return printer MAC address if success.
     * Will return empty string if failed.
     * <p>
     * Call this method from onActivityResult and set all the params.
     *
     * @param resultCode
     * @param requestCode
     * @param data
     * @return
     */
    public static String getPrinterResult(int resultCode, int requestCode, Intent data) {
        String printerAddress = "";
        if (-1 == resultCode && GET_PRINTER_CODE == requestCode && data != null) {
            printerAddress = data.getStringExtra("printama");
        }
        return printerAddress;
    }
}
