package com.etiennelawlor.moviehub.data.database.mappers;

import com.etiennelawlor.moviehub.data.database.models.MovieRealmModel;
import com.etiennelawlor.moviehub.data.repositories.models.MovieDataModel;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by etiennelawlor on 5/14/17.
 */

public class MovieRealmModelMapper implements RealmModelMapper<MovieDataModel, MovieRealmModel>, RealmModelListMapper<MovieDataModel, MovieRealmModel> {

    private GenreRealmModelMapper genreRealmMapper = new GenreRealmModelMapper();

    @Override
    public MovieRealmModel mapToRealmModel(MovieDataModel movieDataModel) {
        MovieRealmModel realmMovie = Realm.getDefaultInstance().createObject(MovieRealmModel.class);

        realmMovie.setAdult(movieDataModel.isAdult());
        realmMovie.setBackdropPath(movieDataModel.getBackdropPath());
        realmMovie.setBudget(movieDataModel.getBudget());
        realmMovie.setGenres(genreRealmMapper.mapListToRealmModelList(movieDataModel.getGenres()));
        realmMovie.setHomepage(movieDataModel.getHomepage());
        realmMovie.setId(movieDataModel.getId());
        realmMovie.setImdbId(movieDataModel.getImdbId());
        realmMovie.setOriginalLanguage(movieDataModel.getOriginalLanguage());
        realmMovie.setOriginalTitle(movieDataModel.getOriginalTitle());
        realmMovie.setOverview(movieDataModel.getOverview());
        realmMovie.setPopularity(movieDataModel.getPopularity());
        realmMovie.setPosterPath(movieDataModel.getPosterPath());
        realmMovie.setReleaseDate(movieDataModel.getReleaseDate());
        realmMovie.setRevenue(movieDataModel.getRevenue());
        realmMovie.setRuntime(movieDataModel.getRuntime());
        realmMovie.setStatus(movieDataModel.getStatus());
        realmMovie.setTagline(movieDataModel.getTagline());
        realmMovie.setTitle(movieDataModel.getTitle());
        realmMovie.setVideo(movieDataModel.isVideo());
        realmMovie.setVoteAverage(movieDataModel.getVoteAverage());
        realmMovie.setVoteCount(movieDataModel.getVoteCount());

        return realmMovie;
    }

    @Override
    public MovieDataModel mapFromRealmModel(MovieRealmModel movieRealmModel) {
        MovieDataModel movieDataModel = new MovieDataModel();
        movieDataModel.setAdult(movieRealmModel.isAdult());
        movieDataModel.setBackdropPath(movieRealmModel.getBackdropPath());
        movieDataModel.setBudget(movieRealmModel.getBudget());
        movieDataModel.setGenres(genreRealmMapper.mapListFromRealmModelList(movieRealmModel.getGenres()));
        movieDataModel.setHomepage(movieRealmModel.getHomepage());
        movieDataModel.setId(movieRealmModel.getId());
        movieDataModel.setImdbId(movieRealmModel.getImdbId());
        movieDataModel.setOriginalLanguage(movieRealmModel.getOriginalLanguage());
        movieDataModel.setOriginalTitle(movieRealmModel.getOriginalTitle());
        movieDataModel.setOverview(movieRealmModel.getOverview());
        movieDataModel.setPopularity(movieRealmModel.getPopularity());
        movieDataModel.setPosterPath(movieRealmModel.getPosterPath());
        movieDataModel.setReleaseDate(movieRealmModel.getReleaseDate());
        movieDataModel.setRevenue(movieRealmModel.getRevenue());
        movieDataModel.setRuntime(movieRealmModel.getRuntime());
        movieDataModel.setStatus(movieRealmModel.getStatus());
        movieDataModel.setTagline(movieRealmModel.getTagline());
        movieDataModel.setTitle(movieRealmModel.getTitle());
        movieDataModel.setVideo(movieRealmModel.isVideo());
        movieDataModel.setVoteAverage(movieRealmModel.getVoteAverage());
        movieDataModel.setVoteCount(movieRealmModel.getVoteCount());
        return movieDataModel;
    }

    @Override
    public RealmList<MovieRealmModel> mapListToRealmModelList(List<MovieDataModel> movieDataModels) {
        RealmList<MovieRealmModel> movieRealmModels = new RealmList<>();
        for(MovieDataModel movieDataModel : movieDataModels){
            movieRealmModels.add(mapToRealmModel(movieDataModel));
        }
        return movieRealmModels;
    }

    @Override
    public List<MovieDataModel> mapListFromRealmModelList(RealmList<MovieRealmModel> movieRealmModels) {
        List<MovieDataModel> movieDataModels = new ArrayList<>();
        for(MovieRealmModel movieRealmModel : movieRealmModels){
            movieDataModels.add(mapFromRealmModel(movieRealmModel));
        }
        return movieDataModels;
    }
}
