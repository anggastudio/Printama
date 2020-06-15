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

    public DeviceListAdapter(ArrayList<BluetoothDevice> bondedDevices) {
        this.bondedDevices = bondedDevices;
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
    }

    @Override
    public int getItemCount() {
        return bondedDevices.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        TextView tvDeviceName;
        ImageView ivIndicator;

        public Holder(@NonNull View itemView) {
            super(itemView);
            tvDeviceName = itemView.findViewById(R.id.tv_device_name);
            ivIndicator = itemView.findViewById(R.id.iv_select_indicator);
        }
    }
}
