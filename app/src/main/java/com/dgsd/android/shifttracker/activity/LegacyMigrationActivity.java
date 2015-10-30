package com.dgsd.android.shifttracker.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dgsd.android.shifttracker.R;
import com.dgsd.android.shifttracker.data.LegacyDbOpenHelper;
import com.dgsd.android.shifttracker.util.RxUtils;
import com.dgsd.shifttracker.data.DataProvider;
import com.dgsd.shifttracker.model.Shift;

import java.util.List;

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
        }).flatMap(new Func1<Shift, Observable<Shift>>() {
            @Override
            public Observable<Shift> call(Shift shift) {
                return dataProvider.addShift(shift);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        RxUtils.bindActivity(this, observable)
                .subscribe(new Subscriber<Shift>() {
                    @Override
                    public void onCompleted() {
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
        return null;
    }
}
