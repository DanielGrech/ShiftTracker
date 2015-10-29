package com.dgsd.android.shifttracker.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dgsd.android.shifttracker.R;

public class SettingsActivity extends BaseActivity {

    public static Intent createIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showUpIndicator();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.act_settings;
    }
}
