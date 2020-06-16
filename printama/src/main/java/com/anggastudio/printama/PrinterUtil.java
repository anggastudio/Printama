package com.anggastudio.printama;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.anggastudio.printama.Printama.CENTER;
import static com.anggastudio.printama.Printama.FULL_WIDTH;
import static com.anggastudio.printama.Printama.ORIGINAL_WIDTH;
import static com.anggastudio.printama.Printama.RIGHT;

class PrinterUtil {
    private static final int PRINTER_WIDTH = 400;
    private static final int INITIAL_MARGIN_LEFT = -12;
    private static final int BIT_WIDTH = 384;
    private static final int WIDTH = 48;
    private static final int HEAD = 8;

    private static final byte[] NEW_LINE = {10};
    private static final byte[] ESC_ALIGN_CENTER = new byte[]{0x1b, 'a', 0x01};
    private static final byte[] ESC_ALIGN_RIGHT = new byte[]{0x1b, 'a', 0x02};
    private static final byte[] ESC_ALIGN_LEFT = new byte[]{0x1b, 'a', 0x00};

    private static final String HEX_STR = "0123456789ABCDEF";
    private static final String[] BINARY_ARRAY = {"0000", "0001", "0010", "0011",
            "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011",
            "1100", "1101", "1110", "1111"};

    private BluetoothDevice printer;
    private BluetoothSocket btSocket = null;
    private OutputStream btOutputStream = null;


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

