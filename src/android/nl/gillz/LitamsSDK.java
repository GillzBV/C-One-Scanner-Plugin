package nl.gillz;

import android.content.Context;
import nl.gillz.helpers.*;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

public class LitamsSDK extends CordovaPlugin implements Scanner {

	private Context context;
	private CallbackContext callbackContext;
	private String deviceName;
	private Sound sound;
	private Vibration vibration;

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);

		context = this.cordova.getActivity().getApplicationContext();

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
		sound.play(ScanStatus.SUCCESS);
		vibration.vibrate(ScanStatus.SUCCESS);
		callbackContext.success(result);
	}

	@Override
	public void error(String result) {
		sound.play(ScanStatus.ERROR);
		vibration.vibrate(ScanStatus.ERROR);
		callbackContext.error(result);
	}
}
