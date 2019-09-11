package com.imuxuan.whitecrash;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.imuxuan.whitecrash.info.CrashInfo;
import com.imuxuan.whitecrash.info.DeviceInfo;

import java.util.List;
import java.util.Set;

/**
 * CrashCatcher
 */
class CrashCatcher {

    public static final String TAG = "CrashCatcher";
    static boolean MATCH_ALL = false;

    private CrashCatcher() {
    }

    private static boolean isStart = false;

    static synchronized void start() {
        if (isStart) {
            return;
        }
        if (MATCH_ALL || checkDevice()) {
            Log.i(TAG, "in the deviceL list");
            isStart = true;
            startCatchLooper();
        }
    }

    private static void startCatchLooper() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (!isStart) {
                        return;
                    }
                    try {
                        Looper.loop();
                    } catch (Throwable e) {
                        Log.i(TAG, "loop crash:" + e);
                        if (!isStart || !checkThrowableHit(e)) {
                            throw e;
                        }
                    }
                }
            }
        });
    }

    static synchronized void stop() {
        isStart = false;
    }

    private static boolean checkDevice() {
        String deviceName = CrashInfoUtils.getDeviceName();
        Log.i(TAG, "deviceName: " + deviceName);
        String buildVersion = CrashInfoUtils.getBuildVersion();
        Set<DeviceInfo> deviceList = CrashWhiteList.getDeviceList();
        if (deviceList == null || deviceList.isEmpty()) {
            return true;
        }
        for (DeviceInfo deviceInfo : deviceList) {
            if (deviceInfo == null) {
                continue;
            }
            if (CrashInfoUtils.valid(deviceName, deviceInfo.getDeviceName())
                    && CrashInfoUtils.valid(buildVersion, deviceInfo.getBuildVersion())
                    && CrashInfoUtils.valid(CrashWhiteList.getCustomInfo(), deviceInfo.getCustomInfo())) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkThrowableHit(Throwable throwable) {
        if (throwable == null || TextUtils.isEmpty(CrashInfoUtils.getThrowableName(throwable))) {
            return false;
        }
        List<CrashInfo> crashList = CrashWhiteList.getCrashList();
        if (crashList == null || crashList.isEmpty()) {
            return false;
        }
        for (int i = 0; i < crashList.size(); i++) {
            if (isThrowableHit(throwable, crashList.get(i))) {
                Log.i(TAG, "ThrowableHit: " + throwable);
                return true;
            }
        }
        return false;
    }

    private static boolean isThrowableHit(Throwable throwable, CrashInfo crashInfo) {
        if (throwable == null || crashInfo == null) {
            return false;
        }
        Log.i(TAG, "throwableName:" + CrashInfoUtils.getThrowableName(throwable) + " ,throwableMessage:" + throwable.getMessage());
        boolean containsName = true;
        if (!TextUtils.isEmpty(crashInfo.getExceptionName())) {
            containsName = CrashInfoUtils.getThrowableName(throwable).contains(crashInfo.getExceptionName());
        }
        boolean containsMessage = true;
        if (!TextUtils.isEmpty(crashInfo.getExceptionInfo())) {
            containsMessage = throwable.getMessage().contains(crashInfo.getExceptionInfo());
        }
        Log.i(TAG, "containsName:" + containsName + " ,containsMessage:" + containsMessage);
        return containsName && containsMessage;
    }
}
