package com.dgsd.android.ShiftTracker;

import android.app.backup.*;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.dgsd.android.ShiftTracker.Data.Db;

import java.io.IOException;

public class StBackupAgent extends BackupAgentHelper {
    private static final String TAG = StBackupAgent.class.getSimpleName();

    public static final String BACKUP_KEY_SP = "com.dgsd.android.ShiftTracker.shared_preferences";
    public static final String BACKUP_KEY_DB = "com.dgsd.android.ShiftTracker.database";

    @Override
    public void onCreate() {
        if(BuildConfig.DEBUG)
            Log.i(TAG, "Creating backup agent..");

        SharedPreferencesBackupHelper spHelper = new SharedPreferencesBackupHelper(this, Const.SHARED_PREFS_NAME);
        addHelper(BACKUP_KEY_SP, spHelper);

        FileBackupHelper dbHelper = new FileBackupHelper(this, "../databases/" + Db.DB_NAME);
        addHelper(BACKUP_KEY_DB, dbHelper);
    }

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {
        if(BuildConfig.DEBUG)
            Log.i(TAG, "Backing up..");
        synchronized (Db.LOCK) {
            super.onBackup(oldState, data, newState);
        }
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {
        if(BuildConfig.DEBUG)
            Log.i(TAG, "Restoring..");
        synchronized (Db.LOCK) {
            super.onRestore(data, appVersionCode, newState);
        }
    }
}
