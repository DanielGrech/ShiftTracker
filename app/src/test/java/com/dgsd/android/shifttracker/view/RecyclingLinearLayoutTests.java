package com.dgsd.android.shifttracker.view;

import android.view.View;
import android.widget.TextView;

import com.dgsd.android.shifttracker.STTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(STTestRunner.class)
public class RecyclingLinearLayoutTests {

    private RecyclingLinearLayout view;

    private RecyclingLinearLayout.ViewFactory<Object, TextView> textViewFactory
            = new RecyclingLinearLayout.ViewFactory<Object, TextView>() {
        @Override
        public TextView onCreateView() {
            return new TextView(RuntimeEnvironment.application);
        }

        @Override
        public void populate(TextView view, Object data, int position) {
            view.setText(data.toString());
        }
    };

    @Before
    public void setup() {
        view = new RecyclingLinearLayout(RuntimeEnvironment.application);
        assertThat(view.getChildCount()).isZero();
    }

    @Test
    public void testShowsOneViewForEachDataItem() {
        final List data = listOfSize(5);
        view.populate(data, textViewFactory);

        assertThat(view.getChildCount()).isEqualTo(data.size());
        assertHasVisibleChildCount(data.size());
    }

    @Test
    public void testHidesExcessViewsOnRepopulate() {
        List data = listOfSize(5);
        view.populate(data, textViewFactory);

        assertThat(view.getChildCount()).isEqualTo(data.size());
        assertHasVisibleChildCount(data.size());

        int originalSize = data.size();
        data = listOfSize(3);

        view.populate(data, textViewFactory);

        // Assert excess views were not destroyed..
        assertThat(view.getChildCount()).isEqualTo(originalSize);
        //.. but were hidden
        assertHasVisibleChildCount(data.size());
    }

    @Test
    public void testShowsExtraViewsOnRepopulate() {
        List data = listOfSize(5);
        view.populate(data, textViewFactory);

        assertThat(view.getChildCount()).isEqualTo(data.size());
        assertHasVisibleChildCount(data.size());

        data = listOfSize(7);
        view.populate(data, textViewFactory);

        assertThat(view.getChildCount()).isEqualTo(data.size());
        assertHasVisibleChildCount(data.size());
    }

    private void assertHasVisibleChildCount(int size) {
        for (int i = 0, childCount = view.getChildCount(); i < childCount; i++) {
            final int expectedVisibility = i < size ? View.VISIBLE : View.GONE;
            assertThat(view.getChildAt(i).getVisibility()).isEqualTo(expectedVisibility);
        }
    }

    private static List<Object> listOfSize(int size) {
        final List<Object> retval = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            retval.add(new Object());
        }
        return retval;
    }
}
