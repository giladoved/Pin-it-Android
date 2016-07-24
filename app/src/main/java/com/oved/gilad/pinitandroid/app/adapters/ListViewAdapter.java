package com.oved.gilad.pinitandroid.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.oved.gilad.pinitandroid.R;
import com.oved.gilad.pinitandroid.models.Pin;

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
        Pin pin = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listcell, parent, false);
        }

        TextView titleTxt = (TextView) convertView.findViewById(R.id.titleTxt);
        TextView locationTxt = (TextView) convertView.findViewById(R.id.locationTxt);

        titleTxt.setText(pin.getName());
        locationTxt.setText(pin.getLat() + ", " + pin.getLng());

        return convertView;
    }
}
