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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Model {
	/*
	 ************************************************************ CONSTANTS
	 */
	protected static final int ROWS = 10;
	protected static final int COLS = 16;
	private static final int SIDE_SENSORS = 4;
	private static final int SIDE_PARAMETERS = 2;
	protected final static int SIDE_STRIP_FORCE = 0;
	protected final static int SIDE_STRIP_POSITION = 1;

	/*
	 ************************************************************** GLOBALS
	 */
	private Object currentXYZLock = new Object();
	private Object oldXYZLock = new Object();
	private Object currentSideLock = new Object();
	private Object oldSideLock = new Object();
	// private Object modelLock = new Object();
	private String sensorStateFilename = new String("sensorState.txt");
	private File sensorStateFile = new File(sensorStateFilename);
	private String dataFilename = new String("sensorData.txt");
	private File dataFile = new File(dataFilename);
	private FileWriter fw = null;
	private static int oldSideSensor[][] = { { 0, 0 }, { 0, 0 }, { 0, 0 }, { 0, 0 } };
	private static int currentSideSensor[][] = { { 0, 0 }, { 0, 0 }, { 0, 0 }, { 0, 0 } };
	protected boolean sensorState[] = { false, false, false, false, false };
	private static int[][] padCell = new int[ROWS][COLS];
	private static int[][] oldPadCell = new int[ROWS][COLS];

	/**
	 ************************************************************* CONSTRUCTOR
	 */
	protected Model() {
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				setCurrentXYZ(i, j, 0);
				setOldXYZ(i, j, 0);
			}
		}
	}

	/*
	 ************************************************************** GETTERS AND SETTERS
	 */

	/**
	 * gets current rear pad state at coordinate (i,j)
	 * 
	 * @param row
	 * @param col
	 * @return requested pad values current state
	 */
	protected int getCurrentXYZ(int row, int col) {
//		synchronized (currentXYZLock) {
			return padCell[row][col];
//		}
	}

	/**
	 * gets OLD rear pad state at coordinate (row,col)
	 * 
	 * @param row
	 * @param col
	 * @return requested pad values current state
	 */
	protected int getOldXYZ(int row, int col) {
//		synchronized (oldXYZLock) {
			return oldPadCell[row][col];
//		}
	}

	/**
	 * gets current side sensor parameter
	 * 
	 * @param sensor
	 * @param parameter
	 * @return requested side sensor current state
	 */
	protected int getCurrentSideSensor(int sensor, int parameter) {
//		synchronized (currentSideLock) {
			return currentSideSensor[sensor][parameter];
//		}
	}

	/**
	 * gets old side sensor FORCE parameter
	 * 
	 * @param sensor
	 * @return requested side sensor old force value
	 */
	protected int getOldSideSensor(int sensor, int parameter) {
//		synchronized (oldSideLock) {
			return oldSideSensor[sensor][parameter];
//		}
	}

	/**
	 * sets old side sensor FORCE parameter
	 * 
	 * @param sensor
	 * @return requested side sensor old force value
	 */
	protected void setOldSideSensor(int sensor, int parameter, int value) {
		synchronized (oldSideLock) {
			if (value != oldSideSensor[sensor][parameter])
				oldSideSensor[sensor][parameter] = value;
		}
	}

	/**
	 * sets side sensor parameter
	 * 
	 * @param stripNumber
	 * @param sensorAttribute
	 * @param value
	 */
	protected void setCurrentSideSensor(int stripNumber, int sensorAttribute, Integer value) {
		synchronized (currentSideLock) {
			if (value != currentSideSensor[stripNumber][sensorAttribute])
				currentSideSensor[stripNumber][sensorAttribute] = value;
		}
	}

	/**
	 * sets OLD rear pad value
	 * 
	 * @param row
	 * @param col
	 * @param value
	 */
	protected void setOldXYZ(int row, int col, int value) {
		synchronized (oldXYZLock) {
			if (value != oldPadCell[row][col])
				oldPadCell[row][col] = value;
		}
	}

	/**
	 * sets rear pad value
	 * 
	 * @param row
	 * @param col
	 * @param value
	 */
	protected void setCurrentXYZ(int row, int col, int value) {
		if (value != padCell[row][col]) {
			synchronized (currentXYZLock) {
				padCell[row][col] = value;
			}
		}
	}

	/*
	 * *************************************** METHODS
	 * ***************************************
	 */

	/**
	 * Writes the current sensor data to a txt file
	 */
	protected void writeSensorDataToFile() {
		try {
			fw = new FileWriter(dataFile);
			fw.write((new Date()).toString() + "\nSensor Data:\n");
			for (int i = 0; i < SIDE_SENSORS; i++) { // side sensors
				for (int j = 0; j < SIDE_PARAMETERS; j++) {
					if (j == 0)
						fw.write("\nSide#" + (i + 1) + "F=" + getCurrentSideSensor(i, j));
					else
						fw.write("\nSide#" + (i + 1) + "P=" + getCurrentSideSensor(i, j));
				}
			}
			fw.write("\nXYZ:");
			for (int i = 0; i < ROWS; i++) { // xyz
				for (int j = 0; j < COLS; j++) {
					fw.write("\n(" + i + "," + j + ")=" + getCurrentXYZ(i, j));
				}
			}
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes the current sensor states to a txt file
	 */
	protected void writeSensorStateToFile() {
		try {
			fw = new FileWriter(sensorStateFile);
			fw.write((new Date()).toString() + "\nSensor States:\n");
			for (int i = 0; i < sensorState.length; i++) {
				if (i < 4)
					fw.write("\nSide#" + (i + 1) + "=" + sensorState[i]);
				else
					fw.write("\nXYZ#" + "=" + sensorState[i]);
			}
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reinitialises entire model
	 */
	protected void cleanSensors() {
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				setCurrentXYZ(i, j, 0);
				setOldXYZ(i, j, 0);
			}
		}

		for (int i = 0; i < 4; i++) {
			
			for (int j = 0; j < 2; j++) {
				setCurrentSideSensor(i, j, 0);
				setOldSideSensor(i, j, 0);
			}
		}
	}
}