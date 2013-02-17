package com.dgsd.android.ShiftTracker.Fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.Time;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import com.dgsd.android.ShiftTracker.Adapter.DayAdapter;
import com.dgsd.android.ShiftTracker.Data.DbField;
import com.dgsd.android.ShiftTracker.Data.Provider;
import com.dgsd.android.ShiftTracker.EditShiftActivity;
import com.dgsd.android.ShiftTracker.Model.Shift;
import com.dgsd.android.ShiftTracker.R;
import com.dgsd.android.ShiftTracker.Service.DbService;
import com.dgsd.android.ShiftTracker.Util.AlarmUtils;
import com.dgsd.android.ShiftTracker.Util.Anim;
import com.dgsd.android.ShiftTracker.Util.Api;
import com.dgsd.android.ShiftTracker.Util.TimeUtils;
import com.squareup.timessquare.CalendarPickerView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import java.util.Calendar;
import java.util.Date;

/**
 * Created: 16/02/13 10:36 PM
 */
public class MonthFragment extends SherlockFragment implements LoaderManager.LoaderCallbacks<Cursor>,CalendarPickerView.OnDateSelectedListener, AdapterView.OnItemClickListener {
    public static final String TAG = MonthFragment.class.getSimpleName();

    private static final String KEY_MONTH = "_month";
    private static final String KEY_YEAR = "_year";

    private static final int LOADER_ID_DAYS = 0;
    private static final int LOADER_ID_TEMPLATES = 1;

    private int mMonth;
    private int mYear;

    private CalendarPickerView mCalendar;

    private TextView mAddShiftBtn;

    private ListView mEventList;

    private DayAdapter mAdapter;

    private TemplateListFragment mTemplateList;

    private boolean mHasTemplates = false;

