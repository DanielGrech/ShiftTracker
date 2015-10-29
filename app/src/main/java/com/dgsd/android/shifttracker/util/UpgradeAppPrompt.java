package com.dgsd.android.shifttracker.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;

import com.dgsd.android.shifttracker.R;
import com.dgsd.android.shifttracker.manager.AnalyticsManager;

public class UpgradeAppPrompt {

    public static void show(final Context context, @StringRes int message) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.upgrade_app_prompt_title)
                .setMessage(message)
                .setPositiveButton(R.string.upgrade_app_prompt_action,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AnalyticsManager.trackClick("upgrade_app_prompt_link");
                                final Intent intent = IntentUtils.getPaidAppPlayStoreIntent();
                                if (IntentUtils.isAvailable(context, intent)) {
                                    context.startActivity(intent);
                                }
                                dialog.dismiss();
                            }
                        })
                .show();
    }
}
