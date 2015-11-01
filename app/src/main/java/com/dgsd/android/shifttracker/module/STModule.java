package com.dgsd.android.shifttracker.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dgsd.android.shifttracker.BuildConfig;
import com.dgsd.android.shifttracker.R;
import com.dgsd.android.shifttracker.STApp;
import com.dgsd.android.shifttracker.data.AppSettings;
import com.dgsd.android.shifttracker.manager.AdManager;
import com.dgsd.android.shifttracker.manager.AnalyticsManager.TrackingAgent;
import com.dgsd.android.shifttracker.manager.AppRatingManager;
import com.dgsd.android.shifttracker.util.DebugTrackingAgent;
import com.dgsd.android.shifttracker.util.GoogleAnalyticsTrackingAgent;
import com.dgsd.shifttracker.data.DataProvider;
import com.dgsd.shifttracker.data.DbDataProvider;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.lacronicus.easydatastorelib.DatastoreBuilder;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.sqlbrite.SqlBrite;

import java.util.Arrays;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

/**
 * Dagger module to provide dependency injection
 */
@SuppressWarnings("unused")
@Module
public class STModule {
    private STApp application;

    public STModule(STApp app) {
        application = app;
    }

    @Provides
    @Singleton
    STApp providesApp() {
        return application;
    }

    @Provides
    Context providesContext() {
        return application;
    }

    @Provides
    RefWatcher providesRefWatcher(STApp app) {
        return app.getRefWatcher();
    }

    @Provides
    @Singleton
    SharedPreferences providesSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    @Singleton
    AppSettings providesAppSettings(SharedPreferences prefs) {
        return new DatastoreBuilder(prefs).create(AppSettings.class);
    }

    @Provides
    @Singleton
    DataProvider providesDataProvider(Context context, SqlBrite sqlBrite) {
        return DbDataProvider.create(context, sqlBrite);
    }

    @Provides
    @Singleton
    SqlBrite providesSqlBrite() {
        return SqlBrite.create(new SqlBrite.Logger() {
            @Override
            public void log(String message) {
                Timber.d("Database - %s", message);
            }
        });
    }

    @Provides
    @Singleton
    AppRatingManager providesAppRatingManager(AppSettings appSettings) {
        return new AppRatingManager(appSettings);
    }

    @Provides
    @Singleton
    AdManager providesAdManager(AppSettings appSettings) {
        return new AdManager(appSettings);
    }

    @Provides
    @Singleton
    List<TrackingAgent> providesTrackingAgents(Tracker tracker) {
        return Arrays.asList(
                new DebugTrackingAgent(),
                new GoogleAnalyticsTrackingAgent(tracker)
        );
    }

    @Provides
    @Singleton
    Tracker providesGoogleAnalyticsTracker(Context context, GoogleAnalytics ga) {
        final Tracker tracker = ga.newTracker(BuildConfig.GA_TRACKING_ID);
        tracker.setAppId(BuildConfig.APPLICATION_ID);
        tracker.setAppVersion(BuildConfig.VERSION_NAME);
        tracker.setAppName(context.getString(R.string.app_name));
        tracker.enableAutoActivityTracking(false);
        return tracker;
    }

    @Provides
    @Singleton
    GoogleAnalytics providesGoogleAnalytics(Context context) {
        final GoogleAnalytics ga = GoogleAnalytics.getInstance(context.getApplicationContext());
        ga.setDryRun(BuildConfig.DEBUG);
        ga.setLogger(new Logger() {
            @Override
            public void verbose(String message) {
                Timber.v(message);
            }

            @Override
            public void info(String message) {
                Timber.i(message);
            }

            @Override
            public void warn(String message) {
                Timber.w(message);
            }

            @Override
            public void error(String message) {
                Timber.e(message);
            }

            @Override
            public void error(Exception e) {
                Timber.v(e, "GA Error");
            }

            @Override
            public void setLogLevel(int i) {
                // No-op
            }

            @Override
            public int getLogLevel() {
                return LogLevel.VERBOSE;
            }
        });

        return ga;
    }
}
