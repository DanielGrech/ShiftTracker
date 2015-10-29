package com.dgsd.android.shifttracker.util;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.format.Time;

import com.dgsd.android.shifttracker.BuildConfig;
import com.dgsd.android.shifttracker.R;
import com.dgsd.shifttracker.model.Shift;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import au.com.bytecode.opencsv.CSVWriter;

import static java.lang.String.valueOf;

public class ShiftToCsvMapper {

    public static final String FILE_PROVIDER_AUTHORITY = BuildConfig.APPLICATION_ID + ".fileprovider";

    private static final String[] FILE_HEADERS = {
            "title",
            "notes",
            "pay_rate",
            "unpaid_break_in_millis",
            "color",
            "reminder_in_millis_before_start",
            "start_time",
            "end_time",
            "overtime_start_time",
            "overtime_end_time",
            "overtime_pay_rate"
    };

    private final Context context;

    public ShiftToCsvMapper(Context context) {
        this.context = context;
    }

    public Uri generateCsvFile(@NonNull Collection<Shift> shifts) throws IOException {
        final String filePath = writeCsv(shifts);
        return FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, new File(filePath));
    }

    private String writeCsv(Collection<Shift> shifts) throws IOException {
        final String filePath = getFilePath(getFileName());

        try (final CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeNext(FILE_HEADERS);
            for (Shift shift : shifts) {
                writer.writeNext(new String[]{
                        shift.title(),
                        shift.notes(),
                        shift.payRate() < 0 ? "" : valueOf(shift.payRate()),
                        shift.unpaidBreakDuration() <= 0 ? "" : valueOf(shift.unpaidBreakDuration()),
                        valueOf(shift.color()),
                        shift.reminderBeforeShift() < 0 ? "" : valueOf(shift.reminderBeforeShift()),
                        valueOf(shift.timePeriod().startMillis()),
                        valueOf(shift.timePeriod().endMillis()),
                        shift.overtime() == null ? "" : valueOf(shift.overtime().startMillis()),
                        shift.overtime() == null ? "" : valueOf(shift.overtime().endMillis()),
                        shift.overtimePayRate() < 0 ? "" : valueOf(shift.overtimePayRate()),
                });
            }
        }

        return filePath;
    }

    private String getFilePath(String fileName) throws IOException {
        File cacheFile = new File(context.getCacheDir() + File.separator + fileName);
        if (!cacheFile.exists()) {
            if (!cacheFile.createNewFile()) {
                throw new IOException("Unable to create file");
            }
        }

        return cacheFile.getAbsolutePath();
    }

    @SuppressWarnings("deprecation")
    private String getFileName() {
        final Time time = new Time();
        time.setToNow();
        return context.getString(R.string.app_name) + " export - " + time.format2445() + ".csv";
    }
}
