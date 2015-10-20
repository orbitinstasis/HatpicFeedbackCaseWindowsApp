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
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.text.DefaultCaret;

@SuppressWarnings("serial")
public class ControlPanelGui extends javax.swing.JFrame implements Runnable {

	/*
	 * ************************************************************ GLOBALS
	 */
	public javax.swing.JButton btnConnect;
	public javax.swing.JButton btnDisconnect;
	public javax.swing.JButton btnShowSensorSelectorPane;
	public javax.swing.JButton btnDebug;
	public javax.swing.JButton btnShowVisualGui;
	public javax.swing.JButton btnShowDataGui;
	@SuppressWarnings("rawtypes")
	public javax.swing.JComboBox cboxPorts;
	private javax.swing.JLabel controlPanelLabel;
	private javax.swing.JLabel logNameLabel;
	public javax.swing.JLabel sensorNumberLabel;
	private javax.swing.JLabel comPortSelectLabel;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JTextArea jTextArea1;
	public javax.swing.JTextArea txtLog;
	// object for selecting my sensors
	SensorSelectorGUI sensorSelector = null;
	// Communicator object
	Communicator communicator = null;
	SensorOutputDataGUI datagui = null;
	SensorOutputVisualGUI visualgui = null;
	Thread guiUpdater = new Thread(this);

