package nl.gillz.helpers;

import android.content.Context;
import android.media.MediaPlayer;

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
		success = MediaPlayer.create(context, context.getResources().getIdentifier("success", "raw", context.getPackageName()));
		fail = MediaPlayer.create(context, context.getResources().getIdentifier("fail", "raw", context.getPackageName()));
		error = MediaPlayer.create(context, context.getResources().getIdentifier("error", "raw", context.getPackageName()));
	}
}