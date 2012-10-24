package com.dgsd.android.ShiftTracker.Data;

/**
 * @author Daniel Grech
 */
public class DbTable {
	private static final String TAG = DbTable.class.getSimpleName();

    public static final DbTable SHIFTS = new DbTable("shifts", new DbField[] {
        DbField.ID,
        DbField.JULIAN_DAY,
        DbField.START_TIME,
        DbField.END_TIME,
        DbField.PAY_RATE,
        DbField.NAME,
        DbField.NOTE,
        DbField.BREAK_DURATION
    });

	public String name;
	public DbField[] fields;

	public DbTable(String n, DbField[] f) {
		name = n;
		fields = f;
	}

	@Override
	public String toString() {
		return name;
	}


	public String[] getFieldNames() {
		String[] results = new String[fields.length];

		for (int i = 0, size = fields.length; i < size; i++) {
			results[i] = fields[i].name;
		}

		return results;
	}

	public String dropSql() {
		return new StringBuilder().append("DROP TABLE ").append(name).toString();
	}

	public String createSql() {
		StringBuilder builder = new StringBuilder().append("CREATE TABLE ").append(name).append(" ").append("(");

		// Ensure that a comma does not appear on the last iteration
		String comma = "";
		for (DbField field : fields) {
			builder.append(comma);
			comma = ",";

			builder.append(field.name);
			builder.append(" ");
			builder.append(field.type);
			builder.append(" ");

			if (field.constraint != null) {
				builder.append(field.constraint);
			}
		}

		builder.append(")");

		return builder.toString();

	}
}
