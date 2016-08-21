package com.oved.gilad.pinitandroid.app.tabs;

import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.oved.gilad.pinitandroid.R;
import com.oved.gilad.pinitandroid.app.custom.CustomIconRenderer;
import com.oved.gilad.pinitandroid.app.custom.PinCluster;
import com.oved.gilad.pinitandroid.app.pages.MainActivity;
import com.oved.gilad.pinitandroid.models.Pin;
import com.oved.gilad.pinitandroid.rest.ApiServiceBuilder;
import com.oved.gilad.pinitandroid.utils.Constants;
import com.oved.gilad.pinitandroid.utils.PubSubBus;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapTab extends Fragment {

    List<Pin> pins;
    MapView mapView;
    GoogleMap map;
    Location location;
    ClusterManager<PinCluster> clusterManager;
    CustomIconRenderer customIconRenderer;

    Bus bus;
    MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_map_view, container, false);

        bus = PubSubBus.getInstance();
        bus.register(this);

        mainActivity = (MainActivity) getActivity();

        pins = new ArrayList<>();

        mapView = (MapView) inflatedView.findViewById(R.id.mapTabMap);
        mapView.onCreate(savedInstanceState);

        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setMyLocationEnabled(true);

        MapsInitializer.initialize(this.getActivity());

        location = mainActivity.getLocation();
        positionToLocation();

        clusterManager = new ClusterManager<>(getContext(), map);

        map.setOnCameraChangeListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);
        customIconRenderer = new CustomIconRenderer(getContext(), map, clusterManager);
        clusterManager.setRenderer(customIconRenderer);

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Tracker tracker = mainActivity.getTracker();
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Pin")
                        .setAction("Clicked")
                        .build());

                Pin chosenPin = customIconRenderer.getClusterItem(marker).getPin();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(chosenPin.getDescription() + "\n" + chosenPin.getDirections())
                        .setTitle(chosenPin.getTitle())
                        .setCancelable(false)
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<PinCluster>() {
            @Override
            public boolean onClusterClick(final Cluster<PinCluster> cluster) {
                Tracker tracker = mainActivity.getTracker();
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Pin")
                        .setAction("Cluster")
                        .build());

                map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        cluster.getPosition(), (float) Math.floor(map
                                .getCameraPosition().zoom + 1)), 300, null);
                return true;
            }
        });

        loadPins();

        return inflatedView;
    }

    public void loadPins() {
        final String userId = mainActivity.getUserId();
        if (userId != null) {
            Call<List<Pin>> getAllPinsCall = ApiServiceBuilder.getInstance().api().getAllPins();
            getAllPinsCall.enqueue(new Callback<List<Pin>>() {
                @Override
                public void onResponse(Call<List<Pin>> call, Response<List<Pin>> response) {
                    if (response.isSuccessful()) {
                        pins = response.body();
                        if (pins.size() == 0) {
                            return;
                        }

                        for (Pin pin : pins) {
                            if (pin != null) {
                                PinCluster clusterPin = new PinCluster(pin.getLat(), pin.getLng(), pin, userId);
                                clusterManager.addItem(clusterPin);
                            }
                        }
                        clusterManager.cluster();
                    } else {
                        Constants.Error("Error getting the pins");
                    }
                }

                @Override
                public void onFailure(Call<List<Pin>> call, Throwable t) {
                    Constants.Error("Error getting the pins: " + t.getMessage());
                }
            });
        }
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && getActivity() != null) {
            loadPins();
        }
    }

    @Subscribe
    public void getChosenMarker(String pid) {
        if (pins.size() == 0) {
            loadPins();
        }

        for (Pin pin : pins) {
            if (pid.equals(pin.getId())) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(pin.getLat(), pin.getLng()), 13));
            }
        }
    }

    public void positionToLocation() {
        if (location != null) {
            positionMap(location);
        }
    }

    public void positionMap(Location location) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12));
    }

    @Subscribe
    public void getLocation(Location location) {
        if (this.location == null) {
            //Bounding box
            /*LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : markers) {
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 10);
            map.animateCamera(cu);*/

            //pin around current location
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12));
        }
        this.location = location;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
