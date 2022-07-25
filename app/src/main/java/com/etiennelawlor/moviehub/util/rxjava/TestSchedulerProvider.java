package com.etiennelawlor.moviehub.util.rxjava;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by etiennelawlor on 12/22/17.
 */

public class TestSchedulerProvider implements SchedulerProvider {
    @Override
    public Scheduler io() {
        return Schedulers.trampoline();
    }

    @Override
    public Scheduler ui() {
        return Schedulers.trampoline();
    }

    @Override
    public Scheduler computation() {
        return Schedulers.trampoline();
    }

    @Override
    public Scheduler newThread() {
        return Schedulers.trampoline();
    }
}
