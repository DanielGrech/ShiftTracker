package com.dgsd.android.shifttracker.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.dgsd.android.shifttracker.BuildConfig;
import com.dgsd.android.shifttracker.data.LegacyDbOpenHelper;
import com.dgsd.android.shifttracker.service.UpgradeMigrationService;

public class StartupActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.IS_PAID) {
            UpgradeMigrationService.start(this);
        }

        final Intent intent;
        if (LegacyDbOpenHelper.databaseExists(this)) {
            intent = LegacyMigrationActivity.createIntent(this);
        } else {
            intent = HomeActivity.createIntent(this);
        }

        startActivity(intent);
        finish();
    }
}
