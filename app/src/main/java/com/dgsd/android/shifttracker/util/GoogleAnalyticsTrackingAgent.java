package com.dgsd.android.shifttracker.util;

import com.dgsd.android.shifttracker.manager.AnalyticsManager;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class GoogleAnalyticsTrackingAgent implements AnalyticsManager.TrackingAgent {

    private final Tracker tracker;

    public GoogleAnalyticsTrackingAgent(Tracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public void screenView(String screenName) {
        tracker.setScreenName(screenName);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void event(String eventCategory, String eventName) {
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(eventCategory)
                .setAction(eventName)
                .build());
    }
}
