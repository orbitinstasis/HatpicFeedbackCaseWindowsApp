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
	 ************************************************************  CONSTANTS
	 */
	protected static final int ROWS = 10;
	protected static final int COLS = 16;
	protected static final int SIDE_SENSORS = 4;
	protected static final int SIDE_PARAMETERS = 2;
	protected final static int SIDE_STRIP_FORCE = 0;
	protected final static int SIDE_STRIP_POSITION = 1;
	
	/*
	 **************************************************************  GLOBALS
	 */
	Object modelLock = new Object();
	String sensorStateFilename = new String("sensorState.txt");
	File sensorStateFile = new File(sensorStateFilename);
	String dataFilename = new String("sensorData.txt");
	File dataFile = new File(dataFilename);
	FileWriter fw = null;
	private static int oldSideSensor[][] = { { 0, 0 }, { 0, 0 }, { 0, 0 }, { 0, 0 } };
	private static int currentSideSensor[][] = { { 0, 0 }, { 0, 0 }, { 0, 0 }, { 0, 0 } };
	protected boolean sensorState[] = { false, false, false, false, false };
	private static int[][] padCell = new int[ROWS][COLS];
	private static int[][] oldPadCell = new int[ROWS][COLS];

	/**
	 *************************************************************  CONSTRUCTOR
	 */
	public Model() {
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				synchronized (modelLock) {
					padCell[i][j] = 0;
					oldPadCell[i][j] = 0;
				}
			}
		}
	}

	/*
	 **************************************************************  GETTERS AND SETTERS 
	 */
	
	/**
	 * gets current rear pad state at coordinate (i,j)
	 * 
	 * @param i ROW
	 * @param j COL 
	 * @return requested pad values current state 
	 */
	public int getCurrentXYZ(int i, int j) {
		return Model.padCell[i][j];
	}

	/**
	 * gets current side sensor parameter
	 * 
	 * @param i sensor number
	 * @param j sensor parameter 
	 * @return requested side sensor current state
	 */
	public int getCurrentSideSensor(int i, int j) {
		return Model.currentSideSensor[i][j];
	}
	
	/**
	 * sets side sensor parameter 
	 * 
	 * @param stripNumber
	 * @param sensorAttribute
	 * @param value
	 */
	public void setCurrentSideSensor(int stripNumber, int sensorAttribute, Integer value) {
		synchronized (modelLock) {
			Model.oldSideSensor[stripNumber][sensorAttribute] = Model.currentSideSensor[stripNumber][sensorAttribute];
			Model.currentSideSensor[stripNumber][sensorAttribute] = value;
		}
	}

	/**
	 * sets rear pad value after saving existing rear pad model to oldPadCell
	 * this can be useful to know if a sensor cell has changed since the previous reading 
	 * 
	 * @param row
	 * @param col
	 * @param value
	 */
	public void setCurrentXYZ(int row, int col, int value) {
		synchronized (modelLock) {
			Model.oldPadCell[row][col] = Model.padCell[row][col];
			Model.padCell[row][col] = value;
		}
	}
	
	/*
	 * *************************************** METHODS ***************************************
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
						fw.write("\nSide#" + (i + 1) + "F=" + currentSideSensor[i][j]);
					else
						fw.write("\nSide#" + (i + 1) + "P=" + currentSideSensor[i][j]);
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
	public void cleanSensors() {
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				synchronized (modelLock) {
					padCell[i][j] = 0;
					oldPadCell[i][j] = 0;
				}
			}
		}
		Model.oldSideSensor = Model.currentSideSensor;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 2; j++) {
				synchronized (modelLock) {
					currentSideSensor[i][j] = 0;
				}
			}
		}
	}
}