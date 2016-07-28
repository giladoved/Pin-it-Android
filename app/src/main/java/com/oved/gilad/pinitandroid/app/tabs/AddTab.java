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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.oved.gilad.pinitandroid.R;
import com.oved.gilad.pinitandroid.models.Pin;
import com.oved.gilad.pinitandroid.rest.ApiServiceBuilder;
import com.oved.gilad.pinitandroid.utils.Constants;
import com.oved.gilad.pinitandroid.utils.LastKnownLocation;
import com.oved.gilad.pinitandroid.utils.PubSubBus;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTab extends Fragment implements View.OnClickListener {

    Location location;
    MapView mapView;
    GoogleMap map;

    Button addPinBtn;
    EditText pinTitleTxt;
    EditText pinDescriptionTxt;
    EditText pinDirectionsTxt;
    LinearLayout addPinFormLayout;
    TextView lookingForLocationLbl;

    Bus bus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_add_pin, container, false);

        Bus bus = PubSubBus.getInstance();
        bus.register(this);

        pinTitleTxt = (EditText) inflatedView.findViewById(R.id.pinTitleTxt);
        pinDescriptionTxt = (EditText) inflatedView.findViewById(R.id.pinDescriptionTxt);
        pinDirectionsTxt = (EditText) inflatedView.findViewById(R.id.pinDirectionsTxt);

        lookingForLocationLbl = (TextView) inflatedView.findViewById(R.id.lookingForLocationLbl);

        addPinFormLayout = (LinearLayout) inflatedView.findViewById(R.id.addPinFormLayout);

        location = LastKnownLocation.getLocation();

        addPinBtn = (Button) inflatedView.findViewById(R.id.addPinBtn);
        addPinBtn.setOnClickListener(this);
        addPinBtn.setEnabled(false);

        mapView = (MapView) inflatedView.findViewById(R.id.addPinMap);
        mapView.onCreate(savedInstanceState);

        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

        MapsInitializer.initialize(this.getActivity());

        return inflatedView;
    }

    @Subscribe
    public void getLocation(Location location) {
        addPinFormLayout.setVisibility(View.VISIBLE);
        lookingForLocationLbl.setVisibility(View.GONE);
        mapView.setVisibility(View.VISIBLE);

        this.location = location;
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18));
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

    @Override
    public void onClick(View v) {
        if (location == null || (location.getLatitude() == 0 && location.getLongitude() == 0)) {
            Toast.makeText(getActivity().getApplicationContext(), "Please wait until your location is found", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences settings = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        String userId = settings.getString(Constants.ID_KEY, null);
        if (userId == null) {
            return;
        }

        final String pinTitle = pinTitleTxt.getText().toString().toLowerCase().trim();
        final String pinDescription = pinDescriptionTxt.getText().toString().toLowerCase().trim();
        final String pinDirections = pinDirectionsTxt.getText().toString().toLowerCase().trim();
        if (pinTitle.length() < 2) {
            //invalid input
            Constants.Toast(getContext(), "Please enter a valid title, description and directions");
            pinTitleTxt.setText("");
            pinTitleTxt.requestFocus();
        } else {
            //add pin
            Pin pinToAdd = new Pin();
            pinToAdd.setUserId(userId);
            pinToAdd.setName(pinTitle);
            pinToAdd.setDescription(pinDescription);
            pinToAdd.setDirections(pinDirections);
            pinToAdd.setLat(this.location.getLatitude());
            pinToAdd.setLng(this.location.getLongitude());
            Call<Pin> addPinCall = ApiServiceBuilder.getInstance().api().addPin(pinToAdd);
            addPinCall.enqueue(new Callback<Pin>() {
                @Override
                public void onResponse(Call<Pin> call, Response<Pin> response) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Keep on exploring")
                            .setTitle("Pinned it!")
                            .setCancelable(false)
                            .setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    pinTitleTxt.setText("");
                                    pinDescriptionTxt.setText("");
                                    pinDirectionsTxt.setText("");

                                    pinTitleTxt.requestFocus();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

                @Override
                public void onFailure(Call<Pin> call, Throwable t) {
                    Toast.makeText(getContext(), "There was an error saving your pin...", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
