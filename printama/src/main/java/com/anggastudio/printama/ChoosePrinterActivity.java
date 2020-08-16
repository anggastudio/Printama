package com.anggastudio.printama;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Set;

public class ChoosePrinterActivity extends AppCompatActivity {

    private Set<BluetoothDevice> bondedDevices;
    private String mPrinterName;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_printer);

        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null && !defaultAdapter.getBondedDevices().isEmpty()) {
            bondedDevices = defaultAdapter.getBondedDevices();
        } else {
            Intent intent = new Intent();
            intent.putExtra("printama", "failed to connect printer");
            setResult(Activity.RESULT_OK, intent);
            finish();
        }

        saveButton = findViewById(R.id.btn_save_printer);
        saveButton.setOnClickListener(v -> savePrinter());
        mPrinterName = Pref.getString(Pref.SAVED_DEVICE);
        toggleSaveButton();

        RecyclerView rvDeviceList = findViewById(R.id.rv_device_list);
        rvDeviceList.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<>(bondedDevices);
        DeviceListAdapter adapter = new DeviceListAdapter(bluetoothDevices, mPrinterName);
        rvDeviceList.setAdapter(adapter);
        adapter.setOnConnectPrinter(printerName -> {
            this.mPrinterName = printerName;
            toggleSaveButton();
        });
    }

    private void toggleSaveButton() {
        if (mPrinterName != null) {
            saveButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGreen));
        } else {
            saveButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGray5));
        }
    }

    private void savePrinter() {
        Pref.setString(Pref.SAVED_DEVICE, mPrinterName);
        Intent intent = new Intent();
        intent.putExtra("printama", mPrinterName);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}