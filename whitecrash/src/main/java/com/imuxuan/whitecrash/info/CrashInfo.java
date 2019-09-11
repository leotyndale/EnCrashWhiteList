package com.imuxuan.whitecrash.info;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * CrashInfo
 */
public class CrashInfo {

    private String exceptionName;
    private String exceptionInfo;
    private List<DeviceInfo> deviceInfo;

    public String getExceptionInfo() {
        return exceptionInfo;
    }

    public CrashInfo setExceptionInfo(String exceptionInfo) {
        this.exceptionInfo = exceptionInfo;
        return this;
    }

    public String getExceptionName() {
        return exceptionName;
    }

    public CrashInfo setExceptionName(String exceptionName) {
        this.exceptionName = exceptionName;
        return this;
    }

    public List<DeviceInfo> getDeviceInfo() {
        return deviceInfo;
    }

    public CrashInfo addDeviceInfo(DeviceInfo deviceInfo) {
        if (this.deviceInfo == null){
            this.deviceInfo = new CopyOnWriteArrayList<>();
        }
        this.deviceInfo.add(deviceInfo);
        return this;
    }
}
