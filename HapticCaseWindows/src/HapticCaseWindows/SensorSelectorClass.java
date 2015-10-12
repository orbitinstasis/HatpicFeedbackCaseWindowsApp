package HapticCaseWindows;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class SensorSelectorClass extends javax.swing.JFrame {
	static JButton[] sensor = new JButton[5];

	// protected JButton strip1But = new JButton();
	// protected JButton strip2But = new JButton();
	// protected JButton strip3But = new JButton();
	// protected JButton strip4But = new JButton();
	// protected JButton xyzBut = new JButton("XYZ");
	protected static boolean sensorState[] = { true, false, false, false, true }; // CHANGE
																					// THIS
	FlowLayout experimentLayout = new FlowLayout();

	// passed from main GUI
	static GUI window = null;
	static Communicator communicator = null;

	public SensorSelectorClass(GUI window, Communicator communicator) {
		super("Sensor Selector");
		this.window = window;
		this.communicator = communicator;
	}


	
	public void changeButText(int id) {
		String temp;
		Font onFont = null;
		Font offFont = null;
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
			offFont = new Font(sensor[id].getFont().getName(), Font.PLAIN, sensor[id].getFont().getSize());
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

		// init buttons
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
					communicator.changeSensorsOutsideSleepBySwitch(j);
					changeButText(j);
					window.sensorNumberLabel.setText(Integer.toString(communicator.getSensors()));
				}
			});
		}

		pane.add(compsToExperiment, BorderLayout.CENTER);
		pane.add(controls, BorderLayout.SOUTH);
	}

	public void toggleSensorButtons() {
		if (communicator.getConnected() == true) {
			for (int i = 0; i < 5; i++) {
				sensor[i].setEnabled(true);
			}
		} else {
			for (int i = 0; i < 5; i++) {
				sensor[i].setEnabled(false);
			}
		}


	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 */
	public static void createAndShowGUI() {
		// Create and set up the window.
		SensorSelectorClass frame = new SensorSelectorClass(window, communicator);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Set up the content pane.
		frame.addComponentsToPane(frame.getContentPane());
		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void mainHere() {
		/* Use an appropriate Look and Feel */
		try {
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		/* Turn off metal's use of bold fonts */
		UIManager.put("swing.boldMetal", Boolean.FALSE);
		// Schedule a job for the event dispatchi thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}