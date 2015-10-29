package com.dgsd.shifttracker.model;

import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ShiftWeekMapping {

    private final List<Shift> shifts;

    private LinkedHashMap<Integer, List<Shift>> mapByWeekDay;

    public ShiftWeekMapping(List<Shift> shifts) {
        this.shifts = shifts == null ? new LinkedList<Shift>() : Collections.unmodifiableList(shifts);
    }

    public LinkedHashMap<Integer, List<Shift>> getMapping() {
        if (this.mapByWeekDay == null) {
            this.mapByWeekDay = new LinkedHashMap<>();
            populateMapping();
        }

        return this.mapByWeekDay;
    }

    private void populateMapping() {
        final Calendar calendar = GregorianCalendar.getInstance();

        final Set<Shift> alreadyAssigned = new HashSet<>();
        for (int day = Calendar.SUNDAY; day <= Calendar.SATURDAY; day++) {
            final List<Shift> shiftsOnDay = new LinkedList<>();
            for (Shift s : shifts) {
                if (!alreadyAssigned.contains(s)) {
                    calendar.setTimeInMillis(s.timePeriod().startMillis());
                    final int startDay = calendar.get(Calendar.DAY_OF_WEEK);
                    if (startDay == day) {
                        shiftsOnDay.add(s);
                        alreadyAssigned.add(s);
                    }
                }
            }

            mapByWeekDay.put(day, shiftsOnDay);
        }
    }
}
