package com.dgsd.android.ShiftTracker;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.dgsd.android.ShiftTracker.Adapter.WeekPagerAdapter;
import com.dgsd.android.ShiftTracker.Util.TimeUtils;
import com.dgsd.android.ShiftTracker.Util.UIUtils;
import com.viewpagerindicator.TitlePageIndicator;

public class MainActivity extends SherlockFragmentActivity {

    private TitlePageIndicator mIndicator;
    private ViewPager mPager;
    private WeekPagerAdapter mAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UIUtils.tryForceMenuOverflow(this);

        final int currentJd = TimeUtils.getCurrentJulianDay();

        mIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
        mPager = (ViewPager) findViewById(R.id.pager);

        mAdapter = new WeekPagerAdapter(this, getSupportFragmentManager(), currentJd);
        mPager.setAdapter(mAdapter);
        mIndicator.setViewPager(mPager, mAdapter.getPositionForJulianDay(currentJd));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.today: {
                mIndicator.setCurrentItem(mAdapter.getPositionForJulianDay(TimeUtils.getCurrentJulianDay()));
            }
        }

        return super.onOptionsItemSelected(item);
    }
}