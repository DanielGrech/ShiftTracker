package com.dgsd.shifttracker.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class ShiftWeekMapping {

    private final List<Shift> shifts;

    private final List<Integer> dayOrder;

    private Map<Integer, List<Shift>> mapByWeekDay;

    public ShiftWeekMapping(int startDayOfWeek, List<Shift> shifts) {
        this.shifts = shifts == null ? new LinkedList<Shift>() : Collections.unmodifiableList(shifts);

        this.dayOrder = new ArrayList<>(7);
        for (int i = startDayOfWeek; i <= 6; i++) {
            this.dayOrder.add(i);
        }
        for (int i = 0; i < startDayOfWeek; i++) {
            this.dayOrder.add(i);
        }
    }

    public Map<Integer, List<Shift>> getMapping() {
        if (this.mapByWeekDay == null) {
            this.mapByWeekDay = new TreeMap<>();
            populateMapping();
        }

        return this.mapByWeekDay;
    }

    private void populateMapping() {
        final Map<Integer, List<Shift>> map = new HashMap<>();
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

            map.put(convertCalendarWeekDay(day), shiftsOnDay);
        }

        mapByWeekDay = new TreeMap<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return Integer.compare(dayOrder.indexOf(lhs), dayOrder.indexOf(rhs));
            }
        });
        mapByWeekDay.putAll(map);
    }

    private static int convertCalendarWeekDay(int day) {
        int proposedDay = day - 2;
        if (proposedDay < 0) {
            proposedDay = 6; //Sunday
        }

        return proposedDay;
    }
}
