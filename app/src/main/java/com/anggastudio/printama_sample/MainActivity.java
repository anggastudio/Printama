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

import com.anggastudio.printama.PW;
import com.anggastudio.printama.Printama;
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

        findViewById(R.id.btn_printer_settings).setOnClickListener(v -> connectToPrinter(true));
        SharedPref.init(MainActivity.this);
        checkBluetoothPermission();

        // Check if the activity was started by a share intent
        Intent intent = getIntent();
        if (Intent.ACTION_SEND.equals(intent.getAction()) && intent.getType().startsWith("image/")) {
            handleSharedImage(intent);
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(intent.getAction()) && intent.getType().startsWith("image/")) {
            handleSharedImages(intent);
        }
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
            displaySavedPrinterName();
            displayPrinterWidthInfo();
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void displaySavedPrinterName() {
        // get saved printer name
        String connectedToStr = getPrinterConnectMessage();
        // display to the UI
        TextView tvConnectedTo = findViewById(R.id.tv_printer_info);
        tvConnectedTo.setText(connectedToStr);
    }

    private void showAfterConnectLayout() {
        findViewById(R.id.btn_connect).setVisibility(View.GONE);
        findViewById(R.id.printer_connected_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.reset_button_layout).setVisibility(View.VISIBLE);

        findViewById(R.id.btn_printer_test).setOnClickListener(v -> testPrinter());
        findViewById(R.id.btn_printer_width).setOnClickListener(v -> choosePrinterWidth());
        findViewById(R.id.btn_reset).setOnClickListener(v -> resetPrinterConnection());
    }

    private void choosePrinterWidth() {
        Printama.showIs3inchesDialog(this, is3inches -> {
            Printama.is3inchesPrinter(is3inches);
            displayPrinterWidthInfo();
        });
    }

    private void displayPrinterWidthInfo() {
        boolean is3inches = Printama.is3inchesPrinter();
        TextView tvPrinterWidth = findViewById(R.id.tv_printer_width_info);
        if (is3inches) {
            tvPrinterWidth.setText("3 inches");
        } else {
            tvPrinterWidth.setText("2 inches");
        }
    }

    private void resetPrinterConnection() {
        Printama.resetPrinterConnection();
    }

    private void hideAfterConnectLayout() {
        findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
        findViewById(R.id.printer_connected_layout).setVisibility(View.GONE);
        findViewById(R.id.reset_button_layout).setVisibility(View.GONE);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private String getPrinterConnectMessage() {
        String deviceNameDisplay = Printama.getSavedPrinterName(this);
        String connectedToStr = "";
        if (deviceNameDisplay == null) {
            connectedToStr = "Please connect Printer";
            hideAfterConnectLayout();
        } else {
            connectedToStr = "Connected to " + deviceNameDisplay;
            showAfterConnectLayout();
        }
        return connectedToStr;
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void showPrinterList() {
        Printama.showPrinterList(this, selectedDevice -> {
            if (selectedDevice != null) {
                // printer chosen
                TextView tvConnectedTo = findViewById(R.id.tv_printer_info);
                String connectedToStr = getPrinterConnectMessage();
                tvConnectedTo.setText(connectedToStr);

                showAfterConnectLayout();

                Toast.makeText(this, connectedToStr, Toast.LENGTH_SHORT).show();
            } else {
                // failed to choose printer
                hideAfterConnectLayout();
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
        if (printerName != null) {
            TextView connectedTo = findViewById(R.id.tv_printer_info);
            String text = "Connected to : " + printerName;
            connectedTo.setText(text);

            showAfterConnectLayout();
            showToast(printerName);
        } else {
            hideAfterConnectLayout();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            // Check if the activity was started by a share intent
            if (Intent.ACTION_SEND.equals(intent.getAction()) && intent.getType().startsWith("image/")) {
                handleSharedImage(intent);
            } else if (Intent.ACTION_SEND_MULTIPLE.equals(intent.getAction()) && intent.getType().startsWith("image/")) {
                handleSharedImages(intent);
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void handleSharedImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        // Handle the single shared image URI here
        // Example: display the shared image in an ImageView
        ((ImageView) findViewById(R.id.iv_image_will_be_printed)).setImageURI(imageUri);
        printImageReceived(imageUri);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void handleSharedImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        // Handle the multiple shared image URIs here
        // Example: display the shared images in a RecyclerView
        ((ImageView) findViewById(R.id.iv_image_will_be_printed)).setImageURI(imageUris.get(0));
        printImageReceived(imageUris.get(0));
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void printImageReceived(Uri imageUri) {
        Bitmap bitmap = convertUriToBitmap(imageUri);
        if (bitmap != null) {
            if (Util.isAllowToPrint()) {
                // Print the bitmap as
                Printama.with(this).connect(printama -> {
                    printama.printImage(bitmap, PW.FULL_WIDTH);
                    printama.close();
                }, this::showToast);
            } else {
                showToast("print not allowed");
            }
        } else {
            // Handle the case where conversion failed
            showToast("failed to print image");
        }

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
                displaySavedPrinterName();
            } else {
                // Permission denied
                // You can handle this case as per your app's requirement
                showToast("permission denied");
            }
        }
    }

    public Bitmap convertUriToBitmap(Uri uri) {
        try {
            // Use content resolver to open input stream from the URI
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                // Decode the input stream into a Bitmap using BitmapFactory
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close(); // Close the input stream
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // Return null if conversion fails
    }
}