package com.oved.gilad.pinitandroid.app.tabs;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.oved.gilad.pinitandroid.R;
import com.oved.gilad.pinitandroid.models.Pin;
import com.oved.gilad.pinitandroid.rest.ApiServiceBuilder;
import com.oved.gilad.pinitandroid.utils.Constants;
import com.oved.gilad.pinitandroid.utils.PubSubBus;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
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
    List<Marker> markers;
    Map<Marker, Pin> markersToPins;
    Map<String, Marker> pinsToMarkers;
    Location location;

    Bus bus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_map_view, container, false);

        Bus bus = PubSubBus.getInstance();
        bus.register(this);

        markers = new ArrayList<>();
        markersToPins = new HashMap<>();
        pinsToMarkers = new HashMap<>();

        mapView = (MapView) inflatedView.findViewById(R.id.mapTabMap);
        mapView.onCreate(savedInstanceState);

        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setMyLocationEnabled(true);

        MapsInitializer.initialize(this.getActivity());

        SharedPreferences settings = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        final String userId = settings.getString(Constants.ID_KEY, null);
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
                                LatLng pinLocation = new LatLng(pin.getLat(), pin.getLng());
                                Marker marker = map.addMarker(new MarkerOptions()
                                        .position(pinLocation)
                                        .title(pin.getTitle())
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
                                        .snippet(pin.getDescription()));
                                if (userId.equals(pin.getUserId())) {
                                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pingrey));
                                }

                                markers.add(marker);
                                markersToPins.put(marker, pin);
                                pinsToMarkers.put(pin.getId(), marker);
                            }
                        }

                        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker marker) {
                                Pin chosenPin = markersToPins.get(marker);
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

        return inflatedView;
    }

    @Subscribe
    public void getChosenMarker(String pid) {
        Marker chosenMarker = pinsToMarkers.get(pid);
        Constants.Log("Chosen marker: " + chosenMarker.getTitle());
        chosenMarker.showInfoWindow();
        Pin pin = markersToPins.get(chosenMarker);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(pin.getLat(), pin.getLng()), 13));
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
