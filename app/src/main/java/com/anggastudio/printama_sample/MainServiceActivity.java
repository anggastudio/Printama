package com.anggastudio.printama_sample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.anggastudio.printama.Printama;
import com.anggastudio.printama.constants.PW;
import com.anggastudio.printama_sample.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainServiceActivity extends AppCompatActivity {

    private Uri imageUri = null;
    private ArrayList<Uri> imageUris = new ArrayList<>();

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_service);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ((Button) findViewById(R.id.btn_print_again)).setOnClickListener(view -> {
            printAgain();
        });
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void printAgain() {
        if (imageUri != null) {
            printImageReceived(imageUri);
        } else if (imageUris != null) {
            ((ImageView) findViewById(R.id.iv_image_will_be_printed)).setImageURI(imageUris.get(0));
            printImageReceived(imageUris.get(0));
        } else {
            showToast("Nothing can be printed!");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if the activity was started by a share intent
        checkBluetoothPermission();
        checkSharedImageIntent();
    }

    private void checkBluetoothPermission() {
        // Check if the permission is not granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            // Permission has already been granted
            // not implemented yet
        } else {
            // not implemented yet
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void checkSharedImageIntent() {
        Intent intent = getIntent();
        if (Intent.ACTION_SEND.equals(intent.getAction()) && intent.getType() != null && intent.getType().startsWith("image/")) {
            handleSharedImage(intent);
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(intent.getAction()) && intent.getType() != null && intent.getType().startsWith("image/")) {
            handleSharedImages(intent);
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void handleSharedImage(Intent intent) {
        imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        // Handle the single shared image URI here
        // Example: display the shared image in an ImageView
        ((ImageView) findViewById(R.id.iv_image_will_be_printed)).setImageURI(imageUri);
        printImageReceived(imageUri);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void handleSharedImages(Intent intent) {
        imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        // Handle the multiple shared image URIs here
        // Example: display the shared images in a RecyclerView
        if (imageUris != null) {
            ((ImageView) findViewById(R.id.iv_image_will_be_printed)).setImageURI(imageUris.get(0));
            printImageReceived(imageUris.get(0));
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void printImageReceived(Uri imageUri) {
        Bitmap bitmap = convertUriToBitmap(imageUri);
        if (bitmap != null) {
            if (Util.isAllowToPrint()) {
                // Print the bitmap as
                Printama.with(this).connect(printama -> {
                    printama.printImage(bitmap, PW.FULL_WIDTH);

                    // Estimate printed height (FULL_WIDTH scaling)
                    int printerWidthDots = Printama.is3inchesPrinter() ? 576 : 384;
                    float scale = printerWidthDots / Math.max(1f, (float) bitmap.getWidth());
                    int scaledHeightDots = (int) (bitmap.getHeight() * scale);

                    // Give the printer time to finish before closing
                    long delayMs = Math.max(3000L, scaledHeightDots * 4L);

                    printama.addNewLine();
                    printama.closeAfter(delayMs);
                }, this::showToast);
            } else {
                showToast("print not allowed");
            }
        } else {
            // Handle the case where conversion failed
            showToast("failed to print image");
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

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}