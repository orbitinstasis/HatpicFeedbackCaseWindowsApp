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
import java.util.ArrayList;
import java.util.List;

import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class Controller {
	
	/*
	 ************************************************************** CONSTANTS
	 */
	@SuppressWarnings("unused")
	private final static int END_MARKER = 0xFF;
	private static final int FIRST_STRIP = 0;
	private static final int SECOND_STRIP = 1;
	private static final int THIRD_STRIP = 2;
	private static final int FOURTH_STRIP = 3;

	/*
	 * ************************************************************* GLOBALS
	 */
	private Communicator communicator = null;
	private ControlPanelGui window = null;
	private int xCount = 0;
	private int yCount = 0;
	private boolean gotZero = false;
	private SensorState currentSensor = SensorState.READY;
	protected volatile int sensors = 0;
	protected Model modelState = new Model();
	protected List<SensorState> activeSensors = new ArrayList<SensorState>(5);
	private static enum SensorState {
		IN_STRIP_1, IN_STRIP_2, IN_STRIP_3, IN_STRIP_4, IN_XYZ, READY;
	}

	/**
	 * ****************************************************** CONSTRUCTOR 
	 * @param window
	 * @param communicator
	 */
	public Controller(ControlPanelGui window, Communicator communicator) {
		this.window = window;
		this.communicator = communicator;
	}

	/*
	 ************************************************************** GETTERS / SETTERS
	 */
	
	/**
	 * Forces currentSensor to READY state 
	 */
	protected void setReady() {
		currentSensor = SensorState.READY;
	}

	/**
	 * checks whether activeSensor list contains input state 
	 * 
	 * @param input state
	 * @return activeSensor contains input state
	 */
	protected boolean inSensorQuery(SensorState in) {
		return activeSensors.contains(in);
	}

	/*
	 * ************************************************************* CONSUMER THREAD ******************************
	 */
	
	/**
	 * Consumer Thread
	 * 
	 * Calls consumerMethod 
	 * Complements the producer in the serialReader instantiated from within the communicator 
	 */
	protected Thread consumerThread = new Thread(new Runnable() {
		public void run() {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			communicator.queue.clear();
			try {
				consumerMethod();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	});

	/*
	 * ************************************************************* METHODS ******************************
	 */
	
	/**
	 * Maps x linearly to new scale 
	 * 
	 * @param x
	 * @param in_min
	 * @param in_max
	 * @param out_min
	 * @param out_max
	 * @return mapped value 
	 */
    protected float map(float x, float in_min, float in_max, float out_min, float out_max)
    {
        if (x > in_max) x = in_max;
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }
	
	/**
	 * Called by Consumer Thread
	 * 
	 * Writes all active sensor data to model 
	 */
	@SuppressWarnings("incomplete-switch")
	private void consumerMethod() throws InterruptedException {
//		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		while (communicator.isConsuming) {
			if (communicator.halt) {
				synchronized (consumerThread) {
					consumerThread.wait();
				}
				synchronized (window.guiUpdater) {
					window.guiUpdater.notify();
				}
				synchronized (window.datagui.dataGuiUpdater) {
					window.datagui.dataGuiUpdater.notify();
				}
				synchronized (window.visualgui.visualGuiUpdater) {
					communicator.window.visualgui.visualGuiUpdater.notify();
				}
				communicator.halt = false;
			}
			while (currentSensor == SensorState.READY) {
				if (communicator.queue.take() == 255 && !activeSensors.isEmpty()) {
					synchronized (communicator.changingSensorLock) {
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
				communicator.queue.take();
		}
	}

	/**
	 * Called by consumer method from within consumer thread
	 * 
	 * we want to peek if it's 255, don't consume it since we consume it in
	 * consumerMethod
	 */
	private void setXYZ() throws InterruptedException {
		int input = communicator.queue.take();
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
				yCount++;
				if (yCount >= Model.COLS) {
					yCount = 0;
					xCount++;
					if (xCount >= Model.ROWS) {
						xCount = Model.ROWS - 1;
					}
				}
			}
			input = communicator.queue.take();
			if (input == 255)
				nextIsMarker = true;
		}
	}

	/**
	 * Called by consumer method from within consumer thread
	 * 
	 * writes side sensor data to model
	 */
	private void setSideSensor(int sensorID) throws InterruptedException {
		int force = communicator.queue.take();
		modelState.setCurrentSideSensor(sensorID, Model.SIDE_STRIP_FORCE, force);
		if (force > 0) {
			if (sensorID > 1)
				modelState.setCurrentSideSensor(sensorID, Model.SIDE_STRIP_POSITION, (254 - communicator.queue.take()));
			else
				modelState.setCurrentSideSensor(sensorID, Model.SIDE_STRIP_POSITION, communicator.queue.take());
		} else {
			modelState.setCurrentSideSensor(sensorID, Model.SIDE_STRIP_POSITION, 0);
			communicator.queue.take();
		}
	}
	
	/**
	 * called when state of hardware changes 
	 * 
	 * adds/removes active sensors on hardware to activeSensors array list
	 */
	private void setSensorState(int sensorID) {
		switch (sensorID) {
		case 0b00000001:
			activeSensors.add(SensorState.IN_STRIP_1);
			break;
		case 0b00000010:
			activeSensors.add(SensorState.IN_STRIP_2);
			break;
		case 0b00000100:
			activeSensors.add(SensorState.IN_STRIP_3);
			break;
		case 0b00001000:
			activeSensors.add(SensorState.IN_STRIP_4);
			break;
		case 0b00010000:
			activeSensors.add(SensorState.IN_XYZ);
		}
	}

	/**
	 * Called when a hardware state change is requested 
	 * 
	 * writes the desired sensor either high or low depending on existing hardware state
	 * if all sensors are eventually off, then sleep baud rate is entered on both hardware and receiver 
	 * 
	 */
	protected void changeSensorsOutsideSleepBySwitch(int desiredSensor) {
		synchronized (communicator.changingSensorLock) {
			activeSensors.clear();// clear active sensor list
			currentSensor = SensorState.READY;
			modelState.cleanSensors();
			if ((sensors & (0b00000001 << desiredSensor)) == 0) {
				sensors = (sensors & ~(1 << desiredSensor)) | (1 << desiredSensor);
			} else {
				sensors = (sensors & ~(1 << desiredSensor)) & ~(1 << desiredSensor);
			}
			communicator.writeData(sensors);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (communicator.isAsleep) {
				try {
					communicator.serialPort.setSerialPortParams(Communicator.AWAKE_BAUD_RATE, SerialPort.DATABITS_8,
							SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				} catch (UnsupportedCommOperationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				communicator.isAsleep = false;
			}
			modelState.cleanSensors();
			if (sensors != 0) {
				for (int i = 0; i < 5; i++) {
					int tempInput = (sensors & (0b00000001 << i));
					if (tempInput != 0)
						setSensorState(tempInput);
				}
			} else { // hardware is asleep so we want to wait the consumer !
				try {
					communicator.serialPort.setSerialPortParams(Communicator.SLEEP_BAUD_RATE, SerialPort.DATABITS_8,
							SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				} catch (UnsupportedCommOperationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				currentSensor = SensorState.READY;
				activeSensors.clear();// clear active sensor list
				communicator.queue.clear();
				modelState.cleanSensors();
				communicator.isAsleep = true;
			}
			modelState.cleanSensors();
			communicator.queue.clear();
			if (sensors == 0) {
				communicator.halt = true;
				communicator.logText = "Hardware has gone to sleep.";
				window.txtLog.setForeground(Color.MAGENTA);
				window.txtLog.append(communicator.logText + "\n");
			} else if (sensors > 0 && communicator.halt) {
				communicator.halt = false;
				window.wake();
				String logText = "Hardware has woken up.";
				window.txtLog.setForeground(Color.BLACK);
				window.txtLog.append(logText + "\n");
			}
			modelState.cleanSensors();
		}
		window.pack();
	}
}