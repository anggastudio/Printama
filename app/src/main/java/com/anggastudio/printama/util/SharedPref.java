package com.anggastudio.printama.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class SharedPref {

    public static final String ATTEMP_COUNT = "attemp_count";
    private static SharedPreferences sharedPreferences;
    public SharedPref() {
        // empty constructor
    }

    public static void init(Context context) {
        if (context == null) return;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getString(String key) {
        if (sharedPreferences == null) return null;
        return sharedPreferences.getString(key, "");
    }

    public static void setString(String key, String value) {
        if (sharedPreferences == null) return;
        if (value == null) value = "";
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static Integer getInt(String key) {
        if (sharedPreferences == null) return null;
        return sharedPreferences.getInt(key, -1);
    }

    public static void setInt(String key, Integer value) {
        if (sharedPreferences == null) return;
        if (value == null) value = 0;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

}
