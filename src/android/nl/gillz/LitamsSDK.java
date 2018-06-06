package nl.gillz;

import android.content.Context;
import android.os.CountDownTimer;
import nl.gillz.helpers.*;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class LitamsSDK extends CordovaPlugin implements Scanner {

	private Context context;
	private CallbackContext callbackContext;
	private PluginResult pluginResult;

	private CountDownTimer countdownTimer;

	private String deviceName;
	private Sound sound;
	private Vibration vibration;

	private C4000 c4000 = null;
	private COne cOne = null;

	private Boolean isScanning = false;
	private Boolean multiScan = false;
	private List<String> results = new ArrayList<String>();
	private Integer error = 0;

	@Override
	public void initialize(CordovaInterface cordovaInterface, CordovaWebView cordovaWebView) {
		super.initialize(cordovaInterface, cordovaWebView);

		context = cordovaInterface.getActivity().getApplicationContext();

		setupCountDownTimer();

		deviceName = Device.getInstance().getDeviceName();
		sound = new Sound(context);
		vibration = new Vibration(context);
	}

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		this.callbackContext = callbackContext;

		if (action.equals("canScan")) {
			callbackContext.success(Device.getInstance().canScan());
		} else if (action.equals("scan")) {
			countdownTimer.cancel();
			this.multiScan = args.getBoolean(0);
			results.clear();
			this.scan();
		} else if (action.equals("stopScan")) {
			stop();
		} else {
			return false;
		}

		return true;
	}

	private void setupCountDownTimer() {
		countdownTimer = new CountDownTimer(1000, 500) {

			public void onTick(long millisUntilFinished) {
			}

			public void onFinish() {
				executeScan();
			}
		};
	}

	private void scan() {
		error = 0;
		isScanning = true;
		if (multiScan) {
			pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
			pluginResult.setKeepCallback(true);
			callbackContext.sendPluginResult(pluginResult);
		}

		if (deviceName.equals("C4000") && c4000 == null) {
			c4000 = new C4000(this, context);
		} else if (deviceName.equals("C-One") && cOne == null) {
			cOne = new COne(this, context);
		}

		executeScan();
	}

	private void executeScan() {
		if (c4000 != null)
			c4000.scan();

		if (cOne != null)
			cOne.scan();
	}

	@Override
	public void success(String result) {
		error = 0;
		if (multiScan && !results.contains(result)) {
			sound.play(ScanStatus.SUCCESS);
			vibration.vibrate(ScanStatus.SUCCESS);
		} else if (multiScan && results.contains(result)) {
			sound.play(ScanStatus.FAIL);
			vibration.vibrate(ScanStatus.FAIL);
		} else {
			sound.play(ScanStatus.SUCCESS);
			vibration.vibrate(ScanStatus.SUCCESS);
		}

		if (multiScan) {
			results.add(result);

			pluginResult = new PluginResult(PluginResult.Status.OK, result);
			pluginResult.setKeepCallback(true);
			callbackContext.sendPluginResult(pluginResult);

			countdownTimer.start();
		} else {
			isScanning = false;
			callbackContext.success(result);
		}
	}

	@Override
	public void error(String result) {
		if (isScanning) {
			error++;
			if (error > 5) {
				stop();
			} else {
				sound.play(ScanStatus.ERROR);
				vibration.vibrate(ScanStatus.ERROR);

				if (multiScan) {
					countdownTimer.start();
				} else {
					callbackContext.error(result);
				}
			}
		}
	}

	private void stop() {
		isScanning = false;
		countdownTimer.cancel();
		this.multiScan = false;
		results.clear();
		callbackContext.success("Scan stopped");
	}
}
