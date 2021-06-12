package configuration;

import random.MersenneTwister;

import java.text.DecimalFormat;
import java.util.HashMap;

public enum Configuration {
    instance;

    public final String userDirectory = System.getProperty("user.dir");
    public final String fileSeparator = System.getProperty("file.separator");
    public final String dataDirectory = userDirectory + fileSeparator + "data" + fileSeparator;
    public final String logDirectory = userDirectory + fileSeparator + "log" + fileSeparator;
    public final String logfilePath = logDirectory + "debug.log";

    public final MersenneTwister randomGenerator = new MersenneTwister(System.currentTimeMillis());
    public final DecimalFormat decimalFormat = new DecimalFormat("#0.000000000000000");

    public final double startPheromoneValue = 0.00005;

    public final boolean isDebug = false;

    public boolean loglevel = false;
    public double alpha = 0;
    public double beta = 0;
    public double decayFactor = 0.3;
    public int numberOfAnts = 25;
    public int numberOfIterations = 50;
    public int numberOfThreads = 4;
    public String data = "pr299.tsp";
    public String result = "swam_intelligence.log";

    // method to set all necessary variables
    public void set(String... args) throws Exception {
        HashMap<String, String> values = new HashMap<>();
        if (args.length % 2 != 0) {
            throw new Exception("Bitte Eingabewerte überprüfen");
        }
        for (int i = 0; i < args.length; i++) {
            if (args[i].charAt(0) == '-') {
                values.put(args[i].substring(1), args[i+1]);
                i++;
            }
        }
        // try to get all the inputs from the hashmap and parse them to the correct datatype
        try {
            this.data = dataDirectory + values.get("data");
            this.alpha = Double.parseDouble(values.get("alpha"));
            this.beta = Double.parseDouble(values.get("beta"));
            this.decayFactor = Double.parseDouble(values.get("decay"));
            this.numberOfIterations = Integer.parseInt(values.get("niterations"));
            this.numberOfAnts = Integer.parseInt(values.get("nants"));
            this.numberOfThreads = Integer.parseInt(values.get("nthreads"));
            this.result = logDirectory + values.get("result");
            this.loglevel = Boolean.parseBoolean(values.get("loglevel"));
        } catch (NumberFormatException nfe) {
            throw new Exception("Ein oder mehrere Parameter konnten nicht übergeben werden. Überprüfen Sie ihre Eingaben!");
        }
        // check plausibility of the input
        checkPlausibility();
    }

    private void checkPlausibility() throws Exception {
        if (alpha > 10 || alpha < 1 || beta > 10 || beta < 1) {
            throw new Exception("-alpha and -beta have to be between 0 and 10");
        }
        if (!(0 <= decayFactor && decayFactor <= 1)) {
            throw new Exception("-decay has to be between 0 and 1");
        }
        if (numberOfIterations < 1) {
            throw new Exception("-niterations has to be greater than 0");
        }
        if (numberOfAnts < 1) {
            throw new Exception("-nants has to be greater than 0");
        }
        if (numberOfThreads < 1) {
            throw new Exception("-nthreads has to be greater than 0");
        }
    }
}