package com.dgsd.android.shifttracker.mvp.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.dgsd.android.shifttracker.R;
import com.dgsd.android.shifttracker.manager.StatisticsEngine;
import com.dgsd.android.shifttracker.module.AppServicesComponent;
import com.dgsd.android.shifttracker.mvp.view.MvpView;
import com.dgsd.android.shifttracker.util.ModelUtils;
import com.dgsd.android.shifttracker.util.TimeUtils;
import com.dgsd.shifttracker.data.DataProvider;
import com.dgsd.shifttracker.model.Shift;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;

abstract class ShiftCollectionPresenter<V extends MvpView> extends Presenter<V> {

    @Inject
    DataProvider dataProvider;

    protected final long startMillis;

    protected final long endMillis;

    private long totalTimeWorked = -1l;

    private float totalEarnings = -1f;

    public ShiftCollectionPresenter(@NonNull V view, AppServicesComponent component,
                                    long startMillis, long endMillis) {
        super(view, component);
        this.startMillis = startMillis;
        this.endMillis = endMillis;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (dataProvider == null) {
            throw new IllegalStateException("dataProvider == null. Did you forget to inject?");
        }
    }

    protected Observable<List<Shift>> getShifts() {
        return dataProvider.getShiftsBetween(startMillis, endMillis)
                .doOnNext(new Action1<List<Shift>>() {
                    @Override
                    public void call(List<Shift> shifts) {
                        totalEarnings = StatisticsEngine.getTotalEarnings(shifts);
                        totalTimeWorked = StatisticsEngine.getTotalTimeWorked(shifts);
                    }
                });
    }

    public String getStatisticsSummary() {
        if (totalTimeWorked >= 0 && totalEarnings >= 0) {
            if (totalEarnings == 0 && totalTimeWorked == 0) {
                return getContext().getString(R.string.shift_collection_title_no_shift);
            } else {
                return getContext().getString(R.string.shift_collection_title_template,
                        ModelUtils.formatCurrency(totalEarnings),
                        TimeUtils.formatDuration(totalTimeWorked)
                );
            }
        } else {
            return "";
        }
    }
}
