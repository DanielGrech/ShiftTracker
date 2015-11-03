package com.dgsd.shifttracker.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class AbstractColorItemTest {

    @Test
    public void testEqualsForNullInput() {
        assertThat(ColorItem.builder()
                .color(0xFF000000)
                .description("Black")
                .create())
                .isNotEqualTo(null);
    }

    @Test
    public void testEqualsForNonColorItemInput() {
        assertThat(ColorItem.builder()
                .color(0xFF000000)
                .description("Black")
                .create())
                .isNotEqualTo(new Object());
    }

    @Test
    public void testEqualsWithSameColor() {
        final int black = 0xFF000000;
        assertThat(ColorItem.builder()
                .color(black)
                .description("Black")
                .create())
                .isEqualTo(ColorItem.builder()
                        .color(black)
                        .description("Other Black")
                        .create());
    }

    @Test
    public void testEqualsWithDifferentColor() {
        final int black = 0xFF000000;
        final int white = 0xFFFFFFFF;
        assertThat(ColorItem.builder()
                .color(black)
                .description("Black")
                .create())
                .isNotEqualTo(ColorItem.builder()
                        .color(white)
                        .description("White")
                        .create());
    }
}