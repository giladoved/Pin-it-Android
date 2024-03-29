package com.oved.gilad.pinitandroid.app.pages;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.oved.gilad.pinitandroid.R;
import com.oved.gilad.pinitandroid.utils.Constants;

public class LandingActivity extends AppCompatActivity {

    Button startBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, 0);
        String foundUserID = prefs.getString(Constants.ID_KEY, null);
        if (foundUserID != null) {
            Intent i = new Intent(LandingActivity.this, MainActivity.class);
            startActivity(i);
        }

        startBtn = (Button) findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && checkSelfPermission(
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION},
                            Constants.PERMISSIONS_REQUEST_LOCATION);
                } else {
                    Intent i = new Intent(LandingActivity.this, RegisterActivity.class);
                    startActivity(i);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent i = new Intent(LandingActivity.this, RegisterActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void onBackPressed() {
    }
}
