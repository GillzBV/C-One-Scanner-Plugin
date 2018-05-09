package nl.gillz.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import com.rscja.deviceapi.RFIDWithLF;
import com.rscja.deviceapi.exception.ConfigurationException;

public class C4000 {

	private final Scanner scanner;
	private final Context context;
	private RFIDWithLF rfidWithLF;

	public C4000(Scanner scanner, Context context) {
		this.scanner = scanner;
		this.context = context;
	}

	public void scan() {

	}

	private void setupRfidWithLF() {
		try {
			rfidWithLF = RFIDWithLF.getInstance();
			new InitTask().execute();
		} catch (ConfigurationException configurationException) {
			scanner.error("Invalid scanner configuration");
		}
	}

	public class InitTask extends AsyncTask<String, Integer, Boolean> {
		ProgressDialog mypDialog;

		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			return rfidWithLF.init();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			mypDialog.cancel();

			if (!result) {
				Toast.makeText(context, "init fail",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			mypDialog = new ProgressDialog(context);
			mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mypDialog.setMessage("init...");
			mypDialog.setCanceledOnTouchOutside(false);
			mypDialog.show();
		}

	}
}