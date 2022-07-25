package com.etiennelawlor.moviehub.presentation.persons;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.etiennelawlor.moviehub.MovieHubApplication;
import com.etiennelawlor.moviehub.R;
import com.etiennelawlor.moviehub.di.component.PersonsComponent;
import com.etiennelawlor.moviehub.di.module.PersonsModule;
import com.etiennelawlor.moviehub.domain.models.PersonDomainModel;
import com.etiennelawlor.moviehub.domain.models.PersonsDomainModel;
import com.etiennelawlor.moviehub.presentation.base.BaseAdapter;
import com.etiennelawlor.moviehub.presentation.base.BaseFragment;
import com.etiennelawlor.moviehub.presentation.mappers.PersonPresentationModelMapper;
import com.etiennelawlor.moviehub.presentation.mappers.PersonsPresentationModelMapper;
import com.etiennelawlor.moviehub.presentation.models.PersonPresentationModel;
import com.etiennelawlor.moviehub.presentation.models.PersonsPresentationModel;
import com.etiennelawlor.moviehub.presentation.persondetails.PersonDetailsActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * Created by etiennelawlor on 12/16/16.
 */

public class PersonsFragment extends BaseFragment implements PersonsAdapter.OnItemClickListener, PersonsAdapter.OnReloadClickListener, PersonsPresentationContract.View {

    // region Views
    @BindView(R.id.rv)
    RecyclerView recyclerView;
    @BindView(R.id.error_ll)
    LinearLayout errorLinearLayout;
    @BindView(R.id.pb)
    ProgressBar progressBar;
    @BindView(android.R.id.empty)
    LinearLayout emptyLinearLayout;

    private View selectedPersonView;
    // endregion

    // region Member Variables
    private PersonsAdapter personsAdapter;
    private Unbinder unbinder;
    private StaggeredGridLayoutManager layoutManager;
    private PersonsPresentationModel personsPresentationModel;
    private PersonsComponent personsComponent;
    private boolean isLoading = false;
    private PersonsPresentationModelMapper personsPresentationModelMapper = new PersonsPresentationModelMapper();
    private PersonPresentationModelMapper personPresentationModelMapper = new PersonPresentationModelMapper();
    // endregion

    // region Injected Variables
    @Inject
    PersonsPresentationContract.Presenter personsPresenter;
    // endregion

