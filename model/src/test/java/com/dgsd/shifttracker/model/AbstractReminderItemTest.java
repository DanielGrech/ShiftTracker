package com.dgsd.shifttracker.model;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;


public class AbstractReminderItemTest {

    @Test
    public void testEqualsForNullInput() {
        assertThat(ReminderItem.builder()
                .millisBeforeShift(TimeUnit.SECONDS.toMillis(1))
                .description("1 second")
                .create())
                .isNotEqualTo(null);
    }

    @Test
    public void testEqualsForNonReminderItemInput() {
        assertThat(ReminderItem.builder()
                .millisBeforeShift(TimeUnit.SECONDS.toMillis(1))
                .description("1 second")
                .create())
                .isNotEqualTo(new Object());
    }

    @Test
    public void testEqualsWithSameColor() {
        final long oneSecond = TimeUnit.SECONDS.toMillis(1);
        assertThat(ReminderItem.builder()
                .millisBeforeShift(oneSecond)
                .description("One second")
                .create())
                .isEqualTo(ReminderItem.builder()
                        .millisBeforeShift(oneSecond)
                        .description("One thousand millis")
                        .create());
    }

    @Test
    public void testEqualsWithDifferentColor() {
        final long oneSecond = TimeUnit.SECONDS.toMillis(1);
        final long twoSeconds = TimeUnit.SECONDS.toMillis(2);
        assertThat(ReminderItem.builder()
                .millisBeforeShift(oneSecond)
                .description("One second")
                .create())
                .isNotEqualTo(ReminderItem.builder()
                        .millisBeforeShift(twoSeconds)
                        .description("Two seconds")
                        .create());
    }

    @Test
    public void testIsNoReminder() {
        final ReminderItem item = ReminderItem.builder()
                .millisBeforeShift(-1)
                .description("No reminder")
                .create();

        assertThat(item.isNoReminder()).isTrue();
    }

    @Test
    public void testIsNoReminderWhenNot() {
        final ReminderItem item = ReminderItem.builder()
                .millisBeforeShift(TimeUnit.SECONDS.toMillis(1))
                .description("1 second")
                .create();

        assertThat(item.isNoReminder()).isFalse();
    }
}