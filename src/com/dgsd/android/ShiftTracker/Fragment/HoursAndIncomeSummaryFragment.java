package com.dgsd.android.ShiftTracker.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.*;
import android.widget.AdapterView;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.dgsd.android.ShiftTracker.Adapter.WeekAdapter;
import com.dgsd.android.ShiftTracker.Data.DbField;
import com.dgsd.android.ShiftTracker.Data.Provider;
import com.dgsd.android.ShiftTracker.EditShiftActivity;
import com.dgsd.android.ShiftTracker.Model.Shift;
import com.dgsd.android.ShiftTracker.R;
import com.dgsd.android.ShiftTracker.Service.DbService;
import com.dgsd.android.ShiftTracker.Util.*;
import com.emilsjolander.components.StickyListHeaders.StickyListHeadersListView;
import de.neofonie.mobile.app.android.widget.crouton.Crouton;
import de.neofonie.mobile.app.android.widget.crouton.Style;

import java.text.NumberFormat;

public class HoursAndIncomeSummaryFragment extends SherlockDialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String KEY_JD = "_julian_day";

    private static final int LOADER_ID_MONTH= 0x01;
    private static final int LOADER_ID_3_MONTH = 0x02;
    private static final int LOADER_ID_6_MONTH = 0x03;
    private static final int LOADER_ID_9_MONTH = 0x04;
    private static final int LOADER_ID_YEAR = 0x05;

    private int mJulianDay = -1;

    private TextView mMonth;
    private TextView mThreeMonth;
    private TextView mSixMonth;
    private TextView mNineMonth;
    private TextView mThisYear;

    public static HoursAndIncomeSummaryFragment newInstance(int julianDay) {
        HoursAndIncomeSummaryFragment frag = new HoursAndIncomeSummaryFragment();

        Bundle args = new Bundle();
        args.putInt(KEY_JD, julianDay);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            mJulianDay = getArguments().getInt(KEY_JD, mJulianDay);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hour_and_income_summary, container, false);

        mMonth = (TextView) v.findViewById(R.id.month);
        mThreeMonth = (TextView) v.findViewById(R.id.three_months);
        mSixMonth = (TextView) v.findViewById(R.id.six_months);
        mNineMonth = (TextView) v.findViewById(R.id.nine_months);
        mThisYear = (TextView) v.findViewById(R.id.year);

        Time time = new Time();
        time.setJulianDay(mJulianDay);

        getDialog().setTitle(DateFormat.getDateFormat(getActivity()).format(time.toMillis(true)));
        getDialog().setCanceledOnTouchOutside(true);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(LOADER_ID_MONTH, null, this);
        getLoaderManager().initLoader(LOADER_ID_3_MONTH, null, this);
        getLoaderManager().initLoader(LOADER_ID_6_MONTH, null, this);
        getLoaderManager().initLoader(LOADER_ID_9_MONTH, null, this);
        getLoaderManager().initLoader(LOADER_ID_YEAR, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        Time time = new Time();
        time.setJulianDay(mJulianDay);

        switch(id) {
            case LOADER_ID_MONTH: {
                time.month--;
                break;
            }
            case LOADER_ID_3_MONTH: {
                time.month -= 3;
                break;
            }
            case LOADER_ID_6_MONTH: {
                time.month -= 6;
                break;
            }
            case LOADER_ID_9_MONTH: {
                time.month -= 9;
                break;
            }
            case LOADER_ID_YEAR:
                time.year--;
                break;
        }

        time.normalize(true);
        return getLoaderBetween(TimeUtils.getJulianDay(time), mJulianDay);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        PayAndDuration pad = PayAndDuration.from(cursor);

        TextView view = null;
        switch(loader.getId()) {
            case LOADER_ID_MONTH:
                view = mMonth;
                break;
            case LOADER_ID_3_MONTH:
                view = mThreeMonth;
                break;
            case LOADER_ID_6_MONTH:
                view = mSixMonth;
                break;
            case LOADER_ID_9_MONTH:
                view = mNineMonth;
                break;
            case LOADER_ID_YEAR:
                view = mThisYear;
                break;
        }

        if(view == null)
            return;

        String payText = NumberFormat.getCurrencyInstance().format(pad.pay);
        String hoursText = UIUtils.getDurationAsHours(pad.mins);

        view.setText(payText + "\n" + hoursText);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private CursorLoader getLoaderBetween(int fromJd, int toJd) {
        final String sel = DbField.JULIAN_DAY + " >= ? AND " + DbField.JULIAN_DAY + " < ?";
        final String[] args = new String[] {
            String.valueOf(fromJd),
            String.valueOf(toJd)
        };

        return new CursorLoader(getActivity(), Provider.SHIFTS_URI, null, sel, args, null);
    }

    static class PayAndDuration {
        public float pay;
        public long mins;

        private PayAndDuration() {
            pay = 0.0f;
            mins = 0;
        }

        static PayAndDuration from(Cursor cursor) {
            PayAndDuration pad = new PayAndDuration();
            if(cursor != null && cursor.moveToFirst()) {
                do {
                    Shift shift = Shift.fromCursor(cursor);

                    pad.pay += shift.getIncome();
                    pad.mins += shift.getDurationInMinutes();
                } while(cursor.moveToNext());
            }

            return pad;
        }
    }
}
