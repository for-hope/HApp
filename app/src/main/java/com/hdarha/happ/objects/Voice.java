package com.hdarha.happ.objects;


import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Voice {

    @SerializedName("caption")
    @Expose
    private String caption;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("tags")
    @Expose
    private List<String> tags = null;

    private boolean fav = false;

    private long duration = 0;



    /**
     * No args constructor for use in serialization
     *
     */
    public Voice() {
    }

    /**
     *
     * @param name
     * @param caption
     * @param location
     * @param tags
     */
    public Voice(String caption, String name, String location, List<String> tags) {
        super();
        this.caption = caption;
        this.name = name;
        this.location = location;
        this.tags = tags;
    }



    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}