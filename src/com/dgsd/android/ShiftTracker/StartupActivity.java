package com.dgsd.android.ShiftTracker;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import com.dgsd.android.ShiftTracker.Util.DiagnosticUtils;

public class StartupActivity extends Activity {
    private static final String TAG = StartupActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent;

        ComponentName allInOne = new ComponentName(this, AllInOneActivity.class);
        ComponentName main = new ComponentName(this, MainActivity.class);

        if(DiagnosticUtils.isTablet(this)) {
            intent = new Intent(this, AllInOneActivity.class);
            getPackageManager().setComponentEnabledSetting(main, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        } else {
            intent = new Intent(this, MainActivity.class);
            getPackageManager().setComponentEnabledSetting(allInOne, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }

        //Pass on any extras we've already received
        intent.putExtras(getIntent());

        //Make our choice!
        startActivity(intent);
        finish();
    }
}