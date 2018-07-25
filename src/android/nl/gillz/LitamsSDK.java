package nl.gillz;

import android.Manifest;
import android.content.Context;
import android.os.CountDownTimer;
import nl.gillz.helpers.*;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class LitamsSDK extends CordovaPlugin implements ScannerCallback, BluetoothCallback {

	private CordovaInterface cordovaInterface;
	private Context context;
	private CallbackContext callbackContext;
	private PluginResult pluginResult;

	private CountDownTimer countdownTimer;

	private String deviceName;
	private Sound sound;
	private Vibration vibration;

	private C4000 c4000 = null;
	private COne cOne = null;
	private Bluetooth bluetooth = null;

	private Boolean isScanning = false;
	private List<String> results = new ArrayList<String>();
	private Integer error = 0;

	private Boolean multiScan = false;
	private Integer duration = 15000;

	@Override
	public void initialize(CordovaInterface cordovaInterface, CordovaWebView cordovaWebView) {
		super.initialize(cordovaInterface, cordovaWebView);

		this.cordovaInterface = cordovaInterface;
		this.context = cordovaInterface.getActivity().getApplicationContext();

		setupCountDownTimer();

		deviceName = Device.getInstance().getDeviceName();
		sound = new Sound(context);
		vibration = new Vibration(context);

		checkPermissions();
	}

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		this.callbackContext = callbackContext;

		if (action.equals("canScan")) {
			callbackContext.success(Device.getInstance().canScan());
		} else if (action.equals("isScanning")) {
			callbackContext.success(isScanning ? 1 : 0);
		} else if (action.equals("scan")) {
			countdownTimer.cancel();
			results.clear();
			multiScan = args.getBoolean(0);
			duration = args.getInt(1);
			scan();
		} else if (action.equals("stopScan")) {
			stop();
		} else if (action.equals("startBluetooth")) {
			startBluetooth();
		} else if (action.equals("sendBluetoothMessage")) {
			sendBluetoothMessage(args.getString(0));
		} else if (action.equals("stopBluetooth")) {
			stopBluetooth();
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

	private void checkPermissions() {
		if (!cordovaInterface.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
			cordovaInterface.requestPermission(this, 1, Manifest.permission.ACCESS_COARSE_LOCATION);
		} else if (!cordovaInterface.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
			cordovaInterface.requestPermission(this, 2, Manifest.permission.ACCESS_FINE_LOCATION);
		} else if (!cordovaInterface.hasPermission(Manifest.permission.BLUETOOTH)) {
			cordovaInterface.requestPermission(this, 3, Manifest.permission.BLUETOOTH);
		} else if (!cordovaInterface.hasPermission(Manifest.permission.BLUETOOTH_ADMIN)) {
			cordovaInterface.requestPermission(this, 4, Manifest.permission.BLUETOOTH_ADMIN);
		} else if (!cordovaInterface.hasPermission(Manifest.permission.VIBRATE)) {
			cordovaInterface.requestPermission(this, 5, Manifest.permission.VIBRATE);
		} else if (!cordovaInterface.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
			cordovaInterface.requestPermission(this, 6, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		}
	}

	public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
		checkPermissions();
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
			c4000.scan(duration);

		if (cOne != null)
			cOne.scan(duration);
	}

	private void startBluetooth() {
		pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
		pluginResult.setKeepCallback(true);
		callbackContext.sendPluginResult(pluginResult);

		if (bluetooth == null) {
			bluetooth = new Bluetooth(this, context);
		}

		bluetooth.setupConnection();
	}

	private void sendBluetoothMessage(String message) {
		pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
		pluginResult.setKeepCallback(true);
		callbackContext.sendPluginResult(pluginResult);

		if (bluetooth == null) {
			bluetooth = new Bluetooth(this, context);
		}

		bluetooth.sendBluetoothMessage(message);
	}

	private void stopBluetooth() {
		if (bluetooth != null) {
			bluetooth.stopConnection();
			bluetooth = null;
		}
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
			sound.play(ScanStatus.ERROR);
			vibration.vibrate(ScanStatus.ERROR);
			if (multiScan) {
				if (error >= 2) {
					stop();
				} else {
					countdownTimer.start();
				}
			} else {
				callbackContext.error(result);
			}
		}
	}

	private void stop() {
		isScanning = false;
		countdownTimer.cancel();
		error = 0;
		results.clear();
		multiScan = false;
		duration = 15000;
		callbackContext.success("Scan stopped");
	}

	@Override
	public void message(String message) {
		pluginResult = new PluginResult(PluginResult.Status.OK, message);
		pluginResult.setKeepCallback(true);
		callbackContext.sendPluginResult(pluginResult);
	}

	@Override
	public void failure(String failure) {
		pluginResult = new PluginResult(PluginResult.Status.ERROR, failure);
		pluginResult.setKeepCallback(true);
		callbackContext.sendPluginResult(pluginResult);
	}
}
