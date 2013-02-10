/*
 * Copyright 2013 Daniel Grech
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.dgsd.android.ShiftTracker.Fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.dgsd.android.ShiftTracker.R;
import org.holoeverywhere.app.AlertDialog;

import java.text.NumberFormat;

public class LinkToPaidAppFragment extends SherlockDialogFragment{

    public static final String KEY_MESSAGE = "_message";

    private String mMessage;

    public static LinkToPaidAppFragment newInstance(String message) {
        LinkToPaidAppFragment frag = new LinkToPaidAppFragment();

        if(!TextUtils.isEmpty(message)) {
            Bundle args = new Bundle();
            args.putString(KEY_MESSAGE, message);
            frag.setArguments(args);
        }
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMessage = getArguments() == null ? null : getArguments().getString(KEY_MESSAGE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setTitle(R.string.feature_unavailable);
        b.setMessage(mMessage);
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
