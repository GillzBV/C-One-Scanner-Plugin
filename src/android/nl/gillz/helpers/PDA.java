package nl.gillz.helpers;

import android.content.Context;
import android.os.CountDownTimer;
import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortTool;

import java.io.*;
import java.math.BigInteger;

public class PDA {

    private final ScannerCallback scannerCallback;
    private final Context context;

    private CountDownTimer countdownTimer;

    private SerialPortTool serialPortTool;
    private SerialPort serialPort;
    private OutputStream outputStream;
    private InputStream inputStream;
    private ReadThread readThread;

    public PDA(ScannerCallback scannerCallback, Context context) {
        this.scannerCallback = scannerCallback;
        this.context = context;
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
            outputStream = serialPort.getOutputStream();
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

    private void onDataReceived(final byte[] buffer, final int size) {
        if (size > 0) {
            byte[] id = new byte[size];

            System.arraycopy(buffer, 0, id, 0, size);
            String hex = bytesToHexString(id);
            String ASCIItoHex = convertHexToString(hex);
            String temp = rev(ASCIItoHex.substring(1, 11));
            String result = new BigInteger(temp, 16).toString(10);

            close();
            power("0");
            countdownTimer.cancel();
            scannerCallback.success(result);
        }
    }

    public String bytesToHexString(byte[] bArr) {
        StringBuffer sb = new StringBuffer(bArr.length);
        String sTmp;

        for (int i = 0; i < bArr.length; i++) {
            sTmp = Integer.toHexString(0xFF & bArr[i]);
            if (sTmp.length() < 2)
                sb.append(0);
            sb.append(sTmp.toUpperCase());
        }

        return sb.toString();
    }

    static String rev(String ox) {
        byte[] b = ox.getBytes();
        byte[] result = new byte[b.length];
        for (int i = b.length - 1, j = 0; i >= 0; i--, j++)
            result[j] = b[i];
        return new String(result);
    }

    public String convertHexToString(String hex) {

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for (int i = 0; i < hex.length() - 1; i += 2) {

            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char) decimal);

            temp.append(decimal);
        }

        return sb.toString();
    }

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();

            while (!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[64];
                    if (inputStream == null) {
                        return;
                    }
                    size = inputStream.read(buffer);
                    if (size > 0) {
                        onDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    return;
                }
            }
        }
    }
}
