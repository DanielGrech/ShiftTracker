package com.dgsd.shifttracker.data;

import android.content.ContentValues;
import android.database.MatrixCursor;

import com.dgsd.shifttracker.model.Shift;
import com.dgsd.shifttracker.model.TimePeriod;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.concurrent.TimeUnit;

import static com.dgsd.shifttracker.data.DbContract.COL_COLOR;
import static com.dgsd.shifttracker.data.DbContract.COL_END_TIME;
import static com.dgsd.shifttracker.data.DbContract.COL_ID;
import static com.dgsd.shifttracker.data.DbContract.COL_IS_TEMPLATE;
import static com.dgsd.shifttracker.data.DbContract.COL_NOTES;
import static com.dgsd.shifttracker.data.DbContract.COL_OVERTIME_END_TIME;
import static com.dgsd.shifttracker.data.DbContract.COL_OVERTIME_PAY_RATE;
import static com.dgsd.shifttracker.data.DbContract.COL_OVERTIME_START_TIME;
import static com.dgsd.shifttracker.data.DbContract.COL_PAY_RATE;
import static com.dgsd.shifttracker.data.DbContract.COL_REMINDER;
import static com.dgsd.shifttracker.data.DbContract.COL_START_TIME;
import static com.dgsd.shifttracker.data.DbContract.COL_TITLE;
import static com.dgsd.shifttracker.data.DbContract.COL_UNPAID_BREAK;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class ModelUtilsTest {

    @Test
    public void testConvertFromShiftToCursorWithNoId() {
        final ContentValues values = ModelUtils.convert(createShift().withId(-1));
        assertThat(values.get(COL_ID.name)).isNull();
    }

    @Test
    public void testConvertFromShiftToCursorWithNoOvertime() {
        final ContentValues values = ModelUtils.convert(createShift().withOvertime(null));
        assertThat(values.getAsLong(COL_OVERTIME_START_TIME.name)).isEqualTo(-1l);
        assertThat(values.getAsLong(COL_OVERTIME_END_TIME.name)).isEqualTo(-1l);
    }

    @Test
    public void testConvertFromShiftToCursorWhenNotATemplate() {
        final ContentValues values = ModelUtils.convert(createShift().withIsTemplate(false));
        assertThat(values.get(COL_IS_TEMPLATE.name)).isInstanceOf(Integer.class).isEqualTo(0);
    }

    @Test
    public void testConvertFromShiftToCursor() {
        final long EXPECTED_ID = 123;
        final String EXPECTED_TITLE = "Title";
        final String EXPECTED_NOTES = "Here are some notes";
        final float EXPECTED_PAY_RATE = 49.95f;
        final long EXPECTED_UNPAID_BREAK = TimeUnit.MINUTES.toMillis(45);
        final int EXPECTED_COLOR = 0xFF00FF00;
        final long EXPECTED_REMINDER = TimeUnit.MINUTES.toMillis(10);
        final boolean EXPECTED_IS_TEMPLATE = true;
        final long EXPECTED_START_TIME = 100l;
        final long EXPECTED_END_TIME = 200l;
        final long EXPECTED_OVERTIME_START_TIME = 200l;
        final long EXPECTED_OVERTIME_END_TIME = 300l;
        final float EXPECTED_OVERTIME_PAY_RATE = 60f;

        final Shift shift = Shift.builder()
                .id(EXPECTED_ID)
                .title(EXPECTED_TITLE)
                .notes(EXPECTED_NOTES)
                .payRate(EXPECTED_PAY_RATE)
                .unpaidBreakDuration(EXPECTED_UNPAID_BREAK)
                .color(EXPECTED_COLOR)
                .reminderBeforeShift(EXPECTED_REMINDER)
                .isTemplate(EXPECTED_IS_TEMPLATE)
                .timePeriod(TimePeriod.builder()
                        .startMillis(EXPECTED_START_TIME)
                        .endMillis(EXPECTED_END_TIME)
                        .create())
                .overtime(TimePeriod.builder()
                        .startMillis(EXPECTED_OVERTIME_START_TIME)
                        .endMillis(EXPECTED_OVERTIME_END_TIME)
                        .create())
                .overtimePayRate(EXPECTED_OVERTIME_PAY_RATE)
                .create();

        final ContentValues values = ModelUtils.convert(shift);

        assertThat(values.get(COL_ID.name)).isInstanceOf(Long.class).isEqualTo(EXPECTED_ID);
        assertThat(values.get(COL_TITLE.name)).isInstanceOf(String.class).isEqualTo(EXPECTED_TITLE);
        assertThat(values.get(COL_NOTES.name)).isInstanceOf(String.class).isEqualTo(EXPECTED_NOTES);
        assertThat(values.get(COL_PAY_RATE.name)).isInstanceOf(Float.class).isEqualTo(EXPECTED_PAY_RATE);
        assertThat(values.get(COL_UNPAID_BREAK.name)).isInstanceOf(Long.class).isEqualTo(EXPECTED_UNPAID_BREAK);
        assertThat(values.get(COL_COLOR.name)).isInstanceOf(Integer.class).isEqualTo(EXPECTED_COLOR);
        assertThat(values.get(COL_REMINDER.name)).isInstanceOf(Long.class).isEqualTo(EXPECTED_REMINDER);
        assertThat(values.get(COL_IS_TEMPLATE.name)).isInstanceOf(Integer.class).isEqualTo(EXPECTED_IS_TEMPLATE ? 1 : 0);
        assertThat(values.get(COL_START_TIME.name)).isInstanceOf(Long.class).isEqualTo(EXPECTED_START_TIME);
        assertThat(values.get(COL_END_TIME.name)).isInstanceOf(Long.class).isEqualTo(EXPECTED_END_TIME);
        assertThat(values.get(COL_OVERTIME_START_TIME.name)).isInstanceOf(Long.class).isEqualTo(EXPECTED_OVERTIME_START_TIME);
        assertThat(values.get(COL_OVERTIME_END_TIME.name)).isInstanceOf(Long.class).isEqualTo(EXPECTED_OVERTIME_END_TIME);
        assertThat(values.get(COL_OVERTIME_PAY_RATE.name)).isInstanceOf(Float.class).isEqualTo(EXPECTED_OVERTIME_PAY_RATE);
    }

    @Test
    public void testConvertFromCursorToShift() {
        final long EXPECTED_ID = 123;
        final String EXPECTED_TITLE = "Title";
        final String EXPECTED_NOTES = "Here are some notes";
        final float EXPECTED_PAY_RATE = 49.95f;
        final long EXPECTED_UNPAID_BREAK = TimeUnit.MINUTES.toMillis(45);
        final int EXPECTED_COLOR = 0xFF00FF00;
        final long EXPECTED_REMINDER = TimeUnit.MINUTES.toMillis(10);
        final boolean EXPECTED_IS_TEMPLATE = true;
        final long EXPECTED_START_TIME = 100l;
        final long EXPECTED_END_TIME = 200l;
        final long EXPECTED_OVERTIME_START_TIME = 200l;
        final long EXPECTED_OVERTIME_END_TIME = 300l;
        final float EXPECTED_OVERTIME_PAY_RATE = 60f;

        final String[] fieldNames = new String[DbContract.TABLE_SHIFTS.fields.length];
        for (int i = 0; i < fieldNames.length; i++) {
            fieldNames[i] = DbContract.TABLE_SHIFTS.fields[i].name;
        }
        final MatrixCursor cursor = new MatrixCursor(fieldNames);
        cursor.addRow(new Object[]{
                EXPECTED_ID,
                EXPECTED_TITLE,
                EXPECTED_NOTES,
                EXPECTED_PAY_RATE,
                EXPECTED_UNPAID_BREAK,
                EXPECTED_COLOR,
                EXPECTED_START_TIME,
                EXPECTED_END_TIME,
                EXPECTED_IS_TEMPLATE ? 1 : 0,
                EXPECTED_OVERTIME_START_TIME,
                EXPECTED_OVERTIME_END_TIME,
                EXPECTED_OVERTIME_PAY_RATE,
                EXPECTED_REMINDER
        });

        cursor.moveToFirst();
        final Shift shift = ModelUtils.convert(cursor);

        assertThat(shift.id()).isEqualTo(EXPECTED_ID);
        assertThat(shift.title()).isInstanceOf(String.class).isEqualTo(EXPECTED_TITLE);
        assertThat(shift.notes()).isInstanceOf(String.class).isEqualTo(EXPECTED_NOTES);
        assertThat(shift.payRate()).isEqualTo(EXPECTED_PAY_RATE);
        assertThat(shift.unpaidBreakDuration()).isEqualTo(EXPECTED_UNPAID_BREAK);
        assertThat(shift.color()).isEqualTo(EXPECTED_COLOR);
        assertThat(shift.reminderBeforeShift()).isEqualTo(EXPECTED_REMINDER);
        assertThat(shift.isTemplate()).isEqualTo(EXPECTED_IS_TEMPLATE);
        assertThat(shift.timePeriod().startMillis()).isEqualTo(EXPECTED_START_TIME);
        assertThat(shift.timePeriod().endMillis()).isEqualTo(EXPECTED_END_TIME);
        assertThat(shift.overtime().startMillis()).isEqualTo(EXPECTED_OVERTIME_START_TIME);
        assertThat(shift.overtime().endMillis()).isEqualTo(EXPECTED_OVERTIME_END_TIME);
        assertThat(shift.overtimePayRate()).isEqualTo(EXPECTED_OVERTIME_PAY_RATE);
    }

    private static Shift createShift() {
        return Shift.builder()
                .payRate(10)
                .unpaidBreakDuration(0)
                .timePeriod(TimePeriod.builder()
                        .startMillis(0)
                        .endMillis(10)
                        .create())
                .overtime(TimePeriod.builder()
                        .startMillis(10)
                        .endMillis(20)
                        .create())
                .overtimePayRate(20)
                .create();
    }
}