package com.dgsd.android.shifttracker.data;

import com.lacronicus.easydatastorelib.BooleanEntry;
import com.lacronicus.easydatastorelib.FloatEntry;
import com.lacronicus.easydatastorelib.IntEntry;
import com.lacronicus.easydatastorelib.LongEntry;
import com.lacronicus.easydatastorelib.Preference;

import java.util.concurrent.TimeUnit;

public interface AppSettings {

    @Preference("weekday_start")
    IntEntry startDayOfWeek();

    @Preference("default_start_time")
    LongEntry defaultStartTime();

    @Preference("default_end_time")
    LongEntry defaultEndTime();

    @Preference("default_unpaid_break_duration")
    LongEntry defaultUnpaidBreakDuration();

    @Preference("default_pay_rate")
    FloatEntry defaultPayRate();

    @Preference("default_reminder_index")
    IntEntry defaultReminderIndex();

    @Preference("default_color_index")
    IntEntry defaultColorIndex();

    @Preference("last_selected_view")
    IntEntry lastSelectedViewType();

    @Preference("last_launch_time")
    LongEntry lastLaunchTime();

    @Preference("first_launch_time")
    LongEntry firstLaunchTime();

    @Preference("launch_count")
    IntEntry launchCount();

    @Preference("has_shown_app_rating_dialog")
    BooleanEntry hasShownAppRatingDialog();

    class Defaults {
        public static long startTime() {
            return TimeUnit.HOURS.toMillis(9);
        }

        public static long endTime() {
            return TimeUnit.HOURS.toMillis(17);
        }

        public static int startDayOfWeek() {
            return 0;
        }

        public static float payRate() {
            return -1f;
        }

        public static long unpaidBreakDuration() {
            return 0;
        }

        public static int reminderItem() {
            return 0;
        }

        public static int colorItem() {
            return 4; // Our primary color
        }
    }
}