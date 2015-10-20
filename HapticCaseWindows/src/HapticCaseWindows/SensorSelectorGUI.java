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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class SensorSelectorGUI extends javax.swing.JFrame {

	/*
	 ************************************************************* GLOBALS
	 */
	protected static long lastHit;
	private JButton[] sensor = new JButton[5];
	private FlowLayout experimentLayout = new FlowLayout();
	private SensorSelectorGUI sensorSelector;
	// passed from main GUI
	protected ControlPanelGui window = null;
	protected Communicator communicator = null;

	/*
	 ************************************************************* CONSTANTS
	 */
	private static int SENSOR_CHANGE_LOCKOUT_TIME = 100;

	/**
	 * ***********************************************************CONSTRUCTOR 
	 * @param window
	 * @param communicator
	 */
	public SensorSelectorGUI(ControlPanelGui window, Communicator communicator) {
		super("Sensor Selector");
		lastHit = System.currentTimeMillis();
		this.window = window;
		this.communicator = communicator;
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

	/*
	 * ************************************** METHODS ******************************
	 */
	
	/**
	 * Toggles enabled state of all sensor buttons
	 */
	public void toggleSensorButtons(boolean tog) {
		for (int i = 0; i < 5; i++) {
			sensor[i].setEnabled(tog);
		}
	}

	/**
	 * Re-initialises all button states depending on hardware state
	 */
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
			communicator.controller.modelState.sensorState[i] = false;
		}
	}
	
	/**
	 * Changes the sensor selector button that has just been pressed change
	 * depends on existing button/hardware state
	 * 
	 * Font and text is changed
	 */
	public void changeButText(int id) {
		String temp;
		Font onFont = null;
		if (id == 4)
			temp = "XYZ: ";
		else
			temp = "Strip " + (id + 1) + ": ";
		if (communicator.controller.modelState.sensorState[id] == false) {
			communicator.controller.modelState.sensorState[id] = true;
			temp = temp.concat("On");
			onFont = new Font(sensor[id].getFont().getName(), Font.BOLD, sensor[id].getFont().getSize());
			sensor[id].setFont(onFont);
		} else {
			communicator.controller.modelState.sensorState[id] = false;
			temp = temp.concat("Off");
			sensor[id].setFont(onFont);
		}
		sensor[id].setText(temp);
		this.pack();
	}

	/*
	 * ****************************COMPONENT LAYOUT AND ACTION LISTENERS *************************
	 */
	
	/**
	 * instantiates container pane with all buttons with flowLayout depending on
	 * initialised hardware state attaches action listener which itself has a
	 * time lock --> each button can be pressed once per
	 * SENSOR_CHANGE_LOCKOUT_TIME
	 */
	private void addComponentsToPane(final Container pane) {
		final JPanel compsToExperiment = new JPanel();
		compsToExperiment.setLayout(experimentLayout);
		experimentLayout.setAlignment(FlowLayout.TRAILING);
		JPanel controls = new JPanel();
		controls.setLayout(new FlowLayout());
		for (int i = 0; i < 5; i++) {
			boolean isOn = ((communicator.controller.sensors & (0b00000001 << i)) > 0);
			communicator.controller.modelState.sensorState[i] = isOn;
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
					if ((now - lastHit) > (SENSOR_CHANGE_LOCKOUT_TIME)) {
						communicator.controller.changeSensorsOutsideSleepBySwitch(j);
						synchronized (communicator.changingSensorLock) {
							changeButText(j);
							window.datagui.toggleReadingFont(j, communicator.controller.modelState.sensorState[j]);
							window.sensorNumberLabel
									.setText("Sensors: " + Integer.toBinaryString(communicator.controller.sensors));
						}
						lastHit = System.currentTimeMillis();
					}
				}
			});
		}
		pane.add(compsToExperiment, BorderLayout.CENTER);
		pane.add(controls, BorderLayout.SOUTH);
	}

	/*
	 * ************************************************************* MAIN METHODS ******************************
	 */

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 */
	public void createAndShowGUI() {
		sensorSelector.addComponentsToPane(sensorSelector.getContentPane());
		toggleSensorButtons(false);
		sensorSelector.pack();
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/8-this.getSize().height/8);
		
		sensorSelector.setVisible(true);
	}

	/**
	 * called by constructor of controklPanelGui
	 * 
	 * Initialises this class and invokes the JFrame
	 */
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