package HapticCaseWindows;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class SensorSelectorGUI extends javax.swing.JFrame {
	/*
	 * GLOBALS
	 */
	protected static long lastHit;
	JButton[] sensor = new JButton[5];
	protected boolean sensorState[] = { false, false, false, false, false }; 
	FlowLayout experimentLayout = new FlowLayout();
	private SensorSelectorGUI sensorSelector;
	// passed from main GUI
	static ConnectorGUI window = null;
	static Communicator communicator = null;

	/*
	 * CONSTRUCTOR
	 */
	public SensorSelectorGUI(ConnectorGUI window, Communicator communicator) {
		super("Sensor Selector");
		lastHit = System.currentTimeMillis();
		SensorSelectorGUI.window = window;
		SensorSelectorGUI.communicator = communicator;
		setResizable(false);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				setVisible(false);
				window.btnShowSensorSelectorPane.setEnabled(true);
				dispose();
			}
		});
	}

	public void changeButText(int id) {
		String temp;
		Font onFont = null;
		if (id == 4)
			temp = "XYZ: ";
		else
			temp = "Strip " + (id + 1) + ": ";
		if (sensorState[id] == false) {
			sensorState[id] = true;
			temp = temp.concat("On");
			onFont = new Font(sensor[id].getFont().getName(), Font.BOLD, sensor[id].getFont().getSize());
			sensor[id].setFont(onFont);
		} else {
			sensorState[id] = false;
			temp = temp.concat("Off");
			sensor[id].setFont(onFont);
		}
		sensor[id].setText(temp);
		this.pack();
	}

	private void addComponentsToPane(final Container pane) {
		final JPanel compsToExperiment = new JPanel();
		compsToExperiment.setLayout(experimentLayout);
		experimentLayout.setAlignment(FlowLayout.TRAILING);
		JPanel controls = new JPanel();
		controls.setLayout(new FlowLayout());
		for (int i = 0; i < 5; i++) {
			boolean isOn = ((communicator.getSensors() & (0b00000001 << i)) > 0);
			sensorState[i] = isOn;
			Font onFont = null;
			Font offFont = null;
			if (i > 3) {
				if (isOn) {
					sensor[i] = new JButton("XYZ: On");
					onFont = new Font(sensor[i].getFont().getName(), Font.BOLD, sensor[i].getFont().getSize());
					sensor[i].setFont(onFont);
				} else {
					sensor[i] = new JButton("XYZ: Off");
					offFont = new Font(sensor[i].getFont().getName(), Font.PLAIN, sensor[i].getFont().getSize());
					sensor[i].setFont(offFont);
				}
			} else {
				if (isOn) {
					sensor[i] = new JButton("Strip " + (i + 1) + ": On");
					onFont = new Font(sensor[i].getFont().getName(), Font.BOLD, sensor[i].getFont().getSize());
					sensor[i].setFont(onFont);
				} else {
					sensor[i] = new JButton("Strip " + (i + 1) + ": Off");
					offFont = new Font(sensor[i].getFont().getName(), Font.PLAIN, sensor[i].getFont().getSize());
					sensor[i].setFont(offFont);
				}
			}
			compsToExperiment.add(sensor[i]);
			final int j = i;
			sensor[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					long now = System.currentTimeMillis();
					if ((now - lastHit) > (500)) {
						communicator.changeSensorsOutsideSleepBySwitch(j);
						changeButText(j);
						window.datagui.toggleReadingFont(j, sensorState[j]);
						window.sensorNumberLabel.setText("Sensors: " + Integer.toBinaryString(communicator.getSensors()));
						lastHit = System.currentTimeMillis();
					}
				}
			});
		}
		pane.add(compsToExperiment, BorderLayout.CENTER);
		pane.add(controls, BorderLayout.SOUTH);
	}

	public void toggleSensorButtons(boolean tog) {
		for (int i = 0; i < 5; i++) {
			sensor[i].setEnabled(tog);
		}
	}

	public void resetButtonState() {
		Font offFont = null;
		for (int i = 0; i < 5; i++) {
			if (i > 3) {
				sensor[i].setText("XYZ: Off");
				offFont = new Font(sensor[i].getFont().getName(), Font.PLAIN, sensor[i].getFont().getSize());
				sensor[i].setFont(offFont);
			} else {
				sensor[i].setText("Strip " + (i + 1) + ": Off");
				offFont = new Font(sensor[i].getFont().getName(), Font.PLAIN, sensor[i].getFont().getSize());
				sensor[i].setFont(offFont);
			}
			sensorState[i] = false;
		}
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 */
	public void createAndShowGUI() {
		sensorSelector.addComponentsToPane(sensorSelector.getContentPane());
		toggleSensorButtons(false);
		sensorSelector.pack();
		sensorSelector.setVisible(true);
	}

	public void mainSensorSelectorGui(SensorSelectorGUI sensorSelector) {
		this.sensorSelector = sensorSelector;
		try {
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		/* Turn off metal's use of bold fonts */
		UIManager.put("swing.boldMetal", Boolean.FALSE);
		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}