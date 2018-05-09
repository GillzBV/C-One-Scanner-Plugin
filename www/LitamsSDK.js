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

	this.scan = function (successCallback, errorCallback, fakeResult) {
		if (fakeResult == null)
			fakeResult = '';
		cordova.exec(
			successCallback,
			errorCallback,
			'LitamsSDK',
			'scan',
			[fakeResult]
		);
	};
}

window.litamsSDK = new litamsSDK();