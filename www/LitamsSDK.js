function litamsSDK() {
	this.canScan = function (successCallback) {
		cordova.exec(
			successCallback,
			null,
			'LitamsSDK',
			'canScan',
			[]
		);
	};

	this.isScanning = function (successCallback) {
		cordova.exec(
			successCallback,
			null,
			'LitamsSDK',
			'isScanning',
			[]
		);
	};

	this.scan = function (successCallback, errorCallback, multiScan) {
		if (multiScan == null)
			multiScan = false;
		cordova.exec(
			successCallback,
			errorCallback,
			'LitamsSDK',
			'scan',
			[multiScan]
		);
	};

	this.stopScan = function (successCallback) {
		cordova.exec(
			successCallback,
			null,
			'LitamsSDK',
			'stopScan',
			[]
		);
	};

	this.startBluetooth = function (successCallback) {
		cordova.exec(
			successCallback,
			null,
			'LitamsSDK',
			'startBluetooth',
			[]
		);
	};

	this.sendBluetoothMessage = function (successCallback, message) {
		if (message != null)
			cordova.exec(
				successCallback,
				null,
				'LitamsSDK',
				'sendBluetoothMessage',
				[message]
			);
	};

	this.stopBluetooth = function (successCallback) {
		cordova.exec(
			successCallback,
			null,
			'LitamsSDK',
			'stopBluetooth',
			[]
		);
	};
}

window.litamsSDK = new litamsSDK();