package com.etiennelawlor.moviehub.data.network.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by etiennelawlor on 12/16/16.
 */

public class PersonCreditResponse extends CreditResponse {

    // region Fields
    @SerializedName("job")
    public String job;
    @SerializedName("character")
    public String character;
    @SerializedName("title")
    public String title;
    @SerializedName("name")
    public String name;
    @SerializedName("department")
    public String department;
    @SerializedName("poster_path")
    public String posterPath;
    @SerializedName("first_air_date")
    public String firstAirDate;
    @SerializedName("release_date")
    public String releaseDate;
    @SerializedName("media_type")
    public String mediaType;
    // endregion

    // region Getters

    public String getJob() {
        return job;
    }

    public String getCharacter() {
        return character;
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getFirstAirDate() {
        return firstAirDate;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getMediaType() {
        return mediaType;
    }

    // endregion

    // region Setters

    public void setJob(String job) {
        this.job = job;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setFirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    // endregion

    @Override
    public String toString() {
        return "PersonCreditResponse{" +
                "job='" + job + '\'' +
                ", character='" + character + '\'' +
                ", title='" + title + '\'' +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", firstAirDate='" + firstAirDate + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", mediaType='" + mediaType + '\'' +
                '}';
    }
}
