package com.dgsd.android.shifttracker.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.dgsd.android.shifttracker.BuildConfig;
import com.dgsd.android.shifttracker.R;
import com.dgsd.android.shifttracker.adapter.BrowsablePagerAdapter;
import com.dgsd.android.shifttracker.adapter.MonthPagerAdapter;
import com.dgsd.android.shifttracker.adapter.WeekPagerAdapter;
import com.dgsd.android.shifttracker.manager.AnalyticsManager;
import com.dgsd.android.shifttracker.module.AppServicesComponent;
import com.dgsd.android.shifttracker.mvp.presenter.HomePresenter;
import com.dgsd.android.shifttracker.mvp.presenter.HomePresenter.ViewType;
import com.dgsd.android.shifttracker.mvp.view.HomeMvpView;
import com.dgsd.android.shifttracker.util.AdUtils;
import com.dgsd.android.shifttracker.util.DialogUtils;
import com.dgsd.android.shifttracker.util.IntentUtils;
import com.dgsd.android.shifttracker.util.TimeUtils;
import com.dgsd.android.shifttracker.view.TintedWhiteFloatingActionButton;
import com.dgsd.shifttracker.model.Shift;
import com.google.android.gms.ads.AdView;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

import static com.dgsd.android.shifttracker.util.DrawableUtils.getTintedDrawable;

