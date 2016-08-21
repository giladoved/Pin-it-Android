package com.oved.gilad.pinitandroid.app.custom;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.oved.gilad.pinitandroid.R;

/**
 * Created by gilad on 8/20/16.
 */
public class CustomIconRenderer extends DefaultClusterRenderer<PinCluster> {

    public CustomIconRenderer(Context context, GoogleMap map,
                           ClusterManager<PinCluster> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(PinCluster item, MarkerOptions markerOptions) {
        markerOptions.title(item.getPin().getTitle());
        if (item.isMyPin()) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pingrey));
        } else {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin));
        }
        markerOptions.snippet(item.getPin().getDescription());
        super.onBeforeClusterItemRendered(item, markerOptions);
    }

    @Override
    protected void onClusterItemRendered(PinCluster clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);

    }
}
