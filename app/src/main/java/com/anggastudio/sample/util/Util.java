package com.anggastudio.sample.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class Util {

    public static Bitmap decodeBase64(String base64) {
        byte[] decodedByte = Base64.decode(base64, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
