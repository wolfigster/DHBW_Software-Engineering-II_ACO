import aco.Ant;
import aco.AntColony;
import aco.ProblemInstance;
import base.City;
import configuration.Configuration;
import log.LogEngine;

import java.io.*;
import java.util.ArrayList;

public class Application {
    // bounds for pr299 = 48191
    // -data pr299.tsp -alpha 5 -beta 5 -decay 0.3 -niterations 128 -nants 64 -nthreads 4 -result result.log -loglevel 1
    public static void main(String[] args) throws Exception {
        // get arguments and save them to the configuration
        Configuration.instance.set(args);
        // start logging if loglevel is true
        if (Configuration.instance.loglevel) {
            LogEngine.instance.init(Configuration.instance.logFile);
        }
        // read the pr299.tsp file or the file with where given with the arg -data
        readCities();

        // start time for total runtime
        long startTime = System.nanoTime();

        // initialize AntColony and start to solve the tsp
        AntColony antColony = new AntColony();
        antColony.solve();

        // get end time and calculate total runtime
        long runtime = System.nanoTime() - startTime;
        runtime = runtime / 1000000;

        if (Configuration.instance.loglevel) {
            LogEngine.instance.close();
        }

        // write result to file
        LogEngine.instance.init(Configuration.instance.result);
        // [timestamp dd.MM.yyyy HH:mm:ss] | [niterations] | [nagents] | [total runtime in ms] | [best distance] | [best tour]
        Ant best = antColony.getBestAnt();
        String message = "iterations: " + Configuration.instance.numberOfIterations + " | threads: " + Configuration.instance.numberOfThreads + " | agents: " + Configuration.instance.numberOfAnts + " | total runtime: " + runtime + "ms | best distance: " + best.getDistance() + " | best tour: " + best.getTour();
        LogEngine.instance.writeResult(message);
        LogEngine.instance.close();
    }

    public static void readCities() {
        ArrayList<City> cities = new ArrayList<>();
        int dimensions = 0;

        // read line by line
        try (BufferedReader br = new BufferedReader(new FileReader(Configuration.instance.data))) {
            for (String line; (line= br.readLine()) != null;) {
                // get line which starts with DIMENSION and set the dimensions of the matrix
                if (line.startsWith("DIMENSION")) {
                    dimensions = Integer.parseInt(line.split(":")[1].trim());
                    ProblemInstance.instance.setDistance(new double[dimensions][dimensions]);
                }
                // add the cities to the cities list
                if (Character.isDigit(line.charAt(0))) {
                    String[] coords = line.split(" ");
                    if (cities.size() < dimensions) cities.add(new City(Integer.parseInt(coords[0]), Double.parseDouble(coords[1]), Double.parseDouble(coords[2])));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // calculate for each city the distance to the all the other cities
        cities.forEach(city -> cities.forEach(city::calculateDistance));
    }
}
