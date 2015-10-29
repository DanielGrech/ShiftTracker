package com.dgsd.android.shifttracker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dgsd.android.shifttracker.R;
import com.dgsd.shifttracker.model.ReminderItem;

public class RemindersSpinnerAdapter extends BaseAdapter {

    private final ReminderItem[] reminders;

    public RemindersSpinnerAdapter(ReminderItem[] reminders) {
        this.reminders = reminders;
    }

    @Override
    public int getCount() {
        return reminders.length;
    }

    @Override
    public ReminderItem getItem(int position) {
        return reminders[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        final TextView view;
        if (convertView == null) {
            view = (TextView) LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        } else {
            view = (TextView) convertView;
        }
        view.setText(reminders[position].description());

        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TextView view;
        if (convertView == null) {
            view = (TextView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.li_reminder, parent, false);
        } else {
            view = (TextView) convertView;
        }
        view.setText(reminders[position].description());

        return view;
    }
}
