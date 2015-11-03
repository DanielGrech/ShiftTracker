package com.dgsd.shifttracker.model;

import org.immutables.value.Value;

import java.io.Serializable;

@Value.Immutable
@ImmutableStyle
abstract class AbstractReminderItem implements Serializable {

    abstract String description();

    abstract long millisBeforeShift();

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof AbstractReminderItem
                && ((AbstractReminderItem) obj).millisBeforeShift() == millisBeforeShift();
    }

    public boolean isNoReminder() {
        return millisBeforeShift() == -1;
    }
}
