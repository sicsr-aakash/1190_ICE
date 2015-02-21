package com.siddharthbhatt.voice_recog;


import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by siddharthbhatt on 21/02/15.
 */
public class SettingsClass {

    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;


    private static SettingsClass ourInstance = new SettingsClass();

    public static SettingsClass getInstance() {
        return ourInstance;
    }

    private SettingsClass() {
        //sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    }
}
