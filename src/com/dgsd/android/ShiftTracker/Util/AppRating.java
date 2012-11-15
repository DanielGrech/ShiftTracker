package com.dgsd.android.ShiftTracker.Util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import com.dgsd.android.ShiftTracker.R;
import org.holoeverywhere.app.AlertDialog;

/**
 * Helper class which creates a dialog prompting the user to
 * rate the current application. 
 * 
 * This class can also be set to automatically show the dialog after a
 * given number of days, or a given number of launches. In order for this
 * to work properly, the method {@link #app_launched(android.content.Context)} must be
 * called in the activity {@link android.app.Activity#onCreate(android.os.Bundle)} method
 * 
 * Adapted from: http://www.androidsnippets.com/prompt-engaged-users-to-rate-your-app-in-the-android-market-appirater
 */
public class AppRating {
    
    private final static int DAYS_UNTIL_PROMPT = 7;
    private final static int LAUNCHES_UNTIL_PROMPT = 3;
    
    public static void app_launched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
        if (prefs.getBoolean("dontshowagain", false)) { return ; }
        
        SharedPreferences.Editor editor = prefs.edit();
        
        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }
        
        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch + 
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                showRateDialog(mContext, editor);
            }
        }
        
        editor.commit();
    }   
    
    public static void showRateDialog(final Context context, final SharedPreferences.Editor editor) {
        final String appName = DiagnosticUtils.getApplicationName(context);
        final AlertDialog.Builder b = new AlertDialog.Builder(context);
        b.setTitle("Rate " + appName);
        b.setIcon(R.drawable.ic_launcher);
        b.setMessage("If you find " + appName + " useful, please take a moment to rate it. Thanks for your support!");
        b.setPositiveButton("Rate", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="
                        + DiagnosticUtils.getApplicationPackage(context))));

                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }

                dialog.dismiss();
            }
        });

        b.setNegativeButton("No thanks", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }
                dialog.dismiss();
            }
        });

        b.create().show();
    }
}