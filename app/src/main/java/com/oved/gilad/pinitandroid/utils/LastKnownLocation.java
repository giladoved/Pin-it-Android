package com.oved.gilad.pinitandroid.utils;

import android.location.Location;

/**
 * Created by gilad on 7/27/16.
 */
public class LastKnownLocation {
    private static Location location = null;

    public static Location getLocation(){
        return location;
    }

    public static void setLocation(Location loc) {
        location = loc;
    }
}
