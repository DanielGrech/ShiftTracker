package com.dgsd.android.ShiftTracker.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Typeface;
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
import com.dgsd.android.ShiftTracker.Model.Shift;
import com.dgsd.android.ShiftTracker.R;
import com.dgsd.android.ShiftTracker.Util.Prefs;
import com.dgsd.android.ShiftTracker.Util.TimeUtils;
import com.dgsd.android.ShiftTracker.Util.UIUtils;
import com.emilsjolander.components.StickyListHeaders.StickyListHeadersCursorAdapter;

import java.text.NumberFormat;
import java.util.*;

public class WeekAdapter extends StickyListHeadersCursorAdapter {
    public static final String NEW_ROW_KEY = WeekAdapter.class.getName() + "_NEW_ROW_KEY";

    private LayoutInflater inflater;
    private int mStartingJulianDay;
    private Random mRand;
    private Time mTime;

    private SparseArray<String> mJdToTitleArray;
    private SparseArray<String> mIdToTimeArray;
    private SparseArray<String> mIdToPayArray;

    private boolean mIs24Hour;
    private boolean mShowIncomePref;

    private Formatter mFormatter;
    private StringBuilder mStringBuilder;

    private NumberFormat mCurrencyFormatter;
    private int mCurrentJulianDay;

    public WeekAdapter(Context context, Cursor c, int julianDay) {
        super(context, c, false);
        inflater = LayoutInflater.from(context);
        mStartingJulianDay = julianDay;
        mRand = new Random();
        mTime = new Time();

        mIs24Hour = DateFormat.is24HourFormat(context);

        mStringBuilder = new StringBuilder();
        mFormatter = new Formatter((mStringBuilder));

        mCurrencyFormatter = NumberFormat.getCurrencyInstance();

        //Caching
        mJdToTitleArray = new SparseArray<String>();
        mIdToTimeArray = new SparseArray<String>();
        mIdToPayArray = new SparseArray<String>();

        mCurrentJulianDay = TimeUtils.getCurrentJulianDay();

        mShowIncomePref = Prefs.getInstance(context).get(context.getString(R.string.settings_key_show_income), true);
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
                            DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_NO_YEAR);

            mJdToTitleArray.put(jd, title);
        }

        //Highlight the current day with a bold title
        if(jd == mCurrentJulianDay)
            holder.title.setTypeface(null, Typeface.BOLD);
        else
            holder.title.setTypeface(null, Typeface.NORMAL);

        holder.title.setText(title);
    }

    @Override
    protected long getHeaderId(Context context, Cursor cursor) {
        return cursor.getLong(cursor.getColumnIndex(DbField.JULIAN_DAY.name));
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        final Shift shift = Shift.fromCursor(cursor);
        if(TextUtils.equals(NEW_ROW_KEY, shift.name)) {
            holder.noShiftWrapper.setVisibility(View.VISIBLE);
            holder.shiftWrapper.setVisibility(View.GONE);

            holder.shift = null;
            holder.julianDay = shift.julianDay;

            return;
        }

        holder.noShiftWrapper.setVisibility(View.GONE);
        holder.shiftWrapper.setVisibility(View.VISIBLE);

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

        SparseArray<List<Object[]>> jdToRowArray = new SparseArray<List<Object[]>>();

        if(cursor.moveToFirst()) {
            final int jdIndex = cursor.getColumnIndex(DbField.JULIAN_DAY.name);
            do {
                Object[] row = new Object[colCount];
                for(int i = 0; i < colCount; i++)
                    row[i] = cursor.getString(i);

                final int shiftDay = cursor.getInt(jdIndex);

                List<Object[]> rowsOnSameJd = jdToRowArray.get(shiftDay, null);
                if(rowsOnSameJd == null)
                    rowsOnSameJd = new ArrayList<Object[]>();

                rowsOnSameJd.add(row);

                jdToRowArray.put(shiftDay, rowsOnSameJd);
            } while(cursor.moveToNext());
        }

        for (int i = jd; i < jd + 7; i++) {
            List<Object[]> rows = jdToRowArray.get(i);
            if (rows != null)
                for(Object[] row : rows)
                    mc.addRow(row);

            //Add a 'Add Shift' row
            Object[] row = new Object[colCount];
            row[0] = mRand.nextInt(Integer.MAX_VALUE);  // DbField.ID
            row[1] = i;                                 // DbField.JULIAN_DAY
            row[2] = -1;                                // DbField.START_TIME
            row[3] = -1;                                // DbField.END_TIME
            row[4] = -1;                                // DbField.PAY_RATE
            row[5] = NEW_ROW_KEY;                       // DbField.NAME
            row[6] = null;                              // DbField.NOTE
            row[7] = -1;                                // DbField.BREAK_DURATION
            row[8] = 0;                                 // DbField.IS_TEMPLATE

            mc.addRow(row);
        }

        return super.swapCursor(mc);
    }

    private void clearCaches() {
        mJdToTitleArray.clear();
        mIdToTimeArray.clear();
        mIdToPayArray.clear();
    }

    public CursorLoader getWeeklyLoader(Context context) {
        final String sel = DbField.JULIAN_DAY + " >= ? AND " + DbField.JULIAN_DAY + " < ?";
        final String[] args = new String[] {
                String.valueOf(mStartingJulianDay),
                String.valueOf(mStartingJulianDay + 7)
        };

        final String sort = DbField.JULIAN_DAY + " ASC," + DbField.START_TIME + " ASC, " + DbField.NAME + " ASC";

        return new CursorLoader(context, Provider.SHIFTS_URI, null, sel, args, sort);
    }

    private String getTimeText(Shift shift) {
        String time = mIdToTimeArray.get( (int) shift.id);
        if(!TextUtils.isEmpty(time))
            return time;

        int flags = DateUtils.FORMAT_SHOW_TIME;
        if(mIs24Hour)
            flags |= DateUtils.FORMAT_24HOUR;

        mStringBuilder.setLength(0);
        time = DateUtils.formatDateRange(getContext(), mFormatter, shift.startTime, shift.endTime, flags).toString();

        time += " (" + UIUtils.getDurationAsHours(shift.getDurationInMinutes()) + " Hrs)";

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

    private static class HeaderViewHolder {
        TextView title;

        public HeaderViewHolder(View view) {
            if(view == null)
                return;

            title = (TextView) view.findViewById(R.id.text);

            view.setTag(this);
        }
    }

    public static class ViewHolder {
        View noShiftWrapper;
        View shiftWrapper;

        TextView name;
        TextView time;
        TextView note;
        TextView pay;

        public Shift shift;
        public int julianDay;

        public ViewHolder(View view) {
            if(view == null)
                return;

            noShiftWrapper = view.findViewById(R.id.no_shift);
            shiftWrapper = view.findViewById(R.id.new_shift);

            name = (TextView) view.findViewById(R.id.name);
            time = (TextView) view.findViewById(R.id.time);
            note = (TextView) view.findViewById(R.id.note);
            pay = (TextView) view.findViewById(R.id.pay);

            julianDay = -1;

            view.setTag(this);
        }
    }
}
