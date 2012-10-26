package com.dgsd.android.ShiftTracker;

import com.dgsd.android.ShiftTracker.Receiver.ShiftListWidgetProvider;
import com.dgsd.android.ShiftTracker.Service.DbService;

public class StPaidApp extends StApp implements DbService.OnDbEventListener {

    @Override
    public void onCreate() {
        super.onCreate();
        DbService.setOnDbEventListener(this);
    }

    @Override
    public void onInsert() {
        ShiftListWidgetProvider.triggerUpdate(StPaidApp.this);
    }

    @Override
    public void onDelete() {
        ShiftListWidgetProvider.triggerUpdate(StPaidApp.this);
    }

    @Override
    public void onUpdate() {
        ShiftListWidgetProvider.triggerUpdate(StPaidApp.this);
    }
}
