package com.dgsd.shifttracker.model;

import org.immutables.value.Value;

import java.io.Serializable;

@Value.Immutable
@ImmutableStyle
abstract class AbstractTimePeriod implements Serializable {

    abstract long startMillis();

    abstract long endMillis();

    @Value.Derived
    public long durationInMillis() {
        return endMillis() - startMillis();
    }

    public boolean contains(long time) {
        return time >= startMillis() && time <= endMillis();
    }

    public boolean isValid() {
        return startMillis() <= endMillis();
    }
}
