package nl.gillz.helpers;

import android.os.CountDownTimer;
import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortTool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

public class PDA {

    private final ScannerCallback scannerCallback;

    private CountDownTimer countdownTimer;

    private SerialPortTool serialPortTool;
    private SerialPort serialPort;
    private InputStream inputStream;
    private ReadThread readThread;

    public PDA(ScannerCallback scannerCallback) {
        this.scannerCallback = scannerCallback;
    }

    public void scan(Integer duration) {
        setupCountDownTimer(duration);
        countdownTimer.start();

        power("1");
        open();
    }

    private void setupCountDownTimer(Integer duration) {
        countdownTimer = new CountDownTimer(duration, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                close();
                power("0");
                scannerCallback.error("Scan expired");
            }
        };
    }

    private void power(String id) {
        try {
            FileWriter localFileWriterOn = new FileWriter(new File("/proc/gpiocontrol/set_uhf"));
            localFileWriterOn.write(id);
            localFileWriterOn.close();
        } catch (Exception e) {
            countdownTimer.cancel();
            scannerCallback.error("Power exception occurred");
        }
    }

    private void open() {
        if (serialPortTool == null) {
            serialPortTool = new SerialPortTool();
        }

        try {
            serialPort = serialPortTool.getSerialPort("/dev/ttyS3", 9600);
            inputStream = serialPort.getInputStream();

            if (readThread == null) {
                readThread = new ReadThread();
                readThread.start();
            }
        } catch (Exception e) {
            power("0");
            countdownTimer.cancel();
            scannerCallback.error("Open exception occurred");
        }
    }

    private void close() {
        if (readThread != null) {
            readThread.interrupt();
            readThread = null;
        }

        if (serialPortTool != null) {
            serialPortTool.closeSerialPort();
        }

        serialPort = null;
    }

    private void onDataSuccess(final byte[] buffer, final int size) {
        String result = "";

        byte[] id = new byte[size];
        System.arraycopy(buffer, 0, id, 0, size);
        String value = convertHexToString(bytesToHexString(id));

        if (value.length() > 13) {
            String bigIntegerValue = (new BigInteger(revert(value.substring(11, 14)), 16)).toString();

            for (int i = 0; i < 3 - bigIntegerValue.length(); i++) {
                result += "0";
            }

            result += bigIntegerValue;
        } else {
            result += "000";
        }

        if (value.length() > 10) {
            String bigIntegerValue = (new BigInteger(revert(value.substring(1, 11)), 16)).toString();

            for (int i = 0; i < 12 - bigIntegerValue.length(); i++) {
                result += "0";
            }

            result += bigIntegerValue;
        } else {
            result += "000000000000";
        }

        close();
        power("0");
        countdownTimer.cancel();
        scannerCallback.success(result);
    }

    private void onDataError() {
        close();
        power("0");
        countdownTimer.cancel();
        scannerCallback.error("Read data error");
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder stringBuffer = new StringBuilder(bytes.length);
        String tempString;

        for (byte byteValue : bytes) {
            tempString = Integer.toHexString(0xFF & byteValue);

            if (tempString.length() < 2) {
                stringBuffer.append(0);
            }

            stringBuffer.append(tempString.toUpperCase());
        }

        return stringBuffer.toString();
    }

    private String convertHexToString(String hex) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < hex.length() - 1; i += 2) {
            stringBuilder.append((char) Integer.parseInt(hex.substring(i, (i + 2)), 16));
        }

        return stringBuilder.toString();
    }

    private String revert(String input) {
        byte[] bytes = input.getBytes();
        byte[] output = new byte[bytes.length];

        for (int i = bytes.length - 1, j = 0; i >= 0; i--, j++) {
            output[j] = bytes[i];
        }

        return new String(output);
    }

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();

            while (!isInterrupted()) {
                try {
                    byte[] buffer = new byte[64];
                    int size = inputStream.read(buffer);

                    if (inputStream == null) {
                        return;
                    }

                    if (size > 0) {
                        onDataSuccess(buffer, size);
                    } else {
                        onDataError();
                    }
                } catch (IOException e) {
                    onDataError();
                }
            }
        }
    }
}
