package com.etiennelawlor.moviehub.presentation.televisionshows;

import com.etiennelawlor.moviehub.domain.models.TelevisionShowDomainModel;
import com.etiennelawlor.moviehub.domain.models.TelevisionShowsDomainModel;
import com.etiennelawlor.moviehub.domain.usecases.TelevisionShowsDomainContract;
import com.etiennelawlor.moviehub.presentation.models.TelevisionShowPresentationModel;
import com.etiennelawlor.moviehub.util.rxjava.SchedulerProvider;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * Created by etiennelawlor on 2/9/17.
 */

public class TelevisionShowsPresenter implements TelevisionShowsPresentationContract.Presenter {

    // region Member Variables
    private final TelevisionShowsPresentationContract.View televisionShowsView;
    private final TelevisionShowsDomainContract.UseCase televisionShowsUseCase;
    private final SchedulerProvider schedulerProvider;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    // endregion

    // region Constructors
    public TelevisionShowsPresenter(TelevisionShowsPresentationContract.View televisionShowsView, TelevisionShowsDomainContract.UseCase televisionShowsUseCase, SchedulerProvider schedulerProvider) {
        this.televisionShowsView = televisionShowsView;
        this.televisionShowsUseCase = televisionShowsUseCase;
        this.schedulerProvider = schedulerProvider;
    }
    // endregion

    // region TelevisionShowsPresentationContract.Presenter Methods

    @Override
    public void onDestroyView() {
        if (compositeDisposable != null)
            compositeDisposable.clear();
    }

    @Override
    public void onLoadPopularTelevisionShows(final int currentPage) {
        if(currentPage == 1){
            televisionShowsView.hideEmptyView();
            televisionShowsView.hideErrorView();
            televisionShowsView.showLoadingView();
        } else{
            televisionShowsView.showLoadingFooterView();
        }

        Disposable disposable = televisionShowsUseCase.getPopularTelevisionShows(currentPage)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribeWith(new DisposableSingleObserver<TelevisionShowsDomainModel>() {
                    @Override
                    public void onSuccess(TelevisionShowsDomainModel televisionShowsDomainModel) {
                        if(televisionShowsDomainModel != null){
                            List<TelevisionShowDomainModel> televisionShowDomainModels = televisionShowsDomainModel.getTelevisionShows();
                            int currentPage = televisionShowsDomainModel.getPageNumber();
                            boolean isLastPage = televisionShowsDomainModel.isLastPage();
                            boolean hasTelevisionShows = televisionShowsDomainModel.hasTelevisionShows();
                            if(currentPage == 1){
                                televisionShowsView.hideLoadingView();

                                if(hasTelevisionShows){
                                    televisionShowsView.addHeaderView();
                                    televisionShowsView.showTelevisionShows(televisionShowDomainModels);

                                    if(!isLastPage)
                                        televisionShowsView.addFooterView();
                                } else {
                                    televisionShowsView.showEmptyView();
                                }
                            } else {
                                televisionShowsView.removeFooterView();

                                if(hasTelevisionShows){
                                    televisionShowsView.showTelevisionShows(televisionShowDomainModels);

                                    if(!isLastPage)
                                        televisionShowsView.addFooterView();
                                }
                            }

                            televisionShowsView.setTelevisionShowsDomainModel(televisionShowsDomainModel);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();

                        if(currentPage == 1){
                            televisionShowsView.hideLoadingView();

                            televisionShowsView.showErrorView();
                        } else {
                            televisionShowsView.showErrorFooterView();
                        }
                    }
                });

        compositeDisposable.add(disposable);
    }

    @Override
    public void onTelevisionShowClick(TelevisionShowPresentationModel televisionShow) {
        televisionShowsView.openTelevisionShowDetails(televisionShow);
    }

    @Override
    public void onScrollToEndOfList() {
        televisionShowsView.loadMoreTelevisionShows();
    }

    // endregion
}
