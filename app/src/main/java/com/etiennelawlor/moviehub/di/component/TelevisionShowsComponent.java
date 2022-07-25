package com.etiennelawlor.moviehub.di.component;

import com.etiennelawlor.moviehub.di.module.TelevisionShowsModule;
import com.etiennelawlor.moviehub.di.scope.PresentationScope;
import com.etiennelawlor.moviehub.presentation.televisionshows.TelevisionShowsFragment;

import dagger.Subcomponent;

/**
 * Created by etiennelawlor on 2/9/17.
 */

@PresentationScope
@Subcomponent(modules = {TelevisionShowsModule.class})
public interface TelevisionShowsComponent {
    // Setup injection targets
    void inject(TelevisionShowsFragment target);
}
