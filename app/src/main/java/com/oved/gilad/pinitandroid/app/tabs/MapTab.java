package com.oved.gilad.pinitandroid.app.tabs;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.oved.gilad.pinitandroid.R;
import com.oved.gilad.pinitandroid.models.Pin;
import com.oved.gilad.pinitandroid.rest.ApiServiceBuilder;
import com.oved.gilad.pinitandroid.utils.Constants;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_map_view, container, false);

        markers = new ArrayList<>();
        markersToPins = new HashMap<>();

        mapView = (MapView) inflatedView.findViewById(R.id.mapTabMap);
        mapView.onCreate(savedInstanceState);

        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

        MapsInitializer.initialize(this.getActivity());

        SharedPreferences settings = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        String userId = settings.getString(Constants.ID_KEY, null);
        if (userId != null) {
            Call<List<Pin>> getAllPinsCall = ApiServiceBuilder.getInstance().api().getAllPins(userId);
            getAllPinsCall.enqueue(new Callback<List<Pin>>() {
                @Override
                public void onResponse(Call<List<Pin>> call, Response<List<Pin>> response) {
                    if (response.isSuccessful()) {
                        pins = response.body();

                        if (pins.size() == 0) {
                            return;
                        }

                        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

                        Constants.Log("pins size: " + pins.size());
                        for (Pin pin : pins) {
                            Constants.Log("found pin: " + pin.getLat() + " , " + pin.getLng());
                            LatLng pinLocation = new LatLng(pin.getLat(), pin.getLng());
                            boundsBuilder.include(pinLocation);
                            Marker marker = map.addMarker(new MarkerOptions()
                                    .position(pinLocation)
                                    .title(pin.getName())
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
                                    .snippet(pin.getDescription()));
                            markers.add(marker);
                            markersToPins.put(marker, pin);
                        }

                        map.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 50));
                        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker marker) {
                                Pin chosenPin = markersToPins.get(marker);

                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage(chosenPin.getDescription() + "\n" + chosenPin.getDirections())
                                        .setTitle(chosenPin.getName())
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
