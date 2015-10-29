package com.dgsd.android.shifttracker.util;

import android.os.Build;

import com.dgsd.android.shifttracker.STTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(STTestRunner.class)
public class ApiTest {

    @Test
    public void testConstructor() {
        // For code coverage..
        new Api();
    }

    @Config(sdk = Build.VERSION_CODES.KITKAT)
    @Test
    public void testIsFunctionalityWhenNotMatching() {
        assertThat(Api.is(Api.LOLLIPOP)).isFalse();
    }

    @Config(sdk = Build.VERSION_CODES.KITKAT)
    @Test
    public void testIsFunctionalityForKitkat() {
        assertThat(Api.is(Api.KITKAT)).isTrue();
    }

    @Config(sdk = Build.VERSION_CODES.LOLLIPOP)
    @Test
    public void testIsFunctionalityForLollipop() {
        assertThat(Api.is(Api.LOLLIPOP)).isTrue();
    }

    @Config(sdk = Build.VERSION_CODES.KITKAT)
    @Test
    public void testIsMinWhenNotMeetingApi() {
        assertThat(Api.isMin(Api.LOLLIPOP)).isFalse();
    }

    @Config(sdk = Build.VERSION_CODES.LOLLIPOP)
    @Test
    public void testIsMinWhenAboveApi() {
        assertThat(Api.isMin(Api.KITKAT)).isTrue();
    }

    @Config(sdk = Build.VERSION_CODES.KITKAT)
    @Test
    public void testIsMinWhenOnSameApi() {
        assertThat(Api.isMin(Api.KITKAT)).isTrue();
    }

    @Config(sdk = Build.VERSION_CODES.KITKAT)
    @Test
    public void testIsUpToOnSameApi() {
        assertThat(Api.isUpTo(Api.KITKAT)).isTrue();
    }


    @Config(sdk = Build.VERSION_CODES.LOLLIPOP)
    @Test
    public void testIsUpToOnLowerApi() {
        assertThat(Api.isUpTo(Api.KITKAT)).isFalse();
    }

    @Config(sdk = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Test
    public void testIsUpToOnHigherApi() {
        assertThat(Api.isUpTo(Api.KITKAT)).isTrue();
    }
}
