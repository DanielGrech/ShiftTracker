package com.dgsd.android.shifttracker;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.FileBackupHelper;
import android.app.backup.SharedPreferencesBackupHelper;
import android.os.ParcelFileDescriptor;

import java.io.IOException;

import timber.log.Timber;

public class STBackupAgent extends BackupAgentHelper {

    private static final String BACKUP_KEY_SP = "shifttracker_shared_preferences";
    private static final String BACKUP_KEY_DB = "shifttracker_database";

    @Override
    public void onCreate() {
        Timber.d("Creating backup agent..");

        // Dirty hax .. we just hardcode our file names
        addHelper(BACKUP_KEY_SP,
                new SharedPreferencesBackupHelper(this, getPackageName() + "_preferences"));

        addHelper(BACKUP_KEY_DB, new FileBackupHelper(this, "../databases/st.db"));
    }

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {
        Timber.d("Backing up..");
        super.onBackup(oldState, data, newState);
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {
        Timber.d("Restoring..");
        super.onRestore(data, appVersionCode, newState);
    }
}
