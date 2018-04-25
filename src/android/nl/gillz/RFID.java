package nl.gillz;

import android.content.Context;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

public class RFID extends CordovaPlugin {

	private Context context;
	private CallbackContext callbackContext;

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);

		context = this.cordova.getActivity().getApplicationContext();
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
		callbackContext.success("123456789123456");
	}
}
