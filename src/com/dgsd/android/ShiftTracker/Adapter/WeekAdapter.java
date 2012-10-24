package com.dgsd.android.ShiftTracker.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.support.v4.content.CursorLoader;
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
import com.dgsd.android.ShiftTracker.R;
import com.emilsjolander.components.StickyListHeaders.StickyListHeadersCursorAdapter;

import java.util.Formatter;
import java.util.Random;

public class WeekAdapter extends StickyListHeadersCursorAdapter {
    public static final String NEW_ROW_KEY = WeekAdapter.class.getName() + "_NEW_ROW_KEY";

    private LayoutInflater inflater;
    private int mStartingJulianDay;
    private Random mRand;
    private Time mTime;

    private SparseArray<String> mJdToTitleArray;
    private SparseArray<String> mIdToTimeArray;

    private boolean mIs24Hour;

    private Formatter mFormatter;
    private StringBuilder mStringBuilder;

    public WeekAdapter(Context context, Cursor c, int julianDay) {
        super(context, c, false);
        inflater = LayoutInflater.from(context);
        mStartingJulianDay = julianDay;
        mRand = new Random();
        mTime = new Time();

        mIs24Hour = DateFormat.is24HourFormat(context);

        mStringBuilder = new StringBuilder();
        mFormatter = new Formatter((mStringBuilder));

        //Caching
        mJdToTitleArray = new SparseArray<String>();
        mIdToTimeArray = new SparseArray<String>();
    }

    @Override
    protected View newHeaderView(Context context, Cursor cursor) {
        View v = inflater.inflate(R.layout.title_with_underline, null);
        new HeaderViewHolder(v);
        return v;
    }

    @Override
    protected void bindHeaderView(View view, Context context, Cursor cursor) {
        HeaderViewHolder holder = (HeaderViewHolder) view.getTag();

        final int jd = cursor.getInt(cursor.getColumnIndex(DbField.JULIAN_DAY.name));
        String title = mJdToTitleArray.get(jd, null);
        if(TextUtils.isEmpty(title)) {

            mTime.setJulianDay(jd);
            title = DateUtils.formatDateTime(getContext(), mTime.toMillis(true),
                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY |
                            DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_NO_YEAR);

            mJdToTitleArray.put(jd, title);
        }

        holder.title.setText(title);
    }

    @Override
    protected long getHeaderId(Context context, Cursor cursor) {
        return cursor.getLong(cursor.getColumnIndex(DbField.JULIAN_DAY.name));
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        final String name = cursor.getString(cursor.getColumnIndex(DbField.NAME.name));

        if(TextUtils.equals(NEW_ROW_KEY, name)) {
            holder.noShiftWrapper.setVisibility(View.VISIBLE);
            holder.shiftWrapper.setVisibility(View.GONE);

            return;
        }

        holder.noShiftWrapper.setVisibility(View.GONE);
        holder.shiftWrapper.setVisibility(View.VISIBLE);

        holder.name.setText(name);
        holder.time.setText(getTimeText(cursor));
        holder.pay.setText("$15");
        holder.note.setText("This is a note!");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = inflater.inflate(R.layout.list_item_shift_wrapper, null);
        new ViewHolder(v);
        return v;
    }

    @Override
    public Cursor swapCursor(Cursor cursor) {
        clearCaches();

        if(cursor == null)
            return super.swapCursor(null);

        MatrixCursor mc = new MatrixCursor(DbTable.SHIFTS.getFieldNames(), cursor.getCount() + 7);

        int jd = mStartingJulianDay;
        final int colCount = cursor.getColumnCount();

        SparseArray<Object[]> jdToRowArray = new SparseArray<Object[]>();
        if(cursor.moveToFirst()) {
            final int jdIndex = cursor.getColumnIndex(DbField.JULIAN_DAY.name);
            do {
                Object[] row = new Object[colCount];
                for(int i = 0; i < colCount; i++)
                    row[i] = cursor.getString(i);

                jdToRowArray.put(cursor.getInt(jdIndex), row);
            } while(cursor.moveToNext());
        }

        for (int i = jd; i < jd + 7; i++) {
            Object[] row = jdToRowArray.get(i);
            if (row != null)
                mc.addRow(row);

            //Add a 'Add Shift' row
            row = new Object[colCount];
            row[0] = mRand.nextInt(Integer.MAX_VALUE);  // DbField.ID
            row[1] = i;                                 // DbField.JULIAN_DAY
            row[2] = -1;                                // DbField.START_TIME
            row[3] = -1;                                // DbField.END_TIME
            row[4] = -1;                                // DbField.PAY_RATE
            row[5] = NEW_ROW_KEY;                       // DbField.NAME
            row[6] = -1;                                // DbField.BREAK_DURATION

            mc.addRow(row);
        }

        return super.swapCursor(mc);
    }

    private void clearCaches() {
        mJdToTitleArray.clear();
        mIdToTimeArray.clear();
    }

    public CursorLoader getLoaderForWeekStarting(Context context) {
        final String sel = DbField.JULIAN_DAY + " >= ? AND " + DbField.JULIAN_DAY + " < ?";
        final String[] args = new String[] {
                String.valueOf(mStartingJulianDay),
                String.valueOf(mStartingJulianDay + 6)
        };

        final String sort = DbField.JULIAN_DAY + " ASC," + DbField.START_TIME + " ASC";

        return new CursorLoader(context, Provider.SHIFTS_URI, null, sel, args, sort);
    }

    private String getTimeText(Cursor cursor) {
        final int id = cursor.getInt(0);
        String time = mIdToTimeArray.get(id);
        if(!TextUtils.isEmpty(time))
            return time;

        int flags = DateUtils.FORMAT_SHOW_TIME;
        if(mIs24Hour)
            flags |= DateUtils.FORMAT_24HOUR;

        final long t1 = cursor.getLong(cursor.getColumnIndex(DbField.START_TIME.name));
        final long t2 = cursor.getLong(cursor.getColumnIndex(DbField.END_TIME.name));

        mStringBuilder.setLength(0);
        time = DateUtils.formatDateRange(getContext(), mFormatter, t1, t2, flags).toString();

        mIdToTimeArray.put(id, time);
        return time;
    }

    private static class HeaderViewHolder {
        TextView title;

        public HeaderViewHolder(View view) {
            if(view == null)
                return;

            title = (TextView) view.findViewById(R.id.text);

            view.setTag(this);
        }
    }

    private static class ViewHolder {
        View noShiftWrapper;
        View shiftWrapper;

        TextView name;
        TextView time;
        TextView note;
        TextView pay;

        public ViewHolder(View view) {
            if(view == null)
                return;

            noShiftWrapper = view.findViewById(R.id.no_shift);
            shiftWrapper = view.findViewById(R.id.new_shift);

            name = (TextView) view.findViewById(R.id.name);
            time = (TextView) view.findViewById(R.id.time);
            note = (TextView) view.findViewById(R.id.note);
            pay = (TextView) view.findViewById(R.id.pay);

            view.setTag(this);
        }
    }
}
