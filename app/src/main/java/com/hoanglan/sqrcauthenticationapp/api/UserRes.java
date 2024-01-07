package com.hoanglan.sqrcauthenticationapp.api;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserRes {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("username")
    @Expose
    private String username;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}