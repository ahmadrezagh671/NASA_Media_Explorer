package com.ahmadrezagh671.nasamediaexplorer.Models;

public class ItemData {

    String center,date_created,description,location,media_type,nasa_id,title;
    String[] keywords;

    public ItemData(String center, String date_created, String description, String location, String media_type, String nasa_id, String title, String[] keywords) {
        this.center = center;
        this.date_created = date_created;
        this.description = description;
        this.location = location;
        this.media_type = media_type;
        this.nasa_id = nasa_id;
        this.title = title;
        this.keywords = keywords;
    }

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    public String getNasa_id() {
        return nasa_id;
    }

    public void setNasa_id(String nasa_id) {
        this.nasa_id = nasa_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getKeywords() {
        return keywords;
    }

    public void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }
}
