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
import com.dgsd.android.MonthView.MonthView;
import com.dgsd.android.ShiftTracker.Adapter.DayAdapter;
import com.dgsd.android.ShiftTracker.Data.DbField;
import com.dgsd.android.ShiftTracker.Data.Provider;
import com.dgsd.android.ShiftTracker.EditShiftActivity;
import com.dgsd.android.ShiftTracker.Model.Shift;
import com.dgsd.android.ShiftTracker.R;
import com.dgsd.android.ShiftTracker.Service.DbService;
import com.dgsd.android.ShiftTracker.Util.*;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import java.util.*;

/**
 * Created: 16/02/13 10:36 PM
 */
public class MonthFragment extends SherlockFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener, MonthView.OnDateClickedListener {
    public static final String TAG = MonthFragment.class.getSimpleName();

    private static final String KEY_MONTH = "_month";
    private static final String KEY_YEAR = "_year";

    private static final int LOADER_ID_DAYS = 0;
    private static final int LOADER_ID_TEMPLATES = 1;
    private static final int LOADER_ID_DAYS_WITH_SHIFTS = 2;

    private int mMonth;
    private int mYear;

    private MonthView mMonthView;

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

        int startDay = Integer.valueOf(Prefs.getInstance(getActivity())
                .get(getActivity().getString(R.string.settings_key_start_day), "2"));

        switch(startDay) {
            case 0:
                startDay = Calendar.SUNDAY;
                break;
            case 1:
                startDay = Calendar.MONDAY;
                break;
            case 2:
                startDay = Calendar.TUESDAY;
                break;
            case 3:
                startDay = Calendar.WEDNESDAY;
                break;
            case 4:
                startDay = Calendar.THURSDAY;
                break;
            case 5:
                startDay = Calendar.FRIDAY;
                break;
            case 6:
                startDay = Calendar.SATURDAY;
                break;
        }

        mMonthView = (MonthView) v.findViewById(R.id.month);
        mMonthView.set(mYear, mMonth, startDay);
        mMonthView.setDateClickedListener(this);

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
        getLoaderManager().initLoader(LOADER_ID_DAYS_WITH_SHIFTS, null, this);
    }

    public int getSelectedJulianDay() {
        final Date d = mMonthView.getSelectedDate();
        if(d == null)
            return -1;
        else
            return TimeUtils.getJulianDay(d.getTime());
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v == mEventList)  {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

            if(info.targetView == mAddShiftBtn) {
                getActivity().getMenuInflater().inflate(R.menu.week_list_item_popup, menu);
                if(!mHasTemplates) {
                    MenuItem item = menu.findItem(R.id.template_shift);
                    item.setEnabled(false);
                    item.setVisible(false);
                }
            } else {
                getActivity().getMenuInflater().inflate(R.menu.week_list_item_context_menu, menu);
            }
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

        mMonthView.setSelected(cal.get(Calendar.DAY_OF_MONTH));
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
        else if(id == LOADER_ID_DAYS_WITH_SHIFTS) {
            Time t = new Time();
            t.set(1, mMonth, mYear);
            t.normalize(true);
            final int startJd = TimeUtils.getJulianDay(t);

            t.set(1, mMonth + 1, mYear);
            t.monthDay--;
            t.normalize(true);
            final int endJd = TimeUtils.getJulianDay(t);

            return new CursorLoader(getActivity(), Provider.DAYS_WITH_SHIFTS, null,
                    DbField.JULIAN_DAY + ">= " + startJd +
                            " AND " + DbField.JULIAN_DAY + " <= " + endJd,
                    null, null);
        } else
            return null;

    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor) {
        if(loader.getId() == LOADER_ID_DAYS)
            mAdapter.swapCursor(cursor);
        else if(loader.getId() == LOADER_ID_TEMPLATES)
            mHasTemplates = cursor != null && cursor.getCount() > 0;
        else if(loader.getId() == LOADER_ID_DAYS_WITH_SHIFTS) {
            if(cursor != null && cursor.moveToFirst()) {
                Set<Integer> daysOfMonth = new HashSet<Integer>();
                do {
                    daysOfMonth.add(TimeUtils.getMonthDay(cursor.getInt(0)));
                } while(cursor.moveToNext());

                for(int i = 1; i <= 31; i++) {
                    mMonthView.mark(i, daysOfMonth.contains(i));
                }
            }
        }

    }

    @Override
    public void onLoaderReset(final Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
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

    @Override
    public void onDateClicked(final Date d) {
        mAdapter.setJulianDay(TimeUtils.getJulianDay(d.getTime()));

        if(isResumed()) {
            Loader l = getLoaderManager().restartLoader(LOADER_ID_DAYS, null, this);
            if(l != null)
                l.forceLoad();
        }
    }
}
