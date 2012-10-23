package com.dgsd.android.ShiftTracker.Fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockFragment;
import com.dgsd.android.ShiftTracker.Adapter.WeekAdapter;
import com.dgsd.android.ShiftTracker.R;
import com.emilsjolander.components.StickyListHeaders.StickyListHeadersListView;

public class WeekFragment extends SherlockFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String KEY_JD = "_julian_day";

    private static final int LOADER_ID_SHIFTS = 0x01;

    private StickyListHeadersListView mList;
    private WeekAdapter mAdapter;

    private int mStartJulianDay = -1;

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

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID_SHIFTS, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        switch(id) {
            case LOADER_ID_SHIFTS: {
                return mAdapter.getLoaderForWeekStarting(getActivity());
            }
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }
}
