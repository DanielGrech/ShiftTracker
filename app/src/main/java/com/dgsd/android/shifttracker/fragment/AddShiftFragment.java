package com.dgsd.android.shifttracker.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.dgsd.android.shifttracker.BuildConfig;
import com.dgsd.android.shifttracker.R;
import com.dgsd.android.shifttracker.adapter.ColorsSpinnerAdapter;
import com.dgsd.android.shifttracker.adapter.RemindersSpinnerAdapter;
import com.dgsd.android.shifttracker.module.AppServicesComponent;
import com.dgsd.android.shifttracker.mvp.presenter.AddShiftPresenter;
import com.dgsd.android.shifttracker.mvp.view.AddShiftMvpView;
import com.dgsd.android.shifttracker.util.AdUtils;
import com.dgsd.android.shifttracker.util.UpgradeAppPrompt;
import com.dgsd.android.shifttracker.util.ViewUtils;
import com.dgsd.android.shifttracker.view.DateTextView;
import com.dgsd.android.shifttracker.view.ShiftDetailSectionView;
import com.dgsd.android.shifttracker.view.TimeTextView;
import com.dgsd.shifttracker.model.ColorItem;
import com.dgsd.shifttracker.model.ReminderItem;
import com.dgsd.shifttracker.model.Shift;
import com.dgsd.shifttracker.model.TimePeriod;
import com.google.android.gms.ads.AdView;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import timber.log.Timber;

import static com.dgsd.android.shifttracker.util.DrawableUtils.getTintedDrawable;
import static com.dgsd.android.shifttracker.util.TimeUtils.toDateTime;
import static com.dgsd.android.shifttracker.util.ViewUtils.extractPayRate;

@SuppressWarnings("deprecation")
public class AddShiftFragment extends PresentableFragment<AddShiftPresenter> implements AddShiftMvpView {

