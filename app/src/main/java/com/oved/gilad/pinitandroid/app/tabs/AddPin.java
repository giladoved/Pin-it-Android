package com.oved.gilad.pinitandroid.app.tabs;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oved.gilad.pinitandroid.R;
import com.oved.gilad.pinitandroid.utils.PubSubBus;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class AddPin extends Fragment {

    Location location;
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_add_pin, container, false);

        Bus bus = PubSubBus.getInstance();
        bus.register(this);

        return inflatedView;
    }

    @Subscribe
    public void getLocation(Location location) {
        this.location = location;
    }
}
