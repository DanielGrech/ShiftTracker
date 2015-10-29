package com.dgsd.android.shifttracker.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dgsd.android.shifttracker.R;
import com.dgsd.shifttracker.model.ColorItem;

import static com.dgsd.android.shifttracker.util.DrawableUtils.getTintedDrawable;

public class ColorsSpinnerAdapter extends BaseAdapter {

    private final ColorItem[] colors;

    public ColorsSpinnerAdapter(ColorItem[] colors) {
        this.colors = colors;
    }

    @Override
    public int getCount() {
        return colors.length;
    }

    @Override
    public ColorItem getItem(int position) {
        return colors[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        final Resources res = context.getResources();
        final TextView view;
        if (convertView == null) {
            view = (TextView) LayoutInflater.from(context)
                    .inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
            view.setCompoundDrawablePadding(res.getDimensionPixelSize(R.dimen.padding_small));
        } else {
            view = (TextView) convertView;
        }
        view.setText(colors[position].description());

        final Drawable indicator = res.getDrawable(R.drawable.color_indicator);
        view.setCompoundDrawablesRelativeWithIntrinsicBounds(
                getTintedDrawable(indicator, colors[position].color()),
                null, null, null);
        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TextView view;
        if (convertView == null) {
            view = (TextView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.li_color_description, parent, false);
        } else {
            view = (TextView) convertView;
        }
        view.setText(colors[position].description());

        return view;
    }
}
