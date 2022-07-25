package com.etiennelawlor.moviehub.data.repositories.models;

import java.util.Date;
import java.util.List;

/**
 * Created by etiennelawlor on 12/30/17.
 */

public class MovieCreditsDataModel {

    // region Fields
    public int id;
    public List<MovieCreditDataModel> cast = null;
    public List<MovieCreditDataModel> crew = null;
    private Date expiredAt;
    // endregion

    // region Getters

    public int getId() {
        return id;
    }

    public List<MovieCreditDataModel> getCast() {
        return cast;
    }

    public List<MovieCreditDataModel> getCrew() {
        return crew;
    }

    public Date getExpiredAt() {
        return expiredAt;
    }

    // endregion

    // region Setters

    public void setId(int id) {
        this.id = id;
    }

    public void setCast(List<MovieCreditDataModel> cast) {
        this.cast = cast;
    }

    public void setCrew(List<MovieCreditDataModel> crew) {
        this.crew = crew;
    }

    public void setExpiredAt(Date expiredAt) {
        this.expiredAt = expiredAt;
    }

    // endregion

    @Override
    public String toString() {
        return "MovieCreditsDataModel{" +
                "id=" + id +
                ", cast=" + cast +
                ", crew=" + crew +
                ", expiredAt=" + expiredAt +
                '}';
    }
}
