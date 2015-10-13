package HapticCaseWindows;
/* TODO: 
 *		seperate the controller code in communicator from communicator (make a new class)
 *use that multithreaded swing components for gui OR JAVAFX WITH ORACLES SOMETHING SOMETING
 * * 		disable sensor buttons when not connected ! (add to toggle method)
 *				why does sensorSelector.toggleSensorButtons();not work ? ??
 		---> why can't i toggle the sensor buttons for half a second? 
 *
 *turn toggleAllControls back on in here adn gui ! 
 *
 *at teh end we want to have the model data as some retrievable data (and status things) probably in a FILE format for other shits to access!
 */

import java.awt.Color;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import HapticCaseWindows.Controller;

public class Communicator // implements SerialPortEventListener
{
	/*
	 * GLOBALS
	 */
	static volatile private int sensors = 0;
	public boolean isAsleep = true; // we want this in a file so other programs can query it
	protected Model modelState = new Model();
	private BlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(512);
	protected List<SensorState> activeSensors = new ArrayList<SensorState>(5);
	Object changingSensorLock = new Object();
	protected SensorState currentSensor = SensorState.READY;
	boolean isConsuming = false;
	// for containing the ports that will be found
	private Enumeration ports = null;
	// map the port names to CommPortIdentifiers
	private HashMap portMap = new HashMap();
	// this is the object that contains the opened port
	private CommPortIdentifier selectedPortIdentifier = null;
	private SerialPort serialPort = null;
	// input and output streams for sending and receiving data
	private InputStream input = null;
	private OutputStream output = null;
	// just a boolean flag that i use for enabling
	// and disabling buttons depending on whether the program
	// is connected to a serial port or not
	private boolean bConnected = false;
	private int xCount = 0;
	private int yCount = 0;
	private boolean gotZero = false;
	// passed from main GUI
	GUI window = null;
	SensorSelectorClass sensorSelector = null;
	// a string for recording what goes on in the program
	// this string is written to the GUI
	String logText = "";
	boolean halt = false;
	static boolean isChangingSensors = false;
	
	/*
	 * CONSTANTS
	 */
	protected static enum SensorState {
		IN_STRIP_1, IN_STRIP_2, IN_STRIP_3, IN_STRIP_4, IN_XYZ, READY;
	}
	// the timeout value for connecting with the port
	final static int TIMEOUT = 2000;
	final static int SLEEP_BAUD_RATE = 9600;
	final static int AWAKE_BAUD_RATE = 115200;
	final static int END_MARKER = 0xFF;
	final static int INIT_SLEEP_SETTING = 0;
	final static int MAX_BUFFER = 32;
	protected static int SIDE_STRIP_FORCE = 0;
	protected static int SIDE_STRIP_POSITION = 1;
	private static final int FIRST_STRIP = 0;
	private static final int SECOND_STRIP = 1;
	private static final int THIRD_STRIP = 2;
	private static final int FOURTH_STRIP = 3;

	/*
	 *  CONSTRUCTOR
	 */
	public Communicator(GUI window, SensorSelectorClass sensorSelector) {
		this.window = window;
		this.sensorSelector = sensorSelector;
	}

	public int getSensors() {
		return sensors;
	}

