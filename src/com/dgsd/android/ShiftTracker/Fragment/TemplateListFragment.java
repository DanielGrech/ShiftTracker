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

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.dgsd.android.ShiftTracker.Adapter.TemplateAdapter;
import com.dgsd.android.ShiftTracker.Data.DbField;
import com.dgsd.android.ShiftTracker.Data.Provider;
import com.dgsd.android.ShiftTracker.Model.Shift;
import com.dgsd.android.ShiftTracker.R;
import com.dgsd.android.ShiftTracker.Util.Anim;

public class TemplateListFragment extends SherlockDialogFragment implements LoaderManager.LoaderCallbacks<Cursor>,AdapterView.OnItemClickListener {
    private ListView mList;
    private TemplateAdapter mAdapter;
    private boolean mDismissOnItemClick;

    private OnTemplateClickListener mOnTemplateClickListener;

    public static TemplateListFragment newInstance() {
        TemplateListFragment frag = new TemplateListFragment();
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_template_list, container, false);

        mAdapter = new TemplateAdapter(getActivity());

        mList = (ListView) v.findViewById(R.id.list);
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(this);
        mList.setEmptyView(v.findViewById(android.R.id.empty));

        getDialog().setTitle(R.string.shift_templates);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        return new CursorLoader(getActivity(), Provider.SHIFTS_URI, null,
                DbField.IS_TEMPLATE + "> 0", null, DbField.NAME + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> list, final View view, int pos, long id) {
        final TemplateAdapter.ViewHolder holder = (TemplateAdapter.ViewHolder) view.getTag();
        if(holder != null && holder.shift != null && mOnTemplateClickListener != null)
            mOnTemplateClickListener.onTemplateClicked(holder.shift);

        if(mDismissOnItemClick && this.getDialog() != null && this.getDialog().isShowing())
            this.dismiss();
    }

    public void setDismissOnItemClick(boolean dismiss) {
        mDismissOnItemClick = dismiss;
    }

    public void setOnItemClickListener(OnTemplateClickListener listener) {
        mOnTemplateClickListener = listener;
    }

    public static interface OnTemplateClickListener {
        public void onTemplateClicked(Shift shift);
    }
}
