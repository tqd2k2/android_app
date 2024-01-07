package com.hoanglan.sqrcauthenticationapp.api;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AuthRes {

    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("match_score")
    @Expose
    private Double matchScore;
    @SerializedName("refresh_token")
    @Expose
    private String refreshToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(Double matchScore) {
        this.matchScore = matchScore;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}