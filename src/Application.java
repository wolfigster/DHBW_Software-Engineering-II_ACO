import aco.ProblemInstance;
import base.City;
import configuration.Configuration;
import log.LogEngine;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Application {
    // -data pr299.txt -alpha 5 -beta 5 -decay 0.3 -niterations 256 -nants 256 -nthreads 4 -result result.log -loglevel 0
    public static void main(String[] args) throws Exception {
        Configuration.instance.set(args);
        if (Configuration.instance.loglevel) {
            LogEngine.instance.init(Configuration.instance.result);
        }
        readCities();
    }

    public static void readCities() {
        ArrayList<City> cities = new ArrayList<>();
        int dimensions = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(Configuration.instance.data))) {
            for (String line; (line= br.readLine()) != null;) {
                System.out.println(line);
                if (line.startsWith("DIMENSION")) {
                    dimensions = Integer.parseInt(line.split(":")[1].trim());
                    ProblemInstance.instance.setDistance(new double[dimensions][dimensions]);
                }
                if (Character.isDigit(line.charAt(0))) {
                    String[] coords = line.split(" ");
                    if (cities.size() < dimensions) cities.add(new City(Integer.parseInt(coords[0]), Double.parseDouble(coords[1]), Double.parseDouble(coords[2])));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        cities.forEach(city -> cities.forEach(city::calculateDistance));
        System.out.println(Arrays.deepToString(ProblemInstance.instance.distance));
        File log = new File(Configuration.instance.result);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(log.getAbsolutePath(), true));
            for (double[] distance : ProblemInstance.instance.distance) {
                bw.write(Arrays.toString(distance) + "\n");
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
