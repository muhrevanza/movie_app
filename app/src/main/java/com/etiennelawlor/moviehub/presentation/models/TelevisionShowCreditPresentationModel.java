package com.etiennelawlor.moviehub.presentation.models;

/**
 * Created by etiennelawlor on 12/31/17.
 */

public class TelevisionShowCreditPresentationModel extends CreditPresentationModel {

    // region Constants
    public static final String SECURE_BASE_URL = "https://image.tmdb.org/t/p/";
    public static final String PROFILE_SIZE = "h632";
    // endregion

    // region Fields
    public String job;
    public String character;
    public String name;
    public String department;
    public String profilePath;
    // endregion

    // region Getters

    public String getJob() {
        return job;
    }

    public String getCharacter() {
        return character;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public String getProfileUrl(){
        String profileUrl = String.format("%s%s%s", SECURE_BASE_URL, PROFILE_SIZE, profilePath);
        return profileUrl;
    }

    // endregion

    // region Setters

    public void setJob(String job) {
        this.job = job;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    // endregion

    @Override
    public String toString() {
        return "TelevisionShowCreditPresentationModel{" +
                "job='" + job + '\'' +
                ", character='" + character + '\'' +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", profilePath='" + profilePath + '\'' +
                '}';
    }
}