    // region Listeners
    @OnClick(R.id.retry_btn)
    public void onReloadButtonClicked() {
        personsPresenter.onLoadPopularPersons(personsPresentationModel == null ? 1 : personsPresentationModel.getPageNumber());
    }

    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(final RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int visibleItemCount = recyclerView.getChildCount();
            int totalItemCount = recyclerView.getAdapter().getItemCount();
            int[] positions = layoutManager.findFirstVisibleItemPositions(null);
            int firstVisibleItem = positions[1];

            if ((visibleItemCount + firstVisibleItem) >= totalItemCount
                    && totalItemCount > 0
                    && !isLoading
                    && !personsPresentationModel.isLastPage()) {
                personsPresenter.onScrollToEndOfList();
            }
        }
    };

    // endregion

    // region Constructors
    public PersonsFragment() {
    }
    // endregion

    // region Factory Methods
    public static PersonsFragment newInstance() {
        return new PersonsFragment();
    }

    public static PersonsFragment newInstance(Bundle extras) {
        PersonsFragment fragment = new PersonsFragment();
        fragment.setArguments(extras);
        return fragment;
    }
    // endregion

    // region Lifecycle Methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createPersonsComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_people, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        personsAdapter = new PersonsAdapter(getContext());
        personsAdapter.setOnItemClickListener(this);
        personsAdapter.setOnReloadClickListener(this);
        recyclerView.setItemAnimator(new SlideInUpAnimator());
        recyclerView.setAdapter(personsAdapter);

        // Pagination
        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);

        personsPresenter.onLoadPopularPersons(personsPresentationModel == null ? 1 : personsPresentationModel.getPageNumber());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        removeListeners();
        unbinder.unbind();
        personsPresenter.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        releasePersonsComponent();
    }
    // endregion

    // region PersonsAdapter.OnItemClickListener Methods
    @Override
    public void onItemClick(int position, View view) {
        selectedPersonView = view;
        PersonPresentationModel person = personsAdapter.getItem(position);
        if(person != null){
            personsPresenter.onPersonClick(person);
        }
    }
    // endregion

    // region PersonsAdapter.OnReloadClickListener Methods
    @Override
    public void onReloadClick() {
        personsPresenter.onLoadPopularPersons(personsPresentationModel.getPageNumber());
    }
    // endregion

    // region PersonsPresentationContract.View Methods

    @Override
    public void showEmptyView() {
        emptyLinearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideEmptyView() {
        emptyLinearLayout.setVisibility(View.GONE);
    }

    @Override
    public void showErrorView() {
        errorLinearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideErrorView() {
        errorLinearLayout.setVisibility(View.GONE);
    }

    @Override
    public void showLoadingView() {
        progressBar.setVisibility(View.VISIBLE);
        isLoading = true;
    }

    @Override
    public void hideLoadingView() {
        progressBar.setVisibility(View.GONE);
        isLoading = false;
    }

    @Override
    public void addHeaderView() {
        personsAdapter.addHeader();
    }

    @Override
    public void addFooterView() {
        personsAdapter.addFooter();
    }

    @Override
    public void removeFooterView() {
        personsAdapter.removeFooter();
        isLoading = false;
    }

    @Override
    public void showErrorFooterView() {
        personsAdapter.updateFooter(BaseAdapter.FooterType.ERROR);
    }

    @Override
    public void showLoadingFooterView() {
        personsAdapter.updateFooter(BaseAdapter.FooterType.LOAD_MORE);
        isLoading = true;
    }

    @Override
    public void showPersons(List<PersonDomainModel> persons) {
        personsAdapter.addAll(personPresentationModelMapper.mapListToPresentationModelList(persons));
    }

    @Override
    public void loadMorePersons() {
        personsPresentationModel.incrementPageNumber();
        personsPresenter.onLoadPopularPersons(personsPresentationModel.getPageNumber());
    }

    @Override
    public void setPersonsDomainModel(PersonsDomainModel personsDomainModel) {
        this.personsPresentationModel = personsPresentationModelMapper.mapToPresentationModel(personsDomainModel);
    }

    @Override
    public void openPersonDetails(PersonPresentationModel person) {
        Window window = getActivity().getWindow();
//            window.setStatusBarColor(primaryDark);

        Pair<View, String> personPair  = getPersonPair();
        ActivityOptionsCompat options = getActivityOptionsCompat(personPair);

        window.setExitTransition(null);
        ActivityCompat.startActivity(getActivity(), PersonDetailsActivity.createIntent(getContext(), person), options.toBundle());
    }

    // endregion

    // region Helper Methods
    private void removeListeners() {
        personsAdapter.setOnItemClickListener(null);
    }

    private ActivityOptionsCompat getActivityOptionsCompat(Pair pair){
        ActivityOptionsCompat options = null;

        Pair<View, String> bottomNavigationViewPair = getBottomNavigationViewPair();
        Pair<View, String> statusBarPair = getStatusBarPair();
        Pair<View, String> navigationBarPair  = getNavigationBarPair();
        Pair<View, String> appBarPair  = getAppBarPair();

        if(pair!=null
                && bottomNavigationViewPair != null
                && statusBarPair!= null
                && navigationBarPair!= null
                && appBarPair!= null){
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                    pair, bottomNavigationViewPair, statusBarPair, navigationBarPair, appBarPair);
        } else if(pair != null
                && bottomNavigationViewPair != null
                && statusBarPair != null
                && appBarPair!= null){
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                    pair, bottomNavigationViewPair, statusBarPair, appBarPair);
        } else if(pair != null
                && bottomNavigationViewPair != null
                && navigationBarPair != null
                && appBarPair!= null){
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                    pair, bottomNavigationViewPair, navigationBarPair, appBarPair);
        }

        return options;
    }

    private Pair<View, String> getPersonPair(){
        Resources resources = getResources();
        String transitionName = resources.getString(R.string.transition_person_thumbnail);
        View view = selectedPersonView.findViewById(R.id.thumbnail_iv);
        return getPair(view, transitionName);
    }

    private Pair<View, String> getBottomNavigationViewPair(){
        Resources resources = getResources();
        String transitionName = resources.getString(R.string.transition_bottom_navigation);
        View view = getActivity().findViewById(R.id.bottom_navigation);
        return getPair(view, transitionName);
    }

    private Pair<View, String> getStatusBarPair(){
        View view = getActivity().findViewById(android.R.id.statusBarBackground);
        return getPair(view);
    }

    private Pair<View, String> getNavigationBarPair(){
        View view = getActivity().findViewById(android.R.id.navigationBarBackground);
        return getPair(view);
    }

    private Pair<View, String> getAppBarPair(){
        Resources resources = getResources();
        String transitionName = resources.getString(R.string.transition_app_bar);
        View view = getActivity().findViewById(R.id.appbar);
        return getPair(view, transitionName);
    }

    private Pair<View, String> getPair(View view, String transitionName){
        Pair<View, String> pair = null;
        if(view != null) {
            pair = Pair.create(view, transitionName);
        }
        return pair;
    }

    private Pair<View, String> getPair(View view){
        Pair<View, String> pair = null;
        if(view != null) {
            pair = Pair.create(view, view.getTransitionName());
        }
        return pair;
    }

    public void scrollToTop(){
        recyclerView.scrollToPosition(0);
    }

    private PersonsComponent createPersonsComponent(){
        personsComponent = ((MovieHubApplication)getActivity().getApplication())
                .getApplicationComponent()
                .createSubcomponent(new PersonsModule(this));
        return personsComponent;
    }

    public void releasePersonsComponent(){
        personsComponent = null;
    }
    // endregion
}
