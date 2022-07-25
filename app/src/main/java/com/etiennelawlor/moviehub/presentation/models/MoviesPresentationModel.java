package com.etiennelawlor.moviehub.presentation.models;

import java.util.Date;
import java.util.List;

/**
 * Created by etiennelawlor on 2/20/17.
 */

public class MoviesPresentationModel {

    // region Fields
    private List<MoviePresentationModel> movies;
    private int pageNumber;
    private boolean isLastPage;
    private Date expiredAt;
    // endregion

    // region Constructors

    public MoviesPresentationModel() {
    }

    // endregion

    // region Getters

    public List<MoviePresentationModel> getMovies() {
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

    public void setMovies(List<MoviePresentationModel> movies) {
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

    // Helper Methods
    public boolean hasMovies() { return movies.size() > 0;}

    public void incrementPageNumber() { this.pageNumber += 1; }
    // endregion

    @Override
    public String toString() {
        return "MoviesPresentationModel{" +
                "movies=" + movies +
                ", pageNumber=" + pageNumber +
                ", isLastPage=" + isLastPage +
                ", expiredAt=" + expiredAt +
                '}';
    }
}
