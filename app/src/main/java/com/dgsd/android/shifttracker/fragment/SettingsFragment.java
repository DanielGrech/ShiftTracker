package com.dgsd.android.shifttracker.fragment;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import com.dgsd.android.shifttracker.BuildConfig;
import com.dgsd.android.shifttracker.R;
import com.dgsd.android.shifttracker.STApp;
import com.dgsd.android.shifttracker.data.AppSettings;
import com.dgsd.android.shifttracker.data.AppSettings.Defaults;
import com.dgsd.android.shifttracker.manager.AnalyticsManager;
import com.dgsd.android.shifttracker.util.IntentUtils;
import com.dgsd.android.shifttracker.util.ModelUtils;
import com.dgsd.android.shifttracker.util.RxUtils;
import com.dgsd.android.shifttracker.util.ShiftToCsvMapper;
import com.dgsd.android.shifttracker.util.UpgradeAppPrompt;
import com.dgsd.android.shifttracker.util.ViewUtils;
import com.dgsd.shifttracker.data.DataProvider;
import com.dgsd.shifttracker.model.ReminderItem;
import com.dgsd.shifttracker.model.Shift;
import com.trello.rxlifecycle.RxLifecycle;

import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;
import de.psdev.licensesdialog.LicensesDialog;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static android.text.format.DateFormat.is24HourFormat;
import static com.dgsd.android.shifttracker.util.TimeUtils.getMillisSinceMidnight;
import static com.dgsd.android.shifttracker.util.TimeUtils.millisSinceMidnightToTime;

