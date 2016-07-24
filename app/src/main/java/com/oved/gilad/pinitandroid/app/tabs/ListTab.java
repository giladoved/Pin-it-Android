package com.oved.gilad.pinitandroid.app.tabs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_list_view, container, false);

        SharedPreferences settings = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        String userId = settings.getString(Constants.ID_KEY, null);
        if (userId != null) {
            final Call<List<Pin>> getAllPinsCall = ApiServiceBuilder.getInstance().api().getAllPins(userId);
            getAllPinsCall.enqueue(new Callback<List<Pin>>() {
                @Override
                public void onResponse(Call<List<Pin>> call, Response<List<Pin>> response) {
                    if (response.isSuccessful()) {
                        pins = new ArrayList<Pin>(response.body());

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
                    Constants.Error("Error getting the pins: " + t.getMessage());
                }
            });
        }

        return inflatedView;
    }
}
