package aco;

import configuration.Configuration;
import log.LogEngine;

import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;

public class AntColony {
    private final double[][] pheromones;
    private final Ant[] ants;
    private final Worker[] workers;
    private final CyclicBarrier cyclicBarrier;

    public AntColony() {
        cyclicBarrier = new CyclicBarrier(Configuration.instance.numberOfThreads);

        if (Configuration.instance.isDebug) {
            LogEngine.instance.write("--- aco.AntColony()");
        }

        int count = ProblemInstance.instance.getNumberOfCities();
        pheromones = new double[count][count];

        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count; j++) {
                pheromones[i][j] = Configuration.instance.startPheromoneValue;
            }
        }

        ants = new Ant[Configuration.instance.numberOfAnts];

        for (int i = 0; i < Configuration.instance.numberOfAnts; i++) {
            ants[i] = new Ant(i, ProblemInstance.instance, this);
        }

        if (Configuration.instance.isDebug) {
            LogEngine.instance.write("---");
        }


        // init workers
        // split the numberOfAnts to each worker
        workers = new Worker[Configuration.instance.numberOfThreads];
        int size = Configuration.instance.numberOfAnts / Configuration.instance.numberOfThreads + 1;
        for (int i = 0; i < Configuration.instance.numberOfThreads; i++) {
            ArrayList<Ant> antList = new ArrayList<>();

            // calculate starting- & ending-index
            int start = i * size;
            int end = (i + 1) * size;

            // assign specific ants to the antlist
            for (int index = start; index < end; index++) {
                if (index < Configuration.instance.numberOfAnts) antList.add(ants[index]);
            }

            // create new Worker with specific antlist
            workers[i] = new Worker(cyclicBarrier, antList);
        }
    }

    /*
    public static void main(java.lang.String[] args) {
        System.out.println("logfilePath : " + Configuration.instance.logfilePath);
        LogEngine.instance.init("debug.log");
        LogEngine.instance.write("--- starting");

        AntColony antColony = new AntColony();
        antColony.solve();
        LogEngine.instance.write(antColony.toString());

        LogEngine.instance.close();
    }
     */

    public void addPheromone(int from, int to, double pheromoneValue) {
        pheromones[from - 1][to - 1] += pheromoneValue;
    }

    public double getPheromone(int from, int to) {
        return pheromones[from - 1][to - 1];
    }

    public void doDecay() {
        if (Configuration.instance.isDebug) {
            LogEngine.instance.write("--- aco.AntColony.doDecay()");
        }

        int count = ProblemInstance.instance.getNumberOfCities();
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count; j++) {
                pheromones[i][j] *= (1.0 - Configuration.instance.decayFactor);
            }
        }

        if (Configuration.instance.isDebug) {
            LogEngine.instance.write("---");
        }
    }

    // made getBestAnt public to access it from the Application class to log the result
    public Ant getBestAnt() {
        int indexOfAntWithBestObjectiveValue = 0;
        double objectiveValue = Double.MAX_VALUE;

        for (int i = 0; i < Configuration.instance.numberOfAnts; i++) {
            double currentObjectiveValue = ants[i].getObjectiveValue();
            if (currentObjectiveValue < objectiveValue) {
                objectiveValue = currentObjectiveValue;
                indexOfAntWithBestObjectiveValue = i;
            }
        }

        return ants[indexOfAntWithBestObjectiveValue];
    }

    public void solve() throws InterruptedException {
        int iteration = 0;

        while (iteration < Configuration.instance.numberOfIterations) {
            // start time for iteration
            long startTime = System.nanoTime();
            iteration++;

            if (Configuration.instance.isDebug) {
                printPheromoneMatrix();
            }

            // create multiple threads for each worker
            Thread thread = new Thread();
            for(int i = 0; i < Configuration.instance.numberOfThreads; i++) {
                thread = new Thread(workers[i]);
                thread.start();
            }

            // wait for thread to die
            thread.join();

            /*
            for (int i = 0; i < Configuration.instance.numberOfAnts; i++) {
                ants[i].newRound();
                ants[i].lookForWay();
            }

            System.out.println("iteration [" + iteration + "] | " + getBestAnt());
             */

            doDecay();
            // declare best ant to access it for the logging at the end of this method
            Ant best = getBestAnt();
            best.layPheromone();

            if (Configuration.instance.isDebug) {
                printPheromoneMatrix();
            }

            // get end time - everything below is logging and no actual work
            long runtime = System.nanoTime() - startTime;
            runtime = runtime / 1000000;

            // [timestamp dd.MM.yyyy HH:mm:ss] | [i] | [runtime of iteration in ms] | [best agent id] | [best distance] | [best tour]
            String iterationMessage = String.format("iteration: %4s", iteration);
            String runtimeMessage = String.format("runtime of iteration: %5sms", runtime);
            String bestAgentMessage = String.format("best agent id: %4s", best.getId());
            String bestDistanceMessage = "best distance: " + best.getDistance();
            String bestTourMessage = best.getTour();

            String message = iterationMessage + " | " + runtimeMessage + " | " + bestAgentMessage + " | " + bestDistanceMessage + " | " + bestTourMessage;
            LogEngine.instance.write(message);
        }
    }

    public void printPheromoneMatrix() {
        LogEngine.instance.write("--- aco.AntColony.printPheromoneMatrix()");

        int n = pheromones.length;
        for (double[] pheromone : pheromones) {
            for (int j = 0; j < n; j++) {
                System.out.print(Configuration.instance.decimalFormat.format(pheromone[j]) + " ");
            }
            System.out.println();
        }

        System.out.println("---");
    }

    public String toString() {
        return getBestAnt().toString();
    }
}