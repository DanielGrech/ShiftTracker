package com.dgsd.shifttracker.data;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.SparseIntArray;

import java.util.HashMap;

class DbContract {

    static final Field COL_ID = new Field("_id", Field.TYPE_INTEGER, "PRIMARY KEY AUTOINCREMENT");
    static final Field COL_TITLE = new Field("title", Field.TYPE_TEXT);
    static final Field COL_NOTES = new Field("notes", Field.TYPE_TEXT);
    static final Field COL_PAY_RATE = new Field("pay_rate", Field.TYPE_REAL);
    static final Field COL_UNPAID_BREAK = new Field("unpaid_break", Field.TYPE_INTEGER);
    static final Field COL_COLOR = new Field("color", Field.TYPE_INTEGER);
    static final Field COL_IS_TEMPLATE = new Field("is_template", Field.TYPE_INTEGER);
    static final Field COL_REMINDER = new Field("reminder", Field.TYPE_INTEGER);
    static final Field COL_OVERTIME_PAY_RATE = new Field("overtime_pay_rate", Field.TYPE_REAL);
    static final Field COL_START_TIME = new Field("start_time", Field.TYPE_INTEGER);
    static final Field COL_END_TIME = new Field("end_time", Field.TYPE_INTEGER);
    static final Field COL_OVERTIME_START_TIME = new Field("overtime_start_time", Field.TYPE_INTEGER);
    static final Field COL_OVERTIME_END_TIME = new Field("overtime_end_time", Field.TYPE_INTEGER);

    static final Table TABLE_SHIFTS = new Table("shifts",
            COL_ID,
            COL_TITLE,
            COL_NOTES,
            COL_PAY_RATE,
            COL_UNPAID_BREAK,
            COL_COLOR,
            COL_START_TIME,
            COL_END_TIME,
            COL_IS_TEMPLATE,
            COL_OVERTIME_START_TIME,
            COL_OVERTIME_END_TIME,
            COL_OVERTIME_PAY_RATE,
            COL_REMINDER
    );

    static class Table {
        final String name;
        final Field[] fields;
        private final HashMap<String, Integer> fieldNameToPositionMapping;

        Table(String name, Field... fields) {
            this.name = name;
            this.fields = fields;

            this.fieldNameToPositionMapping = new HashMap<>();
            for (int i = 0, len = fields.length; i < len; i++) {
                this.fieldNameToPositionMapping.put(fields[i].name, i);
            }
        }

        public String getCreateSql() {
            final StringBuilder sb = new StringBuilder();
            sb.append("CREATE TABLE IF NOT EXISTS ").append(name)
                    .append('(')
                    .append(TextUtils.join(",", fields))
                    .append(')');

            return sb.toString();
        }

        public int columnIndex(@NonNull Field column) {
            Integer pos = fieldNameToPositionMapping.get(column.name);
            if (pos == null) {
                throw new IllegalStateException("No field named '" + column.name +
                        "' in table '" + this.name + "'");
            } else {
                return pos;
            }
        }
    }

    static class Field {

        private static final String TYPE_INTEGER = "INTEGER";
        private static final String TYPE_REAL = "REAL";
        private static final String TYPE_TEXT = "TEXT";

        final String name;
        final String type;
        final String modifiers;

        Field(String name, String type) {
            this(name, type, "");
        }

        Field(String name, String type, String modifiers) {
            this.name = name;
            this.type = type;
            this.modifiers = modifiers;
        }

        @Override
        public String toString() {
            return name + ' ' + type + ' ' + modifiers;
        }
    }

    static class Queries {
        static String GET_SHIFT = "SELECT * FROM " + TABLE_SHIFTS.name +
                " WHERE " + COL_ID.name + " = ? ";

        static String GET_SHIFTS_BETWEEN = "SELECT * FROM " + TABLE_SHIFTS.name +
                " WHERE " + COL_START_TIME.name + " >= ? AND " + COL_START_TIME.name + " <= ?" +
                " ORDER BY " + COL_START_TIME.name + " ASC";

        static String GET_TEMPLATE_SHIFTS = "SELECT * FROM " + TABLE_SHIFTS.name +
                " WHERE " + COL_IS_TEMPLATE.name + " > 0";

        static String DELETE_SHIFT_CLAUSE = COL_ID.name + " = ? ";
    }
}
