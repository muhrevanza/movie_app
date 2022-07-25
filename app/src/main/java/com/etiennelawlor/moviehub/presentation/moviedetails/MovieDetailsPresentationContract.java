package com.etiennelawlor.moviehub.presentation.moviedetails;

import com.etiennelawlor.moviehub.domain.models.MovieDetailsDomainModel;
import com.etiennelawlor.moviehub.presentation.base.BasePresenter;
import com.etiennelawlor.moviehub.presentation.models.MoviePresentationModel;
import com.etiennelawlor.moviehub.presentation.models.PersonPresentationModel;

/**
 * Created by etiennelawlor on 2/9/17.
 */

public interface MovieDetailsPresentationContract {

    interface View {
        void setMovieDetailsDomainModel(MovieDetailsDomainModel movieDetailsDomainModel);
        void showToolbarTitle();
        void hideToolbarTitle();
        void showErrorView();

        // Navigation methods
        void openPersonDetails(PersonPresentationModel person);
        void openMovieDetails(MoviePresentationModel movie);
    }

    interface Presenter extends BasePresenter {
        void onLoadMovieDetails(int movieId);
        void onPersonClick(PersonPresentationModel person);
        void onMovieClick(MoviePresentationModel movie);
        void onScrollChange(boolean isScrolledPastThreshold);
    }
}
