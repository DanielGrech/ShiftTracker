/*
 * Copyright 2013 Daniel Grech
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
