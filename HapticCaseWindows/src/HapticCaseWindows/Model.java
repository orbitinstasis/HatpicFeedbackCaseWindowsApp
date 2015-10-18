package HapticCaseWindows;

public class Model {
	// CONSTANTS
	protected static final int ROWS = 10;
	protected static final int COLS = 16;

	// GLOBALS
	Object modelLock = new Object();

	public static int oldSideSensor[][] = { { 0, 0 }, { 0, 0 }, { 0, 0 }, { 0, 0 } };
	public static int currentSideSensor[][] = { { 0, 0 }, { 0, 0 }, { 0, 0 }, { 0, 0 } };

	public static int[][] padCell = new int[ROWS][COLS];
	public static int[][] oldPadCell = new int[ROWS][COLS];

	// CONSTRUCTOR
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

	public void setCurrentSideSensor(int stripNumber, int sensorAttribute, Integer value) {
		synchronized (modelLock) {
			Model.oldSideSensor[stripNumber][sensorAttribute] = Model.currentSideSensor[stripNumber][sensorAttribute];
			Model.currentSideSensor[stripNumber][sensorAttribute] = value;
		}
	}

	public void setCurrentXYZ(int row, int col, int value) {
		synchronized (modelLock) {
			Model.oldPadCell[row][col] = Model.padCell[row][col];
			Model.padCell[row][col] = value;
		}
	}

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
