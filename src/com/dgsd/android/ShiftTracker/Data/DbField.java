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
public class DbField {
    public static final DbField ID = new DbField("_id", "integer", "primary key");
    public static final DbField JULIAN_DAY = new DbField("day", "integer");
    public static final DbField END_JULIAN_DAY = new DbField("end_day", "integer");
    public static final DbField START_TIME = new DbField("start", "integer");
    public static final DbField END_TIME = new DbField("end", "integer");
    public static final DbField PAY_RATE = new DbField("payrate", "real");
    public static final DbField NAME = new DbField("name", "text", "collate nocase");
    public static final DbField NOTE = new DbField("note", "text");
    public static final DbField BREAK_DURATION = new DbField("break_duration", "integer");
    public static final DbField IS_TEMPLATE = new DbField("is_template", "integer");
    public static final DbField REMINDER = new DbField("reminder", "integer", "default -1");

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
