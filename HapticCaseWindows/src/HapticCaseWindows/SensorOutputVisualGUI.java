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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;

import javax.swing.UIManager;

@SuppressWarnings("serial")
public class SensorOutputVisualGUI extends javax.swing.JFrame {

	/*
	 * ************************************************************** GLOBAL
	 */
    private java.awt.Canvas rearXYZCanvas;
    private javax.swing.JPanel rearXYZPanel;
    private java.awt.Canvas side1Canvas;
    private java.awt.Canvas side2Canvas;
    private java.awt.Canvas side3Canvas;
    private java.awt.Canvas side4Canvas;
    private javax.swing.JPanel sideSensor1Panel;
    private javax.swing.JPanel sideSensor2Panel;
    private javax.swing.JPanel sideSensor3Panel;
    private javax.swing.JPanel sideSensor4Panel;  
    ControlPanelGui window = null;
    SensorOutputVisualGUI visualgui = null;
    /**
     * ************************************************************* CONSRTUCTOR
     * @param window 
     */
    public SensorOutputVisualGUI(ControlPanelGui window) {
    	this.window = window;
        initComponents();
        
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int y = 2;
		this.setLocation(dim.width/y+this.getSize().width/y, dim.height/3+this.getSize().height/3);
        
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				setVisible(false);
				window.btnShowVisualGui.setEnabled(true);
				dispose();
			}
		});
    }
              
	/*
	 * **********************************************************************************MAIN METHOD 
	 */                   

    /**
     * Main method that instantiates this class and visualgui
     * 
     * @param visualgui
     */
    public void visualGUImain(SensorOutputVisualGUI visualgui) {
    	this.visualgui = visualgui;
		try {
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                visualgui.setVisible(true);
            }
        });
    }

         
    private void initComponents() {
		/*
		 * *********************************************BUTTON/LABEL INSTANTIATION**************************************
		 */
        sideSensor2Panel = new javax.swing.JPanel();
        side2Canvas = new java.awt.Canvas();
        sideSensor3Panel = new javax.swing.JPanel();
        side3Canvas = new java.awt.Canvas();
        sideSensor1Panel = new javax.swing.JPanel();
        side1Canvas = new java.awt.Canvas();
        sideSensor4Panel = new javax.swing.JPanel();
        side4Canvas = new java.awt.Canvas();
        rearXYZPanel = new javax.swing.JPanel();
        rearXYZCanvas = new java.awt.Canvas();

        setTitle("Sensor Data Visual Display");
        setBackground(new java.awt.Color(255, 255, 255));
        setForeground(java.awt.Color.white);
        setResizable(false);

		/*
		 ****************************************************** JFrame components Layout **********************
		 */
        
        sideSensor2Panel.setBackground(new java.awt.Color(255, 255, 255));
        sideSensor2Panel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Side 2", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 10))); // NOI18N
        sideSensor2Panel.setForeground(new java.awt.Color(255, 255, 255));
        sideSensor2Panel.setPreferredSize(new java.awt.Dimension(50, 200));

        javax.swing.GroupLayout sideSensor2PanelLayout = new javax.swing.GroupLayout(sideSensor2Panel);
        sideSensor2Panel.setLayout(sideSensor2PanelLayout);
        sideSensor2PanelLayout.setHorizontalGroup(
            sideSensor2PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(side2Canvas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        sideSensor2PanelLayout.setVerticalGroup(
            sideSensor2PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(side2Canvas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        sideSensor3Panel.setBackground(new java.awt.Color(255, 255, 255));
        sideSensor3Panel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Side 3", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 10))); // NOI18N
        sideSensor3Panel.setPreferredSize(new java.awt.Dimension(50, 200));

        javax.swing.GroupLayout sideSensor3PanelLayout = new javax.swing.GroupLayout(sideSensor3Panel);
        sideSensor3Panel.setLayout(sideSensor3PanelLayout);
        sideSensor3PanelLayout.setHorizontalGroup(
            sideSensor3PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(side3Canvas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        sideSensor3PanelLayout.setVerticalGroup(
            sideSensor3PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(side3Canvas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        sideSensor1Panel.setBackground(new java.awt.Color(255, 255, 255));
        sideSensor1Panel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Side 1", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 10))); // NOI18N
        sideSensor1Panel.setPreferredSize(new java.awt.Dimension(50, 200));

        javax.swing.GroupLayout sideSensor1PanelLayout = new javax.swing.GroupLayout(sideSensor1Panel);
        sideSensor1Panel.setLayout(sideSensor1PanelLayout);
        sideSensor1PanelLayout.setHorizontalGroup(
            sideSensor1PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(side1Canvas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        sideSensor1PanelLayout.setVerticalGroup(
            sideSensor1PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(side1Canvas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        sideSensor4Panel.setBackground(new java.awt.Color(255, 255, 255));
        sideSensor4Panel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Side 4", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 10))); // NOI18N
        sideSensor4Panel.setPreferredSize(new java.awt.Dimension(50, 200));

        javax.swing.GroupLayout sideSensor4PanelLayout = new javax.swing.GroupLayout(sideSensor4Panel);
        sideSensor4Panel.setLayout(sideSensor4PanelLayout);
        sideSensor4PanelLayout.setHorizontalGroup(
            sideSensor4PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(side4Canvas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        sideSensor4PanelLayout.setVerticalGroup(
            sideSensor4PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(side4Canvas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        rearXYZPanel.setBackground(new java.awt.Color(255, 255, 255));
        rearXYZPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Rear XYZ Pad", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 10))); // NOI18N

        rearXYZCanvas.setPreferredSize(new java.awt.Dimension(250, 400));

        javax.swing.GroupLayout rearXYZPanelLayout = new javax.swing.GroupLayout(rearXYZPanel);
        rearXYZPanel.setLayout(rearXYZPanelLayout);
        rearXYZPanelLayout.setHorizontalGroup(
            rearXYZPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(rearXYZCanvas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        rearXYZPanelLayout.setVerticalGroup(
            rearXYZPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(rearXYZCanvas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sideSensor3Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sideSensor4Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(rearXYZPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sideSensor1Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sideSensor2Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(sideSensor4Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sideSensor1Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sideSensor3Panel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sideSensor2Panel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addComponent(rearXYZPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        pack();
    }    
}