package com.etiennelawlor.moviehub.data.repositories.mappers;

import com.etiennelawlor.moviehub.data.network.response.MovieResponse;
import com.etiennelawlor.moviehub.data.repositories.models.MovieDataModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by etiennelawlor on 12/30/17.
 */

public class MovieDataModelMapper implements DataModelMapper<MovieResponse, MovieDataModel>, DataModelListMapper<MovieResponse, MovieDataModel> {

    // region Member Variables
    private GenreDataModelMapper genreDataModelMapper = new GenreDataModelMapper();
    // endregion

    @Override
    public MovieDataModel mapToDataModel(MovieResponse movieResponse) {
        MovieDataModel movieDataModel = new MovieDataModel();
        movieDataModel.setAdult(movieResponse.isAdult());
        movieDataModel.setBackdropPath(movieResponse.getBackdropPath());
        movieDataModel.setBudget(movieResponse.getBudget());
        movieDataModel.setGenres(genreDataModelMapper.mapListToDataModelList(movieResponse.getGenres()));
        movieDataModel.setHomepage(movieResponse.getHomepage());
        movieDataModel.setId(movieResponse.getId());
        movieDataModel.setImdbId(movieResponse.getImdbId());
        movieDataModel.setOriginalLanguage(movieResponse.getOriginalLanguage());
        movieDataModel.setOriginalTitle(movieResponse.getOriginalTitle());
        movieDataModel.setOverview(movieResponse.getOverview());
        movieDataModel.setPopularity(movieResponse.getPopularity());
        movieDataModel.setPosterPath(movieResponse.getPosterPath());
        movieDataModel.setReleaseDate(movieResponse.getReleaseDate());
        movieDataModel.setRevenue(movieResponse.getRevenue());
        movieDataModel.setRuntime(movieResponse.getRuntime());
        movieDataModel.setStatus(movieResponse.getStatus());
        movieDataModel.setTagline(movieResponse.getTagline());
        movieDataModel.setTitle(movieResponse.getTitle());
        movieDataModel.setVideo(movieResponse.isVideo());
        movieDataModel.setVoteAverage(movieResponse.getVoteAverage());
        movieDataModel.setVoteCount(movieResponse.getVoteCount());
        return movieDataModel;
    }

    @Override
    public List<MovieDataModel> mapListToDataModelList(List<MovieResponse> movieResponses) {
        List<MovieDataModel> movieDataModels = new ArrayList<>();
        if(movieResponses != null && movieResponses.size()>0) {
            for (MovieResponse movieResponse : movieResponses) {
                movieDataModels.add(mapToDataModel(movieResponse));
            }
        }
        return movieDataModels;
    }
}
