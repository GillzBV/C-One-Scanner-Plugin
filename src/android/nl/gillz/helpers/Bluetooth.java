package nl.gillz.helpers;


import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import me.aflak.bluetooth.DeviceCallback;
import me.aflak.bluetooth.DiscoveryCallback;

import java.util.UUID;

public class Bluetooth {

	private final BluetoothCallback bluetoothCallback;
	private final Context context;
	private me.aflak.bluetooth.Bluetooth bluetooth;

	public Bluetooth(BluetoothCallback bluetoothCallback, Context context) {
		this.bluetoothCallback = bluetoothCallback;
		this.context = context;

		setupBluetooth();
		setupBluetoothDiscovery();
		setupBluetoothDevice();

		bluetooth.startScanning();

//
//		bluetooth.send("test");
	}

	private void setupBluetooth() {
		bluetooth = new me.aflak.bluetooth.Bluetooth(context, UUID.fromString("0000111E-0000-1000-8000-00805F9B34FB"));

		bluetooth.onStart();

		if (!bluetooth.isEnabled())
			bluetooth.enable();

		bluetooth.setBluetoothCallback(new me.aflak.bluetooth.BluetoothCallback() {

			@Override
			public void onBluetoothTurningOn() {
				Log.v("test", "onBluetoothTurningOn");
			}

			@Override
			public void onBluetoothOn() {
				Log.v("test", "onBluetoothOn");
			}

			@Override
			public void onBluetoothTurningOff() {
				Log.v("test", "onBluetoothTurningOff");
			}

			@Override
			public void onBluetoothOff() {
				Log.v("test", "onBluetoothOff");
			}

			@Override
			public void onUserDeniedActivation() {
				Log.v("test", "onUserDeniedActivation");
			}
		});
	}

	private void setupBluetoothDiscovery() {
		bluetooth.setDiscoveryCallback(new DiscoveryCallback() {
			@Override
			public void onDiscoveryStarted() {
				Log.v("test", "onDiscoveryStarted");
			}

			@Override
			public void onDiscoveryFinished() {
				Log.v("test", "onDiscoveryFinished");
			}

			@Override
			public void onDeviceFound(BluetoothDevice device) {
				Log.v("test", "onDeviceFound " + device);
				if (device.getAddress().equals("B8:27:EB:6C:34:CA")) {
					bluetooth.pair(device);
				}
			}

			@Override
			public void onDevicePaired(BluetoothDevice device) {
				Log.v("test", "onDevicePaired " + device);
				bluetooth.connectToDevice(device);
			}

			@Override
			public void onDeviceUnpaired(BluetoothDevice device) {
				Log.v("test", "onDeviceUnpaired " + device);
				bluetooth.pair(device);
			}

			@Override
			public void onError(String message) {
				Log.v("test", "onError " + message);
			}
		});
	}

	private void setupBluetoothDevice() {
		bluetooth.setDeviceCallback(new DeviceCallback() {
			@Override
			public void onDeviceConnected(BluetoothDevice device) {
				Log.v("test", "onDeviceConnected " + device);
			}

			@Override
			public void onDeviceDisconnected(BluetoothDevice device, String message) {
				Log.v("test", "onDeviceDisconnected " + device + " " + message);
				bluetooth.connectToDevice(device);
			}

			@Override
			public void onMessage(String message) {
				Log.v("test", "onMessage " + message);
			}

			@Override
			public void onError(String message) {
				Log.v("test", "onError " + message);
			}

			@Override
			public void onConnectError(BluetoothDevice device, String message) {
				Log.v("test", "onConnectError " + device + " " + message);
				bluetooth.connectToDevice(device);
			}
		});
	}
}