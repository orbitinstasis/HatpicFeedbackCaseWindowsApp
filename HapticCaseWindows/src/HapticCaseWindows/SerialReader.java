package HapticCaseWindows;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

/**
 * Handles the input coming from the serial port. A new line character is
 * treated as the end of a block in this example.
 */
public class SerialReader implements SerialPortEventListener {
	private InputStream in;
	Communicator communicator = null;
	
	public SerialReader(Communicator communicator, ControlPanelGui window, InputStream in) {
		this.in = in;
		this.communicator = communicator;
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
	}

	public void serialEvent(SerialPortEvent arg0) {
		int data;

		try {
			while (((data = in.read()) > -1)) {//consumerThread.isAlive() && ((data = in.read()) > -1)) {
				communicator.addToQueue(data);
			}
		} catch (IOException e) {
			communicator.logText = "Error reading serial data. (" + e.toString() + ")";
			communicator.window.txtLog.setForeground(Color.red);
			communicator.window.txtLog.append(communicator.logText + "\n");
		}
	}
}
