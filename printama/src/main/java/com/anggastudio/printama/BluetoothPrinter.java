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

public class BluetoothPrinter {
    public static final int PRINTER_WIDTH = 400;
    public static final int BIT_WIDTH = 384;
    private static final int WIDTH = 48;
    private static final int DOT_LINE_LIMIT = 200;
    private static final int DC2V_HEAD = 4;
    private static final int GSV_HEAD = 8;
    public static final int ALIGN_CENTER = 100;
    public static final int ALIGN_RIGHT = 101;
    public static final int ALIGN_LEFT = 102;

    private static final byte[] NEW_LINE = {10};
    private static final byte[] ESC_ALIGN_CENTER = new byte[]{0x1b, 'a', 0x01};
    private static final byte[] ESC_ALIGN_RIGHT = new byte[]{0x1b, 'a', 0x02};
    private static final byte[] ESC_ALIGN_LEFT = new byte[]{0x1b, 'a', 0x00};

    private BluetoothDevice printer;
    private BluetoothSocket btSocket = null;
    private OutputStream btOutputStream = null;

    public BluetoothPrinter(BluetoothDevice printer) {
        this.printer = printer;
    }

    public void connectPrinter(final PrinterConnectListener listener) {
        new ConnectTask(new ConnectTask.BtConnectListener() {
            @Override
            public void onConnected(BluetoothSocket socket) {
                btSocket = socket;
                try {
                    btOutputStream = socket.getOutputStream();
                    listener.onConnected();
                } catch (IOException e) {
                    listener.onFailed();
                }
            }

            @Override
            public void onFailed() {
                listener.onFailed();
            }
        }).execute(printer);
    }

    public boolean isConnected() {
        return btSocket != null && btSocket.isConnected();
    }

