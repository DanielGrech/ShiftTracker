package com.dgsd.android.shifttracker.mvp.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.format.Time;

import com.dgsd.android.shifttracker.BuildConfig;
import com.dgsd.android.shifttracker.R;
import com.dgsd.android.shifttracker.data.AppSettings;
import com.dgsd.android.shifttracker.data.AppSettings.Defaults;
import com.dgsd.android.shifttracker.manager.AdManager;
import com.dgsd.android.shifttracker.manager.AnalyticsManager;
import com.dgsd.android.shifttracker.module.AppServicesComponent;
import com.dgsd.android.shifttracker.mvp.view.AddShiftMvpView;
import com.dgsd.android.shifttracker.service.ReminderScheduleService;
import com.dgsd.android.shifttracker.util.AlarmUtils;
import com.dgsd.android.shifttracker.util.ModelUtils;
import com.dgsd.android.shifttracker.util.TimeUtils;
import com.dgsd.shifttracker.data.DataProvider;
import com.dgsd.shifttracker.model.ColorItem;
import com.dgsd.shifttracker.model.ReminderItem;
import com.dgsd.shifttracker.model.Shift;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import timber.log.Timber;

import static com.dgsd.android.shifttracker.util.ModelUtils.getColorItems;
import static com.dgsd.android.shifttracker.util.ModelUtils.getReminderItems;
import static com.dgsd.android.shifttracker.util.TimeUtils.daysBetween;
import static com.dgsd.android.shifttracker.util.TimeUtils.isSameDay;
import static com.dgsd.android.shifttracker.util.TimeUtils.toTime;

@SuppressWarnings("deprecation")
public class AddShiftPresenter extends Presenter<AddShiftMvpView> {

    private enum DateChangeType {
        START,
        END,
        OVERTIME_START,
        OVERTIME_END
    }

    @Inject
    AppSettings appSettings;

    @Inject
    DataProvider dataProvider;

    @Inject
    AdManager adManager;

    final Date dateHint;

    final long shiftIdToEdit;

    final boolean cloneOnly;

    boolean hasSetInitialValues;

    final Calendar calendar = Calendar.getInstance();

    public AddShiftPresenter(@NonNull AddShiftMvpView view, AppServicesComponent component, Date dateHint) {
        this(view, component, dateHint, -1, false);
    }

    public AddShiftPresenter(@NonNull AddShiftMvpView view, AppServicesComponent component, long shiftId, boolean cloneOnly) {
        this(view, component, null, shiftId, cloneOnly);
    }

    private AddShiftPresenter(@NonNull AddShiftMvpView view, AppServicesComponent component,
                              Date dateHint, long shiftId, boolean cloneOnly) {
        super(view, component);
        component.inject(this);
        this.dateHint = dateHint;
        this.shiftIdToEdit = shiftId;
        this.cloneOnly = cloneOnly;
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

        if (!hasSetInitialValues) {
            hasSetInitialValues = true;

            setupDateTime();
            setupPayInfo();
            setupReminders();
            setupColors();

            if (shiftIdToEdit >= 0) {
                bind(dataProvider.getShift(shiftIdToEdit).take(1), new SimpleSubscriber<Shift>() {
                    @Override
                    public void onNext(Shift shift) {
                        populateViewWith(shift);
                    }
                });
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AnalyticsManager.trackScreenView("add_shift");
    }

    public void onSaveClicked() {
        AnalyticsManager.trackClick("save");
        Shift shift = getView().getShift();

        if (!shift.timePeriod().isValid()) {
            getView().showError(getContext().getString(R.string.error_time_is_invalid));
            return;
        }

        if (shift.overtime() != null && !shift.overtime().isValid()) {
            getView().showError(getContext().getString(R.string.error_overtimetime_is_invalid));
            return;
        }

        if (!cloneOnly && shiftIdToEdit >= 0) {
            shift = shift.withId(shiftIdToEdit);
        }

        Timber.d("Going to save shift: %s", shift);
        bind(dataProvider.addShift(shift), new SimpleSubscriber<Shift>() {

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "Error saving shift");
                getView().showError(getContext().getString(R.string.error_saving_shift));
            }

            @Override
            public void onNext(Shift shift) {
                AlarmUtils.cancel(getContext(), shift.id());

                if (shift.hasReminder() && !shift.reminderHasPassed()) {
                    ReminderScheduleService.schedule(getContext(), shift);
                }

                getView().showShiftList();
            }
        });
    }

