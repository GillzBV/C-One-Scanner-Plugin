package nl.gillz.helpers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import fr.coppernic.sdk.utils.core.CpcDefinitions;

public class COne {

	private final Scanner scanner;
	private final Context context;
	private static final String AGRIDENT_WEDGE = "fr.coppernic.tools.cpcagridentwedge";
	private String result = "";
	private boolean isScanning = false;
	private CountDownTimer countDownTimer;
	private BroadcastReceiver broadcastReceiver;

	public COne(Scanner scanner, Context context) {
		this.scanner = scanner;
		this.context = context;

		setupBroadcastReceiver();
		setupCountDownTimer();
	}

	public void scan() {
		if (!isScanning) {
			if (!isAppInstalled(context, AGRIDENT_WEDGE)) {
				scanner.error(AGRIDENT_WEDGE + " is not installed on the device");
				return;
			}

			Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(AGRIDENT_WEDGE);
			if (launchIntent != null) {
				result = "";
				isScanning = true;
				context.startActivity(launchIntent);
				countDownTimer.start();
			} else {
				scanner.error("Unknown error occurred");
			}
		}
	}

	private void setupBroadcastReceiver() {
		broadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction() != null && intent.getAction().equals(CpcDefinitions.ACTION_AGRIDENT_SUCCESS)) {
					result = intent.getStringExtra(CpcDefinitions.KEY_BARCODE_DATA);
					isScanning = false;
					countDownTimer.cancel();
					scanner.success(result);
				} else if (intent.getAction().equals(CpcDefinitions.ACTION_AGRIDENT_ERROR)) {
					if (isScanning)
						scanner.error("Scanner error occurred");
                } else if (intent.getAction().equals(CpcDefinitions.INTENT_ACTION_STOP_SCAN)) {
					scanner.stop();
				} else {
					scanner.error("Unknown error occurred");
				}
			}
		};

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(CpcDefinitions.ACTION_AGRIDENT_SUCCESS);
		intentFilter.addAction(CpcDefinitions.ACTION_AGRIDENT_ERROR);
        intentFilter.addAction(CpcDefinitions.INTENT_ACTION_STOP_SCAN);
        context.registerReceiver(broadcastReceiver, intentFilter);
	}

	private void setupCountDownTimer() {
		countDownTimer = new CountDownTimer(10000, 1000) {

			public void onTick(long millisUntilFinished) {
			}

			public void onFinish() {
				isScanning = false;
				if (!result.equals("")) {
					scanner.success(result);
				} else {
					scanner.error("No RFID tag found");
				}
			}
		};
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