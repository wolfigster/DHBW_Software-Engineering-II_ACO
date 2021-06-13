package aco;

import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Worker implements Runnable {

    private final CyclicBarrier cyclicBarrier;
    private final ArrayList<Ant> ants;

    public Worker(CyclicBarrier cyclicBarrier, ArrayList<Ant> ants) {
        this.cyclicBarrier = cyclicBarrier;
        this.ants = ants;
    }

    @Override
    public void run() {
        ants.forEach(ant -> {
            ant.newRound();
            ant.lookForWay();
        });
        try {
            cyclicBarrier.await();
        } catch (BrokenBarrierException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
