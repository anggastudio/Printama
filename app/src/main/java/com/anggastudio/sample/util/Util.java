package com.anggastudio.sample.util;

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

}
