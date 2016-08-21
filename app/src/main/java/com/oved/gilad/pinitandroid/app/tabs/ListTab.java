package com.oved.gilad.pinitandroid.app.tabs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oved.gilad.pinitandroid.R;
import com.oved.gilad.pinitandroid.app.adapters.ListViewAdapter;
import com.oved.gilad.pinitandroid.models.Pin;
import com.oved.gilad.pinitandroid.rest.ApiServiceBuilder;
import com.oved.gilad.pinitandroid.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListTab extends ListFragment {

    ArrayList<Pin> pins;
    SwipeRefreshLayout swipeList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_list_view, container, false);

        swipeList = (SwipeRefreshLayout) inflatedView.findViewById(R.id.swipeList);
        swipeList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPins();
            }
        });
        swipeList.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);

        loadPins();

        return inflatedView;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            loadPins();
        }
    }

    public void loadPins() {
        SharedPreferences settings = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        String userId = settings.getString(Constants.ID_KEY, null);
        if (userId != null) {
            Constants.Log("userid: " + userId);
            final Call<List<Pin>> getAllPinsCall = ApiServiceBuilder.getInstance().api().getAllPins();
            getAllPinsCall.enqueue(new Callback<List<Pin>>() {
                @Override
                public void onResponse(Call<List<Pin>> call, Response<List<Pin>> response) {
                    swipeList.setRefreshing(false);
                    if (response.isSuccessful()) {
                        Constants.Log("successful!");
                        pins = new ArrayList<Pin>(response.body());
                        Constants.Log("successful!: " + pins.size());

                        if (pins.size() == 0) {
                            return;
                        }

                        getListView().setAdapter(new ListViewAdapter(getContext(), pins));
                    } else {
                        Constants.Error("Error getting the pins");
                    }
                }

                @Override
                public void onFailure(Call<List<Pin>> call, Throwable t) {
                    swipeList.setRefreshing(false);
                    Constants.Error("Error getting the pins: " + t.getMessage());
                }
            });
        }
    }
}
