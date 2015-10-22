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
/* TODO: 
 *
 *-->>> additionally have a peek when writing to the model, turn that current sensor off if you read END_MARKER
 *
 *FIX THREAD HANDLING - THEY'RE NOT WAITING AS INTENDED but they are correctly behaving as the while loops intended (the wait will remove unnecessary while loop condtion checks )
 *
 *make a listener in the model that alerts all programs that want to access sensor data, when the sensor data has changed. else don'e poll the sensor data (as it's not changing )
 *
 *move code that saves last active value (in the viosual gui) of the sensor (so oldSideSensopr = current side sensor) in the model instead of the visual gui runner 
 */

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TooManyListenersException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class Communicator
{
	/*
	 **************************************************************  GLOBALS
	 */
	public boolean isAsleep = true; // we want this in a file so other programs
	// can query it
	Controller controller = null;
	Object changingSensorLock = new Object();
	protected BlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(512);
	boolean isConsuming = false;
	// for containing the ports that will be found
	private Enumeration<?> ports = null;
	// map the port names to CommPortIdentifiers
	private HashMap<String, CommPortIdentifier> portMap = new HashMap<String, CommPortIdentifier>();
	// this is the object that contains the opened port
	private CommPortIdentifier selectedPortIdentifier = null;
	protected SerialPort serialPort = null;
	// input and output streams for sending and receiving data
	private InputStream input = null;
	private OutputStream output = null;
	// just a boolean flag that i use for enabling
	// and disabling buttons depending on whether the program
	// is connected to a serial port or not
	private boolean bConnected = false;
	// passed from main GUI
	ControlPanelGui window = null;
	// SensorSelectorGUI sensorSelector = null;
	// a string for recording what goes on in the program
	// this string is written to the GUI
	String logText = "";
	protected boolean halt = true;

	/*
	 **************************************************************  CONSTANTS
	 */
	// the timeout value for connecting with the port
	private final static int TIMEOUT = 2000;
	final static int SLEEP_BAUD_RATE = 9600;
	final static int AWAKE_BAUD_RATE = 115200;

	private final static int INIT_SLEEP_SETTING = 0;

	/**
	 * ******************************************************************** CONSTRUCTOR 
	 * @param window
	 */
	public Communicator(ControlPanelGui window) {
		this.window = window;
		controller = new Controller(window, this);
		window.sensorNumberLabel.setText("Sensors: " + controller.sensors);
	}

	/*
	 **************************************************************  METHODS 
	 */
	
	/**
	 * Adds integer 'data' to the array blocking queue 'queue'
	 * @param data
	 */
	public void addToQueue(int data) {
		queue.add(data);
	}
	
	/*
	 ************************************************************** MAIN COMMUNICATOR METHODS 
	 */
	
	/**
	 * Searches and adds all the found ports to a combo box on the GUI
	 */
	@SuppressWarnings("unchecked")
	public void searchForPorts() {
		ports = CommPortIdentifier.getPortIdentifiers();
		while (ports.hasMoreElements()) {
			CommPortIdentifier curPort = (CommPortIdentifier) ports.nextElement();
			// get only serial ports
			if (curPort.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				window.cboxPorts.addItem(curPort.getName());
				portMap.put(curPort.getName(), curPort);
			}
		}
	}

	/*
	 **************************************************************  CONNECT 
	 */
	
	/**
	 * connect to the selected port in the combo box
	 * pre: ports are already found by using the searchForPorts method
	 * post: the connected comm port is stored in commPort, otherwise an exception is generated
	 */
	public void connect() {
		String selectedPort = (String) window.cboxPorts.getSelectedItem();
		selectedPortIdentifier = (CommPortIdentifier) portMap.get(selectedPort);
		CommPort commPort = null;
		halt = true;
		try {
			// the method below returns an object of type CommPort
			commPort = selectedPortIdentifier.open("HapticControlPanel", TIMEOUT);
			// the CommPort object can be casted to a SerialPort object
			serialPort = (SerialPort) commPort;
			// for controlling GUI elements
			setConnected(true);
			logText = selectedPort + " opened successfully.\nHardware is asleep until a sensor request.";
			window.txtLog.setForeground(Color.BLACK);
			window.txtLog.append(logText + "\n");
			window.toggleAllControls();
			window.sensorSelector.toggleSensorButtons(true);
		} catch (PortInUseException e) {
			logText = selectedPort + " is in use. (" + e.toString() + ")";
			window.txtLog.setForeground(Color.RED);
			window.txtLog.append(logText + "\n");
		} catch (Exception e) {
			logText = "Failed to open " + selectedPort + "(" + e.toString() + ")";
			window.txtLog.append(logText + "\n");
			window.txtLog.setForeground(Color.RED);
		}
	}


	/**
	 * open the input and output streams
	 * pre: an open port
	 * post: initialised input and output streams for use to communicate data
	 * 
	 * @return successful
	 */
	public boolean initIOStream() {
		boolean successful = false;
		try {
			input = serialPort.getInputStream();
			output = serialPort.getOutputStream();
			successful = true;
			return successful;
		} catch (IOException e) {
			logText = "I/O Streams failed to open. (" + e.toString() + ")";
			window.txtLog.setForeground(Color.red);
			window.txtLog.append(logText + "\n");
			return successful;
		}
	}

	/**
	 * starts the event listener that knows whenever data is available to be read
	 * pre: an open serial port
	 * post: an event listener for the serial port that knows when data is received
	 */
	public void initListener() {
		try {
			serialPort.addEventListener(new SerialReaderConsumer(this, window, input));
			serialPort.notifyOnDataAvailable(true);
			controller.setReady();
			synchronized (changingSensorLock) {
				controller.activeSensors.clear();// clear active sensor list
				controller.modelState.cleanSensors();
				queue.clear();
				writeData(controller.sensors);
				try {
					int tempBaud = 0;
					Thread.sleep(50);
					if (isAsleep || controller.sensors == 0)
						tempBaud = SLEEP_BAUD_RATE;
					else
						tempBaud = AWAKE_BAUD_RATE;
					serialPort.setSerialPortParams(tempBaud, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
							SerialPort.PARITY_NONE);
					queue.clear();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (UnsupportedCommOperationException e) {
					logText = "Couldn't set awake paramters ";
					window.txtLog.append(logText + "\n");
					window.txtLog.setForeground(Color.RED);
				}
			}
			isAsleep = true;
			window.sensorNumberLabel.setText("" + controller.sensors);
		} catch (TooManyListenersException e) {
			logText = "Too many listeners. (" + e.toString() + ")";
			window.txtLog.setForeground(Color.red);
			window.txtLog.append(logText + "\n");
		}

	}

	/*
	 **************************************************************  DISCONNECT 
	 */

	/**
	 * disconnect the serial port
	 * pre: an open serial port
	 * post: closed serial port
	 */
	public void disconnect() {
		synchronized (changingSensorLock) {
			halt = true;
			isAsleep = true;
			window.sensorSelector.resetButtonState();
			controller.setReady();
			queue.clear();
			controller.sensors = 0;
			
			writeData(INIT_SLEEP_SETTING);
			try {
				Thread.sleep(50);
				serialPort.setSerialPortParams(SLEEP_BAUD_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);
			} catch (UnsupportedCommOperationException e1) {
				e1.printStackTrace();
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			}

			serialPort.removeEventListener();
			serialPort.close();
			try {
				input.close();
				output.close();
			} catch (IOException e) {
				logText = "Failed to close " + serialPort.getName() + "(" + e.toString() + ")";
				window.txtLog.setForeground(Color.red);
				window.txtLog.append(logText + "\n");
			}

			setConnected(false);
			window.toggleAllControls();
			window.sensorSelector.toggleSensorButtons(false);
			logText = "Disconnected.\n";
			window.txtLog.setForeground(Color.MAGENTA);
			window.txtLog.append(logText + "\n");

			controller.modelState.cleanSensors();
			controller.sensors = 0;
			window.sensorSelector.resetButtonState();
			for (int i = 0; i < 5; i++) {
				window.datagui.toggleReadingFont(i, false);
			}
		}

	}

	/*
	 **************************************************************  GETTERS / SETTERS  
	 */
	
	/**
	 * know if  the com port is connected to this app 
	 * @return connected 
	 */
	final public boolean getConnected() {
		return bConnected;
	}

	/**
	 * sets connected flag 
	 * @param bConnected
	 */
	public void setConnected(boolean bConnected) {
		this.bConnected = bConnected;
	}
	
	/**
	 * sets isConsuming high or low
	 * 
	 * This is only set high once when the connect button is pressed
	 * and only set low before system.exit is called
	 * @param in
	 */
	public void setReadingFlag(boolean in) {
		isConsuming = in;
	}

	/*
	 **************************************************************  WRITE DATA 
	 */
	
	/**
	 * method that can be called to send data 
	 * pre open serial port 
	 * post: data sent to the other device
	 * @param input
	 */
	protected void writeData(int input) {
		final int temp = input & 0xFFFF;
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					// System.out.println("shit to be written is" + temp);
					output.write(temp);
					output.flush();
				} catch (Exception e) {
					logText = "Failed to write data. (" + e.toString() + ")";
					window.txtLog.setForeground(Color.red);
					window.txtLog.append(logText + "\n");
				}
			}
		};
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}