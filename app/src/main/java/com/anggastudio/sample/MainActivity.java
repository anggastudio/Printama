package com.anggastudio.sample;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.anggastudio.printama.Printama;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_printer_settings).setOnClickListener(v -> showPrinterList());

        getSavedPrinter();
    }

    private void getSavedPrinter() {
        BluetoothDevice connectedPrinter = Printama.with(this).getConnectedPrinter();
        if (connectedPrinter != null) {
            TextView connectedTo = findViewById(R.id.tv_printer_info);
            String text = "Connected to : " + connectedPrinter.getName();
            connectedTo.setText(text);
            findViewById(R.id.btn_printer_test).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_printer_test).setOnClickListener(v -> testPrinter());
        }
    }

    private void showPrinterList() {
        Printama.showPrinterList(this, R.color.colorBlue, printerName -> {
            Toast.makeText(this, printerName, Toast.LENGTH_SHORT).show();
            TextView connectedTo = findViewById(R.id.tv_printer_info);
            String text = "Connected to : " + printerName;
            connectedTo.setText(text);
            if (!printerName.contains("failed")) {
                findViewById(R.id.btn_printer_test).setVisibility(View.VISIBLE);
                findViewById(R.id.btn_printer_test).setOnClickListener(v -> testPrinter());
            }
        });
    }

    private void showPrinterListActivity() {
        // only use this when your project is not androidX
        Printama.showPrinterList(this);
    }

    private void testPrinter() {
        Intent intent = new Intent(MainActivity.this, TestActivity.class);
        ContextCompat.startActivity(this, intent, new Bundle());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String printerName = Printama.getPrinterResult(resultCode, requestCode, data);
        showResult(printerName);
    }

    private void showResult(String printerName) {
        showToast(printerName);
        TextView connectedTo = findViewById(R.id.tv_printer_info);
        String text = "Connected to : " + printerName;
        connectedTo.setText(text);
        if (!printerName.contains("failed")) {
            findViewById(R.id.btn_printer_test).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_printer_test).setOnClickListener(v -> testPrinter());
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
