package com.dgsd.android.ShiftTracker.Model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import com.dgsd.android.ShiftTracker.Data.DbField;
import com.dgsd.android.ShiftTracker.Util.JsonRepresentable;
import org.json.JSONObject;

public class Shift implements Parcelable, JsonRepresentable {
    public long id;
    public String name;
    public String note;
    public long startTime;
    public long endTime;
    public int julianDay;
    public float payRate;
    public long breakDuration;

    public static Shift fromParcel(Parcel in) {
        Shift s = new Shift();

        s.id = in.readLong();
        s.name = in.readString();
        s.note = in.readString();
        s.startTime = in.readLong();
        s.endTime = in.readLong();
        s.julianDay = in.readInt();
        s.payRate = in.readFloat();
        s.breakDuration = in.readLong();

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

        Shift s = new Shift();
        s.id = cursor.getLong(idCol);
        s.name = cursor.getString(nameCol);
        s.note = cursor.getString(noteCol);
        s.startTime = cursor.getLong(startCol);
        s.endTime = cursor.getLong(endCol);
        s.julianDay = cursor.getInt(dayCol);
        s.payRate = cursor.getFloat(payCol);
        s.breakDuration = cursor.getLong(breakCol);
        return s;
    }

    @Override
    public JSONObject toJson() {
        return null;
    }

    @Override
    public void fromJson(JSONObject json) {

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
        dest.writeLong(breakDuration);
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
