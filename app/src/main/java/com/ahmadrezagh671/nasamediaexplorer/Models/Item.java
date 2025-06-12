package com.ahmadrezagh671.nasamediaexplorer.Models;

import java.util.Arrays;
import java.util.List;

public class Item {

    String href;
    String center,date_created,description,location,media_type,nasa_id,title;
    String[] keywords;
    List<ItemLink> links;

    public Item(ItemData itemData, List<ItemLink> links,String href) {
        this.href = href;
        this.center = itemData.getCenter();
        this.date_created = itemData.getDate_created();
        this.description = itemData.getDescription();
        this.location = itemData.getLocation();
        this.media_type = itemData.getMedia_type();
        this.nasa_id = itemData.getNasa_id();
        this.title = itemData.getTitle();
        this.keywords = itemData.getKeywords();
        this.links = links;
    }

    @Override
    public String toString() {
        String linksString = "no link found";
        if (links != null)
            linksString = links.toString();
        return "Item{" +
                "href='" + href + '\'' +
                ", center='" + center + '\'' +
                ", date_created='" + date_created + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", media_type='" + media_type + '\'' +
                ", nasa_id='" + nasa_id + '\'' +
                ", title='" + title + '\'' +
                ", keywords=" + Arrays.toString(keywords) +
                ", links=" +  linksString +
                '}';
    }

    public String getKeywordsString(){
        if (keywords == null || keywords.length == 0) return "";

        StringBuilder keywordsString = new StringBuilder();
        for (int i = 0; i < keywords.length; i++){
            keywordsString.append(keywords[i]);
            if (i != keywords.length - 1){
                keywordsString.append(", ");
            }
        }
        return keywordsString.toString();
    }

    public ItemLink getPreviewLink(){
        if (links == null){
            return null;
        }else {
            for (ItemLink link : links) {
                if (link.getRel().equals("preview")) {
                    return link;
                }
            }
        }
        return null;
    }
    public ItemLink getCanonicalLink(){
        for (ItemLink link : links) {
            if (link.getRel().equals("canonical")) {
                return link;
            }
        }
        return null;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
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

    public List<ItemLink> getLinks() {
        return links;
    }

    public void setLinks(List<ItemLink> links) {
        this.links = links;
    }
}


