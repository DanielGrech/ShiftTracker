package com.dgsd.android.shifttracker.mvp.presenter;

import android.text.format.Time;

import com.dgsd.android.shifttracker.STTestRunner;
import com.dgsd.android.shifttracker.mvp.view.AddShiftMvpView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;

import java.util.Calendar;
import java.util.Date;

import static com.dgsd.android.shifttracker.util.TimeUtils.toTime;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("deprecation")
@RunWith(STTestRunner.class)
public class AddShiftPresenterTest {

    @Test
    public void testOnStartTimeChanged() {
        final AddShiftMvpView view = createView();

        AddShiftPresenter.onStartTimeChanged(view, 10, 0);

        verify(view).showStartTime(argThat(new TimeMatcher(10, 0)));
        verify(view, never()).showEndTime(any(Time.class));
    }

    @Test
    public void testOnStartTimeChangedWhenOnDifferentDayToEnd() {
        final AddShiftMvpView view = createView();

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.DAY_OF_YEAR, 1);

        when(view.getCurrentEndDate()).thenReturn(cal.getTime());

        AddShiftPresenter.onStartTimeChanged(view, 23, 0);

        verify(view).showStartTime(argThat(new TimeMatcher(23, 0)));
        verify(view, never()).showEndTime(any(Time.class));
    }

    @Test
    public void testOnStartTimeChangedWhenPushingBackEnd() {
        final AddShiftMvpView view = createView();

        when(view.getCurrentStartTime()).thenReturn(toTime(9, 0));
        when(view.getCurrentEndTime()).thenReturn(toTime(11, 0));

        AddShiftPresenter.onStartTimeChanged(view, 12, 0);

        verify(view).showStartTime(argThat(new TimeMatcher(12, 0)));
        verify(view).showEndTime(argThat(new TimeMatcher(14, 0)));
    }

    @Test
    public void testOnStartTimeChangedWhenPushingBackEndToNextDay() {
        final AddShiftMvpView view = createView();

        AddShiftPresenter.onStartTimeChanged(view, 20, 0);

        verify(view).showStartTime(argThat(new TimeMatcher(20, 0)));
        verify(view).showEndTime(argThat(new TimeMatcher(23, 59)));
    }

    @Test
    public void testOnStartTimeChangedWhenPushingBackOvertime() {
        final AddShiftMvpView view = createView();

        when(view.getCurrentStartTime()).thenReturn(toTime(9, 0));
        when(view.getCurrentEndTime()).thenReturn(toTime(11, 0));
        when(view.getCurrentOvertimeStartTime()).thenReturn(toTime(11, 0));
        when(view.getCurrentOvertimeEndTime()).thenReturn(toTime(12, 0));

        AddShiftPresenter.onStartTimeChanged(view, 13, 0);

        verify(view).showStartTime(argThat(new TimeMatcher(13, 0)));
        verify(view).showEndTime(argThat(new TimeMatcher(15, 0)));
        verify(view).showOvertimeStartTime(argThat(new TimeMatcher(15, 0)));
        verify(view).showOvertimeEndTime(argThat(new TimeMatcher(16, 0)));
    }

    @Test
    public void testOnEndTimeChanged() {
        final AddShiftMvpView view = createView();

        when(view.getCurrentOvertimeStartTime()).thenReturn(toTime(17, 30));

        AddShiftPresenter.onEndTimeChanged(view, 17, 15);

        verify(view).showEndTime(argThat(new TimeMatcher(17, 15)));
        verify(view, never()).showStartTime(any(Time.class));
        verify(view, never()).showOvertimeStartTime(any(Time.class));
    }

    @Test
    public void testOnEndTimeChangedWhenPushingBackStart() {
        final AddShiftMvpView view = createView();

        AddShiftPresenter.onEndTimeChanged(view, 8, 30);

        verify(view).showEndTime(argThat(new TimeMatcher(8, 30)));
        verify(view).showStartTime(argThat(new TimeMatcher(0, 30)));
    }

    @Test
    public void testOnEndTimeChangedWhenPushingBackStartOnDifferentDay() {
        final AddShiftMvpView view = createView();

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.DAY_OF_YEAR, 1);

        when(view.getCurrentEndDate()).thenReturn(cal.getTime());

        AddShiftPresenter.onEndTimeChanged(view, 8, 30);

        verify(view).showEndTime(argThat(new TimeMatcher(8, 30)));
        verify(view, never()).showStartTime(any(Time.class));
    }

    @Test
    public void testOnEndTimeChangedWhenPushingStartToPreviousDay() {
        final AddShiftMvpView view = createView();

        AddShiftPresenter.onEndTimeChanged(view, 4, 0);

        verify(view).showEndTime(argThat(new TimeMatcher(4, 0)));
        verify(view).showStartTime(argThat(new TimeMatcher(0, 0)));
    }

    @Test
    public void testOnEndTimeChangedWhenPushingBackOvertimeStart() {
        final AddShiftMvpView view = createView();

        when(view.getCurrentOvertimeStartTime()).thenReturn(toTime(22, 0));
        when(view.getCurrentOvertimeEndTime()).thenReturn(toTime(23, 0));

        AddShiftPresenter.onEndTimeChanged(view, 23, 0);

        verify(view).showEndTime(argThat(new TimeMatcher(23, 0)));
        verify(view).showOvertimeStartTime(argThat(new TimeMatcher(23, 0)));
        verify(view).showOvertimeEndTime(argThat(new TimeMatcher(23, 59)));
    }

    @Test
    public void testOnEndTimeChangedWhenPushingBackOvertimeStartToNextDay() {
        final AddShiftMvpView view = createView();

        AddShiftPresenter.onEndTimeChanged(view, 18, 30);

        verify(view).showEndTime(argThat(new TimeMatcher(18, 30)));
        verify(view).showOvertimeStartTime(argThat(new TimeMatcher(18, 30)));
        verify(view).showOvertimeEndTime(argThat(new TimeMatcher(19, 30)));
    }

    @Test
    public void testOnOvertimeStartTimeChangedWhenPushingBackOvertimeEnd() {
        final AddShiftMvpView view = createView();

        when(view.getCurrentOvertimeStartTime()).thenReturn(toTime(18, 0));
        when(view.getCurrentOvertimeEndTime()).thenReturn(toTime(19, 0));

        AddShiftPresenter.onOvertimeStartTimeChanged(view, 20, 0);

        verify(view).showOvertimeStartTime(argThat(new TimeMatcher(20, 0)));
        verify(view).showOvertimeEndTime(argThat(new TimeMatcher(21, 0)));
    }

    @Test
    public void testOnOvertimeStartTimeChangedWhenPushingBackOvertimeEndOnDifferentDay() {
        final AddShiftMvpView view = createView();

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.DAY_OF_YEAR, 1);

        when(view.getCurrentOvertimeEndDate()).thenReturn(cal.getTime());

        AddShiftPresenter.onOvertimeStartTimeChanged(view, 20, 0);

        verify(view).showOvertimeStartTime(argThat(new TimeMatcher(20, 0)));
        verify(view, never()).showOvertimeEndTime(any(Time.class));
    }

    @Test
    public void testOnOvertimeStartTimeChangedWhenPushingEndPastCurrentDay() {
        final AddShiftMvpView view = createView();

        when(view.getCurrentOvertimeStartTime()).thenReturn(toTime(17, 0));
        when(view.getCurrentOvertimeEndTime()).thenReturn(toTime(19, 0));

        AddShiftPresenter.onOvertimeStartTimeChanged(view, 23, 0);

        verify(view).showOvertimeStartTime(argThat(new TimeMatcher(23, 0)));
        verify(view).showOvertimeEndTime(argThat(new TimeMatcher(23, 59)));
    }

    @Test
    public void testOnOvertimeStartTimeChangedWhenPushingEnd() {
        final AddShiftMvpView view = createView();

        AddShiftPresenter.onOvertimeStartTimeChanged(view, 16, 0);

        verify(view).showOvertimeStartTime(argThat(new TimeMatcher(16, 0)));
        verify(view).showEndTime(argThat(new TimeMatcher(16, 0)));
    }

    @Test
    public void testOnOvertimeStartTimeChangedWhenPushingEndOnDifferentDay() {
        final AddShiftMvpView view = createView();

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.DAY_OF_YEAR, 1);

        when(view.getCurrentOvertimeStartDate()).thenReturn(cal.getTime());

        AddShiftPresenter.onOvertimeStartTimeChanged(view, 1, 0);

        verify(view).showOvertimeStartTime(argThat(new TimeMatcher(1, 0)));
        verify(view, never()).showEndTime(any(Time.class));
    }

    @Test
    public void testOnOvertimeStartTimeChangedWhenPushingStartAndEnd() {
        final AddShiftMvpView view = createView();

        AddShiftPresenter.onOvertimeStartTimeChanged(view, 8, 30);

        verify(view).showOvertimeStartTime(argThat(new TimeMatcher(8, 30)));
        verify(view).showEndTime(argThat(new TimeMatcher(8, 30)));
        verify(view).showStartTime(argThat(new TimeMatcher(0, 30)));
    }

    @Test
    public void testOnOvertimeStartTimeChangedWhenPushingStartBackToPreviousDay() {
        final AddShiftMvpView view = createView();

        AddShiftPresenter.onOvertimeStartTimeChanged(view, 7, 0);

        verify(view).showOvertimeStartTime(argThat(new TimeMatcher(7, 0)));
        verify(view).showEndTime(argThat(new TimeMatcher(7, 0)));
        verify(view).showStartTime(argThat(new TimeMatcher(0, 0)));
    }

    @Test
    public void testOnOvertimeEndTimeChanged() {
        final AddShiftMvpView view = createView();

        AddShiftPresenter.onOvertimeEndTimeChanged(view, 20, 0);

        verify(view).showOvertimeEndTime(argThat(new TimeMatcher(20, 0)));
        verify(view, never()).showOvertimeStartTime(any(Time.class));
    }

    @Test
    public void testOnOvertimeEndTimeChangedWhenPushingBackOvertimeStart() {
        final AddShiftMvpView view = createView();

        when(view.getCurrentOvertimeStartTime()).thenReturn(toTime(17, 30));

        AddShiftPresenter.onOvertimeEndTimeChanged(view, 17, 0);

        verify(view).showOvertimeEndTime(argThat(new TimeMatcher(17, 0)));
        verify(view).showOvertimeStartTime(argThat(new TimeMatcher(16, 30)));
    }

    @Test
    public void testOnOvertimeEndTimeChangedPushingOvertimeStartToPreviousDay() {
        final AddShiftMvpView view = createView();

        AddShiftPresenter.onOvertimeEndTimeChanged(view, 0, 30);

        verify(view).showOvertimeEndTime(argThat(new TimeMatcher(0, 30)));
        verify(view).showOvertimeStartTime(argThat(new TimeMatcher(0, 0)));
        verify(view).showStartTime(argThat(new TimeMatcher(0, 0)));
        verify(view).showEndTime(argThat(new TimeMatcher(0, 0)));
    }

    /**
     * @return An {@link AddShiftMvpView} with it's time set 9am-5pm, with overtime 5pm - 6pm
     */
    private static AddShiftMvpView createView() {
        final Date date = new Date();

        final AddShiftMvpView view = mock(AddShiftMvpView.class);

        when(view.getCurrentStartDate()).thenReturn(date);
        when(view.getCurrentEndDate()).thenReturn(date);
        when(view.getCurrentStartTime()).thenReturn(toTime(9, 0));
        when(view.getCurrentEndTime()).thenReturn(toTime(17, 0));

        when(view.isOvertimeShowing()).thenReturn(true);
        when(view.getCurrentOvertimeStartDate()).thenReturn(date);
        when(view.getCurrentOvertimeEndDate()).thenReturn(date);
        when(view.getCurrentOvertimeStartTime()).thenReturn(toTime(17, 0));
        when(view.getCurrentOvertimeEndTime()).thenReturn(toTime(18, 0));

        return view;
    }

    private class TimeMatcher implements ArgumentMatcher<Time> {

        private final int hour;
        private final int minute;

        private Time lastArg;

        private TimeMatcher(int hour, int minute) {
            this.hour = hour;
            this.minute = minute;
        }

        @Override
        public boolean matches(Object arg) {
            if (arg instanceof Time) {
                lastArg = (Time) arg;
                return lastArg.hour == this.hour && lastArg.minute == this.minute;
            }
            return false;
        }

        @Override
        public String toString() {
            final String message = String.format("Did not match. Expected hour = %s, minute = %s",
                    this.hour, this.minute);
            if (lastArg == null) {
                return message;
            } else {
                return String.format("%s. Got hour = %s, minute = %s",
                        message, lastArg.hour, lastArg.minute);
            }
        }
    }
}