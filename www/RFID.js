window.sum = function (num1, num2, successCallback, errorCallback) {
	cordova.exec(successCallback, errorCallback, 'RFID', 'sum', [num1, num2]);
};