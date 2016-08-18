package com.oved.gilad.pinitandroid.utils;

import android.Manifest;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by gilad on 7/23/16.
 */
public class Constants {
    public static final String BASE_URL = "https://boiling-anchorage-90459.herokuapp.com/";

    public static final String PREFS_NAME = "PinItPrefs";

    public static final int REQUEST_IMAGE_CAPTURE = 13;

    public static final String TAG = "Pinit";

    public static final String COGNITO_POOL_ID = "us-east-1:6bf819d9-3283-46a2-9c5a-52c81119732c";
    public static final String BUCKET_NAME = "pinit-app";

    public static final String PHOTO_LINK = "https://s3.amazonaws.com/pinit-app/";

    public static final String GOOGLE_ANALYTICS_ID = "UA-66562531-3";

    public static final String ID_KEY = "id";
    public static final String NAME_KEY = "name";
    public static final String DATE_KEY = "date";

    private static final String BITMAP_STORAGE_KEY = "viewbitmap";

    public static final int PERMISSIONS_REQUEST_LOCATION = 51;

    public static final int REQUEST_EXTERNAL_STORAGE = 11;
    public static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

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
