package com.dgsd.android.ShiftTracker;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.dgsd.android.ShiftTracker.Util.DiagnosticUtils;
import com.dgsd.android.ShiftTracker.Util.IntentUtils;

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

        //Set the version string
        Preference p = prefMgr.findPreference(getString(R.string.settings_key_version));
        p.setSummary(DiagnosticUtils.getApplicationVersionString(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                StApp.doDefaultNavigateUp(this);
                return true;
            case R.id.contact:
                startActivity(IntentUtils.newEmailIntent(getString(R.string.support_email),
                        DiagnosticUtils.getApplicationName(this) + " support", null, "Contact support"));
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
