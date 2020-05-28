package nl.gillz.helpers;

import android.content.Context;
import android.os.CountDownTimer;
import com.rscja.deviceapi.RFIDWithLF;
import com.rscja.deviceapi.exception.ConfigurationException;

public class C4000 {

    private final ScannerCallback scannerCallback;
    private final Context context;

    private CountDownTimer countdownTimer;

    private RFIDWithLF rfidWithLF;

    private Boolean scanning = false;

    public C4000(ScannerCallback scannerCallback, Context context) {
        this.scannerCallback = scannerCallback;
        this.context = context;

        setupRfidWithLF();
    }

    public void scan(Integer duration) {
        setupCountDownTimer(duration);
        countdownTimer.start();

        scanning = true;
        read();
    }

    private void setupRfidWithLF() {
        try {
            rfidWithLF = RFIDWithLF.getInstance();
            rfidWithLF.initWithNeedleTag();
        } catch (ConfigurationException configurationException) {
            scannerCallback.error("Scanner error occurred");
        }
    }

    private void setupCountDownTimer(Integer duration) {
        countdownTimer = new CountDownTimer(duration, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                scannerCallback.error("Scan expired");
                scanning = false;
            }
        };
    }

    private void read() {
        String result = rfidWithLF.readWithNeedleTag();

        if (result != null && !result.equals("") && !result.equals("-1")) {
            scanning = false;
            countdownTimer.cancel();
            scannerCallback.success(result);
        } else if (scanning) {
            read();
        }
    }
}
