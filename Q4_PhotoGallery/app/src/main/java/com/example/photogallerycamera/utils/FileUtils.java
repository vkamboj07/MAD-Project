package com.example.photogallerycamera.utils;

import androidx.documentfile.provider.DocumentFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtils {

    private FileUtils() {
        // Utility class
    }

    public static String getDisplayName(DocumentFile documentFile) {
        String name = documentFile.getName();
        return name == null ? "Unknown" : name;
    }

    public static String getReadableSize(long bytes) {
        if (bytes < 0) return "Unknown";
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "B";
        return String.format(Locale.getDefault(), "%.1f %s", bytes / Math.pow(1024, exp), pre);
    }

    public static String getFormattedDate(long timeMillis) {
        if (timeMillis <= 0) return "Unknown";
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timeMillis));
    }
}
