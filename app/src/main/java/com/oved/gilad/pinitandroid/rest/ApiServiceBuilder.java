package com.oved.gilad.pinitandroid.rest;
import com.oved.gilad.pinitandroid.utils.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by gilad on 6/22/16.
 */
public class ApiServiceBuilder {
    private static ApiServiceBuilder instance = null;

    private ApiService apiService;

    public ApiServiceBuilder() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static ApiServiceBuilder getInstance() {
        if(instance == null) {
            instance = new ApiServiceBuilder();
        }
        return instance;
    }

    public ApiService api() {
        return apiService;
    }
}
