package com.imuxuan.whitecrash;

import com.imuxuan.whitecrash.info.CrashInfo;

import java.util.Map;

/**
 * Created by Yunpeng Li on 2019-09-03.
 */
public class WhiteCrash {

    private static volatile WhiteCrash mInstance;

    private WhiteCrash() {
    }

    public static WhiteCrash get() {
        if (mInstance == null) {
            synchronized (WhiteCrash.class) {
                if (mInstance == null) {
                    mInstance = new WhiteCrash();
                }
            }
        }
        return mInstance;
    }

    public WhiteCrash catchCrash(CrashInfo crashInfo) {
        CrashWhiteList.addWhiteCrash(crashInfo);
        return this;
    }

    public WhiteCrash setCustomInfo(Map<String, String> customInfo) {
        CrashWhiteList.setCustomInfo(customInfo);
        return this;
    }

    public WhiteCrash start() {
        CrashCatcher.start();
        return this;
    }

    public WhiteCrash stop() {
        CrashCatcher.stop();
        return this;
    }

    public WhiteCrash matchAll() {
        CrashCatcher.MATCH_ALL = true;
        return this;
    }

    public static String getDeviceName() {
        return CrashInfoUtils.getDeviceName();
    }

    public static String getBuildVersion() {
        return CrashInfoUtils.getBuildVersion();
    }
}
