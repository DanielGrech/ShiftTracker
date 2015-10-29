package com.dgsd.android.shifttracker.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.dgsd.android.shifttracker.R;
import com.dgsd.android.shifttracker.fragment.ViewShiftFragment;

public class ViewShiftActivity extends SingleFragmentActivity {

    private static final String EXTRA_SHIFT_ID = "_shift_id";
    private static final String ACTION_FROM_REMINDER = "ACTION_FROM_REMINDER";

    public static Intent createIntent(Context context, long shiftId) {
        return new Intent(context, ViewShiftActivity.class)
                .putExtra(EXTRA_SHIFT_ID, shiftId);
    }

    public static Intent createIntentFromReminder(Context context, long shiftId) {
        return new Intent(context, ViewShiftActivity.class)
                .setAction(ACTION_FROM_REMINDER)
                .putExtra(EXTRA_SHIFT_ID, shiftId);
    }

    @Override
    protected Fragment createFragment() {
        return ViewShiftFragment.newInstance(getIntent().getLongExtra(EXTRA_SHIFT_ID, -1));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cm_shift, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final ViewShiftFragment fragment
                = findFragment(R.id.fragment_container, ViewShiftFragment.class);
        switch (item.getItemId()) {
            case android.R.id.home:
                if (ACTION_FROM_REMINDER.equals(getIntent().getAction())) {
                    onSupportNavigateUp();
                    return true;
                } else {
                    break;
                }
            case R.id.delete:
                fragment.getPresenter().onDeleteClicked();
                return true;
            case R.id.clone:
                fragment.getPresenter().onCloneClicked();
                return true;
            case R.id.export:
                fragment.getPresenter().onExportClicked();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean supportShouldUpRecreateTask(Intent targetIntent) {
        return true;
    }
}
