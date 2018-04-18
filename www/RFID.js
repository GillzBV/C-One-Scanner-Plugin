window.rfidscan = function (successCallback, errorCallback) {
	cordova.exec(successCallback, errorCallback, 'RFID', 'scan', []);
};