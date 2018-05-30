package nl.gillz.helpers;

import android.content.Context;
import android.media.MediaPlayer;
import nl.gillz.demoApp.R;

public class Sound {

	private final Context context;
	private MediaPlayer success;
	private MediaPlayer fail;
	private MediaPlayer error;

	public Sound(Context context) {
		this.context = context;

		setupMediaPlayers();
	}

	public void play(ScanStatus scanStatus) {
		switch (scanStatus) {
			case SUCCESS:
				success.start();
				break;
			case FAIL:
				fail.start();
				break;
			case ERROR:
				error.start();
				break;
		}
	}

	private void setupMediaPlayers() {
		success = MediaPlayer.create(context, R.raw.success);
		fail = MediaPlayer.create(context, R.raw.fail);
		error = MediaPlayer.create(context, R.raw.error);
	}
}