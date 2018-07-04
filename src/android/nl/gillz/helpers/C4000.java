package nl.gillz.helpers;

import android.content.Context;
import com.rscja.deviceapi.RFIDWithLF;
import com.rscja.deviceapi.exception.ConfigurationException;

public class C4000 {

	private final ScannerCallback scannerCallback;
	private final Context context;
	private RFIDWithLF rfidWithLF;

	public C4000(ScannerCallback scannerCallback, Context context) {
		this.scannerCallback = scannerCallback;
		this.context = context;

		setupRfidWithLF();
	}

	public void scan() {
		String result = rfidWithLF.readWithNeedleTag();

		if (result != null && !result.equals("") && !result.equals("-1")) {
			scannerCallback.success(result);
		} else {
			scannerCallback.error("Scanner error occurred");
		}
	}

	private void setupRfidWithLF() {
		try {
			rfidWithLF = RFIDWithLF.getInstance();
			rfidWithLF.initWithNeedleTag();
		} catch (ConfigurationException configurationException) {
			scannerCallback.error("Scanner error occurred");
		}
	}
}