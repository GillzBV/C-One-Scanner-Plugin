package nl.gillz.helpers;

import android.content.Context;
import nl.gillz.kt50b2.DeviceControl;
import nl.gillz.kt50b2.SerialNative;

public class KT50B2 {

	private final ScannerCallback scannerCallback;
	private final Context context;

	private DeviceControl deviceControl;
	private SerialNative serialNative;

	public KT50B2(ScannerCallback scannerCallback, Context context) {
		this.scannerCallback = scannerCallback;
		this.context = context;

		setupSerialNative();
		setupDeviceControl();
	}

	public void scan(Integer duration) {
		serialNative.ClearBuffer();
	}

	private void setupSerialNative() {
		serialNative = new SerialNative();

		if (serialNative.OpenComPort("/dev/ttyMT2") < 0) {
			scannerCallback.error("Scanner error occurred");
		}
	}

	private void setupDeviceControl() {
		if (serialNative != null) {
			try {
				deviceControl = new DeviceControl("/sys/class/misc/mtgpio/pin");
				deviceControl.PowerOnDevice();
			} catch (Exception exception) {
				scannerCallback.error("Scanner error occurred");
			}
		}
	}
}