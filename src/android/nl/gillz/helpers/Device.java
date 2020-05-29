package nl.gillz.helpers;

import com.jaredrummler.android.device.DeviceName;

import java.util.ArrayList;
import java.util.List;

public class Device {

    private static Device device;
    private final List<String> devices = new ArrayList<String>();

    private Device() {
        devices.add("C4000");
        devices.add("C-One");
        devices.add("KT50_B2");
        devices.add("PDA");
    }

    public static Device getInstance() {
        if (device == null) {
            device = new Device();
        }
        return device;
    }

    public Integer canScan() {
        String deviceName = DeviceName.getDeviceName();
        return (deviceName != null && devices.contains(deviceName)) ? 1 : 0;
    }

    public String getDeviceName() {
        String deviceName = DeviceName.getDeviceName();
        return deviceName != null ? deviceName : "";
    }
}
