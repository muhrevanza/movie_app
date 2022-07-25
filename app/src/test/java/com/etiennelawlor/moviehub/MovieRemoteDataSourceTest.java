package com.etiennelawlor.moviehub;

import android.graphics.Movie;

import com.etiennelawlor.moviehub.data.network.MovieHubService;
import com.etiennelawlor.moviehub.data.network.response.MovieResponse;
import com.etiennelawlor.moviehub.data.network.response.MoviesResponse;
import com.etiennelawlor.moviehub.data.repositories.movie.MovieRemoteDataSource;
import com.etiennelawlor.moviehub.di.component.DaggerApplicationComponent;
import com.etiennelawlor.moviehub.di.module.ApplicationModule;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import io.reactivex.observers.TestObserver;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

/**
 * Created by etiennelawlor on 12/25/17.
 */

public class MovieRemoteDataSourceTest {

    private static final long MOVIE_ID = 181808L;
    private static final int PAGE = 1;

    private MockWebServer mockWebServer;
    private JsonReader jsonReader;
//    private MovieHubService movieHubService;
    private MovieRemoteDataSource movieRemoteDataSource;

    @Inject
    MovieHubService movieHubService;

//    @Inject
//    MovieHubService movieHubService;

    @Before
    public void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();


        jsonReader = new JsonReader();

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws
                    InterruptedException {
                String p = request.getPath();
                MockResponse response = new MockResponse().setResponseCode(200);
                if (p.equals("movie/popular")) {
                    return response.setBody(jsonReader.readString
                            ("json/popular_movies.json"));
                } else if (p.equals("movie/" + MOVIE_ID)) {
                    return response.setBody(jsonReader.readString
                            ("json/movie.json"));
                }

                return new MockResponse().setResponseCode(404);
            }
        };
        mockWebServer.setDispatcher(dispatcher);
        HttpUrl baseUrl = mockWebServer.url("/");

//        movieHubService = ServiceGenerator.createService(
//                MovieHubService.class,
//                MovieHubService.BASE_URL,
//                new AuthorizedNetworkInterceptor(context));

//        DaggerApplicationComponent.builder()
//                .applicationModule(new ApplicationModule(MovieHubApplication.getInstance()))
//                .build()
////                .createSubcomponent(new NetworkModule())
////                .plus(new NetworkModule())
//                .inject(this);

//        TestMovieHubApplication.getInstance().getApplicationComponent().inject(this);

        movieRemoteDataSource = new MovieRemoteDataSource(movieHubService);


    }

    @Test
    public void getPopularMovies() throws Exception {
        TestObserver<MoviesResponse> testObserver = new TestObserver<>();
        movieRemoteDataSource.getPopularMovies(PAGE)
                .subscribeWith(testObserver);

        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);

//        MoviesResponse moviesReponse = (MoviesResponse) testObserver.getEvents().get(0).get(0);

//        assertEquals( , moviesEnvelope.getMovies());


//        testSubscriber.assertNoErrors();
//        testSubscriber.assertValueCount(1);
//
//        // correctness of fields
//        Recipient r = testSubscriber.getOnNextEvents().get(0).get(0);
//        assertEquals("FIOT", r.getSubdivisionName());
//        assertEquals("Teacher", r.getProfileName());

    }

    @Test
    public void getMovie() throws Exception {
        TestObserver<MovieResponse> testObserver = new TestObserver<>();
        movieRemoteDataSource.getMovie(MOVIE_ID)
                .subscribeWith(testObserver);

        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);

//        MovieResponse movie = (MovieResponse) testObserver.getEvents().get(0).get(0);

//        assertEquals( , moviesEnvelope.getMovies());

    }

    @After
    public void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

}
