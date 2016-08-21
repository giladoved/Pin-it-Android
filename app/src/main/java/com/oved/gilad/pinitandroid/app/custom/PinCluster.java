package com.oved.gilad.pinitandroid.app.custom;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.oved.gilad.pinitandroid.models.Pin;

/**
 * Created by gilad on 8/20/16.
 */
public class PinCluster implements ClusterItem {
    private final LatLng position;
    private final Pin pin;
    private final String userId;

    public PinCluster(double lat, double lng, Pin pin, String userId) {
        position = new LatLng(lat, lng);
        this.pin = pin;
        this.userId = userId;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public Pin getPin() { return pin; }

    public boolean isMyPin() {
        if (pin.getUserId().equals(userId)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PinCluster that = (PinCluster) o;

        if (!position.equals(that.position)) return false;
        return userId.equals(that.userId);

    }

    @Override
    public int hashCode() {
        int result = position.hashCode();
        result = 31 * result + userId.hashCode();
        return result;
    }
}
