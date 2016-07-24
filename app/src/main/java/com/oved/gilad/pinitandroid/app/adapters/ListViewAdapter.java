package com.oved.gilad.pinitandroid.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.oved.gilad.pinitandroid.R;
import com.oved.gilad.pinitandroid.models.Pin;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.util.ArrayList;

/**
 * Created by gilad on 7/23/16.
 */
public class ListViewAdapter extends ArrayAdapter<Pin> {
    public ListViewAdapter(Context context, ArrayList<Pin> pins) {
        super(context, 0, pins);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Pin pin = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listcell, parent, false);
        }

        TextView titleTxt = (TextView) convertView.findViewById(R.id.titleTxt);
        TextView descriptionTxt = (TextView) convertView.findViewById(R.id.descriptionTxt);
        TextView directionsTxt = (TextView) convertView.findViewById(R.id.directionsTxt);
        TextView dateCreatedTxt = (TextView) convertView.findViewById(R.id.dateCreatedTxt);

        titleTxt.setText(pin.getName());
        descriptionTxt.setText(pin.getDescription());
        directionsTxt.setText(pin.getDirections());

        DateTime dateCreated = ISODateTimeFormat.dateTime().parseDateTime(pin.getDateCreated());
        String formattedDateCreated = DateFormat.format("MMM d h:mm aa", dateCreated.getMillis()).toString();
        dateCreatedTxt.setText(formattedDateCreated);

        ImageButton navigateBtn = (ImageButton) convertView.findViewById(R.id.navigateBtn);
        navigateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri pinUri = Uri.parse("google.navigation:q=" + pin.getLat() + "," + pin.getLng() + "&mode=w");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, pinUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                getContext().startActivity(mapIntent);
            }
        });

        return convertView;
    }
}
