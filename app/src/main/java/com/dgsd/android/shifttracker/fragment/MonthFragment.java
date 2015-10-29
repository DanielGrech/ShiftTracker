package com.dgsd.android.shifttracker.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.dgsd.android.shifttracker.R;
import com.dgsd.android.shifttracker.activity.AddShiftActivity;
import com.dgsd.android.shifttracker.activity.ViewShiftActivity;
import com.dgsd.android.shifttracker.manager.AnalyticsManager;
import com.dgsd.android.shifttracker.module.AppServicesComponent;
import com.dgsd.android.shifttracker.mvp.presenter.MonthPresenter;
import com.dgsd.android.shifttracker.mvp.view.MonthMvpView;
import com.dgsd.android.shifttracker.util.IntentUtils;
import com.dgsd.android.shifttracker.view.MonthFragmentContainerView;
import com.dgsd.android.shifttracker.view.MonthView;
import com.dgsd.shifttracker.model.MonthYear;
import com.dgsd.shifttracker.model.Shift;

import java.util.Date;
import java.util.List;

import butterknife.Bind;

public class MonthFragment extends PresentableFragment<MonthPresenter>
        implements MonthMvpView, MonthView.OnDateSelectedListener, MonthFragmentContainerView.OnShiftClickedListener {

    private static final String KEY_MONTH_YEAR = "_month_year";

    @Bind(R.id.container_view)
    MonthFragmentContainerView containerView;

    public static MonthFragment newInstance(@NonNull MonthYear monthYear) {
        final MonthFragment fragment = new MonthFragment();

        final Bundle args = new Bundle();
        args.putSerializable(KEY_MONTH_YEAR, monthYear);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected MonthPresenter createPresenter(AppServicesComponent servicesComponent, Bundle savedInstanceState) {
        final Bundle args = getArguments();
        final MonthYear monthYear = args == null ? null : (MonthYear) args.getSerializable(KEY_MONTH_YEAR);
        if (monthYear == null) {
            throw new IllegalStateException("No monthYear passed. Please use MonthFragment.newInstance() method");
        }

        return new MonthPresenter(this, servicesComponent, monthYear);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_month;
    }

    @Override
    protected void onCreateView(View rootView, Bundle savedInstanceState) {
        super.onCreateView(rootView, savedInstanceState);

        final MonthYear monthYear = getPresenter().getMonthYear();

        containerView.setOnShiftClickedListener(this);
        containerView.getMonthView().setOnDateSelectedListener(this);
        containerView.getMonthView().set(monthYear.month(), monthYear.year());
    }

    @Override
    public void onDateSelected(Date date) {
        AnalyticsManager.trackClick("selected_date");
        getPresenter().onDateSelected(date);
    }

    @Override
    public void setStartDayOfWeek(int startDay) {
        containerView.getMonthView().setFirstDayOfWeek(startDay);
    }

    @Override
    public void setDaysMarked(int[] monthDaysToMark) {
        containerView.getMonthView().resetMarkedCells();
        for (int monthDay : monthDaysToMark) {
            containerView.getMonthView().setDayMarked(monthDay, true);
        }
    }

    @Override
    public void setDayTitle(String title) {
        containerView.getDayTitle().setText(title);
    }

    @Override
    public void selectDay(int dayOfMonth) {
        containerView.getMonthView().selectDay(dayOfMonth);
    }

    @Override
    public void showShifts(List<Shift> shifts) {
        containerView.showShifts(shifts);
    }

    @Override
    public void showShift(Shift shift) {
        startActivity(ViewShiftActivity.createIntent(getContext(), shift.id()));
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

    @Override
    public void onShiftClicked(View view, Shift shift) {
        getPresenter().onShiftClicked(shift);
    }

    @Override
    public void onShiftLongClicked(View view, final Shift shift) {
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
