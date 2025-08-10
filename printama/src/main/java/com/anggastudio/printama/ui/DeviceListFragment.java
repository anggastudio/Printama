package com.anggastudio.printama.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anggastudio.printama.Printama;
import com.anggastudio.printama.R;

import java.util.ArrayList;
import java.util.Set;

public class DeviceListFragment extends DialogFragment {

    private Printama.OnConnectPrinter onConnectPrinter;
    private Set<BluetoothDevice> bondedDevices;
    private String selectedDeviceAddress;
    private Button saveButton;
    private Button testButton;
    private int inactiveColor;
    private int activeColor;
    private RecyclerView rvDeviceList;
    private TextView emptyStateText;

    public DeviceListFragment() {
        // Required empty public constructor
    }

    public static DeviceListFragment newInstance() {
        DeviceListFragment fragment = new DeviceListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device_list, container, false);
    }

    public void setOnConnectPrinter(Printama.OnConnectPrinter onConnectPrinter) {
        this.onConnectPrinter = onConnectPrinter;
    }

    public void setDeviceList(Set<BluetoothDevice> bondedDevices) {
        this.bondedDevices = bondedDevices;
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        testButton = view.findViewById(R.id.btn_test_printer);
        testButton.setOnClickListener(v -> testPrinter());
        saveButton = view.findViewById(R.id.btn_save_printer);
        saveButton.setOnClickListener(v -> savePrinter());
        
        rvDeviceList = view.findViewById(R.id.rv_device_list);
        emptyStateText = view.findViewById(R.id.tv_empty_state); // Add this TextView to layout

        if (Printama.getPrinter() != null) {
            selectedDeviceAddress = Printama.getPrinter().getAddress();
        }

        setupDeviceList();
    }

    /**
     * Sets up the device list or shows empty state if no devices are available
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void setupDeviceList() {
        if (bondedDevices == null || bondedDevices.isEmpty()) {
            showEmptyState();
        } else {
            showDeviceList();
        }
    }

    /**
     * Shows the device list when bonded devices are available
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void showDeviceList() {
        rvDeviceList.setVisibility(View.VISIBLE);
        if (emptyStateText != null) {
            emptyStateText.setVisibility(View.GONE);
        }
        
        rvDeviceList.setLayoutManager(new LinearLayoutManager(getContext()));
        ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<>(bondedDevices);
        DeviceListAdapter adapter = new DeviceListAdapter(bluetoothDevices, selectedDeviceAddress);
        rvDeviceList.setAdapter(adapter);
        adapter.setOnConnectPrinter(device -> {
            this.selectedDeviceAddress = device.getAddress();
            toggleButtons();
        });
    }

    /**
     * Shows empty state when no bonded devices are available
     */
    private void showEmptyState() {
        rvDeviceList.setVisibility(View.GONE);
        if (emptyStateText != null) {
            emptyStateText.setVisibility(View.VISIBLE);
            emptyStateText.setText("No paired Bluetooth printers found.\n\nTap the button below to open Bluetooth settings and pair a printer.");
        }
        
        // Change save button to "Open Bluetooth Settings"
        saveButton.setText("Open Bluetooth Settings");
        saveButton.setOnClickListener(v -> openBluetoothSettings());
        
        // Disable test button
        testButton.setVisibility(View.GONE);
    }

    /**
     * Opens Bluetooth settings for the user to pair devices
     */
    private void openBluetoothSettings() {
        Intent bluetoothSettings = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivityForResult(bluetoothSettings, 1001);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            // User returned from Bluetooth settings
            // The calling code should refresh the device list and call setDeviceList() again
            if (onConnectPrinter != null) {
                // Notify the parent that user returned from settings
                // Parent should refresh bonded devices and call setDeviceList() again
                dismiss(); // Close dialog so parent can refresh
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setColor();
        toggleButtons();
    }

    private void setColor() {
        if (getContext() != null) {
            if (this.activeColor == 0) {
                this.activeColor = ContextCompat.getColor(getContext(), R.color.colorGreen);
            }
            if (this.inactiveColor == 0) {
                this.inactiveColor = ContextCompat.getColor(getContext(), R.color.colorGray5);
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void testPrinter() {
        if (selectedDeviceAddress != null) {
            Printama.with(getActivity(), selectedDeviceAddress).printTest();
        }
    }

    private void toggleButtons() {
        if (getContext() != null && bondedDevices != null && !bondedDevices.isEmpty()) {
            if (selectedDeviceAddress != null) {
                testButton.setBackgroundColor(activeColor);
                saveButton.setBackgroundColor(activeColor);
                testButton.setEnabled(true);
                saveButton.setEnabled(true);
            } else {
                testButton.setBackgroundColor(inactiveColor);
                saveButton.setBackgroundColor(inactiveColor);
                testButton.setEnabled(false);
                saveButton.setEnabled(false);
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void savePrinter() {
        if (selectedDeviceAddress != null) {
            Printama.savePrinter(selectedDeviceAddress);
            if (onConnectPrinter != null) {
                BluetoothDevice device = Printama.getPrinter(); // saved printer
                onConnectPrinter.onConnectPrinter(device);
            }
            dismiss();
        }
    }

    public void setColorTheme(int activeColor, int inactiveColor) {
        if (activeColor != 0) {
            this.activeColor = activeColor;
        }
        if (inactiveColor != 0) {
            this.inactiveColor = inactiveColor;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if(dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }
}
