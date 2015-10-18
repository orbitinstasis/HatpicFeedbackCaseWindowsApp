/*
	Haptic Feedback Case Java Control Panel
	Copyright (C) 2015:
         Ben Kazemi, ebaykazemi@googlemail.com

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 3
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

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
public class SerialReaderConsumer implements SerialPortEventListener {
	/*
	 * ******************************************************** GLOBALS
	 */
	private InputStream in;
	Communicator communicator = null;

	/**
	 * **************************************************************
	 * CONSTRUCTOR additionally sets thread priority to max since the producer
	 * relies on this class/thread
	 * 
	 * @param communicator
	 * @param window
	 * @param in
	 */
	public SerialReaderConsumer(Communicator communicator, ControlPanelGui window, InputStream in) {
		this.in = in;
		this.communicator = communicator;
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
	}

	/**
	 * Called for each serial event
	 * 
	 * is the producer, and adds each new integer received serially to the queue
	 * in the communicator
	 */
	public void serialEvent(SerialPortEvent arg0) {
		int data;

		try {
			while (((data = in.read()) > -1)) {// consumerThread.isAlive() &&
												// ((data = in.read()) > -1)) {
				communicator.addToQueue(data);
			}
		} catch (IOException e) {
			communicator.logText = "Error reading serial data. (" + e.toString() + ")";
			communicator.window.txtLog.setForeground(Color.red);
			communicator.window.txtLog.append(communicator.logText + "\n");
		}
	}
}