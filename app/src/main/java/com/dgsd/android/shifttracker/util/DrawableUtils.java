package com.dgsd.android.shifttracker.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.graphics.drawable.DrawableCompat;

public class DrawableUtils {

    private DrawableUtils() {
        // No instances..
    }

    @SuppressWarnings("deprecation")
    public static Drawable getTintedDrawable(Context context, @DrawableRes int imageRes, @ColorInt int color) {
        return getTintedDrawable(context.getResources().getDrawable(imageRes), color);
    }

    @SuppressWarnings("deprecation")
    public static Drawable getTintedDrawable(Drawable drawable, @ColorInt int color) {
        final Drawable wrapped = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrapped, color);
        return wrapped;
    }
}
