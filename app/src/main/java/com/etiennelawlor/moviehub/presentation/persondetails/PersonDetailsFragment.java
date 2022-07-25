package com.etiennelawlor.moviehub.presentation.persondetails;

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
import com.etiennelawlor.moviehub.di.component.PersonDetailsComponent;
import com.etiennelawlor.moviehub.di.module.PersonDetailsModule;
import com.etiennelawlor.moviehub.domain.models.PersonDetailsDomainModel;
import com.etiennelawlor.moviehub.presentation.base.BaseAdapter;
import com.etiennelawlor.moviehub.presentation.base.BaseFragment;
import com.etiennelawlor.moviehub.presentation.common.GravitySnapHelper;
import com.etiennelawlor.moviehub.presentation.mappers.PersonDetailsPresentationModelMapper;
import com.etiennelawlor.moviehub.presentation.models.MoviePresentationModel;
import com.etiennelawlor.moviehub.presentation.models.PersonCreditPresentationModel;
import com.etiennelawlor.moviehub.presentation.models.PersonDetailsPresentationModel;
import com.etiennelawlor.moviehub.presentation.models.PersonPresentationModel;
import com.etiennelawlor.moviehub.presentation.models.ProfileImagePresentationModel;
import com.etiennelawlor.moviehub.presentation.models.ProfileImagesPresentationModel;
import com.etiennelawlor.moviehub.presentation.models.TelevisionShowPresentationModel;
import com.etiennelawlor.moviehub.presentation.moviedetails.MovieDetailsActivity;
import com.etiennelawlor.moviehub.presentation.televisionshowdetails.TelevisionShowDetailsActivity;
import com.etiennelawlor.moviehub.util.AnimationUtility;
import com.etiennelawlor.moviehub.util.ColorUtility;
import com.etiennelawlor.moviehub.util.DateUtility;
import com.etiennelawlor.moviehub.util.DisplayUtility;
import com.etiennelawlor.moviehub.util.FontCache;
import com.etiennelawlor.moviehub.util.TrestleUtility;
import com.etiennelawlor.moviehub.util.ViewUtility;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.etiennelawlor.moviehub.presentation.persondetails.PersonDetailsActivity.KEY_PERSON;

/**
 * Created by etiennelawlor on 12/18/16.
 */

public class PersonDetailsFragment extends BaseFragment implements PersonDetailsPresentationContract.View {

    // region Constants
    public static final String PATTERN = "yyyy-MM-dd";
    private static final float SCRIM_ADJUSTMENT = 0.075f;
    private static final int DELAY = 0;
    private static final int START_OFFSET = 500;
    public static final String SECURE_BASE_URL = "https://image.tmdb.org/t/p/";
    public static final String POSTER_SIZE = "w342";
    public static final String PROFILE_SIZE = "h632";
    public static final String MOVIE_MEDIA_TYPE = "movie";
    public static final String TELEVISION_SHOW_MEDIA_TYPE = "tv";
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
    @BindView(R.id.person_profile_iv)
    ImageView personProfileImageView;
    @BindView(R.id.title_tv)
    TextView titleTextView;
    @BindView(R.id.bio_tv)
    TextView bioTextView;
    @BindView(R.id.birthplace_tv)
    TextView birthplaceTextView;
    @BindView(R.id.dob_tv)
    TextView dateOfBirthTextView;
    @BindView(R.id.dod_tv)
    TextView dateOfDeathTextView;
    @BindView(R.id.dod_ll)
    LinearLayout dateOfDeathLinearLayout;
    @BindView(R.id.person_details_header_ll)
    LinearLayout personDetailsHeaderLinearLayout;
    @BindView(R.id.person_details_body_ll)
    LinearLayout personDetailsBodyLinearLayout;
    @BindView(R.id.nsv)
    NestedScrollView nestedScrollView;
    @BindView(R.id.cast_vs)
    ViewStub castViewStub;
    @BindView(R.id.crew_vs)
    ViewStub crewViewStub;
    @BindView(R.id.pb)
    ProgressBar progressBar;

    private View selectedView;
    // endregion

