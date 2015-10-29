package com.dgsd.android.shifttracker.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;

import com.dgsd.android.shifttracker.R;
import com.dgsd.android.shifttracker.fragment.AddShiftFragment;

public class AddShiftActivity extends SingleFragmentActivity<AddShiftFragment> {

    private static final String EXTRA_DATE_HINT = "_date_hint";

    private static final String EXTRA_SHIFT_ID = "_shift_id";

    private static final String EXTRA_CLONE_SHIFT = "_clone_shift";

    public static Intent createIntent(Context context) {
        return createIntent(context, -1);
    }

    public static Intent createIntentForEdit(Context context, long shiftId) {
        return createIntent(context).putExtra(EXTRA_SHIFT_ID, shiftId);
    }

    public static Intent createIntentForClone(Context context, long shiftId) {
        return createIntentForEdit(context, shiftId)
                .putExtra(EXTRA_CLONE_SHIFT, true);
    }

    public static Intent createIntent(Context context, long dateHint) {
        return new Intent(context, AddShiftActivity.class)
                .putExtra(EXTRA_DATE_HINT, dateHint);
    }

    @Override
    protected AddShiftFragment createFragment() {
        final long shiftId = getIntent().getLongExtra(EXTRA_SHIFT_ID, -1);
        final boolean clone = getIntent().getBooleanExtra(EXTRA_CLONE_SHIFT, false);
        if (shiftId >= 0) {
            return AddShiftFragment.newEditInstance(shiftId, clone);
        } else {
            return AddShiftFragment.newInstance(getIntent().getLongExtra(EXTRA_DATE_HINT, -1));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.act_add_shift, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save) {
            final AddShiftFragment fragment
                    = findFragment(R.id.fragment_container, AddShiftFragment.class);

            // Argh! Break in abstraction!
            fragment.getPresenter().onSaveClicked();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.edit_shift_edit_warning_dialog_title)
                .setMessage(R.string.edit_shift_edit_warning_dialog_message)
                .setPositiveButton(R.string.edit_shift_edit_warning_dialog_positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(R.string.edit_shift_edit_warning_dialog_negative_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        AddShiftActivity.super.onBackPressed();
                    }
                })
                .show();
    }
}
