package com.dgsd.shifttracker.model;

import org.immutables.value.Value;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Value.Immutable
@ImmutableStyle
abstract class AbstractMonthYear implements Serializable {

    private Calendar calendar;

    abstract int month();

    abstract int year();

    public long startMillis() {
        ensureCalendar();
        calendar.set(year(), month(), 1, 0, 0, 0);

        return calendar.getTimeInMillis();
    }

    public long endMillis() {
        ensureCalendar();
        calendar.set(year(), month(), 1, 23, 59, 59);
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);

        return calendar.getTimeInMillis();
    }

    public boolean isCurrent() {
        ensureCalendar();
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar.get(Calendar.MONTH) == month() && calendar.get(Calendar.YEAR) == year();
    }

    private void ensureCalendar() {
        if (calendar == null) {
            calendar = GregorianCalendar.getInstance();
        }

        calendar.clear();
    }
}
