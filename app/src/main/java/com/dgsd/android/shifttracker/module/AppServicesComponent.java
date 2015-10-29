package com.dgsd.android.shifttracker.module;

import com.dgsd.android.shifttracker.data.AppSettings;
import com.dgsd.android.shifttracker.manager.AnalyticsManager;
import com.dgsd.android.shifttracker.mvp.presenter.AddShiftPresenter;
import com.dgsd.android.shifttracker.mvp.presenter.HomePresenter;
import com.dgsd.android.shifttracker.mvp.presenter.MonthPresenter;
import com.dgsd.android.shifttracker.mvp.presenter.StatisticsPresenter;
import com.dgsd.android.shifttracker.mvp.presenter.ViewShiftPresenter;
import com.dgsd.android.shifttracker.mvp.presenter.WeekPresenter;
import com.dgsd.android.shifttracker.service.ReminderScheduleService;
import com.dgsd.android.shifttracker.service.ShowReminderService;
import com.dgsd.shifttracker.data.DataProvider;

import java.util.List;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Dagger component to provide dependency injection
 */
@Singleton
@Component(modules = STModule.class)
public interface AppServicesComponent {

	void inject(HomePresenter presenter);

	void inject(MonthPresenter presenter);

	void inject(AddShiftPresenter presenter);

	void inject(WeekPresenter presenter);

	void inject(ViewShiftPresenter presenter);

	void inject(StatisticsPresenter presenter);

	void inject(ReminderScheduleService service);

	void inject(ShowReminderService service);

	AppSettings appSettings();

	DataProvider dataProvider();

	List<AnalyticsManager.TrackingAgent> analyticsTrackingAgents();
}
