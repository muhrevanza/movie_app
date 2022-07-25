package com.etiennelawlor.moviehub;

import com.etiennelawlor.moviehub.domain.models.MovieDomainModel;
import com.etiennelawlor.moviehub.domain.models.MoviesDomainModel;
import com.etiennelawlor.moviehub.domain.usecases.MoviesDomainContract;
import com.etiennelawlor.moviehub.presentation.models.MoviePresentationModel;
import com.etiennelawlor.moviehub.presentation.movies.MoviesPresentationContract;
import com.etiennelawlor.moviehub.presentation.movies.MoviesPresenter;
import com.etiennelawlor.moviehub.util.rxjava.SchedulerProvider;
import com.etiennelawlor.moviehub.util.rxjava.TestSchedulerProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Single;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by etiennelawlor on 2/9/17.
 */

public class MoviesPresenterTest {

    // region Test Doubles

    // Mocks
    @Mock
    private MoviesPresentationContract.View mockMoviesView;
    @Mock
    private MoviesDomainContract.UseCase mockMoviesUseCase;

    // Stubs
    private MoviesDomainModel moviesDomainModelStub;
    // endregion

    // region Member Variables
    private MoviesPresenter moviesPresenter;
    private SchedulerProvider schedulerProvider = new TestSchedulerProvider();
    // endregion

    @Before
    public void setUp() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        moviesPresenter = new MoviesPresenter(mockMoviesView, mockMoviesUseCase, schedulerProvider);
    }

    // region Test Methods
