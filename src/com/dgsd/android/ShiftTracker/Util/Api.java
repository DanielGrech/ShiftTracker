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

import android.os.Build;

/**
 * @author Daniel Grech
 */
public class Api {
	private static final String TAG = Api.class.getSimpleName();

	public static final int LEVEL = Build.VERSION.SDK_INT;
	public static final int FROYO = Build.VERSION_CODES.FROYO;
    public static final int GINGERBREAD = Build.VERSION_CODES.GINGERBREAD;
    public static final int GINGERBREAD_MR1 = Build.VERSION_CODES.GINGERBREAD_MR1;
	public static final int HONEYCOMB = Build.VERSION_CODES.HONEYCOMB;
	public static final int ICS = Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    public static final int JELLYBEAN = Build.VERSION_CODES.JELLY_BEAN;

	public static boolean isMin(int level) {
		return LEVEL >= level;
	}
}
