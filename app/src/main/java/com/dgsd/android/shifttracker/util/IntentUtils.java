package com.dgsd.android.shifttracker.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.annotation.NonNull;

import com.dgsd.android.shifttracker.BuildConfig;
import com.dgsd.shifttracker.model.Shift;

import java.util.List;

/**
 * Common Intent-related functionality
 */
public class IntentUtils {

    IntentUtils() {
        // No instances..
    }

    /**
     * Check if the given Intent has a component to handle it
     *
     * @param context Context used to query the {@link PackageManager}
     * @param intent  The intent to check
     * @return <code>true</code> if a component is available, <code>false</code> otherwise
     */
    public static boolean isAvailable(Context context, Intent intent) {
        final List<ResolveInfo> info = getResolveInfo(context, intent);
        return info != null && !info.isEmpty();
    }

    public static Intent getEmailIntent(String email, String subject) {
        return new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null))
                .putExtra(Intent.EXTRA_SUBJECT, subject);
    }

    public static Intent getPlayStoreIntent() {
        return new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID));
    }

    public static Intent getPaidAppPlayStoreIntent() {
        return new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + BuildConfig.PAID_APP_APPLICATION_ID));
    }

    public static Intent getCalendarItemIntent(@NonNull Shift shift) {
        return new Intent(Intent.ACTION_INSERT)
                .setData(Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, shift.timePeriod().startMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, shift.timePeriod().endMillis())
                .putExtra(Events.TITLE, shift.title())
                .putExtra(Events.DTSTART, shift.timePeriod().startMillis())
                .putExtra(Events.DTEND, shift.timePeriod().endMillis())
                .putExtra(Events.DESCRIPTION, shift.notes())
                .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
    }

    public static Intent share(String subject, Uri fileUri) {
        return new Intent(Intent.ACTION_SEND)
                .setType("*/*")
                .setData(fileUri)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .putExtra(Intent.EXTRA_SUBJECT, subject)
                .putExtra(Intent.EXTRA_STREAM, fileUri);
    }


    /**
     * Query the package manager with the given intent
     *
     * @param context Context used to query the {@link PackageManager}
     * @param intent  The intent to check
     * @return A list of components on the device which can handle the intent
     */
    static List<ResolveInfo> getResolveInfo(Context context, Intent intent) {
        final PackageManager manager = context.getPackageManager();
        return manager.queryIntentActivities(intent, 0);
    }
}
