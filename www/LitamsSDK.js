function litamsSDK() {
    this.playSound = function (successCallback, scanStatus, vibrate) {
        if (scanStatus == null)
            scanStatus = 1;

        if (vibrate == null)
            vibrate = false;

        cordova.exec(
            successCallback,
            null,
            'LitamsSDK',
            'playSound',
            [scanStatus, vibrate]
        );
    };

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

    this.scan = function (successCallback, errorCallback, multiScan, duration) {
        if (multiScan == null)
            multiScan = false;

        if (duration == null)
            duration = 15000;

        cordova.exec(
            successCallback,
            errorCallback,
            'LitamsSDK',
            'scan',
            [multiScan, duration]
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
        if (message == null)
            message = '';

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
