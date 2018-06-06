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

	this.startBluetooth = function (successCallback, errorCallback) {
		cordova.exec(
			successCallback,
			errorCallback,
			'LitamsSDK',
			'startBluetooth',
			[]
		);
	};

	this.sendBluetoothMessage = function (successCallback, errorCallback) {
		cordova.exec(
			successCallback,
			errorCallback,
			'LitamsSDK',
			'sendBluetoothMessage',
			[]
		);
	};

	this.stopBluetooth = function (successCallback, errorCallback) {
		cordova.exec(
			successCallback,
			errorCallback,
			'LitamsSDK',
			'stopBluetooth',
			[]
		);
	};
}

window.litamsSDK = new litamsSDK();