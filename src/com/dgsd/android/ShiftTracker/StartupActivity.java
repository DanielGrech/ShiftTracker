package com.dgsd.android.ShiftTracker;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import com.dgsd.android.ShiftTracker.Service.ImportFreeAppData;
import com.dgsd.android.ShiftTracker.Util.DiagnosticUtils;
import com.dgsd.android.ShiftTracker.Util.Prefs;

public class StartupActivity extends Activity {
    private static final String TAG = StartupActivity.class.getSimpleName();

    public static final String KEY_HAS_STARTED_BEFORE = "com.dgsd.android.ShiftTracker.StartupActivity._has_started_before";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent;

        ComponentName allInOne = new ComponentName(this, AllInOneActivity.class);
        ComponentName main = new ComponentName(this, MainActivity.class);

        if (DiagnosticUtils.isTablet(this)) {
            intent = new Intent(this, AllInOneActivity.class);
            getPackageManager().setComponentEnabledSetting(main, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        } else {
            intent = new Intent(this, MainActivity.class);
            getPackageManager().setComponentEnabledSetting(allInOne, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }

        //If we're using the paid app, we want to import data from free app on first run
        if (!StApp.isFreeApp(this)) {
            Prefs p = Prefs.getInstance(this);
            if (!p.get(KEY_HAS_STARTED_BEFORE, false)) {
                p.set(KEY_HAS_STARTED_BEFORE, true);

                if(hasFreeAppInstalled()) {
                    startService(new Intent(this, ImportFreeAppData.class));
                }
            }
        }

        //Pass on any extras we've already received
        intent.putExtras(getIntent());

        //Make our choice!
        startActivity(intent);
        finish();
    }

    private boolean hasFreeAppInstalled() {
        try {
            getPackageManager().getApplicationInfo("com.dgsd.android.ShiftTrackerFree", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}