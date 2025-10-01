package com.anggastudio.printama_sample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

    private static final int PICK_IMAGE_REQUEST = 1001;
    
    private Uri imageUri = null;
    private ArrayList<Uri> imageUris = new ArrayList<>();
    private Bitmap originalBitmap = null;
    private FrameLayout imageViewContainer;
    private ImageView imageView;
    private Switch trimWhitespaceSwitch;
    private SeekBar heightSeekBar;
    private TextView heightPercentageText;
    private Button printButton;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

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

        initializeImagePicker();
        initializeViews();
        setupListeners();
    }

    private void initializeImagePicker() {
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        loadImageFromUri(selectedImageUri);
                    }
                }
            }
        );
    }

    private void initializeViews() {
        imageViewContainer = findViewById(R.id.iv_image_will_be_printed_container);
        imageView = findViewById(R.id.iv_image_will_be_printed);
        trimWhitespaceSwitch = findViewById(R.id.switch_trim_whitespace);
        heightSeekBar = findViewById(R.id.seekbar_height);
        heightPercentageText = findViewById(R.id.tv_height_percentage);
        printButton = findViewById(R.id.btn_print);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void setupListeners() {
        printButton.setOnClickListener(view -> printCurrentImage());
        imageViewContainer.setOnClickListener(view -> openImagePicker());
        imageView.setOnClickListener(view -> openImagePicker());

        trimWhitespaceSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> updatePreview());
        
        heightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    updateHeightPercentageText(progress);
                    updatePreview();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void updateHeightPercentageText(int progress) {
        int percentage = (int) (progress * 0.5f + 50); // Convert 0-200 to 50-150
        heightPercentageText.setText(percentage + "%");
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private void checkSharedImageIntent() {
        Intent intent = getIntent();
        if (Intent.ACTION_SEND.equals(intent.getAction()) && intent.getType() != null && intent.getType().startsWith("image/")) {
            handleSharedImage(intent);
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(intent.getAction()) && intent.getType() != null && intent.getType().startsWith("image/")) {
            handleSharedImages(intent);
        }
    }

    private void handleSharedImage(Intent intent) {
        imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            loadImageFromUri(imageUri);
        }
    }

    private void handleSharedImages(Intent intent) {
        imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null && !imageUris.isEmpty()) {
            loadImageFromUri(imageUris.get(0));
        }
    }

    private void loadImageFromUri(Uri uri) {
        originalBitmap = convertUriToBitmap(uri);
        if (originalBitmap != null) {
            // Apply orientation correction based on EXIF data
            originalBitmap = correctImageOrientation(originalBitmap, uri);
            updatePreview();
        } else {
            showToast("Failed to load image");
        }
    }

    private void updatePreview() {
        if (originalBitmap == null) return;

        Bitmap processedBitmap = originalBitmap;
        
        // Apply whitespace trimming if enabled
        if (trimWhitespaceSwitch.isChecked()) {
            processedBitmap = trimWhitespace(processedBitmap);
        }
        
        // Apply height scaling
        int progress = heightSeekBar.getProgress();
        float scale = progress * 0.005f + 0.5f; // Convert 0-200 to 0.5-1.5 (50%-150%)
        if (scale != 1f) {
            int newHeight = Math.max(1, (int) (processedBitmap.getHeight() * scale));
            processedBitmap = Bitmap.createScaledBitmap(processedBitmap, processedBitmap.getWidth(), newHeight, true);
        }
        
        imageView.setImageBitmap(processedBitmap);
    }

    private Bitmap trimWhitespace(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        
        int top = 0, bottom = height - 1, left = 0, right = width - 1;
        
        // Find top boundary
        outerTop:
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (bitmap.getPixel(x, y) != Color.WHITE && (bitmap.getPixel(x, y) & 0xFF000000) != 0) {
                    top = y;
                    break outerTop;
                }
            }
        }
        
        // Find bottom boundary
        outerBottom:
        for (int y = height - 1; y >= top; y--) {
            for (int x = 0; x < width; x++) {
                if (bitmap.getPixel(x, y) != Color.WHITE && (bitmap.getPixel(x, y) & 0xFF000000) != 0) {
                    bottom = y;
                    break outerBottom;
                }
            }
        }
        
        // Find left boundary
        outerLeft:
        for (int x = 0; x < width; x++) {
            for (int y = top; y <= bottom; y++) {
                if (bitmap.getPixel(x, y) != Color.WHITE && (bitmap.getPixel(x, y) & 0xFF000000) != 0) {
                    left = x;
                    break outerLeft;
                }
            }
        }
        
        // Find right boundary
        outerRight:
        for (int x = width - 1; x >= left; x--) {
            for (int y = top; y <= bottom; y++) {
                if (bitmap.getPixel(x, y) != Color.WHITE && (bitmap.getPixel(x, y) & 0xFF000000) != 0) {
                    right = x;
                    break outerRight;
                }
            }
        }
        
        // Create cropped bitmap
        if (top < bottom && left < right) {
            return Bitmap.createBitmap(bitmap, left, top, right - left + 1, bottom - top + 1);
        }
        
        return bitmap; // Return original if no content found
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void printCurrentImage() {
        if (originalBitmap == null) {
            showToast("No image to print!");
            return;
        }

        // Get the processed bitmap (same as preview)
        Bitmap processedBitmap = originalBitmap;
        
        if (trimWhitespaceSwitch.isChecked()) {
            processedBitmap = trimWhitespace(processedBitmap);
        }
        
        int progress = heightSeekBar.getProgress();
        float scale = progress * 0.005f + 0.5f; // Convert 0-200 to 0.5-1.5 (50%-150%)
        if (scale != 1f) {
            int newHeight = Math.max(1, (int) (processedBitmap.getHeight() * scale));
            processedBitmap = Bitmap.createScaledBitmap(processedBitmap, processedBitmap.getWidth(), newHeight, true);
        }

        // Make the bitmap final for use in lambda
        final Bitmap finalProcessedBitmap = processedBitmap;

        if (Util.isAllowToPrint()) {
            Printama.with(this).connect(printama -> {
                // Use FULL_WIDTH only if trimming is enabled, otherwise use ORIGINAL_WIDTH to prevent auto-trimming
                int printWidth = trimWhitespaceSwitch.isChecked() ? PW.FULL_WIDTH : PW.ORIGINAL_WIDTH;
                printama.printImage(finalProcessedBitmap, printWidth);

                // Estimate printed height based on the print width used
                int printerWidthDots = Printama.is3inchesPrinter() ? 576 : 384;
                float printScale;
                if (printWidth == PW.FULL_WIDTH) {
                    printScale = printerWidthDots / Math.max(1f, (float) finalProcessedBitmap.getWidth());
                } else {
                    // For ORIGINAL_WIDTH, no scaling is applied
                    printScale = 1f;
                }
                int scaledHeightDots = (int) (finalProcessedBitmap.getHeight() * printScale);

                // Give the printer time to finish before closing
                long delayMs = Math.max(3000L, scaledHeightDots * 4L);

                printama.addNewLine();
                printama.closeAfter(delayMs);
            }, this::showToast);
        } else {
            showToast("Print not allowed");
        }
    }

    public Bitmap convertUriToBitmap(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private Bitmap correctImageOrientation(Bitmap bitmap, Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                ExifInterface exif = new ExifInterface(inputStream);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                inputStream.close();
                
                Matrix matrix = new Matrix();
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                        break;
                    case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                        matrix.postScale(-1, 1);
                        break;
                    case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                        matrix.postScale(1, -1);
                        break;
                    case ExifInterface.ORIENTATION_TRANSPOSE:
                        matrix.postRotate(90);
                        matrix.postScale(-1, 1);
                        break;
                    case ExifInterface.ORIENTATION_TRANSVERSE:
                        matrix.postRotate(270);
                        matrix.postScale(-1, 1);
                        break;
                    default:
                        return bitmap; // No rotation needed
                }
                
                return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap; // Return original if error occurs
    }
}