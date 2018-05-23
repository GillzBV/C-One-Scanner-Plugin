package nl.gillz.helpers;

import android.content.Context;
import com.rscja.deviceapi.RFIDWithLF;
import com.rscja.deviceapi.entity.AnimalEntity;
import com.rscja.deviceapi.exception.ConfigurationException;

public class C4000 {

	private final Scanner scanner;
	private final Context context;
	private RFIDWithLF rfidWithLF;

	public C4000(Scanner scanner, Context context) {
		this.scanner = scanner;
		this.context = context;

		setupRfidWithLF();
	}

	public void scan() {
		AnimalEntity animalEntity = rfidWithLF.readAnimalTags(1);

		if (animalEntity != null) {
			scanner.success(String.valueOf(animalEntity.getNationalID()));
		} else {
			scanner.error("Scanner error occurred");
		}

		rfidWithLF.free();
	}

	private void setupRfidWithLF() {
		try {
			rfidWithLF = RFIDWithLF.getInstance();
			rfidWithLF.init();
		} catch (ConfigurationException configurationException) {
			scanner.error("Scanner error occurred");
		}
	}
}