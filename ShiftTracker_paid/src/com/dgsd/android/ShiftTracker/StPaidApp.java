package com.dgsd.android.ShiftTracker;

import android.content.Context;
import android.preference.*;
import com.dgsd.android.ShiftTracker.Receiver.NextShiftWidgetProvider;
import com.dgsd.android.ShiftTracker.Receiver.ShiftListWidgetProvider;
import com.dgsd.android.ShiftTracker.Service.DbService;

public class StPaidApp extends StApp implements DbService.OnDbEventListener, SettingsActivity.OnCreateSettingsListener {

    @Override
    public void onCreate() {
        super.onCreate();
        DbService.setOnDbEventListener(this);
        SettingsActivity.setOnCreateSettingsListener(this);
    }

    @Override
    public void onInsert() {
        ShiftListWidgetProvider.triggerUpdate(StPaidApp.this);
        NextShiftWidgetProvider.triggerUpdate(StPaidApp.this);
    }

    @Override
    public void onDelete() {
        ShiftListWidgetProvider.triggerUpdate(StPaidApp.this);
        NextShiftWidgetProvider.triggerUpdate(StPaidApp.this);
    }

    @Override
    public void onUpdate() {
        ShiftListWidgetProvider.triggerUpdate(StPaidApp.this);
        NextShiftWidgetProvider.triggerUpdate(StPaidApp.this);
    }

    @Override
    public void onSettingsCreated(final PreferenceActivity activity, PreferenceManager prefsManager, PreferenceScreen screen) {
        PreferenceCategory dataCategory = new PreferenceCategory(activity);
        dataCategory.setTitle(R.string.settings_category_data);
        dataCategory.setOrder(2);
        screen.addPreference(dataCategory);

        Preference exportPref = new Preference(activity);
        exportPref.setTitle(R.string.settings_title_export);
        exportPref.setSummary(R.string.settings_summary_export);
        exportPref.setKey(getString(R.string.settings_key_export));
        exportPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ExportDataTask.start(activity);

                return true;
            }
        });

        dataCategory.addPreference(exportPref);
    }
}
