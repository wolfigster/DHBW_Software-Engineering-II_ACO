package aco;

import configuration.Configuration;
import log.LogEngine;

public class AntColony {
    private final double[][] pheromones;
    private final Ant[] ants;

    public AntColony() {
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
    }

    public static void main(java.lang.String[] args) {
        System.out.println("logfilePath : " + Configuration.instance.logfilePath);
        LogEngine.instance.init("debug.log");
        LogEngine.instance.write("--- starting");

        AntColony antColony = new AntColony();
        antColony.solve();
        LogEngine.instance.write(antColony.toString());

        LogEngine.instance.close();
    }

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

    private Ant getBestAnt() {
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

    public void solve() {
        int iteration = 0;

        while (iteration < Configuration.instance.numberOfIterations) {
            LogEngine.instance.write("*** iteration - " + iteration);

            if (Configuration.instance.isDebug) {
                printPheromoneMatrix();
            }

            iteration++;

            for (int i = 0; i < Configuration.instance.numberOfAnts; i++) {
                ants[i].newRound();
                ants[i].lookForWay();
            }

            System.out.println("iteration [" + iteration + "] | " + getBestAnt());

            doDecay();
            getBestAnt().layPheromone();

            if (Configuration.instance.isDebug) {
                printPheromoneMatrix();
            }

            LogEngine.instance.write("***");
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