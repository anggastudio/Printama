package com.anggastudio.printama;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Set;

public class DeviceListFragment extends DialogFragment {

    private Printama.OnConnectPrinter onConnectPrinter;
    private Set<BluetoothDevice> bondedDevices;
    private String mPrinterName;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btn_save_printer).setOnClickListener(v -> savePrinter());

        RecyclerView rvDeviceList = view.findViewById(R.id.rv_device_list);
        rvDeviceList.setLayoutManager(new LinearLayoutManager(getContext()));
        ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<>(bondedDevices);
        DeviceListAdapter adapter = new DeviceListAdapter(bluetoothDevices);
        rvDeviceList.setAdapter(adapter);
        adapter.setOnConnectPrinter(printerName -> this.mPrinterName = printerName);
    }

    private void savePrinter() {
        if (onConnectPrinter != null) {
            onConnectPrinter.onConnectPrinter(mPrinterName);
        }
        dismiss();
    }
}
