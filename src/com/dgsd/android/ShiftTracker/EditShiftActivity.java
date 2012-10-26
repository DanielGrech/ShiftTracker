package com.dgsd.android.ShiftTracker;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.dgsd.android.ShiftTracker.Data.DbField;
import com.dgsd.android.ShiftTracker.Data.Provider;
import com.dgsd.android.ShiftTracker.Fragment.EditShiftFragment;
import com.dgsd.android.ShiftTracker.Model.Shift;
import com.dgsd.android.ShiftTracker.Service.DbService;
import de.neofonie.mobile.app.android.widget.crouton.Crouton;
import de.neofonie.mobile.app.android.widget.crouton.Style;

public class EditShiftActivity extends SherlockFragmentActivity {
    private static final String KEY_FRAGMENT = "_key_fragment";
    public static final String EXTRA_SHIFT = "com.dgsd.android.ShiftTracker.EditShiftActivity._extra_id";
    public static final String EXTRA_JULIAN_DAY = "com.dgsd.android.ShiftTracker.EditShiftActivity._extra_julian_day";

    private EditShiftFragment mEditShiftFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_single_fragment);

        setupActionBar();

        FragmentManager fm = getSupportFragmentManager();
        if(savedInstanceState != null)
            mEditShiftFragment = (EditShiftFragment) fm.getFragment(savedInstanceState, KEY_FRAGMENT);

        if(mEditShiftFragment == null) {
            final Shift shift = (Shift) getIntent().getParcelableExtra(EXTRA_SHIFT);
            final int jd = getIntent().getIntExtra(EXTRA_JULIAN_DAY, -1);

            if(shift != null)
                mEditShiftFragment = EditShiftFragment.newInstance(shift);
            else
                mEditShiftFragment = EditShiftFragment.newInstance(jd);

            fm.beginTransaction().replace(R.id.container, mEditShiftFragment).commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mEditShiftFragment != null && mEditShiftFragment.isAdded())
            getSupportFragmentManager().putFragment(outState, KEY_FRAGMENT, mEditShiftFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.edit_shift, menu);

        MenuItem deleteItem = menu.findItem(R.id.delete);
        deleteItem.setEnabled(mEditShiftFragment.isEditing());
        deleteItem.setVisible(mEditShiftFragment.isEditing());

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.delete) {
            if(mEditShiftFragment.isEditing())
                DbService.async_delete(this, Provider.SHIFTS_URI, DbField.ID + "=" + mEditShiftFragment.getEditingId());

            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void setupActionBar() {
        // Inflate a "Done/Discard" custom action bar view.
        final ActionBar ab = getSupportActionBar();
        LayoutInflater inflater = (LayoutInflater) ab.getThemedContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.ab_done_discard, null);
        view.findViewById(R.id.actionbar_done).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String error = mEditShiftFragment.validate();
                        if(TextUtils.isEmpty(error)) {
                            Shift shift = mEditShiftFragment.getShift();

                            final ContentValues values = shift.toContentValues();
                            if(mEditShiftFragment.isEditing()) {
                                DbService.async_update(EditShiftActivity.this, Provider.SHIFTS_URI,
                                        DbField.ID + "=" + mEditShiftFragment.getEditingId(), values);
                            } else {
                                DbService.async_insert(EditShiftActivity.this, Provider.SHIFTS_URI, values);
                            }

                            finish();
                        } else {
                            Crouton.clearCroutonsForActivity(EditShiftActivity.this);
                            Crouton.showText(EditShiftActivity.this, error, Style.ALERT);
                        }
                    }
                });
        view.findViewById(R.id.actionbar_discard).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

        // Show the custom action bar view and hide the normal Home icon and title.
        ab.setDisplayOptions(
                ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
                        | ActionBar.DISPLAY_SHOW_TITLE);
        ab.setCustomView(view, new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onDestroy() {
        Crouton.clearCroutonsForActivity(this);
        super.onDestroy();
    }
}
