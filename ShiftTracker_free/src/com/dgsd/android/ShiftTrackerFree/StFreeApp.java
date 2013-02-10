/*
 * Copyright 2013 Daniel Grech
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
