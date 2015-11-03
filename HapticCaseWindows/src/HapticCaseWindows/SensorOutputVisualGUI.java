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
public class SensorOutputVisualGUI extends javax.swing.JFrame implements Runnable {

	/*
	 * ************************************************************** GLOBAL
	 */   
	private  JPanel[] panel = new JPanel[5];
	private  Canvas[] canvas = new Canvas[5];
	private  GroupLayout[] grouplayout = new GroupLayout[5];
	private  Graphics gc[] = new Graphics[5]; // this gc is used for each canvas
    private ControlPanelGui window = null;
    protected SensorOutputVisualGUI visualgui = null;
    protected Graphics2D[] g2d =  new Graphics2D[5];
    protected Thread visualGuiUpdater = new Thread(this);

	/*
	 * ************************************************************** CONSTANTS
	 */
//    private static Color stripColor = Color.decode("#d35400");
//    private static Color xyzColor = Color.decode("#c0392b");
//    
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
        }
        
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int y = 2;
		this.setLocation(dim.width/y+this.getSize().width/y, dim.height/3+this.getSize().height/3);
        
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				setVisible(false);
				window.btnShowVisualGui.setEnabled(true);
				dispose();
			}
		});
    }
       
	/*
	 * ********************************************************************************** RUNNER 
	 */  
    boolean isFirstReading = false; //DELETE THIS - IT GOES WITH THE THUMB SCROLLER
    int count = 0;  // DEBUG 
	@Override
	public void run() {
		while (window.communicator.isConsuming) {
			if (!window.communicator.isAsleep) {
	
				/*
				 * Next chunk of code is the thumb scroller
				 * 
				 * we want to add a tolerance for the position value so it doesn't pick up noise			
				 */
				final int tempSensor = 3;
				int startCurrentForce = window.communicator.controller.modelState.getCurrentSideSensor(tempSensor, 0);
				int startOldForce = window.communicator.controller.modelState.getOldSideSensor(tempSensor, 0);
				if (startOldForce != startCurrentForce) {
					final int ERR = 4;
					int speed = 0;
					int startOldPosition = window.communicator.controller.modelState.getOldSideSensor(tempSensor, 1);
					int startCurrentPosition = window.communicator.controller.modelState.getCurrentSideSensor(tempSensor, 1);
					 if (isFirstReading) { //avoid bogus reading from an invalid position reading
				         isFirstReading = false;
				     } else {
				         if (startCurrentForce > 25 && (Math.abs(startOldPosition - startCurrentPosition) >= ERR)) { 
				             speed = ((int) window.communicator.controller.map(startCurrentForce, 0, 120, 0, 100)) * Math.abs(startCurrentPosition - startOldPosition); // multiplier is force multiplied by difference of strp position
				             if (startCurrentPosition >= startOldPosition)  //moving down
				                 speed *= -1;
				             speed = (Integer.signum(speed) *  (int) window.communicator.controller.map(Math.abs(speed), 0, 1500, 0, 5));
				             if (speed != 0) {
					             if (speed > 4) //accelerate when applied (excessive) force 
					            	 speed += 4;
				            	 int sleep = 3*Math.abs(speed);
				            	 try {Thread.sleep(sleep);} catch (InterruptedException e) {e.printStackTrace();}
				            	 System.out.println("\n\nSpeed: " + speed + ",    Count: " + count++ + ",     Sleep: " + sleep); 
					             window.communicator.controller.robot.mouseWheel(speed); // do the scroll
				             }
				         }
				     }
				     if (startCurrentForce < 1) {
				         isFirstReading = true;
				         window.communicator.controller.modelState.setOldSideSensor(tempSensor, 1, 0);
				     }
				     /*
				      * note that we want to essneitally save this old side sensor value automatically in the model class (same as in visual gui - these classes shouldn't be modifying the model)
				      */
				     window.communicator.controller.modelState.setOldSideSensor(tempSensor, 1, startCurrentPosition);
				}
				
				
				
				
				
				
				
				
				/*
				 * Next chunk of code (two fors) is the intended runner code.
				 */
				
//				/*
//				 * for each side strip sensor
//				 */
//                for (int i = 0; i < 4; i++) {
//                    int force = window.communicator.controller.modelState.getCurrentSideSensor(i, 0);
//                    if (force != window.communicator.controller.modelState.getOldSideSensor(i, 0)) { // if we're only dealing with changing cells
////                    	System.out.println("force != getoldSensorForce");
//	                    if (force > 0) {
//	                        int position = (int) window.communicator.controller.map(window.communicator.controller.modelState.getCurrentSideSensor(i, 1), 0, 254, 0, window.visualgui.canvas[i].getHeight());
//	        				window.visualgui.g2d[i].setColor(Color.BLACK);
//	        				window.visualgui.g2d[i].fillRect(0, 0, window.visualgui.canvas[0].getWidth(), window.visualgui.canvas[0].getHeight());
//	                        window.visualgui.g2d[i].setColor(Color.WHITE);
//	                        window.visualgui.g2d[i].setComposite(AlphaComposite.getInstance(AlphaComposite.SRC,((window.communicator.controller.map(force, 0, 100, 0, 1))) ));
//	                        window.communicator.controller.modelState.setOldSideSensor(i, 0,  window.communicator.controller.modelState.getCurrentSideSensor(i, 0)); //save old force
//                        	int temp = (int)(window.communicator.controller.map(force, 0, 60, 10, 38));
//                        	window.visualgui.g2d[i].fillOval((window.visualgui.canvas[0].getWidth()/2 - temp/2), (position-19), temp, temp);
//                        }
//                    }
//                }
//				
//                /*
//                 * do XYZ here
//                 */
//                for (int i = 0; i < 10; i++) {
//                    for (int j = 0; j < 16; j++) {
//                    	int force = window.communicator.controller.modelState.getCurrentXYZ(i, j);
//                        if (force != window.communicator.controller.modelState.getOldXYZ(i, j)) {
//                        	int RECT_SIZE = 25;
//            				window.visualgui.g2d[4].setColor(Color.BLACK);
//            				window.visualgui.g2d[4].fillRect((RECT_SIZE*i),(j*RECT_SIZE),(RECT_SIZE),(RECT_SIZE));
//                            window.communicator.controller.modelState.setOldXYZ(i, j, window.communicator.controller.modelState.getCurrentXYZ(i, j));
//                        	if (force > 0) {
//                                window.visualgui.g2d[4].setColor(Color.WHITE);
//                                window.visualgui.g2d[4].setComposite(AlphaComposite.getInstance(AlphaComposite.SRC,((window.communicator.controller.map(force, 0, 100, 0, 1))) ));
//                        		window.visualgui.g2d[4].fillRect((i * RECT_SIZE), (j * RECT_SIZE), (RECT_SIZE), (RECT_SIZE));
//                        	}
//                        }
//                    }
//                }
                
                
                
                
                
			}
		}
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
    protected void visualGUImain(SensorOutputVisualGUI visualgui) {
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