package com.dgsd.shifttracker.data;


import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.dgsd.shifttracker.model.Shift;
import com.dgsd.shifttracker.model.TimePeriod;

import static com.dgsd.shifttracker.data.DbContract.COL_COLOR;
import static com.dgsd.shifttracker.data.DbContract.COL_END_TIME;
import static com.dgsd.shifttracker.data.DbContract.COL_ID;
import static com.dgsd.shifttracker.data.DbContract.COL_NOTES;
import static com.dgsd.shifttracker.data.DbContract.COL_OVERTIME_END_TIME;
import static com.dgsd.shifttracker.data.DbContract.COL_OVERTIME_PAY_RATE;
import static com.dgsd.shifttracker.data.DbContract.COL_OVERTIME_START_TIME;
import static com.dgsd.shifttracker.data.DbContract.COL_PAY_RATE;
import static com.dgsd.shifttracker.data.DbContract.COL_REMINDER;
import static com.dgsd.shifttracker.data.DbContract.COL_START_TIME;
import static com.dgsd.shifttracker.data.DbContract.COL_TITLE;
import static com.dgsd.shifttracker.data.DbContract.COL_UNPAID_BREAK;
import static com.dgsd.shifttracker.data.DbContract.TABLE_SHIFTS;
import static com.dgsd.shifttracker.data.DbContract.Table;

public class ModelUtils {

    ModelUtils() {
        // No instances..
    }

    public static ContentValues convert(@NonNull Shift shift) {
        final ContentValues values = new ContentValues();
        if (shift.id() >= 0) {
            values.put(COL_ID.name, shift.id());
        }

        values.put(DbContract.COL_TITLE.name, shift.title());
        values.put(DbContract.COL_NOTES.name, shift.notes());
        values.put(DbContract.COL_PAY_RATE.name, shift.payRate());
        values.put(DbContract.COL_UNPAID_BREAK.name, shift.unpaidBreakDuration());
        values.put(DbContract.COL_COLOR.name, shift.color());
        values.put(DbContract.COL_REMINDER.name, shift.reminderBeforeShift());
        values.put(DbContract.COL_IS_TEMPLATE.name, shift.isTemplate() ? 1 : 0);
        values.put(DbContract.COL_START_TIME.name, shift.timePeriod().startMillis());
        values.put(DbContract.COL_END_TIME.name, shift.timePeriod().endMillis());
        values.put(DbContract.COL_OVERTIME_START_TIME.name,
                shift.overtime() == null ? -1 : shift.overtime().endMillis());
        values.put(DbContract.COL_OVERTIME_END_TIME.name,
                shift.overtime() == null ? -1 : shift.overtime().startMillis());
        values.put(DbContract.COL_OVERTIME_PAY_RATE.name, shift.overtimePayRate());
        return values;
    }

    public static Shift convert(@NonNull Cursor cursor) {
        final Table t = TABLE_SHIFTS;
        Shift.Builder builder = Shift.builder()
                .id(cursor.getLong(t.columnIndex(COL_ID)))
                .title(cursor.getString(t.columnIndex(COL_TITLE)))
                .notes(cursor.getString(t.columnIndex(COL_NOTES)))
                .payRate(cursor.getFloat(t.columnIndex(COL_PAY_RATE)))
                .unpaidBreakDuration(cursor.getLong(t.columnIndex(COL_UNPAID_BREAK)))
                .color(cursor.getInt(t.columnIndex(COL_COLOR)))
                .reminderBeforeShift(cursor.getLong(t.columnIndex(COL_REMINDER)))
                .isTemplate(cursor.getInt(t.columnIndex(DbContract.COL_IS_TEMPLATE)) == 1);

        builder = builder.timePeriod(TimePeriod.builder()
                .startMillis(cursor.getLong(t.columnIndex(COL_START_TIME)))
                .endMillis(cursor.getLong(t.columnIndex(COL_END_TIME)))
                .create());

        final long overtimeStart = cursor.getLong(t.columnIndex(COL_OVERTIME_START_TIME));
        final long overtimeEnd = cursor.getLong(t.columnIndex(COL_OVERTIME_END_TIME));

        if (overtimeStart > 0 && overtimeEnd > 0) {
            builder = builder.overtimePayRate(cursor.getFloat(t.columnIndex(COL_OVERTIME_PAY_RATE)))
                    .overtime(TimePeriod.builder()
                            .startMillis(overtimeStart)
                            .endMillis(overtimeEnd)
                            .create());
        }

        return builder.create();
    }
}
