package com.dgsd.android.ShiftTracker.Data;

/**
 * @author Daniel Grech
 */
public class DbField {
    public static final DbField ID = new DbField("_id", "integer", "primary key");
    public static final DbField JULIAN_DAY = new DbField("day", "integer");
    public static final DbField START_TIME = new DbField("start", "integer");
    public static final DbField END_TIME = new DbField("end", "integer");
    public static final DbField PAY_RATE = new DbField("payrate", "real");
    public static final DbField NAME = new DbField("name", "text");
    public static final DbField NOTE = new DbField("note", "text");
    public static final DbField BREAK_DURATION = new DbField("break_duration", "integer");
    public static final DbField IS_TEMPLATE = new DbField("is_template", "integer");

    public String name;
	public String type;
	public String constraint;

	public DbField(String n, String t) {
		this(n, t, null);
	}

	public DbField(String n, String t, String c) {
		name = n;
		type = t;
		constraint = c;
	}

	@Override
	public String toString() {
		return name;
	}
}
