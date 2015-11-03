package com.dgsd.shifttracker.data;

import android.database.sqlite.SQLiteDatabase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class DbOpenHelperTest {

    @Test
    public void testOnCreateExecutesCreateScripts() {
        final SQLiteDatabase db = mock(SQLiteDatabase.class);

        final DbOpenHelper helper = DbOpenHelper.getInstance(RuntimeEnvironment.application);
        helper.onCreate(db);
        verify(db).execSQL(DbContract.TABLE_SHIFTS.getCreateSql());
    }   
}