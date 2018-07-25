package com.android.lflibs;

public class serial_native {

	private int fd;
	private int delay = 100;

	public int OpenComPort(String device) {
		fd = openport(device);
		if (fd < 0) {
			return -1;
		}
		return 0;
	}

	public void CloseComPort() {
		closeport(fd);
	}

	public byte[] ReadPort(int count) {
		return readport(fd, count, delay);
	}

	public int WritePort(byte[] buf) {
		return writeport(fd, buf);
	}

	public void ClearBuffer() {
		clearportbuf(fd);
	}

	private native int openport(String port);

	private native void closeport(int fd);

	private native byte[] readport(int fd, int count, int delay);

	private native int writeport(int fd, byte[] buf);

	private native void clearportbuf(int fd);

	static {
		System.loadLibrary("package");
		System.loadLibrary("lfrfid");
	}
}