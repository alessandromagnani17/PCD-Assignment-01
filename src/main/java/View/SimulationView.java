package View;



import Controller.ControllerGui;
import Model.Body;
import Model.Boundary;
import Model.P2d;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/**
 * Simulation view
 *
 * @author aricci
 *
 */
public class SimulationView {
        
	private VisualiserFrame frame;
	ControllerGui controllerGui;
	
    /**
     * Creates a view of the specified size (in pixels)
     * 
     * @param w
     * @param h
     */
    public SimulationView(int w, int h){
		controllerGui = new ControllerGui(this);
    	frame = new VisualiserFrame(w,h, controllerGui);
    }

    public void display(ArrayList<Body> bodies, double vt, long iter, Boundary bounds){
		frame.display(bodies, vt, iter, bounds);
    }
    
    public static class VisualiserFrame extends JFrame {

        private VisualiserPanel panelWithBalls;
		private JPanel panelWithButtons;
		private JPanel panelMain;

		private ControllerGui controllerGui;
		private JButton start;
		private JButton stop;

        public VisualiserFrame(int w, int h, ControllerGui controllerGui){
            setTitle("Bodies Simulation");
            setSize(w,h);
            setResizable(false);
            panelWithBalls = new VisualiserPanel(w,h);
			panelWithButtons = new JPanel();
			panelWithButtons.setSize(300,300);
			panelMain = new JPanel(new BorderLayout());

			this.controllerGui = controllerGui;
			start = new JButton("start");
			stop = new JButton("stop");
			stop.setEnabled(false);

			// ActionListener for the start button.
			// On click the GUI controller start the simulation (method notifyStarted()).
			start.addActionListener((start) -> {
					System.out.println("Start");
					controllerGui.notifyStarted();
					this.start.setEnabled(false);
					this.stop.setEnabled(true);
			});

			// ActionListener for the stop button.
			// On click the GUI controller stop the simulation (method notifyStopped()).
			stop.addActionListener((stop) -> {
					System.out.println("Stop");
					controllerGui.notifyStopped();
					this.stop.setEnabled(false);
			});

			panelWithButtons.add(start);
			panelWithButtons.add(stop);

			panelMain.add(panelWithBalls, BorderLayout.CENTER);
			panelMain.add(panelWithButtons, BorderLayout.SOUTH);

			getContentPane().add(panelMain);

            addWindowListener(new WindowAdapter(){
    			public void windowClosing(WindowEvent ev){
    				System.exit(-1);
    			}
    			public void windowClosed(WindowEvent ev){
    				System.exit(-1);
    			}
    		});
    		this.setVisible(true);
        }
        
        public void display(ArrayList<Body> bodies, double vt, long iter, Boundary bounds){
        	try {
	        	SwingUtilities.invokeAndWait(() -> {
	        		panelWithBalls.display(bodies, vt, iter, bounds);
	            	repaint();
	        	});
        	} catch (Exception ex) {
				System.out.println(ex);
			}
        };
        
        public void updateScale(double k) {
        	panelWithBalls.updateScale(k);
        }    	
    }

    public static class VisualiserPanel extends JPanel implements KeyListener {
        
    	private ArrayList<Body> bodies;
    	private Boundary bounds;
    	
    	private long nIter;
    	private double vt;
    	private double scale = 1;
    	
        private long dx;
        private long dy;
        
        public VisualiserPanel(int w, int h){
            setSize(w,h);
            dx = w/2 - 20;
            dy = h/2 - 20;
			this.addKeyListener(this);
			setFocusable(true);
			setFocusTraversalKeysEnabled(false);
			requestFocusInWindow(); 
        }

        public void paint(Graphics g){    		    		
    		if (bodies != null) {
        		Graphics2D g2 = (Graphics2D) g;
        		
        		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        		          RenderingHints.VALUE_ANTIALIAS_ON);
        		g2.setRenderingHint(RenderingHints.KEY_RENDERING,
        		          RenderingHints.VALUE_RENDER_QUALITY);
        		g2.clearRect(0,0,this.getWidth(),this.getHeight());

        		
        		int x0 = getXcoord(bounds.getX0());
        		int y0 = getYcoord(bounds.getY0());
        		
        		int wd = getXcoord(bounds.getX1()) - x0;
        		int ht = y0 - getYcoord(bounds.getY1());
        		
    			g2.drawRect(x0, y0 - ht, wd, ht);
    			
	    		bodies.forEach( b -> {
	    			P2d p = b.getPos();
			        int radius = (int) (10*scale);
			        if (radius < 1) {
			        	radius = 1;
			        }
			        g2.drawOval(getXcoord(p.getX()),getYcoord(p.getY()), radius, radius); 
			    });		    
	    		String time = String.format("%.2f", vt);
	    		g2.drawString("Bodies: " + bodies.size() + " - vt: " + time + " - nIter: " + nIter + " (UP for zoom in, DOWN for zoom out)", 2, 20);
    		}
        }
        
        private int getXcoord(double x) {
        	return (int)(dx + x*dx*scale);
        }

        private int getYcoord(double y) {
        	return (int)(dy - y*dy*scale);
        }
        
        public void display(ArrayList<Body> bodies, double vt, long iter, Boundary bounds){
            this.bodies = bodies;
            this.bounds = bounds;
            this.vt = vt;
            this.nIter = iter;
        }
        
        public void updateScale(double k) {
        	scale *= k;
        }

		@Override
		public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 38){  		/* KEY UP */
					scale *= 1.1;
				} else if (e.getKeyCode() == 40){  	/* KEY DOWN */
					scale *= 0.9;  
				} 
		}

		public void keyReleased(KeyEvent e) {}
		public void keyTyped(KeyEvent e) {}
    }
}
