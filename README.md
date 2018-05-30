# Litams SDK
SDK created to implement with a Cordova application, has native Android functions designed for the Litams application.
## Functions
Functions that can be invoked with javascript and their parameters and return values.
### canScan
Used to check if a device can actually use a RFID scanner.
**Parameters**
- Function (will be used to return a boolean)
### scan
Scan a RFID tag.
**Parameters**
- Function (will be used to return a string when scanning is successful)
- Function (will be used to return a string if scanning failed)
- Boolean (provide false for a single scan, provide true for multiple scans in a row)
### stopScan
Stop a RFID multiscan
**Parameters**
- Function (will be used to return a string)
