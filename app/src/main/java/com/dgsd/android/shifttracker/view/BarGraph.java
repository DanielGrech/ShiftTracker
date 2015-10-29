package com.dgsd.android.shifttracker.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.dgsd.android.shifttracker.R;

import java.util.ArrayList;

import static com.dgsd.android.shifttracker.util.ViewUtils.dpToPx;

public class BarGraph extends View {

    private static final int ANIM_DURATION = 1500;

    private static final Interpolator ANIM_INTERPOLATOR = new DecelerateInterpolator(1.5f);

    private Paint axisPaint;

    private Paint axisTitlePaint;

    private Paint barPaint;

    private ArrayList<Float> barValues;

    private float maxBarValue = -1;

    private float interpolationValue;

    private boolean hasStartedAnimation;

    private String xAxisTitle;

    private String yAxisTitle;

    private Rect xAxisTitleTextBounds;

    private Rect yAxisTitleTextBounds;

    private int axisTitlePadding;

    public BarGraph(Context context) {
        this(context, null);
    }

    public BarGraph(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarGraph(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final Resources res = getResources();

        axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        axisPaint.setColor(Color.LTGRAY);
        axisPaint.setStyle(Paint.Style.STROKE);
        axisPaint.setStrokeWidth(dpToPx(context, 1));

        axisTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        axisTitlePaint.setColor(res.getColor(R.color.text_secondary));
        axisTitlePaint.setTextSize(
                res.getDimensionPixelSize(R.dimen.bar_graph_axis_title_text_size));

        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        barPaint.setColor(getResources().getColor(R.color.accent));
        barPaint.setAlpha(200);
        barPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        barValues = new ArrayList<>();

        xAxisTitleTextBounds = new Rect();
        yAxisTitleTextBounds = new Rect();

        axisTitlePadding = res.getDimensionPixelSize(R.dimen.bar_graph_axis_title_padding);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int h = canvas.getHeight();
        final int w = canvas.getWidth();

        final int pT = getPaddingTop();
        final int pB = getPaddingBottom();
        final int pL = getPaddingLeft();
        final int pR = getPaddingRight();

        final boolean hasXAxisTitle = !TextUtils.isEmpty(xAxisTitle);
        final boolean hasYAxisTitle = !TextUtils.isEmpty(yAxisTitle);

        int xAxisY = h - pB;
        int xAxisEndX = w - pR;
        int yAxisX = pL;

        final float axisWidth = axisPaint.getStrokeWidth();

        // X-Axis label
        if (hasXAxisTitle) {
            xAxisY -= axisTitlePadding * 2;

            final int startLabelX = xAxisEndX;
            final int targetLabelX = xAxisEndX - axisTitlePadding - xAxisTitleTextBounds.width();

            canvas.drawText(xAxisTitle,
                    startLabelX - ((startLabelX - targetLabelX) * interpolationValue),
                    xAxisY + axisTitlePadding,
                    axisTitlePaint);
            xAxisY -= xAxisTitleTextBounds.height();
        }

        if (hasYAxisTitle) {
            yAxisX += axisTitlePadding * 2;
            canvas.save();
            {
                canvas.rotate(-90);
                canvas.translate(
                        (-yAxisTitleTextBounds.width() - axisTitlePadding) * interpolationValue,
                        yAxisTitleTextBounds.height() + axisTitlePadding);

                canvas.drawText(yAxisTitle, 0, 0, axisTitlePaint);
            }
            canvas.restore();

            yAxisX += yAxisTitleTextBounds.height();
        }

        // Y-Axis
        canvas.drawLine(yAxisX, pT, yAxisX, xAxisY, axisPaint);

        // X-Axis
        canvas.drawLine(yAxisX, xAxisY, xAxisEndX, xAxisY, axisPaint);

        if (!barValues.isEmpty()) {
            final float barWidth = w / barValues.size();
            for (int i = 0, size = barValues.size(); i < size; i++) {
                final float percentageY = interpolationValue * (barValues.get(i) / maxBarValue);

                canvas.drawRect(yAxisX + (axisWidth / 2) + (barWidth * i),
                        pT + (h * (1f - percentageY)),
                        yAxisX + (axisWidth / 2) + (barWidth * (i + 1)),
                        xAxisY - (axisWidth / 2),
                        barPaint);
            }
        }
    }

    public float getInterpolationValue() {
        return interpolationValue;
    }

    public void setInterpolationValue(float interpolationValue) {
        this.interpolationValue = interpolationValue;
        invalidate();
    }

    public void setXAxisTitle(String title) {
        xAxisTitle = title;
        if (TextUtils.isEmpty(title)) {
            xAxisTitleTextBounds.setEmpty();
        } else {
            axisTitlePaint.getTextBounds(title, 0, title.length(), xAxisTitleTextBounds);
        }
        invalidate();
    }

    public void setYAxisTitle(String title) {
        yAxisTitle = title;
        if (TextUtils.isEmpty(title)) {
            yAxisTitleTextBounds.setEmpty();
        } else {
            axisTitlePaint.getTextBounds(title, 0, title.length(), yAxisTitleTextBounds);
        }
        invalidate();
    }

    public void reset() {
        barValues.clear();
        invalidate();
    }

    public void addValue(float value) {
        barValues.add(value);

        if (Float.compare(maxBarValue, value) < 0) {
            maxBarValue = value;
        }

        if (!hasStartedAnimation) {
            invalidate();
        }
    }

    public void startAnimation() {
        hasStartedAnimation = true;
    }

    public void endAnimation() {
        hasStartedAnimation = false;
        runAnimation();
    }

    public void runAnimation() {
        final ObjectAnimator anim = ObjectAnimator.ofFloat(this, "interpolationValue", 0f, 1f);
        anim.setInterpolator(ANIM_INTERPOLATOR);
        anim.setDuration(ANIM_DURATION);
        anim.start();
    }

    private static class IndexToInterpolation {
        public int index;
        public float value;

        public IndexToInterpolation(int index, float value) {
            this.index = index;
            this.value = value;
        }
    }
}