package com.knockfish.utils;

public class FileUtil {
    public static String formatFileSize(Long size) {
        if (size == null) return "0 KB";

        double sizeInKB = size / 1024.0;
        if (sizeInKB < 1024) {
            return String.format("%.2f KB", sizeInKB);
        } else {
            double sizeInMB = sizeInKB / 1024.0;
            return String.format("%.2f MB", sizeInMB);
        }
    }
}
