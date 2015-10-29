package com.dgsd.android.shifttracker.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Space;
import android.widget.TextView;

import com.dgsd.android.shifttracker.R;
import com.dgsd.android.shifttracker.activity.AddShiftActivity;
import com.dgsd.android.shifttracker.module.AppServicesComponent;
import com.dgsd.android.shifttracker.mvp.presenter.ViewShiftPresenter;
import com.dgsd.android.shifttracker.mvp.view.ViewShiftMvpView;
import com.dgsd.android.shifttracker.util.AdUtils;
import com.dgsd.android.shifttracker.util.Api;
import com.dgsd.android.shifttracker.util.IntentUtils;
import com.dgsd.android.shifttracker.util.ModelUtils;
import com.dgsd.android.shifttracker.util.TimeUtils;
import com.dgsd.android.shifttracker.util.ViewUtils;
import com.dgsd.shifttracker.model.ReminderItem;
import com.dgsd.shifttracker.model.Shift;
import com.dgsd.shifttracker.model.TimePeriod;
import com.google.android.gms.ads.AdView;

import butterknife.Bind;
import butterknife.OnClick;

import static android.text.format.DateUtils.FORMAT_ABBREV_ALL;
import static android.text.format.DateUtils.FORMAT_SHOW_DATE;
import static android.text.format.DateUtils.FORMAT_SHOW_TIME;
import static android.text.format.DateUtils.FORMAT_SHOW_WEEKDAY;
import static com.dgsd.android.shifttracker.util.ColorUtils.complementary;
import static com.dgsd.android.shifttracker.util.ColorUtils.darken;
import static com.dgsd.android.shifttracker.util.ModelUtils.formatCurrency;

public class ViewShiftFragment extends PresentableFragment<ViewShiftPresenter> implements ViewShiftMvpView {

    private static final String KEY_SHIFT_ID = "_shift_id";

    @Bind(R.id.app_bar)
    AppBarLayout appBar;

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Bind(R.id.time_range)
    TextView timeRange;

    @Bind(R.id.overtime_title)
    TextView overtimeTitle;

    @Bind(R.id.overtime_time_range)
    TextView overtimeTimeRange;

    @Bind(R.id.overtime_pay_rate)
    TextView overtimePayRate;

    @Bind(R.id.section_pay)
    ViewGroup paySection;

    @Bind(R.id.pay_rate)
    TextView payRate;

    @Bind(R.id.pay_rate_and_break_spacing)
    Space payRateAndBreakSpacing;

    @Bind(R.id.unpaid_break)
    TextView unpaidBreak;

    @Bind(R.id.section_notes)
    ViewGroup notesSection;

    @Bind(R.id.notes)
    TextView notes;

    @Bind(R.id.section_reminder)
    ViewGroup remindersSection;

    @Bind(R.id.reminder)
    TextView reminder;

    @Bind(R.id.total_pay_wrapper)
    ViewGroup totalPayWrapper;

    @Bind(R.id.total_pay_value)
    TextView totalPayValue;

    @Nullable
    @Bind(R.id.ad_view)
    AdView adView;

