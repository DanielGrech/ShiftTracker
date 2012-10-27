package com.dgsd.android.ShiftTracker.Fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.WazaBe.HoloEverywhere.app.AlertDialog;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.dgsd.android.ShiftTracker.Data.DbField;
import com.dgsd.android.ShiftTracker.Data.Provider;
import com.dgsd.android.ShiftTracker.Model.Shift;
import com.dgsd.android.ShiftTracker.R;
import com.dgsd.android.ShiftTracker.Util.TimeUtils;
import com.dgsd.android.ShiftTracker.Util.UIUtils;

import java.text.NumberFormat;

public class LinkToPaidAppFragment extends SherlockDialogFragment{


    public static LinkToPaidAppFragment newInstance() {
        LinkToPaidAppFragment frag = new LinkToPaidAppFragment();
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setTitle(R.string.feature_unavailable);
        b.setMessage(R.string.summary_unavailable_message);
        b.setPositiveButton(R.string.get_full_version, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri uri = Uri.parse("market://details?id=com.dgsd.android.ShiftTracker");
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
                dialog.dismiss();
            }
        });

        return b.create();
    }

}
