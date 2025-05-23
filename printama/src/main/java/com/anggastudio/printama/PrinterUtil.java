package com.anggastudio.printama;

import static com.anggastudio.printama.Printama.CENTER;
import static com.anggastudio.printama.Printama.FULL_WIDTH;
import static com.anggastudio.printama.Printama.ORIGINAL_WIDTH;
import static com.anggastudio.printama.Printama.RIGHT;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

class PrinterUtil {
    private static final String TAG = "PRINTAMA";
    private static final int INITIAL_MARGIN_LEFT = -5;
    private static final int PRINTER_WIDTH_2_INCH = 384; // 2-inch (58mm) printer
    private static final int PRINTER_WIDTH_3_INCH = 576; // 3-inch (80mm) printer
    private static final int MAX_CHAR_2_INCH = 32;
    private static final int MAX_CHAR_3_INCH = 48;
    private static final int WIDTH_2_INCH = 48;  // 384/8 = 48 bytes per line
    private static final int WIDTH_3_INCH = 72;  // 576/8 = 72 bytes per line
    private static final int HEAD = 8;
    // printer commands
    private static final byte[] NEW_LINE = {10};
    private static final byte[] ESC_ALIGN_CENTER = {0x1b, 'a', 0x01};
    private static final byte[] ESC_ALIGN_RIGHT = {0x1b, 'a', 0x02};
    private static final byte[] ESC_ALIGN_LEFT = {0x1b, 'a', 0x00};
    private static final byte[] FEED_PAPER_AND_CUT = {0x1D, 0x56, 66, 0x00};

    private static final byte[] SMALL = new byte[]{0x1B, 0x21, 0x01};
    private static final byte[] NORMAL = new byte[]{0x1B, 0x21, 0x00};
    private static final byte[] BOLD = new byte[]{0x1B, 0x21, 0x08};
    private static final byte[] WIDE = new byte[]{0x1B, 0x21, 0x20};
    private static final byte[] TALL = new byte[]{0x1B, 0x21, 0x10};
    private static final byte[] UNDERLINE = new byte[]{0x1B, 0x21, (byte) 0x80};
    private static final byte[] DELETE_LINE = new byte[]{0x1B, 0x21, (byte) 0x40};
    private static final byte[] WIDE_BOLD = new byte[]{0x1B, 0x21, 0x20 | 0x08};
    private static final byte[] TALL_BOLD = new byte[]{0x1B, 0x21, 0x10 | 0x08};
    private static final byte[] WIDE_TALL = new byte[]{0x1B, 0x21, 0x20 | 0x10};
    private static final byte[] WIDE_TALL_BOLD = new byte[]{0x1B, 0x21, 0x20 | 0x10 | 0x08};

    private final BluetoothDevice printer;
    private BluetoothSocket btSocket = null;
    private OutputStream btOutputStream = null;
    private boolean is3InchPrinter;

    PrinterUtil(BluetoothDevice printer) {
        this.printer = printer;
    }

    void connectPrinter(final PrinterConnected successListener, PrinterConnectFailed failedListener) {
        new ConnectAsyncTask(new ConnectAsyncTask.ConnectionListener() {
            @Override
            public void onConnected(BluetoothSocket socket) {
                btSocket = socket;
                try {
                    btOutputStream = socket.getOutputStream();
                    successListener.onConnected();
                } catch (IOException e) {
                    failedListener.onFailed();
                }
            }

            @Override
            public void onFailed() {
                failedListener.onFailed();
            }
        }).execute(printer);
    }

    boolean isConnected() {
        return btSocket != null && btSocket.isConnected();
    }

