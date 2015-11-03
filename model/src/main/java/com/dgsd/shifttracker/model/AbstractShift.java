package com.dgsd.shifttracker.model;

import org.immutables.value.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

import static java.math.BigDecimal.valueOf;

@Value.Immutable
@ImmutableStyle
abstract class AbstractShift implements Serializable {

    abstract TimePeriod timePeriod();

    @Nullable
    abstract TimePeriod overtime();

    @Nullable
    abstract String title();

    @Nullable
    abstract String notes();

    @Value.Default
    public long id() {
        return -1;
    }

    @Value.Default
    public float payRate() {
        return -1f;
    }

    @Value.Default
    public float overtimePayRate() {
        return -1f;
    }

    @Value.Default
    public long unpaidBreakDuration() {
        return -1;
    }

    @Value.Default
    public int color() {
        return -1;
    }

    @Value.Default
    public boolean isTemplate() {
        return false;
    }

    @Value.Default
    public long reminderBeforeShift() {
        return -1;
    }

    public long totalPaidDuration() {
        final long regularDuration = timePeriod().durationInMillis() - unpaidBreakDuration();
        if (overtime() == null) {
            return regularDuration;
        } else {
            return regularDuration + overtime().durationInMillis();
        }
    }

    public float totalPay() {
        return calculatePay(payRate(), timePeriod(), unpaidBreakDuration()) + calculatePay(overtimePayRate(), overtime(), 0);
    }

    public long reminderTime() {
        return hasReminder() ? (timePeriod().startMillis() - reminderBeforeShift()) : -1;
    }

    public boolean hasReminder() {
        return reminderBeforeShift() >= 0;
    }

    public boolean reminderHasPassed() {
        return hasReminder() && (reminderTime() < System.currentTimeMillis());
    }

    static float calculatePay(float payRate, TimePeriod timePeriod, long unpaidBreakDuration) {
        if (timePeriod == null || Float.compare(payRate, 0f) < 0) {
            return 0;
        } else {
            final BigDecimal durationInMillis = valueOf(
                    timePeriod.durationInMillis() - unpaidBreakDuration);
            final BigDecimal payPerMillisecond = valueOf(payRate).divide(
                    valueOf(TimeUnit.HOURS.toMillis(1)), 20, BigDecimal.ROUND_HALF_UP);

            return durationInMillis.multiply(payPerMillisecond).setScale(2, RoundingMode.FLOOR).floatValue();
        }
    }
}
