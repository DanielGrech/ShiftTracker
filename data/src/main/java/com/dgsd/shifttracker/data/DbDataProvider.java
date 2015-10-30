package com.dgsd.shifttracker.data;

import android.app.backup.BackupAgent;
import android.app.backup.BackupManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.dgsd.shifttracker.model.Shift;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;

import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE;
import static com.dgsd.shifttracker.data.DbContract.Queries;
import static com.dgsd.shifttracker.data.DbContract.TABLE_SHIFTS;

public class DbDataProvider implements DataProvider {

    private final BriteDatabase db;
    private final Context context;
    private final ContentResolver contentResolver;
    private final BackupManager backupManager;

    public static DbDataProvider create(Context context, SqlBrite sqlBrite) {
        final Context appContext = context.getApplicationContext();
        final DbOpenHelper dbOpenHelper = DbOpenHelper.getInstance(appContext);
        final BriteDatabase briteDb = sqlBrite.wrapDatabaseHelper(dbOpenHelper);

        return new DbDataProvider(appContext, briteDb);
    }

    public DbDataProvider(Context context, BriteDatabase db) {
        this.db = db;
        this.context = context;
        this.contentResolver = context.getContentResolver();
        this.backupManager = new BackupManager(context);
    }

    @Override
    public Observable<List<Shift>> getShiftsBetween(long startMillis, long endMillis) {
        final String[] args = {
                String.valueOf(startMillis),
                String.valueOf(endMillis)
        };

        return db.createQuery(TABLE_SHIFTS.name, Queries.GET_SHIFTS_BETWEEN, args)
                .mapToList(CURSOR_TO_SHIFT_MAPPING);
    }

    @Override
    public Observable<List<Shift>> getTemplateShifts() {
        return db.createQuery(TABLE_SHIFTS.name, Queries.GET_TEMPLATE_SHIFTS)
                .mapToList(CURSOR_TO_SHIFT_MAPPING);
    }

    @Override
    public Observable<Shift> getShift(final long shiftId) {
        return db.createQuery(TABLE_SHIFTS.name, Queries.GET_SHIFT, String.valueOf(shiftId))
                .mapToOneOrDefault(CURSOR_TO_SHIFT_MAPPING, null)
                .flatMap(new Func1<Shift, Observable<Shift>>() {
                    @Override
                    public Observable<Shift> call(Shift shift) {
                        if (shift == null) {
                            return Observable.error(
                                    new RuntimeException("No shift with id: " + shiftId));
                        } else {
                            return Observable.just(shift);
                        }
                    }
                });
    }

    @Override
    public Observable<Shift> addShift(final Shift shift) {
        return Observable.defer(new Func0<Observable<Shift>>() {
            @Override
            public Observable<Shift> call() {
                final long id = db.insert(TABLE_SHIFTS.name,
                        ModelUtils.convert(shift), CONFLICT_REPLACE);
                if (id >= 0) {
                    notifyDataChanged();
                    return Observable.just(shift.withId(id));
                } else {
                    return Observable.error(new RuntimeException("Adding shift unsuccessful"));
                }
            }
        });
    }

    @Override
    public Observable<Void> removeShift(final long shiftId) {
        return Observable.defer(new Func0<Observable<Void>>() {
            @Override
            public Observable<Void> call() {
                db.delete(TABLE_SHIFTS.name, Queries.DELETE_SHIFT_CLAUSE, String.valueOf(shiftId));
                notifyDataChanged();
                return Observable.just(null);
            }
        });
    }

    static final Func1<Cursor, Shift> CURSOR_TO_SHIFT_MAPPING = new Func1<Cursor, Shift>() {
        @Override
        public Shift call(Cursor cursor) {
            return ModelUtils.convert(cursor);
        }
    };

    private void notifyDataChanged() {
        contentResolver.notifyChange(STContentProvider.SHIFTS_URI, null);
        context.sendBroadcast(new Intent(DataProvider.UPDATE_ACTION));
        backupManager.dataChanged();
    }
}