    public void onShowOvertimeToggled(boolean showOvertime) {
        AnalyticsManager.trackClick("show_overtime_" + showOvertime);
        if (showOvertime) {
            getView().showOvertime();
        } else {
            getView().hideOvertime();
        }
    }

    public void onStartDateChanged(int year, int monthOfYear, int dayOfMonth) {
        solveDateConstraint(DateChangeType.START, toDate(year, monthOfYear, dayOfMonth));
    }

    public void onEndDateChanged(int year, int monthOfYear, int dayOfMonth) {
        solveDateConstraint(DateChangeType.END, toDate(year, monthOfYear, dayOfMonth));
    }

    public void onOvertimeStartDateChanged(int year, int monthOfYear, int dayOfMonth) {
        solveDateConstraint(DateChangeType.OVERTIME_START, toDate(year, monthOfYear, dayOfMonth));
    }

    public void onOvertimeEndDateChanged(int year, int monthOfYear, int dayOfMonth) {
        solveDateConstraint(DateChangeType.OVERTIME_END, toDate(year, monthOfYear, dayOfMonth));
    }

    public void onStartTimeChanged(int hourOfDay, int minute) {
        final Date startDate = getView().getCurrentStartDate();
        final Date endDate = getView().getCurrentEndDate();

        final Time currentStartTime = getView().getCurrentStartTime();
        final Time endTime = getView().getCurrentEndTime();

        final Time newStartTime = toTime(hourOfDay, minute);

        if (newStartTime.after(endTime) && isSameDay(startDate, endDate)) {
            final long prevDuration = endTime.toMillis(false) - currentStartTime.toMillis(false);

            final Time newEndTime = toTime(newStartTime.toMillis(false) + prevDuration);
            if (newEndTime.hour < newStartTime.hour) {
                // We've rolled over to the next day...
                onEndTimeChanged(23, 59);
            } else {
                onEndTimeChanged(newEndTime.hour, newEndTime.minute);
            }
        }

        getView().showStartTime(newStartTime);
    }

    public void onEndTimeChanged(int hourOfDay, int minute) {
        final boolean overtimeIsVisible = getView().isOvertimeShowing();

        final Date startDate = getView().getCurrentStartDate();
        final Date endDate = getView().getCurrentEndDate();
        final Date overtimeStartDate = getView().getCurrentOvertimeStartDate();

        final Time currentEndTime = getView().getCurrentEndTime();
        final Time startTime = getView().getCurrentStartTime();
        final Time overtimeStartTime = getView().getCurrentOvertimeStartTime();
        final Time newEndTime = toTime(hourOfDay, minute);

        if (newEndTime.before(startTime) && isSameDay(startDate, endDate)) {
            final long prevDuration = currentEndTime.toMillis(false) - startTime.toMillis(false);

            final Time newStartTime = toTime(newEndTime.toMillis(false) - prevDuration);
            if (newStartTime.hour > newEndTime.hour) {
                // We've rolled over to the previous day..
                onStartTimeChanged(0, 0);
            } else {
                onStartTimeChanged(newStartTime.hour, newStartTime.minute);
            }
        } else if (overtimeIsVisible && overtimeStartTime.before(newEndTime)
                && isSameDay(endDate, overtimeStartDate)) {
            onOvertimeStartTimeChanged(newEndTime.hour, newEndTime.minute);
        }

        getView().showEndTime(newEndTime);
    }

    public void onOvertimeStartTimeChanged(int hourOfDay, int minute) {
        final Date endDate = getView().getCurrentEndDate();
        final Date overtimeStartDate = getView().getCurrentOvertimeStartDate();
        final Date overtimeEndDate = getView().getCurrentEndDate();

        final Time endTime = getView().getCurrentEndTime();
        final Time overtimeEndTime = getView().getCurrentOvertimeEndTime();
        final Time overtimeStartTime = getView().getCurrentOvertimeStartTime();
        final Time newOvertimeStartTime = toTime(hourOfDay, minute);

        if (newOvertimeStartTime.after(overtimeEndTime) && isSameDay(overtimeEndDate, overtimeStartDate)) {
            final long prevDuration = overtimeEndTime.toMillis(false) - overtimeStartTime.toMillis(false);

            final Time newOvertimeEndTime = toTime(newOvertimeStartTime.toMillis(false) + prevDuration);

            if (newOvertimeEndTime.hour < newOvertimeStartTime.hour) {
                // We've rolled over to the next day..
                onOvertimeEndTimeChanged(23, 59);
            } else {
                onOvertimeEndTimeChanged(newOvertimeEndTime.hour, newOvertimeEndTime.minute);
            }
        } else if (newOvertimeStartTime.before(endTime) && isSameDay(endDate, overtimeStartDate)) {
            onEndTimeChanged(newOvertimeStartTime.hour, newOvertimeStartTime.minute);
        }

        getView().showOvertimeStartTime(newOvertimeStartTime);
    }