public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener,
        PreferenceManager.OnDisplayPreferenceDialogListener {

    private AppSettings appSettings;

    private DataProvider dataProvider;

    private Preference buildInfoPref;
    private Preference fullVersionPref;
    private Preference defaultReminderPref;
    private Preference appVersionPref;
    private Preference supportPref;
    private Preference ratePref;
    private Preference licensesPref;
    private Preference startDayPref;
    private Preference startTimePref;
    private Preference endTimePref;
    private Preference payRatePref;
    private Preference unpaidBreakDurationPref;
    private PreferenceCategory aboutCategory;
    private PreferenceCategory defaultsCategory;

    private void setupPreferences() {
        buildInfoPref = findPreference(R.string.settings_key_build_info);
        fullVersionPref = findPreference(R.string.settings_key_view_full_version);
        defaultReminderPref = findPreference(R.string.settings_key_default_reminder);
        appVersionPref = findPreference(R.string.settings_key_app_version);
        supportPref = findPreference(R.string.settings_key_support);
        ratePref = findPreference(R.string.settings_key_rate);
        licensesPref = findPreference(R.string.settings_key_licenses);
        startDayPref = findPreference(R.string.settings_key_start_day);
        startTimePref = findPreference(R.string.settings_key_default_start_time);
        endTimePref = findPreference(R.string.settings_key_default_end_time);
        payRatePref = findPreference(R.string.settings_key_default_pay_rate);
        unpaidBreakDurationPref = findPreference(R.string.settings_key_default_break_duration);

        aboutCategory = (PreferenceCategory) findPreference(R.string.settings_cat_key_about);
        defaultsCategory = (PreferenceCategory) findPreference(R.string.settings_cat_key_defaults);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final STApp app = (STApp) getActivity().getApplication();
        appSettings = app.getAppServicesComponent().appSettings();
        dataProvider = app.getAppServicesComponent().dataProvider();

        setupPreferences();

        if (buildInfoPref != null) {
            if (BuildConfig.DEBUG) {
                buildInfoPref.setSummary(getString(R.string.settings_summary_build_info,
                        BuildConfig.BUILD_TIME, BuildConfig.GIT_SHA));
            } else {
                aboutCategory.removePreference(buildInfoPref);
            }
        }

        if (BuildConfig.IS_PAID) {
            if (fullVersionPref != null) {
                getPreferenceScreen().removePreference(fullVersionPref);
            }
        } else {
            if (defaultReminderPref != null) {
                defaultsCategory.removePreference(defaultReminderPref);
            }
        }

        appVersionPref.setSummary(BuildConfig.VERSION_NAME);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onResume() {
        super.onResume();
        AnalyticsManager.trackScreenView("settings");
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        setSummaryValues();
    }

    @Override
    public void onPause() {
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (!super.onPreferenceTreeClick(preference)) {
            final String prefKey = preference.getKey();
            if (TextUtils.equals(prefKey, supportPref.getKey())) {
                AnalyticsManager.trackClick("support");
                final Intent emailIntent = IntentUtils.getEmailIntent(
                        BuildConfig.SUPPORT_EMAIL, String.format("%s %s support",
                                getString(R.string.app_name), BuildConfig.VERSION_NAME)
                );

                if (IntentUtils.isAvailable(getActivity(), emailIntent)) {
                    startActivity(emailIntent);
                }
                return true;
            } else if (TextUtils.equals(prefKey, ratePref.getKey())) {
                AnalyticsManager.trackClick("rate");
                final Intent playStoreIntent = IntentUtils.getPlayStoreIntent();
                if (IntentUtils.isAvailable(getActivity(), playStoreIntent)) {
                    startActivity(playStoreIntent);
                }

                return true;
            } else if (TextUtils.equals(prefKey, licensesPref.getKey())) {
                AnalyticsManager.trackClick("licenses");
                new LicensesDialog.Builder(getActivity())
                        .setTitle(R.string.settings_title_licenses)
                        .setIncludeOwnLicense(true)
                        .setNotices(R.raw.licenses)
                        .build()
                        .show();
                return true;
            } else if (TextUtils.equals(prefKey, fullVersionPref.getKey())) {
                AnalyticsManager.trackClick("get_full_app");
                final Intent playStoreIntent = IntentUtils.getPaidAppPlayStoreIntent();
                if (IntentUtils.isAvailable(getActivity(), playStoreIntent)) {
                    startActivity(playStoreIntent);
                }
                return true;
            } else if (TextUtils.equals(prefKey, getString(R.string.settings_key_export_data))) {
                AnalyticsManager.trackClick("export");
                if (BuildConfig.IS_PAID) {
                    runExport();
                } else {
                    UpgradeAppPrompt.show(getContext(), R.string.upgrade_app_prompt_export_shifts);
                }
                return true;
            } else if (TextUtils.equals(prefKey, startTimePref.getKey())) {
                AnalyticsManager.trackClick("start_time");
                showTimePickerForKey(prefKey);
            } else if (TextUtils.equals(prefKey, endTimePref.getKey())) {
                AnalyticsManager.trackClick("end_time");
                showTimePickerForKey(prefKey);
            } else if (TextUtils.equals(prefKey, payRatePref.getKey())) {
                AnalyticsManager.trackClick("pay_rate");
                showPayRateDialog();
            }
        }

        return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (startDayPref.getKey().equals(key)) {
            appSettings.startDayOfWeek().put(
                    Integer.parseInt(sharedPreferences.getString(key, String.valueOf(Defaults.startDayOfWeek()))));
        } else if (unpaidBreakDurationPref.getKey().equals(key)) {
            appSettings.defaultUnpaidBreakDuration().put(
                    Long.parseLong(sharedPreferences.getString(key, String.valueOf(Defaults.unpaidBreakDuration()))));
        } else if (defaultReminderPref.getKey().equals(key)) {
            final long defaultMillis = ModelUtils.getReminderItemByIndex(
                    getContext(), Defaults.reminderItem()).millisBeforeShift();
            final long reminderMillis = Long.parseLong(
                    sharedPreferences.getString(key, String.valueOf(defaultMillis)));

            ReminderItem[] reminderItems = ModelUtils.getReminderItems(getContext());
            for (int i = 0, len = reminderItems.length; i < len; i++) {
                if (reminderItems[i].millisBeforeShift() == reminderMillis) {
                    appSettings.defaultReminderIndex().put(i);
                    break;
                }
            }
        }

        setSummaryValues();
    }

    private void showTimePickerForKey(String prefKey) {
        final boolean isStartTime = prefKey.equals(startTimePref.getKey());

        final long millisSinceMidnight;
        if (isStartTime) {
            millisSinceMidnight = appSettings.defaultStartTime().get(Defaults.startTime());
        } else {
            millisSinceMidnight = appSettings.defaultEndTime().get(Defaults.endTime());
        }

        final OnTimeSetListener listener = new OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                final long millisSinceMidnight = getMillisSinceMidnight(hourOfDay, minute);
                if (isStartTime) {
                    appSettings.defaultStartTime().put(millisSinceMidnight);
                } else {
                    appSettings.defaultEndTime().put(millisSinceMidnight);
                }
            }
        };

        final Time time = millisSinceMidnightToTime(millisSinceMidnight);
        final TimePickerDialog tpd = new TimePickerDialog(
                getContext(),
                listener,
                time.hour,
                time.minute,
                is24HourFormat(getContext()));
        tpd.show();
    }

    private void showPayRateDialog() {
        final View inputView = LayoutInflater.from(getContext())
                .inflate(R.layout.view_pay_rate_preference_dialog, null);
        final EditText payRateInput = ButterKnife.findById(inputView, R.id.input);
        final float payRate = appSettings.defaultPayRate().get(Defaults.payRate());
        if (Float.compare(payRate, 0) > 0) {
            payRateInput.setText(String.valueOf(payRate));
        }
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.settings_title_default_pay_rate)
                .setView(inputView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Float payRate = ViewUtils.extractPayRate(payRateInput);
                        if (payRate == null) {
                            Snackbar.make(getView(),
                                    R.string.error_invalid_pay_rate, Snackbar.LENGTH_SHORT).show();
                        } else {
                            appSettings.defaultPayRate().put(payRate);
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private Preference findPreference(@StringRes int prefKey) {
        return findPreference(getString(prefKey));
    }

    @SuppressWarnings("deprecation")
    private void setSummaryValues() {
        final int weekValue = appSettings.startDayOfWeek().get(Defaults.startDayOfWeek());
        startDayPref.setSummary(getResources().getStringArray(R.array.days_of_week)[weekValue]);

        final Time startTime = millisSinceMidnightToTime(
                appSettings.defaultStartTime().get(Defaults.startTime()));
        startTimePref.setSummary(DateUtils.formatDateTime(
                getContext(), startTime.toMillis(false), DateUtils.FORMAT_SHOW_TIME));

        final Time endTime = millisSinceMidnightToTime(
                appSettings.defaultEndTime().get(Defaults.endTime()));
        endTimePref.setSummary(DateUtils.formatDateTime(
                getContext(), endTime.toMillis(false), DateUtils.FORMAT_SHOW_TIME));

        final float payRate = appSettings.defaultPayRate().get(Defaults.payRate());
        if (Float.compare(payRate, 0) <= 0) {
            payRatePref.setSummary(getString(R.string.settings_summary_default_pay_rate));
        } else {
            payRatePref.setSummary(ModelUtils.formatCurrency(payRate));
        }

        final long unpaidDuration = appSettings.defaultUnpaidBreakDuration()
                .get(Defaults.unpaidBreakDuration());

        final String[] unpaidDurationValues
                = getResources().getStringArray(R.array.default_unpaid_break_values);
        for (int i = 0, len = unpaidDurationValues.length; i < len; i++) {
            final String value = unpaidDurationValues[i];
            if (value.equals(String.valueOf(unpaidDuration))) {
                unpaidBreakDurationPref.setSummary(
                        getResources().getStringArray(R.array.default_unpaid_break_labels)[i]);
                break;
            }
        }

        final ReminderItem reminderItem = ModelUtils.getReminderItemByIndex(getContext(),
                appSettings.defaultReminderIndex().get(Defaults.reminderItem()));
        if (reminderItem != null) {
            defaultReminderPref.setSummary(reminderItem.description());
        }
    }

    private void runExport() {
        final ProgressDialog progressDialog =
                ProgressDialog.show(getContext(), null, getString(R.string.exporting_data), true);
        dataProvider.getShiftsBetween(0, Long.MAX_VALUE)
                .map(new Func1<List<Shift>, Uri>() {
                    @Override
                    public Uri call(List<Shift> shifts) {
                        try {
                            return new ShiftToCsvMapper(getContext()).generateCsvFile(shifts);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Uri>() {
                    @Override
                    public void call(Uri fileUri) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }

                        final Intent intent = IntentUtils.share(
                                getString(R.string.export_shift_email_subject), fileUri);
                        if (IntentUtils.isAvailable(getContext(), intent)) {
                            startActivity(intent);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable error) {
                        Timber.e(error, "Error running exporting");

                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }

                        if (getView() != null && getView().isShown()) {
                            Snackbar.make(getView(), R.string.error_exporting_data,
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
