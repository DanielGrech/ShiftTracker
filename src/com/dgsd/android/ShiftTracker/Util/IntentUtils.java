package com.dgsd.android.ShiftTracker.Util;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

public class IntentUtils {
    public static final String MIME_TYPE_EMAIL = "message/rfc822";

    public static Intent newMapsIntent(double lat, double lon, String name) {
        StringBuilder b = new StringBuilder();
        b.append("geo:").append(lat).append(',').append(lon);
        b.append("?q=").append(lat).append(',').append(lon);
        if(!TextUtils.isEmpty(name)) {
            b.append("(").append(name).append(")");
        }

        b.append("&z=15");

        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(b.toString()));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        return intent;
    }

    public static Intent newStreetViewIntent(double lat, double lon) {
        StringBuilder builder = new StringBuilder();
        builder.append("google.streetview:cbll=");
        builder.append(lat);
        builder.append(",");
        builder.append(lon);

        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(builder.toString()));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        return intent;
    }

    public static Intent newEmailIntent(String address, String subject, String body, String chooserTitle) {
        return newEmailIntent(new String[]{address}, subject, body, chooserTitle);
    }

    public static Intent newEmailIntent(String[] addresses, String subject, String body, String chooserTitle) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.setType(MIME_TYPE_EMAIL);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        return Intent.createChooser(intent, chooserTitle);
    }
}