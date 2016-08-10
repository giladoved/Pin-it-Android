package com.oved.gilad.pinitandroid.app.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.oved.gilad.pinitandroid.R;
import com.oved.gilad.pinitandroid.app.pages.MainActivity;
import com.oved.gilad.pinitandroid.models.Pin;
import com.oved.gilad.pinitandroid.utils.Constants;
import com.oved.gilad.pinitandroid.utils.PubSubBus;
import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.util.ArrayList;

/**
 * Created by gilad on 7/23/16.
 */
public class ListViewAdapter extends ArrayAdapter<Pin> {
    int position;
    ArrayList<Pin> pins;

    public ListViewAdapter(Context context, ArrayList<Pin> pins) {
        super(context, 0, pins);
        this.pins = pins;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Pin pin = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listcell, parent, false);
        }

        TextView titleTxt = (TextView) convertView.findViewById(R.id.titleTxt);
        //TextView descriptionTxt = (TextView) convertView.findViewById(R.id.descriptionTxt);
        //TextView directionsTxt = (TextView) convertView.findViewById(R.id.directionsTxt);
        TextView dateCreatedTxt = (TextView) convertView.findViewById(R.id.dateCreatedTxt);

        titleTxt.setText(pin.getTitle());
        //descriptionTxt.setText(pin.getDescription());
        //directionsTxt.setText(pin.getDirections());

        DateTime dateCreated = ISODateTimeFormat.dateTime().parseDateTime(pin.getDateCreated());
        String formattedDateCreated = DateFormat.format("MMM d h:mm aa", dateCreated.getMillis()).toString();
        dateCreatedTxt.setText(formattedDateCreated);

        ImageButton navigateBtn = (ImageButton) convertView.findViewById(R.id.navigateBtn);
        navigateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getContext();
                mainActivity.openMap();

                Bus bus = PubSubBus.getInstance();
                Constants.Log("pin: " + pin);
                Constants.Log("Pid: " + pin.getId());
                bus.post(pin.getId());

                //google nav
                /*Uri pinUri = Uri.parse("google.navigation:q=" + pin.getLat() + "," + pin.getLng() + "&mode=w");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, pinUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                getContext().startActivity(mapIntent);*/
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                int height = size.y;

                final ImageView imageView = new ImageView(getContext());
                Pin pin = pins.get(position);
                String imageLocation = Constants.PHOTO_LINK + pin.getImage();
                Picasso.with(getContext()).load(imageLocation).placeholder(R.drawable.pin)
                        .error(R.drawable.pingrey).resize(width - 100, height - 300).into(imageView);

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getContext()).
                                setMessage(pin.getTitle()).
                                setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).
                                setView(imageView);
                builder.create().show();
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle("Delete Pin");
                builder.setMessage("Are you sure you want to delete this pin forever?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences settings = getContext().getSharedPreferences(Constants.PREFS_NAME, 0);
                        String userId = settings.getString(Constants.ID_KEY, null);
                        if (userId != null) {
                            if (pins.get(position).getUserId().equals(userId)) {
                                remove(pins.get(position));
                                notifyDataSetChanged();

                                //TODO: call delete pin in the backend to actually remove pin
                            }
                        }
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

                return true;
            }
        });

        return convertView;
    }
}
