package com.hdarha.happ.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OKResponse {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("url")
    @Expose
    private String url;

    /**
     * No args constructor for use in serialization
     *
     */
    public OKResponse() {
    }

    /**
     *
     * @param url
     * @param status
     */
    public OKResponse(String status, String url) {
        super();
        this.status = status;
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}