public class HomeActivity extends PresentableActivity<HomePresenter> implements
        HomeMvpView, NavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {

    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @Bind(R.id.navigation_drawer)
    NavigationView navigationView;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.pager)
    ViewPager viewPager;

    @Bind(R.id.fab)
    TintedWhiteFloatingActionButton fab;

    @Nullable
    @Bind(R.id.ad_view)
    AdView adView;

    public static Intent createIntent(Context context) {
        return new Intent(context, HomeActivity.class);
    }

    @Override
    protected HomePresenter createPresenter(AppServicesComponent component) {
        return new HomePresenter(this, component);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.act_home;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(getTintedDrawable(this, R.drawable.ic_menu, Color.WHITE));
        ab.setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(this);
        setupNavigationHeader();

        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(Gravity.LEFT);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            closeDraw();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.week_view:
                if (item.isChecked()) {
                    closeDraw();
                } else {
                    getPresenter().onWeekViewClicked();
                }
                return true;
            case R.id.month_view:
                if (item.isChecked()) {
                    closeDraw();
                } else {
                    getPresenter().onMonthViewClicked();
                }
                return true;
            case R.id.stats_view:
                getPresenter().onStatisticsClicked();
                return true;
            case R.id.add_shift:
                getPresenter().onAddShiftClicked();
                return false;
            case R.id.settings:
                getPresenter().onSettingsClicked();
                return false;
            case R.id.send_feedback:
                getPresenter().onSendFeedbackClicked();
                return false;
            case R.id.rate:
                getPresenter().onRateAppClicked();
                return false;
        }
        return false;
    }


    @OnClick(R.id.fab)
    void onFabClicked() {
        getPresenter().onAddShiftClicked();
    }

    @Override
    public void closeDraw() {
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    @Override
    public void sendSupportEmail(String emailSubject, String supportEmail) {
        final Intent intent = IntentUtils.getEmailIntent(supportEmail, emailSubject);
        if (IntentUtils.isAvailable(this, intent)) {
            startActivity(intent);
        }

    }

    @Override
    public void showRateApp() {
        final Intent intent = IntentUtils.getPlayStoreIntent();
        if (IntentUtils.isAvailable(this, intent)) {
            startActivity(intent);
        }
    }

    @Override
    public void showSettings() {
        startActivity(SettingsActivity.createIntent(this));
    }

    @Override
    public void showWeekView(int startDayOfWeek) {
        navigationView.setCheckedItem(R.id.week_view);
        resetAdapter(new WeekPagerAdapter(this, getSupportFragmentManager(), startDayOfWeek,
                TimeUtils.getJulianDay(System.currentTimeMillis())));
    }

    @Override
    public void showMonthView() {
        navigationView.setCheckedItem(R.id.month_view);
        resetAdapter(new MonthPagerAdapter(getSupportFragmentManager()));
    }

    @Override
    public void showStatistics() {
        startActivity(StatisticsActivity.createIntent(this));
    }

    @Override
    public void showRateAppPrompt(String title, String message) {
        AnalyticsManager.trackEvent("rate_app_prompt");
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.app_rating_prompt_positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getPresenter().onRateAppClicked();
                    }
                })
                .setNegativeButton(R.string.app_rating_prompt_negative_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AnalyticsManager.trackClick("cancel_rate_app_prompt");
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void showAddNewShiftFromTemplate(final List<Shift> templateShifts) {
        DialogUtils.getShiftTemplateDialog(
                getContext(), templateShifts,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getPresenter().onAddNewShiftClicked();
                        dialog.dismiss();
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AnalyticsManager.trackClick("cancel_add_shift");
                        dialog.dismiss();
                    }
                },
                new DialogUtils.OnShiftClickedListener() {
                    @Override
                    public void onShiftClicked(Shift shift) {
                        final ViewType type = viewPager.getAdapter() instanceof WeekPagerAdapter ?
                                ViewType.WEEK : ViewType.MONTH;
                        getPresenter().onAddShiftFromTemplateClicked(type, shift, getSelectedDate());
                    }
                },
                new DialogUtils.OnEditShiftClickedListener() {
                    @Override
                    public void onEditShiftClicked(Shift shift) {
                        getPresenter().onEditShiftTemplate(shift);
                    }
                }
        ).show();
    }

    @Override
    public void addShiftFromTemplate(Shift shift) {
        startActivity(AddShiftActivity.createIntentForClone(this, shift.id()));
    }

    @Override
    public void editTemplateShift(Shift shift) {
        startActivity(AddShiftActivity.createIntentForEdit(this, shift.id()));
    }

    @Override
    public void showError(String message) {
        Snackbar.make(drawerLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void addNewShift() {
        final Date selectedDate = getSelectedDate();
        if (selectedDate == null) {
            startActivity(AddShiftActivity.createIntent(this));
        } else {
            startActivity(AddShiftActivity.createIntent(this, selectedDate.getTime()));
        }
    }

    @Override
    public void showAd() {
        AdUtils.loadAndShowAd(adView, new AdUtils.OnAdShownListener() {
            @Override
            public void onAdShown(AdView view) {
                viewPager.setPaddingRelative(
                        viewPager.getPaddingStart(),
                        viewPager.getPaddingTop(),
                        viewPager.getPaddingEnd(),
                        view.getHeight()
                );
            }
        });
    }

    @Override
    public void onPageSelected(int position) {
        if (viewPager.getAdapter() != null && viewPager.getAdapter() instanceof BrowsablePagerAdapter) {
            final BrowsablePagerAdapter adapter = (BrowsablePagerAdapter) viewPager.getAdapter();
            setTitle(adapter.getStatisticsSummary(position));
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // No-op
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // No-op
    }

    public void setTitleFromFragment(String title, Fragment fragment) {
        if (viewPager.getAdapter() != null && viewPager.getAdapter() instanceof BrowsablePagerAdapter) {
            final BrowsablePagerAdapter adapter = (BrowsablePagerAdapter) viewPager.getAdapter();
            if (viewPager.getCurrentItem() == adapter.getPositionForFragment(fragment)) {
                setTitle(title);
            }
        }
    }

    private void setupNavigationHeader() {
        final View navHeader = navigationView.inflateHeaderView(R.layout.view_nav_header);
        if (!BuildConfig.IS_PAID) {
            navHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent = IntentUtils.getPaidAppPlayStoreIntent();
                    if (IntentUtils.isAvailable(getContext(), intent)) {
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private void resetAdapter(final BrowsablePagerAdapter newAdapter) {
        int startingPos = newAdapter.getStartingPosition();

        final BrowsablePagerAdapter existingAdapter = (BrowsablePagerAdapter) viewPager.getAdapter();
        if (existingAdapter != null) {
            final Date selectedDate
                    = existingAdapter.getSelectedDateForItem(viewPager.getCurrentItem());
            final int pos = newAdapter.getPositionForDate(selectedDate);
            if (pos >= 0) {
                startingPos = pos;
            }
        }

        viewPager.setAdapter(newAdapter);
        viewPager.setCurrentItem(startingPos, false);
    }

    private Date getSelectedDate() {
        final BrowsablePagerAdapter adapter = (BrowsablePagerAdapter) viewPager.getAdapter();
        return adapter.getSelectedDateForItem(viewPager.getCurrentItem());
    }
}
