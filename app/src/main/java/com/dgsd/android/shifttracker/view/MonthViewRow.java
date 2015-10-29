package com.dgsd.android.shifttracker.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;


import com.dgsd.android.shifttracker.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MonthViewRow extends LinearLayout {

    @Bind(R.id.cell1)
    MonthViewCell cell1;

    @Bind(R.id.cell2)
    MonthViewCell cell2;

    @Bind(R.id.cell3)
    MonthViewCell cell3;

    @Bind(R.id.cell4)
    MonthViewCell cell4;

    @Bind(R.id.cell5)
    MonthViewCell cell5;

    @Bind(R.id.cell6)
    MonthViewCell cell6;

    @Bind(R.id.cell7)
    MonthViewCell cell7;

    public MonthViewRow(final Context context) {
        super(context);
    }

    public MonthViewRow(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public MonthViewRow(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }
}
