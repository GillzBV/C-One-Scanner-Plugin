package nl.gillz;

import android.content.Context;
import nl.gillz.helpers.C4000;
import nl.gillz.helpers.COne;
import nl.gillz.helpers.Device;
import nl.gillz.helpers.Scanner;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

public class LitamsSDK extends CordovaPlugin implements Scanner {

	private Context context;
	private CallbackContext callbackContext;
	private String deviceName;

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);

		context = this.cordova.getActivity().getApplicationContext();

		deviceName = Device.getInstance().getDeviceName();
	}

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		this.callbackContext = callbackContext;

		if (action.equals("canScan")) {
			callbackContext.success(Device.getInstance().canScan());
		} else if (action.equals("scan")) {
			String fakeResult = args.getString(0);
			this.scan(fakeResult);
		} else {
			return false;
		}
		return true;
	}

	private void scan(String fakeResult) {
		if (!fakeResult.equals("")) {
			callbackContext.success(fakeResult);
		} else if (deviceName.equals("C4000")) {
			C4000 C4000 = new C4000(this, context);
			C4000.scan();
		} else if (deviceName.equals("C-One")) {
			COne cOne = new COne(this, context);
			cOne.scan();
		}
	}

	@Override
	public void success(String result) {
		callbackContext.success(result);
	}

	@Override
	public void error(String result) {
		callbackContext.error(result);
	}
}
