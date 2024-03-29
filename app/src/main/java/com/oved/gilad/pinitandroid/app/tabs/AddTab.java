package com.oved.gilad.pinitandroid.app.tabs;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.oved.gilad.pinitandroid.R;
import com.oved.gilad.pinitandroid.app.pages.MainActivity;
import com.oved.gilad.pinitandroid.models.Pin;
import com.oved.gilad.pinitandroid.rest.ApiServiceBuilder;
import com.oved.gilad.pinitandroid.utils.Constants;
import com.oved.gilad.pinitandroid.utils.PubSubBus;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTab extends Fragment implements View.OnClickListener {

    Location location;
    MapView mapView;
    GoogleMap map;

    Button addPhotoBtn;
    EditText pinTitleTxt;
    EditText pinDescriptionTxt;
    EditText pinDirectionsTxt;
    LinearLayout addPinFormLayout;
    TextView lookingForLocationLbl;

    Bus bus;

    String encodedImage;
    String username;
    String pinId;

    String currentPhotoPath;
    File createdFile;

    MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_add_pin, container, false);

        bus = PubSubBus.getInstance();
        bus.register(this);

        mainActivity = (MainActivity) getActivity();

        pinTitleTxt = (EditText) inflatedView.findViewById(R.id.pinTitleTxt);
        pinDescriptionTxt = (EditText) inflatedView.findViewById(R.id.pinDescriptionTxt);
        pinDirectionsTxt = (EditText) inflatedView.findViewById(R.id.pinDirectionsTxt);

        lookingForLocationLbl = (TextView) inflatedView.findViewById(R.id.lookingForLocationLbl);

        addPinFormLayout = (LinearLayout) inflatedView.findViewById(R.id.addPinFormLayout);

        addPhotoBtn = (Button) inflatedView.findViewById(R.id.addPhotoBtn);
        addPhotoBtn.setOnClickListener(this);

        mapView = (MapView) inflatedView.findViewById(R.id.addPinMap);
        mapView.onCreate(savedInstanceState);

        map = mapView.getMap();
        map.setMyLocationEnabled(true);

        MapsInitializer.initialize(this.getActivity());

        addPinFormLayout.setVisibility(View.VISIBLE);
        lookingForLocationLbl.setVisibility(View.GONE);
        mapView.setVisibility(View.VISIBLE);

        location = mainActivity.getLocation();
        positionToLocation();

        username = mainActivity.getUsername();

        return inflatedView;
    }

    @Subscribe
    public void getLocation(Location location) {
        this.location = location;
        positionMap(location);
    }

    public void positionToLocation() {
        if (location != null) {
            positionMap(location);
        }
    }

    public void positionMap(Location location) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18));
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

    public void addPin() {
        if (location == null || (location.getLatitude() == 0 && location.getLongitude() == 0)) {
            Toast.makeText(getActivity().getApplicationContext(), "Please wait until your location is found", Toast.LENGTH_SHORT).show();
            return;
        }

        if (encodedImage == null) {
            encodedImage = "";
        }

        String userId = mainActivity.getUserId();
        if (userId == null) {
            return;
        }

        //add pin
        final String pinTitle = pinTitleTxt.getText().toString().toLowerCase().trim();
        final String pinDescription = pinDescriptionTxt.getText().toString().toLowerCase().trim();
        final String pinDirections = pinDirectionsTxt.getText().toString().toLowerCase().trim();

        final Pin pinToAdd = new Pin();
        pinToAdd.setUserId(userId);
        pinToAdd.setTitle(pinTitle);
        pinToAdd.setDescription(pinDescription);
        pinToAdd.setDirections(pinDirections);
        pinToAdd.setLat(this.location.getLatitude());
        pinToAdd.setLng(this.location.getLongitude());
        pinToAdd.setImage(pinId);
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

                                mainActivity.openMap();
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

    @Override
    public void onClick(View v) {
        final String pinTitle = pinTitleTxt.getText().toString().toLowerCase().trim();
        final String pinDescription = pinDescriptionTxt.getText().toString().toLowerCase().trim();
        final String pinDirections = pinDirectionsTxt.getText().toString().toLowerCase().trim();
        if (pinTitle == null || pinTitle.length() < 2 || pinDescription == null || pinDescription.length() < 2) {
            //invalid input
            Constants.Toast(getContext(), "Please enter a valid title, description and directions");
            pinTitleTxt.setText("");
            pinTitleTxt.requestFocus();
        } else {
            verifyStoragePermissions(getActivity());

            dispatchTakePictureIntent();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            createdFile = createImageFile();
            currentPhotoPath = createdFile.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(createdFile));
        } catch (IOException e) {
            createdFile = null;
            currentPhotoPath = null;
            e.printStackTrace();
        }

        startActivityForResult(takePictureIntent, Constants.REQUEST_IMAGE_CAPTURE);
    }

    private File createImageFile() throws IOException {
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //String imageFileName = timeStamp + ".png";
        pinId = UUID.randomUUID() + "";
        String imageFileName = pinId + ".png";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/pinit/", imageFileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            if (currentPhotoPath != null) {
                Constants.Log("path: "  + createdFile.getAbsolutePath());
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.uploadImage(createdFile, pinId);
                addPin();

                //add to gallery
                Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                Uri contentUri = Uri.fromFile(createdFile);
                mediaScanIntent.setData(contentUri);
                getContext().sendBroadcast(mediaScanIntent);

                currentPhotoPath = null;
            }
        }
    }


    //http://stackoverflow.com/questions/8854359/exception-open-failed-eacces-permission-denied-on-android
    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    Constants.PERMISSIONS_STORAGE,
                    Constants.REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