    public void finish() {
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

    public boolean printText(String text) {
        try {
            btOutputStream.write(encodeNonAscii(text).getBytes());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean printUnicode(byte[] data) {
        try {
            btOutputStream.write(data);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean printLine() {
        return printText("________________________________");
    }

    public boolean addNewLine() {
        return printUnicode(NEW_LINE);
    }

    public int addNewLines(int count) {
        int success = 0;
        for (int i = 0; i < count; i++) {
            if (addNewLine()) success++;
        }

        return success;
    }

    public boolean printImage(Bitmap bitmap) {
        return printImage(bitmap, 0);
    }

    public boolean printImage(Bitmap bitmap, int width) {
        Bitmap scaledBitmap = scaledBitmap(bitmap, width);
        int marginLeft = -14;
        if (width > 0 && width < PRINTER_WIDTH) {
            marginLeft = marginLeft + ((PRINTER_WIDTH - width) / 2);
        }
        byte[] command = generateBitmapArrayGSV_MSB(scaledBitmap, marginLeft, 0);
        int lines = (command.length - GSV_HEAD) / WIDTH;
        System.arraycopy(new byte[]{
                0x1D, 0x76, 0x30, 0x00, 0x30, 0x00, (byte) (lines & 0xff),
                (byte) ((lines >> 8) & 0xff)
        }, 0, command, 0, GSV_HEAD);
        return printUnicode(command);
    }

    private static byte[] generateBitmapArrayGSV_MSB(Bitmap bm, int bitMarginLeft, int bitMarginTop) {
        byte[] result = null;
        int n = bm.getHeight() + bitMarginTop;
        int offset = GSV_HEAD;
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
        int desiredWidth = width == 0 || bitmap.getWidth() < PRINTER_WIDTH ? bitmap.getWidth() : PRINTER_WIDTH;
        if (width > 0 && width < PRINTER_WIDTH) {
            desiredWidth = width;
        }
        int height;
        float scale = (float) desiredWidth / (float) bitmap.getWidth();
        height = (int) (bitmap.getHeight() * scale);
        return Bitmap.createScaledBitmap(bitmap, desiredWidth, height, true);
    }

    public void setAlign(int alignType) {
        byte[] d;
        switch (alignType) {
            case ALIGN_CENTER:
                d = ESC_ALIGN_CENTER;
                break;
            case ALIGN_LEFT:
                d = ESC_ALIGN_LEFT;
                break;
            case ALIGN_RIGHT:
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

    public void setLineSpacing(int lineSpacing) {
        byte[] cmd = new byte[]{0x1B, 0x33, (byte) lineSpacing};
        printUnicode(cmd);
    }

    public void setBold(boolean bold) {
        byte[] cmd = new byte[]{0x1B, 0x45, bold ? (byte) 1 : 0};
        printUnicode(cmd);
    }

    public void feedPaper() {
        addNewLine();
        addNewLine();
        addNewLine();
        addNewLine();
    }

    private static class ConnectTask extends AsyncTask<BluetoothDevice, Void, BluetoothSocket> {
        private BtConnectListener listener;

        private ConnectTask(BtConnectListener listener) {
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
            } catch (IOException e) {
            }
            try {
                socket.connect();
            } catch (IOException e) {
                try {
                    socket = (BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[]{int.class})
                            .invoke(device, 1);
                    socket.connect();
                } catch (Exception e2) {
                    connected = false;
                }
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

        private interface BtConnectListener {
            void onConnected(BluetoothSocket socket);

            void onFailed();
        }
    }

    public interface PrinterConnectListener {
        void onConnected();

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

    public static byte[] decodeBitmap(Bitmap bmp) {
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();

        List<String> list = new ArrayList<>();
        StringBuffer sb;
        int zeroCount = bmpWidth % 8;
        String zeroStr = "";
        if (zeroCount > 0) {
            for (int i = 0; i < (8 - zeroCount); i++) zeroStr = zeroStr + "0";
        }

        for (int i = 0; i < bmpHeight; i++) {
            sb = new StringBuffer();
            for (int j = 0; j < bmpWidth; j++) {
                int color = bmp.getPixel(j, i);
                int r = (color >> 16) & 0xff;
                int g = (color >> 8) & 0xff;
                int b = color & 0xff;
                if (r > 160 && g > 160 && b > 160) sb.append("0");
                else sb.append("1");
            }
            if (zeroCount > 0) sb.append(zeroStr);
            list.add(sb.toString());
        }

        List<String> bmpHexList = binaryListToHexStringList(list);
        String commandHexString = "1D763000";
        String widthHexString = Integer
                .toHexString(bmpWidth % 8 == 0 ? bmpWidth / 8 : (bmpWidth / 8 + 1));
        if (widthHexString.length() > 2) {
            return null;
        } else if (widthHexString.length() == 1) {
            widthHexString = "0" + widthHexString;
        }
        widthHexString = widthHexString + "00";

        String heightHexString = Integer.toHexString(bmpHeight);
        if (heightHexString.length() > 2) {
            return null;
        } else if (heightHexString.length() == 1) {
            heightHexString = "0" + heightHexString;
        }
        heightHexString = heightHexString + "00";

        List<String> commandList = new ArrayList<>();
        commandList.add(commandHexString + widthHexString + heightHexString);
        commandList.addAll(bmpHexList);

        return hexList2Byte(commandList);
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

    private static String hexStr = "0123456789ABCDEF";
    private static String[] binaryArray = {"0000", "0001", "0010", "0011",
            "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011",
            "1100", "1101", "1110", "1111"};

    private static String strToHexString(String binaryStr) {
        String hex = "";
        String f4 = binaryStr.substring(0, 4);
        String b4 = binaryStr.substring(4, 8);
        for (int i = 0; i < binaryArray.length; i++) {
            if (f4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }
        for (int i = 0; i < binaryArray.length; i++) {
            if (b4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }

        return hex;
    }

    private static byte[] hexList2Byte(List<String> list) {
        List<byte[]> commandList = new ArrayList<>();
        for (String hexStr : list) commandList.add(hexStringToBytes(hexStr));
        return sysCopy(commandList);
    }

    private static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) return null;
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte[] sysCopy(List<byte[]> srcArrays) {
        int len = 0;
        for (byte[] srcArray : srcArrays) {
            len += srcArray.length;
        }
        byte[] destArray = new byte[len];
        int destLen = 0;
        for (byte[] srcArray : srcArrays) {
            System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
            destLen += srcArray.length;
        }

        return destArray;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public BluetoothSocket getSocket() {
        return btSocket;
    }

    public BluetoothDevice getDevice() {
        return printer;
    }
}
