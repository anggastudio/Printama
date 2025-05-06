package com.anggastudio.printama_sample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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


        findViewById(R.id.btn_printer_settings).setOnClickListener(v -> showPrinterList());
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

    private void checkBluetoothPermission() {
        // Check if the permission is not granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN},
                    PERMISSION_REQUEST_BLUETOOTH_CONNECT);
        } else {
            // Permission has already been granted
            // You can proceed with your Bluetooth functionality here
            displaySavedPrinterName();
        }
    }

    private void displaySavedPrinterName() {
        // get saved printer name
        String connectedToStr = getPrinterConnectMessage();
        // display to the UI
        TextView tvConnectedTo = findViewById(R.id.tv_printer_info);
        tvConnectedTo.setText(connectedToStr);
    }

    private void showTestPrinterButton() {
        findViewById(R.id.btn_printer_test).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_printer_test).setOnClickListener(v -> testPrinter());
    }

    private void hideTestPrinterButton() {
        findViewById(R.id.btn_printer_test).setVisibility(View.GONE);
    }

    private String getPrinterConnectMessage() {
        String deviceNameDisplay = Printama.getSavedPrinterName(this);
        String connectedToStr = "";
        if (deviceNameDisplay == null) {
            connectedToStr = "Please connect Printer";
            hideTestPrinterButton();
        } else {
            connectedToStr = "Connected to : " + deviceNameDisplay;
            showTestPrinterButton();
        }
        return connectedToStr;
    }

    private void showPrinterList() {
        Printama.showPrinterList(this, R.color.black, selectedDevice -> {
            if (selectedDevice != null) {
                TextView tvConnectedTo = findViewById(R.id.tv_printer_info);
                String connectedToStr = getPrinterConnectMessage();
                tvConnectedTo.setText(connectedToStr);

                showTestPrinterButton();
                // Only show Toast if printer name is not null
                String deviceNameDisplay = Printama.getDeviceNameDisplay(selectedDevice);
                Toast.makeText(this, deviceNameDisplay, Toast.LENGTH_SHORT).show();
            } else {
                hideTestPrinterButton();
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

            showTestPrinterButton();
            showToast(printerName);
        } else {
            hideTestPrinterButton();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

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

    private void handleSharedImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        // Handle the single shared image URI here
        // Example: display the shared image in an ImageView
        ((ImageView) findViewById(R.id.iv_image_will_be_printed)).setImageURI(imageUri);
        printImageReceived(imageUri);
    }

    private void handleSharedImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        // Handle the multiple shared image URIs here
        // Example: display the shared images in a RecyclerView
        ((ImageView) findViewById(R.id.iv_image_will_be_printed)).setImageURI(imageUris.get(0));
        printImageReceived(imageUris.get(0));
    }

    private void printImageReceived(Uri imageUri) {
        Bitmap bitmap = convertUriToBitmap(imageUri);
        if (bitmap != null) {
            if (Util.isAllowToPrint()) {
                // Print the bitmap as
                Printama.with(this).connect(printama -> {
                    printama.printImage(bitmap, Printama.FULL_WIDTH);
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
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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