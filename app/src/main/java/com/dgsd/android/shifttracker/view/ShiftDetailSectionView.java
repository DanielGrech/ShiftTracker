package com.dgsd.android.shifttracker.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dgsd.android.shifttracker.R;

import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;

import static com.dgsd.android.shifttracker.util.DrawableUtils.getTintedDrawable;
import static com.dgsd.android.shifttracker.util.ViewUtils.extractThemeColor;

public class ShiftDetailSectionView extends LinearLayout {

    final ImageView sectionIcon;

    final ViewGroup sectionContent;

    public ShiftDetailSectionView(Context context) {
        this(context, null);
    }

    public ShiftDetailSectionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShiftDetailSectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.view_add_shift_section, this, true);

        sectionIcon = ButterKnife.findById(this, R.id.section_icon);
        sectionContent = ButterKnife.findById(this, R.id.section_content);

        TypedArray ta = null;
        try {
            ta = context.obtainStyledAttributes(attrs,
                    R.styleable.ShiftDetailSectionView, defStyleAttr, 0);

            final Drawable imageDrawable = ta.getDrawable(R.styleable.ShiftDetailSectionView_sectionIcon);
            if (imageDrawable != null) {
                sectionIcon.setImageDrawable(getTintedDrawable(imageDrawable,
                        extractThemeColor(context, android.R.attr.textColorSecondary, Color.BLACK)));
            }
        } finally {
            if (ta != null) {
                ta.recycle();
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        final List<View> children = new LinkedList<>();
        for (int i = 0, childCount = getChildCount(); i < childCount; i++) {
            final View childView = getChildAt(i);
            if (childView != sectionIcon && childView != sectionContent) {
                children.add(childView);
            }
        }

        for (View child : children) {
            removeView(child);
            sectionContent.addView(child);
        }
    }

    public void setSectionIcon(Drawable icon) {
        sectionIcon.setImageDrawable(icon);
    }
}
