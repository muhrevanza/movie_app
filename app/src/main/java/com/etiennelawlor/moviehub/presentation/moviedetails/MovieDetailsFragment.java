package com.etiennelawlor.moviehub.presentation.moviedetails;

import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Transition;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.etiennelawlor.moviehub.MovieHubApplication;
import com.etiennelawlor.moviehub.R;
import com.etiennelawlor.moviehub.di.component.MovieDetailsComponent;
import com.etiennelawlor.moviehub.di.module.MovieDetailsModule;
import com.etiennelawlor.moviehub.domain.models.MovieDetailsDomainModel;
import com.etiennelawlor.moviehub.presentation.base.BaseAdapter;
import com.etiennelawlor.moviehub.presentation.base.BaseFragment;
import com.etiennelawlor.moviehub.presentation.common.GravitySnapHelper;
import com.etiennelawlor.moviehub.presentation.mappers.MovieDetailsPresentationModelMapper;
import com.etiennelawlor.moviehub.presentation.models.GenrePresentationModel;
import com.etiennelawlor.moviehub.presentation.models.MovieCreditPresentationModel;
import com.etiennelawlor.moviehub.presentation.models.MovieDetailsPresentationModel;
import com.etiennelawlor.moviehub.presentation.models.MoviePresentationModel;
import com.etiennelawlor.moviehub.presentation.models.PersonPresentationModel;
import com.etiennelawlor.moviehub.presentation.persondetails.PersonDetailsActivity;
import com.etiennelawlor.moviehub.util.AnimationUtility;
import com.etiennelawlor.moviehub.util.ColorUtility;
import com.etiennelawlor.moviehub.util.DateUtility;
import com.etiennelawlor.moviehub.util.DisplayUtility;
import com.etiennelawlor.moviehub.util.FontCache;
import com.etiennelawlor.moviehub.util.TrestleUtility;
import com.etiennelawlor.moviehub.util.ViewUtility;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.etiennelawlor.moviehub.presentation.moviedetails.MovieDetailsActivity.KEY_MOVIE;

/**
 * Created by etiennelawlor on 12/18/16.
 */

public class MovieDetailsFragment extends BaseFragment implements MovieDetailsPresentationContract.View {

    // region Constants
    public static final String PATTERN = "yyyy-MM-dd";
    private static final float SCRIM_ADJUSTMENT = 0.075f;
    private static final int DELAY = 0;
    private static final int START_OFFSET = 500;
    public static final String BACKDROP_SIZE = "w780";
    public static final String POSTER_SIZE = "w342";
    public static final String SECURE_BASE_URL = "https://image.tmdb.org/t/p/";
    // endregion

    // region Views
    @BindView(R.id.main_content)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;
    @BindView(R.id.backdrop_iv)
    ImageView backdropImageView;
    @BindView(R.id.backdrop_fl)
    FrameLayout backdropFrameLayout;
    @BindView(R.id.movie_poster_iv)
    ImageView moviePosterImageView;
    @BindView(R.id.title_tv)
    TextView titleTextView;
    @BindView(R.id.duration_tv)
    TextView durationTextView;
    @BindView(R.id.genres_tv)
    TextView genresTextView;
    @BindView(R.id.overview_tv)
    TextView overviewTextView;
    @BindView(R.id.status_tv)
    TextView statusTextView;
    @BindView(R.id.release_date_tv)
    TextView releaseDateTextView;
    @BindView(R.id.budget_tv)
    TextView budgetTextView;
    @BindView(R.id.revenue_tv)
    TextView revenueTextView;
    @BindView(R.id.rating_tv)
    TextView ratingTextView;
    @BindView(R.id.movie_details_header_ll)
    LinearLayout movieDetailsHeaderLinearLayout;
    @BindView(R.id.movie_details_body_ll)
    LinearLayout movieDetailsBodyLinearLayout;
    @BindView(R.id.nsv)
    NestedScrollView nestedScrollView;
    @BindView(R.id.cast_vs)
    ViewStub castViewStub;
    @BindView(R.id.crew_vs)
    ViewStub crewViewStub;
    @BindView(R.id.similar_movies_vs)
    ViewStub similarMoviesViewStub;
    @BindView(R.id.pb)
    ProgressBar progressBar;

