package HapticCaseWindows;

import java.awt.Font;
import java.awt.event.WindowAdapter;

import javax.swing.JLabel;
import javax.swing.UIManager;

/**
 *
 * @author Orbital
 */
@SuppressWarnings("serial")
public class SensorOutputDataGUI extends javax.swing.JFrame implements Runnable {
	/*
	 * GLOBALS
	 */
	// JLabels
	static protected JLabel[][] sideSensor;
	protected JLabel[] forceLabel;
	static protected JLabel[][] padCellData;
	protected JLabel[] positionLabel;
	protected JLabel[] sensorLabel;
	protected JLabel rearPadLabel;
	// JPanels
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel rearXYZDataPanel;
	private javax.swing.JPanel sensorDataMainPanel;
	// Passed in from gui
	ControlPanelGui window = null;
	SensorOutputDataGUI datagui = null;
	Thread dataGuiUpdater = new Thread(this);

	/**
	 * CONSTRUCTOR
	 * 
	 * @param connectorGUI
	 */
	public SensorOutputDataGUI(ControlPanelGui window) {
		this.window = window;
		initComponents();
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				setVisible(false);
				window.btnShowDataGui.setEnabled(true);
				dispose();
			}
		});
	}

	@Override
	public void run() {
		while (window.communicator.isConsuming) {
			if (!window.communicator.isAsleep) { // DEBUG
				for (int i = 0; i < 4; i++) {
					for (int j = 0; j < 2; j++) {
						int tempInt = window.communicator.controller.getCurrentSideSensor(i, j);
						String tempString = new String();
						if (tempInt < 10) {
							tempString = "00";
						} else if (tempInt < 100) {
							tempString = "0";
						}

						SensorOutputDataGUI.sideSensor[i][j].setText(tempString + tempInt);
					}
				}
				for (int i = 0; i < 10; i++) {
					for (int j = 0; j < 16; j++) {
						SensorOutputDataGUI.padCellData[i][j]
								.setText("" + window.communicator.controller.getCurrentXYZ(i, j));
					}
				}
			}
		}
	}

	protected void toggleReadingFont(int i, boolean isOn) {
		// System.out.println("sensor: " + in + " is " + state);
		if (i < 4) {
			if (isOn) {
				sensorLabel[i].setText("Sensor " + (i + 1) + " On");
				sensorLabel[i].setFont(
						new Font(sensorLabel[i].getFont().getName(), Font.BOLD, sensorLabel[i].getFont().getSize()));
				forceLabel[i].setFont(
						new Font(forceLabel[i].getFont().getName(), Font.BOLD, forceLabel[i].getFont().getSize()));
				positionLabel[i].setFont(new Font(positionLabel[i].getFont().getName(), Font.BOLD,
						positionLabel[i].getFont().getSize()));
				sideSensor[i][0].setFont(new Font(sideSensor[i][0].getFont().getName(), Font.BOLD,
						sideSensor[i][0].getFont().getSize()));
				sideSensor[i][1].setFont(new Font(sideSensor[i][1].getFont().getName(), Font.BOLD,
						sideSensor[i][1].getFont().getSize()));
			} else {
				sensorLabel[i].setText("Sensor " + (i + 1) + " Off");
				sensorLabel[i].setFont(
						new Font(sensorLabel[i].getFont().getName(), Font.ITALIC, sensorLabel[i].getFont().getSize()));
				forceLabel[i].setFont(
						new Font(forceLabel[i].getFont().getName(), Font.ITALIC, forceLabel[i].getFont().getSize()));
				positionLabel[i].setFont(new Font(positionLabel[i].getFont().getName(), Font.ITALIC,
						positionLabel[i].getFont().getSize()));
				sideSensor[i][0].setText("000");
				sideSensor[i][0].setText("000");
				sideSensor[i][0].setFont(new Font(sideSensor[i][0].getFont().getName(), Font.ITALIC,
						sideSensor[i][0].getFont().getSize()));
				sideSensor[i][1].setFont(new Font(sideSensor[i][1].getFont().getName(), Font.ITALIC,
						sideSensor[i][1].getFont().getSize()));
			}
		} else {
			for (int k = 0; k < 10; k++) {
				for (int j = 0; j < 16; j++) {
					if (isOn)
						padCellData[k][j].setFont(new Font(padCellData[k][j].getFont().getName(), Font.BOLD,
								padCellData[k][j].getFont().getSize()));
					else {
						padCellData[k][j].setText("0");
						padCellData[k][j].setFont(new Font(padCellData[k][j].getFont().getName(), Font.ITALIC,
								padCellData[k][j].getFont().getSize()));
					}
				}
			}
		}
	}

	/**
	 * @param datagui
	 */
	public void numericGUImain(SensorOutputDataGUI datagui) {
		this.datagui = datagui;
		try {
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				datagui.setVisible(true);
			}
		});
	}

	private void initComponents() {
		forceLabel = new JLabel[4];
		positionLabel = new JLabel[4];
		sensorLabel = new JLabel[4];
		sideSensor = new JLabel[4][2];
		rearPadLabel = new JLabel("   Rear Pad:");
		padCellData = new JLabel[10][16];

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 16; j++) {
				padCellData[i][j] = new JLabel("0");
			}
		}

		for (int i = 0; i < 4; i++) {
			sensorLabel[i] = new JLabel("Sensor " + (i + 1));
			forceLabel[i] = new JLabel("Force: ");
			positionLabel[i] = new JLabel("Position: ");
			for (int j = 0; j < 2; j++) {
				sideSensor[i][j] = new JLabel("000");
			}
		}
		jPanel2 = new javax.swing.JPanel();
		sensorDataMainPanel = new javax.swing.JPanel();
		rearXYZDataPanel = new javax.swing.JPanel();

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 100, Short.MAX_VALUE));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 100, Short.MAX_VALUE));

		setTitle("Sensor Data Numeric Output");
		setResizable(false);

		sensorDataMainPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

		rearXYZDataPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

		javax.swing.GroupLayout rearXYZDataPanelLayout = new javax.swing.GroupLayout(rearXYZDataPanel);
		rearXYZDataPanel.setLayout(rearXYZDataPanelLayout);
		rearXYZDataPanelLayout.setHorizontalGroup(
				rearXYZDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(rearXYZDataPanelLayout.createSequentialGroup()
								.addGroup(rearXYZDataPanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING))
						// .addComponent(rearPadLabel,
						// javax.swing.GroupLayout.PREFERRED_SIZE,
						// javax.swing.GroupLayout.DEFAULT_SIZE,
						// javax.swing.GroupLayout.PREFERRED_SIZE))
						// .addComponent(label1,
						// javax.swing.GroupLayout.PREFERRED_SIZE,
						// javax.swing.GroupLayout.DEFAULT_SIZE,
						// javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(0, 170, Short.MAX_VALUE)));

		rearXYZDataPanelLayout
				.setVerticalGroup(rearXYZDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(rearXYZDataPanelLayout.createSequentialGroup()
								// .addComponent(rearPadLabel,
								// javax.swing.GroupLayout.PREFERRED_SIZE,
								// javax.swing.GroupLayout.DEFAULT_SIZE,
								// javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								// .addComponent(label1,
								// javax.swing.GroupLayout.PREFERRED_SIZE,
								// javax.swing.GroupLayout.DEFAULT_SIZE,
								// javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(0, 350, Short.MAX_VALUE)));

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 16; j++) {
				rearXYZDataPanel.add(padCellData[i][j]);
				padCellData[i][j].setLocation(((i * 38) + 30), (j * 22));
				padCellData[i][j].setSize(86, 14);
			}
		}

		javax.swing.GroupLayout sensorDataMainPanelLayout = new javax.swing.GroupLayout(sensorDataMainPanel);
		sensorDataMainPanel.setLayout(sensorDataMainPanelLayout);
		sensorDataMainPanelLayout.setHorizontalGroup(

		sensorDataMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(sensorDataMainPanelLayout.createSequentialGroup()

		.addGroup(sensorDataMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(sensorLabel[0], javax.swing.GroupLayout.PREFERRED_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addGroup(sensorDataMainPanelLayout.createSequentialGroup()
						.addGroup(sensorDataMainPanelLayout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(forceLabel[0], javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(positionLabel[0], javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(4, 4, 4)
						.addGroup(sensorDataMainPanelLayout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(sideSensor[0][1], javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(sideSensor[0][0], javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
						.addGap(30, 30, 30)

		.addGroup(sensorDataMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(sensorLabel[1], javax.swing.GroupLayout.PREFERRED_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addGroup(sensorDataMainPanelLayout.createSequentialGroup()
						.addGroup(sensorDataMainPanelLayout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(forceLabel[1], javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(positionLabel[1], javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(4, 4, 4)
						.addGroup(sensorDataMainPanelLayout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(sideSensor[1][1], javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(sideSensor[1][0], javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
						.addGap(30, 30, 30)

		.addGroup(sensorDataMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(sensorLabel[2], javax.swing.GroupLayout.PREFERRED_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addGroup(sensorDataMainPanelLayout.createSequentialGroup()
						.addGroup(sensorDataMainPanelLayout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(forceLabel[2], javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(positionLabel[2], javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(4, 4, 4)
						.addGroup(sensorDataMainPanelLayout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(sideSensor[2][1], javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(sideSensor[2][0], javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
						.addGap(30, 30, 30)

		.addGroup(sensorDataMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(sensorLabel[3], javax.swing.GroupLayout.PREFERRED_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addGroup(sensorDataMainPanelLayout.createSequentialGroup()
						.addGroup(sensorDataMainPanelLayout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(forceLabel[3], javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(positionLabel[3], javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(4, 4, 4)
						.addGroup(sensorDataMainPanelLayout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(sideSensor[3][1], javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(sideSensor[3][0], javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
						.addGap(30, 30, 30))

		.addComponent(rearXYZDataPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
				Short.MAX_VALUE));

		sensorDataMainPanelLayout.setVerticalGroup(sensorDataMainPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(sensorDataMainPanelLayout.createSequentialGroup().addGroup(sensorDataMainPanelLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(sensorDataMainPanelLayout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
								.addComponent(sideSensor[0][1], javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGroup(sensorDataMainPanelLayout.createSequentialGroup().addComponent(sensorLabel[0],
										javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(sensorDataMainPanelLayout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(forceLabel[0], javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(sideSensor[0][0], javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(positionLabel[0], javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addComponent(sideSensor[1][1], javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGroup(sensorDataMainPanelLayout.createSequentialGroup().addComponent(sensorLabel[1],
										javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(sensorDataMainPanelLayout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(forceLabel[1], javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(sideSensor[1][0], javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(positionLabel[1], javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addComponent(sideSensor[2][1], javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGroup(sensorDataMainPanelLayout.createSequentialGroup().addComponent(sensorLabel[2],
										javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(sensorDataMainPanelLayout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(forceLabel[2], javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(sideSensor[2][0], javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(positionLabel[2], javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)))
						.addGroup(sensorDataMainPanelLayout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
								.addComponent(sideSensor[3][1], javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGroup(sensorDataMainPanelLayout.createSequentialGroup().addComponent(sensorLabel[3],
										javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(sensorDataMainPanelLayout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(forceLabel[3], javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(sideSensor[3][0], javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(positionLabel[3], javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE))))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(rearXYZDataPanel, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(sensorDataMainPanel,
						javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				sensorDataMainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
				javax.swing.GroupLayout.PREFERRED_SIZE));
		pack();
	}

}
