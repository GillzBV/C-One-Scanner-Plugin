package nl.gillz;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

public class RFID extends CordovaPlugin {

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
	}

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		if (action.equals("scan")) {
			// Integer num1 = args.getInt(0);
			this.scan(callbackContext);
			return true;
		}
		return false;
	}

	private void scan(CallbackContext callbackContext) {
		// callbackContext.error("Error");
		callbackContext.success("ABC123ABC123AB");
	}
}