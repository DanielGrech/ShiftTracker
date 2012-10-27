package com.dgsd.android.ShiftTrackerFree;

import android.content.Intent;
import android.net.Uri;
import android.preference.*;
import com.dgsd.android.ShiftTracker.SettingsActivity;
import com.dgsd.android.ShiftTracker.StApp;

public class StFreeApp extends StApp implements SettingsActivity.OnCreateSettingsListener {

    @Override
    public void onCreate() {
        super.onCreate();
        SettingsActivity.setOnCreateSettingsListener(this);
    }

    @Override
    public void onSettingsCreated(final PreferenceActivity activity, PreferenceManager prefsManager, PreferenceScreen screen) {
        PreferenceCategory aboutCategory = (PreferenceCategory) prefsManager.findPreference(getString(R.string.settings_category_about));

        Preference fullVersionPref = new Preference(activity);
        fullVersionPref.setOrder(0);
        fullVersionPref.setTitle(R.string.settings_title_full_app);
        fullVersionPref.setSummary(R.string.settings_summary_full_app);
        fullVersionPref.setKey(getString(R.string.settings_key_full_app));
        fullVersionPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Uri uri = Uri.parse("market://details?id=com.dgsd.android.ShiftTracker");
                activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                return true;
            }
        });

        aboutCategory.addPreference(fullVersionPref);
    }
}