    private View selectedPersonView;
    private View selectedMovieView;
    // endregion

    // region Member Variables
    private MoviePresentationModel movie;
    private Unbinder unbinder;
    private Typeface font;
    private int moviePosterHeight;
    private int padding;
    private int statusBarColor;
    private SimilarMoviesAdapter similarMoviesAdapter;
    private MovieCreditsAdapter castAdapter;
    private MovieCreditsAdapter crewAdapter;
    private Transition sharedElementEnterTransition;
    private MovieDetailsPresentationModel movieDetailsPresentationModel;
    private MovieDetailsComponent movieDetailsComponent;
    private final Handler handler = new Handler();
    private MovieDetailsPresentationModelMapper movieDetailsPresentationModelMapper = new MovieDetailsPresentationModelMapper();
    // endregion

    // region Injected Variables
    @Inject
    MovieDetailsPresentationContract.Presenter movieDetailsPresenter;
    // endregion

    // region Listeners
    private NestedScrollView.OnScrollChangeListener nestedScrollViewOnScrollChangeListener = new NestedScrollView.OnScrollChangeListener() {
        @Override
        public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            int scrollThreshold = moviePosterHeight - movieDetailsHeaderLinearLayout.getMeasuredHeight() + padding;

            boolean isScrolledPastThreshold = (scrollY >= scrollThreshold);
            movieDetailsPresenter.onScrollChange(isScrolledPastThreshold);
        }
    };

    private BaseAdapter.OnItemClickListener similarMoviesAdapterOnItemClickListener = new BaseAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int position, View view) {
            selectedMovieView = view;
            MoviePresentationModel movie = similarMoviesAdapter.getItem(position);
            if(movie != null){
                movieDetailsPresenter.onMovieClick(movie);
            }
        }
    };

    private Transition.TransitionListener enterTransitionListener = new Transition.TransitionListener() {
        @Override
        public void onTransitionStart(Transition transition) {
        }

        @Override
        public void onTransitionEnd(Transition transition) {
            if(movie != null)
                movieDetailsPresenter.onLoadMovieDetails(movie.getId());
            sharedElementEnterTransition.removeListener(this);
        }

        @Override
        public void onTransitionCancel(Transition transition) {
        }

        @Override
        public void onTransitionPause(Transition transition) {
        }

        @Override
        public void onTransitionResume(Transition transition) {
        }
    };

    private ViewTreeObserver.OnGlobalLayoutListener movieDetailsHeaderTreeObserverOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {

            CoordinatorLayout.LayoutParams params =
                    (CoordinatorLayout.LayoutParams) nestedScrollView.getLayoutParams();
            AppBarLayout.ScrollingViewBehavior behavior =
                    (AppBarLayout.ScrollingViewBehavior) params.getBehavior();
            behavior.setOverlayTop(DisplayUtility.dp2px(getContext(), 156) - movieDetailsHeaderLinearLayout.getMeasuredHeight());

            movieDetailsHeaderLinearLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
    };

    private BaseAdapter.OnItemClickListener castAdapterOnItemClickListener = new BaseAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int position, View view) {
            selectedPersonView = view;
            MovieCreditPresentationModel movieCredit = castAdapter.getItem(position);
            if(movieCredit != null){
                PersonPresentationModel person = new PersonPresentationModel();

                person.setName(movieCredit.getName());
                person.setId(movieCredit.getId());
                person.setProfilePath(movieCredit.getProfilePath());

                movieDetailsPresenter.onPersonClick(person);
            }
        }
    };

    private BaseAdapter.OnItemClickListener crewAdapterOnItemClickListener = new BaseAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int position, View view) {
            selectedPersonView = view;
            MovieCreditPresentationModel movieCredit = crewAdapter.getItem(position);
            if(movieCredit != null){
                PersonPresentationModel person = new PersonPresentationModel();

                person.setName(movieCredit.getName());
                person.setId(movieCredit.getId());
                person.setProfilePath(movieCredit.getProfilePath());

                movieDetailsPresenter.onPersonClick(person);
            }
        }
    };

    private Animation.AnimationListener movieDetailsBodyAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            handler.postDelayed(() -> {
                setUpCast();
                setUpCrew();
                setUpSimilarMovies();
            }, DELAY);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };
    // endregion

    // region Callbacks
    private Callback backdropCallback = new Callback() {
        @Override
        public void onSuccess() {
            final Bitmap bitmap = ((BitmapDrawable) backdropImageView.getDrawable()).getBitmap();
            Palette.from(bitmap).generate(palette -> {
                boolean isDark;
                @ColorUtility.Lightness int lightness = ColorUtility.isDark(palette);
                if (lightness == ColorUtility.LIGHTNESS_UNKNOWN) {
                    isDark = ColorUtility.isDark(bitmap, bitmap.getWidth() / 2, 0);
                } else {
                    isDark = lightness == ColorUtility.IS_DARK;
                }

                if (!isDark && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // Make back icon dark on light images
                    ImageButton backButton = (ImageButton) toolbar.getChildAt(0);
                    backButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.dark_icon));

                    // Make toolbar title text color dark
                    collapsingToolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(getContext(), R.color.eighty_percent_transparency_black));
                }

                // color the status bar. Set a complementary dark color on L,
                // light or dark color on M (with matching status bar icons)
                statusBarColor = getActivity().getWindow().getStatusBarColor();
                final Palette.Swatch topColor =
                        ColorUtility.getMostPopulousSwatch(palette);
                if (topColor != null
                        && (isDark || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                    statusBarColor = ColorUtility.scrimify(topColor.getRgb(),
                            isDark, SCRIM_ADJUSTMENT);
                    // set a light status bar on M+
                    if (!isDark && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ViewUtility.setLightStatusBar(getActivity().getWindow().getDecorView());
                    }
                }

                if (statusBarColor != getActivity().getWindow().getStatusBarColor()) {
                    ValueAnimator statusBarColorAnim = ValueAnimator.ofArgb(
                            getActivity().getWindow().getStatusBarColor(), statusBarColor);
                    statusBarColorAnim.addUpdateListener(animation -> {
                        if(getActivity() != null){
                            getActivity().getWindow().setStatusBarColor(
                                    (int) animation.getAnimatedValue());
                        }
                    });
                    statusBarColorAnim.setDuration(500L);
                    statusBarColorAnim.setInterpolator(
                            AnimationUtility.getFastOutSlowInInterpolator(getContext()));
                    statusBarColorAnim.start();
                }

                if (isDark || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    GradientDrawable gradientDrawable = new GradientDrawable(
                            GradientDrawable.Orientation.BOTTOM_TOP,
                            new int[] {
                                    ContextCompat.getColor(getContext(), android.R.color.transparent),
                                    statusBarColor});

                    backdropFrameLayout.setForeground(gradientDrawable);
                    collapsingToolbarLayout.setContentScrim(new ColorDrawable(ColorUtility.modifyAlpha(statusBarColor, 0.9f)));
                } else {
                    GradientDrawable gradientDrawable = new GradientDrawable(
                            GradientDrawable.Orientation.BOTTOM_TOP,
                            new int[] {
                                    ContextCompat.getColor(getContext(), android.R.color.transparent),
                                    ContextCompat.getColor(getContext(), R.color.status_bar_color)});

                    backdropFrameLayout.setForeground(gradientDrawable);
                    collapsingToolbarLayout.setContentScrim(new ColorDrawable(ColorUtility.modifyAlpha(ContextCompat.getColor(getContext(), R.color.status_bar_color), 0.9f)));
                }
            });
        }

        @Override
        public void onError() {

        }
    };

    private Callback posterCallback = new Callback() {
        @Override
        public void onSuccess() {
            final Bitmap bitmap = ((BitmapDrawable) moviePosterImageView.getDrawable()).getBitmap();
            Palette.from(bitmap).generate(palette -> {
                setUpMovieHeaderBackgroundColor(palette);
                setUpTitleTextColor(titleTextView, palette);

                getActivity().supportStartPostponedEnterTransition();
            });
        }

        @Override
        public void onError() {
            getActivity().supportStartPostponedEnterTransition();
        }
    };
    // endregion

    // region Constructors
    public MovieDetailsFragment() {
    }
    // endregion

    // region Factory Methods
    public static MovieDetailsFragment newInstance() {
        return new MovieDetailsFragment();
    }

    public static MovieDetailsFragment newInstance(Bundle extras) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        fragment.setArguments(extras);
        return fragment;
    }
    // endregion

    // region Lifecycle Methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().supportPostponeEnterTransition();

        createMovieDetailsComponent().inject(this);

        font = FontCache.getTypeface("Lato-Medium.ttf", getContext());

        moviePosterHeight = DisplayUtility.dp2px(getContext(), 156);
        padding = DisplayUtility.dp2px(getContext(), 16);

        if (getArguments() != null) {
            movie = getArguments().getParcelable(KEY_MOVIE);
        }

        setHasOptionsMenu(true);

        sharedElementEnterTransition = getActivity().getWindow().getSharedElementEnterTransition();
        sharedElementEnterTransition.addListener(enterTransitionListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            setCollapsingToolbarTitle("");
        }

        if(movie != null){
            setUpBackdrop();
            setUpPoster();
            setUpTitle();

            movieDetailsHeaderLinearLayout.getViewTreeObserver().addOnGlobalLayoutListener(movieDetailsHeaderTreeObserverOnGlobalLayoutListener);
        }

        nestedScrollView.setNestedScrollingEnabled(false);
        nestedScrollView.setOnScrollChangeListener(nestedScrollViewOnScrollChangeListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        removeListeners();
        unbinder.unbind();
        movieDetailsPresenter.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        releaseMovieDetailsComponent();
    }

    // endregion

    // region MovieDetailsPresentationContract.View Methods

    @Override
    public void setMovieDetailsDomainModel(MovieDetailsDomainModel movieDetailsDomainModel) {
        this.movieDetailsPresentationModel = movieDetailsPresentationModelMapper.mapToPresentationModel(movieDetailsDomainModel);
        nestedScrollView.setNestedScrollingEnabled(true);
//        movie = movieDetailsPresentationModel.getMovie();

//        setUpBackdrop();
        setUpOverview();
        setUpDuration();
        setUpGenres();
        setUpStatus();
        setUpReleaseDate();
        setUpBudget();
        setUpRevenue();
        setUpRating();

        showMovieDetailsBody();
    }

    @Override
    public void showToolbarTitle() {
        String name = "";
        if (movie != null) {
            name = movie.getTitle();
        }
        setCollapsingToolbarTitle(name);
    }

    @Override
    public void hideToolbarTitle() {
        setCollapsingToolbarTitle("");
    }

    @Override
    public void showErrorView() {
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.main_content),
                TrestleUtility.getFormattedText(getString(R.string.oops_something_went_wrong), font, 16),
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.retry, view -> {
            if(movie != null)
                movieDetailsPresenter.onLoadMovieDetails(movie.getId());
        });
        View snackBarView = snackbar.getView();
