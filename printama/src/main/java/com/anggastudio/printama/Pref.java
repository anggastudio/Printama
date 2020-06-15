package com.anggastudio.printama;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

class Pref {

    static final String SAVED_DEVICE = "bonded_device";
    private static SharedPreferences sharedPreferences;

    public static void init(Context context) {
        if (context == null) return;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getString(String key) {
        if (sharedPreferences == null) return null;
        return new String(Base64.decode(sharedPreferences.getString(key, ""), 0));
    }

    public static void setString(String key, String value) {
        if (sharedPreferences == null) return;
        if (value == null) value = "";
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, Base64.encodeToString(value.getBytes(), 0));
        editor.apply();
    }

    public static int getInt(String key) {
        if (sharedPreferences == null) return 0;
        return sharedPreferences.getInt(key, 0);
    }

    public static void setInt(String key, int value) {
        if (sharedPreferences == null) return;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static boolean getBool(String key) {
        if (sharedPreferences == null) return false;
        return sharedPreferences.getBoolean(key, false);
    }

    public static void setBool(String key, boolean value) {
        if (sharedPreferences == null) return;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static double getFloat(String key) {
        if (sharedPreferences == null) return 0;
        return sharedPreferences.getFloat(key, 0);
    }

    public static void setFloat(String key, float value) {
        if (sharedPreferences == null) return;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

}
