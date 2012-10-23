package com.dgsd.android.ShiftTracker.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;
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

import java.util.Random;

public class WeekAdapter extends StickyListHeadersCursorAdapter {
    public static final String NEW_ROW_KEY = WeekAdapter.class.getName() + "_NEW_ROW_KEY";

    private LayoutInflater inflater;
    private int mStartingJulianDay;
    private Random mRand;
    private Time mTime;

    private SparseArray<String> mJdToTitleArray;

    public WeekAdapter(Context context, Cursor c, int julianDay) {
        super(context, c, false);
        inflater = LayoutInflater.from(context);
        mStartingJulianDay = julianDay;
        mRand = new Random();
        mTime = new Time();

        //Caching
        mJdToTitleArray = new SparseArray<String>();
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
        ((TextView)view).setText(cursor.getString(cursor.getColumnIndex(DbField.NAME.name)));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
//        ViewHolder holder = new ViewHolder();
//        View v = inflater.inflate(R.layout.test_list_item_layout, null);
//        holder.text = (TextView) v.findViewById(R.id.text);
//        v.setTag(holder);
        TextView v = new TextView(context);
        v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 140));
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

        if(cursor.moveToFirst()) {
            SparseArray<Object[]> jdToRowArray = new SparseArray<Object[]>();
            final int jdIndex = cursor.getColumnIndex(DbField.JULIAN_DAY.name);
            do {
                Object[] row = new Object[colCount];
                for(int i = 0; i < colCount; i++)
                    row[i] = cursor.getString(i);

                jdToRowArray.put(cursor.getInt(jdIndex), row);
            } while(cursor.moveToNext());

            for(int i = jd; i < jd + 7; i++) {
                Object[] row = jdToRowArray.get(i);
                if(row == null) {
                    //Nothing for this jd, insert black row!
                    row = new Object[colCount];
                    row[0] = mRand.nextInt(Integer.MAX_VALUE); //DbField.ID
                    row[1] = i; // DbField.JULIAN_DAY
                    row[2] = -1; // DbField.START_TIME
                    row[3] = -1; // DbField.END_TIME
                    row[4] = -1; // DbField.PAY_RATE
                    row[5] = NEW_ROW_KEY; //DbField.NAME
                    row[6] = -1; //DbField.BREAK_DURATION
                }

                mc.addRow(row);
            }
        } else {
            //No shifts at all .. add defaults for all!
            for(int i = 0; i < 7; i++) {
                Object[] row = new Object[colCount];
                row[0] = mRand.nextInt(Integer.MAX_VALUE); //DbField.ID
                row[1] = jd++; // DbField.JULIAN_DAY
                row[2] = -1; // DbField.START_TIME
                row[3] = -1; // DbField.END_TIME
                row[4] = -1; // DbField.PAY_RATE
                row[5] = NEW_ROW_KEY; //DbField.NAME
                row[6] = -1; //DbField.BREAK_DURATION

                mc.addRow(row);
            }
        }

        return super.swapCursor(mc);
    }

    private void clearCaches() {
        mJdToTitleArray.clear();
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

    private static class HeaderViewHolder {
        TextView title;

        public HeaderViewHolder(View view) {
            if(view == null)
                return;

            title = (TextView) view.findViewById(R.id.text);
            view.setTag(this);
        }
    }
}