    public void onOvertimeEndTimeChanged(int hourOfDay, int minute) {
        final Date overtimeStartDate = getView().getCurrentOvertimeStartDate();
        final Date overtimeEndDate = getView().getCurrentEndDate();

        final Time overtimeEndTime = getView().getCurrentOvertimeEndTime();
        final Time overtimeStartTime = getView().getCurrentOvertimeStartTime();
        final Time newOvertimeEndTime = toTime(hourOfDay, minute);

        if (newOvertimeEndTime.before(overtimeStartTime) && isSameDay(overtimeStartDate, overtimeEndDate)) {
            final long prevDuration = overtimeEndTime.toMillis(false) - overtimeStartTime.toMillis(false);

            final Time newOvertimeStartTime = toTime(newOvertimeEndTime.toMillis(false) - prevDuration);

            if (newOvertimeStartTime.hour > newOvertimeEndTime.hour) {
                // We've rolled over to the previous day..
                onOvertimeStartTimeChanged(0, 0);
            } else {
                onOvertimeStartTimeChanged(newOvertimeStartTime.hour, newOvertimeStartTime.minute);
            }
        }

        getView().showOvertimeEndTime(newOvertimeEndTime);
    }

    void setupColors() {
        final ColorItem[] colors = getColorItems(getContext());
        getView().showColors(colors);

        final int defaultReminderIdx = appSettings.defaultColorIndex().get(Defaults.colorItem());
        getView().showColor(colors[defaultReminderIdx]);
    }

    void setupReminders() {
        final ReminderItem[] reminders = getReminderItems(getContext());
        if (BuildConfig.IS_PAID) {
            getView().showReminders(reminders);
        } else {
            getView().showReminders(new ReminderItem[]{reminders[Defaults.reminderItem()]});
        }

        final int defaultReminderIdx = appSettings.defaultReminderIndex().get(Defaults.reminderItem());
        getView().showReminder(reminders[defaultReminderIdx]);
    }

    void setupPayInfo() {
        final float defaultPayRate = appSettings.defaultPayRate().get(Defaults.payRate());
        if (defaultPayRate > 0f) {
            getView().showPayRate(defaultPayRate);
        }

        final long unpaidBreakDuration = appSettings.defaultUnpaidBreakDuration().get(Defaults.unpaidBreakDuration());
        if (unpaidBreakDuration > 0) {
            getView().showUnpaidBreakDuration(TimeUnit.MILLISECONDS.toMinutes(unpaidBreakDuration));
        }
    }

    void populateViewWith(Shift shift) {
        calendar.setTimeInMillis(shift.timePeriod().startMillis());

        getView().showStartDate(calendar.getTime());
        getView().showStartTime(toTime(calendar.getTimeInMillis()));

        calendar.setTimeInMillis(shift.timePeriod().endMillis());

        getView().showEndDate(calendar.getTime());
        getView().showEndTime(toTime(calendar.getTimeInMillis()));

        if (!TextUtils.isEmpty(shift.title())) {
            getView().showTitle(shift.title());
        }

        if (shift.payRate() > 0f) {
            getView().showPayRate(shift.payRate());
        }

        if (shift.unpaidBreakDuration() > 0) {
            getView().showUnpaidBreakDuration(
                    TimeUnit.MILLISECONDS.toMinutes(shift.unpaidBreakDuration()));
        }

        final ColorItem colorItem = ModelUtils.getColorItem(getContext(), shift.color());
        if (colorItem != null) {
            getView().showColor(colorItem);
        }

        final ReminderItem reminderItem = ModelUtils.getReminderItem(getContext(), shift.reminderBeforeShift());
        if (reminderItem != null) {
            getView().showReminder(reminderItem);
        }

        if (shift.overtime() != null) {
            getView().showOvertime();

            if (shift.overtimePayRate() > 0f) {
                getView().showOvertimePayRate(shift.overtimePayRate());
            }

            calendar.setTimeInMillis(shift.overtime().startMillis());

            getView().showOvertimeStartDate(calendar.getTime());
            getView().showOvertimeStartTime(toTime(calendar.getTimeInMillis()));

            calendar.setTimeInMillis(shift.overtime().endMillis());

            getView().showOvertimeEndDate(calendar.getTime());
            getView().showOvertimeEndTime(toTime(calendar.getTimeInMillis()));
        }

        if (!TextUtils.isEmpty(shift.notes())) {
            getView().showNotes(shift.notes());
        }

        if (!cloneOnly) {
            getView().showSaveAsTemplate(shift.isTemplate());
        }
    }

