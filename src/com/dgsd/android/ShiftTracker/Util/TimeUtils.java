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

import android.text.format.Time;

/**
 * @author Daniel Grech
 */
public class TimeUtils {
	private static final String TAG = TimeUtils.class.getSimpleName();

	public static final class InMillis {
		public static final long SECOND = 1000;
		public static final long MINUTE = SECOND * 60;
		public static final long HOUR = MINUTE * 60;
		public static final long DAY = HOUR * 24;
		public static final long WEEK = DAY * 7;
		public static final long MONTH = WEEK * 4;
		public static final long YEAR = MONTH * 12;
	}

	private static final Object TIME_LOCK = new Object();
    private static Time mTime = new Time();
    private static Time mTime2 = new Time();

    public static long getMillisFrom(int julianDay, long timeOfDayMillis) {
        synchronized (TIME_LOCK) {
            mTime.setJulianDay(julianDay);

            mTime2.set(timeOfDayMillis);
            mTime2.normalize(true);

            mTime.hour = mTime2.hour;
            mTime.minute = mTime2.minute;
            mTime.second = mTime2.second;

            mTime.normalize(true);
            return mTime.toMillis(true);
        }
    }

	public static int getCurrentJulianDay() {
		synchronized (TIME_LOCK) {
			mTime.setToNow();
			return Time.getJulianDay(mTime.toMillis(true), mTime.gmtoff);
		}
	}

	public static long getStartMillisForJulianDay(int julianDay) {
		synchronized (TIME_LOCK) {
			return mTime.setJulianDay(julianDay);
		}
	}

    public static long getEndMillisForJulianDay(int julianDay) {
        long startMillis = getStartMillisForJulianDay(julianDay);
        return startMillis + InMillis.DAY - InMillis.SECOND;
    }

	public static int getJulianDay(Time t) {
		if(t == null) {
			return 0;
		} else {
			return Time.getJulianDay(t.toMillis(true), t.gmtoff);
		}
	}

	public static int getJulianDay(long millis) {
		synchronized (TIME_LOCK) {
			return Time.getJulianDay(millis, mTime.gmtoff);
		}
	}



	public static long getCurrentMillis() {
		synchronized (TIME_LOCK) {
			mTime.setToNow();
			return mTime.toMillis(true);
		}
	}

	public static String getWeekInMonthString(long millis) {
		synchronized (TIME_LOCK) {
			mTime.set(millis);
			switch((mTime.monthDay - 1) /7) {
				case 0: return "first";
				case 1: return "second";
				case 2: return "third";
				case 3: return "fourth";
				default: return "last";
			}
		}
	}

    public static String getFullDayOfWeekString(long millis) {
        synchronized (TIME_LOCK) {
            mTime.set(millis);
            return mTime.format("%A");
        }
    }

	public static String getDayOfWeekString(long millis) {
		synchronized (TIME_LOCK) {
			mTime.set(millis);
			return mTime.format("%a");
		}
	}

    public static String getFullMonthString(long millis) {
        synchronized (TIME_LOCK) {
            mTime.set(millis);
            return mTime.format("%B");
        }
    }

	public static String getMonthString(long millis) {
		synchronized (TIME_LOCK) {
			mTime.set(millis);
			return mTime.format("%b");
		}
	}

	public static String getDayOfMonthString(long millis) {
		synchronized (TIME_LOCK) {
			mTime.set(millis);
			return mTime.format("%e");
		}
	}

	public static String getDayOfMonthString(int julianDay) {
		synchronized (TIME_LOCK) {
			mTime.setJulianDay(julianDay);
			return mTime.format("%e");
		}
	}

	public static String getDateString(long millis) {
		synchronized (TIME_LOCK) {
			mTime.set(millis);
			return mTime.format("%d/%m/%y");
		}
	}

    public static String getDateString(int julianDay) {
        synchronized (TIME_LOCK) {
            mTime.setJulianDay(julianDay);
            return mTime.format("%d/%m/%y");
        }
    }

    public static long getMillisSinceMidnight(long millis) {
        return millis - getStartMillisForJulianDay(getJulianDay(millis));
    }

    public static long round(long time) {
        //Round time to nearest 15 minute block
        synchronized (TIME_LOCK) {
            mTime.set(time);
            mTime.normalize(true);

            if(mTime.minute < 8)
                mTime.minute = 0;
            else if(mTime.minute < 23)
                mTime.minute = 15;
            else if(mTime.minute < 38)
                mTime.minute = 30;
            else if(mTime.minute < 53)
                mTime.minute = 45;
            else {
                mTime.minute = 0;
                mTime.hour++;
            }

            mTime.normalize(true);
            return mTime.toMillis(true);
        }
    }
}
