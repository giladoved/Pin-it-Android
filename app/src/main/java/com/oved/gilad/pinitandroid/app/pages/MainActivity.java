package com.oved.gilad.pinitandroid.app.pages;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.oved.gilad.pinitandroid.R;
import com.oved.gilad.pinitandroid.app.adapters.PagerAdapter;
import com.oved.gilad.pinitandroid.app.tabs.AddTab;
import com.oved.gilad.pinitandroid.app.tabs.MapTab;
import com.oved.gilad.pinitandroid.utils.AnalyticsApplication;
import com.oved.gilad.pinitandroid.utils.Constants;
import com.oved.gilad.pinitandroid.utils.PubSubBus;
import com.squareup.otto.Bus;

import java.io.File;

//http://stackoverflow.com/questions/30093673/use-the-android-default-gps-on-off-dialog-in-my-application?lq=1
public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback, LocationListener {

    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    Location location;
    TransferUtility transferUtility;
    AmazonS3 s3;

    ViewPager viewPager;
    PagerAdapter adapter;
    TabLayout tabLayout;

    Bus bus;

    Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        tracker = application.getDefaultTracker();

        final CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                Constants.COGNITO_POOL_ID,
                Regions.US_EAST_1
        );

        s3 = new AmazonS3Client(credentialsProvider);
        transferUtility = new TransferUtility(s3, getApplicationContext());

        bus = PubSubBus.getInstance();

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.addicon));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.listicon));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.mapicon));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                Constants.Log("Tab Clicked: " + tab.getPosition());

                String tabName = "";

                switch(tab.getPosition()) {
                    case 0:
                        tabName = "add";
                        AddTab addTap = (AddTab) adapter.getItem(0);
                        addTap.positionToLocation();
                        break;
                    case 1:
                        tabName = "list";
                        break;
                    case 2:
                        tabName = "map";
                        MapTab mapTab = (MapTab) adapter.getItem(2);
                        mapTab.positionToLocation();
                        break;
                    default:
                        break;
                }

                Constants.Log("Setting screen: " + tabName);
                tracker.setScreenName("Image~" + tabName);
                tracker.send(new HitBuilders.ScreenViewBuilder().build());

                //hides keyboard
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        googleApiClient.connect();

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(Constants.LOCATION_UPDATE_TIME);
        locationRequest.setFastestInterval(Constants.LOCATION_UPDATE_TIME);
    }

    public void uploadImage(File file, String pinId) {
        try {
            Constants.Log("file: " + file.length());
            Constants.Log("pinId: " + pinId);
            transferUtility.upload(Constants.BUCKET_NAME, pinId, file, CannedAccessControlList.PublicRead);
        } catch (Exception x) {
            Constants.Error("Error in uploading: " + x.getMessage());
        }
    }

    public void openMap() {
        viewPager.setCurrentItem(2);
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult result =
                LocationServices.SettingsApi.checkLocationSettings(
                        googleApiClient,
                        builder.build()
                );

        result.setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Constants.Log("Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Constants.Error("Error");
    }

    @Override
    public void onResult(Result result) {
        final Status status = result.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                // NO need to show the dialog;
                startLocationUpdates();
                break;

            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                //  Location settings are not satisfied. Show the user a dialog
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(MainActivity.this, Constants.PERMISSIONS_REQUEST_LOCATION);
                } catch (IntentSender.SendIntentException e) {
                    //failed to show
                }
                break;

            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are unavailable so not possible to show any dialog now
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.PERMISSIONS_REQUEST_LOCATION) {
            if (resultCode == RESULT_OK) {
                //Constants.Toast(getApplicationContext(), "GPS enabled");
                startLocationUpdates();
            } else {
                Constants.Toast(getApplicationContext(), "Please turn on your location services to use Pin it");
            }
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    public Tracker getTracker() {
        return tracker;
    }

    public String getUserId() {
        return getSharedPreferences(Constants.PREFS_NAME, 0).getString(Constants.ID_KEY, null);
    }

    public String getUsername() {
        return getSharedPreferences(Constants.PREFS_NAME, 0).getString(Constants.NAME_KEY, null);
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        bus.post(location);
        Constants.Log("Posted location: " + location.getLatitude() + ", " + location.getLongitude());
    }

    @Override
    public void onBackPressed() {
        if (tabLayout.getSelectedTabPosition() == 2) {
            viewPager.setCurrentItem(1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settingsBtn) {
            SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
            String username = settings.getString(Constants.NAME_KEY, null);
            if (username != null) {
                Toast.makeText(MainActivity.this, "Hello " + username + "!", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}