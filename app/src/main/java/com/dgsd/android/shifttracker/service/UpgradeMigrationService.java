package com.dgsd.android.shifttracker.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.dgsd.android.shifttracker.BuildConfig;
import com.dgsd.android.shifttracker.STApp;
import com.dgsd.android.shifttracker.data.AppSettings;
import com.dgsd.shifttracker.data.DataProvider;
import com.dgsd.shifttracker.data.ModelUtils;
import com.dgsd.shifttracker.model.Shift;

import java.util.LinkedList;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

public class UpgradeMigrationService extends IntentService {

    private static final Uri FREE_SHIFT_URI
            = Uri.parse("content://" + BuildConfig.CONTENT_PROVIDER_AUTHORITY_FREE + "/shifts");

    public static void start(Context context) {
        context.startService(new Intent(context, UpgradeMigrationService.class));
    }

    public UpgradeMigrationService() {
        super(UpgradeMigrationService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final STApp app = (STApp) getApplication();
        final AppSettings appSettings = app.getAppServicesComponent().appSettings();
        final DataProvider dataProvider = app.getAppServicesComponent().dataProvider();

        final boolean hasRunMigration = appSettings.hasMigratedFreeData().get(false);
        if (BuildConfig.IS_PAID && !hasRunMigration) {
            try {
                final List<Shift> shifts = getShiftsFromFreeApp();
                Observable.from(shifts)
                        .flatMap(new Func1<Shift, Observable<Shift>>() {
                            @Override
                            public Observable<Shift> call(Shift shift) {
                                return dataProvider.addShift(shift);
                            }
                        })
                        .subscribe(new Action1<Shift>() {
                            @Override
                            public void call(Shift shift) {
                                Timber.d("Migrated from free app: %s", shift);
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable error) {
                                Timber.e(error, "Error migrating shift from free app");
                            }
                        });
            } catch (Exception ex) {
                Timber.e(ex, "Error migrating data from free version of the app");
            } finally {
                Timber.d("Finished migrating from free app");
                appSettings.hasMigratedFreeData().put(true);
            }
        }
    }

    private List<Shift> getShiftsFromFreeApp() {
        final List<Shift> shifts = new LinkedList<>();
        final Cursor cursor = getContentResolver().query(
                FREE_SHIFT_URI, null, null, null, null, null
        );
        if (cursor != null && cursor.moveToFirst()) {
            do {
                shifts.add(ModelUtils.convert(cursor));
            } while (cursor.moveToNext());
        }

        return shifts;
    }
}