    void finish() {
        if (btSocket != null) {
            try {
                btOutputStream.close();
                btSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            btSocket = null;
        }
    }

    private boolean printUnicode(byte[] data) {
        try {
            btOutputStream.write(data);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    //----------------------------------------------------------------------------------------------
    // PRINT TEXT
    //----------------------------------------------------------------------------------------------

    boolean printText(String text) {
        try {
            String s = StrUtil.encodeNonAscii(text);
            btOutputStream.write(s.getBytes());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    void setNormalText() {
        printUnicode(NORMAL);
    }

    void setSmallText() {
        printUnicode(SMALL);
    }

    void setBold() {
        printUnicode(BOLD);
    }

    void setUnderline() {
        printUnicode(UNDERLINE);
    }

    void setDeleteLine() {
        printUnicode(DELETE_LINE);
    }

    void setTall() {
        printUnicode(TALL);
    }

    void setWide() {
        printUnicode(WIDE);
    }

    void setWideBold() {
        printUnicode(WIDE_BOLD);
    }

    void setTallBold() {
        printUnicode(TALL_BOLD);
    }

    void setWideTall() {
        printUnicode(WIDE_TALL);
    }

    void setWideTallBold() {
        printUnicode(WIDE_TALL_BOLD);
    }

    void printEndPaper() {
        printUnicode(FEED_PAPER_AND_CUT);
    }

    boolean addNewLine() {
        return printUnicode(NEW_LINE);
    }

    int addNewLine(int count) {
        int success = 0;
        for (int i = 0; i < count; i++) {
            if (addNewLine()) success++;
        }
        return success;
    }

    void setAlign(int alignType) {
        byte[] d;
        switch (alignType) {
            case CENTER:
                d = ESC_ALIGN_CENTER;
                break;
            case RIGHT:
                d = ESC_ALIGN_RIGHT;
                break;
            default:
                d = ESC_ALIGN_LEFT;
                break;
        }
        try {
            btOutputStream.write(d);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setLineSpacing(int lineSpacing) {
        byte[] cmd = new byte[]{0x1B, 0x33, (byte) lineSpacing};
        printUnicode(cmd);
    }

    //----------------------------------------------------------------------------------------------
    // PRINT IMAGE
    //----------------------------------------------------------------------------------------------

    boolean printImage(Bitmap bitmap) {
        try {
            int width = bitmap.getWidth() > getPrinterWidth() ? FULL_WIDTH : ORIGINAL_WIDTH;
            return printImage(Printama.CENTER, bitmap, width);
        } catch (NullPointerException e) {
            Log.e(TAG, "Maybe resource is vector or mipmap?");
            return false;
        }
    }

    boolean printImage(Bitmap bitmap, int width) {
        return printImage(Printama.CENTER, bitmap, width);
    }

    boolean printImage(int alignment, Bitmap bitmap, int width) {
        Bitmap scaledBitmap = scaledBitmap(bitmap, width);
        if (scaledBitmap != null) {
            int marginLeft = 0;
            if (alignment == CENTER) {
                marginLeft = (getPrinterWidth() - scaledBitmap.getWidth()) / 2;
            } else if (alignment == RIGHT) {
                marginLeft = getPrinterWidth() - scaledBitmap.getWidth();
            }
            
            // Calculate correct width in bytes for printer command
            int widthBytes = getLineWidth();
            int lines = scaledBitmap.getHeight();
    
            byte[] command = autoGrayScale(scaledBitmap, marginLeft, 5);
            
            // Fix: Correct printer command header for both printer sizes
            System.arraycopy(new byte[]{
                    0x1D, 0x76, 0x30, 0x00,
                    (byte) (widthBytes & 0xff),  // Width in bytes (low byte)
                    (byte) ((widthBytes >> 8) & 0xff),  // Width in bytes (high byte)
                    (byte) (lines & 0xff),  // Height (low byte)
                    (byte) ((lines >> 8) & 0xff)  // Height (high byte)
            }, 0, command, 0, HEAD);
    
            // Convert GS v 0 command to ESC * commands for better compatibility
            byte[][] commandLines = convertGSv0ToEscAsterisk(command);
            
            // Print each line of the image
            boolean success = true;
            for (byte[] line : commandLines) {
                if (!printUnicode(line)) {
                    success = false;
                    break;
                }
            }
            
            // Add a command to reset the printer state after image printing
            if (success) {
                printUnicode(new byte[]{0x1B, 0x40}); // ESC @ command to initialize printer
            }
            
            return success;
        } else {
            return false;
        }
    }

    private byte[] autoGrayScale(Bitmap bm, int bitMarginLeft, int bitMarginTop) {
        byte[] result;
        int n = bm.getHeight() + bitMarginTop;
        int offset = HEAD;
        int lineWidth = getLineWidth();
        result = new byte[n * lineWidth + offset];
        Arrays.fill(result, (byte) 0x00); // Initialize all bytes to zero

        // Create a temporary array to hold grayscale values
        int[][] grayPixels = new int[bm.getHeight()][bm.getWidth()];

        // First pass: Convert to grayscale and store values
        for (int y = 0; y < bm.getHeight(); y++) {
            for (int x = 0; x < bm.getWidth(); x++) {
                int color = bm.getPixel(x, y);
                int alpha = Color.alpha(color);
                if (alpha < 128) {
                    grayPixels[y][x] = 255; // Treat transparent as white
                    continue;
                }

                // Convert to grayscale using luminance formula
                int red = Color.red(color);
                int green = Color.green(color);
                int blue = Color.blue(color);
                int gray = (int) (red * 0.299 + green * 0.587 + blue * 0.114);
                gray = Math.max(0, Math.min(255, gray));
                grayPixels[y][x] = gray;
            }
        }

        // Second pass: Apply dithering and convert to binary
        for (int y = 0; y < bm.getHeight(); y++) {
            for (int x = 0; x < bm.getWidth(); x++) {
                // Skip pixels that would be outside the printer width
                if (x + bitMarginLeft >= getPrinterWidth()) {
                    continue;
                }

                int oldPixel = Math.max(0, Math.min(255, grayPixels[y][x]));
                int newPixel = oldPixel > 128 ? 255 : 0;  // 128 threshold

                // If dark pixel, set it in result array
                if (newPixel == 0) {
                    int bitX = bitMarginLeft + x;
                    int byteX = bitX / 8;
                    int byteY = y + bitMarginTop;
                    int bitOffset = 7 - (bitX % 8);  // Bit position within byte

                    // Ensure we don't exceed the line width
                    if (byteX < lineWidth) {
                        int byteIndex = offset + byteY * lineWidth + byteX;

                        if (byteIndex < result.length) {
                            result[byteIndex] |= (1 << bitOffset);
                        }
                    }
                }

                // Apply error diffusion with proper bounds checking
                int error = oldPixel - newPixel;

                // Distribute error to neighboring pixels using Floyd-Steinberg dithering
                if (x + 1 < bm.getWidth()) {
                    grayPixels[y][x + 1] = Math.max(0, Math.min(255,
                            grayPixels[y][x + 1] + (error * 7 / 16)));
                }

                if (y + 1 < bm.getHeight()) {
                    if (x > 0) {
                        grayPixels[y + 1][x - 1] = Math.max(0, Math.min(255,
                                grayPixels[y + 1][x - 1] + (error * 3 / 16)));
                    }

                    grayPixels[y + 1][x] = Math.max(0, Math.min(255,
                            grayPixels[y + 1][x] + (error * 5 / 16)));

                    if (x + 1 < bm.getWidth()) {
                        grayPixels[y + 1][x + 1] = Math.max(0, Math.min(255,
                                grayPixels[y + 1][x + 1] + (error * 1 / 16)));
                    }
                }
            }
        }

        // Ensure the end of the image data is properly terminated
        // Add padding zeros at the end to ensure clean termination
        for (int i = offset + n * lineWidth - lineWidth; i < result.length; i++) {
            result[i] = 0x00;
        }

        return result;
    }

    private Bitmap scaledBitmap(Bitmap bitmap, int width) {
        try {
            int printerWidth = getPrinterWidth();
            int desiredWidth = getDesiredWidth(bitmap, width, printerWidth);

            float scale = (float) desiredWidth / (float) bitmap.getWidth();
            int height = (int) (bitmap.getHeight() * scale);
            return Bitmap.createScaledBitmap(bitmap, desiredWidth, height, true);
        } catch (NullPointerException e) {
            Log.e(TAG, "Maybe resource is vector or mipmap?");
            return null;
        }
    }

    private static int getDesiredWidth(Bitmap bitmap, int width, int printerWidth) {
        int desiredWidth;

        if (width == FULL_WIDTH || width >= printerWidth) {
            // Always use full printer width when requested or when width exceeds printer capacity
            desiredWidth = printerWidth;
        } else if (width > 0) {
            // Use specified width if it's positive and within printer limits
            desiredWidth = width;
        } else {
            // For width <= 0, scale to printer width if original exceeds it
            desiredWidth = Math.min(bitmap.getWidth(), printerWidth);
        }
        return desiredWidth;
    }

    void feedPaper() {
        addNewLine();
        addNewLine();
        addNewLine();
        addNewLine();
    }

    public int getMaxChar() {
        return is3InchPrinter ? MAX_CHAR_3_INCH : MAX_CHAR_2_INCH;
    }

    private static class ConnectAsyncTask extends AsyncTask<BluetoothDevice, Void, BluetoothSocket> {
        private final ConnectionListener listener;

        private ConnectAsyncTask(ConnectionListener listener) {
            this.listener = listener;
        }

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        @Override
        protected BluetoothSocket doInBackground(BluetoothDevice... bluetoothDevices) {
            BluetoothDevice device = bluetoothDevices[0];
            UUID uuid;
            if (device != null) {
                ParcelUuid[] uuids = device.getUuids();
                uuid = (uuids != null && uuids.length > 0) ? uuids[0].getUuid() : UUID.randomUUID();
            } else {
                return null;
            }
            BluetoothSocket socket = null;
            boolean connected = true;
            try {
                socket = device.createRfcommSocketToServiceRecord(uuid);
                socket.connect();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e2) {
                connected = false;
            }
            return connected ? socket : null;
        }

        @Override
        protected void onPostExecute(BluetoothSocket bluetoothSocket) {
            if (listener != null) {
                if (bluetoothSocket != null) listener.onConnected(bluetoothSocket);
                else listener.onFailed();
            }
        }

        private interface ConnectionListener {
            void onConnected(BluetoothSocket socket);

            void onFailed();
        }
    }

    public interface PrinterConnected {
        void onConnected();
    }

    public interface PrinterConnectFailed {
        void onFailed();
    }

    public boolean isIs3InchPrinter() {
        return is3InchPrinter;
    }

    public void isIs3InchPrinter(boolean is3inches) {
        is3InchPrinter = is3inches;
    }

    private int getLineWidth() {
        return is3InchPrinter ? WIDTH_3_INCH : WIDTH_2_INCH;
    }

    private int getPrinterWidth() {
        return is3InchPrinter ? PRINTER_WIDTH_3_INCH : PRINTER_WIDTH_2_INCH;
    }


    // escpos lib

    public static final byte LF = 0x0A;
    public static final byte[] LINE_SPACING_24 = {0x1b, 0x33, 0x18};
    public static final byte[] LINE_SPACING_30 = {0x1b, 0x33, 0x1e};

    public static byte[][] convertGSv0ToEscAsterisk(byte[] bytes) {
        int
                xL = bytes[4] & 0xFF,
                xH = bytes[5] & 0xFF,
                yL = bytes[6] & 0xFF,
                yH = bytes[7] & 0xFF,
                bytesByLine = xH * 256 + xL,
                dotsByLine = bytesByLine * 8,
                nH = dotsByLine / 256,
                nL = dotsByLine % 256,
                imageHeight = yH * 256 + yL,
                imageLineHeightCount = (int) Math.ceil((double) imageHeight / 24.0),
                imageBytesSize = 6 + bytesByLine * 24;
    
        byte[][] returnedBytes = new byte[imageLineHeightCount + 2][];
        returnedBytes[0] = LINE_SPACING_24;
        for (int i = 0; i < imageLineHeightCount; ++i) {
            int pxBaseRow = i * 24;
            byte[] imageBytes = new byte[imageBytesSize];
            imageBytes[0] = 0x1B;
            imageBytes[1] = 0x2A;
            imageBytes[2] = 0x21;
            imageBytes[3] = (byte) nL;
            imageBytes[4] = (byte) nH;
            for (int j = 5; j < imageBytes.length - 1; ++j) { // Fixed: -1 to avoid overwriting LF
                int
                        imgByte = j - 5,
                        byteRow = imgByte % 3,
                        pxColumn = imgByte / 3,
                        bitColumn = 1 << (7 - pxColumn % 8),
                        pxRow = pxBaseRow + byteRow * 8;
                for (int k = 0; k < 8; ++k) {
                    int indexBytes = bytesByLine * (pxRow + k) + pxColumn / 8 + 8;
    
                    if (indexBytes >= bytes.length) {
                        break;
                    }
    
                    boolean isBlack = (bytes[indexBytes] & bitColumn) == bitColumn;
                    if (isBlack) {
                        imageBytes[j] |= 1 << (7 - k);
                    }
                }
            }
            imageBytes[imageBytes.length - 1] = LF;
            returnedBytes[i + 1] = imageBytes;
        }
        returnedBytes[returnedBytes.length - 1] = LINE_SPACING_30;
        return returnedBytes;
    }
}
