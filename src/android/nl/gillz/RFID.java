package nl.gillz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import fr.coppernic.sdk.utils.core.CpcDefinitions;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

public class RFID extends CordovaPlugin {

	private Context context;
	private CallbackContext callbackContext;
	private static final String AGRIDENT_WEDGE = "fr.coppernic.tools.cpcagridentwedge";
	private String result = "";
	private boolean isScanning = false;
	private CountDownTimer countDownTimer = new CountDownTimer(2000, 1000) {

		public void onTick(long millisUntilFinished) {
		}

		public void onFinish() {
			isScanning = false;
			if (!result.equals("")) {
				callbackContext.success(result);
			} else {
				callbackContext.error("No RFID tag found");
			}
		}
	};

	private BroadcastReceiver agridentReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction() != null && intent.getAction().equals(CpcDefinitions.ACTION_AGRIDENT_SUCCESS)) {
				result = intent.getStringExtra(CpcDefinitions.KEY_BARCODE_DATA);
			} else if (intent.getAction().equals(CpcDefinitions.ACTION_AGRIDENT_ERROR)) {
				callbackContext.error("Scanner error occurred");
			} else {
				callbackContext.error("Unknown error occurred");
			}
		}
	};

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);

		context = this.cordova.getActivity().getApplicationContext();

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(CpcDefinitions.ACTION_AGRIDENT_SUCCESS);
		intentFilter.addAction(CpcDefinitions.ACTION_AGRIDENT_ERROR);
		context.registerReceiver(agridentReceiver, intentFilter);
	}

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		this.callbackContext = callbackContext;
		if (action.equals("scan")) {
			this.scan();
			return true;
		}
		return false;
	}

	private void scan() {
		if (!isScanning) {
			if (!isAppInstalled(context, AGRIDENT_WEDGE)) {
				callbackContext.error(AGRIDENT_WEDGE + " is not installed on the device");
				return;
			}

			Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(AGRIDENT_WEDGE);
			if (launchIntent != null) {
				result = "";
				isScanning = true;
				context.startActivity(launchIntent);
				countDownTimer.start();
			} else {
				callbackContext.error("Unknown error occurred");
			}
		}
	}

	private boolean isAppInstalled(Context context, String packageName) {
		try {
			context.getPackageManager().getApplicationInfo(packageName, 0);
			return true;
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
	}
}
