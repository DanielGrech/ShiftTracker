package com.dgsd.android.ShiftTracker.Model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import com.dgsd.android.ShiftTracker.Data.DbField;
import com.dgsd.android.ShiftTracker.Util.TimeUtils;

public class Shift implements Parcelable {
    public long id;
    public String name;
    public String note;
    public long startTime;
    public long endTime;
    public int julianDay;
    public float payRate;
    public int breakDuration;
    public boolean isTemplate;

    public Shift() {
        id = -1;
        name = null;
        note = null;
        startTime = -1;
        endTime = -1;
        julianDay = -1;
        payRate = -1;
        breakDuration = -1;
        isTemplate = false;
    }

    public static Shift fromParcel(Parcel in) {
        Shift s = new Shift();

        s.id = in.readLong();
        s.name = in.readString();
        s.note = in.readString();
        s.startTime = in.readLong();
        s.endTime = in.readLong();
        s.julianDay = in.readInt();
        s.payRate = in.readFloat();
        s.breakDuration = in.readInt();
        s.isTemplate = in.readInt() == 1;

        return s;
    }

    public static Shift fromCursor(Cursor cursor) {
        if(cursor == null)
            return null;

        final int idCol = cursor.getColumnIndex(DbField.ID.name);
        final int nameCol = cursor.getColumnIndex(DbField.NAME.name);
        final int noteCol = cursor.getColumnIndex(DbField.NOTE.name);
        final int payCol = cursor.getColumnIndex(DbField.PAY_RATE.name);
        final int startCol = cursor.getColumnIndex(DbField.START_TIME.name);
        final int endCol = cursor.getColumnIndex(DbField.END_TIME.name);
        final int dayCol = cursor.getColumnIndex(DbField.JULIAN_DAY.name);
        final int breakCol = cursor.getColumnIndex(DbField.BREAK_DURATION.name);
        final int isTemplateCol = cursor.getColumnIndex(DbField.IS_TEMPLATE.name);

        Shift s = new Shift();
        s.id = cursor.getLong(idCol);
        s.name = cursor.getString(nameCol);
        s.note = cursor.getString(noteCol);
        s.startTime = cursor.getLong(startCol);
        s.endTime = cursor.getLong(endCol);
        s.julianDay = cursor.getInt(dayCol);
        s.payRate = cursor.getFloat(payCol);
        s.breakDuration = cursor.getInt(breakCol);
        s.isTemplate = cursor.getInt(isTemplateCol) == 1;
        return s;
    }

    public float getIncome() {
        if(payRate < 0)
            return 0;
        else
            return (getDurationInMinutes() / 60.0f) * payRate;
    }

    public long getDurationInMinutes() {
        long duration = endTime - startTime;
        if(breakDuration > 0)
            duration -= (breakDuration * TimeUtils.InMillis.MINUTE);

        if(duration < 0)
            duration = 0;

        return duration / TimeUtils.InMillis.MINUTE;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();

        if(id >= 0)
            values.put(DbField.ID.name, id);

        values.put(DbField.JULIAN_DAY.name, julianDay);
        values.put(DbField.START_TIME.name, startTime);
        values.put(DbField.END_TIME.name, endTime);
        values.put(DbField.PAY_RATE.name, payRate);
        values.put(DbField.NAME.name, name);
        values.put(DbField.NOTE.name, note);
        values.put(DbField.BREAK_DURATION.name, breakDuration);
        values.put(DbField.IS_TEMPLATE.name, isTemplate ? 1 : 0);

        return values;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(note);
        dest.writeLong(startTime);
        dest.writeLong(endTime);
        dest.writeInt(julianDay);
        dest.writeFloat(payRate);
        dest.writeInt(breakDuration);
        dest.writeInt(isTemplate ? 1 : 0);
    }

    public static final Creator<Shift> CREATOR = new Creator<Shift>() {
        public Shift createFromParcel(Parcel in) {
            return Shift.fromParcel(in);
        }

        public Shift[] newArray(int size) {
            return new Shift[size];
        }
    };
}
