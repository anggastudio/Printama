package com.anggastudio.printama;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

class Pref {
    private Pref() {
        // empty constructor
    }

    static final String SAVED_DEVICE = "bonded_device";
    static final String IS_PRINTER_3INCH = "is_printer_3inch";
    private static SharedPreferences sharedPreferences;

    static void init(Context context) {
        if (context == null) return;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    static String getString(String key) {
        if (sharedPreferences == null) return null;
        return sharedPreferences.getString(key, "");
    }

    static void setString(String key, String value) {
        if (sharedPreferences == null) return;
        if (value == null) value = "";
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    static boolean getBoolean(String key) {
        if (sharedPreferences == null) return false;
        return sharedPreferences.getBoolean(key, false);
    }

    static void setBoolean(String key, boolean value) {
        if (sharedPreferences == null) return;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
}
