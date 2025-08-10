package com.anggastudio.printama.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;

import androidx.annotation.RequiresPermission;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anggastudio.printama.Printama;
import com.anggastudio.printama.R;

import java.util.ArrayList;
import java.util.Set;

public class ChoosePrinterActivity extends Activity {

    private Set<BluetoothDevice> bondedDevices;
    private String mPrinterAddress;
    private Button saveButton;
    private Button testButton;

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideToolbar();
        setContentView(R.layout.activity_choose_printer);

        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null && !defaultAdapter.getBondedDevices().isEmpty()) {
            bondedDevices = defaultAdapter.getBondedDevices();
        } else {
            showNoBondedDevicesDialog();
        }
    }

    private void hideToolbar() {
        if (getActionBar() != null) {
            getActionBar().hide();
        }
    }

    /**
     * Shows a dialog informing the user that no Bluetooth printers are paired
     * and provides options to go to Bluetooth settings or cancel
     */
    private void showNoBondedDevicesDialog() {
        new AlertDialog.Builder(this)
                .setTitle("No Bluetooth Printers Found")
                .setMessage("No paired Bluetooth devices found. You need to pair your device with a Bluetooth printer first.\n\nWould you like to go to Bluetooth settings to pair a printer?")
                .setPositiveButton("Open Bluetooth Settings", (dialog, which) -> {
                    // Open Bluetooth settings
                    Intent bluetoothSettings = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                    startActivityForResult(bluetoothSettings, 1001);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // User cancelled, finish with error
                    finishWithError();
                })
                .setCancelable(false) // Prevent dismissing by tapping outside
                .show();
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            // User returned from Bluetooth settings, check again for bonded devices
            refreshBondedDevices();
        }
    }

    /**
     * Refreshes the list of bonded devices after user returns from Bluetooth settings
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void refreshBondedDevices() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null && !defaultAdapter.getBondedDevices().isEmpty()) {
            bondedDevices = defaultAdapter.getBondedDevices();
            // Restart the activity to show the device list
            recreate();
        } else {
            // Still no bonded devices, show dialog again
            showNoBondedDevicesDialog();
        }
    }

    private void finishWithError() {
        Intent intent = new Intent();
        intent.putExtra("printama", "No Bluetooth printers paired");
        setResult(Activity.RESULT_CANCELED, intent); // Changed to RESULT_CANCELED for actual failures
        finish();
    }


    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Override
    protected void onStart() {
        super.onStart();
        if (bondedDevices == null) {
            finishWithError();
        } else {
            testButton = findViewById(R.id.btn_test_printer);
            testButton.setOnClickListener(v -> testPrinter());
            saveButton = findViewById(R.id.btn_save_printer);
            saveButton.setOnClickListener(v -> savePrinter());
            mPrinterAddress = Printama.getPrinter().getAddress();
            toggleButtons();

            RecyclerView rvDeviceList = findViewById(R.id.rv_device_list);
            rvDeviceList.setLayoutManager(new LinearLayoutManager(this));
            ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<>(bondedDevices);
            DeviceListAdapter adapter = new DeviceListAdapter(bluetoothDevices, mPrinterAddress);
            rvDeviceList.setAdapter(adapter);
            adapter.setOnConnectPrinter(device -> {
                this.mPrinterAddress = device.getAddress();
                toggleButtons();
            });
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void testPrinter() {
        Printama.with(this, mPrinterAddress).printTest();
    }

    private void toggleButtons() {
        if (mPrinterAddress != null) {
            testButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGreen));
            saveButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGreen));
        } else {
            testButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGray5));
            saveButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGray5));
        }
    }

    private void savePrinter() {
        Printama.savePrinter(mPrinterAddress);
        Intent intent = new Intent();
        intent.putExtra("printama", mPrinterAddress);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}