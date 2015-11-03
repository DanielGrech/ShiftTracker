package com.dgsd.shifttracker.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractTimePeriodTest {

    @Test
    public void testDurationInMillis() {
        assertThat(TimePeriod.builder()
                .startMillis(50)
                .endMillis(75)
                .create()
                .durationInMillis()).isEqualTo(25);
    }

    @Test
    public void testContains() {
        assertThat(TimePeriod.builder()
                .startMillis(50)
                .endMillis(100)
                .create()
                .contains(75)).isTrue();
    }

    @Test
    public void testContainsWhenTimeIsAfterEnd() {
        assertThat(TimePeriod.builder()
                .startMillis(50)
                .endMillis(100)
                .create()
                .contains(150)).isFalse();
    }

    @Test
    public void testContainsWhenTimeIsBeforeStart() {
        assertThat(TimePeriod.builder()
                .startMillis(50)
                .endMillis(100)
                .create()
                .contains(25)).isFalse();
    }

    @Test
    public void testIsValid() {
        assertThat(TimePeriod.builder()
                .startMillis(50)
                .endMillis(100)
                .create()
                .isValid()).isTrue();
    }

    @Test
    public void testIsValidWhenEndIsBeforeStart() {
        assertThat(TimePeriod.builder()
                .startMillis(100)
                .endMillis(50)
                .create()
                .isValid()).isFalse();
    }

}