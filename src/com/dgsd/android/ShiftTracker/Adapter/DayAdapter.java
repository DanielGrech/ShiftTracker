/*
 * Copyright 2013 Daniel Grech
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.dgsd.android.ShiftTracker.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Typeface;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.dgsd.android.ShiftTracker.Data.DbField;
import com.dgsd.android.ShiftTracker.Data.DbTable;
import com.dgsd.android.ShiftTracker.Data.Provider;
import com.dgsd.android.ShiftTracker.Model.Shift;
import com.dgsd.android.ShiftTracker.R;
import com.dgsd.android.ShiftTracker.Util.Prefs;
import com.dgsd.android.ShiftTracker.Util.TimeUtils;
import com.dgsd.android.ShiftTracker.Util.UIUtils;
import com.emilsjolander.components.StickyListHeaders.StickyListHeadersCursorAdapter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Random;

public class DayAdapter extends CursorAdapter {
    private LayoutInflater inflater;

    private SparseArray<String> mIdToTimeArray;
    private SparseArray<String> mIdToPayArray;

    private boolean mIs24Hour;
    private boolean mShowIncomePref;

    private Formatter mFormatter;
    private StringBuilder mStringBuilder;

    private NumberFormat mCurrencyFormatter;
    private int mJulianDay;

    public DayAdapter(Context context, Cursor c, int julianDay) {
        super(context, c, false);
        inflater = LayoutInflater.from(context);
        mJulianDay = julianDay;

        mIs24Hour = DateFormat.is24HourFormat(context);

        mStringBuilder = new StringBuilder();
        mFormatter = new Formatter((mStringBuilder));

        mCurrencyFormatter = NumberFormat.getCurrencyInstance();

        //Caching
        mIdToTimeArray = new SparseArray<String>();
        mIdToPayArray = new SparseArray<String>();

        mShowIncomePref = Prefs.getInstance(context).get(context.getString(R.string.settings_key_show_income), true);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        final Shift shift = Shift.fromCursor(cursor);

        holder.name.setText(shift.name);
        holder.time.setText(getTimeText(shift));

        String payText = getPayText(shift);
        if(mShowIncomePref && !TextUtils.isEmpty(payText)) {
            holder.pay.setText(payText);
            holder.pay.setVisibility(View.VISIBLE);
        } else {
            holder.pay.setText(null);
            holder.pay.setVisibility(View.GONE);
        }

        if(TextUtils.isEmpty(shift.note)) {
            holder.note.setVisibility(View.GONE);
        } else {
            holder.note.setText(shift.note);
            holder.note.setVisibility(View.VISIBLE);
        }

        holder.shift = shift;
        holder.julianDay = shift.julianDay;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = inflater.inflate(R.layout.list_item_shift, null);
        new ViewHolder(v);
        return v;
    }

    @Override
    public Cursor swapCursor(Cursor cursor) {
        clearCaches();
        return super.swapCursor(cursor);
    }

    private void clearCaches() {
        mIdToTimeArray.clear();
        mIdToPayArray.clear();
    }

    public void setJulianDay(int julianDay) {
        mJulianDay = julianDay;
    }

    public CursorLoader getDayLoader(Context context) {
        final String sel = DbField.JULIAN_DAY + " = ?";
        final String[] args = { String.valueOf(mJulianDay) };

        final String sort = DbField.START_TIME + " ASC, " + DbField.NAME + " ASC";

        return new CursorLoader(context, Provider.SHIFTS_URI, DbTable.SHIFTS.getFieldNames(), sel, args, sort);
    }

    private String getTimeText(Shift shift) {
        String time = mIdToTimeArray.get( (int) shift.id);
        if(!TextUtils.isEmpty(time))
            return time;

        int flags = DateUtils.FORMAT_SHOW_TIME;
        if(mIs24Hour)
            flags |= DateUtils.FORMAT_24HOUR;

        mStringBuilder.setLength(0);
        time = DateUtils.formatDateRange(mContext, mFormatter, shift.getStartTime(), shift.getEndTime(), flags).toString();

        time += " (" + UIUtils.getDurationAsHours(shift.getDurationInMinutes()) + ")";

        mIdToTimeArray.put( (int) shift.id, time);
        return time;
    }

    private String getPayText(Shift shift) {
        if(shift.payRate <= 0.01)
            return null;

        String pay = mIdToPayArray.get( (int) shift.id);
        if(!TextUtils.isEmpty(pay))
            return pay;

        pay = mCurrencyFormatter.format(shift.getIncome());

        mIdToPayArray.put( (int) shift.id, pay);
        return pay;
    }

    public static class ViewHolder {
        TextView name;
        TextView time;
        TextView note;
        TextView pay;

        public Shift shift;
        public int julianDay;

        public ViewHolder(View view) {
            if(view == null)
                return;

            name = (TextView) view.findViewById(R.id.name);
            time = (TextView) view.findViewById(R.id.time);
            note = (TextView) view.findViewById(R.id.note);
            pay = (TextView) view.findViewById(R.id.pay);

            julianDay = -1;

            view.setTag(this);
        }
    }
}
