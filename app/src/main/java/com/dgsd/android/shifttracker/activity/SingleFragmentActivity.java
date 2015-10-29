package com.dgsd.android.shifttracker.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.dgsd.android.shifttracker.R;

abstract class SingleFragmentActivity<T extends Fragment> extends BaseActivity {

    protected abstract T createFragment();

    @Override
    protected final int getLayoutResource() {
        return R.layout.act_single_fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");

        final FragmentManager fm = getSupportFragmentManager();
        final Fragment frag = fm.findFragmentById(R.id.fragment_container);
        if (frag == null) {
            fm.beginTransaction()
                    .replace(R.id.fragment_container, createFragment())
                    .commitAllowingStateLoss();
        }
    }

}
