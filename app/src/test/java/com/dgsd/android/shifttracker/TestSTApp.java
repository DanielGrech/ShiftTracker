package com.dgsd.android.shifttracker;

/**
 * App class used to run Robolectric tests.
 */
@SuppressWarnings("unused")
public class TestSTApp extends STApp {

    @Override
    void enableDebugTools() {
        // Not whilst running tests
    }

    @Override
    void enableAppOnlyFunctionality() {
        // Not whilst running tests
    }
}