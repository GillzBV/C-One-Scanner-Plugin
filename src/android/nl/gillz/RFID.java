package nl.gillz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import fr.coppernic.sdk.utils.core.CpcDefinitions;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

public class RFID extends CordovaPlugin {

	private Context context;
	private CallbackContext callbackContext;
	private static final String AGRIDENT_WEDGE = "fr.coppernic.tools.cpcagridentwedge";
	private String result = "";

	private BroadcastReceiver agridentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Clears keyboard wedge edit text
			if (intent.getAction().equals(CpcDefinitions.ACTION_AGRIDENT_SUCCESS)) {
				// Displays data read in the intent edit text
				String dataRead = intent.getStringExtra(CpcDefinitions.KEY_BARCODE_DATA);
				context.unregisterReceiver(agridentReceiver);
				result = dataRead;
			} else if (intent.getAction().equals(CpcDefinitions.ACTION_AGRIDENT_ERROR)) {
				// Displays no data read in intent edit text
				result = "error";
			} else {
				result = "error";
			}
			callbackContext.success(result);
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
		Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(AGRIDENT_WEDGE);
		context.startActivity(launchIntent);
	}
}
