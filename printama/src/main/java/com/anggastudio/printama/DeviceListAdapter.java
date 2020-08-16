package com.anggastudio.printama;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.Holder> {

    private final ArrayList<BluetoothDevice> bondedDevices;
    private int selectedDevicePos = -1;
    private Printama.OnConnectPrinter onConnectPrinter;

    public DeviceListAdapter(ArrayList<BluetoothDevice> bondedDevices, String mPrinterName) {
        this.bondedDevices = bondedDevices;
        for (int i = 0; i < bondedDevices.size(); i++) {
            if (bondedDevices.get(i).getName().equalsIgnoreCase(mPrinterName)) {
                selectedDevicePos = i;
            }
        }
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        BluetoothDevice device = bondedDevices.get(position);
        holder.tvDeviceName.setText(device.getName());
        holder.itemView.setOnClickListener(v -> {
            selectDevice(holder, position);
        });
        if (position == selectedDevicePos) {
            holder.ivIndicator.setImageResource(R.drawable.ic_check_circle);
        } else {
            holder.ivIndicator.setImageResource(R.drawable.ic_circle);
        }
    }

    private void selectDevice(Holder holder, int position) {
        selectedDevicePos = position;
        holder.ivIndicator.setImageResource(R.drawable.ic_check_circle);
        if (onConnectPrinter != null) {
            BluetoothDevice device = bondedDevices.get(position);
            onConnectPrinter.onConnectPrinter(device.getName());
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return bondedDevices.size();
    }

    public void setOnConnectPrinter(Printama.OnConnectPrinter onConnectPrinter) {
        this.onConnectPrinter = onConnectPrinter;
    }

    static class Holder extends RecyclerView.ViewHolder {

        TextView tvDeviceName;
        ImageView ivIndicator;

        public Holder(@NonNull View itemView) {
            super(itemView);
            tvDeviceName = itemView.findViewById(R.id.tv_device_name);
            ivIndicator = itemView.findViewById(R.id.iv_select_indicator);
        }
    }
}
