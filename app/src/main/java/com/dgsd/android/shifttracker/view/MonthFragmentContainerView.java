package com.dgsd.android.shifttracker.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dgsd.android.shifttracker.R;
import com.dgsd.android.shifttracker.util.ViewUtils;
import com.dgsd.shifttracker.model.Shift;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.dgsd.android.shifttracker.util.DrawableUtils.getTintedDrawable;

public class MonthFragmentContainerView extends ScrollView {

    private static final float PARALLAX_FACTOR = 0.5f;
    private static final float MIN_ALPHA = 0.1f;

    @Bind(R.id.month_view)
    MonthView monthView;

    @Bind(R.id.day_title)
    TextView dayTitle;

    @Bind(R.id.shift_container)
    RecyclingLinearLayout shiftContainer;

    @Bind(R.id.empty_view)
    ViewGroup emptyView;

    @Bind(R.id.empty_view_image)
    ImageView emptyViewImage;

    private OnShiftClickedListener onShiftClickedListener;

    public MonthFragmentContainerView(Context context) {
        super(context);
    }

    public MonthFragmentContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MonthFragmentContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        ViewUtils.onPreDraw(this, new Runnable() {
            @Override
            public void run() {
                if (dayTitle != null) {
                    shiftContainer.setMinimumHeight(getHeight() - dayTitle.getHeight());
                    calculateEmptyViewPosition(0);
                }
            }
        });

        emptyViewImage.setImageDrawable(getTintedDrawable(getContext(),
                R.drawable.ic_smilie, getResources().getColor(R.color.divider)));
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        final float transY = Math.max(0, t * PARALLAX_FACTOR);
        final float alpha = Math.min(1f, Math.max(MIN_ALPHA, 1f - ((t * 1f) / monthView.getHeight())));

        monthView.setTranslationY(transY);
        monthView.setAlpha(alpha);

        calculateEmptyViewPosition(t);
    }

    private void calculateEmptyViewPosition(int scrollOffset) {
        final float originalPos = ((View) emptyView.getParent()).getTop();
        final float targetPos = (scrollOffset + (getHeight() - originalPos) - emptyView.getHeight()) / 2;

        emptyView.setTranslationY(targetPos);
    }

    public MonthView getMonthView() {
        return monthView;
    }

    public TextView getDayTitle() {
        return dayTitle;
    }

    public void setOnShiftClickedListener(OnShiftClickedListener onShiftClickedListener) {
        this.onShiftClickedListener = onShiftClickedListener;
    }

    public void showShifts(final List<Shift> shifts) {
        if (shifts == null || shifts.isEmpty()) {
            ViewUtils.show(emptyView);
            ViewUtils.hideInvisible(shiftContainer);
        } else {
            ViewUtils.show(shiftContainer);
            ViewUtils.hideInvisible(emptyView);
            shiftContainer.populate(shifts, new RecyclingLinearLayout.ViewFactory<Shift, ShiftListItemView>() {
                @Override
                public ShiftListItemView onCreateView() {
                    final ShiftListItemView view
                            = ShiftListItemView.inflate(shiftContainer);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final OnShiftClickedListener listener
                                    = MonthFragmentContainerView.this.onShiftClickedListener;
                            if (listener != null) {
                                final ShiftListItemView view = (ShiftListItemView) v;
                                listener.onShiftClicked(view, view.getShift());
                            }
                        }
                    });
                    view.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            final OnShiftClickedListener listener
                                    = MonthFragmentContainerView.this.onShiftClickedListener;
                            if (listener != null) {
                                final ShiftListItemView view = (ShiftListItemView) v;
                                listener.onShiftLongClicked(view, view.getShift());
                            }

                            return true;
                        }
                    });
                    return view;
                }

                @Override
                public void populate(ShiftListItemView view, Shift shift, int position) {
                    view.populate(shift);
                }
            });
        }
    }

    public interface OnShiftClickedListener {
        void onShiftClicked(View view, Shift shift);

        void onShiftLongClicked(View view, Shift shift);
    }
}
