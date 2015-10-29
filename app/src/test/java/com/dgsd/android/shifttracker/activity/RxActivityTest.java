package com.dgsd.android.shifttracker.activity;

import com.dgsd.android.shifttracker.STTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

import com.trello.rxlifecycle.ActivityEvent;
import rx.observers.TestSubscriber;

@RunWith(STTestRunner.class)
public class RxActivityTest {

    @Test
    public void testLifecycleObservable() {
        ActivityController<LameRxActivity> controller = Robolectric.buildActivity(LameRxActivity.class);

        final TestSubscriber<ActivityEvent> subscriber = new TestSubscriber<>();

        controller.get().lifecycle().subscribe(subscriber);

        controller.create()
                .start()
                .resume()
                .pause()
                .stop()
                .destroy();

        subscriber.assertValues(
                ActivityEvent.CREATE,
                ActivityEvent.START,
                ActivityEvent.RESUME,
                ActivityEvent.PAUSE,
                ActivityEvent.STOP,
                ActivityEvent.DESTROY
        );
    }

    /**
     * RxActivity is an abstract class, so we need a concrete implementation here..
     */
    public static class LameRxActivity extends RxActivity {

    }
}