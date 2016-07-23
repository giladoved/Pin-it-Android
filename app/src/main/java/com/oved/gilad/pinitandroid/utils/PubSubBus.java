package com.oved.gilad.pinitandroid.utils;

import com.squareup.otto.Bus;

/**
 * Created by gilad on 7/23/16.
 */
public class PubSubBus {
    private static Bus bus = null;

    public static Bus getInstance(){
        if(bus == null) {
            bus = new Bus();
        }
        return bus;
    }
}
