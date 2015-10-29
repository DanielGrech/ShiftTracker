package com.dgsd.shifttracker.model;

import org.immutables.value.Value;

import java.io.Serializable;

@Value.Immutable(intern = true)
@ImmutableStyle
abstract class AbstractColorItem implements Serializable {

    abstract String description();

    abstract int color();

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof AbstractColorItem
                && ((AbstractColorItem) obj).color() == color();
    }
}
