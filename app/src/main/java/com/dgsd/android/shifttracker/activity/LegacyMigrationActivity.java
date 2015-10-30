package com.dgsd.android.shifttracker.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dgsd.android.shifttracker.R;
import com.dgsd.android.shifttracker.data.AppSettings.Defaults;
import com.dgsd.android.shifttracker.data.LegacyDbOpenHelper;
import com.dgsd.android.shifttracker.util.ModelUtils;
import com.dgsd.android.shifttracker.util.RxUtils;
import com.dgsd.shifttracker.data.DataProvider;
import com.dgsd.shifttracker.model.Shift;
import com.dgsd.shifttracker.model.TimePeriod;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class LegacyMigrationActivity extends BaseActivity {

    public static Intent createIntent(Context context) {
        return new Intent(context, LegacyMigrationActivity.class);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.act_legacy_migration;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        setFinishOnTouchOutside(false);

        runMigration();
    }

    private void runMigration() {
        final DataProvider dataProvider = app.getAppServicesComponent().dataProvider();
        final LegacyDbOpenHelper helper = LegacyDbOpenHelper.getInstance(this);

        final Observable<Shift> observable = Observable.defer(new Func0<Observable<LegacyDbOpenHelper.LegacyShift>>() {
            @Override
            public Observable<LegacyDbOpenHelper.LegacyShift> call() {
                final List<LegacyDbOpenHelper.LegacyShift> shifts = helper.getShifts();
                return shifts == null ?
                        Observable.<LegacyDbOpenHelper.LegacyShift>empty() : Observable.from(shifts);
            }
        }).map(new Func1<LegacyDbOpenHelper.LegacyShift, Shift>() {
            @Override
            public Shift call(LegacyDbOpenHelper.LegacyShift legacyShift) {
                return convert(legacyShift);
            }
        }).distinct().flatMap(new Func1<Shift, Observable<Shift>>() {
            @Override
            public Observable<Shift> call(Shift shift) {
                return dataProvider.addShift(shift);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        RxUtils.bindActivity(this, observable)
                .subscribe(new Subscriber<Shift>() {
                    @Override
                    public void onCompleted() {
                        Timber.d("No shifts found which need migration");
                        onMigrationFinished();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Error running migration");
                        onMigrationFinished();
                    }

                    @Override
                    public void onNext(Shift shift) {
                        Timber.d("Migrated: %s", shift);
                    }
                });
    }

    private void onMigrationFinished() {
        LegacyDbOpenHelper.removeDatabase(this);
        startActivity(HomeActivity.createIntent(this));
        finish();
    }

    private Shift convert(LegacyDbOpenHelper.LegacyShift legacyShift) {
        return Shift.builder()
                .timePeriod(TimePeriod.builder()
                        .startMillis(legacyShift.startTime)
                        .endMillis(legacyShift.endTime)
                        .create())
                .unpaidBreakDuration(legacyShift.breakDuration < 0 ?
                        -1 : TimeUnit.MINUTES.toMillis(legacyShift.breakDuration))
                .reminderBeforeShift(legacyShift.reminder < 0 ?
                        -1 : TimeUnit.MINUTES.toMillis(legacyShift.reminder))
                .color(ModelUtils.getColorItems(this)[Defaults.colorItem()].color())
                .title(legacyShift.name)
                .notes(legacyShift.note)
                .isTemplate(legacyShift.isTemplate)
                .payRate(legacyShift.payRate)
                .create();
    }
}
