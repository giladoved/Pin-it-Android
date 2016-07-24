package com.oved.gilad.pinitandroid.models;

/**
 * Created by gilad on 7/23/16.
 */
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Pin {

    private String _id;
    private String user_id;
    private String name;
    private String description;
    private String directions;
    private Double lat;
    private Double lng;
    private String date_created;

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return _id;
    }

    /**
     *
     * @param id
     * The id
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
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
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
}
