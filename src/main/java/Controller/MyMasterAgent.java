package Controller;

import Model.Body;
import Model.Boundary;
import Model.Flag;
import Model.TaskCompletionLatch;
import View.SimulationView;
import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;

/**
 * The Master Agent.
 *
 */
public class MyMasterAgent extends Thread{

    private final int nSteps;
    private final int NUM_CORE = 2;
    private final ArrayList<Body> bodies;
    private final Boundary bounds;
    private final double vt;
    private final double dt;
    private final TaskCompletionLatch sync;
    private final int nBalls;
    private final CyclicBarrier barrier1;
    private final CyclicBarrier barrier2;
    private final CyclicBarrier barrier3;
    private final SimulationView viewer;
    private final Flag stopFlag;

    /**
     * Creates the Master Agent.
     *
     */
    public MyMasterAgent(ArrayList<Body> bodies,
                         Boundary bounds,
                         double vt,
                         double dt,
                         int nSteps,
                         SimulationView viewer,
                         Flag stopFlag
                        ) {
        this.bodies = bodies;
        this.bounds = bounds;
        this.vt = vt;
        this.dt = dt;
        this.nSteps = nSteps;
        this.viewer = viewer;
        this.stopFlag = stopFlag;
        this.nBalls = this.bodies.size();
        this.barrier1 = new CyclicBarrier(NUM_CORE);
        this.barrier2 = new CyclicBarrier(NUM_CORE);
        this.barrier3 = new CyclicBarrier(NUM_CORE);
        this.sync = new TaskCompletionLatch(NUM_CORE);
    }

    /**
     * Body of the Thread Master.
     *
     */
    public void run(){
        log("Starting || " + "Core: " + NUM_CORE + " || Num balls: " + this.nBalls);
        long t0 = System.currentTimeMillis();

        assignBallsToWorkers();

        try {
            // Waiting for the workers.
            sync.waitCompletion();
            log("Completion arrived");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long t1 = System.currentTimeMillis();
        log("Completed || Time elapsed: " + (t1 - t0));
    }

    private void log(String msg){
        synchronized(System.out){
            System.out.println("[ master ] " + msg);
        }
    }

    /**
     * Algorithm that assigns a balanced number of balls to every worker and runs them.
     *
     */
    private void assignBallsToWorkers(){
        int ballsForCore = this.bodies.size() / this.NUM_CORE;
        int eventuallyRemainBalls = this.bodies.size() % this.NUM_CORE;
        int workerCreated = 0;
        int contTotalBalls = 0;
        boolean flag = true;

        while(contTotalBalls < this.bodies.size()){

            if(workerCreated >= (NUM_CORE - eventuallyRemainBalls) && flag){
                ballsForCore = ballsForCore + 1;
                flag = false;
            }

            createNewWorker(contTotalBalls, ballsForCore);
            contTotalBalls = contTotalBalls + ballsForCore;
            workerCreated = workerCreated + 1;
        }
    }

    /**
     * Creates a new Worker Agent.
     *
     * @param startIndex Index of the first ball assigned to the worker.
     * @param numBallsForThisWorker Number of balls of the worker.
     */
    private void createNewWorker(int startIndex, int numBallsForThisWorker){
        MyWorkerAgent myWorkerAgent = new MyWorkerAgent(this.bodies, startIndex, numBallsForThisWorker, this.dt, this.vt, this.bounds, this.nSteps, this.barrier1, this.barrier2, this.barrier3, this.sync, this.viewer, this.stopFlag);
        myWorkerAgent.start();
    }

}


