package com.dgsd.android.shifttracker.util;

import com.dgsd.android.shifttracker.STTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(STTestRunner.class)
public class EnumUtilsTest {

    private enum TestEnum {
        TEST
    }

    @Test
    public void testConstructor() {
        new EnumUtils(); // For code coverage..
    }

    @Test
    public void testReturnsCorrectEnumForOrdinal() {
        assertThat(EnumUtils.from(TestEnum.class, 0)).isEqualTo(TestEnum.TEST);
    }

    @Test
    public void testReturnsCorrectEnumForName() {
        assertThat(EnumUtils.from(TestEnum.class, "TEST")).isEqualTo(TestEnum.TEST);
    }

    @Test
    public void testReturnsCorrectEnumForLowerCaseName() {
        assertThat(EnumUtils.from(TestEnum.class, "test")).isEqualTo(TestEnum.TEST);
    }

    @Test
    public void testReturnsCorrectEnumForMixedCaseaseName() {
        assertThat(EnumUtils.from(TestEnum.class, "tEsT")).isEqualTo(TestEnum.TEST);
    }

    @Test
    public void testReturnsNullForIncorrectOrdinal() {
        assertThat(EnumUtils.from(TestEnum.class, 9)).isNull();
    }

    @Test
    public void testReturnsNullForIncorrectName() {
        assertThat(EnumUtils.from(TestEnum.class, "rubbish")).isNull();
    }

    @Test
    public void testReturnsNullForNullName() {
        assertThat(EnumUtils.from(TestEnum.class, null)).isNull();
    }
}