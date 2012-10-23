package com.dgsd.android.ShiftTracker;

import android.os.Bundle;
import android.preference.PreferenceManager;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

/**
 *
 * @author Daniel Grech
 */
public class SettingsActivity extends SherlockPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.settings);

        PreferenceManager prefMgr = getPreferenceManager();
        prefMgr.setSharedPreferencesName(Const.SHARED_PREFS_NAME);
        prefMgr.setSharedPreferencesMode(MODE_WORLD_READABLE);

        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                StApp.doDefaultNavigateUp(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
