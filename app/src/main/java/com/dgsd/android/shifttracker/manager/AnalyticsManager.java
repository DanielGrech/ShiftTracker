package com.dgsd.android.shifttracker.manager;

import android.support.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;

public class AnalyticsManager {

    private static final String CATEGORY_CLICK = "click";

    private static final String CATEGORY_EVENT = "misc";

    private static final List<TrackingAgent> trackingAgents = new LinkedList<>();

    private AnalyticsManager() {
        // No instances..
    }

    public static void addAgent(@NonNull TrackingAgent agent) {
        trackingAgents.add(agent);
    }

    public static void addAgents(@NonNull Iterable<TrackingAgent> agents) {
        for (TrackingAgent agent : agents) {
            addAgent(agent);
        }

    }

    public static void trackScreenView(String screenName) {
        for (TrackingAgent agent : trackingAgents) {
            agent.screenView(screenName);
        }
    }

    public static void trackClick(String label) {
        for (TrackingAgent agent : trackingAgents) {
            agent.event(CATEGORY_CLICK, label);
        }
    }

    public static void trackEvent(String label) {
        for (TrackingAgent agent : trackingAgents) {
            agent.event(CATEGORY_EVENT, label);
        }

    }

    public interface TrackingAgent {

        void screenView(String screenName);

        void event(String eventCategory, String eventName);
    }
}
