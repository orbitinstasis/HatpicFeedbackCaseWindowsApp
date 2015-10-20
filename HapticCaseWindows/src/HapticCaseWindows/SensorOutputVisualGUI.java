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

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;

import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.UIManager;

/*
 * 
 * the radius of the circle is going to be 19 px.
 *  
 * For the canvas blocks, the main canvas for the xyz has been sized so THE X,Y SIZE of each xyz block is 25x25
 */
@SuppressWarnings("serial")
public class SensorOutputVisualGUI extends javax.swing.JFrame {

	/*
	 * ************************************************************** GLOBAL
	 */   
	protected JPanel[] panel = new JPanel[5];
	protected Canvas[] canvas = new Canvas[5];
    private GroupLayout[] grouplayout = new GroupLayout[5];
    protected Graphics gc[] = new Graphics[5]; // this gc is used for each canvas
    ControlPanelGui window = null;
    SensorOutputVisualGUI visualgui = null;
    Graphics2D[] g2d =  new Graphics2D[5];
	/*
	 * ************************************************************** CONSTANTS
	 */
    protected static Color stripColor = Color.decode("#d35400");
    protected static Color xyzColor = Color.decode("#c0392b");
    
    /**
     * ************************************************************* CONSRTUCTOR
     * @param window 
     */
    public SensorOutputVisualGUI(ControlPanelGui window) {
    	this.window = window;
        initComponents();

        
        
        for (int i = 0; i < 5; i++) {
        	canvas[i].setBackground(Color.BLACK);
        	canvas[i].setForeground(Color.BLACK);
        	gc[i] = canvas[i].getGraphics();
        	g2d[i] = (Graphics2D)gc[i];
        	


//        	if (i < 4)
//        		gc[i].setColor(new Color(211,84,0,(25 + 25*i)));
//        	else 
//        		gc[i].setColor(xyzColor);
        }
        
//        System.out.println("window.visualgui.panel[3].getHeight());
        
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
	 * ********************************************************************************** METHODs 
	 */  
    
    protected void cleanCanvas(int id) {
    	gc[id].setColor(Color.BLACK);
    	if (id < 4) {
    		gc[id].fillRect(0, 0, canvas[id].getWidth(), canvas[id].getHeight());
    		gc[id].setColor(Color.decode("#d35400"));
    	} else {
    		gc[id].fillRect(0, 0, canvas[id].getWidth(),canvas[id].getHeight());
    		gc[id].setColor(Color.decode("#c0392b"));
    	}
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
        setTitle("Sensor Data Visual Display");
        setBackground(new java.awt.Color(255, 255, 255));
        setForeground(java.awt.Color.white);
        setResizable(false);
    	
        for (int i = 0; i < 5; i++) {
        	panel[i] = new JPanel();
        	canvas[i] = new Canvas();
        	canvas[i].setBackground(new java.awt.Color(255, 255, 255));
        	grouplayout[i] = new GroupLayout(panel[i]);
        }

		/*
		 ****************************************************** JFrame components Layout **********************
		 */
        for (int i = 0; i < 4; i++) { // layout the side sensors 
            panel[i].setBackground(new java.awt.Color(255, 255, 255));
            panel[i].setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Side " + (i+1), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 10))); // NOI18N
            panel[i].setPreferredSize(new java.awt.Dimension(50, 200));
            panel[i].setLayout(grouplayout[i]);
            grouplayout[i].setHorizontalGroup(
            		grouplayout[i] .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(canvas[i], javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            );
            grouplayout[i].setVerticalGroup(
            		grouplayout[i].createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(canvas[i], javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            );
        }

        //xyz***************************************************
        panel[4].setBackground(new java.awt.Color(255, 255, 255));
        panel[4].setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Rear XYZ Pad", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 10))); // NOI18N
        canvas[4].setPreferredSize(new java.awt.Dimension(250, 400));
        panel[4].setLayout(grouplayout[4]);
        grouplayout[4].setHorizontalGroup(
            grouplayout[4].createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(canvas[4], javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        grouplayout[4].setVerticalGroup(
            grouplayout[4].createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(canvas[4], javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel[2], javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panel[3], javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(panel[4], javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel[0], javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panel[1], javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panel[3], javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panel[0], javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel[2], javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panel[1], javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addComponent(panel[4], javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        pack();
    }
}