    boolean printText(String text) {
        try {
            btOutputStream.write(encodeNonAscii(text).getBytes());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
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

    boolean printDashedLine() {
        return printText("________________________________");
    }

    boolean printDoubleDashedLine() {
        return printText("================================");
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

    boolean printImage(Bitmap bitmap) {
        int width = bitmap.getWidth() > PRINTER_WIDTH ? FULL_WIDTH : ORIGINAL_WIDTH;
        return printImage(Printama.CENTER, bitmap, width);
    }

    boolean printImage(Bitmap bitmap, int width) {
        return printImage(Printama.CENTER, bitmap, width);
    }

    boolean printImage(int alignment, Bitmap bitmap, int width) {
        if (width == FULL_WIDTH) width = PRINTER_WIDTH;
        Bitmap scaledBitmap = scaledBitmap(bitmap, width);
        int marginLeft = INITIAL_MARGIN_LEFT;
        if (alignment == CENTER) {
            marginLeft = marginLeft + ((PRINTER_WIDTH - scaledBitmap.getWidth()) / 2);
        } else if (alignment == RIGHT) {
            marginLeft = marginLeft + PRINTER_WIDTH - scaledBitmap.getWidth();
        }
        byte[] command = autoGrayScale(scaledBitmap, marginLeft, 5);
        int lines = (command.length - HEAD) / WIDTH;
        System.arraycopy(new byte[]{
                0x1D, 0x76, 0x30, 0x00, 0x30, 0x00, (byte) (lines & 0xff),
                (byte) ((lines >> 8) & 0xff)
        }, 0, command, 0, HEAD);
        return printUnicode(command);
    }

    private static byte[] autoGrayScale(Bitmap bm, int bitMarginLeft, int bitMarginTop) {
        byte[] result = null;
        int n = bm.getHeight() + bitMarginTop;
        int offset = HEAD;
        result = new byte[n * WIDTH + offset];
        for (int y = 0; y < bm.getHeight(); y++) {
            for (int x = 0; x < bm.getWidth(); x++) {
                if (x + bitMarginLeft < BIT_WIDTH) {
                    int color = bm.getPixel(x, y);
                    int alpha = Color.alpha(color);
                    int red = Color.red(color);
                    int green = Color.green(color);
                    int blue = Color.blue(color);
                    if (alpha > 128 && (red < 128 || green < 128 || blue < 128)) {
                        // set the color black
                        int bitX = bitMarginLeft + x;
                        int byteX = bitX / 8;
                        int byteY = y + bitMarginTop;
                        result[offset + byteY * WIDTH + byteX] |= (0x80 >> (bitX - byteX * 8));
                    }
                } else {
                    // ignore the rest data of this line
                    break;
                }
            }
        }
        return result;
    }

    private Bitmap scaledBitmap(Bitmap bitmap, int width) {
        int desiredWidth = width == 0 || bitmap.getWidth() <= PRINTER_WIDTH ? bitmap.getWidth() : PRINTER_WIDTH;
        if (width > 0 && width <= PRINTER_WIDTH) {
            desiredWidth = width;
        }
        int height;
        float scale = (float) desiredWidth / (float) bitmap.getWidth();
        height = (int) (bitmap.getHeight() * scale);
        return Bitmap.createScaledBitmap(bitmap, desiredWidth, height, true);
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

    void setBold(boolean bold) {
        byte[] cmd = new byte[]{0x1B, 0x45, bold ? (byte) 1 : 0};
        printUnicode(cmd);
    }

    void feedPaper() {
        addNewLine();
        addNewLine();
        addNewLine();
        addNewLine();
    }

    private static class ConnectAsyncTask extends AsyncTask<BluetoothDevice, Void, BluetoothSocket> {
        private static final String COMM_SOCKET_METHOD = "createRfcommSocket";
        private ConnectionListener listener;

        private ConnectAsyncTask(ConnectionListener listener) {
            this.listener = listener;
        }

        @Override
        protected BluetoothSocket doInBackground(BluetoothDevice... bluetoothDevices) {
            BluetoothDevice device = bluetoothDevices[0];
            UUID uuid = null;
            if (device != null) {
                uuid = device.getUuids()[0].getUuid();
            } else {
                return null;
            }
            BluetoothSocket socket = null;
            boolean connected = true;
            try {
                socket = device.createRfcommSocketToServiceRecord(uuid);
                socket.connect();
                socket = (BluetoothSocket) device.getClass().getMethod(COMM_SOCKET_METHOD, new Class[]{int.class}).invoke(device, 1);
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

    private static String encodeNonAscii(String text) {
        return text.replace('á', 'a')
                .replace('č', 'c')
                .replace('ď', 'd')
                .replace('é', 'e')
                .replace('ě', 'e')
                .replace('í', 'i')
                .replace('ň', 'n')
                .replace('ó', 'o')
                .replace('ř', 'r')
                .replace('š', 's')
                .replace('ť', 't')
                .replace('ú', 'u')
                .replace('ů', 'u')
                .replace('ý', 'y')
                .replace('ž', 'z')
                .replace('Á', 'A')
                .replace('Č', 'C')
                .replace('Ď', 'D')
                .replace('É', 'E')
                .replace('Ě', 'E')
                .replace('Í', 'I')
                .replace('Ň', 'N')
                .replace('Ó', 'O')
                .replace('Ř', 'R')
                .replace('Š', 'S')
                .replace('Ť', 'T')
                .replace('Ú', 'U')
                .replace('Ů', 'U')
                .replace('Ý', 'Y')
                .replace('Ž', 'Z');
    }

    private static List<String> binaryListToHexStringList(List<String> list) {
        List<String> hexList = new ArrayList<>();
        for (String binaryStr : list) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < binaryStr.length(); i += 8) {
                String str = binaryStr.substring(i, i + 8);
                String hexString = strToHexString(str);
                sb.append(hexString);
            }
            hexList.add(sb.toString());
        }
        return hexList;
    }

    private static String strToHexString(String binaryStr) {
        String hex = "";
        String f4 = binaryStr.substring(0, 4);
        String b4 = binaryStr.substring(4, 8);
        for (int i = 0; i < BINARY_ARRAY.length; i++) {
            if (f4.equals(BINARY_ARRAY[i]))
                hex += HEX_STR.substring(i, i + 1);
        }
        for (int i = 0; i < BINARY_ARRAY.length; i++) {
            if (b4.equals(BINARY_ARRAY[i]))
                hex += HEX_STR.substring(i, i + 1);
        }
        return hex;
    }

    BluetoothSocket getSocket() {
        return btSocket;
    }

    BluetoothDevice getDevice() {
        return printer;
    }
}
