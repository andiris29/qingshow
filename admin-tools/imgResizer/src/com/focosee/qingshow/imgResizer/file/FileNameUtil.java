package com.focosee.qingshow.imgResizer.file;

import java.util.ArrayList;

/**
 * Created by wxy325 on 5/24/15.
 */
public class FileNameUtil {

    public static String getFormatName(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        String format = fileName.substring(dotIndex + 1);
        return format;
    }

    public static String getNameWithSuffix(String fileName, String suffix) {
        String formatName = FileNameUtil.getFormatName(fileName);
        if (!checkHasSuffix(fileName, suffix, formatName)) {
            int dotIndex = fileName.lastIndexOf('.');
            String name = fileName.substring(0, dotIndex) + suffix + "." + formatName;
            return name;
        } else {
            return fileName;
        }
    }

    private static boolean checkHasSuffix(String fileName, String suffix, String formatName) {
        String suf = suffix + "." + formatName;
        return fileName.endsWith(suf);
    }

    public static boolean checkIsNormalImage(String fileName, ArrayList<String> suffixList) {
        String formatName = FileNameUtil.getFormatName(fileName);
        try {
            for (String suffix : suffixList) {
                if (checkHasSuffix(fileName, suffix, formatName)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