    // region Member Variables
    private PersonPresentationModel person;
    private Unbinder unbinder;
    private Typeface font;
    private int personPosterHeight;
    private int padding;
    private int statusBarColor;
    private PersonCreditsAdapter castAdapter;
    private PersonCreditsAdapter crewAdapter;
    private Transition sharedElementEnterTransition;
    private PersonDetailsPresentationModel personDetailsPresentationModel;
    private PersonDetailsComponent personDetailsComponent;
    private final Handler handler = new Handler();
    private PersonDetailsPresentationModelMapper personDetailsPresentationModelMapper = new PersonDetailsPresentationModelMapper();
    // endregion

    // region Injected Variables
    @Inject
    PersonDetailsPresentationContract.Presenter personDetailsPresenter;
    // endregion

    // region Listeners
    private NestedScrollView.OnScrollChangeListener nestedScrollViewOnScrollChangeListener = new NestedScrollView.OnScrollChangeListener() {
        @Override
        public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            int scrollThreshold = personPosterHeight - personDetailsHeaderLinearLayout.getMeasuredHeight() + padding;

            boolean isScrolledPastThreshold = (scrollY >= scrollThreshold);
            personDetailsPresenter.onScrollChange(isScrolledPastThreshold);
        }
    };

    private BaseAdapter.OnItemClickListener castAdapterOnItemClickListener = new BaseAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int position, View view) {
            selectedView = view;

            PersonCreditPresentationModel personCredit = castAdapter.getItem(position);
            if(personCredit != null){
                String mediaType = personCredit.getMediaType();
                switch (mediaType){
                    case MOVIE_MEDIA_TYPE:
                        MoviePresentationModel movie = new MoviePresentationModel();

                        movie.setTitle(personCredit.getTitle());
                        movie.setId(personCredit.getId());
                        movie.setPosterPath(personCredit.getPosterPath());
                        movie.setReleaseDate(personCredit.getReleaseDate());

                        personDetailsPresenter.onMovieClick(movie);
                        break;
                    case TELEVISION_SHOW_MEDIA_TYPE:
                        TelevisionShowPresentationModel televisionShow = new TelevisionShowPresentationModel();

                        televisionShow.setName(personCredit.getName());
                        televisionShow.setId(personCredit.getId());
                        televisionShow.setPosterPath(personCredit.getPosterPath());
                        televisionShow.setFirstAirDate(personCredit.getFirstAirDate());

                        personDetailsPresenter.onTelevisionShowClick(televisionShow);
                        break;
                    default:
                        break;
                }
            }
        }
    };

    private BaseAdapter.OnItemClickListener crewAdapterOnItemClickListener = new BaseAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int position, View view) {
            selectedView = view;

            PersonCreditPresentationModel personCredit = crewAdapter.getItem(position);
            if(personCredit != null){

                String mediaType = personCredit.getMediaType();
                switch (mediaType){
                    case MOVIE_MEDIA_TYPE:
                        MoviePresentationModel movie = new MoviePresentationModel();

                        movie.setTitle(personCredit.getTitle());
                        movie.setId(personCredit.getId());
                        movie.setPosterPath(personCredit.getPosterPath());
                        movie.setReleaseDate(personCredit.getReleaseDate());

                        personDetailsPresenter.onMovieClick(movie);
                        break;
                    case TELEVISION_SHOW_MEDIA_TYPE:
                        TelevisionShowPresentationModel televisionShow = new TelevisionShowPresentationModel();

                        televisionShow.setName(personCredit.getName());
                        televisionShow.setId(personCredit.getId());
                        televisionShow.setPosterPath(personCredit.getPosterPath());
                        televisionShow.setFirstAirDate(personCredit.getFirstAirDate());

                        personDetailsPresenter.onTelevisionShowClick(televisionShow);
                        break;
                    default:
                        break;
                }
            }
        }
    };

    private Transition.TransitionListener enterTransitionListener = new Transition.TransitionListener() {
        @Override
        public void onTransitionStart(Transition transition) {

        }

        @Override
        public void onTransitionEnd(Transition transition) {
            if(person != null)
                personDetailsPresenter.onLoadPersonDetails(person.getId());
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

    private ViewTreeObserver.OnGlobalLayoutListener personDetailsHeaderTreeObserverOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {

            CoordinatorLayout.LayoutParams params =
                    (CoordinatorLayout.LayoutParams) nestedScrollView.getLayoutParams();
            AppBarLayout.ScrollingViewBehavior behavior =
                    (AppBarLayout.ScrollingViewBehavior) params.getBehavior();
            behavior.setOverlayTop(DisplayUtility.dp2px(getContext(), 156) - personDetailsHeaderLinearLayout.getMeasuredHeight());

            personDetailsHeaderLinearLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
    };

    private Animation.AnimationListener personDetailsBodyAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            handler.postDelayed(() -> {
                setUpCast();
                setUpCrew();
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
            if(isResumed()){
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

        }

        @Override
        public void onError() {

        }
    };

    private Callback profileCallback = new Callback() {
        @Override
        public void onSuccess() {
            final Bitmap bitmap = ((BitmapDrawable) personProfileImageView.getDrawable()).getBitmap();
            Palette.from(bitmap).generate(palette -> {
                setUpPersonHeaderBackgroundColor(palette);
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
    public PersonDetailsFragment() {
    }
    // endregion

    // region Factory Methods
    public static PersonDetailsFragment newInstance() {
        return new PersonDetailsFragment();
    }

    public static PersonDetailsFragment newInstance(Bundle extras) {
        PersonDetailsFragment fragment = new PersonDetailsFragment();
        fragment.setArguments(extras);
        return fragment;
    }
    // endregion

    // region Lifecycle Methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().supportPostponeEnterTransition();

        createPersonDetailsComponent().inject(this);

        font = FontCache.getTypeface("Lato-Medium.ttf", getContext());

        personPosterHeight = DisplayUtility.dp2px(getContext(), 156);
        padding = DisplayUtility.dp2px(getContext(), 16);

        if (getArguments() != null) {
            person = getArguments().getParcelable(KEY_PERSON);
        }

        setHasOptionsMenu(true);

        sharedElementEnterTransition = getActivity().getWindow().getSharedElementEnterTransition();
        sharedElementEnterTransition.addListener(enterTransitionListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_person_details, container, false);
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

        if(person != null){
            setUpProfile();
            setUpTitle();

            personDetailsHeaderLinearLayout.getViewTreeObserver().addOnGlobalLayoutListener(personDetailsHeaderTreeObserverOnGlobalLayoutListener);
        }

        nestedScrollView.setNestedScrollingEnabled(false);
        nestedScrollView.setOnScrollChangeListener(nestedScrollViewOnScrollChangeListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        removeListeners();
        unbinder.unbind();
        personDetailsPresenter.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        releasePersonDetailsComponent();
    }
    // endregion

    // region PersonDetailsPresentationContract.View Methods

    @Override
    public void setPersonDetailsDomainModel(PersonDetailsDomainModel personDetailsDomainModel) {
        this.personDetailsPresentationModel = personDetailsPresentationModelMapper.mapToPresentationModel(personDetailsDomainModel);

        nestedScrollView.setNestedScrollingEnabled(true);

        person = personDetailsPresentationModel.getPerson();

        setUpBackdrop();
        setUpBio();
        setUpBirthplace();
        setUpDateOfBirth();
        setUpDateOfDeath();

        showPersonDetailsBody();
    }

    @Override
    public void showToolbarTitle() {
        String name = "";
        if (person != null) {
            name = person.getName();
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
            if(person != null)
                personDetailsPresenter.onLoadPersonDetails(person.getId());
        });
        View snackBarView = snackbar.getView();
//                            snackBarView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.grey_200));
        TextView textView = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.secondary_text_light));
        textView.setTypeface(font);

        snackbar.show();
    }

    @Override
    public void openMovieDetails(MoviePresentationModel movie) {
//            window.setStatusBarColor(primaryDark);

        Pair<View, String> moviePair = getMoviePair();
        ActivityOptionsCompat options = getActivityOptionsCompat(moviePair);
        Window window = getActivity().getWindow();
        window.setExitTransition(null);
        ActivityCompat.startActivity(getActivity(), MovieDetailsActivity.createIntent(getContext(), movie), options.toBundle());
    }

    @Override
    public void openTelevisionShowDetails(TelevisionShowPresentationModel televisionShow) {
//            window.setStatusBarColor(primaryDark);

        Pair<View, String> televisionShowPair  = getTelevisionShowPair();
        ActivityOptionsCompat options = getActivityOptionsCompat(televisionShowPair);
        Window window = getActivity().getWindow();
        window.setExitTransition(null);
        ActivityCompat.startActivity(getActivity(), TelevisionShowDetailsActivity.createIntent(getContext(), televisionShow), options.toBundle());
    }

    // endregion

    // region Helper Methods
    private void removeListeners() {
        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) null);

        if(castAdapter != null)
            castAdapter.setOnItemClickListener(null);

        if(crewAdapter != null)
            crewAdapter.setOnItemClickListener(null);
    }

    private void setUpBackdrop(){
        String backdropUrl = getBackdropUrl(person);
        if (!TextUtils.isEmpty(backdropUrl)) {
            int screenWidth = DisplayUtility.getScreenWidth(getContext());

            Picasso.with(backdropImageView.getContext())
                    .load(backdropUrl)
                    .resize(screenWidth, (int)(1.5D*screenWidth))
                    .centerCrop()
                    .into(backdropImageView, backdropCallback);
        }
    }

    private void setUpProfile(){
        String posterUrl = getProfileUrl(person);
        if (!TextUtils.isEmpty(posterUrl)) {
            Picasso.with(personProfileImageView.getContext())
                    .load(posterUrl)
                    .resize(DisplayUtility.dp2px(personProfileImageView.getContext(), 104), DisplayUtility.dp2px(personProfileImageView.getContext(), 156))
                    .centerCrop()
                    .into(personProfileImageView, profileCallback);
        }
    }

    private String getProfileUrl(PersonPresentationModel person){
        String profilePath = person.getProfilePath();
        String profileUrl = String.format("%s%s%s", SECURE_BASE_URL, POSTER_SIZE, profilePath);
        return profileUrl;
    }

    private String getBackdropUrl(PersonPresentationModel person){
        String backdropUrl = "";

        ProfileImagesPresentationModel profileImages = person.getImages();
        if(profileImages != null){
            List<ProfileImagePresentationModel> profileImagesList = profileImages.getProfiles();
            if(profileImagesList != null && profileImagesList.size()>0){
                ProfileImagePresentationModel profileImage = profileImagesList.get(profileImagesList.size()-1);
                if(profileImage != null){
                    String filePath = profileImage.getFilePath();

                    backdropUrl = String.format("%s%s%s", SECURE_BASE_URL, PROFILE_SIZE, filePath);
                }
            }

        }

        return backdropUrl;
    }

    private void showPersonDetailsBody(){
        progressBar.setVisibility(View.GONE);

        final int targetHeight = AnimationUtility.getTargetHeight(personDetailsBodyLinearLayout);
        Animation animation = AnimationUtility.getExpandHeightAnimation(personDetailsBodyLinearLayout, targetHeight);
        // 1dp/ms
        animation.setDuration((int)(targetHeight / personDetailsBodyLinearLayout.getContext().getResources().getDisplayMetrics().density));
        animation.setAnimationListener(personDetailsBodyAnimationListener);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setStartOffset(START_OFFSET);
        personDetailsBodyLinearLayout.startAnimation(animation);
    }

    private void setUpCast(){
        List<PersonCreditPresentationModel> cast = personDetailsPresentationModel.getCast();
        if(cast != null && cast.size()>0){
            View castView = castViewStub.inflate();

            RecyclerView castRecyclerView = castView.findViewById(R.id.cast_rv);


            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            castRecyclerView.setLayoutManager(layoutManager);
            castAdapter = new PersonCreditsAdapter(getContext());
            castAdapter.setOnItemClickListener(castAdapterOnItemClickListener);
            castRecyclerView.setAdapter(castAdapter);
            SnapHelper snapHelper = new GravitySnapHelper(Gravity.START);
            snapHelper.attachToRecyclerView(castRecyclerView);

            Collections.sort(cast, (pc1, pc2) -> {
                int year1 = -1;
                if(pc1.getFirstAirYear() != -1){
                    year1 = pc1.getFirstAirYear();
                } else if(pc1.getReleaseYear() != -1){
                    year1 = pc1.getReleaseYear();
                }

                int year2 = -1;
                if(pc2.getFirstAirYear() != -1){
                    year2 = pc2.getFirstAirYear();
                } else if(pc2.getReleaseYear() != -1){
                    year2 = pc2.getReleaseYear();
                }

                if(year1 > year2)
                    return -1;
                else if(year1 < year2)
                    return 1;
                else
                    return 0;
            });

            castAdapter.addAll(cast);
        }
    }

    private void setUpCrew(){
        List<PersonCreditPresentationModel> crew = personDetailsPresentationModel.getCrew();
        if(crew != null && crew.size()>0){
            View crewView = crewViewStub.inflate();

            RecyclerView crewRecyclerView = crewView.findViewById(R.id.crew_rv);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            crewRecyclerView.setLayoutManager(layoutManager);
            crewAdapter = new PersonCreditsAdapter(getContext());
            crewAdapter.setOnItemClickListener(crewAdapterOnItemClickListener);
            crewRecyclerView.setAdapter(crewAdapter);
            SnapHelper snapHelper = new GravitySnapHelper(Gravity.START);
            snapHelper.attachToRecyclerView(crewRecyclerView);

            Collections.sort(crew, (pc1, pc2) -> {
                int year1 = -1;
                if(pc1.getFirstAirYear() != -1){
                    year1 = pc1.getFirstAirYear();
                } else if(pc1.getReleaseYear() != -1){
                    year1 = pc1.getReleaseYear();
                }

                int year2 = -1;
                if(pc2.getFirstAirYear() != -1){
                    year2 = pc2.getFirstAirYear();
                } else if(pc2.getReleaseYear() != -1){
                    year2 = pc2.getReleaseYear();
                }

                if(year1 > year2)
                    return -1;
                else if(year1 < year2)
                    return 1;
                else
                    return 0;
            });

            crewAdapter.addAll(crew);
        }
    }

    private void setUpPersonHeaderBackgroundColor(Palette palette){
        Palette.Swatch swatch = ColorUtility.getMostPopulousSwatch(palette);
        if(swatch != null){
            int startColor = ContextCompat.getColor(getContext(), R.color.grey_600);
            int endColor = swatch.getRgb();

            AnimationUtility.animateBackgroundColorChange(personDetailsHeaderLinearLayout, startColor, endColor);
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
        String name = person.getName();
        if(!TextUtils.isEmpty(name)){
            titleTextView.setText(name);
        }
    }

    private void setUpBio(){
        String biography = person.getBiography();
        if(!TextUtils.isEmpty(biography)){
            bioTextView.setText(biography);
        } else {
            bioTextView.setText(R.string.not_available);
        }
    }

    private void setUpBirthplace(){
        String birthPlace = person.getPlaceOfBirth();
        if(!TextUtils.isEmpty(birthPlace)){
            birthplaceTextView.setText(birthPlace);
        } else {
            birthplaceTextView.setText(R.string.not_available);
        }
    }

    private void setUpDateOfBirth(){
        String dateOfBirth = person.getBirthday();
        if(!TextUtils.isEmpty(dateOfBirth)){

            Calendar calendar = DateUtility.getCalendar(dateOfBirth, PATTERN);

            String month = DateUtility.getMonth(calendar.get(Calendar.MONTH));
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int year = calendar.get(Calendar.YEAR);

            String formattedDateOfBirth = String.format("%s %d, %d", month, day, year);

            dateOfBirthTextView.setText(formattedDateOfBirth);
        } else {
            dateOfBirthTextView.setText(R.string.not_available);
        }
    }

    private void setUpDateOfDeath(){
        String dateOfDeath = person.getDeathday();
        if(!TextUtils.isEmpty(dateOfDeath)){

            Calendar calendar = DateUtility.getCalendar(dateOfDeath, PATTERN);

            String month = DateUtility.getMonth(calendar.get(Calendar.MONTH));
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int year = calendar.get(Calendar.YEAR);

            String formattedDateOfDeath = String.format("%s %d, %d", month, day, year);

            dateOfDeathTextView.setText(formattedDateOfDeath);
            dateOfDeathLinearLayout.setVisibility(View.VISIBLE);
        } else {
            dateOfDeathLinearLayout.setVisibility(View.GONE);
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
        View view = selectedView.findViewById(R.id.thumbnail_iv);
        return getPair(view, transitionName);
    }

    private Pair<View, String> getTelevisionShowPair(){
        Resources resources = getResources();
        String transitionName = resources.getString(R.string.transition_television_show_thumbnail);
        View view = selectedView.findViewById(R.id.thumbnail_iv);
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

    private PersonDetailsComponent createPersonDetailsComponent(){
        personDetailsComponent = ((MovieHubApplication)getActivity().getApplication())
                .getApplicationComponent()
                .createSubcomponent(new PersonDetailsModule(this));
        return personDetailsComponent;
    }

    public void releasePersonDetailsComponent(){
        personDetailsComponent = null;
    }
    // endregion
}
