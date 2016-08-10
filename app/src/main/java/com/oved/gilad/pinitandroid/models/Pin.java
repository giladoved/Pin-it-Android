package com.oved.gilad.pinitandroid.models;

/**
 * Created by gilad on 7/23/16.
 */

import android.graphics.Bitmap;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Pin {

    private String _id;
    private String user_id;
    private String username;
    private String image;
    private String url;
    private String title;
    private String description;
    private String directions;
    private Double lat;
    private Double lng;
    private String date_created;
    private Bitmap bitmap;

    /**
     * @return The id
     */
    public String getId() {
        return _id;
    }

    /**
     * @param id The _id
     */
    public void setId(String id) {
        this._id = id;
    }


    /**
     *
     * @return
     * The userId
     */
    public String getUserId() {
        return user_id;
    }

    /**
     *
     * @param userId
     * The user_id
     */
    public void setUserId(String userId) {
        this.user_id = userId;
    }

    /**
     *
     * @return
     * The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     * The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     * The username
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username
     * The username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return
     * The image
     */
    public String getImage() {
        return image;
    }

    /**
     *
     * @param image
     * The image
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     *
     * @return
     * The url
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url
     * The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     *
     * @return
     * The description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     * The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     * The directions
     */
    public String getDirections() {
        return directions;
    }

    /**
     *
     * @param directions
     * The directions
     */
    public void setDirections(String directions) {
        this.directions = directions;
    }

    /**
     *
     * @return
     * The lat
     */
    public Double getLat() {
        return lat;
    }

    /**
     *
     * @param lat
     * The lat
     */
    public void setLat(Double lat) {
        this.lat = lat;
    }

    /**
     *
     * @return
     * The lng
     */
    public Double getLng() {
        return lng;
    }

    /**
     *
     * @param lng
     * The lng
     */
    public void setLng(Double lng) {
        this.lng = lng;
    }

    /**
     * @return The dateCreated
     */
    public String getDateCreated() {
        return date_created;
    }

    /**
     * @param dateCreated The date_created
     */
    public void setDateCreated(String dateCreated) {
        this.date_created = dateCreated;
    }

    /**
     *
     * @return
     * The bitmap
     */
    public Bitmap getBitmap() {
        return bitmap;
    }

    /**
     *
     * @param bitmap
     * The bitmap
     */
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