    private static final String KEY_DATE_HINT = "_date_hint";
    private static final String KEY_SHIFT_ID = "_shift_id";
    private static final String KEY_CLONE_SHIFT = "_clone_shift";

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.title)
    EditText title;

    @Bind(R.id.start_date_input)
    DateTextView startDate;

    @Bind(R.id.start_time_input)
    TimeTextView startTime;

    @Bind(R.id.end_date_input)
    DateTextView endDate;

    @Bind(R.id.end_time_input)
    TimeTextView endTime;

    @Bind(R.id.overtime_toggle)
    SwitchCompat overtimeToggle;

    @Bind(R.id.overtime_start_wrapper)
    ViewGroup overtimeStartWrapper;

    @Bind(R.id.overtime_start_date_input)
    DateTextView overtimeStartDate;

    @Bind(R.id.overtime_start_time_input)
    TimeTextView overtimeStartTime;

    @Bind(R.id.overtime_end_wrapper)
    ViewGroup overtimeEndWrapper;

    @Bind(R.id.overtime_end_date_input)
    DateTextView overtimeEndDate;

    @Bind(R.id.overtime_end_time_input)
    TimeTextView overtimeEndTime;

    @Bind(R.id.overtime_pay_rate)
    TextView overtimePayRate;

    @Bind(R.id.pay_rate)
    EditText payRate;

    @Bind(R.id.unpaid_break)
    EditText unpaidBreak;

    @Bind(R.id.notes)
    EditText notes;

    @Bind(R.id.section_color)
    ShiftDetailSectionView colorSectionView;

    @Bind(R.id.color_spinner)
    Spinner colorSpinner;

    @Bind(R.id.reminders_spinner)
    Spinner remindersSpinner;

    @Bind(R.id.save_as_template)
    SwitchCompat saveAsTemplateToggle;

    @Nullable
    @Bind(R.id.ad_view)
    AdView adView;

    public static AddShiftFragment newInstance(long dateHint) {
        final AddShiftFragment fragment = new AddShiftFragment();

        final Bundle args = new Bundle();
        args.putLong(KEY_DATE_HINT, dateHint);
        fragment.setArguments(args);

        return fragment;
    }

    public static AddShiftFragment newEditInstance(long shiftId, boolean clone) {
        final AddShiftFragment fragment = new AddShiftFragment();

        final Bundle args = new Bundle();
        args.putLong(KEY_SHIFT_ID, shiftId);
        args.putBoolean(KEY_CLONE_SHIFT, clone);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected AddShiftPresenter createPresenter(AppServicesComponent servicesComponent, Bundle savedInstanceState) {
        long dateHintMillis = -1;
        long shiftId = -1;
        boolean clone = false;

        final Bundle args = getArguments();
        if (args != null) {
            dateHintMillis = getArguments().getLong(KEY_DATE_HINT, dateHintMillis);
            shiftId = getArguments().getLong(KEY_SHIFT_ID, shiftId);
            clone = getArguments().getBoolean(KEY_CLONE_SHIFT, clone);
        }

        if (shiftId >= 0) {
            return new AddShiftPresenter(this, servicesComponent, shiftId, clone);
        } else {
            return new AddShiftPresenter(this, servicesComponent,
                    dateHintMillis <= 0 ? null : new Date(dateHintMillis));
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_add_shift;
    }

    @Override
    protected void onCreateView(View rootView, Bundle savedInstanceState) {
        super.onCreateView(rootView, savedInstanceState);

        setupActionBar();

        colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final ColorItem colorItem = (ColorItem) parent.getItemAtPosition(position);
                colorSectionView.setSectionIcon(getTintedDrawable(
                        getContext(), R.drawable.color_indicator, colorItem.color()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupActionBar() {
        getBaseActivity().setSupportActionBar(toolbar);
        getBaseActivity().showUpIndicator();
        getBaseActivity().getSupportActionBar().setHomeAsUpIndicator(
                getTintedDrawable(getContext(), R.drawable.ic_cancel, Color.WHITE));
    }

    @OnCheckedChanged(R.id.overtime_toggle)
    void onOvertimeToggleChecked(boolean isChecked) {
        getPresenter().onShowOvertimeToggled(isChecked);
    }

    @Override
    public void showStartDate(Date date) {
        this.startDate.setDate(date);
    }

    @Override
    public void showStartTime(Time time) {
        this.startTime.setTime(time);
    }

    @Override
    public void showEndDate(Date date) {
        this.endDate.setDate(date);
    }

    @Override
    public void showEndTime(Time time) {
        this.endTime.setTime(time);
    }

    @Override
    public void showUnpaidBreakDuration(long durationInMins) {
        this.unpaidBreak.setText(String.valueOf(durationInMins));
    }

    @Override
    public void showReminders(ReminderItem[] reminders) {
        remindersSpinner.setAdapter(new RemindersSpinnerAdapter(reminders));

        if (!BuildConfig.IS_PAID) {
            remindersSpinner.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        UpgradeAppPrompt.show(getContext(), R.string.upgrade_app_prompt_edit_reminder);
                    }

                    return true;
                }
            });
            remindersSpinner.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                        UpgradeAppPrompt.show(getContext(), R.string.upgrade_app_prompt_edit_reminder);
                    }
                    return true;
                }
            });
        }
    }

    @Override
    public void showReminder(ReminderItem reminder) {
        if (reminder != null) {
            for (int i = 0, count = remindersSpinner.getCount(); i < count; i++) {
                final Object obj = remindersSpinner.getItemAtPosition(i);
                if (reminder.equals(obj)) {
                    remindersSpinner.setSelection(i);
                    break;
                }
            }
        }
    }

    @Override
    public void showColors(ColorItem[] colors) {
        colorSpinner.setAdapter(new ColorsSpinnerAdapter(colors));
    }

    @Override
    public void showColor(ColorItem color) {
        if (color != null) {
            for (int i = 0, count = colorSpinner.getCount(); i < count; i++) {
                final Object obj = colorSpinner.getItemAtPosition(i);
                if (color.equals(obj)) {
                    colorSpinner.setSelection(i);
                    break;
                }
            }
        }
    }

    @Override
    public void showPayRate(float payRate) {
        this.payRate.setText(formatPayRate(payRate));
    }

    @Override
    public void hideOvertime() {
        overtimeToggle.setChecked(false);
        ViewUtils.beginAnimation((ViewGroup) getView());
        ViewUtils.hide(overtimeStartWrapper, overtimeEndWrapper, overtimePayRate);
    }

    @Override
    public void showOvertime() {
        overtimeToggle.setChecked(true);
        ViewUtils.beginAnimation((ViewGroup) getView());
        ViewUtils.show(overtimeStartWrapper, overtimeEndWrapper, overtimePayRate);

        overtimeStartDate.setDate(endDate.getDate().getTime());
        overtimeStartTime.setTime(endTime.getTime().toMillis(false));

        final long overtimeStartMillis
                = toDateTime(overtimeStartDate.getDate(), overtimeStartTime.getTime());

        final long overtimeEndMillis = overtimeStartMillis + TimeUnit.HOURS.toMillis(1);
        overtimeEndDate.setDate(overtimeEndMillis);
        overtimeEndTime.setTime(overtimeEndMillis);
    }

    @Override
    public void showTitle(String title) {
        this.title.setText(title);
    }

    @Override
    public void showNotes(String notes) {
        this.notes.setText(notes);
    }

    @Override
    public void showOvertimePayRate(float payRate) {
        this.overtimePayRate.setText(formatPayRate(payRate));
    }

    @Override
    public void showOvertimeStartDate(Date date) {
        this.overtimeStartDate.setDate(date);
    }

    @Override
    public void showOvertimeStartTime(Time time) {
        this.overtimeStartTime.setTime(time);
    }

    @Override
    public void showOvertimeEndDate(Date date) {
        this.overtimeEndDate.setDate(date);
    }

    @Override
    public void showOvertimeEndTime(Time time) {
        this.overtimeEndTime.setTime(time);
    }

    @Override
    public void showSaveAsTemplate(boolean showAsSave) {
        saveAsTemplateToggle.setChecked(showAsSave);
    }

    @Override
    public Date getCurrentStartDate() {
        return startDate.getDate();
    }

    @Override
    public Date getCurrentEndDate() {
        return endDate.getDate();
    }

    @Override
    public Date getCurrentOvertimeStartDate() {
        return overtimeStartDate.getDate();
    }

    @Override
    public Date getCurrentOvertimeEndDate() {
        return overtimeEndDate.getDate();
    }

    @Override
    public Time getCurrentStartTime() {
        return startTime.getTime();
    }

    @Override
    public Time getCurrentEndTime() {
        return endTime.getTime();
    }

    @Override
    public Time getCurrentOvertimeStartTime() {
        return overtimeStartTime.getTime();
    }

    @Override
    public Time getCurrentOvertimeEndTime() {
        return overtimeEndTime.getTime();
    }

    @Override
    public Shift getShift() {
        Shift.Builder builder = Shift.builder()
                .title(title.getText().toString())
                .notes(notes.getText().toString())
                .color(((ColorItem) colorSpinner.getSelectedItem()).color())
                .reminderBeforeShift(((ReminderItem) remindersSpinner.getSelectedItem()).millisBeforeShift())
                .isTemplate(saveAsTemplateToggle.isChecked());

        final Float payRateValue = extractPayRate(payRate);
        if (payRateValue != null) {
            builder = builder.payRate(payRateValue);
        }

        if (!TextUtils.isEmpty(unpaidBreak.getText())) {
            final String breakDurationStr = unpaidBreak.getText().toString();
            try {
                final int mins = Integer.parseInt(breakDurationStr);
                builder = builder.unpaidBreakDuration(TimeUnit.MINUTES.toMillis(mins));
            } catch (Exception e) {
                Timber.e(e, "Error parsing break duration");
            }
        }

        builder = builder.timePeriod(TimePeriod.builder()
                .startMillis(toDateTime(startDate.getDate(), startTime.getTime()))
                .endMillis(toDateTime(endDate.getDate(), endTime.getTime()))
                .create());

        if (overtimeToggle.isChecked()) {
            final Float overtimePayRateValue = extractPayRate(overtimePayRate);
            if (overtimePayRateValue != null) {
                builder = builder.overtimePayRate(overtimePayRateValue);
                builder = builder.overtime(TimePeriod.builder()
                        .startMillis(toDateTime(
                                overtimeStartDate.getDate(), overtimeStartTime.getTime()))
                        .endMillis(toDateTime(
                                overtimeEndDate.getDate(), overtimeEndTime.getTime()))
                        .create());
            }
        }

        return builder.create();
    }

    @Override
    public boolean isOvertimeShowing() {
        return overtimeToggle.isChecked();
    }

    @Override
    public void showError(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showShiftList() {
        getActivity().finish();
    }

    @OnClick({
            R.id.start_date_input,
            R.id.end_date_input,
            R.id.overtime_start_date_input,
            R.id.overtime_end_date_input,
    })
    void onDateFieldClicked(final DateTextView clickedView) {
        final DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if (clickedView == startDate) {
                    getPresenter().onStartDateChanged(year, monthOfYear, dayOfMonth);
                } else if (clickedView == overtimeStartDate) {
                    getPresenter().onOvertimeStartDateChanged(year, monthOfYear, dayOfMonth);
                } else if (clickedView == endDate) {
                    getPresenter().onEndDateChanged(year, monthOfYear, dayOfMonth);
                } else if (clickedView == overtimeEndDate) {
                    getPresenter().onOvertimeEndDateChanged(year, monthOfYear, dayOfMonth);
                }
            }
        };

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(clickedView.getDate());
        new DatePickerDialog(
                getContext(),
                listener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    @OnClick({
            R.id.start_time_input,
            R.id.end_time_input,
            R.id.overtime_start_time_input,
            R.id.overtime_end_time_input,
    })
    void onTimeFieldClicked(final TimeTextView clickedView) {
        final TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (clickedView == startTime) {
                    getPresenter().onStartTimeChanged(hourOfDay, minute);
                } else if (clickedView == overtimeStartTime) {
                    getPresenter().onOvertimeStartTimeChanged(hourOfDay, minute);
                } else if (clickedView == endTime) {
                    getPresenter().onEndTimeChanged(hourOfDay, minute);
                } else if (clickedView == overtimeEndTime) {
                    getPresenter().onOvertimeEndTimeChanged(hourOfDay, minute);
                }
            }
        };

        final Time time = clickedView.getTime();
        new TimePickerDialog(
                getContext(),
                listener,
                time.hour,
                time.minute,
                DateFormat.is24HourFormat(getContext())
        ).show();
    }

    @Override
    public void showAd() {
        AdUtils.loadAndShowAd(adView);
    }

    static String formatPayRate(float payRate) {
        return String.format("%.2f", payRate);
    }
}
