package com.anggastudio.printama_sample;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.print.PrintAttributes;
import android.print.PrinterCapabilitiesInfo;
import android.print.PrinterId;
import android.print.PrinterInfo;
import android.printservice.PrintDocument;
import android.printservice.PrintJob;
import android.printservice.PrintService;
import android.printservice.PrinterDiscoverySession;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

import com.anggastudio.printama.Printama;
import com.anggastudio.printama.constants.PW;
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

public class PrintamaPrintService extends PrintService {
    private static final String PRINTER_LOCAL_ID = "printama"; // stable ID across callbacks
    private PrinterId myPrinterId;
    private PrinterInfo cachedPrinter;

    @Override
    protected PrinterDiscoverySession onCreatePrinterDiscoverySession() {

        SharedPref.init(getApplicationContext());
        return new PrinterDiscoverySession() {
            @Override
            public void onStartPrinterDiscovery(@NonNull List<PrinterId> priorityList) {
                Log.d("Print Service", "PrinterDiscoverySession#onStartPrinterDiscovery(priorityList: " + priorityList + ") called");

                // Use a stable PrinterId for this service (do NOT generate a new one each time)
                if (myPrinterId == null) {
                    myPrinterId = generatePrinterId(PRINTER_LOCAL_ID);
                }
                PrinterId printerId = myPrinterId;

                PrinterInfo.Builder builder = new PrinterInfo.Builder(printerId, "Printama", PrinterInfo.STATUS_IDLE);
                builder.setDescription("Print with Printama App");

                PrinterCapabilitiesInfo.Builder capBuilder = new PrinterCapabilitiesInfo.Builder(printerId);

                // Paper sizes (keep your current defaults)
                capBuilder.addMediaSize(new PrintAttributes.MediaSize("80mm", "80 mm roll (3 1/8\")", 3125, 10000), false);
                capBuilder.addMediaSize(new PrintAttributes.MediaSize("58mm", "58 mm roll (2 1/4\")", 2280, 10000), true);

                // Resolutions: 203 dpi default, 300 dpi optional
                capBuilder.addResolution(new PrintAttributes.Resolution("203dpi", "203 dpi", 203, 203), true);
                capBuilder.addResolution(new PrintAttributes.Resolution("300dpi", "300 dpi", 300, 300), false);

                // REQUIRED: specify color modes
                capBuilder.setColorModes(
                        PrintAttributes.COLOR_MODE_MONOCHROME,
                        PrintAttributes.COLOR_MODE_MONOCHROME
                );

                // Optional: explicit zero margins
                capBuilder.setMinMargins(PrintAttributes.Margins.NO_MARGINS);

                builder.setCapabilities(capBuilder.build());

                // Cache and announce the same printer
                cachedPrinter = builder.build();
                List<PrinterInfo> list = new ArrayList<>();
                list.add(cachedPrinter);
                addPrinters(list);
            }

            @Override
            public void onStopPrinterDiscovery() {
                Log.d("Printama Print Service", "MyPrintService#onStopPrinterDiscovery() called");
            }

            @Override
            public void onValidatePrinters(@NonNull List<PrinterId> printerIds) {
                Log.d("Printama Print Service", "MyPrintService#onValidatePrinters(printerIds: " + printerIds + ") called");
                // Re-announce during validation to keep it available
                if (cachedPrinter != null && myPrinterId != null && printerIds.contains(myPrinterId)) {
                    List<PrinterInfo> list = new ArrayList<>();
                    list.add(cachedPrinter);
                    addPrinters(list);
                }
            }

            @Override
            public void onStartPrinterStateTracking(@NonNull PrinterId printerId) {
                Log.d("Printama Print Service", "MyPrintService#onStartPrinterStateTracking(printerId: " + printerId + ") called");
                // Ensure it remains IDLE (available) when selected
                if (cachedPrinter != null && printerId.equals(myPrinterId)) {
                    PrinterInfo.Builder b = new PrinterInfo.Builder(cachedPrinter);
                    b.setStatus(PrinterInfo.STATUS_IDLE);
                    cachedPrinter = b.build();
                    List<PrinterInfo> list = new ArrayList<>();
                    list.add(cachedPrinter);
                    addPrinters(list);
                }
            }

            @Override
            public void onStopPrinterStateTracking(@NonNull PrinterId printerId) {
                Log.d("Printama Print Service", "MyPrintService#onStopPrinterStateTracking(printerId: " + printerId + ") called");
                // no-op: keep it discoverable/available
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

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Override
    protected void onPrintJobQueued(PrintJob printJob) {
        try {
            if (Util.isAllowToPrint()) {
                PrintDocument printDocument = printJob.getDocument();
                ParcelFileDescriptor pfd = printDocument.getData();
                if (pfd != null) {
                    PrintAttributes attrs = (printJob.getInfo() != null) ? printJob.getInfo().getAttributes() : null;

                    // Prefer the job's resolution if provided; otherwise default to 203 dpi
                    int chosenDpi = 203;
                    PrintAttributes.Resolution res = (attrs != null) ? attrs.getResolution() : null;
                    if (res != null) {
                        chosenDpi = res.getHorizontalDpi();
                    }
                    Log.d("PrintService", "Chosen DPI: " + chosenDpi);

                    PrintAttributes.MediaSize media = (attrs != null) ? attrs.getMediaSize() : null;
                    boolean is80mm = (media != null) ? media.getWidthMils() >= 3000 : true;

                    int targetWidthPx = is80mm
                            ? ((chosenDpi >= 300) ? 832 : 576)
                            : ((chosenDpi >= 300) ? 576 : 384);

                    // Convert with fallback for non-seekable descriptors
                    List<Bitmap> bitmaps = convertPdfToBitmaps(pfd, targetWidthPx);

                    if (!bitmaps.isEmpty()) {
                        printBitmap(bitmaps, printJob);
                    } else {
                        Log.w("PrintService", "Failed to convert PDF to Bitmap");
                        printJob.fail("No printable content found");
                    }
                } else {
                    Log.w("PrintService", "No print data");
                    printJob.fail("No data to print");
                }
            } else {
                Log.w("PrintService", "Print not allowed");
                printJob.cancel();
            }
        } catch (Exception e) {
            Log.e("PrintService", "Error processing print job", e);
            // Avoid toast here to prevent SystemUI asset path error
            printJob.cancel();
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void printBitmap(List<Bitmap> bitmaps, PrintJob printJob) {
        Printama.with(this).connect(printama -> {
            boolean is3Inch = Printama.is3inchesPrinter();
            int printerWidthDots = is3Inch ? 576 : 384;

            long totalScaledHeightDots = 0L;

            for (Bitmap bitmap : bitmaps) {
                printama.printImage(bitmap, PW.FULL_WIDTH);

                // Estimate height after FULL_WIDTH scaling
                float scale = printerWidthDots / Math.max(1f, (float) bitmap.getWidth());
                long scaledHeightDots = (long) Math.ceil(bitmap.getHeight() * scale);
                totalScaledHeightDots += scaledHeightDots;
            }

            // Heuristic delay: ~4 ms per dot + base 3s, capped at 20s
            long delayMs = Math.max(3000L, totalScaledHeightDots * 4L);
            delayMs = Math.min(delayMs, 20000L);

            printama.addNewLine();
            printama.closeAfter(delayMs);

            // Mark job complete once submitted and close scheduled
            printJob.complete();
        }, errorMsg -> {
            showToast(errorMsg);
            printJob.fail(errorMsg);
        });
    }

    // Helper inside PrintamaPrintService
    private void showToast(String message) {
        // Suppressed in service to avoid SystemUI toast asset path errors on some OEMs
        Log.d("PrintService", "Toast suppressed: " + message);
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

    // Try to render directly from PFD; if it isn't seekable, copy to a temp file and render from there.
    private List<Bitmap> convertPdfToBitmaps(ParcelFileDescriptor pfd, int targetWidthPx) throws IOException {
        try {
            return renderPdfToBitmaps(pfd, targetWidthPx);
        } catch (IllegalArgumentException | IllegalStateException e) {
            String msg = String.valueOf(e.getMessage()).toLowerCase();
            if (msg.contains("seekable")) {
                // Fall back to a temp file for non-seekable descriptors
                ParcelFileDescriptor tmpPfd = null;
                try {
                    tmpPfd = writePfdToTempFile(pfd);
                    return renderPdfToBitmaps(tmpPfd, targetWidthPx);
                } finally {
                    if (tmpPfd != null) {
                        try {
                            tmpPfd.close();
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
            throw e;
        } catch (IOException e) {
            // Some devices throw IOException instead; also fall back
            ParcelFileDescriptor tmpPfd = null;
            try {
                tmpPfd = writePfdToTempFile(pfd);
                return renderPdfToBitmaps(tmpPfd, targetWidthPx);
            } finally {
                if (tmpPfd != null) {
                    try {
                        tmpPfd.close();
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    // Render pages at target width with print-quality mode, and trim near-white side margins.
    private List<Bitmap> renderPdfToBitmaps(ParcelFileDescriptor sourcePfd, int targetWidthPx) throws IOException {
        List<Bitmap> bitmaps = new ArrayList<>();
        PdfRenderer renderer = null;
        ParcelFileDescriptor dupPfd = null;
        try {
            dupPfd = ParcelFileDescriptor.dup(sourcePfd.getFileDescriptor());
            renderer = new PdfRenderer(dupPfd);

            for (int pageIndex = 0; pageIndex < renderer.getPageCount(); pageIndex++) {
                PdfRenderer.Page page = renderer.openPage(pageIndex);

                int srcW = Math.max(1, page.getWidth());
                int srcH = Math.max(1, page.getHeight());
                int targetHeightPx = Math.max(1, Math.round((srcH / (float) srcW) * targetWidthPx));

                Bitmap bitmap = Bitmap.createBitmap(targetWidthPx, targetHeightPx, Bitmap.Config.ARGB_8888);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT);

                Rect visibleBounds = findVisibleContentBounds(bitmap);
                Bitmap croppedBitmap = (visibleBounds.width() > 0 && visibleBounds.height() > 0)
                        ? cropAndEnlargeBitmap(bitmap, visibleBounds)
                        : bitmap;

                bitmaps.add(croppedBitmap);
                if (croppedBitmap != bitmap) {
                    bitmap.recycle();
                }

                page.close();
            }
        } finally {
            if (renderer != null) {
                try {
                    renderer.close();
                } catch (Exception ignored) {
                }
            }
            if (dupPfd != null) {
                try {
                    dupPfd.close();
                } catch (Exception ignored) {
                }
            }
        }
        return bitmaps;
    }

    // Copy PDF bytes from the (possibly non-seekable) PFD into a temp file, then return a readable PFD.
    private ParcelFileDescriptor writePfdToTempFile(ParcelFileDescriptor src) throws IOException {
        File tmp = File.createTempFile("printama_spool_", ".pdf", getCacheDir());
        // Best-effort cleanup if the process terminates
        tmp.deleteOnExit();

        try (FileInputStream in = new FileInputStream(src.getFileDescriptor());
             FileOutputStream out = new FileOutputStream(tmp)) {
            byte[] buffer = new byte[8192];
            int n;
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
            out.flush();
        }
        return ParcelFileDescriptor.open(tmp, ParcelFileDescriptor.MODE_READ_ONLY);
    }

    private Bitmap cropAndEnlargeBitmap(Bitmap originalBitmap, Rect visibleBounds) {
        // Return the cropped content only; scaling to full width is handled by PW.FULL_WIDTH
        return Bitmap.createBitmap(
                originalBitmap,
                visibleBounds.left,
                visibleBounds.top,
                visibleBounds.width(),
                visibleBounds.height()
        );
    }

    private Rect findVisibleContentBounds(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int left = 0;
        int right = width - 1;

        // Threshold for considering a column as "blank" (near-white)
        // 0..255 where higher = stricter (closer to pure white)
        final int whiteThreshold = 245;

        // Scan from left to find the left visible column
        while (left < width && isBlankColumn(bitmap, left, whiteThreshold)) {
            left++;
        }

        // Scan from right to find the right visible column
        while (right > left && isBlankColumn(bitmap, right, whiteThreshold)) {
            right--;
        }

        return new Rect(left, 0, right + 1, height);
    }

    private boolean isBlankColumn(Bitmap bitmap, int x, int whiteThreshold) {
        int height = bitmap.getHeight();
        for (int y = 0; y < height; y++) {
            int color = bitmap.getPixel(x, y);
            int a = (color >>> 24) & 0xFF;
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;

            // Transparent pixels are blank; opaque pixels must be near-white to be considered blank
            if (a > 10) {
                // Perceptual luminance
                int luminance = (int) (0.299f * r + 0.587f * g + 0.114f * b);
                if (luminance < whiteThreshold) {
                    return false; // Found "ink"
                }
            }
        }
        return true;
    }
}
