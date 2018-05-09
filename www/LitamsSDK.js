function litamsSDK() {
	this.scan = function (successCallback, errorCallback, fakeResult) {
		if (fakeResult == null)
			fakeResult = '';
		cordova.exec(successCallback, errorCallback, 'LitamsSDK', 'scan', [fakeResult]);
	};
}

window.litamsSDK = new litamsSDK();