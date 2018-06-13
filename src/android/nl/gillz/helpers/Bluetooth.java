package nl.gillz.helpers;


import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.CountDownTimer;
import me.aflak.bluetooth.DeviceCallback;
import me.aflak.bluetooth.DiscoveryCallback;

import java.util.List;
import java.util.UUID;

public class Bluetooth {

	private static final String[] knownDeviceNames = {"pisort-01", "Litams"};
	private static final UUID bluetoothUUID = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee");

	private final BluetoothCallback bluetoothCallback;
	private final Context context;
	private me.aflak.bluetooth.Bluetooth bluetooth;

	private CountDownTimer countdownTimer;

	private BluetoothDevice connectedBluetoothDevice;

	private String messageToSend = "";

	public Bluetooth(BluetoothCallback bluetoothCallback, Context context) {
		this.bluetoothCallback = bluetoothCallback;
		this.context = context;

		setupBluetooth();
		setupBluetoothDiscovery();
		setupBluetoothDevice();
		setupCountDownTimer();
	}

	public void setupConnection() {
		if (bluetooth.isEnabled()) {
			bluetooth.disable();
		}

		countdownTimer.start();
	}

	public void sendBluetoothMessage(String message) {
		if (!bluetooth.isConnected()) {
			setupConnection();
			messageToSend = message;
		} else {
			bluetooth.send(message);
		}
	}

	public void stopConnection() {
		if (bluetooth != null) {
			bluetooth.disconnect();
			bluetooth.disable();
			bluetooth.onStop();
			bluetoothCallback.message("Bluetooth has been stopped");
		}
	}

	private void setupBluetooth() {
		bluetooth = new me.aflak.bluetooth.Bluetooth(context, bluetoothUUID);

		bluetooth.onStart();

		bluetooth.setBluetoothCallback(new me.aflak.bluetooth.BluetoothCallback() {

			@Override
			public void onBluetoothTurningOn() {
			}

			@Override
			public void onBluetoothOn() {
				if (!bluetooth.isEnabled())
					bluetooth.enable();

				List<BluetoothDevice> devices = bluetooth.getPairedDevices();

				for (BluetoothDevice bluetoothDevice : devices) {
					if (isKnownDevice(bluetoothDevice)) {
						connectedBluetoothDevice = bluetoothDevice;
					}
				}

				if (connectedBluetoothDevice != null) {
					bluetooth.connectToDevice(connectedBluetoothDevice);
				} else {
					bluetooth.startScanning();
				}
			}

			@Override
			public void onBluetoothTurningOff() {
			}

			@Override
			public void onBluetoothOff() {
				setupConnection();
			}

			@Override
			public void onUserDeniedActivation() {
			}
		});
	}

	private void setupBluetoothDiscovery() {
		bluetooth.setDiscoveryCallback(new DiscoveryCallback() {
			@Override
			public void onDiscoveryStarted() {

			}

			@Override
			public void onDiscoveryFinished() {

			}

			@Override
			public void onDeviceFound(BluetoothDevice bluetoothDevice) {
				if (isKnownDevice(bluetoothDevice)) {
					bluetooth.pair(bluetoothDevice);
				}
			}

			@Override
			public void onDevicePaired(BluetoothDevice bluetoothDevice) {
				bluetooth.connectToDevice(bluetoothDevice);
			}

			@Override
			public void onDeviceUnpaired(BluetoothDevice bluetoothDevice) {
				setupConnection();
			}

			@Override
			public void onError(String message) {
			}
		});
	}

	private void setupBluetoothDevice() {
		bluetooth.setDeviceCallback(new DeviceCallback() {
			@Override
			public void onDeviceConnected(BluetoothDevice bluetoothDevice) {
				if (!messageToSend.equals("")) {
					bluetooth.send(messageToSend);
					messageToSend = "";
				}
			}

			@Override
			public void onDeviceDisconnected(BluetoothDevice bluetoothDevice, String message) {
				setupConnection();
			}

			@Override
			public void onMessage(String message) {
				bluetoothCallback.message(message);
			}

			@Override
			public void onError(String message) {
				setupConnection();
			}

			@Override
			public void onConnectError(BluetoothDevice bluetoothDevice, String message) {
				setupConnection();
			}
		});
	}

	private void setupCountDownTimer() {
		countdownTimer = new CountDownTimer(2000, 1000) {

			public void onTick(long millisUntilFinished) {
			}

			public void onFinish() {
				bluetooth.enable();
			}
		};
	}

	private Boolean isKnownDevice(BluetoothDevice bluetoothDevice) {
		if (bluetoothDevice.getName() == null) {
			return false;
		}

		for (String knownDeviceName : knownDeviceNames) {
			if (bluetoothDevice.getName().equals(knownDeviceName))
				return true;
		}

		return false;
	}
}