    public static MonthFragment newInstance(int month, int year) {
        MonthFragment frag = new MonthFragment();

        Bundle args = new Bundle();
        args.putInt(KEY_MONTH, month);
        args.putInt(KEY_YEAR, year);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if(args != null) {
            mMonth = args.getInt(KEY_MONTH);
            mYear = args.getInt(KEY_YEAR);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_month, container, false);

        mCalendar = (CalendarPickerView) v.findViewById(R.id.calendar);

        Calendar min = Calendar.getInstance();
        min.set(mYear, mMonth, 1);

        Calendar selected = Calendar.getInstance();
        selected.set(mYear, mMonth, 1);

        Calendar max = Calendar.getInstance();
        max.set(mYear, mMonth, 1);
        max.add(Calendar.MONTH, 1);

        mCalendar.init(selected.getTime(), min.getTime(), max.getTime());
        mCalendar.setOnDateSelectedListener(this);

        mAddShiftBtn = (TextView) LayoutInflater.from(getActivity())
                .inflate(R.layout.month_view_add_shift, mEventList, false);

        mAdapter = new DayAdapter(getActivity(), null, getSelectedJulianDay());
        mEventList = (ListView) v.findViewById(R.id.list);
        mEventList.setLayoutAnimation(Anim.getListViewDealAnimator());
        mEventList.setOnItemClickListener(this);
        mEventList.addHeaderView(mAddShiftBtn);
        mEventList.setHeaderDividersEnabled(true);
        mEventList.setAdapter(mAdapter);


        registerForContextMenu(mEventList);

        return v;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID_DAYS, null, this);
        getLoaderManager().initLoader(LOADER_ID_TEMPLATES, null, this);
    }

    public int getSelectedJulianDay() {
        return TimeUtils.getJulianDay(mCalendar.getSelectedDate().getTime());
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v == mEventList)  {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

            if(info.targetView == mAddShiftBtn)
                getActivity().getMenuInflater().inflate(R.menu.week_list_item_popup, menu);
            else
                getActivity().getMenuInflater().inflate(R.menu.week_list_item_context_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        final int jd = getSelectedJulianDay();
        if(item.getItemId() == R.id.template_shift) {
            showTemplateChooser(jd);
        } else if(item.getItemId() == R.id.new_shift) {
            final Intent intent = new Intent(getActivity(), EditShiftActivity.class);
            intent.putExtra(EditShiftActivity.EXTRA_JULIAN_DAY, jd);
            startActivity(intent);
        } else if(item.getItemId() == R.id.delete) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
            final DayAdapter.ViewHolder holder = (DayAdapter.ViewHolder) info.targetView.getTag();

            if(holder != null && holder.shift != null) {
                if(holder.shift.isTemplate) {
                    /**
                     * We dont want to delete templates, just remove them from view. Do this by giving them bogus dates
                     */
                    final ContentValues values = new ContentValues();
                    values.put(DbField.JULIAN_DAY.name, -1);
                    values.put(DbField.END_JULIAN_DAY.name, -1);

                    DbService.async_update(getActivity(), Provider.SHIFTS_URI,
                            DbField.ID + "=" + (holder.shift == null ? -1 : holder.shift.id),
                            values);
                } else {
                    DbService.async_delete(getActivity(), Provider.SHIFTS_URI,
                            DbField.ID + "=" + (holder.shift == null ? -1 : holder.shift.id));
                }

                AlarmUtils.get(getActivity()).cancel(holder.shift);
                showMessage("Shift deleted");
            }
        }

        return true;
    }

    public void selectJulianDay(int jd) {
        Time t = new Time();
        t.setJulianDay(jd);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(t.toMillis(true));

        cal.set(Calendar.DAY_OF_MONTH, 1);

        onDateSelected(cal.getTime());
    }

    private void showTemplateChooser(final int julianDay) {
        if(mTemplateList != null && mTemplateList.isResumed())
            return; //Already showing

        mTemplateList = TemplateListFragment.newInstance();
        mTemplateList.setDismissOnItemClick(true);
        mTemplateList.setOnItemClickListener(new TemplateListFragment.OnTemplateClickListener() {
            @Override
            public void onTemplateClicked(Shift shift) {
                shift.id = -1;
                shift.isTemplate = false;
                shift.endJulianDay = julianDay + (shift.endJulianDay - shift.julianDay);
                shift.julianDay = julianDay;

                DbService.async_insert(getActivity(), Provider.SHIFTS_URI, shift.toContentValues());
                showMessage("New shift created");

                mAdapter.notifyDataSetChanged();
            }
        });
        mTemplateList.show(getSherlockActivity().getSupportFragmentManager(), null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        if(id == LOADER_ID_DAYS)
            return mAdapter.getDayLoader(getActivity());
        else if(id == LOADER_ID_TEMPLATES)
            return new CursorLoader(getActivity(), Provider.SHIFTS_URI, null,
                    DbField.IS_TEMPLATE + "> 0", null, null);
        else
            return null;

    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor) {
        if(loader.getId() == LOADER_ID_DAYS)
            mAdapter.swapCursor(cursor);
        else if(loader.getId() == LOADER_ID_TEMPLATES)
            mHasTemplates = cursor != null && cursor.getCount() > 0;
    }

    @Override
    public void onLoaderReset(final Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onDateSelected(final Date date) {
        mAdapter.setJulianDay(TimeUtils.getJulianDay(date.getTime()));

        if(isResumed()) {
            Loader l = getLoaderManager().restartLoader(LOADER_ID_DAYS, null, this);
            if(l != null)
                l.forceLoad();
        }
    }

    private void showMessage(String msg) {
        if(getActivity() != null)
            Crouton.showText(getActivity(), msg, Style.CONFIRM);
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        DayAdapter.ViewHolder holder = (DayAdapter.ViewHolder) view.getTag();
        if(holder != null && holder.shift != null) {
            final Intent intent = new Intent(getActivity(), EditShiftActivity.class);
            intent.putExtra(EditShiftActivity.EXTRA_SHIFT, holder.shift);
            startActivity(intent);
        } else {
            //Clicked on the 'Add Shift' button
            final int jd = getSelectedJulianDay();

            if(mHasTemplates) {
                if(Api.isMin(Api.HONEYCOMB)) {
                    final PopupMenu popup = new PopupMenu(getActivity(), view);
                    popup.getMenuInflater().inflate(R.menu.week_list_item_popup, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(item.getItemId() == R.id.template_shift) {
                                showTemplateChooser(jd);
                            } else if(item.getItemId() == R.id.new_shift) {
                                final Intent intent = new Intent(getActivity(), EditShiftActivity.class);
                                intent.putExtra(EditShiftActivity.EXTRA_JULIAN_DAY, jd);
                                startActivity(intent);
                            }
                            return false;
                        }
                    });

                    popup.show();
                } else {
                    mEventList.showContextMenuForChild(view);
                }
            } else {
                final Intent intent = new Intent(getActivity(), EditShiftActivity.class);
                intent.putExtra(EditShiftActivity.EXTRA_JULIAN_DAY, jd);
                startActivity(intent);
            }
        }

    }
}
