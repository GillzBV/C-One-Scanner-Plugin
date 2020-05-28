package nl.gillz.helpers;

import android.content.Context;
import android.os.Vibrator;

public class Vibration {

    private final Context context;
    private Vibrator vibrator;

    public Vibration(Context context) {
        this.context = context;

        setupVibrator();
    }

    public void vibrate(ScanStatus scanStatus) {
        long[] pattern = new long[]{0};

        switch (scanStatus) {
            case SUCCESS:
                pattern = new long[]{0, 500};
                break;
            case FAIL:
                pattern = new long[]{0, 500, 100, 500};
                break;
            case ERROR:
                pattern = new long[]{0, 1500};
                break;
        }

        vibrator.vibrate(pattern, -1);
    }

    private void setupVibrator() {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }
}
