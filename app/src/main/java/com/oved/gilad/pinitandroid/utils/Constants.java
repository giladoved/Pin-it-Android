package com.oved.gilad.pinitandroid.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by gilad on 7/23/16.
 */
public class Constants {
    public static final String BASE_URL = "https://boiling-anchorage-90459.herokuapp.com/";

    public static final String PREFS_NAME = "PinItPrefs";

    public static final String TAG = "Pin It";

    public static final String ID_KEY = "id";
    public static final String NAME_KEY = "name";
    public static final String DATE_KEY = "date";

    public static final int PERMISSIONS_REQUEST_LOCATION = 51;

    public static final long LOCATION_UPDATE_TIME = 1000 * 10;

    public static final String GOOGLE_MAPS_API = "AIzaSyBx-NiWfMiom5MP4OMVVpPVa8I91X8XbHM";

    public static void Log(String message) {
        Log.d(TAG, message);
    }

    public static void Error(String message) {
        Log.e(TAG, message);
    }

    public static void Toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
