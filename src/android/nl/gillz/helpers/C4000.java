package nl.gillz.helpers;

import android.content.Context;
import com.rscja.deviceapi.RFIDWithLF;
import com.rscja.deviceapi.entity.AnimalEntity;
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
		AnimalEntity animalEntity = rfidWithLF.readAnimalTags(1);

		if (animalEntity != null) {
			scannerCallback.success(String.valueOf(animalEntity.getNationalID()));
		} else {
			scannerCallback.error("Scanner error occurred");
		}

		rfidWithLF.free();
	}

	private void setupRfidWithLF() {
		try {
			rfidWithLF = RFIDWithLF.getInstance();
			rfidWithLF.init();
		} catch (ConfigurationException configurationException) {
			scannerCallback.error("Scanner error occurred");
		}
	}
}