package com.dgsd.android.ShiftTracker.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import com.dgsd.android.ShiftTracker.Adapter.WeekAdapter;
import com.dgsd.android.ShiftTracker.EditShiftActivity;
import com.dgsd.android.ShiftTracker.Model.Shift;
import com.dgsd.android.ShiftTracker.R;
import com.dgsd.android.ShiftTracker.Util.Prefs;
import com.dgsd.android.ShiftTracker.Util.UIUtils;
import com.emilsjolander.components.StickyListHeaders.StickyListHeadersListView;

import java.text.NumberFormat;

public class WeekFragment extends SherlockFragment implements LoaderManager.LoaderCallbacks<Cursor>,AdapterView.OnItemClickListener {
    private static final String KEY_JD = "_julian_day";
    private static final String BLANK_TOTAL_TEXT = "0 Hrs";

    private static final int LOADER_ID_SHIFTS = 0x01;
    private static final int LOADER_ID_TOTAL = 0x02;

    private StickyListHeadersListView mList;
    private TextView mTotalText;
    private WeekAdapter mAdapter;
    private ViewGroup mStatsWrapper;

    private int mStartJulianDay = -1;

    private boolean mShowHoursPref = true;
    private boolean mShowIncomePref = true;

    public static WeekFragment newInstance(int startJulianDay) {
        WeekFragment frag = new WeekFragment();

        Bundle args = new Bundle();
        args.putInt(KEY_JD, startJulianDay);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            mStartJulianDay = getArguments().getInt(KEY_JD, mStartJulianDay);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_week, container, false);

        mAdapter = new WeekAdapter(getActivity(), null, mStartJulianDay);

        mList = (StickyListHeadersListView) v.findViewById(R.id.list);
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(this);

        mTotalText = (TextView) v.findViewById(R.id.total_text);

        mStatsWrapper = (ViewGroup) v.findViewById(R.id.stats_wrapper);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID_SHIFTS, null, this);
        getLoaderManager().initLoader(LOADER_ID_TOTAL, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();

        Prefs p = Prefs.getInstance(getActivity());
        mShowHoursPref = p.get(getString(R.string.settings_key_show_total_hours), true);
        mShowIncomePref = p.get(getString(R.string.settings_key_show_income), true);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        switch(id) {
            case LOADER_ID_SHIFTS: {
                return mAdapter.getWeeklyLoader(getActivity());
            }
            case LOADER_ID_TOTAL: {
                return mAdapter.getWeeklyLoader(getActivity());
            }
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch(loader.getId()) {
            case LOADER_ID_SHIFTS:
                mAdapter.swapCursor(cursor);
                break;
            case LOADER_ID_TOTAL:
                if((mShowHoursPref || mShowIncomePref) && cursor != null && cursor.moveToFirst()) {
                    float pay = 0.0f;
                    long mins = 0;
                    do {
                        Shift shift = Shift.fromCursor(cursor);

                        pay += shift.getIncome();
                        mins += shift.getDurationInMinutes();
                    } while(cursor.moveToNext());

                    String payText = mShowIncomePref && pay > 0 ? NumberFormat.getCurrencyInstance().format(pay) : null;
                    String hoursText = mShowHoursPref ? UIUtils.getDurationAsHours(mins) + " Hrs" : null;

                    if(TextUtils.isEmpty(payText)) {
                        if(TextUtils.isEmpty(hoursText)) {
                            mStatsWrapper.setVisibility(View.GONE);
                        } else {
                            mStatsWrapper.setVisibility(View.VISIBLE);
                            mTotalText.setText(hoursText);
                        }
                    } else {
                        mStatsWrapper.setVisibility(View.VISIBLE);
                        if(TextUtils.isEmpty(hoursText)) {
                            mTotalText.setText(payText);
                        } else {
                            mTotalText.setText(payText + " / " + hoursText);
                        }
                    }
                } else {
                    mTotalText.setText(BLANK_TOTAL_TEXT);
                    if(!mShowHoursPref) {
                        mStatsWrapper.setVisibility(View.GONE);
                    } else {
                        mStatsWrapper.setVisibility(View.VISIBLE);
                    }
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
        mStatsWrapper.setVisibility(View.GONE);
        mTotalText.setText(BLANK_TOTAL_TEXT);
    }

    @Override
    public void onItemClick(AdapterView<?> list, View view, int pos, long id) {
        final Intent intent = new Intent(getActivity(), EditShiftActivity.class);

        WeekAdapter.ViewHolder holder = (WeekAdapter.ViewHolder) view.getTag();
        if(holder != null) {
            intent.putExtra(EditShiftActivity.EXTRA_JULIAN_DAY, holder.julianDay);
            if(holder.shift != null)
                intent.putExtra(EditShiftActivity.EXTRA_SHIFT, holder.shift);
        }

        startActivity(intent);
    }
}
