package com.dgsd.android.shifttracker.manager;

import com.dgsd.android.shifttracker.BuildConfig;
import com.dgsd.android.shifttracker.data.AppSettings;
import com.dgsd.android.shifttracker.util.TimeUtils;

import java.util.Date;

public class AdManager {

    final AppSettings appSettings;

    final boolean adsEnabled;

    public AdManager(AppSettings appSettings) {
        this.appSettings = appSettings;
        this.adsEnabled = !BuildConfig.IS_PAID;
    }

    public boolean shouldShowAd() {
        return adsEnabled && shouldShow(
                appSettings.firstLaunchTime().get(System.currentTimeMillis()),
                appSettings.launchCount().get(0)
        );
    }

    private boolean shouldShow(long firstLaunchTime, int launchCount) {
        if (launchCount > BuildConfig.MIN_LAUNCHES_BEFORE_SHOWING_ADS) {
            final int daysBetween = TimeUtils.daysBetween(
                    new Date(System.currentTimeMillis()), new Date(firstLaunchTime));
            return daysBetween >= BuildConfig.MIN_DAYS_BEFORE_SHOWING_ADS;
        }

        return false;
    }

}
