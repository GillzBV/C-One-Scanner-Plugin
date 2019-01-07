package nl.gillz;

import android.Manifest;
import android.content.Context;
import android.os.CountDownTimer;
import nl.gillz.helpers.*;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LitamsSDK extends CordovaPlugin implements ScannerCallback, BluetoothCallback {

	private CordovaInterface cordovaInterface;
	private Context context;
	private Map<String, CallbackContext> callbackContexts = new HashMap<String, CallbackContext>();

	private CountDownTimer countdownTimer;

	private String deviceName;
	private Sound sound;
	private Vibration vibration;

	private C4000 c4000 = null;
	private COne cOne = null;
	private KT50B2 kt50B2 = null;

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
		if (action.equals("startBluetooth") || action.equals("sendBluetoothMessage") || action.equals("stopBluetooth")) {
			this.callbackContexts.put("bluetooth", callbackContext);
		} else {
			this.callbackContexts.put(action, callbackContext);
		}

		if (action.equals("playSound")) {
			Integer scanStatus = args.getInt(0);
			Boolean vibrate = args.getBoolean(1);
			switch (scanStatus) {
				case 1:
					playSound(ScanStatus.SUCCESS, vibrate);
					break;
				case 2:
					playSound(ScanStatus.FAIL, vibrate);
					break;
				case 3:
				default:
					playSound(ScanStatus.ERROR, vibrate);
					break;
			}
			PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, scanStatus);
			pluginResult.setKeepCallback(true);
			this.callbackContexts.get("playSound").sendPluginResult(pluginResult);
		} else if (action.equals("canScan")) {
			PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, Device.getInstance().canScan());
			pluginResult.setKeepCallback(true);
			this.callbackContexts.get("canScan").sendPluginResult(pluginResult);
		} else if (action.equals("isScanning")) {
			if (Device.getInstance().canScan() == 1) {
				PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, isScanning ? 1 : 0);
				pluginResult.setKeepCallback(true);
				this.callbackContexts.get("isScanning").sendPluginResult(pluginResult);
			}
		} else if (action.equals("scan")) {
			if (Device.getInstance().canScan() == 1) {
				countdownTimer.cancel();
				results.clear();
				multiScan = args.getBoolean(0);
				duration = args.getInt(1);
				scan();
			}
		} else if (action.equals("stopScan")) {
			if (Device.getInstance().canScan() == 1) {
				stop("stopScan");
			}
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
			PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
			pluginResult.setKeepCallback(true);
			this.callbackContexts.get("scan").sendPluginResult(pluginResult);
		}

		if (deviceName.equals("C4000") && c4000 == null) {
			c4000 = new C4000(this, context);
		} else if (deviceName.equals("C-One") && cOne == null) {
			cOne = new COne(this, context);
		} else if (deviceName.equals("KT50_B2") && kt50B2 == null) {
			kt50B2 = new KT50B2(this, context);
		}

		executeScan();
	}

	private void executeScan() {
		if (c4000 != null)
			c4000.scan(duration);

		if (cOne != null)
			cOne.scan(duration);

		if (kt50B2 != null)
			kt50B2.scan(duration);
	}

	private void startBluetooth() {
		PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
		pluginResult.setKeepCallback(true);
		this.callbackContexts.get("startBluetooth").sendPluginResult(pluginResult);

		if (bluetooth == null) {
			bluetooth = new Bluetooth(this, context);
		}

		bluetooth.setupConnection();
	}

	private void sendBluetoothMessage(String message) {
		PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
		pluginResult.setKeepCallback(true);
		this.callbackContexts.get("sendBluetoothMessage").sendPluginResult(pluginResult);

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
		if (isScanning) {
			error = 0;
			if (multiScan && !results.contains(result)) {
				playSound(ScanStatus.SUCCESS, true);
			} else if (multiScan && results.contains(result)) {
				playSound(ScanStatus.FAIL, true);
			} else {
				playSound(ScanStatus.SUCCESS, true);
			}

			if (multiScan) {
				results.add(result);

				PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, result);
				pluginResult.setKeepCallback(true);
				this.callbackContexts.get("scan").sendPluginResult(pluginResult);

				countdownTimer.start();
			} else {
				isScanning = false;

				PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, result);
				pluginResult.setKeepCallback(true);
				this.callbackContexts.get("scan").sendPluginResult(pluginResult);
			}
		}
	}

	@Override
	public void error(String result) {
		if (isScanning) {
			error++;
			playSound(ScanStatus.ERROR, true);
			if (multiScan) {
				if (error >= 2) {
					stop("scan");
				} else {
					countdownTimer.start();
				}
			} else {
				PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, result);
				pluginResult.setKeepCallback(true);
				this.callbackContexts.get("scan").sendPluginResult(pluginResult);
			}
		}
	}

	private void stop(String action) {
		isScanning = false;
		countdownTimer.cancel();
		error = 0;
		results.clear();
		multiScan = false;
		duration = 15000;

		PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "Scan stopped");
		pluginResult.setKeepCallback(true);
		this.callbackContexts.get(action).sendPluginResult(pluginResult);
	}

	private void playSound(ScanStatus scanStatus, Boolean vibrate) {
		sound.play(scanStatus);
		if (vibrate) {
			vibration.vibrate(scanStatus);
		}
	}

	@Override
	public void message(String message) {
		PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, message);
		pluginResult.setKeepCallback(true);
		this.callbackContexts.get("bluetooth").sendPluginResult(pluginResult);
	}

	@Override
	public void failure(String failure) {
		PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, failure);
		pluginResult.setKeepCallback(true);
		this.callbackContexts.get("bluetooth").sendPluginResult(pluginResult);
	}
}
