package com.dgsd.android.shifttracker;

import android.app.Application;
import android.os.StrictMode;
import android.support.v7.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import io.fabric.sdk.android.Fabric;

import com.dgsd.android.shifttracker.manager.AnalyticsManager;
import com.dgsd.android.shifttracker.manager.AnalyticsManager.TrackingAgent;
import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.dgsd.android.shifttracker.module.AppServicesComponent;
import com.dgsd.android.shifttracker.module.DaggerAppServicesComponent;
import com.dgsd.android.shifttracker.module.STModule;
import com.dgsd.android.shifttracker.util.Api;
import com.dgsd.android.shifttracker.util.CrashlyticsLogger;

import java.util.List;

import timber.log.Timber;

public class STApp extends Application {

    private AppServicesComponent appServicesComponent;

    private RefWatcher refWatcher = RefWatcher.DISABLED;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            enableDebugTools();
        }

        enableAppOnlyFunctionality();

        appServicesComponent = DaggerAppServicesComponent.builder()
                .sTModule(getModule())
                .build();

        AnalyticsManager.addAgents(appServicesComponent.analyticsTrackingAgents());

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
    }

    STModule getModule() {
        return new STModule(this);
    }

    void enableAppOnlyFunctionality() {
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics(), new Answers());
            Timber.plant(new CrashlyticsLogger());
        }
    }

    /**
     * Enables all debug-only functionality.
     * <p/>
     * Extract into a method to allow overriding in other modules/tests
     */
    void enableDebugTools() {
        Timber.plant(new Timber.DebugTree());

        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build());

        StrictMode.enableDefaults();

        if (BuildConfig.DEBUG && Api.isUpTo(Api.LOLLIPOP_MR1)) {
            refWatcher = LeakCanary.install(this);
        }
    }

     /**
     * @return an {@link AppServicesComponent} which holds all the necessary dependencies
     * other application components may want to use for injection purposes
     */
    public AppServicesComponent getAppServicesComponent() {
        return appServicesComponent;
    }

    public RefWatcher getRefWatcher() {
        return refWatcher;
    }
}