//                            snackBarView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.grey_200));
        TextView textView = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.secondary_text_light));
        textView.setTypeface(font);

        snackbar.show();
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

    @Override
    public void openMovieDetails(MoviePresentationModel movie) {
        Window window = getActivity().getWindow();
//                window.setStatusBarColor(statusBarColor);

        Pair<View, String> moviePair  = getMoviePair();
        ActivityOptionsCompat options = getActivityOptionsCompat(moviePair);

        window.setExitTransition(null);
        ActivityCompat.startActivity(getActivity(), MovieDetailsActivity.createIntent(getContext(), movie), options.toBundle());
    }

    // endregion

    // region Helper Methods
    private void removeListeners() {
        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) null);

        if(castAdapter != null)
            castAdapter.setOnItemClickListener(null);

        if(crewAdapter != null)
            crewAdapter.setOnItemClickListener(null);

        if(similarMoviesAdapter != null)
            similarMoviesAdapter.setOnItemClickListener(null);
    }

    private void setUpBackdrop(){
        String backdropUrl = getBackdropUrl(movie);
        int height = DisplayUtility.dp2px(getContext(), 256);

        if (!TextUtils.isEmpty(backdropUrl)) {
            Picasso.with(backdropImageView.getContext())
                    .load(backdropUrl)
                    .resize((int)(1.5D*height), height)
                    .centerCrop()
                    .into(backdropImageView, backdropCallback);
        }
    }

    private void setUpPoster(){
        String posterUrl = getPosterUrl(movie);
        if (!TextUtils.isEmpty(posterUrl)) {
            Picasso.with(moviePosterImageView.getContext())
                    .load(posterUrl)
                    .resize(DisplayUtility.dp2px(moviePosterImageView.getContext(), 104), DisplayUtility.dp2px(moviePosterImageView.getContext(), 156))
                    .centerCrop()
                    .into(moviePosterImageView, posterCallback);
        }
    }

    private String getPosterUrl(MoviePresentationModel movie){
        String posterPath = movie.getPosterPath();
        String posterUrl = String.format("%s%s%s", SECURE_BASE_URL, POSTER_SIZE, posterPath);
        return posterUrl;
    }

    private String getBackdropUrl(MoviePresentationModel movie){
        String backdropPath = movie.getBackdropPath();
        String backdropUrl = String.format("%s%s%s", SECURE_BASE_URL, BACKDROP_SIZE, backdropPath);
        return backdropUrl;
    }

    private void showMovieDetailsBody(){
        progressBar.setVisibility(View.GONE);

        final int targetHeight = AnimationUtility.getTargetHeight(movieDetailsBodyLinearLayout);
        Animation animation = AnimationUtility.getExpandHeightAnimation(movieDetailsBodyLinearLayout, targetHeight);
        // 1dp/ms
        animation.setDuration((int)(targetHeight / movieDetailsBodyLinearLayout.getContext().getResources().getDisplayMetrics().density));
        animation.setAnimationListener(movieDetailsBodyAnimationListener);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setStartOffset(START_OFFSET);
        movieDetailsBodyLinearLayout.startAnimation(animation);
    }

    private void setUpCast(){
        List<MovieCreditPresentationModel> cast = movieDetailsPresentationModel.getCast();
        if(cast != null && cast.size()>0){
            View castView = castViewStub.inflate();

            RecyclerView castRecyclerView = castView.findViewById(R.id.cast_rv);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            castRecyclerView.setLayoutManager(layoutManager);
            castAdapter = new MovieCreditsAdapter(getContext());
            castAdapter.setOnItemClickListener(castAdapterOnItemClickListener);
            castRecyclerView.setAdapter(castAdapter);
            SnapHelper snapHelper = new GravitySnapHelper(Gravity.START);
            snapHelper.attachToRecyclerView(castRecyclerView);

            castAdapter.addAll(cast);
        }
    }

    private void setUpCrew(){
        List<MovieCreditPresentationModel> crew = movieDetailsPresentationModel.getCrew();
        if(crew != null && crew.size()>0){
            View crewView = crewViewStub.inflate();

            RecyclerView crewRecyclerView = crewView.findViewById(R.id.crew_rv);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            crewRecyclerView.setLayoutManager(layoutManager);
            crewAdapter = new MovieCreditsAdapter(getContext());
            crewAdapter.setOnItemClickListener(crewAdapterOnItemClickListener);
            crewRecyclerView.setAdapter(crewAdapter);
            SnapHelper snapHelper = new GravitySnapHelper(Gravity.START);
            snapHelper.attachToRecyclerView(crewRecyclerView);

            crewAdapter.addAll(crew);
        }
    }

    private void setUpSimilarMovies(){
        List<MoviePresentationModel> similarMovies = movieDetailsPresentationModel.getSimilarMovies();
        if(similarMovies != null && similarMovies.size()>0){
            View similarMoviesView = similarMoviesViewStub.inflate();

            RecyclerView similarMoviesRecyclerView = similarMoviesView.findViewById(R.id.similar_movies_rv);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            similarMoviesRecyclerView.setLayoutManager(layoutManager);
            similarMoviesAdapter = new SimilarMoviesAdapter(getContext());
            similarMoviesAdapter.setOnItemClickListener(similarMoviesAdapterOnItemClickListener);
            similarMoviesRecyclerView.setAdapter(similarMoviesAdapter);
            SnapHelper snapHelper = new GravitySnapHelper(Gravity.START);
            snapHelper.attachToRecyclerView(similarMoviesRecyclerView);

            Collections.sort(similarMovies, (m1, m2) -> {
                int year1 = -1;
                if(m1.getReleaseDateYear() != -1){
                    year1 = m1.getReleaseDateYear();
                }

                int year2 = -1;
                if(m2.getReleaseDateYear() != -1){
                    year2 = m2.getReleaseDateYear();
                }

                if(year1 > year2)
                    return -1;
                else if(year1 < year2)
                    return 1;
                else
                    return 0;
            });

            similarMoviesAdapter.addAll(similarMovies);
        }
    }

    private void setUpMovieHeaderBackgroundColor(Palette palette){
        Palette.Swatch swatch = ColorUtility.getMostPopulousSwatch(palette);
        if(swatch != null){
            int startColor = ContextCompat.getColor(getContext(), R.color.grey_600);
            int endColor = swatch.getRgb();

            AnimationUtility.animateBackgroundColorChange(movieDetailsHeaderLinearLayout, startColor, endColor);
        }
    }

    private void setUpTitleTextColor(final TextView tv, Palette palette){
        Palette.Swatch swatch = ColorUtility.getMostPopulousSwatch(palette);
        if(swatch != null){
            int startColor = ContextCompat.getColor(tv.getContext(), R.color.primary_text_light);
            int endColor = swatch.getTitleTextColor();

            AnimationUtility.animateTextColorChange(tv, startColor, endColor);
        }
    }

    private void setUpTitle(){
        String title = movie.getTitle();
        String releaseDate = movie.getReleaseDate();
        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(releaseDate)) {
            Calendar calendar = DateUtility.getCalendar(releaseDate, PATTERN);
            titleTextView.setText(String.format("%s (%s)", title, String.format("%d", calendar.get(Calendar.YEAR))));
        }
    }

    private void setUpOverview(){
        String overview = movie.getOverview();
        if(!TextUtils.isEmpty(overview)){
            overviewTextView.setText(overview);
        } else {
            overviewTextView.setText(R.string.not_available);
        }
    }

    private void setUpDuration(){
        int runTime = movie.getRuntime();
        if(runTime>0) {
            int hours = runTime/60;
            int minutes = runTime%60;
            if(hours>0){
                if(minutes>0){
                    durationTextView.setText(String.format("%dh %dm", hours, minutes));
                } else {
                    durationTextView.setText(String.format("%dh", hours));
                }
            } else {
                durationTextView.setText(String.format("%dm", minutes));
            }
        } else {
            durationTextView.setText(R.string.not_available);
        }
    }

    private void setUpGenres(){
        List<GenrePresentationModel> genres = movie.getGenres();
        if(genres != null && genres.size()>0){
            StringBuilder stringBuilder = new StringBuilder("");

            for(int i=0; i<genres.size(); i++){
                GenrePresentationModel genre = genres.get(i);
                stringBuilder.append(genre.getName());
                if(i!=genres.size()-1){
                    stringBuilder.append(" | ");
                }
            }

            genresTextView.setText(stringBuilder);
        }
    }

    private void setUpStatus(){
        String status = movie.getStatus();
        if(!TextUtils.isEmpty(status)){
            statusTextView.setText(status);
        }
    }

    private void setUpReleaseDate(){
        String releaseDate = movie.getReleaseDate();
        if(!TextUtils.isEmpty(releaseDate)){
            Calendar calendar = DateUtility.getCalendar(releaseDate, PATTERN);

            String month = DateUtility.getMonth(calendar.get(Calendar.MONTH));
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int year = calendar.get(Calendar.YEAR);

            String formattedReleaseDate = String.format("%s %d, %d", month, day, year);

            releaseDateTextView.setText(formattedReleaseDate);
        }
    }

    private void setUpBudget(){
        int budget = movie.getBudget();
        if(budget > 0){
            budgetTextView.setText(String.format("$%s", NumberFormat.getNumberInstance(Locale.US).format(budget)));
        } else {
            budgetTextView.setText(R.string.not_available);
        }
    }

    private void setUpRevenue(){
        long revenue = movie.getRevenue();
        if(revenue > 0L){
            revenueTextView.setText(String.format("$%s", NumberFormat.getNumberInstance(Locale.US).format(revenue)));
        } else {
            revenueTextView.setText(R.string.not_available);
        }
    }

    private void setUpRating(){
        String rating = movieDetailsPresentationModel.getRating();

        if(!TextUtils.isEmpty(rating)){
            ratingTextView.setText(rating);
        } else {
            ratingTextView.setVisibility(View.GONE);
        }
    }

    private void setCollapsingToolbarTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            collapsingToolbarLayout.setCollapsedTitleTypeface(font);
            collapsingToolbarLayout.setTitle(title);
        } else {
            collapsingToolbarLayout.setCollapsedTitleTypeface(font);
            collapsingToolbarLayout.setTitle("");
        }
    }

    public int getStatusBarColor() {
        return statusBarColor;
    }

    private ActivityOptionsCompat getActivityOptionsCompat(Pair pair){
        ActivityOptionsCompat options = null;

        Pair<View, String> navigationBarPair  = getNavigationBarPair();
        Pair<View, String> statusBarPair = getStatusBarPair();

        if(pair!=null && statusBarPair!= null && navigationBarPair!= null){
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                    pair, statusBarPair, navigationBarPair);
        } else if(pair != null && statusBarPair != null){
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                    pair, statusBarPair);
        } else if(pair != null && navigationBarPair != null){
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                    pair, navigationBarPair);
        }

        return options;
    }

    private Pair<View, String> getMoviePair(){
        Resources resources = getResources();
        String transitionName = resources.getString(R.string.transition_movie_thumbnail);
        View view = selectedMovieView.findViewById(R.id.thumbnail_iv);
        return getPair(view, transitionName);
    }

    private Pair<View, String> getPersonPair(){
        Resources resources = getResources();
        String transitionName = resources.getString(R.string.transition_person_thumbnail);
        View view = selectedPersonView.findViewById(R.id.thumbnail_iv);
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

    private MovieDetailsComponent createMovieDetailsComponent(){
        movieDetailsComponent = ((MovieHubApplication)getActivity().getApplication())
                .getApplicationComponent()
                .createSubcomponent(new MovieDetailsModule(this));
        return movieDetailsComponent;
    }

    public void releaseMovieDetailsComponent(){
        movieDetailsComponent = null;
    }
    // endregion
}
