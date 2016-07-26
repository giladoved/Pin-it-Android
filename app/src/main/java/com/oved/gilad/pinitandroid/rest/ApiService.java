package com.oved.gilad.pinitandroid.rest;

/**
 * Created by gilad on 7/23/16.
 */

import com.oved.gilad.pinitandroid.models.Pin;
import com.oved.gilad.pinitandroid.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by gilad on 6/22/16.
 */
public interface ApiService {

    @GET("/users/{user_id}")
    public Call<User> getUserInfo(@Path("user_id") String user_id);

    @POST("/users/register")
    public Call<User> registerUser(@Body User user);

    @POST("/pins/new")
    public Call<Pin> addPin(@Body Pin pin);

    @GET("/pins/all")
    public Call<List<Pin>> getAllPins();
}
