package com.etiennelawlor.moviehub.data.database.models;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by etiennelawlor on 5/14/17.
 */

public class MoviesRealmModel extends RealmObject {

    // region Fields
    private RealmList<MovieRealmModel> movies;
    @PrimaryKey
    private int pageNumber;
    private boolean isLastPage;
    private Date expiredAt;
    // endregion

    // region Getters

    public RealmList<MovieRealmModel> getMovies() {
        return movies;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public boolean isLastPage() {
        return isLastPage;
    }

    public Date getExpiredAt() {
        return expiredAt;
    }

    // endregion

    // region Setters

    public void setMovies(RealmList<MovieRealmModel> movies) {
        this.movies = movies;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }

    public void setExpiredAt(Date expiredAt) {
        this.expiredAt = expiredAt;
    }

    // endregion

}
