package com.dgsd.android.shifttracker.util;

import android.content.Context;
import android.support.annotation.ColorInt;

import com.dgsd.android.shifttracker.R;
import com.dgsd.shifttracker.model.ColorItem;
import com.dgsd.shifttracker.model.ReminderItem;

import java.text.NumberFormat;

public class ModelUtils {

    private static NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance();

    private static ReminderItem[] reminderItemCache;

    private static ColorItem[] colorItemCache;

    private ModelUtils() {
        // No instances..
    }

    public static ReminderItem[] getReminderItems(Context context) {
        if (reminderItemCache == null) {
            final String[] descriptions = context.getResources().getStringArray(R.array.add_shift_reminder_labels);
            final String[] values = context.getResources().getStringArray(R.array.add_shift_reminder_values);

            if (values.length != descriptions.length) {
                throw new IllegalStateException("Values and descriptions size do not match");
            }

            reminderItemCache = new ReminderItem[descriptions.length];

            for (int i = 0, count = descriptions.length; i < count; i++) {
                reminderItemCache[i] = ReminderItem.builder()
                        .description(descriptions[i])
                        .millisBeforeShift(Integer.parseInt(values[i]))
                        .create();
            }
        }

        return reminderItemCache;
    }

    public static ReminderItem getReminderItem(Context context, long millisBefore) {
        final ReminderItem[] items = getReminderItems(context);
        for (ReminderItem item : items) {
            if (item.millisBeforeShift() == millisBefore) {
                return item;
            }
        }

        return null;
    }


    public static ReminderItem getReminderItemByIndex(Context context, int index) {
        try {
            return getReminderItems(context)[index];
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
    }

    public static ColorItem[] getColorItems(Context context) {
        if (colorItemCache == null) {
            final String[] descriptions = context.getResources().getStringArray(R.array.color_item_labels);
            final int[] values = context.getResources().getIntArray(R.array.color_item_values);

            if (values.length != descriptions.length) {
                throw new IllegalStateException("Values and descriptions size do not match");
            }

            colorItemCache = new ColorItem[descriptions.length];

            for (int i = 0, count = descriptions.length; i < count; i++) {
                colorItemCache[i] = ColorItem.builder()
                        .description(descriptions[i])
                        .color(values[i])
                        .create();
            }
        }

        return colorItemCache;
    }

    public static ColorItem getColorItem(Context context, @ColorInt int color) {
        final ColorItem[] items = getColorItems(context);
        for (ColorItem item : items) {
            if (item.color() == color) {
                return item;
            }
        }

        return null;
    }

    public static String formatCurrency(float amount) {
        return CURRENCY_FORMAT.format(amount).replaceAll("\\.00", "");
    }
}
