package nl.gillz.helpers;

import android.content.Context;
import fr.coppernic.sdk.agrident.*;
import fr.coppernic.sdk.power.PowerManager;
import fr.coppernic.sdk.power.api.PowerListener;
import fr.coppernic.sdk.power.api.peripheral.Peripheral;
import fr.coppernic.sdk.power.impl.cone.ConePeripheral;
import fr.coppernic.sdk.utils.core.CpcBytes;
import fr.coppernic.sdk.utils.core.CpcResult;
import fr.coppernic.sdk.utils.io.InstanceListener;

import java.util.Arrays;

import static fr.coppernic.sdk.agrident.MessageType.RFID_READ_SUCCESS;

public class COne implements PowerListener, InstanceListener<Reader>, OnDataReceivedListener {

	private final ScannerCallback scannerCallback;
	private final Context context;

	private Reader reader;

	public COne(ScannerCallback scannerCallback, Context context) {
		this.scannerCallback = scannerCallback;
		this.context = context;
	}

	public void scan() {
		PowerManager.get().registerListener(this);
		ConePeripheral.RFID_AGRIDENT_ABR200_GPIO.on(context);
	}

	@Override
	public void onPowerUp(CpcResult.RESULT result, Peripheral peripheral) {
		ReaderFactory.getInstance(context, this);
	}

	@Override
	public void onPowerDown(CpcResult.RESULT result, Peripheral peripheral) {

	}

	@Override
	public void onCreated(Reader reader) {
		this.reader = reader;

		reader.setOnDataReceivedListener(this);

		CpcResult.RESULT result = reader.open("/dev/ttyHSL1", 9600);

		if (result == CpcResult.RESULT.OK) {
			result = reader.sendCommand(Commands.SET_RF_ON_CMD);
		}
	}

	@Override
	public void onDisposed(Reader reader) {

	}

	@Override
	public void onTagIdReceived(AgridentMessage agridentMessage, CpcResult.RESULT result) {
		if (agridentMessage.getMessageType().equals(RFID_READ_SUCCESS)) {
			scannerCallback.success(CpcBytes.byteArrayToUtf8String(Arrays.copyOfRange(agridentMessage.getData(), 6, 21)));

			reader.sendCommand(Commands.SET_RF_OFF_CMD);
			reader.close();
		}
	}

	@Override
	public void onFirmwareReceived(String s, CpcResult.RESULT result) {

	}

	@Override
	public void onSerialNumberReceived(String s, CpcResult.RESULT result) {

	}

	@Override
	public void onCommandAckReceived(MessageType messageType, boolean b) {

	}

	@Override
	public void onGetConfigReceived(byte b, CpcResult.RESULT result) {

	}

	@Override
	public void onGetConfigAllReceived(Parameters[] parameters, CpcResult.RESULT result) {

	}

	@Override
	public void onReaderInformationReceived(ReaderInformation readerInformation, int i) {

	}
}