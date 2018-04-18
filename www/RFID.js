window.scan = function (successCallback, errorCallback) {
	cordova.exec(successCallback, errorCallback, 'RFID', 'scan', []);
};