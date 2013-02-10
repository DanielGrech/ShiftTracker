/*
 * Copyright 2013 Daniel Grech
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.dgsd.android.ShiftTracker.Data;

/**
 * @author Daniel Grech
 */
public class DbTable {
	private static final String TAG = DbTable.class.getSimpleName();

    public static final DbTable SHIFTS = new DbTable("shifts", new DbField[] {
        DbField.ID,
        DbField.JULIAN_DAY,
        DbField.END_JULIAN_DAY,
        DbField.START_TIME,
        DbField.END_TIME,
        DbField.PAY_RATE,
        DbField.NAME,
        DbField.NOTE,
        DbField.BREAK_DURATION,
        DbField.IS_TEMPLATE,
        DbField.REMINDER
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
