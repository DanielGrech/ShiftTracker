package com.dgsd.android.shifttracker.util;

import android.graphics.Color;
import android.support.annotation.ColorInt;

public class ColorUtils {

    ColorUtils() {
        // No instances..
    }

    public static int complementary(@ColorInt int color) {
        return 0xffffff ^ color;
    }

    public static int darken(@ColorInt int color) {
        final float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }
}