	public void changeSensorsOutsideSleepBySwitch(int desiredSensor) {
		window.toggleSensorControls();
//		SensorSelectorClass.toggleSensorButtons(false);
		isChangingSensors = true;
		synchronized (changingSensorLock) {
			activeSensors.clear();// clear active sensor list
			makeDataLabelsNotVisible();
			currentSensor = SensorState.READY;
			if ((sensors & (0b00000001 << desiredSensor)) == 0) { 
				sensors = (sensors & ~(1 << desiredSensor)) | (1 << desiredSensor); 
			} else {
				sensors = (sensors & ~(1 << desiredSensor)) & ~(1 << desiredSensor);
			}
			writeData(sensors);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (isAsleep) {
				try {
					serialPort.setSerialPortParams(AWAKE_BAUD_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
							SerialPort.PARITY_NONE);
				} catch (UnsupportedCommOperationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				isAsleep = false;
			}
			// add the active enum by iterating over bits of sensor and
			// consulting a
			// switch case setSensorState
			if (sensors != 0) {
				for (int i = 0; i < 5; i++) {
					int tempInput = (sensors & (0b00000001 << i));
					if (tempInput != 0)
						setSensorState(tempInput);
				}
			} else { // hardware is asleep so we want to wait the consumer !
				try {
					serialPort.setSerialPortParams(SLEEP_BAUD_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
							SerialPort.PARITY_NONE);
				} catch (UnsupportedCommOperationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				currentSensor = SensorState.READY;
				activeSensors.clear();// clear active sensor list
				queue.clear();
				modelState.cleanSensors();
				isAsleep = true;
			}
			modelState.cleanSensors();
			queue.clear();
		}
		if (sensors == 0) {
			halt = true;
			logText = "Hardware has gone to sleep.";
			window.txtLog.setForeground(Color.BLACK);
			window.txtLog.append(logText + "\n");
		} else if (sensors > 0 && halt) {
			halt = false;
			window.wake();
			String logText = "Hardware has woken up.";
			window.txtLog.setForeground(Color.BLACK);
			window.txtLog.append(logText + "\n");
		}
		window.toggleSensorControls();
//		SensorSelectorClass.toggleSensorButtons(true);
		isChangingSensors = false;
		window.pack();
	}

	private void makeDataLabelsNotVisible() { // we want to replace this -- it's probably not even necessary 
		window.jLabels1a.setVisible(false);
		window.jLabels1b.setVisible(false);
		window.jLabels2a.setVisible(false);
		window.jLabels2b.setVisible(false);
		window.jLabels3a.setVisible(false);
		window.jLabels3b.setVisible(false);
		window.jLabels4a.setVisible(false);
		window.jLabels4b.setVisible(false);
	}

	private void setSensorState(int sensorID) {
		switch (sensorID) {
		case 0b00000001:
			activeSensors.add(SensorState.IN_STRIP_1);
			window.jLabels1a.setVisible(true);
			window.jLabels1b.setVisible(true);
			break;
		case 0b00000010:
			activeSensors.add(SensorState.IN_STRIP_2);
			window.jLabels2a.setVisible(true);
			window.jLabels2b.setVisible(true);
			break;
		case 0b00000100:
			activeSensors.add(SensorState.IN_STRIP_3);
			window.jLabels3a.setVisible(true);
			window.jLabels3b.setVisible(true);
			break;
		case 0b00001000:
			activeSensors.add(SensorState.IN_STRIP_4);
			window.jLabels4a.setVisible(true);
			window.jLabels4b.setVisible(true);
			break;
		case 0b00010000:
			activeSensors.add(SensorState.IN_XYZ);
		}
	}

	// search for all the serial ports
	// pre: none
	// post: adds all the found ports to a combo box on the GUI
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

	// connect to the selected port in the combo box
	// pre: ports are already found by using the searchForPorts method
	// post: the connected comm port is stored in commPort, otherwise,
	// an exception is generated
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
			SensorSelectorClass.toggleSensorButtons(true);
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

	// open the input and output streams
	// pre: an open port
	// post: initialized intput and output streams for use to communicate data
	public boolean initIOStream() {
		// return value for whather opening the streams is successful or not
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

	// starts the event listener that knows whenever data is available to be
	// read
	// pre: an open serial port
	// post: an event listener for the serial port that knows when data is
	// recieved
	public void initListener() {
		try {
			serialPort.addEventListener(new SerialReader(input));
			serialPort.notifyOnDataAvailable(true);
			currentSensor = SensorState.READY;
			synchronized (changingSensorLock) {
				activeSensors.clear();// clear active sensor list
				makeDataLabelsNotVisible();
				modelState.cleanSensors();
				queue.clear();
				writeData(sensors);
				try {
					int tempBaud = 0;
					Thread.sleep(50);
					if (isAsleep || sensors == 0)
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
			window.sensorNumberLabel.setText("" + getSensors());
		} catch (TooManyListenersException e) {
			logText = "Too many listeners. (" + e.toString() + ")";
			window.txtLog.setForeground(Color.red);
			window.txtLog.append(logText + "\n");
		}

	}

	// disconnect the serial port
	// pre: an open serial port
	// post: clsoed serial port
	public void disconnect() {
		synchronized (changingSensorLock) {
			SensorSelectorClass.resetButtonState();
			currentSensor = SensorState.READY;
			sensors = 0;
			queue.clear();
			writeData(INIT_SLEEP_SETTING);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			try {
				serialPort.setSerialPortParams(SLEEP_BAUD_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);
			} catch (UnsupportedCommOperationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
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
			SensorSelectorClass.toggleSensorButtons(false);
			logText = "Disconnected.\n";
			window.txtLog.setForeground(Color.red);
			window.txtLog.append(logText + "\n");
		}

	}

	final public boolean getConnected() {
		return bConnected;
	}

	public void setConnected(boolean bConnected) {
		this.bConnected = bConnected;
	}

	// method that can be called to send data
	// pre style="font-size: 11px;": open serial port
	// post: data sent to the other device
	public void writeData(int input) {
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

	public void setReadingFlag(boolean in) {
		isConsuming = in;
	}

	/**
	 * Handles the input coming from the serial port. A new line character is
	 * treated as the end of a block in this example.
	 */
	public class SerialReader implements SerialPortEventListener {
		private InputStream in;

		public SerialReader(InputStream in) {
			this.in = in;
		}

		public void serialEvent(SerialPortEvent arg0) {
			int data;

			try {
				Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
				while (consumerThread.isAlive() && ((data = in.read()) > -1)) {
					queue.add(data);
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}

	Thread consumerThread = new Thread(new Runnable() {
		public void run() {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			queue.clear();
			try {
				consumerMethod();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	});


//	private void sleep() {
//		currentSensor = SensorState.READY;
//		writeData(0);
//		try {
//			Thread.sleep(50);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			serialPort.setSerialPortParams(SLEEP_BAUD_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
//					SerialPort.PARITY_NONE);
//		} catch (UnsupportedCommOperationException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		queue.clear();
//		modelState.cleanSensors();
//	}

//	private void wakeup() {
//		writeData(sensors);
//		try {
//			Thread.sleep(50);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			serialPort.setSerialPortParams(AWAKE_BAUD_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
//					SerialPort.PARITY_NONE);
//		} catch (UnsupportedCommOperationException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		queue.clear();
//		modelState.cleanSensors();
//	}

	@SuppressWarnings("incomplete-switch")
	private void consumerMethod() throws InterruptedException {
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		while (isConsuming) {
			if (halt) {

				synchronized (consumerThread) {
					consumerThread.wait();
				}
				synchronized (window.guiUpdater) {
					window.guiUpdater.notify();
				}
				halt = false;
			}
			while (currentSensor == SensorState.READY) {
				if (queue.take() == 255 && !activeSensors.isEmpty()) {
					synchronized (changingSensorLock) {
						currentSensor = activeSensors.get(0);
					}
				}
			}
			for (int i = 0; i < activeSensors.size() && currentSensor != SensorState.READY; i++) {
				switch (activeSensors.get(i)) {
				case IN_STRIP_1:
					setSideSensor(FIRST_STRIP);
					break;
				case IN_STRIP_2:
					setSideSensor(SECOND_STRIP);
					break;
				case IN_STRIP_3:
					setSideSensor(THIRD_STRIP);
					break;
				case IN_STRIP_4:
					setSideSensor(FOURTH_STRIP);
					break;
				case IN_XYZ:
					setXYZ();
					xCount = 0;
					yCount = 0;
				}
			}
			if (!inSensorQuery(SensorState.IN_XYZ))
				queue.take();
		}
	}

	public boolean inSensorQuery(SensorState in) {
		return activeSensors.contains(in);
	}

	// we want to peek if it's 255, don't consume it since we consume it in
	// consumerMethod
	private void setXYZ() throws InterruptedException {
		int input = queue.take();
		boolean nextIsMarker = false;
		while (!nextIsMarker) {
			if (gotZero) {
				for (int z = 0; z < input; z++) {
					modelState.setCurrentXYZ(xCount, yCount, 0);
					// modelState.padCell[xCount][yCount] = 0;
					yCount++;
					if (yCount >= Model.COLS) {
						yCount = 0;
						xCount++;
						if (xCount >= Model.ROWS) {
							xCount = Model.ROWS - 1;
						}
					}
				}
				gotZero = false;
			} else if (input == 0) {
				gotZero = true;
			} else {
				modelState.setCurrentXYZ(xCount, yCount, input);
				// modelState.padCell[xCount][yCount] = (queue.take());
				yCount++;
				if (yCount >= Model.COLS) {
					yCount = 0;
					xCount++;
					if (xCount >= Model.ROWS) {
						xCount = Model.ROWS - 1;
					}
				}
			}
			input = queue.take();
			if (input == 255)
				nextIsMarker = true;
		}
	}

	public int getCurrentSideSensor(int i, int j) {
		return Model.currentSideSensor[i][j];
	}

	public int getCurrentXYZ(int i, int j) {
		return Model.padCell[i][j];
	}

	private void setSideSensor(int sensorID) throws InterruptedException {
		modelState.setCurrentSideSensor(sensorID, SIDE_STRIP_FORCE, queue.take());
		modelState.setCurrentSideSensor(sensorID, SIDE_STRIP_POSITION, queue.take());
	}
}
