package Controller;

import Model.*;
import View.SimulationView;
import java.util.ArrayList;
import java.util.Random;

public class ParallelsBodySimulationMain {

    public static void main(String[] args) {

        SimulationView viewer = new SimulationView(620,620);

        // Per testare il programma con JPF occorre:
        //      - Decommentare riga 20-33 nella classe ParallelsBodySimulationMain
        //      - Commentare riga 12 nella classe ParallelsBodySimulationMain
        //      - Commentare righe 81-84 nella classe MyWorkerAgent

        /*
        Random rand = new Random();
        int nSteps = 2;
        int numBalls = 4;

        Boundary bounds = new Boundary(-4.0, -4.0, 4.0, 4.0);
        ArrayList<Body> bodies = new ArrayList<>();

        for (int i = 0; i < numBalls; i++) {
            double x = bounds.getX0()*0.25 + rand.nextDouble() * (bounds.getX1() - bounds.getX0()) * 0.25;
            double y = bounds.getY0()*0.25 + rand.nextDouble() * (bounds.getY1() - bounds.getY0()) * 0.25;
            bodies.add(new Body(i, new P2d(x, y), new V2d(0, 0), 10));
        }
        MyMasterAgent myMasterAgent = new MyMasterAgent(bodies, bounds, 0, 0.001, nSteps, null, new Flag());
        myMasterAgent.start();
        */
    }

}


