package com.dgsd.shifttracker.model;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractShiftTest {

    @Test
    public void testTotalPayWithNoOvertime() {
        final Shift shift = createShift(10, 0, TimeUnit.HOURS.toMillis(1), 0, 0);
        assertThat(shift.totalPay()).isEqualTo(10);
    }

    @Test
    public void testTotalPayWithNoPayRate() {
        final Shift shift = createShift(0, 0, TimeUnit.HOURS.toMillis(1), 0, 0).withPayRate(-1);
        assertThat(shift.totalPay()).isZero();
    }

    @Test
    public void testTotalPayWithZeroPay() {
        final Shift shift = createShift(0, 0, TimeUnit.HOURS.toMillis(1), 0, 0);
        assertThat(shift.totalPay()).isZero();
    }

    @Test
    public void testTotalPayWithNoPayRateButWithOvertime() {
        final Shift shift = createShift(0, 0, 0, TimeUnit.HOURS.toMillis(1), 10).withPayRate(-1);
        assertThat(shift.totalPay()).isEqualTo(10);
    }

    @Test
    public void testTotalPayWithOvertime() {
        final Shift shift = createShift(10, 0, TimeUnit.HOURS.toMillis(1), TimeUnit.HOURS.toMillis(1), 20);
        assertThat(shift.totalPay()).isEqualTo(30);
    }

    @Test
    public void testTotalPayWithOvertimeButNoOvertimePayRate() {
        final Shift shift = createShift(10, 0, TimeUnit.HOURS.toMillis(1), TimeUnit.HOURS.toMillis(1), 0);
        assertThat(shift.totalPay()).isEqualTo(10);
    }

    @Test
    public void testTotalPayWithBreakAndNoOvertime() {
        final Shift shift = createShift(10, TimeUnit.MINUTES.toMillis(30), TimeUnit.HOURS.toMillis(1), 0, 0);
        assertThat(shift.totalPay()).isEqualTo(5);
    }

    @Test
    public void testTotalPayWithBreakAndOvertime() {
        final Shift shift = createShift(10, TimeUnit.MINUTES.toMillis(30), TimeUnit.HOURS.toMillis(1), TimeUnit.HOURS.toMillis(1), 20);
        assertThat(shift.totalPay()).isEqualTo(25);
    }

    @Test
    public void testTotalPaidDurationWithNoBreakAndNoOvertime() {
        final Shift shift = createShift(0, 0, 100, 0, 0);
        assertThat(shift.totalPaidDuration()).isEqualTo(100);
    }

    @Test
    public void testTotalPaidDurationWithNoBreakWithOvertime() {
        final Shift shift = createShift(0, 0, 100, 20, 0);
        assertThat(shift.totalPaidDuration()).isEqualTo(120);
    }

    @Test
    public void testTotalPaidDurationWithBreakAndNoOvertime() {
        final Shift shift = createShift(0, 20, 100, 0, 0);
        assertThat(shift.totalPaidDuration()).isEqualTo(80);
    }

    @Test
    public void testTotalPaidDurationWithBreakAndOvertime() {
        final Shift shift = createShift(0, 20, 100, 30, 0);
        assertThat(shift.totalPaidDuration()).isEqualTo(110);
    }

    @Test
    public void testHasReminder() {
        final Shift shift = createDefaultShift()
                .withReminderBeforeShift(TimeUnit.MINUTES.toMillis(10));
        assertThat(shift.hasReminder()).isTrue();
    }

    @Test
    public void testHasReminderWithNoReminderSet() {
        assertThat(createDefaultShift().hasReminder()).isFalse();
    }

    @Test
    public void testReminderTimeWithNoReminder() {
        assertThat(createDefaultShift().reminderTime()).isEqualTo(-1);
    }

    @Test
    public void testReminderTime() {
        final Shift shift = createDefaultShift()
                .withReminderBeforeShift(10)
                .withTimePeriod(TimePeriod.builder()
                        .startMillis(100)
                        .endMillis(200)
                        .create());
        assertThat(shift.reminderTime()).isEqualTo(90);
    }

    @Test
    public void testReminderHasPassed() {
        final long currentTime = System.currentTimeMillis();
        final Shift shift = createDefaultShift()
                .withReminderBeforeShift(100)
                .withTimePeriod(TimePeriod.builder()
                        .startMillis(currentTime - TimeUnit.MINUTES.toMillis(5))
                        .endMillis(currentTime + TimeUnit.HOURS.toMillis(1))
                        .create());

        assertThat(shift.reminderHasPassed()).isTrue();
    }

    @Test
    public void testReminderHasPassedWithNoReminderSet() {
        assertThat(createDefaultShift().reminderHasPassed()).isFalse();
    }

    @Test
    public void testReminderHasPassedWhenInFuture() {
        final long currentTime = System.currentTimeMillis();
        final Shift shift = createDefaultShift()
                .withReminderBeforeShift(100)
                .withTimePeriod(TimePeriod.builder()
                        .startMillis(currentTime + TimeUnit.MINUTES.toMillis(5))
                        .endMillis(currentTime + TimeUnit.HOURS.toMillis(1))
                        .create());

        assertThat(shift.reminderHasPassed()).isFalse();
    }

    private static Shift createDefaultShift() {
        return createShift(0, 0, 0, 0, 0);
    }

    private static Shift createShift(float payRate, long breakDuration, long regularPayDuration, long overtimeDuration, float overtimePayRate) {
        return Shift.builder()
                .payRate(payRate)
                .unpaidBreakDuration(breakDuration)
                .timePeriod(TimePeriod.builder()
                        .startMillis(0)
                        .endMillis(regularPayDuration)
                        .create())
                .overtime(overtimeDuration == 0 ? null : TimePeriod.builder()
                        .startMillis(0)
                        .endMillis(overtimeDuration)
                        .create())
                .overtimePayRate(overtimePayRate)
                .create();
    }
}