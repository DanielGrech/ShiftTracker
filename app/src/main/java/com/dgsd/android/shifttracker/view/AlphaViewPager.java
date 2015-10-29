package com.dgsd.android.shifttracker.view;


import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * ViewPager which fades out side pages
 */
public class AlphaViewPager extends ViewPager {

    public AlphaViewPager(Context context) {
        this(context, null);
    }

    public AlphaViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPageTransformer(false, new AlphaPageTransformer());
    }

    static class AlphaPageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(final View page, final float position) {
            page.setAlpha(1f - Math.abs(position));
        }
    }

}
