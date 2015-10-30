package com.dgsd.android.shifttracker.mvp.presenter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.dgsd.android.shifttracker.activity.BaseActivity;
import com.dgsd.android.shifttracker.fragment.BaseFragment;
import com.dgsd.android.shifttracker.module.AppServicesComponent;
import com.dgsd.android.shifttracker.mvp.view.MvpView;
import com.dgsd.android.shifttracker.util.RxUtils;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Base class for all presenters (In the Model-View-Presenter architecture) within the application
 */
public abstract class Presenter<V extends MvpView> {

    private final V view;

    public Presenter(@NonNull V view, AppServicesComponent component) {
        this.view = view;
        if (this.view == null) {
            throw new IllegalArgumentException("view != null");
        }
    }

    protected V getView() {
        return view;
    }

    protected Context getContext() {
        return view.getContext();
    }

    public void onCreate(Bundle savedInstanceState) {

    }

    public void onViewCreated(Bundle savedInstanceState) {

    }

    public void onSaveInstanceState(Bundle savedInstanceState) {

    }

    public void onDestroy() {

    }

    public void onStart() {

    }

    public void onResume() {

    }

    public void onPause() {

    }

    public void onStop() {

    }

    protected <R> Subscription bind(Observable<R> observable, Observer<? super R> observer) {
        final Observable<R> sourceObservable = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        final Observable<R> boundObservable;
        if (view instanceof BaseFragment) {
            boundObservable = RxUtils.bindFragment((BaseFragment) view, sourceObservable);
        } else if (getContext() instanceof BaseActivity) {
            boundObservable = RxUtils.bindActivity((BaseActivity) getContext(), sourceObservable);
        } else {
            boundObservable = sourceObservable;
        }

        return boundObservable.subscribe(observer);
    }

    protected class SimpleSubscriber<T> extends Subscriber<T> {

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            Timber.e(e, "Error from observable");
        }

        @Override
        public void onNext(T t) {

        }
    }
}
