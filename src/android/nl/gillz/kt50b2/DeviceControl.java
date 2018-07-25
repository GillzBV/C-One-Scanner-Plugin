package nl.gillz.kt50b2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DeviceControl {

	private BufferedWriter CtrlFile;

	public DeviceControl(String path) throws IOException {
		File DeviceName = new File(path);
		CtrlFile = new BufferedWriter(new FileWriter(DeviceName, false));
	}

	public void PowerOnDevice() throws IOException {
		CtrlFile.write("-wdout94 1");
		CtrlFile.flush();
	}

	public void PowerOffDevice() throws IOException {
		CtrlFile.write("-wdout94 0");
		CtrlFile.flush();
	}

	public void DeviceClose() throws IOException {
		CtrlFile.close();
	}
}