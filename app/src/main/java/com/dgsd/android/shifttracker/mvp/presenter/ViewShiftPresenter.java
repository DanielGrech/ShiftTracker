package com.dgsd.android.shifttracker.mvp.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.dgsd.android.shifttracker.R;
import com.dgsd.android.shifttracker.manager.AdManager;
import com.dgsd.android.shifttracker.manager.AnalyticsManager;
import com.dgsd.android.shifttracker.module.AppServicesComponent;
import com.dgsd.android.shifttracker.mvp.view.ViewShiftMvpView;
import com.dgsd.shifttracker.data.DataProvider;
import com.dgsd.shifttracker.model.Shift;

import javax.inject.Inject;

import timber.log.Timber;

public class ViewShiftPresenter extends Presenter<ViewShiftMvpView> {

    @Inject
    DataProvider dataProvider;

    @Inject
    AdManager adManager;

    private final long shiftId;

    private Shift shift;

    public ViewShiftPresenter(@NonNull ViewShiftMvpView view, AppServicesComponent component, long shiftId) {
        super(view, component);
        component.inject(this);
        this.shiftId = shiftId;
    }

    @Override
    public void onViewCreated(Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);
        if (adManager.shouldShowAd()) {
            getView().showAd();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        bind(dataProvider.getShift(shiftId).take(1), new SimpleSubscriber<Shift>() {
            @Override
            public void onNext(Shift shift) {
                ViewShiftPresenter.this.shift = shift;
                getView().showShift(shift);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        AnalyticsManager.trackScreenView("view_shift");
    }

    public void onEditClicked() {
        AnalyticsManager.trackClick("edit_shift");
        getView().editShift(shiftId);
    }

    public void onDeleteClicked() {
        AnalyticsManager.trackClick("delete_shift");
        bind(dataProvider.removeShift(shiftId), new SimpleSubscriber<Void>() {
            @Override
            public void onError(Throwable e) {
                Timber.e(e, "Error deleting shift");
                getView().showError(getContext().getString(R.string.error_deleting_shift));
            }

            @Override
            public void onCompleted() {
                getView().showShiftList();
            }
        });
    }

    public void onExportClicked() {
        if (shift != null) {
            AnalyticsManager.trackClick("export_shift");
            getView().exportToCalendar(shift);
        }
    }

    public void onCloneClicked() {
        AnalyticsManager.trackClick("clone_shift");
        getView().cloneShift(shiftId);
    }
}
