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

package com.dgsd.android.ShiftTracker;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import com.dgsd.android.ShiftTracker.Data.Provider;
import com.dgsd.android.ShiftTracker.Util.DiagnosticUtils;

/**
 * @author Daniel Grech
 */
public class StApp extends Application {
    public static Class getHomeClass(Context c) {
        return MainActivity.class;
    }

    public static boolean isFreeApp(Context context) {
        return context.getPackageName().equals("com.dgsd.android.ShiftTrackerFree");
    }

    public static void doDefaultNavigateUp(Activity a) {
        Intent upIntent = new Intent(a, StApp.getHomeClass(a));
        if (NavUtils.shouldUpRecreateTask(a, upIntent)) {
            TaskStackBuilder.create(a).addNextIntent(upIntent).startActivities();
            a.finish();
        } else {
            NavUtils.navigateUpTo(a, upIntent);
        }
    }

	@Override
	public void onCreate() {
		super.onCreate();

        Provider.setAuthority(getPackageName() + ".Data.Provider");

	}
}
