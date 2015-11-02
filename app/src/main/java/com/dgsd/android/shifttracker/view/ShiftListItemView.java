package com.dgsd.android.shifttracker.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dgsd.android.shifttracker.R;
import com.dgsd.android.shifttracker.util.ModelUtils;
import com.dgsd.android.shifttracker.util.TimeUtils;
import com.dgsd.android.shifttracker.util.ViewUtils;
import com.dgsd.shifttracker.model.Shift;

import java.text.NumberFormat;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.text.format.DateUtils.FORMAT_ABBREV_ALL;
import static android.text.format.DateUtils.FORMAT_SHOW_TIME;
import static android.text.format.DateUtils.formatDateTime;
import static com.dgsd.android.shifttracker.util.DrawableUtils.getTintedDrawable;

public class ShiftListItemView extends RelativeLayout {

    @Bind(R.id.total_pay)
    TextView totalPay;

    @Bind(R.id.title)
    TextView title;

    @Bind(R.id.time_summary)
    TextView timeSummary;

    @Bind(R.id.notes)
    TextView notes;

    @Bind(R.id.color_indicator)
    View colorIndicator;

    @Bind(R.id.edit_button)
    ImageView editButton;

    private Shift shift;

    public static ShiftListItemView inflate(ViewGroup parent) {
        return (ShiftListItemView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.li_shift, parent, false);
    }

    public ShiftListItemView(Context context) {
        super(context);
    }

    public ShiftListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShiftListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void populate(Shift shift) {
        this.shift = shift;
        colorIndicator.setBackground(getTintedDrawable(
                getContext(), R.drawable.color_indicator, shift.color()));

        title.setText(TextUtils.isEmpty(shift.title()) ?
                getContext().getString(R.string.default_shift_title) : shift.title());
        ViewUtils.setTextOrHide(notes, shift.notes());

        final String startTime = formatDateTime(
                getContext(), shift.timePeriod().startMillis(), FORMAT_ABBREV_ALL | FORMAT_SHOW_TIME
        );

        final String endTime = formatDateTime(
                getContext(), shift.timePeriod().endMillis(), FORMAT_ABBREV_ALL | FORMAT_SHOW_TIME
        );

        final String elapsedTime = TimeUtils.formatDuration(shift.totalPaidDuration());
        timeSummary.setText(String.format("%s - %s (%s)", startTime, endTime, elapsedTime));

        final float totalPayAmount = shift.totalPay();
        if (Float.compare(totalPayAmount, 0f) <= 0) {
            ViewUtils.hide(totalPay);
        } else {
            ViewUtils.show(totalPay);
            totalPay.setText(ModelUtils.formatCurrency(totalPayAmount));
        }
    }

    public Shift getShift() {
        return shift;
    }

    public void setEditButtonVisible(boolean visible) {
        ViewUtils.visibleWhen(visible, editButton);
    }

    public void setOnEditClickListener(View.OnClickListener listener) {
        editButton.setOnClickListener(listener);
    }
}
