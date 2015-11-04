package com.dgsd.android.shifttracker.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v7.app.AlertDialog;

import com.dgsd.android.shifttracker.R;
import com.dgsd.android.shifttracker.adapter.ShiftTemplateAdapter;
import com.dgsd.shifttracker.model.Shift;

import java.util.List;

public class DialogUtils {

    DialogUtils() {
        // No instances..
    }

    public static AlertDialog getShiftTemplateDialog(final Context context,
                                              final List<Shift> templateShifts,
                                              final OnClickListener onPositiveClickListener,
                                              final OnClickListener onNegativeClickListener,
                                              final OnShiftClickedListener onClickListener,
                                              final OnEditShiftClickedListener onEditListener) {
        final ShiftTemplateAdapter adapter = new ShiftTemplateAdapter(templateShifts);
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.nav_item_add_shift)
                .setAdapter(adapter, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onClickListener.onShiftClicked(templateShifts.get(which));
                    }
                })
                .setPositiveButton(R.string.create_new_shift, onPositiveClickListener)
                .setNegativeButton(android.R.string.cancel, onNegativeClickListener)
                .show();

        adapter.setOnEditShiftListener(new ShiftTemplateAdapter.OnEditShiftClicked() {
            @Override
            public void onEditShift(Shift shift) {
                dialog.dismiss();
                onEditListener.onEditShiftClicked(shift);
            }
        });

        return dialog;
    }

    public interface OnShiftClickedListener {
        void onShiftClicked(Shift shift);
    }

    public interface OnEditShiftClickedListener {
        void onEditShiftClicked(Shift shift);
    }
}
