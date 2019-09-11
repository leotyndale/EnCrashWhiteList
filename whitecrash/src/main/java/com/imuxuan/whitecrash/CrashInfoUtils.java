package com.imuxuan.whitecrash;

import android.os.Build;
import android.text.TextUtils;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * CrashInfoUtils
 *
 * @author Yunpeng Li
 * @since 2019-08-01
 */
class CrashInfoUtils {

    private static final String FORWARD_SLASH_REGEX = Pattern.quote("/");

    public static String getDeviceName() {
        return String.format(Locale.US, "%s/%s", removeForwardSlashesIn(Build.MANUFACTURER), removeForwardSlashesIn(Build.MODEL));
    }

    public static String getBuildVersion() {
        return removeForwardSlashesIn(Build.VERSION.RELEASE);
    }

    private static String removeForwardSlashesIn(String s) {
        if (TextUtils.isEmpty(s)) {
            return "";
        }
        return s.replaceAll(FORWARD_SLASH_REGEX, "");
    }

    public static String getThrowableName(Throwable throwable) {
        return throwable.getClass().getName();
    }

    public static <T> boolean valid(T info, T whiteListInfo) {
        if (info == null || info.equals("")) {
            return false;
        }
        if (whiteListInfo == null || whiteListInfo.equals("")) {
            return true;
        }
        return whiteListInfo.equals(info);
    }

    public static <T> boolean valid(T info, List<T> whiteListInfo) {
        if (info == null || info.equals("")) {
            return false;
        }
        if (whiteListInfo == null || whiteListInfo.isEmpty()) {
            return true;
        }
        return whiteListInfo.contains(info);
    }

    public static <T> boolean valid(Map<String, T> info, Map<String, T> whiteListInfo) {
        if (info == null || info.isEmpty()) {
            return true;
        }
        if (whiteListInfo == null || whiteListInfo.isEmpty()) {
            return true;
        }
        for (String whiteKey : whiteListInfo.keySet()) {
            T whiteValue = whiteListInfo.get(whiteKey);
            if (whiteValue == null){
                continue;
            }
            T infoValue = info.get(whiteKey);
            if (infoValue == null){
                continue;
            }
            if (whiteValue.equals(infoValue)) {
                return true;
            }
        }
        return false;
    }
}
