package com.dgsd.android.ShiftTracker.Util;

import android.content.Context;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;

public class UIUtils {

    public static void tryForceMenuOverflow(Context context) {
        try {
            ViewConfiguration config = ViewConfiguration.get(context);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            //Thats a shame .. o well..
        }
    }

    public static String getDurationAsHours(long minutes) {
        String text = null;
        if(minutes < 60) {
            if(minutes > 0 && minutes < 10)
                text = "0" + minutes;
            else
                text = "" + minutes;

            return text + "m";
        }

        text = (int) Math.floor(minutes / 60) + "h";

        final long mins = minutes % 60;

        if(mins > 0 && mins < 10)
            text += " 0" + mins + "m";
        else if(mins >= 10)
            text += " " + mins + "m";

        return text;
    }
}
