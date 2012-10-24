package com.dgsd.android.ShiftTracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.dgsd.android.ShiftTracker.Adapter.WeekPagerAdapter;
import com.dgsd.android.ShiftTracker.Fragment.GoToFragment;
import com.dgsd.android.ShiftTracker.Util.TimeUtils;
import com.dgsd.android.ShiftTracker.Util.UIUtils;
import com.viewpagerindicator.TitlePageIndicator;

public class MainActivity extends SherlockFragmentActivity implements GoToFragment.OnDateSelectedListener {

    private TitlePageIndicator mIndicator;
    private ViewPager mPager;
    private WeekPagerAdapter mAdapter;
    private GoToFragment mGoToFragment;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            case R.id.settings: {
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            }
            case R.id.go_to: {
                if(mGoToFragment != null && mGoToFragment.isResumed()) {
                    //We're showing already!
                    return true;
                }

                final int centerJd = mAdapter.getJulianDayForPosition(mAdapter.getCenterPosition());
                final int count = mAdapter.getCount() * 7;

                final Time time = new Time();
                time.setJulianDay(centerJd - (count / 2));
                final long min = time.toMillis(true);

                time.setJulianDay(centerJd + (count / 2));
                final long max = time.toMillis(true);

                mGoToFragment = GoToFragment.newInstance(min, max);
                mGoToFragment.setOnDateSelectedListener(this);
                mGoToFragment.show(getSupportFragmentManager(), null);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSelected(int julianDay) {
        mIndicator.setCurrentItem(mAdapter.getPositionForJulianDay(julianDay));
    }
}