package com.dgsd.android.shifttracker.util;

import com.crashlytics.android.Crashlytics;
import com.dgsd.android.shifttracker.BuildConfig;

import timber.log.Timber;

public class CrashlyticsLogger extends Timber.Tree {

    public CrashlyticsLogger() {
        Crashlytics.setString("GIT_SHA", BuildConfig.GIT_SHA);
        Crashlytics.setString("BUILD_NUMBER", BuildConfig.BUILD_NUMBER);
        Crashlytics.setString("BUILD_TIME", BuildConfig.BUILD_TIME);
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if (t != null) {
            Crashlytics.logException(t);
        } else {
            Crashlytics.log(message);
        }
    }
}