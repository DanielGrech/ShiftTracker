package com.dgsd.android.shifttracker.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.dgsd.android.shifttracker.BuildConfig;
import com.dgsd.android.shifttracker.R;
import com.dgsd.android.shifttracker.module.AppServicesComponent;
import com.dgsd.android.shifttracker.mvp.presenter.StatisticsPresenter;
import com.dgsd.android.shifttracker.mvp.view.StatisticsMvpView;
import com.dgsd.android.shifttracker.util.AdUtils;
import com.dgsd.android.shifttracker.util.ModelUtils;
import com.dgsd.android.shifttracker.util.TimeUtils;
import com.dgsd.android.shifttracker.util.UpgradeAppPrompt;
import com.dgsd.android.shifttracker.view.BarGraph;
import com.dgsd.android.shifttracker.view.DateTextView;
import com.google.android.gms.ads.AdView;

import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.OnClick;

public class StatisticsFragment extends PresentableFragment<StatisticsPresenter> implements StatisticsMvpView {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.start_date)
    DateTextView startDate;

    @Bind(R.id.end_date)
    DateTextView endDate;

    @Bind(R.id.earnings_graph)
    BarGraph earningsGraph;

    @Bind(R.id.total_earnings_value)
    TextView earningsGraphTotalLabel;

    @Bind(R.id.avg_earnings_value)
    TextView earningsGraphAverageLabel;

    @Bind(R.id.time_worked_graph)
    BarGraph timeWorkedGraph;

    @Bind(R.id.total_time_worked_value)
    TextView timeWorkedGraphTotalLabel;

    @Bind(R.id.avg_time_worked_value)
    TextView timeWorkedGraphAverageLabel;

    @Bind(R.id.shift_count)
    TextView shiftCount;

    @Bind(R.id.average_earnings_per_day)
    TextView averageEarningsPerDay;

    @Bind(R.id.average_earnings_per_shift)
    TextView averageEarningsPerShift;

    @Bind(R.id.highest_earning_shift)
    TextView highestEarningShift;

    @Bind(R.id.lowest_earning_shift)
    TextView lowestEarningShift;

    @Bind(R.id.average_time_worked_per_day)
    TextView averageTimeWorkedPerDay;

    @Bind(R.id.average_time_worked_per_shift)
    TextView averageTimeWorkedPerShift;

    @Bind(R.id.longest_shift)
    TextView longestShift;

    @Bind(R.id.shortest_shift)
    TextView shortestShift;

    @Nullable
    @Bind(R.id.ad_view)
    AdView adView;

    public static StatisticsFragment newInstance() {
        final StatisticsFragment fragment = new StatisticsFragment();
        return fragment;
    }

    @Override
    protected StatisticsPresenter createPresenter(AppServicesComponent servicesComponent, Bundle savedInstanceState) {
        return new StatisticsPresenter(this, servicesComponent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_statistics;
    }

    @Override
    protected void onCreateView(View rootView, Bundle savedInstanceState) {
        super.onCreateView(rootView, savedInstanceState);

        setupActionBar();
    }

    private void setupActionBar() {
        getBaseActivity().setSupportActionBar(toolbar);
        getBaseActivity().showUpIndicator();
        getBaseActivity().setTitle(R.string.nav_item_stats_view);
    }

    @SuppressWarnings("unused")
    @OnClick({R.id.start_date, R.id.end_date})
    void onDateClicked(final DateTextView view) {
        if (!BuildConfig.IS_PAID) {
            UpgradeAppPrompt.show(getContext(), R.string.upgrade_app_prompt_statistics_date);
            return;
        }

        final Calendar calendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(year, monthOfYear, dayOfMonth);

                if (view == startDate) {
                    getPresenter().onStartDateChanged(calendar.getTime());
                } else if (view == endDate) {
                    getPresenter().onEndDateChanged(calendar.getTime());
                }
            }
        };

        calendar.setTime(view.getDate());

        new DatePickerDialog(getContext(),
                listener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    @Override
    public void setStartDate(Date date) {
        startDate.setDate(date);
    }

    @Override
    public void setEndDate(Date date) {
        endDate.setDate(date);
    }

    @Override
    public Date getStartDate() {
        return startDate.getDate();
    }

    @Override
    public Date getEndDate() {
        return endDate.getDate();
    }

    @Override
    public void showShiftCount(int count) {
        shiftCount.setText(String.valueOf(count));
    }

    @Override
    public void showMostValuableShift(float amount) {
        highestEarningShift.setText(ModelUtils.formatCurrency(amount));
    }

    @Override
    public void showLeastValuableShift(float amount) {
        lowestEarningShift.setText(ModelUtils.formatCurrency(amount));
    }

    @Override
    public void showLongestShift(long durationMillis) {
        longestShift.setText(TimeUtils.formatDuration(durationMillis));
    }

    @Override
    public void showShortestShift(long durationMillis) {
        shortestShift.setText(TimeUtils.formatDuration(durationMillis));
    }

    @Override
    public void showTotalEarnings(float totalEarnings) {
        earningsGraphTotalLabel.setText(ModelUtils.formatCurrency(totalEarnings));
    }

    @Override
    public void showTotalTimeWorked(long totalMillisWorked) {
        timeWorkedGraphTotalLabel.setText(TimeUtils.formatDuration(totalMillisWorked));
    }

    @Override
    public void showAverageEarningsPerDay(float averageEarningsByDay) {
        final String earningsText = ModelUtils.formatCurrency(averageEarningsByDay);

        earningsGraphAverageLabel.setText(earningsText);
        averageEarningsPerDay.setText(earningsText);
    }

    @Override
    public void showAverageEarningsPerShift(float earnings) {
        averageEarningsPerShift.setText(ModelUtils.formatCurrency(earnings));
    }

    @Override
    public void showAverageTimeWorkedPerDay(long averageTimeWorkedByDay) {
        final String timeWorkedText = TimeUtils.formatDuration(averageTimeWorkedByDay);

        timeWorkedGraphAverageLabel.setText(timeWorkedText);
        averageTimeWorkedPerDay.setText(timeWorkedText);
    }

    @Override
    public void showAverageTimeWorkedPerShift(long duration) {
        averageTimeWorkedPerShift.setText(TimeUtils.formatDuration(duration));
    }

    @Override
    public void showEarningsPerDay(SparseArray<Float> julianDayToEarningMap) {
        earningsGraph.reset();
        earningsGraph.startAnimation();

        float maxVal = Integer.MIN_VALUE;
        for (int i = 0, size = julianDayToEarningMap.size(); i < size; i++) {
            final float value = julianDayToEarningMap.valueAt(i);
            earningsGraph.addValue(value);

            if (value > maxVal) {
                maxVal = value;
            }
        }

        earningsGraph.setXAxisTitle(TimeUtils.formatAsAbbreviatedDate(endDate.getDate()));
        earningsGraph.setYAxisTitle(maxVal == Integer.MIN_VALUE ?
                "" : ModelUtils.formatCurrency(maxVal));
        earningsGraph.endAnimation();
    }

    @Override
    public void showTimeWorkedPerDay(SparseArray<Float> julianDayToTimeWorkedMap) {
        timeWorkedGraph.reset();
        timeWorkedGraph.startAnimation();

        long maxVal = Integer.MIN_VALUE;
        for (int i = 0, size = julianDayToTimeWorkedMap.size(); i < size; i++) {
            final long value = julianDayToTimeWorkedMap.valueAt(i).longValue();
            timeWorkedGraph.addValue(value);

            if (value > maxVal) {
                maxVal = value;
            }
        }

        timeWorkedGraph.setXAxisTitle(TimeUtils.formatAsAbbreviatedDate(endDate.getDate()));
        timeWorkedGraph.setYAxisTitle(maxVal == Integer.MIN_VALUE ?
                "" : TimeUtils.formatDuration(maxVal));
        timeWorkedGraph.endAnimation();
    }

    @Override
    public void showAd() {
        AdUtils.loadAndShowAd(adView);
    }
}
