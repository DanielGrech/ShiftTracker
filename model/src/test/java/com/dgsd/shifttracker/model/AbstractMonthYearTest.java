package com.dgsd.shifttracker.model;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractMonthYearTest {

    @Test
    public void testStartMillis() {
        final MonthYear item = MonthYear.builder()
                .month(Calendar.MAY)
                .year(2015)
                .create();

        assertThat(item.startMillis()).isEqualTo(1430402400000L);
    }

    @Test
    public void testEndMillis() {
        final MonthYear item = MonthYear.builder()
                .month(Calendar.MAY)
                .year(2015)
                .create();

        assertThat(item.endMillis()).isEqualTo(1433080799000L);
    }

    @Test
    public void testIsCurrentWhenDifferentYear() {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        final MonthYear item = MonthYear.builder()
                .month(cal.get(Calendar.MONTH))
                .year(cal.get(Calendar.YEAR) + 1)
                .create();

        assertThat(item.isCurrent()).isFalse();
    }

    @Test
    public void testIsCurrentWhenDifferentMonth() {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        final MonthYear item = MonthYear.builder()
                .month(cal.get(Calendar.MONTH) + 1)
                .year(cal.get(Calendar.YEAR))
                .create();

        assertThat(item.isCurrent()).isFalse();
    }

    @Test
    public void testIsCurrent() {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        final MonthYear item = MonthYear.builder()
                .month(cal.get(Calendar.MONTH))
                .year(cal.get(Calendar.YEAR))
                .create();

        assertThat(item.isCurrent()).isTrue();
    }
}