package com.anggastudio.printama.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import net.glxn.qrgen.android.QRCode;

public class Util {

    private Util() {

    }

    public static Bitmap decodeBase64(String base64) {
        byte[] decodedByte = Base64.decode(base64, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public static Bitmap getQrCode(String qrCode) {
        // please see the dependency to create QRCode
        // it's not included in Printama
        return QRCode.from(qrCode).bitmap();
    }

    public static boolean isAllowToPrint() {
        Integer attempCount = SharedPref.getInt(SharedPref.ATTEMP_COUNT);
        if (attempCount != null && attempCount < 100) {
            attempCount++;
            SharedPref.setInt(SharedPref.ATTEMP_COUNT, attempCount);
            return true;
        }
        return false;
    }

}
