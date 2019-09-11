package com.imuxuan.whitecrash;

import com.imuxuan.whitecrash.info.CrashInfo;
import com.imuxuan.whitecrash.info.DeviceInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CrashWhiteList
 *
 * @author Yunpeng Li
 * @since 2019-08-01
 */
class CrashWhiteList {

    private static List<CrashInfo> mCrashList = new ArrayList<>();
    private static Set<DeviceInfo> mDeviceList = new HashSet<>();
    private static Map<String, String> mCustomInfo = new ConcurrentHashMap<>();

    static void addWhiteCrash(CrashInfo crashInfo) {
        mCrashList.add(crashInfo);
    }

    static List<CrashInfo> getCrashList() {
        return mCrashList;
    }

    static Set<DeviceInfo> getDeviceList() {
        for (CrashInfo crashInfo : mCrashList) {
            if (crashInfo == null || crashInfo.getDeviceInfo() == null
                    || crashInfo.getDeviceInfo().isEmpty()) {
                continue;
            }
            mDeviceList.addAll(crashInfo.getDeviceInfo());
        }
        return mDeviceList;
    }

    static Map<String, String> getCustomInfo() {
        return mCustomInfo;
    }

    static void setCustomInfo(Map<String, String> customInfo) {
        CrashWhiteList.mCustomInfo = customInfo;
    }
}
