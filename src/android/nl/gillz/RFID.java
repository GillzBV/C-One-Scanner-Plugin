package nl.gillz;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import fr.coppernic.cpcframework.cpcagrident.Reader;
import fr.coppernic.cpcframework.cpcpowermgmt.cone.PowerMgmt;
import fr.coppernic.cpcframework.cpcpowermgmt.cone.PowerMgmt.InterfacesCone;
import fr.coppernic.cpcframework.cpcpowermgmt.cone.PowerMgmt.ManufacturersCone;
import fr.coppernic.cpcframework.cpcpowermgmt.cone.PowerMgmt.ModelsCone;
import fr.coppernic.cpcframework.cpcpowermgmt.cone.PowerMgmt.PeripheralTypesCone;
import fr.coppernic.sdk.utils.core.CpcResult;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

import java.lang.ref.WeakReference;

public class RFID extends CordovaPlugin {

	private Context context;
	private CallbackContext callbackContext;

	private Reader mReader = null;
	private boolean mIsPortOpened = false;
	private PowerMgmt mPowerMgmt = null;
	private RfidReaderHandler mHandler = null;

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);

		context = this.cordova.getActivity().getApplicationContext();
	}

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		this.callbackContext = callbackContext;
		if (action.equals("scan")) {
			// Integer num1 = args.getInt(0);

			mPowerMgmt = new PowerMgmt(context);
			mHandler = new RfidReaderHandler(context);

			mReader = new Reader(mHandler);

			this.scan();
			return true;
		}
		return false;
	}

	private void scan() {
		// callbackContext.error("Error");

		if (!mIsPortOpened) {
			if (mReader != null) {

				mPowerMgmt.setPower(PeripheralTypesCone.RfidSc, ManufacturersCone.Agrident, ModelsCone.Abr200, InterfacesCone.ExpansionPort, true);

				CpcResult.RESULT res = mReader.open("/dev/ttyHSL1", 1);

				if (res == CpcResult.RESULT.OK) {
					callbackContext.success("OPENED");
					mIsPortOpened = true;
				} else {
					callbackContext.success("FAILED");
				}
			}
		}
		callbackContext.success("ABC123ABC123AB");
	}

	private static void addLog(String s, boolean isTagCount) {

	}

	private static class RfidReaderHandler extends Handler {

		private WeakReference<Context> mWeakReference = null;

		public RfidReaderHandler(Context c) {
			mWeakReference = new WeakReference<Context>(c);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	}
}