package com.dgsd.android.shifttracker.activity;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.MenuItem;

import com.dgsd.android.shifttracker.STApp;

import butterknife.ButterKnife;

public abstract class BaseActivity extends RxActivity {

    protected STApp app;

    /**
     * @return the layout resource to use for this activity,
     * or a value <= 0 if no layout should be used
     */
    @LayoutRes
    protected abstract int getLayoutResource();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (STApp) getApplicationContext();


        final int layoutResId = getLayoutResource();
        if (layoutResId > 0) {
            setContentView(layoutResId);
        }
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Retrieve a fragment from {@linkplain #getSupportFragmentManager()}
     *
     * @param id  The layout id of the fragment to find
     * @param cls The class of the fragment
     * @param <T>
     * @return A fragment found in the layout with the given id, or null
     */
    @SuppressWarnings("unchecked")
    @Nullable
    protected <T extends Fragment> T findFragment(@IdRes int id, Class<T> cls) {
        return (T) getSupportFragmentManager().findFragmentById(id);
    }

    public void showUpIndicator() {
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(true);
        }
    }
}
