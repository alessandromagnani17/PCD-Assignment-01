package Controller;

import Model.*;
import View.SimulationView;
import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;


/**
 * The Worker Agent.
 *
 */
public class MyWorkerAgent extends Thread{

    private final ArrayList<Body> bodies;
    private final double dt;
    private final Boundary bounds;
    private final int nSteps;
    private final CyclicBarrier barrier1;
    private final CyclicBarrier barrier2;
    private final CyclicBarrier barrier3;
    private final TaskCompletionLatch sync;
    private final SimulationView viewer;
    private final int startIndex;
    private final int numBallForThisWorker;
    private double vt;
    private int iter = 0;
    private final Flag stopFlag;

    public MyWorkerAgent(ArrayList<Body> bodies,
                         int starIndex,
                         int numBallForThisWorker,
                         double dt,
                         double vt,
                         Boundary bounds,
                         int nSteps,
                         CyclicBarrier barrier1,
                         CyclicBarrier barrier2,
                         CyclicBarrier barrier3,
                         TaskCompletionLatch sync,
                         SimulationView viewer,
                         Flag stopFlag
                         ){
        this.bodies = bodies;
        this.dt = dt;
        this.vt = vt;
        this.bounds = bounds;
        this.nSteps = nSteps;
        this.barrier1 = barrier1;
        this.barrier2 = barrier2;
        this.barrier3 = barrier3;
        this.sync = sync;
        this.viewer = viewer;
        this.stopFlag = stopFlag;
        this.startIndex = starIndex;
        this.numBallForThisWorker = numBallForThisWorker;
    }

    /**
     * Body of the Thread Workers.
     *
     */
    public void run(){

        while(iter < this.nSteps && !this.stopFlag.isSet()){
            computeTotalForceAndUpdateVelocity();

            wait(barrier1);

            updatePosition();

            checkBoundaryCollision();

            this.vt = this.vt + this.dt;
            this.iter++;

            wait(barrier2);

            // The worker with the last group of balls calls the display() method.
            if(this.startIndex == (this.bodies.size() - this.numBallForThisWorker)) {
                this.viewer.display(this.bodies, this.vt, this.iter, this.bounds);
            }
            wait(barrier3);
        }

        // Notify the Master.
        this.sync.notifyCompletion();
    }

    /**
     * The barrier is now waiting for the other threads.
     *
     */
    private void wait(CyclicBarrier barrier){
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function of the material provided.
     *
     */
    private V2d computeTotalForceOnBody(Body b) {
        V2d totalForce = new V2d(0, 0);

        for (int j = 0; j < bodies.size(); j++) {
            Body otherBody = bodies.get(j);
            if (!b.equals(otherBody)) {
                try {
                    V2d forceByOtherBody = b.computeRepulsiveForceBy(otherBody);
                    totalForce.sum(forceByOtherBody);
                } catch (Exception ex) {
                    System.out.println("" + ex);
                }
            }
        }
        totalForce.sum(b.getCurrentFrictionForce());

        return totalForce;
    }

    /**
     * Function of the material provided.
     *
     */
    private void computeTotalForceAndUpdateVelocity(){
        for (int i = this.startIndex; i < this.startIndex + this.numBallForThisWorker; i++){
            V2d totalForce = computeTotalForceOnBody(this.bodies.get(i));
            V2d acc = new V2d(totalForce).scalarMul(1.0 / this.bodies.get(i).getMass());
            this.bodies.get(i).updateVelocity(acc, this.dt);
        }
    }

    /**
     * Function of the material provided.
     *
     */
    private void updatePosition(){
        for (int i = this.startIndex; i < this.startIndex + this.numBallForThisWorker; i++) {
            this.bodies.get(i).updatePos(this.dt);
        }
    }

    /**
     * Function of the material provided.
     *
     */
    private void checkBoundaryCollision(){
        for (int i = this.startIndex; i < this.startIndex + this.numBallForThisWorker; i++) {
            this.bodies.get(i).checkAndSolveBoundaryCollision(this.bounds);
        }
    }
}
