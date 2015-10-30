package com.dgsd.android.shifttracker.adapter;

import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dgsd.android.shifttracker.R;
import com.dgsd.android.shifttracker.util.TimeUtils;
import com.dgsd.android.shifttracker.view.ShiftListItemView;
import com.dgsd.shifttracker.model.Shift;
import com.dgsd.shifttracker.model.ShiftWeekMapping;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.ViewHolder> {

    private static final int VIEW_TYPE_SHIFT = 0;
    private static final int VIEW_TYPE_EMPTY_DAY = 1;
    private static final int VIEW_TYPE_DAY_TITLE = 2;

    private List<ListItem> items = new ArrayList<>();

    private long weekStartMillis = -1;

    private Calendar calendar = Calendar.getInstance();

    private PublishSubject<Shift> onShiftClickedSubject = PublishSubject.create();

    private PublishSubject<Pair<View, Shift>> onShiftLongClickedSubject = PublishSubject.create();

    private PublishSubject<Integer> onEmptyDayClickedSubject = PublishSubject.create();

    @Override
    public WeekAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view;
        switch (viewType) {
            case VIEW_TYPE_SHIFT:
                view = ShiftListItemView.inflate(parent);
                break;
            case VIEW_TYPE_EMPTY_DAY:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.li_week_empty_view, parent, false);
                break;
            case VIEW_TYPE_DAY_TITLE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.li_week_day_header, parent, false);
                break;
            default:
                throw new IllegalStateException("Unknown viewtype: " + viewType);
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WeekAdapter.ViewHolder holder, int position) {
        final ListItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    public Observable<Shift> observeShiftClicked() {
        return onShiftClickedSubject.asObservable();
    }

    public Observable<Pair<View, Shift>> observeShiftLongClicked() {
        return onShiftLongClickedSubject.asObservable();
    }

    public Observable<Long> observeEmptyDayClicked() {
        return onEmptyDayClickedSubject.asObservable().map(new Func1<Integer, Long>() {
            @Override
            public Long call(Integer day) {
                return weekStartMillis + TimeUnit.DAYS.toMillis(day - 1);
            }
        });
    }

    public void setWeekStartMillis(long weekStartMillis) {
        this.weekStartMillis = weekStartMillis;
        notifyDataSetChanged();
    }

    public void setShifts(ShiftWeekMapping weekMapping) {
        items.clear();

        if (weekMapping != null) {
            final Map<Integer, List<Shift>> mapping = weekMapping.getMapping();
            int offset = 0;
            for (Map.Entry<Integer, List<Shift>> entry : mapping.entrySet()) {
                items.add(ListItem.newDayTitleItem(offset++));

                if (entry.getValue() == null || entry.getValue().isEmpty()) {
                    items.add(ListItem.newEmptyItem(entry.getKey()));
                } else {
                    for (Shift shift : entry.getValue()) {
                        items.add(ListItem.newShiftItem(shift));
                    }
                }
            }
        }

        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(ListItem item) {
            switch (getItemViewType()) {
                case VIEW_TYPE_SHIFT:
                    ((ShiftListItemView) itemView).populate(item.shift);
                    break;
                case VIEW_TYPE_EMPTY_DAY:
                    ((TextView) itemView).setText(itemView.getContext().getString(R.string.no_shifts));
                    break;
                case VIEW_TYPE_DAY_TITLE:
                    ((TextView) itemView).setText(getTitleForWeekday(item.offset));
                    break;
            }
        }

        private String getTitleForWeekday(int offset) {
            calendar.setTimeInMillis(weekStartMillis + TimeUnit.DAYS.toMillis(offset));
            return TimeUtils.formatAsDate(calendar.getTime());
        }

        @Override
        public void onClick(View v) {
            if (getItemViewType() == VIEW_TYPE_SHIFT) {
                final Shift shift = items.get(getAdapterPosition()).shift;
                onShiftClickedSubject.onNext(shift);
            } else if (getItemViewType() == VIEW_TYPE_EMPTY_DAY) {
                onEmptyDayClickedSubject.onNext(items.get(getAdapterPosition()).offset);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (getItemViewType() == VIEW_TYPE_SHIFT) {
                final Shift shift = items.get(getAdapterPosition()).shift;
                onShiftLongClickedSubject.onNext(Pair.create(v, shift));
                return true;
            }
            return false;
        }
    }

    private static class ListItem {

        final Shift shift;
        final int offset;
        final int type;

        static ListItem newShiftItem(Shift shift) {
            return new ListItem(shift, -1, VIEW_TYPE_SHIFT);
        }

        static ListItem newDayTitleItem(int offset) {
            return new ListItem(null, offset, VIEW_TYPE_DAY_TITLE);
        }

        static ListItem newEmptyItem(int offset) {
            return new ListItem(null, offset, VIEW_TYPE_EMPTY_DAY);
        }

        private ListItem(Shift shift, int offset, int type) {
            this.shift = shift;
            this.offset = offset;
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }
}
