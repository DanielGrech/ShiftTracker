package com.dgsd.android.shifttracker.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dgsd.android.shifttracker.R;
import com.dgsd.android.shifttracker.view.ShiftListItemView;
import com.dgsd.shifttracker.model.Shift;

import java.util.List;

public class ShiftTemplateAdapter extends BaseAdapter {

    private final List<Shift> shifts;

    public ShiftTemplateAdapter(List<Shift> shifts) {
        this.shifts = shifts;
    }

    @Override
    public int getCount() {
        return shifts == null ? 0 : shifts.size();
    }

    @Override
    public Shift getItem(int position) {
        return shifts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ShiftListItemView view;
        if (convertView == null) {
            view = ShiftListItemView.inflate(parent);

            final int horiztonalPadding = getDialogPadding(parent.getContext());
            view.setPaddingRelative(
                    horiztonalPadding,
                    view.getPaddingTop(),
                    horiztonalPadding,
                    view.getPaddingTop()
            );
        } else {
            view = (ShiftListItemView) convertView;
        }

        view.populate(getItem(position));

        return view;
    }

    private static int getDialogPadding(Context context) {
        TypedValue value = new TypedValue();
        if (!context.getTheme().resolveAttribute(R.attr.dialogPreferredPadding, value, true)) {
            return 0;
        }

        return context.getResources().getDimensionPixelSize(value.resourceId);
    }
}
