package HapticCaseWindows;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class SensorSelectorGUI extends javax.swing.JFrame  {
	static JButton[] sensor = new JButton[5];

	protected static boolean sensorState[] = { true, false, false, false, true }; // CHANGE
																					// THIS
	FlowLayout experimentLayout = new FlowLayout();

	// passed from main GUI
	static ConnectorGUI window = null;
	static Communicator communicator = null;

	public SensorSelectorGUI(ConnectorGUI window, Communicator communicator) {
		super("Sensor Selector");
		SensorSelectorGUI.window = window;
		SensorSelectorGUI.communicator = communicator;
		setResizable(false);
		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		/*
		 * DEAL WITH DISCONNECTING HERE BUT DON'T FUCKING CLOSE THE SHIT THIS IS JUT A COPY PASTA
		 */
//		this.addWindowListener(new java.awt.event.WindowAdapter() {
//			@Override
//			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
//				// Component frame = null;
//				// setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
//				// if (JOptionPane.showConfirmDialog(frame,
//				// "Are you sure to close this window?", "Really Closing?",
//				// JOptionPane.YES_NO_OPTION,
//				// JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
//				// communicator.setReadingFlag(false);
//				if (communicator.getConnected())
//					communicator.disconnect();
//				System.exit(0);
//				// }
//			}
//		});
	}

//	public static void close() {
//		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
//	}

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
//					System.out.println(Communicator.isChangingSensors);
					if (!Communicator.isChangingSensors) {
						communicator.changeSensorsOutsideSleepBySwitch(j);
						changeButText(j);
						window.sensorNumberLabel.setText(Integer.toString(communicator.getSensors())); 
					}
				}
			});
		}

		pane.add(compsToExperiment, BorderLayout.CENTER);
		pane.add(controls, BorderLayout.SOUTH);
	}

	public static void toggleSensorButtons(boolean tog) {
//		System.out.println("toggle: " + tog);
		for (int i = 0; i < 5; i++) {
				sensor[i].setEnabled(tog);
//				System.out.println(sensor[i]);
		}
	}

	public static void resetButtonState() {
		// we toggle in the controller, we just want to change text and boolean
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

	public void changeVisibility(boolean in) {
		
	}
	
	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 */
	public static void createAndShowGUI() {
		// Create and set up the window.
		SensorSelectorGUI sensorSelectorFrame = new SensorSelectorGUI(window, communicator);
		
		// Set up the content pane.
		sensorSelectorFrame.addComponentsToPane(sensorSelectorFrame.getContentPane());
		// Display the window.
		toggleSensorButtons(false);
		sensorSelectorFrame.pack();
		sensorSelectorFrame.setVisible(true);
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