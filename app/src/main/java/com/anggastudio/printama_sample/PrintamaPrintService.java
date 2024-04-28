package com.anggastudio.printama_sample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.print.PrintAttributes;
import android.print.PrinterCapabilitiesInfo;
import android.print.PrinterId;
import android.print.PrinterInfo;
import android.printservice.PrintDocument;
import android.printservice.PrintService;
import android.printservice.PrinterDiscoverySession;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;


import com.anggastudio.printama.Printama;
import com.anggastudio.printama_sample.util.SharedPref;
import com.anggastudio.printama_sample.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class PrintamaPrintService extends PrintService {

    @Override
    protected PrinterDiscoverySession onCreatePrinterDiscoverySession() {

        SharedPref.init(getApplicationContext());
        return new PrinterDiscoverySession() {
            @Override
            public void onStartPrinterDiscovery(@NonNull List<PrinterId> priorityList) {
                Log.d("Print Service", "PrinterDiscoverySession#onStartPrinterDiscovery(priorityList: " + priorityList + ") called");

                List<PrinterInfo> printers = new ArrayList<>();

                PrinterId printerId = generatePrinterId("1312");
                boolean isExist = false;
                if (!printers.isEmpty()) {
                    for (PrinterInfo printer : printers) {
                        isExist = printer.getId().equals(printerId.getLocalId());
                    }
                }

                if (isExist) return;

                PrinterInfo.Builder builder = new PrinterInfo.Builder(printerId, "Printama", PrinterInfo.STATUS_IDLE);
                builder.setDescription("Print with Printama App");

                PrinterCapabilitiesInfo.Builder capBuilder = new PrinterCapabilitiesInfo.Builder(printerId);

                capBuilder.addMediaSize(new PrintAttributes.MediaSize("3inch", "3\" roll", 3800, 10000), false);
                capBuilder.addMediaSize(new PrintAttributes.MediaSize("2inch", "2\" roll", 2800, 10000), true);

                capBuilder.addResolution(new PrintAttributes.Resolution("resolutionId", "default resolution", 609, 609), true);

                capBuilder.setColorModes(
                        PrintAttributes.COLOR_MODE_MONOCHROME,
                        PrintAttributes.COLOR_MODE_MONOCHROME
                );

                builder.setCapabilities(capBuilder.build());
                printers.add(builder.build());
                addPrinters(printers);
            }

            @Override
            public void onStopPrinterDiscovery() {
                Log.d("Printama Print Service", "MyPrintService#onStopPrinterDiscovery() called");
            }

            @Override
            public void onValidatePrinters(@NonNull List<PrinterId> printerIds) {
                Log.d("Printama Print Service", "MyPrintService#onValidatePrinters(printerIds: " + printerIds + ") called");
            }

            @Override
            public void onStartPrinterStateTracking(@NonNull PrinterId printerId) {
                Log.d("Printama Print Service", "MyPrintService#onStartPrinterStateTracking(printerId: " + printerId + ") called");
            }

            @Override
            public void onStopPrinterStateTracking(@NonNull PrinterId printerId) {
                Log.d("Printama Print Service", "MyPrintService#onStopPrinterStateTracking(printerId: " + printerId + ") called");
            }

            @Override
            public void onDestroy() {
                Log.d("Printama Print Service", "MyPrintService#onDestroy() called");
            }
        };
    }

    @Override
    protected void onRequestCancelPrintJob(android.printservice.PrintJob printJob) {
        Log.d("Printama Print Service", "canceled: " + (printJob != null ? printJob.getId() : "null"));

        if (printJob != null) {
            printJob.cancel();
        }
    }

    @Override
    protected void onPrintJobQueued(android.printservice.PrintJob printJob) {
        try {
            if (Util.isAllowToPrint()) {
                PrintDocument printDocument = printJob.getDocument();
                if (printDocument != null && printDocument.getData() != null && printDocument.getData().getFileDescriptor() != null) {
                    FileInputStream inputStream = new FileInputStream(printDocument.getData().getFileDescriptor());

                    // Convert the PDF data to a list of bitmaps
                    List<Bitmap> bitmaps = convertPdfToBitmaps(inputStream);

                    // Check if bitmaps list is not empty
                    if (!bitmaps.isEmpty()) {
                        // Print each bitmap
                        printBitmap(bitmaps);
                    } else {
                        showToast("Failed to convert PDF to Bitmap");
                    }

                    // Close the input stream
                    inputStream.close();
                }
                showToast("Print success");
            } else {
                showToast("Print not allowed");
            }
            printJob.complete();
        } catch (Exception e) {
            Log.e("PrintService", "Error processing print job", e);
            showToast("Error processing print job: " + e.getMessage());
            printJob.cancel();
        }
    }

    private void printBitmap(List<Bitmap> bitmaps) {
        Printama.with(this).connect(printama -> {
            for (Bitmap bitmap : bitmaps) {
                printama.printImage(bitmap, Printama.FULL_WIDTH);
            }
            printama.close();
        }, this::showToast);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public Bitmap convertInputStreamToBitmap(FileInputStream inputStream) throws IOException {
        // Convert input stream to byte array
        byte[] data = convertInputStreamToByteArray(inputStream);

        // Decode byte array into a Bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888; // Adjust based on your requirements
        options.inSampleSize = 1; // Adjust based on your requirements
        options.inJustDecodeBounds = false; // Set to true to only calculate dimensions
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);

        // Log the dimensions of the bitmap
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Log.d("PrintService", "Bitmap dimensions: " + width + "x" + height);
        } else {
            Log.d("PrintService", "Failed to decode bitmap");
        }

        // Create Bitmap from byte array
        return bitmap;
    }

    private byte[] convertInputStreamToByteArray(FileInputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[1024];

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        return buffer.toByteArray();
    }

    public Bitmap renderPdfPageToBitmap(InputStream inputStream, int pageNumber) {
        try {
            // Convert InputStream to ParcelFileDescriptor
            FileDescriptor fileDescriptor = ((FileInputStream) inputStream).getFD();
            ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.dup(fileDescriptor);

            PdfRenderer renderer = new PdfRenderer(parcelFileDescriptor);
            PdfRenderer.Page page = renderer.openPage(pageNumber);

            // Create a bitmap with the same dimensions as the PDF page
            Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);

            // Render the PDF page onto the bitmap
            Rect rect = new Rect(0, 0, page.getWidth(), page.getHeight());
            page.render(bitmap, rect, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            // Close the page and renderer
            page.close();
            renderer.close();

            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Bitmap> convertPdfToBitmaps(InputStream inputStream) {
        List<Bitmap> bitmaps = new ArrayList<>();
        PdfRenderer renderer = null;
        FileOutputStream outputStream = null;
        ParcelFileDescriptor fileDescriptor = null;
        try {
            // Create a temporary file to store the PDF data
            File tempFile = File.createTempFile("temp_pdf", null);
            outputStream = new FileOutputStream(tempFile);

            // Copy the contents of the InputStream to the temporary file
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // Close the OutputStream
            outputStream.close();

            // Get a ParcelFileDescriptor for the temporary file
            fileDescriptor = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY);

            // Open the PDF document using PdfRenderer
            renderer = new PdfRenderer(fileDescriptor);

            // Iterate through each page of the PDF document
            for (int pageIndex = 0; pageIndex < renderer.getPageCount(); pageIndex++) {
                // Get the page at the specified index
                PdfRenderer.Page page = renderer.openPage(pageIndex);

                // Create a bitmap to render the PDF page
                Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);

                // Render the PDF page onto the bitmap
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                // Find the visible content bounds by scanning the bitmap
                Rect visibleBounds = findVisibleContentBounds(bitmap);

// Crop the bitmap to include only the visible content
                Bitmap croppedBitmap = cropAndEnlargeBitmap(bitmap, visibleBounds);

                // Add the bitmap to the list
                bitmaps.add(bitmap);

                // Close the PDF page
                page.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close the PdfRenderer to release resources
            if (renderer != null) {
                renderer.close();
            }
            // Close the ParcelFileDescriptor
            if (fileDescriptor != null) {
                try {
                    fileDescriptor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // Delete the temporary file
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmaps;
    }

    private Bitmap cropAndEnlargeBitmap(Bitmap originalBitmap, Rect visibleBounds) {
        int originalWidth = originalBitmap.getWidth();
        int originalHeight = originalBitmap.getHeight();

        // Calculate the width of the visible content
        int visibleWidth = visibleBounds.width();

        // Create a new bitmap with the original width and the height of the visible content
        Bitmap enlargedBitmap = Bitmap.createBitmap(originalWidth, visibleBounds.height(), Bitmap.Config.ARGB_8888);

        // Create a canvas with the new bitmap
        Canvas canvas = new Canvas(enlargedBitmap);

        // Calculate the horizontal offset to align the visible content to the left
        int xOffset = (originalWidth - visibleWidth) / 2;

        // Draw the visible content onto the new bitmap, aligning it to the left
        canvas.drawBitmap(originalBitmap, new Rect(visibleBounds.left, visibleBounds.top, visibleBounds.right, visibleBounds.bottom),
                new Rect(xOffset, 0, xOffset + visibleWidth, visibleBounds.height()), null);

        return enlargedBitmap;
    }

    private Rect findVisibleContentBounds(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int left = 0;
        int right = width - 1;

        // Scan from left to find the left visible column
        while (left < width && isBlankColumn(bitmap, left)) {
            left++;
        }

        // Scan from right to find the right visible column
        while (right > left && isBlankColumn(bitmap, right)) {
            right--;
        }

        // Return the visible content bounds
        return new Rect(left, 0, right + 1, height);
    }

    private boolean isBlankColumn(Bitmap bitmap, int x) {
        int height = bitmap.getHeight();
        for (int y = 0; y < height; y++) {
            if ((bitmap.getPixel(x, y) & 0xff000000) != 0) {
                return false;
            }
        }
        return true;
    }
}
