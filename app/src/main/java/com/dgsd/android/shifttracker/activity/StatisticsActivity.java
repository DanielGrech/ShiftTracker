package com.dgsd.android.shifttracker.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.dgsd.android.shifttracker.fragment.StatisticsFragment;

public class StatisticsActivity extends SingleFragmentActivity {

    public static Intent createIntent(Context context) {
        return new Intent(context, StatisticsActivity.class);
    }

    @Override
    protected Fragment createFragment() {
        return StatisticsFragment.newInstance();
    }
}