//    @Test(expected = IOException.class)
    @Test
    public void onLoadPopularMovies_shouldShowError_whenFirstPageRequestFailed() {
        // 1. (Given) Set up conditions required for the test
        moviesDomainModelStub = getMoviesDomainModelStub(0, 1, true);

        when(mockMoviesUseCase.getPopularMovies(anyInt())).thenReturn(Single.error(new IOException()));

        // 2. (When) Then perform one or more actions
        moviesPresenter.onLoadPopularMovies(moviesDomainModelStub.getPageNumber());

        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verify(mockMoviesView).hideEmptyView();
        verify(mockMoviesView).hideErrorView();
        verify(mockMoviesView).showLoadingView();

        verify(mockMoviesView).hideLoadingView();
        verify(mockMoviesView).showErrorView();
    }

    @Test
    public void onLoadPopularMovies_shouldShowError_whenNextPageRequestFailed() {
        // 1. (Given) Set up conditions required for the test
        moviesDomainModelStub = getMoviesDomainModelStub(0, 2, true);

        when(mockMoviesUseCase.getPopularMovies(anyInt())).thenReturn(Single.error(new IOException()));

        // 2. (When) Then perform one or more actions
        moviesPresenter.onLoadPopularMovies(moviesDomainModelStub.getPageNumber());

        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verify(mockMoviesView).showLoadingFooterView();

        verify(mockMoviesView).showErrorFooterView();
    }

    @Test
    public void onLoadPopularMovies_shouldShowEmpty_whenFirstPageHasNoMovies() {
        // 1. (Given) Set up conditions required for the test
        moviesDomainModelStub = getMoviesDomainModelStub(0, 1, true);

        when(mockMoviesUseCase.getPopularMovies(anyInt())).thenReturn(Single.just(moviesDomainModelStub));

        // 2. (When) Then perform one or more actions
        moviesPresenter.onLoadPopularMovies(moviesDomainModelStub.getPageNumber());

        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verify(mockMoviesView).hideEmptyView();
        verify(mockMoviesView).hideErrorView();
        verify(mockMoviesView).showLoadingView();

        verify(mockMoviesView).hideLoadingView();
        verify(mockMoviesView).showEmptyView();
        verify(mockMoviesView).setMoviesDomainModel(moviesDomainModelStub);
    }

    @Test
    public void onLoadPopularMovies_shouldNotAddMovies_whenNextPageHasNoMovies() {
        // 1. (Given) Set up conditions required for the test
        moviesDomainModelStub = getMoviesDomainModelStub(0, 2, true);

        when(mockMoviesUseCase.getPopularMovies(anyInt())).thenReturn(Single.just(moviesDomainModelStub));

        // 2. (When) Then perform one or more actions
        moviesPresenter.onLoadPopularMovies(moviesDomainModelStub.getPageNumber());

        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verify(mockMoviesView).showLoadingFooterView();

        verify(mockMoviesView).removeFooterView();
        verify(mockMoviesView).setMoviesDomainModel(moviesDomainModelStub);
    }

    @Test
    public void onLoadPopularMovies_shouldAddMovies_whenFirstPageHasMoviesAndIsLastPage() {
        // 1. (Given) Set up conditions required for the test
        moviesDomainModelStub = getMoviesDomainModelStub(5, 1, true);

        when(mockMoviesUseCase.getPopularMovies(anyInt())).thenReturn(Single.just(moviesDomainModelStub));

        // 2. (When) Then perform one or more actions
        moviesPresenter.onLoadPopularMovies(moviesDomainModelStub.getPageNumber());

        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verify(mockMoviesView).hideEmptyView();
        verify(mockMoviesView).hideErrorView();
        verify(mockMoviesView).showLoadingView();

        verify(mockMoviesView).hideLoadingView();
        verify(mockMoviesView).addHeaderView();
        verify(mockMoviesView).showMovies(moviesDomainModelStub.getMovies());
        verify(mockMoviesView).setMoviesDomainModel(moviesDomainModelStub);
    }

    @Test
    public void onLoadPopularMovies_shouldAddMovies_whenFirstPageHasMoviesAndIsNotLastPage() {
        // 1. (Given) Set up conditions required for the test
        moviesDomainModelStub = getMoviesDomainModelStub(5, 1, false);

        when(mockMoviesUseCase.getPopularMovies(anyInt())).thenReturn(Single.just(moviesDomainModelStub));

        // 2. (When) Then perform one or more actions
        moviesPresenter.onLoadPopularMovies(moviesDomainModelStub.getPageNumber());

        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verify(mockMoviesView).hideEmptyView();
        verify(mockMoviesView).hideErrorView();
        verify(mockMoviesView).showLoadingView();

        verify(mockMoviesView).hideLoadingView();
        verify(mockMoviesView).addHeaderView();
        verify(mockMoviesView).showMovies(moviesDomainModelStub.getMovies());
        verify(mockMoviesView).addFooterView();
        verify(mockMoviesView).setMoviesDomainModel(moviesDomainModelStub);
    }

    @Test
    public void onLoadPopularMovies_shouldAddMovies_whenNextPageHasMoviesAndIsLastPage() {
        // 1. (Given) Set up conditions required for the test
        moviesDomainModelStub = getMoviesDomainModelStub(5, 2, true);

        when(mockMoviesUseCase.getPopularMovies(anyInt())).thenReturn(Single.just(moviesDomainModelStub));

        // 2. (When) Then perform one or more actions
        moviesPresenter.onLoadPopularMovies(moviesDomainModelStub.getPageNumber());

        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verify(mockMoviesView).showLoadingFooterView();

        verify(mockMoviesView).removeFooterView();
        verify(mockMoviesView).showMovies(moviesDomainModelStub.getMovies());
        verify(mockMoviesView).setMoviesDomainModel(moviesDomainModelStub);
    }

    @Test
    public void onLoadPopularMovies_shouldAddMovies_whenNextPageHasMoviesAndIsNotLastPage() {
        // 1. (Given) Set up conditions required for the test
        moviesDomainModelStub = getMoviesDomainModelStub(5, 2, false);

        when(mockMoviesUseCase.getPopularMovies(anyInt())).thenReturn(Single.just(moviesDomainModelStub));

        // 2. (When) Then perform one or more actions
        moviesPresenter.onLoadPopularMovies(moviesDomainModelStub.getPageNumber());

        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verify(mockMoviesView).showLoadingFooterView();

        verify(mockMoviesView).removeFooterView();
        verify(mockMoviesView).showMovies(moviesDomainModelStub.getMovies());
        verify(mockMoviesView).addFooterView();
        verify(mockMoviesView).setMoviesDomainModel(moviesDomainModelStub);
//        verify(mockMoviesView, times(1)).setModel(any(MoviesPresentationModel.class)); // Alternative verify check
    }

    @Test
    public void onMovieClick_shouldOpenMovieDetails() {
        // 1. (Given) Set up conditions required for the test
        MoviePresentationModel movie = new MoviePresentationModel();

        // 2. (When) Then perform one or more actions
        moviesPresenter.onMovieClick(movie);

        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verify(mockMoviesView).openMovieDetails(movie);

        verifyZeroInteractions(mockMoviesUseCase);
    }

    @Test
    public void onScrollToEndOfList_shouldLoadMoreItems() {
        // 1. (Given) Set up conditions required for the test

        // 2. (When) Then perform one or more actions
        moviesPresenter.onScrollToEndOfList();

        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verify(mockMoviesView).loadMoreMovies();

        verifyZeroInteractions(mockMoviesUseCase);
    }

    @Test
    public void onDestroyView_shouldNotInteractWithViewOrUsecase() {
        // 1. (Given) Set up conditions required for the test

        // 2. (When) Then perform one or more actions
        moviesPresenter.onDestroyView();
        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verifyZeroInteractions(mockMoviesView);
        verifyZeroInteractions(mockMoviesUseCase);
    }

    // endregion

    // region Helper Methods
    private List<MovieDomainModel> getListOfMovies(int numOfMovies){
        List<MovieDomainModel> movies = new ArrayList<>();
        for(int i=0; i<numOfMovies; i++){
            MovieDomainModel movie = new MovieDomainModel();
            movies.add(movie);
        }
        return movies;
    }

    private MoviesDomainModel getMoviesDomainModelStub(int numOfMovies, int pageNumber, boolean lastPage){
        MoviesDomainModel moviesDomainModel = new MoviesDomainModel();
        moviesDomainModel.setMovies(getListOfMovies(numOfMovies));
        moviesDomainModel.setPageNumber(pageNumber);
        moviesDomainModel.setLastPage(lastPage);
        moviesDomainModel.setExpiredAt(Calendar.getInstance().getTime());
        return moviesDomainModel;
    }
    // endregion
}
