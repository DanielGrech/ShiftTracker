package com.dgsd.android.shifttracker.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;

import com.dgsd.android.shifttracker.util.DrawableUtils;

public class TintedWhiteFloatingActionButton extends FloatingActionButton {

    public TintedWhiteFloatingActionButton(Context context) {
        this(context, null);
    }

    public TintedWhiteFloatingActionButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TintedWhiteFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final Drawable drawable = getDrawable();
        if (drawable != null) {
            setImageDrawable(DrawableUtils.getTintedDrawable(drawable, Color.WHITE));
        }
    }
}
