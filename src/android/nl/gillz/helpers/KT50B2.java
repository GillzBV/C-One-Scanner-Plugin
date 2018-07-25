package nl.gillz.helpers;

import android.content.Context;
import android.os.CountDownTimer;
import com.android.lflibs.DeviceControl;
import com.android.lflibs.serial_native;

public class KT50B2 {

	private final ScannerCallback scannerCallback;
	private final Context context;

	private CountDownTimer countdownTimer;

	private DeviceControl deviceControl;
	private serial_native serialNative;

	public KT50B2(ScannerCallback scannerCallback, Context context) {
		this.scannerCallback = scannerCallback;
		this.context = context;

		setupSerialNative();
		setupDeviceControl();
	}

	public void scan(Integer duration) {
		serialNative.ClearBuffer();

		setupCountDownTimer(duration);
		countdownTimer.start();
	}

	private void setupSerialNative() {
		serialNative = new serial_native();

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

	private void setupCountDownTimer(Integer duration) {
		countdownTimer = new CountDownTimer(duration, 1) {

			public void onTick(long millisUntilFinished) {
				byte[] bytes = serialNative.ReadPort(94);

				if (bytes != null) {
					String result = bytesToString(bytes);

					if (result != null && result.length() == 15) {
						scannerCallback.success(result);

						countdownTimer.cancel();
					}
				}
			}

			public void onFinish() {
				scannerCallback.error("Scan expired");
			}
		};
	}

	private String bytesToString(byte[] bytes) {
		String result = "";

		String byteString = new String(bytes);

		if (bytes.length == 30) {
			result = Long.toString(Long.parseLong(byteString.substring(13, 14) + byteString.substring(12, 13) + byteString.substring(11, 12), 16));

			String data = Long.toString(Long.parseLong(byteString.substring(10, 11) + byteString.substring(9, 10) + byteString.substring(8, 9) + byteString.substring(7, 8) + byteString.substring(6, 7) + byteString.substring(5, 6) + byteString.substring(4, 5) + byteString.substring(3, 4) + byteString.substring(2, 3) + byteString.substring(1, 2), 16));

			for (int x = data.length(); x < 12; x++) {
				result += "0";
			}

			result += data;
		} else {
			byte[] b = new byte[4];
			b[0] = (byte) (Integer.parseInt(byteString.substring(1, 3), 16) ^ Integer.parseInt(byteString.substring(3, 5), 16) ^ Integer.parseInt(byteString.substring(5, 7), 16) ^ Integer.parseInt(byteString.substring(7, 9), 16) ^ Integer.parseInt(byteString.substring(7, 9), 16) & 0xff);
			if (b[0] == bytes[11]) {
				result = byteString.substring(1, byteString.length() - 2);
			}
		}

		return result;
	}
}