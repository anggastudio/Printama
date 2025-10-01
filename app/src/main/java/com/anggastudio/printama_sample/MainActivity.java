package com.anggastudio.printama_sample;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.anggastudio.printama.Printama;
import com.anggastudio.printama.PrintamaUI;
import com.anggastudio.printama.constants.PW;
import com.anggastudio.printama_sample.util.SharedPref;
import com.anggastudio.printama_sample.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final int PERMISSION_REQUEST_BLUETOOTH_CONNECT = 432;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_view), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SharedPref.init(MainActivity.this);

        // back button
        findViewById(R.id.btn_back).setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        // connect printer button
        findViewById(R.id.btn_printer_settings).setOnClickListener(v -> connectToPrinter(true));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if the activity was started by a share intent
        checkBluetoothPermission();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void connectToPrinter(boolean isTriggered) {
        // Check if the permission is not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            if (isTriggered) {
                // Permission is not granted
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_CONNECT)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_SCAN)) {
                    // Permission is denied
                    new AlertDialog.Builder(this)
                            .setTitle("Bluetooth Connect Permission (Nearby Devices) is Denied")
                            .setMessage("Printing feature will not active since bluetooth connect (Nearby Devices) permission is not granted. Please open your app settings and allow Nearby devices permission")
                            .setPositiveButton("Open Settings", (dialog, which) -> openDeviceSettings())
                            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                            .show();
                } else {
                    // ask permission
                    requestBluetoothPermission();
                }
            }
        } else {
            // Permission has already been granted
            showPrinterList();
        }
    }

    private void openDeviceSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void requestBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_CONNECT
                    },
                    PERMISSION_REQUEST_BLUETOOTH_CONNECT);
        } else {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, PERMISSION_REQUEST_BLUETOOTH_CONNECT);
        }
    }

    private void checkBluetoothPermission() {
        // Check if the permission is not granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            // Permission has already been granted
            checkPrinterConnection();
        } else {
            hideAfterConnectLayout();
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void showAfterConnectLayout() {
        findViewById(R.id.printer_connected_layout).setVisibility(View.VISIBLE);

        findViewById(R.id.btn_printer_simple_test).setOnClickListener(v -> simplePrintTest());
        findViewById(R.id.btn_printer_advance_test).setOnClickListener(v -> gotoTestActivity());
        findViewById(R.id.btn_printer_width).setOnClickListener(v -> choosePrinterWidth());
        findViewById(R.id.btn_reset).setOnClickListener(v -> resetPrinterConnection());
        findViewById(R.id.btn_image_service).setOnClickListener(v -> gotoImageServiceActivity());

        displayPrinterWidthInfo();
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void simplePrintTest() {
        // Show loading indicator if needed
        Printama.with(this).printTest();
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void choosePrinterWidth() {
        PrintamaUI.showIs3inchesDialog(this, is3inches -> {
            Printama.is3inchesPrinter(is3inches);
            displayPrinterWidthInfo();
        });
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void displayPrinterWidthInfo() {
        boolean is3inches = Printama.is3inchesPrinter();
        TextView tvPrinterWidth = findViewById(R.id.tv_printer_width_info);
        if (is3inches) {
            tvPrinterWidth.setText("3 inches");
        } else {
            tvPrinterWidth.setText("2 inches");
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void resetPrinterConnection() {
        Printama.resetPrinterConnection();
        checkPrinterConnection();
    }

    private void hideAfterConnectLayout() {
        findViewById(R.id.printer_connected_layout).setVisibility(View.GONE);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void checkPrinterConnection() {
        String deviceNameDisplay = Printama.getSavedPrinterName(this);
        TextView tvPrinterConnectionInfo = findViewById(R.id.tv_printer_info);
        TextView btnConnect = findViewById(R.id.btn_connect);
        if (deviceNameDisplay == null || deviceNameDisplay.isEmpty()) {
            tvPrinterConnectionInfo.setText("Printer is not connected");
            btnConnect.setText("Connect");
            hideAfterConnectLayout();
        } else {
            tvPrinterConnectionInfo.setText("Printer is connected to:");
            btnConnect.setText(deviceNameDisplay);
            showAfterConnectLayout();
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void showPrinterList() {
        PrintamaUI.showPrinterList(this, selectedDevice -> {
            if (selectedDevice != null) {
                // do whatever with the selectedDevice
                checkPrinterConnection();
                showAfterConnectLayout();
                // you can also get the printer name from Printama.getSavedPrinterName
                String deviceNameDisplay = Printama.getSavedPrinterName(this);
                showToast("Connected to " + deviceNameDisplay);
            } else {
                // failed to choose printer
                hideAfterConnectLayout();
            }
        });
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void gotoTestActivity() {
        Intent intent = new Intent(MainActivity.this, PrintTestActivity.class);
        ContextCompat.startActivity(this, intent, new Bundle());
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void gotoImageServiceActivity() {
        Intent intent = new Intent(MainActivity.this, MainServiceActivity.class);
        ContextCompat.startActivity(this, intent, new Bundle());
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String printerName = PrintamaUI.getPrinterResult(resultCode, requestCode, data);
        showResult(printerName);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void showResult(String printerName) {
        if (printerName != null && !printerName.isEmpty()) {
            checkPrinterConnection();
            showAfterConnectLayout();
            String connectionMessage = "Connected to : " + printerName;
            showToast(connectionMessage);
        } else {
            hideAfterConnectLayout();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Handle the result of the permission request
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_BLUETOOTH_CONNECT) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
                // You can proceed with your Bluetooth functionality here
                checkPrinterConnection();
            } else {
                // Permission denied
                // You can handle this case as per your app's requirement
                showToast("permission denied");
            }
        }
    }

}