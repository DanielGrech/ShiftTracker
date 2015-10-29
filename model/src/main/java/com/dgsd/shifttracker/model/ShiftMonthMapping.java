package com.dgsd.shifttracker.model;

import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ShiftMonthMapping {

    private static final int MAX_DAYS_IN_MONTH = 31;

    private final List<Shift> shifts;

    private Map<Integer, List<Shift>> mapByMonthDay;

    private int[] monthDaysWithShifts;

    public ShiftMonthMapping(List<Shift> shifts) {
        this.shifts = shifts == null ? new LinkedList<Shift>() : Collections.unmodifiableList(shifts);
    }

    /**
     * @return an array of length 31 (one element for each day of the month) with a list of shifts
     * that start on that day
     */
    public Map<Integer, List<Shift>> getMapping() {
        if (this.mapByMonthDay == null) {
            this.mapByMonthDay = new HashMap<>();
            populateMapping();
        }

        return this.mapByMonthDay;
    }

    public int[] getMonthDaysWithShifts() {
        // Ensure our data is populated.
        getMapping();

        return monthDaysWithShifts;
    }

    private void populateMapping() {
        final Calendar calendar = GregorianCalendar.getInstance();

        final Set<Integer> daysWithShifts = new HashSet<>();
        final Set<Shift> alreadyAssigned = new HashSet<>();
        for (int i = 1; i <= MAX_DAYS_IN_MONTH; i++) {
            final List<Shift> shiftsOnDay = new LinkedList<>();
            for (Shift s : shifts) {
                if (!alreadyAssigned.contains(s)) {
                    calendar.setTimeInMillis(s.timePeriod().startMillis());
                    final int startDate = calendar.get(Calendar.DAY_OF_MONTH);

                    if (startDate == i) {
                        daysWithShifts.add(i);
                        shiftsOnDay.add(s);
                        alreadyAssigned.add(s);
                    }
                }
            }

            mapByMonthDay.put(i, shiftsOnDay);
        }

        this.monthDaysWithShifts = new int[daysWithShifts.size()];

        int index = 0;
        for (Integer monthDay : daysWithShifts) {
            this.monthDaysWithShifts[index++] = monthDay;
        }
    }
}
