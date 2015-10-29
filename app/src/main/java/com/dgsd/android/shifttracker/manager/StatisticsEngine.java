package com.dgsd.android.shifttracker.manager;

import android.util.SparseArray;

import com.dgsd.android.shifttracker.util.TimeUtils;
import com.dgsd.shifttracker.model.Shift;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class StatisticsEngine {

    public enum AverageType {
        PER_ITEM,
        DAY
    }

    final List<Shift> shifts = new LinkedList<>();

    SparseArray<List<Shift>> julianDayToShiftMapping;

    long startTime;

    long endTime;

    ShiftValueProvider earningValueProvider = new EarningsValueProvider();

    ShiftValueProvider timeWorkedValueProvider = new TimeWorkedValueProvider();

    public StatisticsEngine(List<Shift> shifts, long start, long end) {
        this.startTime = start;
        this.endTime = end;

        if (shifts != null) {
            this.shifts.addAll(shifts);
        }
    }

    public Results calculate() {
        julianDayToShiftMapping = getJulianDayToShiftListMapping();

        final Results results = new Results();

        results.shiftCount = getShiftCount();
        results.totalTimeWorked = getTotalTimeWorked();
        results.totalEarnings = getTotalEarnings();
        results.maxEarningItem = getMaxEarningShiftAmount();
        results.minEarningItem = getMinEarningShiftAmount();
        results.averageTimeWorkedByDay = getAverageTimeWorked(AverageType.DAY);
        results.averageTimeWorkedPerItem = getAverageTimeWorked(AverageType.PER_ITEM);
        results.averageEarningsByDay = getAverageEarnings(AverageType.DAY);
        results.averageEarningsPerItem = getAverageEarnings(AverageType.PER_ITEM);
        results.longestShiftDuration = getMaxShiftDuration();
        results.shortestShiftDuration = getMinShiftDuration();
        results.daysToEarningsMap = mapDayToValue(earningValueProvider);
        results.daysToTimeWorkedMap = mapDayToValue(timeWorkedValueProvider);
        return results;
    }


    private long getAverageTimeWorked(AverageType type) {
        switch (type) {
            case PER_ITEM:
                final int shiftCount = getShiftCount();
                return shiftCount == 0 ? 0 : (getTotalTimeWorked() / shiftCount);
            case DAY:
                final int daysWorked = getNumberOfDaysInPeriod();
                return daysWorked == 0 ? 0 : (getTotalTimeWorked() / daysWorked);
        }
        return 0;
    }

    private float getAverageEarnings(AverageType type) {
        switch (type) {
            case PER_ITEM:
                final int shiftCount = getShiftCount();
                return shiftCount == 0 ? 0 : (getTotalEarnings() / shiftCount);
            case DAY:
                final int daysWorked = getNumberOfDaysInPeriod();
                return daysWorked == 0 ? 0 : (getTotalEarnings() / daysWorked);
        }
        return 0;
    }

    private long getMaxShiftDuration() {
        long max = 0;
        for (Shift shift : shifts) {
            max = Math.max(shift.totalPaidDuration(), max);
        }

        return max;
    }

    private float getMaxEarningShiftAmount() {
        float max = 0;

        for (Shift shift : shifts) {
            max = Math.max(shift.totalPay(), max);
        }

        return max;
    }

    private float getMinEarningShiftAmount() {
        float min = Float.MAX_VALUE;

        for (Shift shift : shifts) {
            min = Math.min(shift.totalPay(), min);
        }

        return Float.compare(min, Float.MAX_VALUE) == 0 ? 0 : min;
    }

    private long getMinShiftDuration() {
        long min = Long.MAX_VALUE;

        for (Shift shift : shifts) {
            min = Math.min(shift.totalPaidDuration(), min);
        }

        return Long.compare(min, Long.MAX_VALUE) == 0 ? 0 : min;
    }

    private SparseArray<Float> mapDayToValue(ShiftValueProvider provider) {
        final SparseArray<Float> retval = new SparseArray<>();

        final int startJd = TimeUtils.getJulianDay(startTime);
        final int endJd = TimeUtils.getJulianDay(endTime);

        for (int i = startJd; i <= endJd; i++) {
            List<Shift> shiftsOnDay = julianDayToShiftMapping.get(i);
            retval.put(i, provider.getValue(shiftsOnDay));
        }

        return retval;
    }

    private SparseArray<List<Shift>> getJulianDayToShiftListMapping() {
        final SparseArray<List<Shift>> retval = new SparseArray<>();

        for (Shift shift : shifts) {
            final int jd = TimeUtils.getJulianDay(shift.timePeriod().startMillis());

            List<Shift> existingShifts = retval.get(jd);
            if (existingShifts == null) {
                existingShifts = new LinkedList<>();
            }

            existingShifts.add(shift);
            retval.put(jd, existingShifts);
        }

        return retval;
    }

    private int getShiftCount() {
        return shifts == null ? 0 : shifts.size();
    }

    private interface ShiftValueProvider {
        float getValue(List<Shift> shift);
    }

    private int getNumberOfDaysInPeriod() {
        return 1 + TimeUtils.daysBetween(new Date(startTime), new Date(endTime));
    }

    private float getTotalEarnings() {
        return getTotalEarnings(shifts);
    }

    private long getTotalTimeWorked() {
        return getTotalTimeWorked(shifts);
    }

    private static long getTotalTimeWorked(List<Shift> shifts) {
        long total = 0;
        for (Shift shift : shifts) {
            total += shift.totalPaidDuration();
        }

        return total;
    }

    private static float getTotalEarnings(List<Shift> shifts) {
        float total = 0.0f;
        for (Shift shift : shifts) {
            total += shift.totalPay();
        }

        return total;
    }

    private class TimeWorkedValueProvider implements ShiftValueProvider {

        @Override
        public float getValue(List<Shift> shifts) {
            return shifts == null ? 0 : getTotalTimeWorked(shifts);
        }
    }

    private class EarningsValueProvider implements ShiftValueProvider {

        @Override
        public float getValue(List<Shift> shifts) {
            return shifts == null ? 0 : getTotalEarnings(shifts);
        }
    }

    public static class Results {

        private int shiftCount;

        private long averageTimeWorkedByDay;

        private long averageTimeWorkedPerItem;

        private float averageEarningsByDay;

        private float averageEarningsPerItem;

        private float maxEarningItem;

        private float minEarningItem;

        private long totalTimeWorked;

        private float totalEarnings;

        private long longestShiftDuration;

        private long shortestShiftDuration;

        private SparseArray<Float> daysToEarningsMap = new SparseArray<>();

        private SparseArray<Float> daysToTimeWorkedMap = new SparseArray<>();

        public int getShiftCount() {
            return shiftCount;
        }

        public long getAverageTimeWorkedByDay() {
            return averageTimeWorkedByDay;
        }

        public long getAverageTimeWorkedPerItem() {
            return averageTimeWorkedPerItem;
        }

        public float getAverageEarningsByDay() {
            return averageEarningsByDay;
        }

        public float getAverageEarningsPerItem() {
            return averageEarningsPerItem;
        }

        public float getMaxEarningShiftAmount() {
            return maxEarningItem;
        }

        public float getMinEarningShiftAmount() {
            return minEarningItem;
        }

        public long getTotalTimeWorked() {
            return totalTimeWorked;
        }

        public float getTotalEarnings() {
            return totalEarnings;
        }

        public long getLongestShiftDuration() {
            return longestShiftDuration;
        }

        public long getShortestShiftDuration() {
            return shortestShiftDuration;
        }

        /**
         * For each julian day between the given start and end dates,
         * calculate earnings made from shifts worked
         *
         * @return
         */
        public SparseArray<Float> mapDaysToEarnings() {
            return daysToEarningsMap;
        }

        /**
         * For each julian day between the given start and end dates,
         * calculate earnings made from shifts worked
         *
         * @return
         */
        public SparseArray<Float> mapDaysToTimeWorked() {
            return daysToTimeWorkedMap;
        }
    }

}