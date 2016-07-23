package com.oved.gilad.pinitandroid.models;

import android.location.Location;

import com.oved.gilad.pinitandroid.utils.PubSubBus;
import com.squareup.otto.Bus;

/**
 * Created by gilad on 7/23/16.
 */
public class Position {
    static Location position;

    public static Location getPosition() {
        if (position == null) {
            Location location = new Location("");
            location.setLatitude(0.0d);
            location.setLongitude(0.0d);
            return location;
        }

        return position;
    }

    public static void setPosition(Location newPosition) {
        if (newPosition != null) {
            position = newPosition;
            Bus bus = PubSubBus.getInstance();
            bus.post(Position.getPosition());
        }
    }
}