	/**
	 *************************************************************** CONSTRUCTOR 
	 */
	public ControlPanelGui() {
		// Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		initComponents();
		createObjects();
		setResizable(false);
		communicator.searchForPorts();
		// setAlwaysOnTop(true);
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				communicator.setReadingFlag(false);
				if (communicator.getConnected())
					communicator.disconnect();
				System.exit(0);
			}
		});
		toggleAllControls();
		pack();
		int x = 3;
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/x-this.getSize().height/2);
		setVisible(true);
		sensorSelector.mainSensorSelectorGui(sensorSelector);
		datagui.numericGUImain(datagui);
		visualgui.visualGUImain(visualgui);
		for (int i = 0; i < 5; i++) {
			datagui.toggleReadingFont(i, communicator.controller.modelState.sensorState[i]);
		}
	}

	/*
	 * ****************************METHODS******************************
	 */
	
	/**
	 * notifies all waiting threads
	 */
	protected void wake() {
		synchronized (communicator.controller.consumerThread) {
			communicator.controller.consumerThread.notify();
		}
		synchronized (guiUpdater) {
			guiUpdater.notify();
		}
		synchronized (communicator.window.datagui.dataGuiUpdater) {
			communicator.window.datagui.dataGuiUpdater.notify();
		}
	}
	
	/**
	 * Initialises all classes
	 */
	private void createObjects() {
		communicator = new Communicator(this);
		sensorSelector = new SensorSelectorGUI(this, communicator);
		datagui = new SensorOutputDataGUI(this);
		visualgui = new SensorOutputVisualGUI(this);
	}

	/**
	 * toggles the connect/disconnect/COM combo box and other communication
	 * controllers appropriately depending on the state of the communicator
	 */
	public void toggleAllControls() {
		// sensorSelector.toggleSensorButtons();
		if (communicator.getConnected() == true) {
			btnDisconnect.setEnabled(true);
			btnConnect.setEnabled(false);
			cboxPorts.setEnabled(false);
		} else {
			btnDisconnect.setEnabled(false);
			btnConnect.setEnabled(true);
			cboxPorts.setEnabled(true);
		}
	}
	/*
	 * **********************************************RUNNER**************************
	 */

	/**
	 * Currently a debug runner, used mostly to write out model state
	 */
	@Override
	public void run() {
		// Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		while (communicator.isConsuming) {
			// System.out.println("i'm cons in gui");
			if (communicator.halt) {// NOT DEBUG
				System.out.println("Halt");
				try {
					synchronized (guiUpdater) {
						guiUpdater.wait();
					}
					System.out.println("released guiUpdater");
					synchronized (communicator.window.datagui.dataGuiUpdater) {
						communicator.window.datagui.dataGuiUpdater.wait();
					}
					System.out.println("wake up");
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	// try {
	// Thread.sleep(100);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// DEBUGGER ONLY I.E. TO SYSOUT
	// for (int i = 0; i < communicator.activeSensors.size(); i++) {
	// System.out.println(communicator.activeSensors.get(i).toString());
	// }
	// try {
	// Thread.sleep(500);
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// s1
	// if (communicator.inSensorQuery(SensorState.IN_STRIP_1))
	// System.out.println("s1: " +
	// communicator.getCurrentSideSensor(0, 0) + " "
	// + communicator.getCurrentSideSensor(0, 1));
	// // s2
	// if (communicator.inSensorQuery(SensorState.IN_STRIP_2))
	// System.out.println(" s2: " +
	// communicator.getCurrentSideSensor(1, 0) + " "
	// + communicator.getCurrentSideSensor(1, 1));
	// // s3
	// if (communicator.inSensorQuery(SensorState.IN_STRIP_3))
	// System.out.println(" s3: " +
	// communicator.getCurrentSideSensor(2, 0) + " "
	// + communicator.getCurrentSideSensor(2, 1));
	// // s4
	// if (communicator.inSensorQuery(SensorState.IN_STRIP_4))
	// System.out.println(" s4: " +
	// communicator.getCurrentSideSensor(3, 0) + " "
	// + communicator.getCurrentSideSensor(3, 1));
	// // XYZ
	// if (communicator.inSensorQuery(SensorState.IN_XYZ)) { // if
	// // we're
	// // in
	// // the
	// // xyz
	// for (int i = 0; i < Model.ROWS; i++) {
	// for (int j = 0; j < Model.COLS; j++) {
	// int temp = communicator.getCurrentXYZ(i, j);
	// if (temp < 10) {
	// System.out.print(" ");
	// } else if (temp < 100) {
	// System.out.print(" ");
	// }
	// System.out.print(temp + " ");
	// }
	// System.out.println();
	// }
	// }
	// System.out.println();
	// DEBUG end


	/*
	 ******************************************************* Button performers ****************************************
	 */

	/**
	 * When enabled, makes sensor selector JFrame visible is disabled when
	 * sensor selector is disposed
	 * 
	 * @param evt
	 */
	private void performSensorSelectorGUI(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnLeftAccelActionPerformed
		if (!sensorSelector.isVisible()) {
			btnShowSensorSelectorPane.setEnabled(false);
			sensorSelector.setVisible(true);
			sensorSelector.pack();
		}
	}

	/**
	 * Debug button performer currently set to perform various debug duties
	 * 
	 * @param evt
	 */
	private void performDebugBtn(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnLeftDecelActionPerformed
		// System.out.println("Halt: " + communicator.halt + "\nisasleep: " +
		// communicator.isAsleep);
		// System.out.println("Sensor selector visible: " +
		// sensorSelector.isVisible() + "\ndatagui visibile: "
		// + datagui.isVisible() + "\nVisual gui visible: " +
		// visualgui.isVisible());
		communicator.controller.modelState.writeSensorStateToFile();
		communicator.controller.modelState.writeSensorDataToFile();

	}

	/**
	 * When enabled, makes visual gui JFrame visible is disabled when visual gui
	 * is disposed
	 * 
	 * @param evt
	 */
	private void performTogVisualGUI(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnRightAccelActionPerformed
		if (!visualgui.isVisible()) {
			btnShowVisualGui.setEnabled(false);
			visualgui.setVisible(true);
			visualgui.pack();
		}
	}

	/**
	 * When enabled, makes data gui JFrame visible is disabled when data gui is
	 * disposed
	 * 
	 * @param evt
	 */
	private void performTogDataGUI(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnRightDecelActionPerformed
		if (!datagui.isVisible()) {
			btnShowDataGui.setEnabled(false);
			datagui.setVisible(true);
			datagui.pack();
		}
	}

	/**
	 * Connect button performer States threads if not started thread already,
	 * Else calls notify
	 * 
	 * @param evt
	 */
	private void btnConnectActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnConnectActionPerformed
		if (!communicator.isConsuming) {
			communicator.connect();
			if (communicator.getConnected() == true) {
				if (communicator.initIOStream() == true) {
					communicator.setReadingFlag(true);
					synchronized (communicator.controller.consumerThread) {
						communicator.controller.consumerThread.start();
					}
					synchronized (guiUpdater) {
						guiUpdater.start();
					}
					synchronized (communicator.window.datagui.dataGuiUpdater) {
						communicator.window.datagui.dataGuiUpdater.start();
					}
					communicator.initListener();
				}
			}
		} else {
			communicator.connect();
			if (communicator.getConnected() == true) {
				if (communicator.initIOStream() == true) {
					communicator.initListener();
					wake();
				}
			}
			pack();
		}
	}

	/**
	 * Disconnect button performer
	 * 
	 * @param evt
	 * @throws InterruptedException
	 */
	private void btnDisconnectActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnDisconnectActionPerformed
		communicator.halt = true;
		communicator.disconnect();
		if (!communicator.isAsleep) {
			String logText = "Hardware has gone to sleep.";
			txtLog.setForeground(Color.BLACK);
			txtLog.append(logText + "\n");
		}
	}
	
	
	/**
	 * initialises components and adds them to the layout container
	 */
	@SuppressWarnings("rawtypes")
	private void initComponents() {
		/*
		 * *********************************************BUTTON/LABEL INSTANTIATION**************************************
		 */
		jScrollPane1 = new javax.swing.JScrollPane();
		jTextArea1 = new javax.swing.JTextArea();
		controlPanelLabel = new javax.swing.JLabel();
		sensorNumberLabel = new javax.swing.JLabel();
		btnShowSensorSelectorPane = new javax.swing.JButton();
		btnDebug = new javax.swing.JButton();
		btnShowVisualGui = new javax.swing.JButton();
		btnShowDataGui = new javax.swing.JButton();
		cboxPorts = new javax.swing.JComboBox();
		comPortSelectLabel = new javax.swing.JLabel();
		btnConnect = new javax.swing.JButton();
		btnDisconnect = new javax.swing.JButton();
		logNameLabel = new javax.swing.JLabel();
		jScrollPane2 = new javax.swing.JScrollPane();
		txtLog = new javax.swing.JTextArea();
		jTextArea1.setColumns(20);
		jTextArea1.setRows(5);
		jScrollPane1.setViewportView(jTextArea1);

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("Haptic Feedback Case Control Panel");

		controlPanelLabel.setFont(new java.awt.Font("Tahoma", 1, 14));
		controlPanelLabel.setText("Haptic Feedback Case Control Panel");

		sensorNumberLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

		btnShowSensorSelectorPane.setEnabled(false);
		btnShowSensorSelectorPane.setText("Show Sensor Selector");
		btnShowSensorSelectorPane.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				performSensorSelectorGUI(evt);
			}
		});

		btnDebug.setText("Debug");
		btnDebug.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				performDebugBtn(evt);
			}
		});

		btnShowVisualGui.setEnabled(false);
		btnShowVisualGui.setText("Show Visual GUI");
		btnShowVisualGui.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				performTogVisualGUI(evt);
			}
		});

		btnShowDataGui.setEnabled(false);
		btnShowDataGui.setText("Show Numeric Data");
		btnShowDataGui.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				performTogDataGUI(evt);
			}
		});

		comPortSelectLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
		comPortSelectLabel.setText("Select the COM Port");

		btnConnect.setText("Connect");
		btnConnect.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnConnectActionPerformed(evt);
			}
		});

		btnDisconnect.setText("Disconnect");
		btnDisconnect.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
					btnDisconnectActionPerformed(evt);
			}
		});

		logNameLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
		logNameLabel.setText("Log");

		/**
		 * caret keeps the log text area auto scrolled to the bottom of it's
		 * page
		 */
		DefaultCaret caret = (DefaultCaret) txtLog.getCaret();

		txtLog.setColumns(20);
		txtLog.setEditable(false);
		txtLog.setLineWrap(true);
		txtLog.setRows(5);
		txtLog.setFocusable(false);
		jScrollPane2.setViewportView(txtLog);

		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		/*
		 ****************************************************** JFrame components Layout **********************
		 */
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(controlPanelLabel)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(layout.createSequentialGroup()
												.addComponent(cboxPorts, javax.swing.GroupLayout.PREFERRED_SIZE, 69,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(btnConnect)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(btnDisconnect))
										.addComponent(comPortSelectLabel)
										.addGroup(layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
												.addGroup(javax.swing.GroupLayout.Alignment.LEADING,
														layout.createSequentialGroup()
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																.addComponent(sensorNumberLabel,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		Short.MAX_VALUE))
												.addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout
														.createSequentialGroup()
														.addGroup(layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.LEADING)
																.addComponent(btnDebug)
																.addGroup(layout
																		.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.TRAILING,
																				false)
																		.addComponent(btnShowSensorSelectorPane,
																				javax.swing.GroupLayout.Alignment.LEADING)))
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addGroup(layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.LEADING)
																.addComponent(btnShowDataGui)
																.addGroup(layout.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.TRAILING,
																		false).addComponent(btnShowVisualGui,
																				javax.swing.GroupLayout.Alignment.LEADING)))))
										.addGroup(
												layout.createSequentialGroup()
														.addGroup(layout.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING))
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addGroup(layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING))
												.addGap(3, 3, 3)
												.addGroup(layout.createParallelGroup(
														javax.swing.GroupLayout.Alignment.LEADING))))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(logNameLabel).addComponent(jScrollPane2,
												javax.swing.GroupLayout.PREFERRED_SIZE, 333,
												javax.swing.GroupLayout.PREFERRED_SIZE))))
						.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap().addComponent(controlPanelLabel)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(comPortSelectLabel).addComponent(logNameLabel))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
						.addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
						.addGroup(layout.createSequentialGroup().addGroup(layout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(cboxPorts, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(btnConnect).addComponent(btnDisconnect))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(sensorNumberLabel))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(layout.createSequentialGroup()
														.addComponent(btnShowSensorSelectorPane)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(btnDebug))
										.addGroup(layout.createSequentialGroup().addComponent(btnShowVisualGui)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(btnShowDataGui)))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(layout.createSequentialGroup()
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
										.addGroup(layout.createSequentialGroup()
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
										.addGroup(layout.createSequentialGroup()
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))))
				.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		pack();
	}
}