    public static ViewShiftFragment newInstance(long shiftId) {
        final ViewShiftFragment fragment = new ViewShiftFragment();

        final Bundle args = new Bundle();
        args.putLong(KEY_SHIFT_ID, shiftId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected ViewShiftPresenter createPresenter(AppServicesComponent servicesComponent, Bundle savedInstanceState) {
        return new ViewShiftPresenter(this, servicesComponent, getArguments() == null ?
                -1 : getArguments().getLong(KEY_SHIFT_ID));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_view_shift;
    }

    @Override
    protected void onCreateView(View rootView, Bundle savedInstanceState) {
        super.onCreateView(rootView, savedInstanceState);
        setupActionBar();
    }

    @SuppressLint("NewApi")
    @Override
    public void showShift(Shift shift) {
        final int darkColor = darken(shift.color());
        final int complementaryColor = complementary(shift.color());
        appBar.setBackgroundColor(shift.color());
        collapsingToolbar.setTitle(shift.title());
        collapsingToolbar.setStatusBarScrimColor(darkColor);
        fab.setBackgroundTintList(ColorStateList.valueOf(complementaryColor));
        totalPayValue.setTextColor(complementaryColor);

        if (Api.isMin(Api.LOLLIPOP)) {
            getActivity().getWindow().setNavigationBarColor(darkColor);
        }

        timeRange.setText(DateUtils.formatDateRange(
                getContext(),
                shift.timePeriod().startMillis(),
                shift.timePeriod().endMillis(),
                FORMAT_SHOW_DATE | FORMAT_SHOW_TIME | FORMAT_SHOW_WEEKDAY | FORMAT_ABBREV_ALL
        ));

        final TimePeriod overtime = shift.overtime();
        ViewUtils.visibleWhen(overtime != null, overtimeTitle, overtimeTimeRange, overtimePayRate);
        if (overtime != null) {
            if (Float.compare(shift.overtimePayRate(), 0f) <= 0) {
                ViewUtils.hide(overtimePayRate);
            } else {
                ViewUtils.show(overtimePayRate);
                setDollarAmount(overtimePayRate, shift.overtimePayRate());
            }

            overtimeTimeRange.setText(DateUtils.formatDateRange(
                    getContext(),
                    shift.overtime().startMillis(),
                    shift.overtime().endMillis(),
                    FORMAT_SHOW_DATE | FORMAT_SHOW_TIME | FORMAT_SHOW_WEEKDAY | FORMAT_ABBREV_ALL
            ));
        }

        if (TextUtils.isEmpty(shift.notes())) {
            ViewUtils.hide(notesSection);
        } else {
            ViewUtils.show(notesSection);
            notes.setText(shift.notes());
        }

        if (Float.compare(shift.payRate(), 0f) <= 0) {
            ViewUtils.hide(payRate);
        } else {
            ViewUtils.show(payRate);
            setDollarAmount(payRate, shift.payRate());
        }

        if (shift.unpaidBreakDuration() < 0) {
            ViewUtils.hide(unpaidBreak);
        } else {
            ViewUtils.show(unpaidBreak);
            final String durationText = TimeUtils.formatDuration(shift.unpaidBreakDuration());
            unpaidBreak.setText(
                    getString(R.string.view_shift_break_duration_template, durationText));
        }

        if (ViewUtils.isGone(payRate) && ViewUtils.isGone(unpaidBreak)) {
            ViewUtils.hide(paySection);
        } else {
            ViewUtils.show(paySection);
            ViewUtils.visibleWhen(!ViewUtils.isGone(payRate) && !ViewUtils.isGone(unpaidBreak),
                    payRateAndBreakSpacing);
        }

        final ReminderItem reminderItem = ModelUtils.getReminderItem(
                getContext(), shift.reminderBeforeShift());
        if (reminderItem == null || reminderItem.isNoReminder()) {
            ViewUtils.hide(remindersSection);
        } else {
            ViewUtils.show(remindersSection);
            reminder.setText(reminderItem.description());
        }

        final float totalPay = shift.totalPay();
        if (Float.compare(totalPay, 0f) > 0) {
            ViewUtils.show(totalPayWrapper);
            totalPayValue.setText(formatCurrency(totalPay));
        } else {
            ViewUtils.hide(totalPayWrapper);
        }
    }

    @Override
    public void editShift(long shiftId) {
        startActivity(AddShiftActivity.createIntentForEdit(getContext(), shiftId));
    }

    @Override
    public void cloneShift(long shiftId) {
        startActivity(AddShiftActivity.createIntentForClone(getContext(), shiftId));
    }

    @Override
    public void showError(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showShiftList() {
        getActivity().finish();
    }

    @Override
    public void exportToCalendar(Shift shift) {
        final Intent calIntent = IntentUtils.getCalendarItemIntent(shift);
        if (IntentUtils.isAvailable(getContext(), calIntent)) {
            startActivity(calIntent);
        }
    }

    @Override
    public void showAd() {
        AdUtils.loadAndShowAd(adView);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.fab)
    void onFabClicked() {
        getPresenter().onEditClicked();
    }

    private void setupActionBar() {
        getBaseActivity().setSupportActionBar(toolbar);
        getBaseActivity().showUpIndicator();
    }

    private void setDollarAmount(TextView view, float amount) {
        view.setText(getString(R.string.view_shift_pay_rate_template, formatCurrency(amount)));
    }
}
