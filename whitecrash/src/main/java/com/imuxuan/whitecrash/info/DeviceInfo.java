package com.imuxuan.whitecrash.info;

import android.text.TextUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * CrashInfo
 */
public class DeviceInfo {

    private String deviceName;
    private List<String> buildVersion = new CopyOnWriteArrayList<>();
    private Map<String, String> customInfo = new ConcurrentHashMap<>();

    public String getDeviceName() {
        return deviceName;
    }

    public DeviceInfo setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        return this;
    }

    public List<String> getBuildVersion() {
        return buildVersion;
    }

    public DeviceInfo addBuildVersion(String version) {
        buildVersion.add(version);
        return this;
    }

    public Map<String, String> getCustomInfo() {
        return customInfo;
    }

    public DeviceInfo setCustomInfo(Map<String, String> customInfo) {
        this.customInfo = customInfo;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this || !(obj instanceof DeviceInfo)){
            return true;
        }
        return TextUtils.equals(deviceName, ((DeviceInfo) obj).getDeviceName());
    }

    @Override
    public int hashCode() {
        if (deviceName == null) {
            return 0;
        }
        return deviceName.hashCode();
    }
}
