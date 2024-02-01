package Controller;

import Model.*;
import View.SimulationView;
import java.util.ArrayList;
import java.util.Random;

/**
 * Controller for the GUI.
 *
 */
public class ControllerGui {

	private final Flag stopFlag;
	private final SimulationView viewer;
	private static final int nBalls = 2000;
	private static final int nSteps = 2000;
	private final Random rand = new Random(System.currentTimeMillis());

	/**
	 * Creates a new Flag
	 *
	 * @param viewer the SimulationView
	 */
	public ControllerGui(SimulationView viewer) {
		this.stopFlag = new Flag();
		this.viewer = viewer;
	}

	/**
	 * Starts the simulation creating balls and starting the Master Agent.
	 *
	 */
	public void notifyStarted() {
		Boundary bounds = new Boundary(-4.0, -4.0, 4.0, 4.0);
		ArrayList<Body> bodies = new ArrayList<>();

		for (int i = 0; i < nBalls; i++) {
			double x = bounds.getX0()*0.25 + rand.nextDouble() * (bounds.getX1() - bounds.getX0()) * 0.25;
			double y = bounds.getY0()*0.25 + rand.nextDouble() * (bounds.getY1() - bounds.getY0()) * 0.25;
			bodies.add(new Body(i, new P2d(x, y), new V2d(0, 0), 10));
		}

		MyMasterAgent myMasterAgent = new MyMasterAgent(bodies, bounds, 0, 0.001, nSteps, viewer, stopFlag);
		myMasterAgent.start();
	}

	/**
	 * Stops the simulation.
	 *
	 */
	public void notifyStopped() {
		stopFlag.set();
	}
}
