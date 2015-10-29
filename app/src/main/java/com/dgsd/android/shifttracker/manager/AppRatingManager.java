package com.dgsd.android.shifttracker.manager;

import com.dgsd.android.shifttracker.BuildConfig;
import com.dgsd.android.shifttracker.data.AppSettings;
import com.dgsd.android.shifttracker.util.TimeUtils;

import java.util.Date;

public class AppRatingManager {

    final AppSettings appSettings;

    public AppRatingManager(AppSettings appSettings) {
        this.appSettings = appSettings;
    }

    public void logAppStart() {
        final long currentMillis = System.currentTimeMillis();

        appSettings.lastLaunchTime().put(currentMillis);
        appSettings.firstLaunchTime().put(appSettings.firstLaunchTime().get(currentMillis));
        appSettings.launchCount().put(appSettings.launchCount().get(0) + 1);
    }

    public void setHasShownRatingDialog(boolean hasShown) {
        appSettings.hasShownAppRatingDialog().put(hasShown);
    }

    public boolean shouldShowRatingDialog() {
        if (appSettings.hasShownAppRatingDialog().get(false)) {
            return false;
        } else {
            final long lastLaunchTime = appSettings.lastLaunchTime().get(Long.MAX_VALUE);
            final long firstLaunchTime = appSettings.firstLaunchTime().get(Long.MAX_VALUE);
            final int launchCount = appSettings.launchCount().get(0);

            return shouldShow(firstLaunchTime, lastLaunchTime, launchCount);
        }
    }

    private boolean shouldShow(long firstLaunchTime, long lastLaunchTime, int launchCount) {
        if (launchCount > BuildConfig.MIN_LAUNCHES_BEFORE_APPRATER) {
            final int daysBetween = TimeUtils.daysBetween(
                    new Date(System.currentTimeMillis()), new Date(firstLaunchTime));
            return daysBetween >= BuildConfig.MIN_DAYS_BEFORE_APPRATER;
        }

        return false;
    }

}
