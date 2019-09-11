package com.imuxuan.crashwhitelist;

import android.os.Build;

import com.imuxuan.whitecrash.WhiteCrash;
import com.imuxuan.whitecrash.info.CrashInfo;
import com.imuxuan.whitecrash.info.DeviceInfo;

import java.util.HashMap;

/**
 * CrashWhiteListManager
 *
 * @author Yunpeng Li
 * @since 2019-08-01
 */
public class CrashWhiteListManager {

    private static final String OPPO_R9PLUSTMA = "OPPO/R9PlustmA";
    private static final String OPPO_R9PLUS = "OPPO/R9 Plus";

    private static final String VIVO_X3L = "vivo/vivo X3L";
    private static final String VIVO_X3F = "vivo/vivo X3F";

    private static final String INFO_BRAND = "BRAND";

    public static void start() {
        HashMap<String, String> deviceCustomInfo = new HashMap<>();
        deviceCustomInfo.put(INFO_BRAND, Build.BRAND);

        HashMap<String, String> customInfo = new HashMap<>();
        customInfo.put(INFO_BRAND, Build.BRAND);

        WhiteCrash.get().catchCrash(getMessageNPE()
                .addDeviceInfo(new DeviceInfo().setDeviceName(OPPO_R9PLUSTMA).addBuildVersion("5.1.1"))
                .addDeviceInfo(new DeviceInfo().setDeviceName(OPPO_R9PLUS).addBuildVersion("5.1.1"))
        ).catchCrash(getRemoteServiceException()
                .addDeviceInfo(new DeviceInfo().setDeviceName(VIVO_X3L).addBuildVersion("4.3"))
                .addDeviceInfo(new DeviceInfo().setDeviceName(VIVO_X3F).addBuildVersion("4.3").setCustomInfo(customInfo))
        ).catchCrash(getTestException()
                .addDeviceInfo(new DeviceInfo().setDeviceName(WhiteCrash.getDeviceName())
                        .addBuildVersion(WhiteCrash.getBuildVersion())
                )
        ).setCustomInfo(deviceCustomInfo).start();
    }

    public static void stop() {
        WhiteCrash.get().stop();
    }

    /**
     * OPPO R9 Message日志打印崩溃，ROM bug
     */
    private static CrashInfo getMessageNPE() {
        return new CrashInfo().setExceptionName("NullPointerException")
                .setExceptionInfo("Attempt to invoke virtual method " +
                        "'java.lang.Class java.lang.Object.getClass()' on a null object reference");
    }

    /**
     * Vivo 4.3 通知栏消息崩溃，ROM bug
     */
    private static CrashInfo getRemoteServiceException() {
        return new CrashInfo().setExceptionName("RemoteServiceException")
                .setExceptionInfo("Bad notification posted from package com.netease.newsreader.activity: " +
                        "Couldn't expand RemoteViews for: StatusBarNotification");
    }

    private static CrashInfo getTestException() {
        return new CrashInfo().setExceptionName(NullPointerException.class.getSimpleName());
    }
}
