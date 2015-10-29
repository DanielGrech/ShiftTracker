package com.dgsd.android.shifttracker;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(STTestRunner.class)
public class STAppTest {

    @After
    public void teardown() {
        Mockito.validateMockitoUsage();
    }

    @Test
    public void testGetApplicationComponent() {
        // Mostly for code coverage..
        final STApp app = (STApp) RuntimeEnvironment.application;
        assertThat(app.getAppServicesComponent()).isNotNull();
    }
}