    void setupDateTime() {
        final long startMillis = appSettings.defaultStartTime().get(Defaults.startTime());
        final long endMillis = appSettings.defaultEndTime().get(Defaults.endTime());

        final Date shiftDate = this.dateHint == null ? new Date() : dateHint;

        getView().showStartDate(shiftDate);
        getView().showStartTime(TimeUtils.millisSinceMidnightToTime(startMillis));
        getView().showEndDate(shiftDate);
        getView().showEndTime(TimeUtils.millisSinceMidnightToTime(endMillis));
    }

    Date toDate(int year, int month, int day) {
        calendar.set(year, month, day);
        return calendar.getTime();
    }

    private Date calculateNewDate(Date newValue, Date oldValue, Date constraintTarget) {
        final int daysBetween = daysBetween(oldValue, constraintTarget);

        calendar.setTime(newValue);
        calendar.add(Calendar.DAY_OF_MONTH, daysBetween);

        return calendar.getTime();
    }

    private void solveDateConstraint(DateChangeType type, Date newValue, Date start, Date end, Date overtimeStart, Date overtimeEnd) {
        switch (type) {
            case START:
                if (newValue.after(end)) {
                    final Date newEndDate = calculateNewDate(newValue, start, end);
                    solveDateConstraint(
                            DateChangeType.END,
                            newEndDate,
                            newValue, end, overtimeStart, overtimeEnd
                    );
                }

                getView().showStartDate(newValue);
                break;
            case END:
                if (newValue.before(start)) {
                    final Date newStartDate = calculateNewDate(newValue, end, start);
                    solveDateConstraint(
                            DateChangeType.START,
                            newStartDate,
                            start, newValue, overtimeStart, overtimeEnd
                    );
                } else if (getView().isOvertimeShowing() && newValue.after(overtimeStart)) {
                    final Date newOvertimeStartDate = calculateNewDate(newValue, end, overtimeStart);
                    solveDateConstraint(
                            DateChangeType.OVERTIME_START,
                            newOvertimeStartDate,
                            start, newValue, overtimeStart, overtimeEnd
                    );
                }

                getView().showEndDate(newValue);
                break;
            case OVERTIME_START:
                if (newValue.after(overtimeEnd)) {
                    final Date newOvertimeEndDate = calculateNewDate(newValue, overtimeStart, overtimeEnd);
                    solveDateConstraint(
                            DateChangeType.OVERTIME_END,
                            newOvertimeEndDate,
                            start, end, newValue, overtimeEnd
                    );
                } else if (newValue.before(end)) {
                    final Date newEndDate = calculateNewDate(newValue, overtimeStart, end);
                    solveDateConstraint(
                            DateChangeType.END,
                            newEndDate,
                            start, end, newValue, overtimeEnd
                    );
                }

                getView().showOvertimeStartDate(newValue);
                break;
            case OVERTIME_END:
                if (newValue.before(overtimeStart)) {
                    final Date newOvertimeStartDate = calculateNewDate(newValue, overtimeEnd, overtimeStart);
                    solveDateConstraint(
                            DateChangeType.OVERTIME_START,
                            newOvertimeStartDate,
                            start, end, overtimeStart, newValue
                    );
                }

                getView().showOvertimeEndDate(newValue);
                break;
        }
    }

    private void solveDateConstraint(DateChangeType type, Date newValue) {
        solveDateConstraint(type, newValue,
                getView().getCurrentStartDate(), getView().getCurrentEndDate(),
                getView().getCurrentOvertimeStartDate(), getView().getCurrentOvertimeEndDate()
        );
    }
}
