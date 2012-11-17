package com.dgsd.android.ShiftTracker.Model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.dgsd.android.ShiftTracker.Data.DbField;
import com.dgsd.android.ShiftTracker.Util.TimeUtils;

public class Shift implements Parcelable {
    public long id;
    public String name;
    public String note;
    private long startTime;
    private long endTime;
    public int julianDay;
    public int endJulianDay;
    public float payRate;
    public int breakDuration;
    public boolean isTemplate;
    public int reminder;

    public Shift() {
        id = -1;
        name = null;
        note = null;
        startTime = -1;
        endTime = -1;
        julianDay = -1;
        endJulianDay = -1;
        payRate = -1;
        breakDuration = -1;
        isTemplate = false;
        reminder = -1;
    }

    public static Shift fromParcel(Parcel in) {
        Shift s = new Shift();

        s.id = in.readLong();
        s.name = in.readString();
        s.note = in.readString();
        s.startTime = in.readLong();
        s.endTime = in.readLong();
        s.julianDay = in.readInt();
        s.endJulianDay = in.readInt();
        s.payRate = in.readFloat();
        s.breakDuration = in.readInt();
        s.isTemplate = in.readInt() == 1;
        s.reminder = in.readInt();

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
        final int endDayCol = cursor.getColumnIndex(DbField.END_JULIAN_DAY.name);
        final int breakCol = cursor.getColumnIndex(DbField.BREAK_DURATION.name);
        final int isTemplateCol = cursor.getColumnIndex(DbField.IS_TEMPLATE.name);
        final int reminderCol = cursor.getColumnIndex(DbField.REMINDER.name);

        Shift s = new Shift();
        s.id = cursor.getLong(idCol);
        s.name = cursor.getString(nameCol);
        s.note = cursor.getString(noteCol);
        s.startTime = cursor.getLong(startCol);
        s.endTime = cursor.getLong(endCol);
        s.julianDay = cursor.getInt(dayCol);
        s.endJulianDay = cursor.isNull(endDayCol) ? s.julianDay : cursor.getInt(endDayCol); //For compatability
        s.payRate = cursor.getFloat(payCol);
        s.breakDuration = cursor.getInt(breakCol);
        s.isTemplate = cursor.getInt(isTemplateCol) == 1;
        s.reminder = cursor.getInt(reminderCol);
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

    public long getReminderTime() {
        if(reminder == -1)
            return -1;

        return getStartTime() - (reminder * TimeUtils.InMillis.MINUTE);
    }

    public long getStartTime() {
        if(julianDay == -1 || startTime == -1)
            return -1;
        else
            return TimeUtils.getMillisFrom(julianDay, startTime);
    }

    public void setStartTime(long timeOfDay) {
        startTime = TimeUtils.getMillisFrom(julianDay, timeOfDay);
    }

    public long getEndTime() {
        if(julianDay == -1 || endTime == -1)
            return -1;
        else
            return TimeUtils.getMillisFrom(endJulianDay, endTime);
    }

    public void setEndTime(long timeOfDay) {
        endTime = TimeUtils.getMillisFrom(endJulianDay, timeOfDay);
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();

        if(id >= 0)
            values.put(DbField.ID.name, id);

        values.put(DbField.JULIAN_DAY.name, julianDay);
        values.put(DbField.END_JULIAN_DAY.name, endJulianDay);
        values.put(DbField.START_TIME.name, startTime);
        values.put(DbField.END_TIME.name, endTime);
        values.put(DbField.PAY_RATE.name, payRate);
        values.put(DbField.NAME.name, name);
        values.put(DbField.NOTE.name, note);
        values.put(DbField.BREAK_DURATION.name, breakDuration);
        values.put(DbField.IS_TEMPLATE.name, isTemplate ? 1 : 0);
        values.put(DbField.REMINDER.name, reminder);

        return values;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Shift shift = (Shift) o;

        if (breakDuration != shift.breakDuration)
            return false;
        if (endTime != shift.endTime)
            return false;
        if (julianDay != shift.julianDay)
            return false;
        if (endJulianDay != shift.endJulianDay)
            return false;
        if (Float.compare(shift.payRate, payRate) != 0)
            return false;
        if (startTime != shift.startTime)
            return false;
        if (!TextUtils.equals(name, shift.name))
            return false;
        if (!TextUtils.equals(note, shift.note))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (note != null ? note.hashCode() : 0);
        result = 31 * result + (int) (startTime ^ (startTime >>> 32));
        result = 31 * result + (int) (endTime ^ (endTime >>> 32));
        result = 31 * result + julianDay;
        result = 31 * result + endJulianDay;
        result = 31 * result + (payRate != +0.0f ? Float.floatToIntBits(payRate) : 0);
        result = 31 * result + breakDuration;
        return result;
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
        dest.writeInt(endJulianDay);
        dest.writeFloat(payRate);
        dest.writeInt(breakDuration);
        dest.writeInt(isTemplate ? 1 : 0);
        dest.writeInt(reminder);
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
