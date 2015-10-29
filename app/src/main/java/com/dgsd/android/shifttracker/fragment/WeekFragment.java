package com.dgsd.android.shifttracker.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.dgsd.android.shifttracker.R;
import com.dgsd.android.shifttracker.activity.AddShiftActivity;
import com.dgsd.android.shifttracker.activity.ViewShiftActivity;
import com.dgsd.android.shifttracker.adapter.WeekAdapter;
import com.dgsd.android.shifttracker.module.AppServicesComponent;
import com.dgsd.android.shifttracker.mvp.presenter.WeekPresenter;
import com.dgsd.android.shifttracker.mvp.view.WeekMvpView;
import com.dgsd.android.shifttracker.util.IntentUtils;
import com.dgsd.shifttracker.model.Shift;
import com.dgsd.shifttracker.model.ShiftWeekMapping;
import com.dgsd.shifttracker.model.TimePeriod;
import com.trello.rxlifecycle.RxLifecycle;

import java.util.Date;

import butterknife.Bind;
import rx.functions.Action1;

public class WeekFragment extends PresentableFragment<WeekPresenter> implements WeekMvpView {

    private static final String KEY_TIME_PERIOD = "_time_period";

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    WeekAdapter weekAdapter;

    public static WeekFragment newInstance(TimePeriod timePeriod) {
        final WeekFragment fragment = new WeekFragment();

        final Bundle args = new Bundle();
        args.putSerializable(KEY_TIME_PERIOD, timePeriod);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected WeekPresenter createPresenter(AppServicesComponent servicesComponent, Bundle savedInstanceState) {
        final TimePeriod timePeriod = getArguments() == null ?
                null : (TimePeriod) getArguments().getSerializable(KEY_TIME_PERIOD);
        if (timePeriod == null) {
            throw new IllegalStateException("No time period passed!");
        }

        return new WeekPresenter(this, servicesComponent, timePeriod);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_week;
    }

    @Override
    protected void onCreateView(View rootView, Bundle savedInstanceState) {
        super.onCreateView(rootView, savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(weekAdapter = new WeekAdapter());

        weekAdapter.setWeekStartMillis(getPresenter().getStartOfWeek());
        weekAdapter.observeShiftClicked()
                .compose(RxLifecycle.<Shift>bindView(recyclerView))
                .subscribe(new Action1<Shift>() {
                    @Override
                    public void call(Shift shift) {
                        getPresenter().onShiftClicked(shift);
                    }
                });
        weekAdapter.observeEmptyDayClicked()
                .compose(RxLifecycle.<Long>bindView(recyclerView))
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long millis) {
                        getPresenter().onAddShiftClicked(new Date(millis));
                    }
                });
        weekAdapter.observeShiftLongClicked()
                .compose(RxLifecycle.<Pair<View, Shift>>bindView(recyclerView))
                .subscribe(new Action1<Pair<View, Shift>>() {
                    @Override
                    public void call(Pair<View, Shift> shiftAndView) {
                        showContextMenu(shiftAndView.first, shiftAndView.second);
                    }
                });
    }

    @Override
    public void showShifts(ShiftWeekMapping weekMapping) {
        weekAdapter.setShifts(weekMapping);
    }

    @Override
    public void showShift(Shift shift) {
        startActivity(ViewShiftActivity.createIntent(getContext(), shift.id()));
    }

    @Override
    public void addShiftAt(Date date) {
        startActivity(AddShiftActivity.createIntent(getContext(), date.getTime()));
    }

    @Override
    public void showError(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void exportToCalendar(Shift shift) {
        final Intent calIntent = IntentUtils.getCalendarItemIntent(shift);
        if (IntentUtils.isAvailable(getContext(), calIntent)) {
            startActivity(calIntent);
        }
    }

    @Override
    public void cloneShift(Shift shift) {
        startActivity(AddShiftActivity.createIntentForClone(getContext(), shift.id()));
    }

    private void showContextMenu(View view, final Shift shift) {
        final PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.inflate(R.menu.cm_shift);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        getPresenter().onDeleteShiftClicked(shift);
                        return true;
                    case R.id.clone:
                        getPresenter().onCloneShiftClicked(shift);
                        return true;
                    case R.id.export:
                        getPresenter().onExportShiftClicked(shift);
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }
}
