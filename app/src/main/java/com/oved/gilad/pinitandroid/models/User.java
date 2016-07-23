package com.oved.gilad.pinitandroid.models;

import javax.annotation.Generated;

/**
 * Created by gilad on 6/22/16.
 */

@Generated("org.jsonschema2pojo")
public class User {

    private String _id;
    private String name;
    private String date_created;

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
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
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
