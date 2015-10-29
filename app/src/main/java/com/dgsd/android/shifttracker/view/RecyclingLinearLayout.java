package com.dgsd.android.shifttracker.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.dgsd.android.shifttracker.util.ViewUtils;

import java.util.List;

/**
 * LinearLayout which will populate a list of items, handling recycling, hiding/showing etc.
 */
public class RecyclingLinearLayout extends LinearLayout {

    public RecyclingLinearLayout(Context context) {
        super(context);
    }

    public RecyclingLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclingLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            throw new IllegalArgumentException("Adding child views in XML is not supported");
        }
    }

    @SuppressWarnings("unchecked")
    public <DataType, ViewType extends View> void populate(List<DataType> data,
                                                           ViewFactory<DataType, ViewType> factory) {
        if (factory == null || data == null) {
            for (int i = 0, count = getChildCount(); i < count; i++) {
                ViewUtils.hide(getChildAt(i));
            }
        } else {
            recycleViews(factory, data.size());
            for (int i = 0, size = data.size(); i < size; i++) {
                ViewType child = (ViewType) getChildAt(i);
                ViewUtils.show(child);
                factory.populate(child, data.get(i), i);
            }
        }
    }

    /**
     * Ensures we have the correct number of views available (and visible) in
     * <code>this</code> for <code>itemCount</code> shifts
     *
     * @param itemCount The number of data items to display
     */
    private void recycleViews(ViewFactory factory, int itemCount) {
        final int currentViewCount = getChildCount();

        if (currentViewCount < itemCount) {
            final int numViewsToAdd = itemCount - currentViewCount;
            for (int i = 0; i < numViewsToAdd; i++) {
                final View v = factory.onCreateView();
                addView(v);
            }
        } else if (currentViewCount > itemCount) {
            for (int i = itemCount; i < currentViewCount; i++) {
                ViewUtils.hide(getChildAt(i));
            }
        }
    }

    public interface ViewFactory<DataType, ViewType extends View> {
        ViewType onCreateView();

        void populate(ViewType view, DataType data, int position);
    }

}
